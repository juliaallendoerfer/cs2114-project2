import java.util.ArrayList;

/**
 * Tracks a player's progress through a FlashClass study session: the
 * running score, the current streak of consecutive correct answers, and
 * the inventory of power-up cards that have been collected but not used.
 */
public class Player {

    private int score;                              // running total score
    private int streak;                             // consecutive correct answers
    private int bestStreak;                         // longest streak this session
    private final ArrayList<PowerUpCard> inventory; // unused power-up cards

    /**
     * Constructs a player with no score, no streak, and an empty inventory.
     */
    public Player() {
        this.score = 0;
        this.streak = 0;
        this.bestStreak = 0;
        this.inventory = new ArrayList<PowerUpCard>();
    }

    /** Returns the player's current total score. O(1) */
    public int getScore() {
        return score;
    }

    /**
     * Increments the player's total score by the awarded amount. O(1)
     *
     * @param points the number of points to add
     * @throws IllegalArgumentException if points is negative
     */
    public void addScore(int points) {
        if (points < 0) {
            throw new IllegalArgumentException(
                "Points to add cannot be negative.");
        }
        score += points;
    }

    /** Returns the current consecutive correct streak. O(1) */
    public int getStreak() {
        return streak;
    }

    /** Returns the longest streak reached during the session. O(1) */
    public int getBestStreak() {
        return bestStreak;
    }

    /** Increments the consecutive streak by one. O(1) */
    public void incrementStreak() {
        streak++;
        if (streak > bestStreak) {
            bestStreak = streak;
        }
    }

    /** Resets the consecutive streak to zero after an incorrect answer. O(1) */
    public void resetStreak() {
        streak = 0;
    }

    /**
     * Appends a discovered power-up card to the player's inventory. O(1)
     *
     * @param card the power-up card to store
     * @throws IllegalArgumentException if card is null
     */
    public void addPowerUp(PowerUpCard card) {
        if (card == null) {
            throw new IllegalArgumentException("Power-up card cannot be null.");
        }
        inventory.add(card);
    }

    /**
     * Checks whether a power-up of the given type is in the inventory.
     * O(k) where k is the inventory size.
     *
     * @param type the power-up effect to look for
     * @return true if at least one matching power-up is held
     */
    public boolean hasPowerUp(PowerUpCardType type) {
        return indexOf(type) != -1;
    }

    /**
     * Removes and returns one power-up of the given type from the
     * inventory. O(k) where k is the inventory size.
     *
     * @param type the power-up effect to use
     * @return the removed power-up card, or null if none is held
     */
    public PowerUpCard usePowerUp(PowerUpCardType type) {
        int index = indexOf(type);
        if (index == -1) {
            return null;
        }
        return inventory.remove(index);
    }

    /** Returns the current list of unused power-up cards. O(1) */
    public ArrayList<PowerUpCard> getInventory() {
        return inventory;
    }

    /**
     * Returns the index of the first power-up with the given effect, or -1.
     */
    private int indexOf(PowerUpCardType type) {
        if (type == null) {
            return -1;
        }
        for (int i = 0; i < inventory.size(); i++) {
            if (inventory.get(i).getEffect() == type) {
                return i;
            }
        }
        return -1;
    }
}
