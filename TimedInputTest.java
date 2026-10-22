import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import student.TestCase;

/**
 * Tests the TimedInput class.
 */
public class TimedInputTest extends TestCase
{
    /**
     * Returns a TimedInput reading the given text.
     */
    private TimedInput inputOf(String text)
    {
        return new TimedInput(new ByteArrayInputStream(text.getBytes()));
    }


    /**
     * Tests that lines come back in order and then the input closes.
     */
    public void testReadLines()
    {
        TimedInput input = inputOf("first\nsecond\n");

        assertEquals("first", input.readLine(1000));
        assertEquals("second", input.readLine());
        assertEquals(false, input.isClosed());
        assertNull(input.readLine(1000));
        assertEquals(true, input.isClosed());
        assertNull(input.readLine());
    }


    /**
     * Tests that empty input closes right away.
     */
    public void testEmptyInput()
    {
        TimedInput input = inputOf("");

        assertNull(input.readLine());
        assertEquals(true, input.isClosed());
    }


    /**
     * Tests that waiting with no input returns null after the timeout
     * without closing, and that a line typed later is still read.
     *
     * @throws IOException if the pipe cannot be set up
     */
    public void testTimeout() throws IOException
    {
        PipedOutputStream keyboard = new PipedOutputStream();
        TimedInput input = new TimedInput(new PipedInputStream(keyboard));

        long start = System.currentTimeMillis();
        assertNull(input.readLine(300));
        long waited = System.currentTimeMillis() - start;

        assertEquals(false, input.isClosed());
        assertTrue("Returned after only " + waited + " ms", waited >= 250);

        keyboard.write("late\n".getBytes());
        keyboard.flush();
        assertEquals("late", input.readLine(2000));
        keyboard.close();
    }


    /**
     * Tests that a null stream is rejected.
     */
    public void testNullStream()
    {
        try
        {
            new TimedInput(null);
            fail("Expected IllegalArgumentException for a null stream");
        }
        catch (IllegalArgumentException e)
        {
            // expected
        }
    }
}
