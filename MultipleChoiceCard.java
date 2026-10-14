import java.util.ArrayList;

/**
 * Represents a multiple-choice question (MCQ) with four answer choices and
 * a single correct selection.
 */
public class MultipleChoiceCard extends QuestionCard {

    /** Number of answer choices every MCQ must have. */
    public static final int NUMBER_OF_OPTIONS = 4;

    private final ArrayList<String> options;  // the four answer choices
    private final int correctOptionIndex;     // 1-based index of the answer

    /**
     * Constructs a multiple-choice card worth the default point value.
     *
     * @param id                 unique identifier for the card
     * @param prompt             the question text
     * @param options            exactly four answer choices
     * @param correctOptionIndex 1-based index (1 to 4) of the correct choice
     * @throws IllegalArgumentException if the options or index are invalid
     */
    public MultipleChoiceCard(String id, String prompt,
                              ArrayList<String> options,
                              int correctOptionIndex) {
        this(id, prompt, options, correctOptionIndex, DEFAULT_POINT_VALUE);
    }

    /**
     * Constructs a multiple-choice card with a custom point value.
     *
     * @param id                 unique identifier for the card
     * @param prompt             the question text
     * @param options            exactly four answer choices
     * @param correctOptionIndex 1-based index (1 to 4) of the correct choice
     * @param pointValue         base points awarded for a correct answer
     * @throws IllegalArgumentException if the options, index, or point value
     *                                  are invalid
     */
    public MultipleChoiceCard(String id, String prompt,
                              ArrayList<String> options,
                              int correctOptionIndex, int pointValue) {
        super(id, prompt, CardType.MCQ, pointValue);

        if (options == null || options.size() != NUMBER_OF_OPTIONS) {
            throw new IllegalArgumentException(
                "A multiple-choice card needs exactly "
                + NUMBER_OF_OPTIONS + " options.");
        }
        for (String option : options) {
            if (option == null || option.trim().isEmpty()) {
                throw new IllegalArgumentException(
                    "Answer options cannot be null or blank.");
            }
        }
        if (correctOptionIndex < 1 || correctOptionIndex > NUMBER_OF_OPTIONS) {
            throw new IllegalArgumentException(
                "Correct option index must be between 1 and "
                + NUMBER_OF_OPTIONS + ".");
        }

        // copy so later changes to the caller's list can't alter this card
        this.options = new ArrayList<String>(options);
        this.correctOptionIndex = correctOptionIndex;
    }

    /** Returns the four displayed answer choices. O(1) */
    public ArrayList<String> getOptions() {
        return options;
    }

    /** Returns the 1-based index of the correct answer choice. O(1) */
    public int getCorrectOptionIndex() {
        return correctOptionIndex;
    }

    /**
     * Returns true if the input is the number of the correct option.
     * Input that is not a whole number (or is null) is simply incorrect.
     *
     * @param input the user's answer, expected to be "1" through "4"
     * @return true if the parsed input matches the correct option index
     */
    @Override
    public boolean checkAnswer(String input) {
        if (input == null) {
            return false;
        }
        try {
            return Integer.parseInt(input.trim()) == correctOptionIndex;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}