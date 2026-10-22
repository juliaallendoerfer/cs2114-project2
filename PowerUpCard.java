/**
 * Represents a special modifier card that can be drawn from the chain and
 * held in the player's inventory to modify the game state (for example,
 * Double Points, Second Chance, or Skip).
 */
public class PowerUpCard extends Card {

    private final PowerUpCardType effect;  // which power-up behavior this is
    private final String description;  // how the power-up benefits the user

    /**
     * Constructs a power-up card with custom display text.
     *
     * @param id          unique identifier for the card (e.g., "PWR-1")
     * @param prompt      the banner text shown when the card is drawn
     * @param effect      the power-up behavior
     * @param description explanation of how the power-up benefits the user
     * @throws IllegalArgumentException if effect is null, description is
     *                                  null or blank, or id/prompt are invalid
     */
    public PowerUpCard(String id, String prompt, PowerUpCardType effect,
                       String description) {
        super(id, prompt, CardType.POWER_UP);
        if (effect == null) {
            throw new IllegalArgumentException("Power-up effect cannot be null.");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException(
                "Power-up description cannot be null or blank.");
        }
        this.effect = effect;
        this.description = description.trim();
    }

    /**
     * Constructs a power-up card using the standard prompt and description
     * for the given effect.
     *
     * @param id     unique identifier for the card (e.g., "PWR-1")
     * @param effect the power-up behavior
     * @throws IllegalArgumentException if effect is null or id is invalid
     */
    public PowerUpCard(String id, PowerUpCardType effect) {
        this(id, defaultPrompt(effect), effect, defaultDescription(effect));
    }

    /** Returns the power-up effect enum. O(1) */
    public PowerUpCardType getEffect() {
        return effect;
    }

    /** Returns the human-readable description of the power-up effect. O(1) */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the standard banner text for an effect.
     */
    private static String defaultPrompt(PowerUpCardType effect) {
        if (effect == null) {
            throw new IllegalArgumentException("Power-up effect cannot be null.");
        }
        switch (effect) {
            case DOUBLE_POINTS:
                return "Power-Up: Double Points!";
            case SECOND_CHANCE:
                return "Power-Up: Second Chance!";
            case SKIP:
                return "Power-Up: Skip!";
            case REVEAL_LETTER:
                return "Power-Up: Reveal Letter!";
            case FIFTY_FIFTY:
                return "Power-Up: Fifty-Fifty!";
            default:
                return "Power-Up!";
        }
    }

    /**
     * Returns the standard description for an effect.
     */
    private static String defaultDescription(PowerUpCardType effect) {
        if (effect == null) {
            throw new IllegalArgumentException("Power-up effect cannot be null.");
        }
        switch (effect) {
            case DOUBLE_POINTS:
                return "Doubles the points earned on your next correct answer.";
            case SECOND_CHANCE:
                return "Lets you try again after an incorrect answer.";
            case SKIP:
                return "Allows you to skip the current question.";
            case REVEAL_LETTER:
                return "Reveals a letter in the current question. (For FRQ questions only)";
            case FIFTY_FIFTY:
                return "Removes two incorrect answer choices. (For MCQ questions only)";
            default:
                return "A special power-up.";
        }
    }
}