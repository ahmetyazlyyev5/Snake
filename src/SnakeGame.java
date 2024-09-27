import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    int boardWidth, boardHeight;
    int tileSize = 25;
    Tile snakeHead;
    Tile food;
    Random random;
    Timer gameLoop;
    int velocityX;
    int velocityY;
    boolean showGridLines = false;
    ArrayList<Tile> snakeBody;
    boolean gameOver = false;

    SnakeGame(int boardWidth, int boardHeight) {
        this.boardWidth = boardWidth;
        this.boardHeight = boardHeight;
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        snakeHead = new Tile(5, 5);
        snakeBody = new ArrayList<Tile>();

        food = new Tile(10, 10);
        random = new Random();
        placeFood();

        velocityX = 0;
        velocityY = 0;

        gameLoop = new Timer(100, this);
        gameLoop.start();
    }

    public void placeFood() {
        food.x = random.nextInt(boardWidth / tileSize); // 600/25 = 24. (0 to 24)
        food.y = random.nextInt(boardHeight / tileSize);
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return (tile1.x == tile2.x) && (tile1.y == tile2.y);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {

        // gridlines for visualization
        if (showGridLines) {
            for (int i = 0; i < boardHeight / tileSize; i++) {
                g.drawLine(i * tileSize, 0, i * tileSize, boardHeight);
                g.drawLine(0, i * tileSize, boardWidth, i * tileSize);
            }
        }

        // Food
        g.setColor(Color.RED);
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);
        // g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);

        // Snake Head
        g.setColor(Color.GREEN);
        g.fill3DRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize, true);
        // g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize,
        // tileSize);

        // Snake Body
        for (Tile bodyPart : snakeBody) {
            g.fill3DRect(bodyPart.x * tileSize, bodyPart.y * tileSize, tileSize, tileSize, true);
            // g.fillRect(bodyPart.x * tileSize, bodyPart.y * tileSize, tileSize, tileSize);
        }

        // Score
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        if (gameOver) {
            g.setColor(Color.RED);
            g.drawString("Game Over: " + snakeBody.size(), 10, 25);
        } else
            g.drawString(String.valueOf(snakeBody.size()), 10, 25);

    }

    public void move() {
        if (collision(snakeHead, food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakePart = snakeBody.get(i);
            if (i == 0) {
                snakePart.x = snakeHead.x;
                snakePart.y = snakeHead.y;
            } else {
                Tile previousSnakePart = snakeBody.get(i - 1);
                snakePart.x = previousSnakePart.x;
                snakePart.y = previousSnakePart.y;
            }
        }

        snakeHead.x += velocityX;
        snakeHead.y += velocityY;

        for (Tile bodyPart : snakeBody) {
            if (collision(snakeHead, bodyPart)) {
                gameOver = true;
                break;
            }
        }

        if (snakeHead.x * tileSize < 0 || snakeHead.x * tileSize >= boardWidth
                || snakeHead.y * tileSize < 0 || snakeHead.y * tileSize >= boardHeight)
            gameOver = true;
    }

    private class Tile {
        int x, y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    // this method runs every 100 ms
    // move objects first, then redraw
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityY = -1;
            velocityX = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityY = 1;
            velocityX = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityY = 0;
            velocityX = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityY = 0;
            velocityX = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_B) {
            showGridLines = !showGridLines;
        } else if (e.getKeyCode() == KeyEvent.VK_SPACE && gameOver) {
            gameOver = false;
            snakeBody.clear();
            placeFood();
            snakeHead.x = 5;
            snakeHead.y = 5;
            velocityX = 0;
            velocityY = 0;
            gameLoop.start();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
