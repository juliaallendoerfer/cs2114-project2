import java.util.ArrayList;
import java.util.Scanner;

public class ConsoleUI {
   public static void main(String[] args) {
      Scanner scanner = new Scanner(System.in);
      GameSession game = new GameSession();
      loadSampleQuestions(game);

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

                displayQuestion(question);

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
    /**
 * Adds six sample questions to the game.
 *
 * @param game the current game session
 */
private static void loadSampleQuestions(GameSession game)
{
    // Deck 1: Multiple-choice questions

    ArrayList<String> options1 = new ArrayList<>();
    options1.add("3");
    options1.add("4");
    options1.add("5");
    options1.add("6");

    game.addQuestion(1, new MultipleChoiceCard(
        "MC1",
        "What is 2 + 2?",
        options1,
        2));


    ArrayList<String> options2 = new ArrayList<>();
    options2.add("Python");
    options2.add("Java");
    options2.add("HTML");
    options2.add("CSS");

    game.addQuestion(1, new MultipleChoiceCard(
        "MC2",
        "Which language is commonly used with the JVM?",
        options2,
        2));


    // Deck 2: True/False questions

    game.addQuestion(2, new TrueFalseCard(
        "TF1",
        "Java is an object-oriented programming language.",
        true));

    game.addQuestion(2, new TrueFalseCard(
        "TF2",
        "The Earth is flat.",
        false));


    // Deck 3: Free-response questions

    ArrayList<String> answers1 = new ArrayList<>();
    answers1.add("New York");
    answers1.add("New York City");
    answers1.add("NYC");

    game.addQuestion(3, new FRQCard(
        "FR1",
        "Where is the Statue of Liberty located?",
        answers1));


    ArrayList<String> answers2 = new ArrayList<>();
    answers2.add("extends");

    game.addQuestion(3, new FRQCard(
        "FR2",
        "What Java keyword is used to inherit from a class?",
        answers2));
    }
    /**
     * Displays a question and its answer options, if applicable.
     *
     * @param question the question to display
     */
    private static void displayQuestion(QuestionCard question)
    {
    System.out.println(question.getPrompt());

    if (question instanceof MultipleChoiceCard)
    {
        MultipleChoiceCard mc =
            (MultipleChoiceCard)question;

        for (int i = 0; i < mc.getOptions().size(); i++)
        {
            System.out.println(
                (i + 1) + ". " + mc.getOptions().get(i));
        }
    }
}
}
