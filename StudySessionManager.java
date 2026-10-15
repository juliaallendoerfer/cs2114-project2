import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Scanner;

/**
 * Coordinates the lifecycle of a FlashClass study session. Loads the deck
 * from a CSV file into a LinkedChain, runs the turn loop, evaluates answers,
 * re-queues missed questions to the back of the chain, applies power-ups,
 * and ends the session when the deck is cleared or the user types "exit".
 *
 * Placeholders marked "TODO (Zachias)" are where ConsoleUI and
 * InputValidator plug in once they exist. Code marked "TEMP" is a stand-in
 * that keeps the game playable until then, and should be deleted when the
 * matching TODO is done.
 *
 * Deck CSV format (one card per line, header and # comment lines ignored):
 *   type,id,prompt,data,correct,points
 *   MCQ,MCQ-1,What is 2 + 2?,1|2|3|4,4,100
 *   TF,TF-1,The sky is blue.,,true,
 *   FRQ,FRQ-1,Name the plant food process.,photosynthesis|photo synthesis,,
 *   PWR,PWR-1,,DOUBLE_POINTS,,
 * Lists inside a field are separated by '|'. Wrap a field in double quotes
 * if it contains a comma. The points column may be left blank to use the
 * default point value.
 */
public class StudySessionManager {

    /** Bonus awarded for each power-up still unused when the deck is cleared. */
    public static final int UNUSED_POWER_UP_BONUS = 50;

    /** Extra points per answer already in the current streak. */
    public static final int STREAK_BONUS = 10;

    /** Command prefix the user types to activate a power-up, e.g. "use skip". */
    public static final String USE_COMMAND = "use ";

    private final LinkedChain<Card> deck;  // active cards for the session
    private final Player player;           // the current player
    // TODO (Zachias): add the UI delegate once ConsoleUI exists
    // private final ConsoleUI ui;         // input prompts and output displays
    private boolean isRunning;             // governs the active game loop
    private boolean sessionOver;           // true once endSession() has run
    private boolean doublePointsActive;    // next correct answer is doubled
    private boolean secondChanceActive;    // next wrong answer may be retried
    private final Random random;           // picks the Fifty-Fifty decoy
    private Scanner input;                 // TEMP: replaced by ConsoleUI

    /**
     * Constructs a session with an empty deck and a new player.
     */
    public StudySessionManager() {
        this(new LinkedChain<Card>(), new Player());
    }

    /**
     * Constructs a session and loads its deck from a CSV file.
     *
     * @param deckFile path to the deck CSV file
     */
    public StudySessionManager(String deckFile) {
        this();
        loadDeck(deckFile);
    }

    /**
     * Constructs a session around an existing deck and player.
     *
     * @param deck   the chain of cards to study
     * @param player the player taking the session
     * @throws IllegalArgumentException if deck or player is null
     */
    public StudySessionManager(LinkedChain<Card> deck, Player player) {
        if (deck == null) {
            throw new IllegalArgumentException("Deck cannot be null.");
        }
        if (player == null) {
            throw new IllegalArgumentException("Player cannot be null.");
        }
        this.deck = deck;
        this.player = player;
        // TODO (Zachias): this.ui = new ConsoleUI();
        this.isRunning = false;
        this.sessionOver = false;
        this.doublePointsActive = false;
        this.secondChanceActive = false;
        this.random = new Random();
    }

    // ~ Deck loading ..........................................................

    /**
     * Reads cards from a CSV file and adds them to the back of the deck in
     * file order. Malformed lines are skipped with a warning, and a missing
     * file is reported instead of crashing. O(N) for N lines.
     *
     * @param fileName path to the deck CSV file
     * @return the number of cards that were loaded
     */
    public int loadDeck(String fileName) {
        int loaded = 0;
        int lineNumber = 0;
        try (Scanner file = new Scanner(new File(fileName))) {
            while (file.hasNextLine()) {
                String line = file.nextLine().trim();
                lineNumber++;
                if (line.isEmpty() || line.startsWith("#")
                    || line.toLowerCase().startsWith("type,")) {
                    continue;
                }
                try {
                    deck.addToBack(parseLine(line));
                    loaded++;
                } catch (IllegalArgumentException e) {
                    showMessage("Skipping line " + lineNumber + " of "
                        + fileName + ": " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            showMessage("Could not find deck file: " + fileName);
        }
        return loaded;
    }

    /**
     * Builds one card from a line of the deck CSV file.
     *
     * @param line a CSV line in the format: type,id,prompt,data,correct,points, 
     *                                  difficulty
     * @return the card the line describes
     * @throws IllegalArgumentException if the line is malformed or the card
     *                                  values are invalid
     */
    public static Card parseLine(String line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null.");
        }
        ArrayList<String> fields = splitCsvLine(line);
        if (fields.size() < 6 || fields.size() > 7) {
            throw new IllegalArgumentException(
                "Expected 6 or 7 fields but found " + fields.size() + ".");
        }
        String type = fields.get(0).toUpperCase();
        String id = fields.get(1);
        String prompt = fields.get(2);
        String data = fields.get(3);
        String correct = fields.get(4);
        String difficulty = fields.get(5);
        int points = parsePoints(fields.size() == 7 ? fields.get(6) : "");
        
        switch (type) {
            case "MCQ":
                return new MultipleChoiceCard(id, prompt, splitList(data),
                    parseNumber(correct, "Correct option"), difficulty, points);
            case "TF":
            case "TRUE_FALSE":
                return new TrueFalseCard(id, prompt, parseBoolean(correct), difficulty,
                    points);
            case "FRQ":
            case "FREE_RESPONSE":
                return new FRQCard(id, prompt, splitList(data), difficulty, points);
            case "PWR":
            case "POWER_UP":
                PowerUpCardType effect = parsePowerUpName(data);
                if (effect == null) {
                    throw new IllegalArgumentException(
                        "Unknown power-up \"" + data + "\".");
                }
                return new PowerUpCard(id, effect);
            default:
                throw new IllegalArgumentException(
                    "Unknown card type \"" + fields.get(0) + "\".");
        }
        if(difficulty.toLowerCase() != "easy" || difficulty.toLowerCase() != "meduim" || difficulty.toLowerCase() != "hard") {
            throw new IllegalArgumentException("Unknown difficulty \"" + difficulty + "\".");
        }
    }

    // ~ Game loop .............................................................

    /**
     * Starts the session and runs turns until the deck is cleared or the
     * user exits. O(N) turns for N cards, plus one turn per missed answer.
     */
    public void startSession() {
        if (deck.isEmpty()) {
            showMessage("There are no cards to study.");
            return;
        }
        isRunning = true;
        sessionOver = false;
        // TODO (Zachias): ui.displayMessage(<welcome banner and how to
        //       use power-ups / exit>);
        showMessage("Welcome to FlashClass! Type \"" + USE_COMMAND.trim()
            + " <power-up>\" to use a power-up or \"exit\" to quit.");

        while (isRunning) {
            if (deck.isEmpty()) {
                endSession(true);
            } else {
                runTurn();
            }
        }
    }

    /**
     * Draws the card at the front of the chain. Power-up cards go into the
     * player's inventory; question cards are shown and answered, re-prompting
     * until the turn is over. O(1) per prompt.
     */
    public void runTurn() {
        Card card;
        try {
            card = deck.getHead();
        } catch (NoSuchElementException e) {
            endSession(true);
            return;
        }

        if (card instanceof PowerUpCard) {
            collectPowerUp((PowerUpCard) card);
            return;
        }

        QuestionCard question = (QuestionCard) card;
        // TODO (Zachias): ui.displayHUD(player, deck.getNumberOfEntries());
        // TODO (Zachias): ui.displayCard(question);
        showQuestion(question);  // TEMP

        boolean turnOver = false;
        while (!turnOver && isRunning) {
            // TODO (Zachias): String answer = ui.promptUserInput("> ");
            String answer = readLine("> ");  // TEMP
            if (answer == null) {
                // input stream closed; treat it like "exit"
                endSession(false);
                return;
            }
            turnOver = handleInput(question, answer);
        }
    }

    /**
     * Handles one line of user input for the question at the front of the
     * deck: an exit request, a power-up command, or an answer.
     *
     * @param question the question being answered
     * @param rawInput the line the user typed
     * @return true if the turn is over, false if the user should be
     *         prompted again for the same question
     */
    public boolean handleInput(QuestionCard question, String rawInput) {
        // TODO (Zachias): replace with InputValidator.isExit(rawInput)
        if (rawInput.trim().equalsIgnoreCase("exit")) {  // TEMP
            endSession(false);
            return true;
        }

        String cleaned = rawInput.trim();
        if (cleaned.toLowerCase().startsWith(USE_COMMAND)) {
            PowerUpCardType type =
                parsePowerUpName(cleaned.substring(USE_COMMAND.length()));
            if (type == null) {
                showMessage("Unknown power-up. Choices: " + powerUpChoices());
                return false;
            }
            return activatePowerUp(type, question);
        }

        // TODO (Zachias): validate the answer with InputValidator before
        //       checking it, re-prompting with no penalty when it returns null:
        //   MCQ -> InputValidator.parseMCQ(cleaned)
        //          null -> "Invalid input. Please enter a number (1-4)."
        //   TF  -> InputValidator.parseTF(cleaned)
        //          null -> "Please answer True or False (T/F)."
        //   FRQ -> InputValidator.parseFRQ(cleaned)
        //          null -> "Your answer cannot be blank."
        //   then pass String.valueOf(parsedValue) to submitAnswer().
        // TEMP: only blank input is rejected. Any other bad input (like "abc"
        // on an MCQ) is graded as a wrong answer by checkAnswer().
        if (cleaned.isEmpty()) {
            showMessage("Your answer cannot be blank.");
            return false;
        }
        return submitAnswer(question, cleaned);
    }

    /**
     * Grades a validated answer. A correct answer scores points and removes
     * the card; a wrong answer is retried if Second Chance is active,
     * otherwise the card is re-queued. O(1) when the card is at the front.
     *
     * @param question the question being answered
     * @param answer   the validated answer text
     * @return true if the turn is over, false if the user may retry
     */
    public boolean submitAnswer(QuestionCard question, String answer) {
        question.incrementAttempts();
        if (question.checkAnswer(answer)) {
            awardCorrectAnswer(question);
            return true;
        }
        if (secondChanceActive) {
            secondChanceActive = false;
            showMessage("Not quite, but your Second Chance saved you. "
                + "Try again!");
            return false;
        }
        handleIncorrectAnswer(question);
        return true;
    }

    /**
     * Resets the streak, alerts the player, and moves the missed question to
     * the back of the chain. With one card left the card stays put and must
     * be retried. O(1) when the card is at the front.
     *
     * @param card the question that was answered incorrectly
     */
    public void handleIncorrectAnswer(QuestionCard card) {
        player.resetStreak();
        showMessage("Incorrect! This question will come back later.");
        if (deck.getNumberOfEntries() == 1) {
            showMessage("This is the last card. Answer it correctly to "
                + "finish the deck.");
        }
        moveToBack(card);
    }

    /**
     * Ends the game. When the deck was cleared, every unused power-up is
     * converted into bonus points. Then the session summary is shown.
     * Only the first call has any effect. O(1)
     *
     * @param clearedDeck true if every card was answered, false if the
     *                    user exited early
     */
    public void endSession(boolean clearedDeck) {
        if (sessionOver) {
            return;
        }
        sessionOver = true;
        isRunning = false;

        if (clearedDeck) {
            int unused = player.getInventory().size();
            if (unused > 0) {
                int bonus = unused * UNUSED_POWER_UP_BONUS;
                player.addScore(bonus);
                showMessage("Unused power-ups converted to " + bonus
                    + " bonus points.");
            }
        }

        // TODO (Zachias): ui.displaySummary(player, clearedDeck);
        showMessage(clearedDeck ? "You cleared the deck!" : "Session ended.");
        showMessage("Final score: " + player.getScore()
            + " | Best streak: " + player.getBestStreak());  // TEMP
    }

    // ~ Power-ups .............................................................

    /**
     * Moves a power-up card from the front of the deck into the player's
     * inventory. O(1)
     *
     * @param card the power-up card at the front of the deck
     */
    public void collectPowerUp(PowerUpCard card) {
        removeFromDeck(card);
        player.addPowerUp(card);
        // TODO (Zachias): ui.displayCard(card);
        showMessage(card.getPrompt() + " " + card.getDescription()
            + " Type \"" + USE_COMMAND + card.getEffect().name()
            .toLowerCase() + "\" to use it.");
    }

    /**
     * Uses one power-up from the inventory on the current question. A
     * power-up the player does not own, or one that does not fit the
     * question type, is refused without using up a turn.
     *
     * @param type     the power-up to activate
     * @param question the question currently being answered
     * @return true if the turn is over (Skip), false if the user should be
     *         prompted again for the same question
     */
    public boolean activatePowerUp(PowerUpCardType type,
                                   QuestionCard question) {
        if (!player.hasPowerUp(type)) {
            showMessage("You do not possess this power-up!");
            return false;
        }
        switch (type) {
            case DOUBLE_POINTS:
                if (doublePointsActive) {
                    showMessage("Double Points is already active.");
                    return false;
                }
                player.usePowerUp(type);
                doublePointsActive = true;
                showMessage("Double Points active! Your next correct answer "
                    + "is worth twice the points.");
                return false;
            case SECOND_CHANCE:
                if (secondChanceActive) {
                    showMessage("Second Chance is already active.");
                    return false;
                }
                player.usePowerUp(type);
                secondChanceActive = true;
                showMessage("Second Chance active! Your next wrong answer "
                    + "can be retried.");
                return false;
            case SKIP:
                player.usePowerUp(type);
                showMessage("Skipped! This question moves to the back of "
                    + "the deck.");
                moveToBack(question);
                return true;
            case REVEAL_LETTER:
                if (!(question instanceof FRQCard)) {
                    showMessage("Reveal Letter only works on free-response "
                        + "questions.");
                    return false;
                }
                player.usePowerUp(type);
                String answer = ((FRQCard) question).getAcceptableAnswers()
                    .get(0).trim();
                showMessage("Hint: the answer starts with \""
                    + answer.charAt(0) + "\".");
                return false;
            case FIFTY_FIFTY:
                if (!(question instanceof MultipleChoiceCard)) {
                    showMessage("Fifty-Fifty only works on multiple-choice "
                        + "questions.");
                    return false;
                }
                player.usePowerUp(type);
                showFiftyFifty((MultipleChoiceCard) question);
                return false;
            default:
                return false;
        }
    }

    // ~ Getters ...............................................................

    /** Returns the deck of remaining cards. O(1) */
    public LinkedChain<Card> getDeck() {
        return deck;
    }

    /** Returns the current player. O(1) */
    public Player getPlayer() {
        return player;
    }

    /** Returns true while the game loop is active. O(1) */
    public boolean isRunning() {
        return isRunning;
    }

    /** Returns true if Double Points is waiting for a correct answer. O(1) */
    public boolean isDoublePointsActive() {
        return doublePointsActive;
    }

    /** Returns true if Second Chance is waiting for a wrong answer. O(1) */
    public boolean isSecondChanceActive() {
        return secondChanceActive;
    }

    // ~ Private helpers .......................................................

    /**
     * Scores a correct answer (with Double Points and streak bonus) and
     * removes the card from the deck.
     */
    private void awardCorrectAnswer(QuestionCard question) {
        int points = question.getPointValue();
        if (doublePointsActive) {
            points *= 2;
            doublePointsActive = false;
        }
        player.incrementStreak();
        int bonus = STREAK_BONUS * (player.getStreak() - 1);
        player.addScore(points + bonus);
        removeFromDeck(question);

        String message = "Correct! +" + points + " points";
        if (bonus > 0) {
            message += " (+" + bonus + " streak bonus)";
        }
        showMessage(message + ".");
    }

    /**
     * Shows the correct option and one random wrong option of an MCQ.
     */
    private void showFiftyFifty(MultipleChoiceCard question) {
        int correct = question.getCorrectOptionIndex();
        int decoy = random.nextInt(MultipleChoiceCard.NUMBER_OF_OPTIONS - 1)
            + 1;
        if (decoy >= correct) {
            decoy++;
        }
        int first = Math.min(correct, decoy);
        int second = Math.max(correct, decoy);
        ArrayList<String> options = question.getOptions();
        showMessage("Fifty-Fifty! The answer is one of:");
        showMessage("  " + first + ") " + options.get(first - 1));
        showMessage("  " + second + ") " + options.get(second - 1));
    }

    /**
     * Moves a card to the back of the deck. Uses cycleFrontToBack() when the
     * card is at the front, which is O(1) and keeps a single card in place.
     */
    private void moveToBack(Card card) {
        if (deck.getHead() == card) {
            deck.cycleFrontToBack();
        } else if (deck.remove(card)) {
            deck.addToBack(card);
        }
    }

    /**
     * Removes a card from the deck, in O(1) when it is at the front.
     */
    private void removeFromDeck(Card card) {
        if (deck.getHead() == card) {
            deck.removeFirst();
        } else {
            deck.remove(card);
        }
    }

    /**
     * Sends a message to the player.
     */
    private void showMessage(String message) {
        // TODO (Zachias): ui.displayMessage(message);
        System.out.println(message);  // TEMP
    }

    /**
     * TEMP: prints a question until ConsoleUI.displayCard() exists.
     */
    private void showQuestion(QuestionCard question) {
        System.out.println();
        System.out.println(question);
        if (question instanceof MultipleChoiceCard) {
            ArrayList<String> options =
                ((MultipleChoiceCard) question).getOptions();
            for (int i = 0; i < options.size(); i++) {
                System.out.println("  " + (i + 1) + ") " + options.get(i));
            }
        }
    }

    /**
     * TEMP: reads one line of input until ConsoleUI.promptUserInput()
     * exists. Returns null if there is no more input.
     */
    private String readLine(String prompt) {
        if (input == null) {
            input = new Scanner(System.in);
        }
        System.out.print(prompt);
        return input.hasNextLine() ? input.nextLine() : null;
    }

    /**
     * Returns the power-up names a user can type, e.g. "double_points, skip".
     */
    private static String powerUpChoices() {
        StringBuilder choices = new StringBuilder();
        for (PowerUpCardType type : PowerUpCardType.values()) {
            if (choices.length() > 0) {
                choices.append(", ");
            }
            choices.append(type.name().toLowerCase());
        }
        return choices.toString();
    }

    /**
     * Converts a typed name like "double points" or "Fifty-Fifty" to a
     * power-up type, or returns null if it does not match one.
     */
    private static PowerUpCardType parsePowerUpName(String name) {
        String normalized = name.trim().toUpperCase().replaceAll("[\\s-]+", "_");
        try {
            return PowerUpCardType.valueOf(normalized);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    /**
     * Splits one CSV line into trimmed fields. Commas inside double quotes
     * do not split, and "" inside quotes is a literal quote.
     */
    private static ArrayList<String> splitCsvLine(String line) {
        ArrayList<String> fields = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (inQuotes && i + 1 < line.length()
                    && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                fields.add(current.toString().trim());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }
        if (inQuotes) {
            throw new IllegalArgumentException("Unclosed quote.");
        }
        fields.add(current.toString().trim());
        return fields;
    }

    /**
     * Splits a '|' separated field into a list of trimmed strings.
     */
    private static ArrayList<String> splitList(String field) {
        ArrayList<String> items = new ArrayList<String>();
        for (String item : field.split("\\|", -1)) {
            items.add(item.trim());
        }
        return items;
    }

    /**
     * Parses a required whole number, naming the field in the error.
     */
    private static int parseNumber(String text, String fieldName) {
        try {
            return Integer.parseInt(text.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                fieldName + " must be a whole number but was \"" + text
                + "\".");
        }
    }

    /**
     * Parses the optional points column; blank means the default value.
     */
    private static int parsePoints(String text) {
        if (text.isEmpty()) {
            return QuestionCard.DEFAULT_POINT_VALUE;
        }
        return parseNumber(text, "Points");
    }

    /**
     * Parses "true" or "false" (any case) for a true/false card.
     */
    private static boolean parseBoolean(String text) {
        if (text.equalsIgnoreCase("true")) {
            return true;
        }
        if (text.equalsIgnoreCase("false")) {
            return false;
        }
        throw new IllegalArgumentException(
            "True/false answer must be \"true\" or \"false\" but was \""
            + text + "\".");
    }

    /**
     * Runs a study session from the command line.
     *
     * @param args optional path to a deck CSV file (default "deck.csv")
     */
    public static void main(String[] args) {
        String deckFile = args.length > 0 ? args[0] : "deck.csv";
        new StudySessionManager(deckFile).startSession();
    }
}
