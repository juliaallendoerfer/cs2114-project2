public class GameSession
{
    private LinkedChain<QuestionCard>[] decks;
    private int score;

    /**
     * Creates a game session with three empty decks.
     */
    @SuppressWarnings("unchecked")
    public GameSession()
    {
        decks = (LinkedChain<QuestionCard>[])new LinkedChain[3];

        for (int i = 0; i < decks.length; i++)
        {
            decks[i] = new LinkedChain<QuestionCard>();
        }

        score = 0;
    }

    /**
     * Adds a question to the selected deck.
     *
     * @param deckNumber deck number from 1 to 3
     * @param question question to add
     */
    public void addQuestion(int deckNumber, QuestionCard question)
    {
        if (deckNumber < 1 || deckNumber > 3)
        {
            throw new IllegalArgumentException("Invalid deck number.");
        }

        decks[deckNumber - 1].addToBack(question);
    }

    /**
     * Returns the first question in a deck.
     *
     * @param deckNumber deck number from 1 to 3
     * @return the first question, or null if the deck is empty
     */
    public QuestionCard getCurrentQuestion(int deckNumber)
    {
        if (deckNumber < 1 || deckNumber > 3)
        {
            throw new IllegalArgumentException("Invalid deck number.");
        }

        if (decks[deckNumber - 1].isEmpty())
        {
            return null;
        }

        return decks[deckNumber - 1].getHead();
    }

    /**
     * Submits an answer to the first question in a deck.
     *
     * @param deckNumber deck number from 1 to 3
     * @param answer the player's answer
     * @return true if the answer is correct
     */
    public boolean submitAnswer(int deckNumber, String answer)
    {
        QuestionCard question = getCurrentQuestion(deckNumber);

        if (question == null)
        {
            throw new IllegalStateException("This deck is empty.");
        }

        if (answer == null)
        {
            throw new IllegalArgumentException("Answer cannot be null.");
        }

        question.incrementAttempts();

        boolean correct = question.checkAnswer(answer);

        if (correct)
        {
            score += question.getPointValue();
            decks[deckNumber - 1].removeFirst();
        }
        else
        {
            decks[deckNumber - 1].cycleFrontToBack();
        }

        return correct;
    }

    /**
     * Returns the player's score.
     *
     * @return current score
     */
    public int getScore()
    {
        return score;
    }

    /**
     * Returns the number of questions in a deck.
     *
     * @param deckNumber deck number from 1 to 3
     * @return number of questions remaining
     */
    public int getDeckSize(int deckNumber)
    {
        if (deckNumber < 1 || deckNumber > 3)
        {
            throw new IllegalArgumentException("Invalid deck number.");
        }

        return decks[deckNumber - 1].getNumberOfEntries();
    }

    /**
     * Checks whether every deck is empty.
     *
     * @return true if all questions have been answered
     */
    public boolean isGameOver()
    {
        for (int i = 0; i < decks.length; i++)
        {
            if (!decks[i].isEmpty())
            {
                return false;
            }
        }

        return true;
    }
}