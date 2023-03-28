import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class SnakeGame extends JPanel implements Runnable, KeyListener {

    private static final long serialVersionUID = 1L;

    // Game parameters
    public static final int WIDTH = 500;
    public static final int HEIGHT = 500;
    public static final int SCALE = 10;
    public static final int DELAY = 75; // milliseconds

    // Snake parameters
    private ArrayList<Point> snake = new ArrayList<Point>();
    private int direction = KeyEvent.VK_RIGHT; // initial direction
    private int score = 0;

    // Food parameters
    private Point food;

    // Game state variables
    private boolean running = false;
    private Thread thread;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        startGame();
    }

    private void startGame() {
        // Initialize the snake
        snake.add(new Point(10, 10));
        snake.add(new Point(10, 11));
        snake.add(new Point(10, 12));

        // Place the initial food
        placeFood();

        // Start the game loop
        running = true;
        thread = new Thread(this);
        thread.start();
    }

    private void placeFood() {
        Random rand = new Random();
        int x = rand.nextInt(WIDTH / SCALE);
        int y = rand.nextInt(HEIGHT / SCALE);
        food = new Point(x, y);
    }

    private void moveSnake() {
        // Get the head and tail of the snake
        Point head = snake.get(0);
        Point tail = snake.get(snake.size() - 1);

        // Move the head in the current direction
        if (direction == KeyEvent.VK_LEFT) {
            head = new Point(head.x - 1, head.y);
        } else if (direction == KeyEvent.VK_RIGHT) {
            head = new Point(head.x + 1, head.y);
        } else if (direction == KeyEvent.VK_UP) {
            head = new Point(head.x, head.y - 1);
        } else if (direction == KeyEvent.VK_DOWN) {
            head = new Point(head.x, head.y + 1);
        }

        // Add the new head to the front of the snake
        snake.add(0, head);

        // Remove the tail from the back of the snake
        snake.remove(tail);
    }

    private boolean checkCollision() {
        // Check if the head of the snake has collided with the wall
        Point head = snake.get(0);
        if (head.x < 0 || head.x >= WIDTH / SCALE || head.y < 0 || head.y >= HEIGHT / SCALE) {
            return true;
        }

        // Check if the head of the snake has collided with the body
        for (int i = 1; i < snake.size(); i++) {
            Point body = snake.get(i);
            if (head.equals(body)) {
                return true;
            }
        }

        return false;
    }

    private boolean checkFood() {
        // Check if the head of the snake has collided with the food
        Point head = snake.get(0);
        if (head.equals(food)) {
            // Add a new segment to the snake
            Point tail = snake.get(snake.size() - 1);
            snake.add(tail);
            // Place a new food
            placeFood();

            // Increase the score
            score += 10;

            return true;
        }

        return false;
    }

    private void gameOver() {
        running = false;

        // Display the game over message
        int choice = JOptionPane.showOptionDialog(null, "Game Over! Score: " + score + "\nDo you want to restart the game?",
                "Game Over", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, null, null);
        if (choice == JOptionPane.YES_OPTION) {
            resetGame();
        } else {
            System.exit(0);
        }
    }

    private void resetGame() {
        // Reset the game state
        snake.clear();
        snake.add(new Point(10, 10));
        snake.add(new Point(10, 11));
        snake.add(new Point(10, 12));
        direction = KeyEvent.VK_RIGHT;
        score = 0;
        placeFood();
        running = true;

        // Restart the game loop
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        while (running) {
            try {
                // Move the snake
                moveSnake();

                // Check for collisions
                if (checkCollision()) {
                    gameOver();
                }

                // Check for food
                if (checkFood()) {
                    // Pause the game briefly to allow the player to see the new food
                    Thread.sleep(250);
                }

                // Repaint the screen
                repaint();

                // Pause the game
                Thread.sleep(DELAY);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the snake
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * SCALE, p.y * SCALE, SCALE, SCALE);
        }

        // Draw the food
        g.setColor(Color.RED);
        g.fillRect(food.x * SCALE, food.y * SCALE, SCALE, SCALE);

        // Draw the score
        g.setColor(Color.WHITE);
        g.drawString("Score: " + score, 10, 20);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        // Update the direction of the snake based on the key pressed
        if (key == KeyEvent.VK_LEFT && direction != KeyEvent.VK_RIGHT) {
            direction = key;
        } else if (key == KeyEvent.VK_RIGHT && direction != KeyEvent.VK_LEFT) {
            direction = key;
        } else if (key == KeyEvent.VK_UP && direction != KeyEvent.VK_DOWN) {
            direction = key;
        } else if (key == KeyEvent.VK_DOWN && direction != KeyEvent.VK_UP) {
            direction = key;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Do nothing
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Do nothing
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.add(new SnakeGame());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}