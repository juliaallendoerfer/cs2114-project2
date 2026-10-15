import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class ConsoleUI {
    /**
     * Runs the game, loading questions from a deck CSV file.
     *
     * @param args optional path to a deck CSV file (default "deck.csv")
     */
    public static void main(String[] args)
    {
        String deckFile = args.length > 0 ? args[0] : "deck.csv";
        GameSession game = new GameSession();
        if (!loadDeck(game, deckFile))
        {
            return;
        }
        if (game.isGameOver())
        {
            System.out.println("There are no questions in " + deckFile + ".");
            return;
        }

        Scanner scanner = new Scanner(System.in);
        System.out.println("You have entered CardDash!");
        while (!game.isGameOver())
        {
            System.out.println("Choose a deck (1-3):");

            String input = scanner.nextLine();

            try
            {
                int deck = InputValidator.parseDeckChoice(input);

                if (game.getDeckSize(deck) == 0)
                {
                    System.out.println("That deck is empty!");
                    continue;
                }

                QuestionCard question =
                    game.getCurrentQuestion(deck);

                displayQuestion(question);

                System.out.println("Enter your answer:");

                String answer =
                    InputValidator.cleanAnswer(scanner.nextLine());

                if (!InputValidator.isValidAnswer(answer))
                {
                    System.out.println("Please enter an answer.");
                    continue;
                }

                boolean correct = game.submitAnswer(deck, answer);

                if (correct)
                {
                    System.out.println("Correct!");
                }
                else
                {
                    System.out.println("Incorrect. Try again later!");
                }

                System.out.println("Score: " + game.getScore());
            }
            catch (IllegalArgumentException exception)
            {
                System.out.println(exception.getMessage());
            }
        }

        System.out.println("You completed the game!");
        scanner.close();
    }

    /**
     * Loads the question cards from a deck CSV file into the game.
     * Multiple-choice cards go in deck 1, true/false cards in deck 2, and
     * free-response cards in deck 3. Power-up cards are skipped, since this
     * game does not use them. Malformed lines are skipped with a warning.
     *
     * @param game     the current game session
     * @param fileName path to the deck CSV file
     * @return true if the file was found, false if not
     */
    private static boolean loadDeck(GameSession game, String fileName)
    {
        int lineNumber = 0;
        try (Scanner file = new Scanner(new File(fileName)))
        {
            while (file.hasNextLine())
            {
                String line = file.nextLine().trim();
                lineNumber++;
                if (line.isEmpty() || line.startsWith("#")
                    || line.toLowerCase().startsWith("type,"))
                {
                    continue;
                }
                try
                {
                    Card card = StudySessionManager.parseLine(line);
                    if (card instanceof QuestionCard)
                    {
                        game.addQuestion(deckNumberFor(card.getCardType()),
                            (QuestionCard) card);
                    }
                }
                catch (IllegalArgumentException exception)
                {
                    System.out.println("Skipping line " + lineNumber + " of "
                        + fileName + ": " + exception.getMessage());
                }
            }
            return true;
        }
        catch (FileNotFoundException exception)
        {
            System.out.println("Could not find deck file: " + fileName);
            return false;
        }
    }

    /**
     * Returns the deck number (1-3) that holds questions of the given type.
     *
     * @param type the question's card type
     * @return 1 for MCQ, 2 for true/false, 3 for free response
     */
    private static int deckNumberFor(CardType type)
    {
        switch (type)
        {
            case MCQ:
                return 1;
            case TRUE_FALSE:
                return 2;
            default:
                return 3;
        }
    }

    /**
     * Displays a question and its answer options, if applicable.
     *
     * @param question the question to display
     */
    private static void displayQuestion(QuestionCard question)
    {
        System.out.println(question.getPrompt());

        if (question instanceof MultipleChoiceCard)
        {
            MultipleChoiceCard mc =
                (MultipleChoiceCard)question;

            for (int i = 0; i < mc.getOptions().size(); i++)
            {
                System.out.println(
                    (i + 1) + ". " + mc.getOptions().get(i));
            }
        }
    }
}
