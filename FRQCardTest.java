import java.util.ArrayList;

import student.TestCase;

/**
 * Tests the FRQCard class.
 */
public class FRQCardTest extends TestCase
{
    // ~ Fields ................................................................
    private FRQCard card;
    private ArrayList<String> answers;

    /**
     * Sets up each test method.
     */
    public void setUp()
    {
        answers = new ArrayList<String>();
        answers.add("Photosynthesis");
        answers.add("Light reactions");

        card = new FRQCard(
            "FRQ-1",
            "What process do plants use to convert light into energy?",
            answers);
    }


    /**
     * Tests that a free-response card is a QuestionCard.
     */
    public void testIsQuestionCard()
    {
        assertEquals(true, card instanceof QuestionCard);
    }


    /**
     * Tests that inherited properties are stored and the type is
     * FREE_RESPONSE.
     */
    public void testInheritedProperties()
    {
        assertEquals("FRQ-1", card.getId());
        assertEquals(
            "What process do plants use to convert light into energy?",
            card.getPrompt());
        assertEquals(CardType.FREE_RESPONSE, card.getCardType());
    }


    /**
     * Tests that the card stores its acceptable answers.
     */
    public void testGetAcceptableAnswers()
    {
        assertEquals(answers, card.getAcceptableAnswers());
        assertEquals(2, card.getAcceptableAnswers().size());
        assertEquals("Photosynthesis", card.getAcceptableAnswers().get(0));
    }


    /**
     * Tests that changing the original list does not change the card.
     */
    public void testAnswersAreCopied()
    {
        answers.set(0, "Changed");
        answers.add("Extra");

        assertEquals("Photosynthesis", card.getAcceptableAnswers().get(0));
        assertEquals(2, card.getAcceptableAnswers().size());
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
        FRQCard hard = new FRQCard(
            "FRQ-2",
            "Prompt",
            answers,
            300);

        assertEquals(300, hard.getPointValue());
    }


    /**
     * Tests a card with a single acceptable answer.
     */
    public void testSingleAcceptableAnswer()
    {
        ArrayList<String> single = new ArrayList<String>();
        single.add("DNA");

        FRQCard dna = new FRQCard("FRQ-3", "Prompt", single);

        assertEquals(true, dna.checkAnswer("dna"));
        assertEquals(1, dna.getAcceptableAnswers().size());
    }


    // ~ checkAnswer ...........................................................

    /**
     * Tests an exact match.
     */
    public void testCheckAnswerExactMatch()
    {
        assertEquals(true, card.checkAnswer("Photosynthesis"));
    }


    /**
     * Tests that matching ignores case.
     */
    public void testCheckAnswerIgnoresCase()
    {
        assertEquals(true, card.checkAnswer("photosynthesis"));
        assertEquals(true, card.checkAnswer("PHOTOSYNTHESIS"));
        assertEquals(true, card.checkAnswer("PhotoSynthesis"));
    }


    /**
     * Tests that surrounding whitespace is ignored.
     */
    public void testCheckAnswerTrimsWhitespace()
    {
        assertEquals(true, card.checkAnswer(" photosynthesis "));
        assertEquals(true, card.checkAnswer("\tPhotosynthesis\n"));
    }


    /**
     * Tests that any of the acceptable answers is accepted.
     */
    public void testCheckAnswerAnyAcceptableAnswer()
    {
        assertEquals(true, card.checkAnswer("Photosynthesis"));
        assertEquals(true, card.checkAnswer("light reactions"));
    }


    /**
     * Tests that extra spaces between words are collapsed.
     */
    public void testCheckAnswerCollapsesInnerWhitespace()
    {
        assertEquals(true, card.checkAnswer("light    reactions"));
        assertEquals(true, card.checkAnswer("  Light \t reactions  "));
    }


    /**
     * Tests that acceptable answers stored with odd spacing or case still
     * match normal input.
     */
    public void testCheckAnswerCleansStoredAnswers()
    {
        ArrayList<String> messy = new ArrayList<String>();
        messy.add("  Cellular   RESPIRATION ");

        FRQCard other = new FRQCard("FRQ-4", "Prompt", messy);

        assertEquals(true, other.checkAnswer("cellular respiration"));
    }


    /**
     * Tests a wrong answer.
     */
    public void testCheckAnswerWrongAnswer()
    {
        assertEquals(false, card.checkAnswer("cellular respiration"));
        assertEquals(false, card.checkAnswer("osmosis"));
    }


    /**
     * Tests that typos are not accepted.
     */
    public void testCheckAnswerTypos()
    {
        assertEquals(false, card.checkAnswer("photosynthesys"));
        assertEquals(false, card.checkAnswer("photosynthes"));
        assertEquals(false, card.checkAnswer("photo synthesis"));
    }


    /**
     * Tests that a partial or extended answer is not accepted.
     */
    public void testCheckAnswerPartialAndExtended()
    {
        assertEquals(false, card.checkAnswer("light"));
        assertEquals(false, card.checkAnswer("photosynthesis in plants"));
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
     * Tests that checkAnswer does not change the attempt count.
     */
    public void testCheckAnswerDoesNotChangeAttempts()
    {
        card.checkAnswer("photosynthesis");
        card.checkAnswer("wrong");
        assertEquals(0, card.getAttempts());
    }


    // ~ Constructor validation ................................................

    /**
     * Tests that a null answer list throws an exception.
     */
    public void testNullAnswerList()
    {
        try
        {
            new FRQCard("FRQ-5", "Prompt", null);
            fail("Expected IllegalArgumentException for null answers");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that an empty answer list throws an exception.
     */
    public void testEmptyAnswerList()
    {
        try
        {
            new FRQCard(
                "FRQ-5",
                "Prompt",
                new ArrayList<String>());
            fail("Expected IllegalArgumentException for empty answer list");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a null answer entry throws an exception.
     */
    public void testNullAnswerEntry()
    {
        answers.add(null);

        try
        {
            new FRQCard("FRQ-5", "Prompt", answers);
            fail("Expected IllegalArgumentException for null answer entry");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }


    /**
     * Tests that a blank answer entry throws an exception.
     */
    public void testBlankAnswerEntry()
    {
        answers.set(1, "   ");

        try
        {
            new FRQCard("FRQ-5", "Prompt", answers);
            fail("Expected IllegalArgumentException for blank answer entry");
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
            new FRQCard("FRQ-5", "Prompt", answers, 0);
            fail("Expected IllegalArgumentException for point value 0");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }
}