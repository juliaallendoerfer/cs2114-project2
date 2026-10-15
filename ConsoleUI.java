import java.util.Scanner;

public class ConsoleUI {
   public static void main(String[] args) {
      Scanner scanner = new Scanner(System.in);
      GameSession game = new GameSession();

      System.out.println("You have entered CardDash!");
      while (!game.isGameOver())
        {
            System.out.println("Choose a deck (1-3):");

            String input = scanner.nextLine();

            try
            {
                int deck = InputValidator.parseDeckChoice(input);

                if (game.getDeckSize(deck) == 0)
                {
                    System.out.println("That deck is empty!");
                    continue;
                }

                QuestionCard question =
                    game.getCurrentQuestion(deck);

                // Display the question here
                System.out.println(question);

                System.out.println("Enter your answer:");

                String answer =
                    InputValidator.cleanAnswer(scanner.nextLine());

                if (!InputValidator.isValidAnswer(answer))
                {
                    System.out.println("Please enter an answer.");
                    continue;
                }

                boolean correct = game.submitAnswer(deck, answer);

                if (correct)
                {
                    System.out.println("Correct!");
                }
                else
                {
                    System.out.println("Incorrect. Try again later!");
                }

                System.out.println("Score: " + game.getScore());
            }
            catch (IllegalArgumentException exception)
            {
                System.out.println(exception.getMessage());
            }
        }

        System.out.println("You completed the game!");
        scanner.close();
    }
}
