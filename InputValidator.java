public class InputValidator {
    /**
     * Removes extra spaces from the beginning and end
     * of a player's answer.
     *
     * @param input the player's raw answer
     * @return the cleaned answer
     */
    public static String cleanAnswer(String input)
    {
        if (input == null)
        {
            return "";
        }

        return input.trim();
    }


    /**
     * Converts the player's input into a deck number.
     *
     * @param input the player's raw input
     * @return the selected deck number (1, 2, or 3)
     * @throws IllegalArgumentException if input is invalid
     */
    public static int parseDeckChoice(String input)
    {
        if (input == null)
        {
            throw new IllegalArgumentException(
                "Please enter a deck number.");
        }

        try
        {
            int choice = Integer.parseInt(input.trim());

            if (choice < 1 || choice > 3)
            {
                throw new IllegalArgumentException(
                    "Please choose a deck from 1 to 3.");
            }

            return choice;
        }
        catch (NumberFormatException exception)
        {
            throw new IllegalArgumentException(
                "Please enter a valid number.");
        }
    }


    /**
     * Checks whether a menu choice is within
     * the allowed range.
     *
     * @param input the player's raw input
     * @param min the smallest valid choice
     * @param max the largest valid choice
     * @return true if the choice is valid
     */
    public static boolean isValidMenuChoice(
        String input, int min, int max)
    {
        if (input == null || min > max)
        {
            return false;
        }

        try
        {
            int choice = Integer.parseInt(input.trim());

            return choice >= min && choice <= max;
        }
        catch (NumberFormatException exception)
        {
            return false;
        }
    }


    /**
     * Checks whether the player entered
     * a non-empty answer.
     *
     * @param input the player's answer
     * @return true if the answer is valid
     */
    public static boolean isValidAnswer(String input)
    {
        return input != null && !input.trim().isEmpty();
    }
}
