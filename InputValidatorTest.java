import student.TestCase;

public class InputValidatorTest extends TestCase {
    /**
     * Tests cleaning answers with extra spaces.
     */
    public void testCleanAnswer()
    {
        assertEquals("Hello",
            InputValidator.cleanAnswer("  Hello  "));

        assertEquals("New York",
            InputValidator.cleanAnswer("New York"));

        assertEquals("",
            InputValidator.cleanAnswer("   "));

        assertEquals("",
            InputValidator.cleanAnswer(null));
    }
    /**
     * Tests valid deck choices.
     */
    public void testParseDeckChoice()
    {
        assertEquals(1, InputValidator.parseDeckChoice("1"));
        assertEquals(2, InputValidator.parseDeckChoice(" 2 "));
        assertEquals(3, InputValidator.parseDeckChoice("3"));
    }


    /**
     * Tests invalid deck choices.
     */
    public void testInvalidDeckChoice()
    {
        String[] invalid = {
            "hello", "0", "4", "", "1.5", " "
        };

        for (String input : invalid)
        {
            Exception thrown = null;

            try
            {
                InputValidator.parseDeckChoice(input);
            }
            catch (Exception exception)
            {
                thrown = exception;
            }

            assertNotNull(thrown);
            assertTrue(thrown instanceof IllegalArgumentException);
        }
    }


    /**
     * Tests null input for deck selection.
     */
    public void testNullDeckChoice()
    {
        Exception thrown = null;

        try
        {
            InputValidator.parseDeckChoice(null);
        }
        catch (Exception exception)
        {
            thrown = exception;
        }

        assertNotNull(thrown);
        assertTrue(thrown instanceof IllegalArgumentException);
    }


    /**
     * Tests valid menu choices.
     */
    public void testValidMenuChoice()
    {
        assertTrue(
            InputValidator.isValidMenuChoice("1", 1, 3));

        assertTrue(
            InputValidator.isValidMenuChoice(" 2 ", 1, 3));

        assertTrue(
            InputValidator.isValidMenuChoice("3", 1, 3));
    }


    /**
     * Tests invalid menu choices.
     */
    public void testInvalidMenuChoice()
    {
        assertFalse(
            InputValidator.isValidMenuChoice("0", 1, 3));

        assertFalse(
            InputValidator.isValidMenuChoice("4", 1, 3));

        assertFalse(
            InputValidator.isValidMenuChoice("hello", 1, 3));

        assertFalse(
            InputValidator.isValidMenuChoice("", 1, 3));

        assertFalse(
            InputValidator.isValidMenuChoice(null, 1, 3));

        assertFalse(
            InputValidator.isValidMenuChoice("2", 3, 1));
    }


    /**
     * Tests valid player answers.
     */
    public void testValidAnswer()
    {
        assertTrue(InputValidator.isValidAnswer("True"));
        assertTrue(InputValidator.isValidAnswer(" 42 "));
        assertTrue(InputValidator.isValidAnswer("A"));
        assertTrue(InputValidator.isValidAnswer("New York"));
    }


    /**
     * Tests invalid player answers.
     */
    public void testInvalidAnswer()
    {
        assertFalse(InputValidator.isValidAnswer(""));
        assertFalse(InputValidator.isValidAnswer("   "));
        assertFalse(InputValidator.isValidAnswer(null));
    }

}
