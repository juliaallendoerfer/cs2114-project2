import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * Reads lines of user input with an optional time limit. A Scanner blocks
 * until a line arrives, so a background thread reads the lines instead and
 * hands them over through a queue that can be waited on with a timeout.
 */
public class TimedInput {

    /** Marks the end of input in the queue. Compared by identity. */
    private static final String END = new String("<end of input>");

    private final LinkedBlockingQueue<String> lines;  // lines read so far
    private boolean closed;                           // true once END is taken

    /**
     * Starts reading lines from the given stream in the background.
     *
     * @param in the stream to read, usually System.in
     * @throws IllegalArgumentException if in is null
     */
    public TimedInput(InputStream in) {
        if (in == null) {
            throw new IllegalArgumentException("Input stream cannot be null.");
        }
        this.lines = new LinkedBlockingQueue<String>();
        this.closed = false;

        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        Thread readerThread = new Thread(() -> {
            try {
                String line = reader.readLine();
                while (line != null) {
                    lines.add(line);
                    line = reader.readLine();
                }
            } catch (IOException e) {
                // treat a broken stream like the end of input
            }
            lines.add(END);
        }, "TimedInput reader");
        // a daemon thread never keeps the program running on its own
        readerThread.setDaemon(true);
        readerThread.start();
    }

    /**
     * Waits up to the given time for the next line.
     *
     * @param timeoutMillis the longest time to wait, in milliseconds
     * @return the line, or null if no line arrived in time or input has
     *         ended (use isClosed() to tell the two apart)
     */
    public String readLine(long timeoutMillis) {
        if (closed) {
            return null;
        }
        try {
            return unwrap(lines.poll(Math.max(0, timeoutMillis),
                TimeUnit.MILLISECONDS));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Waits as long as needed for the next line.
     *
     * @return the line, or null if input has ended
     */
    public String readLine() {
        if (closed) {
            return null;
        }
        try {
            return unwrap(lines.take());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
    }

    /**
     * Returns true once every line has been read and the input has ended.
     * O(1)
     */
    public boolean isClosed() {
        return closed;
    }

    /**
     * Returns the line, or null (and marks the input closed) for END.
     */
    private String unwrap(String line) {
        if (line == END) {
            closed = true;
            return null;
        }
        return line;
    }
}
