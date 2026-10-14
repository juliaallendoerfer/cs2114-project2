import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests the PowerUpCard class.
 */
public class PowerUpCardTest extends TestCase
{
    // ~ Fields ................................................................
    private PowerUpCard card;

    /**
     * Sets up each test method.
     */
    public void setUp()
    {
        card = new PowerUpCard(
            "PWR-1",
            "Power-Up: Double Points!",
            PowerUpCardType.DOUBLE_POINTS,
            "Doubles the points on your next correct answer.");
    }


    /**
     * Tests that a power-up card is a Card but not a QuestionCard.
     */
    public void testIsCardButNotQuestionCard()
    {
        Card asCard = card;

        assertEquals(true, asCard instanceof Card);
        assertEquals(false, asCard instanceof QuestionCard);
    }


    /**
     * Tests that inherited properties are stored and the type is POWER_UP.
     */
    public void testInheritedProperties()
    {
        assertEquals("PWR-1", card.getId());
        assertEquals("Power-Up: Double Points!", card.getPrompt());
        assertEquals(CardType.POWER_UP, card.getCardType());
    }


    /**
     * Tests that the card stores its effect.
     */
    public void testGetEffect()
    {
        assertEquals(PowerUpCardType.DOUBLE_POINTS, card.getEffect());
    }


    /**
     * Tests that the card stores its description.
     */
    public void testGetDescription()
    {
        assertEquals(
            "Doubles the points on your next correct answer.",
            card.getDescription());
    }


    /**
     * Tests that the description is trimmed.
     */
    public void testDescriptionIsTrimmed()
    {
        PowerUpCard padded = new PowerUpCard(
            "PWR-2",
            "Prompt",
            PowerUpCardType.SKIP,
            "  Allows you to skip the current question.  ");

        assertEquals("Allows you to skip the current question.", padded.getDescription());
    }


    /**
     * Tests that every effect can be stored on a card.
     */
    public void testAllEffects()
    {
        for (PowerUpCardType type : PowerUpCardType.values())
        {
            PowerUpCard other = new PowerUpCard(
                "PWR-X",
                "Prompt",
                type,
                "Description");

            assertEquals(type, other.getEffect());
            assertEquals(CardType.POWER_UP, other.getCardType());
        }
    }


    // ~ Convenience constructor ...............................................

    /**
     * Tests the default Double Points card.
     */
    public void testDefaultDoublePoints()
    {
        PowerUpCard other = new PowerUpCard("PWR-3", PowerUpCardType.DOUBLE_POINTS);

        assertEquals("PWR-3", other.getId());
        assertEquals(PowerUpCardType.DOUBLE_POINTS, other.getEffect());
        assertEquals("Power-Up: Double Points!", other.getPrompt());
        assertEquals(
            "Doubles the points earned on your next correct answer.",
            other.getDescription());
    }


    /**
     * Tests the default Second Chance card.
     */
    public void testDefaultSecondChance()
    {
        PowerUpCard other = new PowerUpCard("PWR-4", PowerUpCardType.SECOND_CHANCE);

        assertEquals(PowerUpCardType.SECOND_CHANCE, other.getEffect());
        assertEquals("Power-Up: Second Chance!", other.getPrompt());
        assertEquals(
            "Lets you try again after an incorrect answer.",
            other.getDescription());
    }


    /**
     * Tests the default Hint card.
     */
    public void testDefaultHint()
    {
        PowerUpCard other = new PowerUpCard("PWR-5", PowerUpCardType.REVEAL_LETTER);

        assertEquals(PowerUpCardType.REVEAL_LETTER, other.getEffect());
        assertEquals("Power-Up: Reveal Letter!", other.getPrompt());
        assertEquals("Reveals a letter in the current question. (For FRQ questions only)",
            other.getDescription());
    }


    /**
     * Tests that every effect has non-empty default text.
     */
    public void testDefaultTextExistsForAllEffects()
    {
        for (PowerUpCardType type : PowerUpCardType.values())
        {
            PowerUpCard other = new PowerUpCard("PWR-X", type);

            assertEquals(false, other.getPrompt().isEmpty());
            assertEquals(false, other.getDescription().isEmpty());
        }
    }


    // ~ Constructor validation ................................................

    /**
     * Tests that a null effect throws an exception.
     */
    public void testNullEffect()
    {
        try
        {
            new PowerUpCard("PWR-6", "Prompt", null, "Description");
            fail("Expected IllegalArgumentException for null effect");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a null effect throws an exception in the convenience
     * constructor.
     */
    public void testNullEffectConvenienceConstructor()
    {
        try
        {
            new PowerUpCard("PWR-6", null);
            fail("Expected IllegalArgumentException for null effect");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a null description throws an exception.
     */
    public void testNullDescription()
    {
        try
        {
            new PowerUpCard("PWR-6", "Prompt", PowerUpCardType.SKIP, null);
            fail("Expected IllegalArgumentException for null description");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that an empty description throws an exception.
     */
    public void testEmptyDescription()
    {
        try
        {
            new PowerUpCard("PWR-6", "Prompt", PowerUpCardType.FIFTY_FIFTY, "");
            fail("Expected IllegalArgumentException for empty description");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a blank description throws an exception.
     */
    public void testBlankDescription()
    {
        try
        {
            new PowerUpCard("PWR-6", "Prompt", PowerUpCardType.SKIP, "   ");
            fail("Expected IllegalArgumentException for blank description");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that invalid Card properties are still rejected.
     */
    public void testInvalidIdAndPrompt()
    {
        try
        {
            new PowerUpCard("", "Prompt", PowerUpCardType.REVEAL_LETTER, "Description");
            fail("Expected IllegalArgumentException for empty id");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }

        try
        {
            new PowerUpCard("PWR-6", " ", PowerUpCardType.SKIP, "Description");
            fail("Expected IllegalArgumentException for blank prompt");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }
}