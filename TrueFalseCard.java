/**
 * Represents a true/false question with a boolean ground truth.
 */
public class TrueFalseCard extends QuestionCard {

    private final boolean correctAnswer;  // the ground truth for the prompt

    /**
     * Constructs a true/false card worth the default point value.
     *
     * @param id            unique identifier for the card
     * @param prompt        the statement to be judged true or false
     * @param correctAnswer the ground truth for the prompt
     */
    public TrueFalseCard(String id, String prompt, boolean correctAnswer) {
        this(id, prompt, correctAnswer, DEFAULT_POINT_VALUE);
    }

    /**
     * Constructs a true/false card with a custom point value.
     *
     * @param id            unique identifier for the card
     * @param prompt        the statement to be judged true or false
     * @param correctAnswer the ground truth for the prompt
     * @param pointValue    base points awarded for a correct answer
     * @throws IllegalArgumentException if pointValue is not positive
     */
    public TrueFalseCard(String id, String prompt, boolean correctAnswer,
                         int pointValue) {
        super(id, prompt, CardType.TRUE_FALSE, pointValue);
        this.correctAnswer = correctAnswer;
    }

    /** Returns the ground truth boolean. O(1) */
    public boolean getCorrectAnswer() {
        return correctAnswer;
    }

    /**
     * Returns true if the input matches the ground truth. Accepted tokens
     * are "t", "f", "true", and "false" (case-insensitive, surrounding
     * whitespace ignored). Any other input, including null, is incorrect.
     *
     * @param input the user's answer
     * @return true if the parsed answer matches the correct answer
     */
    @Override
    public boolean checkAnswer(String input) {
        if (input == null) {
            return false;
        }
        String cleaned = input.trim().toLowerCase();
        if (cleaned.equals("t") || cleaned.equals("true")) {
            return correctAnswer;
        }
        if (cleaned.equals("f") || cleaned.equals("false")) {
            return !correctAnswer;
        }
        return false;
    }
}