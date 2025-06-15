import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.Random;
import javax.swing.*;

public class WhacAMole {
    int boardWidth = 600;
    int boardHeight = 650; // 50 for the text panel on top

    JFrame frame = new JFrame("Mario: Whac A Mole");
    JLabel textLabel = new JLabel();
    JLabel highScoreLabel = new JLabel();
    JPanel textPanel = new JPanel();
    JPanel boardPanel = new JPanel();

    JButton[] board = new JButton[9];
    ImageIcon moleIcon;
    ImageIcon plantIcon;
    ImageIcon plant2Icon;

    JButton currMoleTile;
    JButton currPlantTile;
    JButton currPlant2Tile;

    Random random = new Random();
    Timer setMoleTimer;
    Timer setPlantTimer;
    Timer setPlant2Timer;
    int score = 0;
    int highScore = 0;

    JButton restartButton; // Declare the restart button

    WhacAMole() {
        loadHighScore(); // Load high score from file

        frame.setSize(boardWidth, boardHeight);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        textLabel.setFont(new Font("Arial", Font.PLAIN, 50));
        textLabel.setHorizontalAlignment(JLabel.CENTER);
        textLabel.setText("Score: " + score);
        textLabel.setOpaque(true);

        highScoreLabel.setFont(new Font("Arial", Font.PLAIN, 20));
        highScoreLabel.setHorizontalAlignment(JLabel.CENTER);
        highScoreLabel.setText("High Score: " + highScore);
        highScoreLabel.setOpaque(true);

        textPanel.setLayout(new BorderLayout());
        textPanel.add(textLabel, BorderLayout.CENTER);
        textPanel.add(highScoreLabel, BorderLayout.SOUTH);
        frame.add(textPanel, BorderLayout.NORTH);

        boardPanel.setLayout(new GridLayout(3, 3));
        frame.add(boardPanel, BorderLayout.CENTER);

        // Load icons and scale
        Image moleImg = new ImageIcon(getClass().getResource("/monty_rabbit.png")).getImage();
        moleIcon = new ImageIcon(moleImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        Image plantImg = new ImageIcon(getClass().getResource("/piranha_plant.png")).getImage();
        plantIcon = new ImageIcon(plantImg.getScaledInstance(150, 150, Image.SCALE_SMOOTH));

        plant2Icon = plantIcon; // Reuse the same image for plant 2

        // Initialize board tiles
        for (int i = 0; i < 9; i++) {
            JButton tile = new JButton();
            board[i] = tile;
            boardPanel.add(tile);
            tile.setFocusable(false);

            tile.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JButton clickedButton = (JButton) e.getSource();
                    if (clickedButton == currMoleTile) {
                        score += 10;
                        textLabel.setText("Score: " + score);
                        updateHighScore();
                        setMoleTimer.restart();
                    } else if (clickedButton == currPlantTile || clickedButton == currPlant2Tile) {
                        gameOver();
                    }
                }
            });
        }

        // Restart button setup
        restartButton = new JButton("Restart Game");
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        frame.add(restartButton, BorderLayout.SOUTH);

        // Timers for mole and plants
        setMoleTimer = new Timer(800, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currMoleTile != null) {
                    currMoleTile.setIcon(null);
                    currMoleTile = null;
                }
                int num = random.nextInt(9);
                JButton tile = board[num];
                if (currPlantTile == tile || currPlant2Tile == tile) return;
                currMoleTile = tile;
                currMoleTile.setIcon(moleIcon);
            }
        });

        setPlantTimer = new Timer(800, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currPlantTile != null) {
                    currPlantTile.setIcon(null);
                    currPlantTile = null;
                }
                int num = random.nextInt(9);
                JButton tile = board[num];
                if (currMoleTile == tile) return;
                currPlantTile = tile;
                currPlantTile.setIcon(plantIcon);
            }
        });

        setPlant2Timer = new Timer(800, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (currPlant2Tile != null) {
                    currPlant2Tile.setIcon(null);
                    currPlant2Tile = null;
                }
                int num = random.nextInt(9);
                JButton tile = board[num];
                if (currMoleTile == tile) return;
                currPlant2Tile = tile;
                currPlant2Tile.setIcon(plant2Icon);
            }
        });

        setMoleTimer.start();
        setPlantTimer.start();
        setPlant2Timer.start();

        frame.setVisible(true);
    }

    public void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader("highscore.txt"))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                highScore = Integer.parseInt(line.trim());
            }
        } catch (IOException | NumberFormatException e) {
            // Handle file IO or parsing exception (file not found, empty file, invalid content)
            System.err.println("Error loading high score: " + e.getMessage());
        }
    }

    public void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("highscore.txt"))) {
            writer.write(Integer.toString(highScore));
        } catch (IOException e) {
            System.err.println("Error saving high score: " + e.getMessage());
        }
    }

    public void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            highScoreLabel.setText("High Score: " + highScore);
            saveHighScore(); // Save high score to file
        }
    }

    public void gameOver() {
        textLabel.setText("Game Over: " + score);
        setMoleTimer.stop();
        setPlantTimer.stop();
        setPlant2Timer.stop();
        for (int i = 0; i < 9; i++) {
            board[i].setEnabled(false);
        }
    }

    public void restartGame() {
        score = 0;
        textLabel.setText("Score: " + score);
        setMoleTimer.restart();
        setPlantTimer.restart();
        setPlant2Timer.restart();
        for (int i = 0; i < 9; i++) {
            board[i].setEnabled(true);
            board[i].setIcon(null);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new WhacAMole();
            }
        });
    }
}
