import static org.junit.Assert.*;
import org.junit.Test;
import java.util.ArrayList;

/**
 * Tests the MultipleChoiceCard class.
 */
public class MultipleChoiceCardTest extends TestCase
{
    // ~ Fields ................................................................
    private MultipleChoiceCard card;
    private ArrayList<String> options;

    /**
     * Sets up each test method.
     */
    public void setUp()
    {
        options = new ArrayList<String>();
        options.add("Mitochondria");
        options.add("Nucleus");
        options.add("Ribosome");
        options.add("Golgi apparatus");

        card = new MultipleChoiceCard(
            "MCQ-1",
            "Which organelle produces most of the cell's ATP?",
            options,
            1);
    }


    /**
     * Tests that a multiple-choice card is a QuestionCard.
     */
    public void testIsQuestionCard()
    {
        assertEquals(true, card instanceof QuestionCard);
    }


    /**
     * Tests that inherited properties are stored and the type is MCQ.
     */
    public void testInheritedProperties()
    {
        assertEquals("MCQ-1", card.getId());
        assertEquals("Which organelle produces most of the cell's ATP?",
            card.getPrompt());
        assertEquals(CardType.MCQ, card.getCardType());
    }


    /**
     * Tests that the card stores its options.
     */
    public void testGetOptions()
    {
        assertEquals(options, card.getOptions());
        assertEquals(4, card.getOptions().size());
        assertEquals("Mitochondria", card.getOptions().get(0));
        assertEquals("Golgi apparatus", card.getOptions().get(3));
    }


    /**
     * Tests that the card stores its correct option index.
     */
    public void testGetCorrectOptionIndex()
    {
        assertEquals(1, card.getCorrectOptionIndex());
    }


    /**
     * Tests that changing the original list does not change the card.
     */
    public void testOptionsAreCopied()
    {
        options.set(0, "Changed");
        options.add("Extra");

        assertEquals("Mitochondria", card.getOptions().get(0));
        assertEquals(4, card.getOptions().size());
    }


    /**
     * Tests the default point value and attempts.
     */
    public void testDefaultPointsAndAttempts()
    {
        assertEquals(100, card.getPointValue());
        assertEquals(0, card.getAttempts());
    }


    /**
     * Tests a card with a custom point value.
     */
    public void testCustomPointValue()
    {
        MultipleChoiceCard hard = new MultipleChoiceCard(
            "MCQ-2",
            "Prompt",
            options,
            4,
            300);

        assertEquals(300, hard.getPointValue());
        assertEquals(4, hard.getCorrectOptionIndex());
    }


    /**
     * Tests that every valid correct option index is accepted.
     */
    public void testAllValidCorrectIndexes()
    {
        for (int i = 1; i <= 4; i++)
        {
            MultipleChoiceCard other = new MultipleChoiceCard(
                "MCQ-X",
                "Prompt",
                options,
                i);

            assertEquals(i, other.getCorrectOptionIndex());
        }
    }


    // ~ checkAnswer ...........................................................

    /**
     * Tests a correct answer.
     */
    public void testCheckAnswerCorrect()
    {
        assertEquals(true, card.checkAnswer("1"));
    }


    /**
     * Tests that a correct answer with surrounding whitespace is accepted.
     */
    public void testCheckAnswerWithWhitespace()
    {
        assertEquals(true, card.checkAnswer("  1  "));
    }


    /**
     * Tests an incorrect option number.
     */
    public void testCheckAnswerIncorrectOption()
    {
        assertEquals(false, card.checkAnswer("4"));
        assertEquals(false, card.checkAnswer("2"));
    }


    /**
     * Tests input that is not a number.
     */
    public void testCheckAnswerNonNumber()
    {
        assertEquals(false, card.checkAnswer("abc"));
        assertEquals(false, card.checkAnswer("one"));
        assertEquals(false, card.checkAnswer("1.0"));
    }


    /**
     * Tests empty, blank, and null input.
     */
    public void testCheckAnswerEmptyBlankAndNull()
    {
        assertEquals(false, card.checkAnswer(""));
        assertEquals(false, card.checkAnswer("   "));
        assertEquals(false, card.checkAnswer(null));
    }


    /**
     * Tests numbers outside the 1 to 4 range.
     */
    public void testCheckAnswerOutOfRange()
    {
        assertEquals(false, card.checkAnswer("0"));
        assertEquals(false, card.checkAnswer("5"));
        assertEquals(false, card.checkAnswer("-1"));
    }


    /**
     * Tests that checkAnswer does not change the attempt count.
     */
    public void testCheckAnswerDoesNotChangeAttempts()
    {
        card.checkAnswer("1");
        card.checkAnswer("3");
        assertEquals(0, card.getAttempts());
    }


    // ~ Constructor validation ................................................

    /**
     * Tests that null options throw an exception.
     */
    public void testNullOptions()
    {
        try
        {
            new MultipleChoiceCard("MCQ-3", "Prompt", null, 1);
            fail("Expected IllegalArgumentException for null options");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that too few options throw an exception.
     */
    public void testTooFewOptions()
    {
        options.remove(3);

        try
        {
            new MultipleChoiceCard("MCQ-3", "Prompt", options, 1);
            fail("Expected IllegalArgumentException for 3 options");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that too many options throw an exception.
     */
    public void testTooManyOptions()
    {
        options.add("Lysosome");

        try
        {
            new MultipleChoiceCard("MCQ-3", "Prompt", options, 1);
            fail("Expected IllegalArgumentException for 5 options");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a null option entry throws an exception.
     */
    public void testNullOptionEntry()
    {
        options.set(2, null);

        try
        {
            new MultipleChoiceCard("MCQ-3", "Prompt", options, 1);
            fail("Expected IllegalArgumentException for null option");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a blank option entry throws an exception.
     */
    public void testBlankOptionEntry()
    {
        options.set(1, "   ");

        try
        {
            new MultipleChoiceCard("MCQ-3", "Prompt", options, 1);
            fail("Expected IllegalArgumentException for blank option");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a correct index below 1 throws an exception.
     */
    public void testCorrectIndexTooLow()
    {
        try
        {
            new MultipleChoiceCard("MCQ-3", "Prompt", options, 0);
            fail("Expected IllegalArgumentException for index 0");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a correct index above 4 throws an exception.
     */
    public void testCorrectIndexTooHigh()
    {
        try
        {
            new MultipleChoiceCard("MCQ-3", "Prompt", options, 5);
            fail("Expected IllegalArgumentException for index 5");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that an invalid point value throws an exception.
     */
    public void testInvalidPointValue()
    {
        try
        {
            new MultipleChoiceCard("MCQ-3", "Prompt", options, 1, 0);
            fail("Expected IllegalArgumentException for point value 0");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }
}