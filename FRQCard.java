import java.util.ArrayList;

/**
 * Represents a free-response question (FRQ) that stores a set of acceptable
 * target answers. Answers are compared case-insensitively.
 */
public class FreeResponseCard extends QuestionCard {

    private final ArrayList<String> acceptableAnswers;  // accepted answers

    /**
     * Constructs a free-response card worth the default point value.
     *
     * @param id                unique identifier for the card
     * @param prompt            the question text
     * @param acceptableAnswers one or more answers that count as correct
     * @throws IllegalArgumentException if the answer list is null, empty, or
     *                                  contains a null or blank entry
     */
    public FreeResponseCard(String id, String prompt,
                            ArrayList<String> acceptableAnswers) {
        this(id, prompt, acceptableAnswers, DEFAULT_POINT_VALUE);
    }

    /**
     * Constructs a free-response card with a custom point value.
     *
     * @param id                unique identifier for the card
     * @param prompt            the question text
     * @param acceptableAnswers one or more answers that count as correct
     * @param pointValue        base points awarded for a correct answer
     * @throws IllegalArgumentException if the answer list is invalid or the
     *                                  point value is not positive
     */
    public FreeResponseCard(String id, String prompt,
                            ArrayList<String> acceptableAnswers,
                            int pointValue) {
        super(id, prompt, CardType.FREE_RESPONSE, pointValue);

        if (acceptableAnswers == null || acceptableAnswers.isEmpty()) {
            throw new IllegalArgumentException(
                "A free-response card needs at least one acceptable answer.");
        }
        for (String answer : acceptableAnswers) {
            if (answer == null || answer.trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "Acceptable answers cannot be null or blank.");
            }
        }

        // copy so later changes to the caller's list can't alter this card
        this.acceptableAnswers = new ArrayList<String>(acceptableAnswers);
    }

    /** Returns the list of acceptable answer strings. O(1) */
    public ArrayList<String> getAcceptableAnswers() {
        return acceptableAnswers;
    }

    /**
     * Returns true if the input matches any acceptable answer. Both the
     * input and the acceptable answers are trimmed, lowercased, and have
     * repeated inner whitespace collapsed before comparing. Null or blank
     * input is simply incorrect.
     *
     * @param input the user's answer
     * @return true if the normalized input matches an acceptable answer
     */
    @Override
    public boolean checkAnswer(String input) {
        if (input == null) {
            return false;
        }
        String cleanedInput = clean(input);
        if (cleanedInput.isEmpty()) {
            return false;
        }
        for (String answer : acceptableAnswers) {
            if (clean(answer).equals(cleanedInput)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Trims, lowercases, and collapses runs of whitespace to single spaces.
     */
    private static String clean(String text) {
        return text.trim().toLowerCase().replaceAll("\\s+", " ");
    }
}