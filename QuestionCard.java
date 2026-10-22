/**
 * Abstract subclass of Card representing an interactive flashcard question.
 * Tracks the point value and the number of attempts, and defines the
 * contract for validating a user's response.
 */
public abstract class QuestionCard extends Card {

    /** Base points awarded when no point value is given. */
    public static final int DEFAULT_POINT_VALUE = 100;

    /** The difficulty levels a question can have, in lowercase. */
    public static final String[] DIFFICULTIES = {"easy", "medium", "hard"};

    private final int pointValue;     // base points for a correct answer
    private int attempts;             // times this question has been attempted
    private final String difficulty;  // "easy", "medium", or "hard"

    /**
     * Constructs a question card worth the default point value (100).
     *
     * @param id         unique identifier for the card
     * @param prompt     the question text
     * @param cardType   category of the card
     * @param difficulty "easy", "medium", or "hard" (any case)
     * @throws IllegalArgumentException if difficulty is not a known level
     */
    protected QuestionCard(String id, String prompt, CardType cardType,
                           String difficulty) {
        this(id, prompt, cardType, difficulty, DEFAULT_POINT_VALUE);
    }

    /**
     * Constructs a question card with a custom point value.
     *
     * @param id         unique identifier for the card
     * @param prompt     the question text
     * @param cardType   category of the card
     * @param difficulty "easy", "medium", or "hard" (any case)
     * @param pointValue base points awarded for a correct answer
     * @throws IllegalArgumentException if difficulty is not a known level or
     *                                  pointValue is not positive
     */
    protected QuestionCard(String id, String prompt, CardType cardType,
                           String difficulty, int pointValue) {
        super(id, prompt, cardType);
        if (pointValue <= 0) {
            throw new IllegalArgumentException(
                "Point value must be greater than zero.");
        }
        this.difficulty = parseDifficulty(difficulty);
        this.pointValue = pointValue;
        this.attempts = 0;
    }

    /**
     * Returns the lowercase difficulty level, or throws if it is unknown.
     */
    private static String parseDifficulty(String difficulty) {
        if (difficulty != null) {
            String cleaned = difficulty.trim().toLowerCase();
            for (String level : DIFFICULTIES) {
                if (level.equals(cleaned)) {
                    return level;
                }
            }
        }
        throw new IllegalArgumentException("Unknown difficulty \""
            + difficulty + "\". Use easy, medium, or hard.");
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
    
    /** Returns the difficulty: "easy", "medium", or "hard". O(1) */
    public String getDifficulty() {
        return difficulty;
    }

    /** Increments the attempt count for this card by one. O(1) */
    public void incrementAttempts() {
        attempts++;
    }
}