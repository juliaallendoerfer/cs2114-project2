/**
 * Abstract superclass for every card handled by FlashClass.
 * Defines the identity and display properties shared by question cards
 * and power-up cards: a unique id, the prompt/display text, and a card type.
 */
public abstract class Card {

    private final String id;          // e.g., "MCQ-1", "PWR-2"
    private final String prompt;      // question text or power-up display description
    private final CardType cardType;  // MCQ, TRUE_FALSE, FREE_RESPONSE, POWER_UP

    /**
     * Constructs a card with the given identity and display text.
     *
     * @param id       unique identifier for the card
     * @param prompt   question text or power-up description
     * @param cardType category of the card
     * @throws IllegalArgumentException if id or prompt is null/blank, or cardType is null
     */
    protected Card(String id, String prompt, CardType cardType) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Card id cannot be null or blank.");
        }
        if (prompt == null || prompt.isBlank()) {
            throw new IllegalArgumentException("Card prompt cannot be null or blank.");
        }
        if (cardType == null) {
            throw new IllegalArgumentException("Card type cannot be null.");
        }
        this.id = id.trim();
        this.prompt = prompt.trim();
        this.cardType = cardType;
    }

    /** Returns the unique identifier of the card. O(1) */
    public String getId() {
        return id;
    }

    /** Returns the card prompt or display text. O(1) */
    public String getPrompt() {
        return prompt;
    }

    /** Returns the card category enum. O(1) */
    public CardType getCardType() {
        return cardType;
    }

    @Override
    public String toString() {
        return "[" + id + "] " + prompt;
    }
}