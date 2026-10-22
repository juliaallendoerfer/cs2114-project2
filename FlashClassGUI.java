
import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class FlashClassGUI extends JFrame
{
    private GameSession game;
    private CardLayout cardLayout;
    private JPanel container;

    private int currentDeck;
    private QuestionCard currentQuestion;

    private static final Color BACKGROUND =
        new Color(23, 33, 58);
    private static final Color PURPLE =
        new Color(139, 92, 246);


    /**
     * Creates the FlashClass window.
     *
     * @param session the game session to display
     */
    public FlashClassGUI(GameSession session)
    {
        game = session;

        setTitle("FlashClass");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        cardLayout = new CardLayout();
        container = new JPanel(cardLayout);

        container.add(createWelcomeScreen(), "welcome");
        container.add(createDeckScreen(), "decks");
        container.add(new JPanel(), "question");
        container.add(new JPanel(), "feedback");

        add(container);
        cardLayout.show(container, "welcome");
    }


    /**
     * Creates the welcome screen.
     *
     * @return the welcome panel
     */
    private JPanel createWelcomeScreen()
    {
        JPanel panel = createPanel();

        JLabel title = createLabel("FLASHCLASS", 48);
        JLabel subtitle =
            createLabel("Learn. Play. Master.", 20);

        JButton start = createButton("START GAME");
        start.addActionListener(e -> showDeckScreen());

        panel.add(title);
        panel.add(Box.createVerticalStrut(15));
        panel.add(subtitle);
        panel.add(Box.createVerticalStrut(40));
        panel.add(start);

        return panel;
    }


    /**
     * Creates the deck selection screen.
     *
     * @return the deck selection panel
     */
    private JPanel createDeckScreen()
    {
        JPanel panel = createPanel();

        panel.add(createLabel("Choose Your Deck", 32));
        panel.add(Box.createVerticalStrut(15));

        panel.add(createLabel(
            "Score: " + game.getScore(), 20));

        panel.add(Box.createVerticalStrut(30));

        String[] names = {
            "Multiple Choice",
            "True / False",
            "Free Response"
        };

        for (int i = 0; i < 3; i++)
        {
            final int deck = i + 1;

            JButton button = createButton(
                names[i] + " (" + game.getDeckSize(deck)
                    + " remaining)");

            button.setEnabled(game.getDeckSize(deck) > 0);

            button.addActionListener(e -> showQuestion(deck));

            panel.add(button);
            panel.add(Box.createVerticalStrut(15));
        }

        return panel;
    }


    /**
     * Displays the deck selection screen.
     */
    private void showDeckScreen()
    {
        if (game.isGameOver())
        {
            showCompletionScreen();
            return;
        }

        container.add(createDeckScreen(), "decks");
        cardLayout.show(container, "decks");
    }


    /**
     * Displays the current question from a deck.
     *
     * @param deck the selected deck number
     */
    private void showQuestion(int deck)
    {
        currentDeck = deck;
        currentQuestion = game.getCurrentQuestion(deck);

        JPanel panel = createPanel();

        panel.add(createLabel(
            currentQuestion.getDifficulty() + " Question",
            18));

        panel.add(Box.createVerticalStrut(20));

        panel.add(createLabel(
            currentQuestion.getPrompt(), 24));

        panel.add(Box.createVerticalStrut(30));

        if (currentQuestion instanceof MultipleChoiceCard)
        {
            MultipleChoiceCard mc =
                (MultipleChoiceCard)currentQuestion;

            ArrayList<String> options = mc.getOptions();

            for (int i = 0; i < options.size(); i++)
            {
                final String answer =
                    String.valueOf(i + 1);

                JButton button = createButton(
                    (i + 1) + ". " + options.get(i));

                button.addActionListener(
                    e -> submitAnswer(answer));

                panel.add(button);
                panel.add(Box.createVerticalStrut(10));
            }
        }
        else if (currentQuestion instanceof TrueFalseCard)
        {
            JButton trueButton = createButton("TRUE");
            JButton falseButton = createButton("FALSE");

            trueButton.addActionListener(
                e -> submitAnswer("true"));

            falseButton.addActionListener(
                e -> submitAnswer("false"));

            panel.add(trueButton);
            panel.add(Box.createVerticalStrut(10));
            panel.add(falseButton);
        }
        else
        {
            JTextField answerField = new JTextField(20);
            answerField.setMaximumSize(
                new Dimension(400, 40));

            JButton submit = createButton("SUBMIT");

            submit.addActionListener(e ->
                submitAnswer(answerField.getText()));

            answerField.addActionListener(e ->
                submitAnswer(answerField.getText()));

            panel.add(answerField);
            panel.add(Box.createVerticalStrut(15));
            panel.add(submit);
        }

        container.add(panel, "question");
        cardLayout.show(container, "question");
    }


    /**
     * Submits the player's answer and displays feedback.
     *
     * @param rawAnswer the answer entered by the player
     */
    private void submitAnswer(String rawAnswer)
    {
        String answer =
            InputValidator.cleanAnswer(rawAnswer);

        if (!InputValidator.isValidAnswer(answer))
        {
            JOptionPane.showMessageDialog(
                this, "Please enter an answer.");
            return;
        }

        boolean correct =
            game.submitAnswer(currentDeck, answer);

        showFeedback(correct);
    }


    /**
     * Displays correct or incorrect feedback.
     *
     * @param correct whether the answer was correct
     */
    private void showFeedback(boolean correct)
    {
        JPanel panel = createPanel();

        if (correct)
        {
            JLabel result = createLabel("CORRECT!", 40);
            result.setForeground(new Color(74, 222, 128));
            panel.add(result);
        }
        else
        {
            JLabel result = createLabel("INCORRECT!", 40);
            result.setForeground(new Color(248, 113, 113));
            panel.add(result);
        }

        panel.add(Box.createVerticalStrut(20));
        panel.add(createLabel(
            "Score: " + game.getScore(), 24));

        panel.add(Box.createVerticalStrut(30));

        JButton continueButton = createButton("CONTINUE");
        continueButton.addActionListener(
            e -> showDeckScreen());

        panel.add(continueButton);

        container.add(panel, "feedback");
        cardLayout.show(container, "feedback");
    }


    /**
     * Displays the final completion screen.
     */
    private void showCompletionScreen()
    {
        JPanel panel = createPanel();

        panel.add(createLabel("SESSION COMPLETE!", 36));
        panel.add(Box.createVerticalStrut(20));
        panel.add(createLabel(
            "Final Score: " + game.getScore(), 26));

        container.add(panel, "complete");
        cardLayout.show(container, "complete");
    }


    /**
     * Creates a vertically arranged panel.
     *
     * @return the configured panel
     */
    private JPanel createPanel()
    {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(
            panel, BoxLayout.Y_AXIS));

        panel.setBackground(BACKGROUND);
        panel.setBorder(
            BorderFactory.createEmptyBorder(50, 80, 50, 80));

        return panel;
    }


    /**
     * Creates a styled text label.
     *
     * @param text the label text
     * @param size the font size
     * @return the configured label
     */
    private JLabel createLabel(String text, int size)
    {
        JLabel label = new JLabel(text);
        label.setFont(new Font(
            "SansSerif", Font.BOLD, size));
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        return label;
    }


    /**
     * Creates a styled button.
     *
     * @param text the button text
     * @return the configured button
     */
    private JButton createButton(String text)
    {
        JButton button = new JButton(text);

        button.setFont(new Font(
            "SansSerif", Font.BOLD, 16));

        button.setBackground(PURPLE);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(400, 50));

        return button;
    }


    /**
     * Starts the graphical application.
     *
     * @param args optional CSV file path
     */
    public static void main(String[] args)
    {
        String fileName =
            args.length > 0 ? args[0] : "deck.csv";

        GameSession session = new GameSession();

        if (!ConsoleUI.loadDeck(session, fileName))
        {
            return;
        }

        SwingUtilities.invokeLater(() ->
        {
            FlashClassGUI app = new FlashClassGUI(session);
            app.setVisible(true);
        });
    }
}
