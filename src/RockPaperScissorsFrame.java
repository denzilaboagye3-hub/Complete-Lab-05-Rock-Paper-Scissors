import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Random;

public class RockPaperScissorsFrame extends JFrame {

    private JButton rockButton;
    private JButton paperButton;
    private JButton scissorsButton;
    private JButton quitButton;

    private JTextField playerWinsField;
    private JTextField computerWinsField;
    private JTextField tiesField;

    private JTextArea resultArea;

    private int playerWins = 0;
    private int computerWins = 0;
    private int ties = 0;

    private int playerRockCount = 0;
    private int playerPaperCount = 0;
    private int playerScissorsCount = 0;

    private String lastPlayerMove = "";

    private final Random random = new Random();

    private final Strategy randomStrategy = new RandomStrategy();
    private final Strategy cheatStrategy = new CheatStrategy();
    private final Strategy leastUsedStrategy = new LeastUsedStrategy();
    private final Strategy mostUsedStrategy = new MostUsedStrategy();
    private final Strategy lastUsedStrategy = new LastUsedStrategy();

    public RockPaperScissorsFrame() {

        setTitle("Rock Paper Scissors Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        createButtonPanel();
        createStatsPanel();
        createResultsPanel();
    }

    private void createButtonPanel() {

        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 10, 10));
        buttonPanel.setBorder(new TitledBorder("Choose Your Move"));
        ImageIcon rockIcon = new ImageIcon(
                new ImageIcon("rock.jpg").getImage()
                        .getScaledInstance(100, 100, Image.SCALE_SMOOTH)
        );

        ImageIcon paperIcon = new ImageIcon(
                new ImageIcon("paper.jpg").getImage()
                        .getScaledInstance(100, 100, Image.SCALE_SMOOTH)
        );

        ImageIcon scissorsIcon = new ImageIcon(
                new ImageIcon("scissors.jpg").getImage()
                        .getScaledInstance(100, 100, Image.SCALE_SMOOTH)
        );

        rockButton = new JButton("Rock", rockIcon);
        paperButton = new JButton("Paper", paperIcon);
        scissorsButton = new JButton("Scissors", scissorsIcon);
        quitButton = new JButton("Quit");

        rockButton.setActionCommand("R");
        paperButton.setActionCommand("P");
        scissorsButton.setActionCommand("S");

        ActionListener gameListener =
                e -> playRound(e.getActionCommand());

        rockButton.addActionListener(gameListener);
        paperButton.addActionListener(gameListener);
        scissorsButton.addActionListener(gameListener);

        quitButton.addActionListener(e -> System.exit(0));

        buttonPanel.add(rockButton);
        buttonPanel.add(paperButton);
        buttonPanel.add(scissorsButton);
        buttonPanel.add(quitButton);

        add(buttonPanel, BorderLayout.NORTH);
    }

    private void createStatsPanel() {

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 10, 5));
        statsPanel.setBorder(new TitledBorder("Game Statistics"));

        statsPanel.add(new JLabel("Player Wins", SwingConstants.CENTER));
        statsPanel.add(new JLabel("Computer Wins", SwingConstants.CENTER));
        statsPanel.add(new JLabel("Ties", SwingConstants.CENTER));

        playerWinsField = new JTextField("0");
        computerWinsField = new JTextField("0");
        tiesField = new JTextField("0");

        playerWinsField.setEditable(false);
        computerWinsField.setEditable(false);
        tiesField.setEditable(false);

        playerWinsField.setHorizontalAlignment(JTextField.CENTER);
        computerWinsField.setHorizontalAlignment(JTextField.CENTER);
        tiesField.setHorizontalAlignment(JTextField.CENTER);

        statsPanel.add(playerWinsField);
        statsPanel.add(computerWinsField);
        statsPanel.add(tiesField);

        add(statsPanel, BorderLayout.SOUTH);
    }

    private void createResultsPanel() {

        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 16));

        JScrollPane scrollPane = new JScrollPane(resultArea);
        scrollPane.setBorder(new TitledBorder("Game Results"));

        add(scrollPane, BorderLayout.CENTER);
    }

    private void playRound(String playerMove) {

        Strategy strategy;
        String strategyName;

        int probability = random.nextInt(100) + 1;

        if (probability <= 10) {
            strategy = cheatStrategy;
            strategyName = "Cheat";

        } else if (probability <= 30) {
            strategy = leastUsedStrategy;
            strategyName = "Least Used";

        } else if (probability <= 50) {
            strategy = mostUsedStrategy;
            strategyName = "Most Used";

        } else if (probability <= 70 && !lastPlayerMove.isEmpty()) {
            strategy = lastUsedStrategy;
            strategyName = "Last Used";

        } else {
            strategy = randomStrategy;
            strategyName = "Random";
        }

        String computerMove = strategy.getMove(playerMove);

        String result = determineWinner(
                playerMove,
                computerMove,
                strategyName
        );

        resultArea.append(result + "\n");

        updatePlayerHistory(playerMove);
        lastPlayerMove = playerMove;

        updateStats();
    }

    private String determineWinner(String playerMove,
                                   String computerMove,
                                   String strategyName) {

        String player = moveName(playerMove);
        String computer = moveName(computerMove);

        if (playerMove.equals(computerMove)) {

            ties++;

            return player + " ties " + computer +
                    ". (Tie! Computer: " + strategyName + ")";
        }

        boolean playerWon =
                (playerMove.equals("R") && computerMove.equals("S")) ||
                        (playerMove.equals("P") && computerMove.equals("R")) ||
                        (playerMove.equals("S") && computerMove.equals("P"));

        String action;

        if ((playerMove.equals("R") && computerMove.equals("S")) ||
                (computerMove.equals("R") && playerMove.equals("S"))) {

            action = "Rock breaks Scissors";

        } else if ((playerMove.equals("P") && computerMove.equals("R")) ||
                (computerMove.equals("P") && playerMove.equals("R"))) {

            action = "Paper covers Rock";

        } else {

            action = "Scissors cuts Paper";
        }

        if (playerWon) {

            playerWins++;

            return action +
                    ". (Player wins! Computer: " +
                    strategyName + ")";

        } else {

            computerWins++;

            return action +
                    ". (Computer wins! Computer: " +
                    strategyName + ")";
        }
    }

    private String moveName(String move) {

        switch (move) {

            case "R":
                return "Rock";

            case "P":
                return "Paper";

            case "S":
                return "Scissors";

            default:
                return "Unknown";
        }
    }

    private void updatePlayerHistory(String playerMove) {

        switch (playerMove) {

            case "R":
                playerRockCount++;
                break;

            case "P":
                playerPaperCount++;
                break;

            case "S":
                playerScissorsCount++;
                break;
        }
    }

    private void updateStats() {

        playerWinsField.setText(String.valueOf(playerWins));
        computerWinsField.setText(String.valueOf(computerWins));
        tiesField.setText(String.valueOf(ties));
    }

    private String moveThatBeats(String move) {

        switch (move) {

            case "R":
                return "P";

            case "P":
                return "S";

            case "S":
                return "R";

            default:
                return "R";
        }
    }

    private class LeastUsedStrategy implements Strategy {

        @Override
        public String getMove(String playerMove) {

            if (playerRockCount <= playerPaperCount &&
                    playerRockCount <= playerScissorsCount) {

                return moveThatBeats("R");

            } else if (playerPaperCount <= playerRockCount &&
                    playerPaperCount <= playerScissorsCount) {

                return moveThatBeats("P");

            } else {

                return moveThatBeats("S");
            }
        }
    }

    private class MostUsedStrategy implements Strategy {

        @Override
        public String getMove(String playerMove) {

            if (playerRockCount >= playerPaperCount &&
                    playerRockCount >= playerScissorsCount) {

                return moveThatBeats("R");

            } else if (playerPaperCount >= playerRockCount &&
                    playerPaperCount >= playerScissorsCount) {

                return moveThatBeats("P");

            } else {

                return moveThatBeats("S");
            }
        }
    }

    private class LastUsedStrategy implements Strategy {

        @Override
        public String getMove(String playerMove) {
            return lastPlayerMove;
        }
    }
}