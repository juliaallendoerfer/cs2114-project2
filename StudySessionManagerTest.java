import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import student.TestCase;

/**
 * Tests the StudySessionManager class: deck loading, turn handling,
 * re-queueing, power-ups, and session completion.
 */
public class StudySessionManagerTest extends TestCase
{
    // ~ Fields ................................................................
    private MultipleChoiceCard mcq;
    private TrueFalseCard tf;
    private FRQCard frq;
    private Player player;

    /**
     * Sets up each test method.
     */
    public void setUp()
    {
        mcq = new MultipleChoiceCard("MCQ-1", "What is 2 + 2?",
            new ArrayList<String>(Arrays.asList("1", "2", "3", "4")), 4);
        tf = new TrueFalseCard("TF-1", "The sky is blue.", true);
        frq = new FRQCard("FRQ-1", "Plant food process?",
            new ArrayList<String>(Arrays.asList("Photosynthesis")));
        player = new Player();
    }


    /**
     * Builds a session whose deck holds the given cards, first card first.
     */
    private StudySessionManager sessionOf(Card... cards)
    {
        LinkedChain<Card> deck = new LinkedChain<Card>();
        for (int i = cards.length - 1; i >= 0; i--)
        {
            deck.add(cards[i]);
        }
        return new StudySessionManager(deck, player);
    }


    /**
     * Asserts that the given text was printed during the test.
     */
    private void assertPrinted(String expected)
    {
        String output = systemOut().getHistory();
        assertTrue("Expected output to contain \"" + expected + "\" but was:\n"
            + output, output.contains(expected));
    }


    /**
     * Asserts that the deck holds exactly the given cards, in order. The
     * array has one spare slot so a broken (cyclic) chain fails here instead
     * of hanging.
     */
    private void assertOrder(StudySessionManager session, Card... expected)
    {
        Card[] actual;
        try
        {
            actual = session.getDeck().toArray(new Card[expected.length + 1]);
        }
        catch (ArrayIndexOutOfBoundsException e)
        {
            fail("Deck has more nodes than entries (cycle in the chain?)");
            return;
        }
        for (int i = 0; i < expected.length; i++)
        {
            assertEquals(expected[i], actual[i]);
        }
        assertNull(actual[expected.length]);
    }


    // ~ parseLine .............................................................

    /**
     * Tests parsing a multiple-choice line.
     */
    public void testParseMCQ()
    {
        Card card = StudySessionManager.parseLine(
            "MCQ,MCQ-1,What is 2 + 2?,1|2|3|4,4,200");

        assertEquals(true, card instanceof MultipleChoiceCard);
        MultipleChoiceCard parsed = (MultipleChoiceCard) card;
        assertEquals("MCQ-1", parsed.getId());
        assertEquals("What is 2 + 2?", parsed.getPrompt());
        assertEquals(4, parsed.getCorrectOptionIndex());
        assertEquals("3", parsed.getOptions().get(2));
        assertEquals(200, parsed.getPointValue());
    }


    /**
     * Tests parsing a true/false line with the default point value.
     */
    public void testParseTF()
    {
        Card card = StudySessionManager.parseLine(
            "TF,TF-1,The sky is blue.,,TRUE,");

        assertEquals(true, card instanceof TrueFalseCard);
        assertEquals(true, ((TrueFalseCard) card).getCorrectAnswer());
        assertEquals(QuestionCard.DEFAULT_POINT_VALUE,
            ((TrueFalseCard) card).getPointValue());
    }


    /**
     * Tests parsing a free-response line with several answers and no
     * points column.
     */
    public void testParseFRQ()
    {
        Card card = StudySessionManager.parseLine(
            "FRQ,FRQ-1,Plant food process?,photosynthesis | photo synthesis,");

        assertEquals(true, card instanceof FRQCard);
        assertEquals(2, ((FRQCard) card).getAcceptableAnswers().size());
        assertEquals(true, ((FRQCard) card).checkAnswer("Photo Synthesis"));
    }


    /**
     * Tests parsing a power-up line.
     */
    public void testParsePowerUp()
    {
        Card card = StudySessionManager.parseLine("PWR,PWR-1,,double points,,");

        assertEquals(true, card instanceof PowerUpCard);
        assertEquals(PowerUpCardType.DOUBLE_POINTS,
            ((PowerUpCard) card).getEffect());
    }


    /**
     * Tests that a quoted prompt may contain commas.
     */
    public void testParseQuotedPrompt()
    {
        Card card = StudySessionManager.parseLine(
            "TF,TF-1,\"Yes, \"\"quotes\"\" work.\",,false,");

        assertEquals("Yes, \"quotes\" work.", card.getPrompt());
    }


    /**
     * Tests that malformed lines are rejected.
     */
    public void testParseBadLines()
    {
        String[] badLines = {
            "XYZ,X-1,Prompt,,true,",
            "TF,TF-1,Prompt",
            "TF,TF-1,Prompt,,maybe,",
            "MCQ,MCQ-1,Prompt,a|b|c|d,five,",
            "MCQ,MCQ-1,Prompt,a|b|c,1,",
            "MCQ,MCQ-1,Prompt,a|b|c|d,1,-5",
            "PWR,PWR-1,,NOT_A_POWER,,",
            "TF,TF-1,\"Unclosed,,true,",
        };
        for (String line : badLines)
        {
            try
            {
                StudySessionManager.parseLine(line);
                fail("Expected IllegalArgumentException for: " + line);
            }
            catch (IllegalArgumentException e)
            {
                // expected
            }
        }
    }


    // ~ loadDeck ..............................................................

    /**
     * Tests loading a file: header, comments, and bad lines are skipped and
     * good cards keep file order.
     *
     * @throws IOException if the temporary file cannot be written
     */
    public void testLoadDeck() throws IOException
    {
        File file = File.createTempFile("deck", ".csv");
        file.deleteOnExit();
        try (PrintWriter out = new PrintWriter(file))
        {
            out.println("type,id,prompt,data,correct,points");
            out.println("# a comment");
            out.println("TF,TF-1,First,,true,");
            out.println("");
            out.println("TF,TF-2,Broken,,maybe,");
            out.println("PWR,PWR-1,,SKIP,,");
        }

        StudySessionManager session = new StudySessionManager();

        assertEquals(2, session.loadDeck(file.getPath()));
        assertEquals(2, session.getDeck().getNumberOfEntries());
        assertEquals("TF-1", session.getDeck().getHead().getId());
        assertEquals("PWR-1", session.getDeck().getTail().getId());
        Card[] cards = session.getDeck().toArray(new Card[3]);
        assertEquals("TF-1", cards[0].getId());
        assertNotNull("addToBack() did not link the second card", cards[1]);
        assertEquals("PWR-1", cards[1].getId());
        assertPrinted("Skipping line 5");
    }


    /**
     * Tests that a missing file is reported instead of crashing.
     */
    public void testLoadMissingFile()
    {
        StudySessionManager session = new StudySessionManager();

        assertEquals(0, session.loadDeck("no-such-file.csv"));
        assertEquals(true, session.getDeck().isEmpty());
        assertPrinted("Could not find deck file");
    }


    // ~ Answers ...............................................................

    /**
     * Tests that a correct answer scores points, builds the streak, and
     * removes the card.
     */
    public void testCorrectAnswer()
    {
        StudySessionManager session = sessionOf(mcq, tf);

        assertEquals(true, session.submitAnswer(mcq, "4"));
        assertEquals(100, player.getScore());
        assertEquals(1, player.getStreak());
        assertEquals(1, mcq.getAttempts());
        assertEquals(tf, session.getDeck().getHead());
        assertEquals(1, session.getDeck().getNumberOfEntries());
    }


    /**
     * Tests the streak bonus on the second correct answer in a row.
     */
    public void testStreakBonus()
    {
        StudySessionManager session = sessionOf(mcq, tf);

        session.submitAnswer(mcq, "4");
        session.submitAnswer(tf, "t");

        assertEquals(100 + 100 + StudySessionManager.STREAK_BONUS,
            player.getScore());
    }


    /**
     * Tests that a missed question moves to the back of the chain, resets
     * the streak, and leaves the score unchanged.
     */
    public void testIncorrectAnswerRequeues()
    {
        StudySessionManager session = sessionOf(mcq, tf, frq);
        player.incrementStreak();

        assertEquals(true, session.submitAnswer(mcq, "1"));

        assertEquals(0, player.getStreak());
        assertEquals(0, player.getScore());
        assertEquals(tf, session.getDeck().getHead());
        assertEquals(mcq, session.getDeck().getTail());
        assertEquals(3, session.getDeck().getNumberOfEntries());
        assertOrder(session, tf, frq, mcq);
    }


    /**
     * Tests that missing the only remaining card keeps it in the chain so it
     * is shown again on the next turn.
     */
    public void testIncorrectAnswerLastCard()
    {
        StudySessionManager session = sessionOf(mcq);

        session.handleIncorrectAnswer(mcq);

        assertEquals(1, session.getDeck().getNumberOfEntries());
        assertEquals(mcq, session.getDeck().getHead());
        assertEquals(mcq, session.getDeck().getTail());
        assertPrinted("last card");
    }


    /**
     * Tests that blank input re-prompts without grading the answer.
     */
    public void testBlankInputReprompts()
    {
        StudySessionManager session = sessionOf(frq);

        assertEquals(false, session.handleInput(frq, "   "));
        assertEquals(0, frq.getAttempts());
        assertEquals(1, session.getDeck().getNumberOfEntries());
    }


    /**
     * Tests that "exit" at a prompt ends the session early.
     */
    public void testExitInput()
    {
        StudySessionManager session = sessionOf(mcq);

        assertEquals(true, session.handleInput(mcq, " EXIT "));
        assertEquals(false, session.isRunning());
        assertPrinted("Session ended");
    }


    // ~ Power-ups .............................................................

    /**
     * Tests that drawing a power-up card moves it into the inventory.
     */
    public void testCollectPowerUp()
    {
        PowerUpCard skip = new PowerUpCard("PWR-1", PowerUpCardType.SKIP);
        StudySessionManager session = sessionOf(skip, mcq);

        session.runTurn();

        assertEquals(true, player.hasPowerUp(PowerUpCardType.SKIP));
        assertEquals(mcq, session.getDeck().getHead());
        assertEquals(1, session.getDeck().getNumberOfEntries());
    }


    /**
     * Tests that an unowned power-up is refused without ending the turn.
     */
    public void testUnownedPowerUp()
    {
        StudySessionManager session = sessionOf(mcq);

        assertEquals(false, session.handleInput(mcq, "use double points"));
        assertEquals(false, session.isDoublePointsActive());
        assertPrinted("You do not possess this power-up!");
    }


    /**
     * Tests that an unknown power-up name is refused.
     */
    public void testUnknownPowerUpName()
    {
        StudySessionManager session = sessionOf(mcq);

        assertEquals(false, session.handleInput(mcq, "use teleport"));
        assertPrinted("Unknown power-up");
    }


    /**
     * Tests that Double Points doubles the next correct answer only.
     */
    public void testDoublePoints()
    {
        player.addPowerUp(new PowerUpCard("PWR-1",
            PowerUpCardType.DOUBLE_POINTS));
        StudySessionManager session = sessionOf(mcq, tf);

        assertEquals(false, session.handleInput(mcq, "use double_points"));
        assertEquals(true, session.isDoublePointsActive());
        assertEquals(false, player.hasPowerUp(PowerUpCardType.DOUBLE_POINTS));

        session.submitAnswer(mcq, "4");

        assertEquals(200, player.getScore());
        assertEquals(false, session.isDoublePointsActive());
    }


    /**
     * Tests that Second Chance lets a wrong answer be retried once.
     */
    public void testSecondChance()
    {
        player.addPowerUp(new PowerUpCard("PWR-1",
            PowerUpCardType.SECOND_CHANCE));
        StudySessionManager session = sessionOf(mcq, tf);
        session.activatePowerUp(PowerUpCardType.SECOND_CHANCE, mcq);

        assertEquals(false, session.submitAnswer(mcq, "1"));
        assertEquals(mcq, session.getDeck().getHead());
        assertEquals(false, session.isSecondChanceActive());

        assertEquals(true, session.submitAnswer(mcq, "4"));
        assertEquals(2, mcq.getAttempts());
        assertEquals(100, player.getScore());
    }


    /**
     * Tests that Skip moves the question back without resetting the streak.
     */
    public void testSkip()
    {
        player.addPowerUp(new PowerUpCard("PWR-1", PowerUpCardType.SKIP));
        player.incrementStreak();
        StudySessionManager session = sessionOf(mcq, tf);

        assertEquals(true, session.activatePowerUp(PowerUpCardType.SKIP, mcq));

        assertEquals(1, player.getStreak());
        assertEquals(tf, session.getDeck().getHead());
        assertEquals(mcq, session.getDeck().getTail());
        assertOrder(session, tf, mcq);
    }


    /**
     * Tests that Reveal Letter gives the first letter of an FRQ answer.
     */
    public void testRevealLetter()
    {
        player.addPowerUp(new PowerUpCard("PWR-1",
            PowerUpCardType.REVEAL_LETTER));
        StudySessionManager session = sessionOf(frq);

        assertEquals(false,
            session.activatePowerUp(PowerUpCardType.REVEAL_LETTER, frq));
        assertPrinted("starts with \"P\"");
        assertEquals(false, player.hasPowerUp(PowerUpCardType.REVEAL_LETTER));
    }


    /**
     * Tests that a power-up used on the wrong question type is refused and
     * kept in the inventory.
     */
    public void testPowerUpWrongQuestionType()
    {
        player.addPowerUp(new PowerUpCard("PWR-1",
            PowerUpCardType.REVEAL_LETTER));
        player.addPowerUp(new PowerUpCard("PWR-2",
            PowerUpCardType.FIFTY_FIFTY));
        StudySessionManager session = sessionOf(tf);

        session.activatePowerUp(PowerUpCardType.REVEAL_LETTER, tf);
        session.activatePowerUp(PowerUpCardType.FIFTY_FIFTY, tf);

        assertEquals(2, player.getInventory().size());
    }


    /**
     * Tests that Fifty-Fifty always keeps the correct option.
     */
    public void testFiftyFifty()
    {
        for (int i = 0; i < 20; i++)
        {
            player.addPowerUp(new PowerUpCard("PWR-" + i,
                PowerUpCardType.FIFTY_FIFTY));
        }
        StudySessionManager session = sessionOf(mcq);
        for (int i = 0; i < 20; i++)
        {
            systemOut().clearHistory();
            session.activatePowerUp(PowerUpCardType.FIFTY_FIFTY, mcq);
            assertPrinted("4) 4");
        }
    }


    // ~ endSession ............................................................

    /**
     * Tests that clearing the deck shows the victory summary.
     */
    public void testEndSessionCleared()
    {
        StudySessionManager session = sessionOf(mcq);
        session.submitAnswer(mcq, "4");

        session.endSession(true);

        assertEquals(false, session.isRunning());
        assertEquals(100, player.getScore());
        assertPrinted("You cleared the deck!");
    }


    /**
     * Tests that unused power-ups become 50 bonus points each when the deck
     * is cleared, and that a second call awards nothing more.
     */
    public void testEndSessionUnusedPowerUps()
    {
        player.addPowerUp(new PowerUpCard("PWR-1", PowerUpCardType.SKIP));
        player.addPowerUp(new PowerUpCard("PWR-2",
            PowerUpCardType.DOUBLE_POINTS));
        StudySessionManager session = sessionOf();

        session.endSession(true);
        session.endSession(true);

        assertEquals(2 * StudySessionManager.UNUSED_POWER_UP_BONUS,
            player.getScore());
    }


    /**
     * Tests that exiting early gives no power-up bonus.
     */
    public void testEndSessionExitNoBonus()
    {
        player.addPowerUp(new PowerUpCard("PWR-1", PowerUpCardType.SKIP));
        StudySessionManager session = sessionOf(mcq);

        session.endSession(false);

        assertEquals(0, player.getScore());
    }


    // ~ Full game .............................................................

    /**
     * Plays a full game through startSession() with scripted input: a power
     * up is collected, a wrong answer is re-queued and retried, bad input
     * re-prompts, and the deck is cleared.
     * TODO (Zachias): re-check once ConsoleUI reads input instead of the
     * TEMP reader in StudySessionManager.
     */
    public void testFullGame()
    {
        PowerUpCard doublePoints =
            new PowerUpCard("PWR-1", PowerUpCardType.DOUBLE_POINTS);
        StudySessionManager session = sessionOf(doublePoints, mcq, tf);
        setSystemIn(multiline(
            "1",                  // MCQ wrong -> back of deck
            "",                   // TF blank -> re-prompt
            "use double points",  // activate on TF
            "true",               // TF right: 200
            "4"));                // MCQ right: 100 + 10 streak bonus

        session.startSession();

        assertEquals(false, session.isRunning());
        assertEquals(true, session.getDeck().isEmpty());
        assertEquals(310, player.getScore());
        assertPrinted("You cleared the deck!");
    }


    /**
     * Tests that typing "exit" during startSession() ends the game early.
     */
    public void testFullGameExit()
    {
        StudySessionManager session = sessionOf(mcq, tf);
        setSystemIn("exit");

        session.startSession();

        assertEquals(false, session.isRunning());
        assertEquals(2, session.getDeck().getNumberOfEntries());
        assertPrinted("Session ended");
    }


    /**
     * Tests that starting with an empty deck does not start the loop.
     */
    public void testStartEmptyDeck()
    {
        StudySessionManager session = sessionOf();

        session.startSession();

        assertEquals(false, session.isRunning());
        assertPrinted("no cards");
    }
}
