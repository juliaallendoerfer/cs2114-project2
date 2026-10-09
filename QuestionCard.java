/**
 * Abstract subclass of Card representing an interactive flashcard question.
 * Tracks the point value and the number of attempts, and defines the
 * contract for validating a user's response.
 */
public abstract class QuestionCard extends Card {

    /** Base points awarded when no point value is given. */
    public static final int DEFAULT_POINT_VALUE = 100;

    private final int pointValue;  // base points for a correct answer
    private int attempts;          // times this question has been attempted

    /**
     * Constructs a question card worth the default point value (100).
     *
     * @param id       unique identifier for the card
     * @param prompt   the question text
     * @param cardType category of the card
     */
    protected QuestionCard(String id, String prompt, CardType cardType) {
        this(id, prompt, cardType, DEFAULT_POINT_VALUE);
    }

    /**
     * Constructs a question card with a custom point value.
     *
     * @param id         unique identifier for the card
     * @param prompt     the question text
     * @param cardType   category of the card
     * @param pointValue base points awarded for a correct answer
     * @throws IllegalArgumentException if pointValue is not positive
     */
    protected QuestionCard(String id, String prompt, CardType cardType,
                           int pointValue) {
        super(id, prompt, cardType);
        if (pointValue <= 0) {
            throw new IllegalArgumentException(
                "Point value must be greater than zero.");
        }
        this.pointValue = pointValue;
        this.attempts = 0;
    }

    /**
     * Evaluates whether the user's sanitized answer is correct.
     *
     * @param input the sanitized user answer
     * @return true if the answer is correct
     */
    public abstract boolean checkAnswer(String input);

    /** Returns the base score points awarded for this card. O(1) */
    public int getPointValue() {
        return pointValue;
    }

    /** Returns the number of attempts made on this card. O(1) */
    public int getAttempts() {
        return attempts;
    }

    /** Increments the attempt count for this card by one. O(1) */
    public void incrementAttempts() {
        attempts++;
    }
}