import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests the TrueFalseCard class.
 */
public class TrueFalseCardTest extends TestCase
{
    // ~ Fields ................................................................
    private TrueFalseCard trueCard;
    private TrueFalseCard falseCard;

    /**
     * Sets up each test method.
     */
    public void setUp()
    {
        trueCard = new TrueFalseCard(
            "TF-1",
            "Water boils at 100 degrees Celsius at sea level.",
            true);

        falseCard = new TrueFalseCard(
            "TF-2",
            "The Sun orbits the Earth.",
            false);
    }


    /**
     * Tests that a true/false card is a QuestionCard.
     */
    public void testIsQuestionCard()
    {
        assertEquals(true, trueCard instanceof QuestionCard);
    }


    /**
     * Tests that inherited properties are stored and the type is TRUE_FALSE.
     */
    public void testInheritedProperties()
    {
        assertEquals("TF-1", trueCard.getId());
        assertEquals("Water boils at 100 degrees Celsius at sea level.",
            trueCard.getPrompt());
        assertEquals(CardType.TRUE_FALSE, trueCard.getCardType());
    }


    /**
     * Tests that the card stores its correct answer.
     */
    public void testGetCorrectAnswer()
    {
        assertEquals(true, trueCard.getCorrectAnswer());
        assertEquals(false, falseCard.getCorrectAnswer());
    }


    /**
     * Tests the default point value and attempts.
     */
    public void testDefaultPointsAndAttempts()
    {
        assertEquals(100, trueCard.getPointValue());
        assertEquals(0, trueCard.getAttempts());
    }


    /**
     * Tests a card with a custom point value.
     */
    public void testCustomPointValue()
    {
        TrueFalseCard hard = new TrueFalseCard("TF-3", "Prompt", true, 250);

        assertEquals(250, hard.getPointValue());
        assertEquals(true, hard.getCorrectAnswer());
    }


    /**
     * Tests that an invalid point value throws an exception.
     */
    public void testInvalidPointValue()
    {
        try
        {
            new TrueFalseCard("TF-3", "Prompt", true, 0);
            fail("Expected IllegalArgumentException for point value 0");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    // ~ checkAnswer, ground truth is true .....................................

    /**
     * Tests that "true" and "t" are correct when the answer is true.
     */
    public void testTrueCardAcceptsTrueTokens()
    {
        assertEquals(true, trueCard.checkAnswer("true"));
        assertEquals(true, trueCard.checkAnswer("t"));
    }


    /**
     * Tests that "false" and "f" are incorrect when the answer is true.
     */
    public void testTrueCardRejectsFalseTokens()
    {
        assertEquals(false, trueCard.checkAnswer("false"));
        assertEquals(false, trueCard.checkAnswer("f"));
    }


    // ~ checkAnswer, ground truth is false ....................................

    /**
     * Tests that "false" and "f" are correct when the answer is false.
     */
    public void testFalseCardAcceptsFalseTokens()
    {
        assertEquals(true, falseCard.checkAnswer("false"));
        assertEquals(true, falseCard.checkAnswer("f"));
    }


    /**
     * Tests that "true" and "t" are incorrect when the answer is false.
     */
    public void testFalseCardRejectsTrueTokens()
    {
        assertEquals(false, falseCard.checkAnswer("true"));
        assertEquals(false, falseCard.checkAnswer("t"));
    }


    // ~ checkAnswer, input handling ...........................................

    /**
     * Tests that answers are not case-sensitive.
     */
    public void testCheckAnswerIgnoresCase()
    {
        assertEquals(true, trueCard.checkAnswer("TRUE"));
        assertEquals(true, trueCard.checkAnswer("T"));
        assertEquals(true, trueCard.checkAnswer("TrUe"));
        assertEquals(true, falseCard.checkAnswer("FALSE"));
        assertEquals(true, falseCard.checkAnswer("F"));
    }


    /**
     * Tests that surrounding whitespace is ignored.
     */
    public void testCheckAnswerTrimsWhitespace()
    {
        assertEquals(true, trueCard.checkAnswer("  true  "));
        assertEquals(true, falseCard.checkAnswer(" f "));
    }


    /**
     * Tests that unrecognized input is incorrect for both cards.
     */
    public void testCheckAnswerUnrecognizedInput()
    {
        assertEquals(false, trueCard.checkAnswer("yes"));
        assertEquals(false, trueCard.checkAnswer("1"));
        assertEquals(false, trueCard.checkAnswer("maybe"));
        assertEquals(false, falseCard.checkAnswer("no"));
        assertEquals(false, falseCard.checkAnswer("0"));
        assertEquals(false, falseCard.checkAnswer("maybe"));
    }


    /**
     * Tests that unrecognized input is incorrect even when it would be
     * "wrong" for the opposite card, so bad input never counts as a match
     * for a false answer.
     */
    public void testBadInputNeverMatchesFalseAnswer()
    {
        assertEquals(false, falseCard.checkAnswer("garbage"));
        assertEquals(false, falseCard.checkAnswer(""));
        assertEquals(false, falseCard.checkAnswer(null));
    }


    /**
     * Tests empty, blank, and null input.
     */
    public void testCheckAnswerEmptyBlankAndNull()
    {
        assertEquals(false, trueCard.checkAnswer(""));
        assertEquals(false, trueCard.checkAnswer("   "));
        assertEquals(false, trueCard.checkAnswer(null));
    }


    /**
     * Tests that partial words are not accepted.
     */
    public void testCheckAnswerPartialWords()
    {
        assertEquals(false, trueCard.checkAnswer("tru"));
        assertEquals(false, trueCard.checkAnswer("truee"));
        assertEquals(false, falseCard.checkAnswer("fals"));
    }


    /**
     * Tests that checkAnswer does not change the attempt count.
     */
    public void testCheckAnswerDoesNotChangeAttempts()
    {
        trueCard.checkAnswer("true");
        trueCard.checkAnswer("false");
        assertEquals(0, trueCard.getAttempts());
    }
}