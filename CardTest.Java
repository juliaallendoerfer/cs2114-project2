import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests the Card class.
 */
public class CardTest extends student.TestCase
{
    // ~ Fields ................................................................
    private Card card;

    /**
     * Minimal concrete subclass used only to test the abstract Card class.
     */
    private static class TestCard extends Card
    {
        /**
         * Creates a test card.
         *
         * @param id
         *            the card id
         * @param prompt
         *            the card prompt
         * @param cardType
         *            the card type
         */
        TestCard(String id, String prompt, CardType cardType)
        {
            super(id, prompt, cardType);
        }
    }


    /**
     * Sets up each test method.
     */
    public void setUp()
    {
        card = new TestCard("MCQ-1", "What is DNA?", CardType.MCQ);
    }


    /**
     * Tests that the card stores its id.
     */
    public void testGetId()
    {
        assertEquals("MCQ-1", card.getId());
    }


    /**
     * Tests that the card stores its prompt.
     */
    public void testGetPrompt()
    {
        assertEquals("What is DNA?", card.getPrompt());
    }


    /**
     * Tests that the card stores its card type.
     */
    public void testGetCardType()
    {
        assertEquals(CardType.MCQ, card.getCardType());
    }


    /**
     * Tests that every card type can be stored.
     */
    public void testAllCardTypes()
    {
        for (CardType type : CardType.values())
        {
            Card other = new TestCard("ID-1", "Some prompt", type);
            assertEquals(type, other.getCardType());
        }
    }


    /**
     * Tests that the constructor trims whitespace from id and prompt.
     */
    public void testConstructorTrimsWhitespace()
    {
        Card trimmed = new TestCard(
            "  FRQ-1  ",
            "  Name the process.  ",
            CardType.FREE_RESPONSE);

        assertEquals("FRQ-1", trimmed.getId());
        assertEquals("Name the process.", trimmed.getPrompt());
    }


    /**
     * Tests that a null id throws an exception.
     */
    public void testNullId()
    {
        try
        {
            new TestCard(null, "Prompt", CardType.MCQ);
            fail("Expected IllegalArgumentException for null id");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that an empty id throws an exception.
     */
    public void testEmptyId()
    {
        try
        {
            new TestCard("", "Prompt", CardType.MCQ);
            fail("Expected IllegalArgumentException for empty id");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a blank id throws an exception.
     */
    public void testBlankId()
    {
        try
        {
            new TestCard("   ", "Prompt", CardType.MCQ);
            fail("Expected IllegalArgumentException for blank id");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a null prompt throws an exception.
     */
    public void testNullPrompt()
    {
        try
        {
            new TestCard("MCQ-1", null, CardType.MCQ);
            fail("Expected IllegalArgumentException for null prompt");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that an empty prompt throws an exception.
     */
    public void testEmptyPrompt()
    {
        try
        {
            new TestCard("MCQ-1", "", CardType.MCQ);
            fail("Expected IllegalArgumentException for empty prompt");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a blank prompt throws an exception.
     */
    public void testBlankPrompt()
    {
        try
        {
            new TestCard("MCQ-1", "   ", CardType.MCQ);
            fail("Expected IllegalArgumentException for blank prompt");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a null card type throws an exception.
     */
    public void testNullCardType()
    {
        try
        {
            new TestCard("MCQ-1", "Prompt", null);
            fail("Expected IllegalArgumentException for null card type");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests the card string representation.
     */
    public void testToString()
    {
        Card power = new TestCard(
            "PWR-1",
            "Double your points!",
            CardType.POWER_UP);

        assertEquals("[PWR-1] Double your points!", power.toString());
    }
}