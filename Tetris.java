import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JPanel;

public class Tetris extends JPanel {

    private static final long serialVersionUID = -8715353373678321308L;

    private final Color[] tetraminoColors = { Color.cyan, Color.blue, Color.orange, Color.yellow, Color.green,
            Color.pink, Color.red };

    private final int nextNum = 4;
    private Point pieceOrigin;
    private int currentPiece;
    private int holdPiece;
    private boolean canHold;
    private int rotation;
    private boolean nearFix;
    private ArrayList<Integer> nextPieces = new ArrayList<Integer>();
    private boolean gameOver;

    private long score;
    private long scoreLine;
    private Color[][] well;

    public static final int boardWidth = 12;
    public static final int boardHeight = 23;
    public static final int blockSize = 35;
    public static final int blockMargin = 2;
    public static final int blockSM = blockMargin + blockSize;
    public static final int width = (boardWidth + 1) * blockSM;
    public static final int height = boardHeight * blockSM;

    /**
     * Creates a border around the well and initializes the dropping piece
     */
    public void init() {
        gameOver = false;
        score = 0;
        scoreLine = 0;
        nearFix = false;
        holdPiece = -1;
        canHold = true;
        nextPieces.clear();
        setBounds(0, 0, width + 2 + blockSM*6, height + 30);
        well = new Color[boardWidth][boardHeight];
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                if (i == 0 || i == boardWidth - 1 || j == boardHeight - 1) {
                    well[i][j] = Color.GRAY;
                } else {
                    well[i][j] = Color.BLACK;
                }
            }
        }
        newPiece();
    }

    /**
     * Put a new, random piece into the dropping position
     */
    public boolean newPiece() {
        pieceOrigin = new Point(5, 2);
        rotation = 0;
        while (nextPieces.size() <= nextNum) {
            ArrayList<Integer> news = new ArrayList<>();
            Collections.addAll(news, 0, 1, 2, 3, 4, 5, 6);
            Collections.shuffle(news);
            nextPieces.addAll(news);
        }
        currentPiece = nextPieces.remove(0);
        if (collidesAt(pieceOrigin.x, pieceOrigin.y, rotation))
            return false;
        return true;
    }

    /**
     * Collision test for the dropping piece
     * 
     * @param x
     * @param y
     * @param rotation
     * @return
     */
    private boolean collidesAt(int x, int y, int rotation) {
        for (Point p : Tetramino.Tetraminos[currentPiece][rotation]) {
            if (well[p.x + x][p.y + y] != Color.BLACK) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rotate the piece clockwise or counterclockwise
     * 
     * @return if interruption is needed
     */
    public boolean rotate(int i) {
        boolean rotated = false;
        int newRotation = (rotation + i) % 4;
        if (newRotation < 0) {
            newRotation = 3;
        }
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y, newRotation)) {
            rotation = newRotation;
            rotated = true;
        }
        repaint();
        return !setNearFix() && rotated;
    }

    /**
     * Move the piece left or right
     * 
     * @return if interruption is needed
     */
    public boolean move(int i) {
        boolean moved = false;
        if (!collidesAt(pieceOrigin.x + i, pieceOrigin.y, rotation)) {
            pieceOrigin.x += i;
            moved = true;
        }
        repaint();
        return !setNearFix() && moved;
    }

    /**
     * Drops the piece one line or fixes it to the well if it can't drop
     */
    public boolean dropDown() {
        boolean fix = false;
        if (!collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            pieceOrigin.y += 1;
        } else {
            fixToWell();
            fix = true;
        }
        setNearFix();
        repaint();
        return fix;
    }

    /**
     * check if current piece is touch fixed block or floor
     * 
     * @return !nearFix && !preNearFix means if piece has some space
     */
    private boolean setNearFix() {
        boolean preNearFix = nearFix;
        if (collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            nearFix = true;
        } else {
            nearFix = false;
        }
        return !nearFix && !preNearFix;
    }

    public void fastDrop() {
        if (!dropDown())
            score++;
    }

    public void hardDrop() {
        while (!dropDown()) {
            score++;
        }
    }

    /**
     * hold piece
     * 
     * @return if interruption is needed
     */
    public boolean hold() {
        if (canHold) {
            canHold = false;
            if (holdPiece >= 0)
                nextPieces.add(0, holdPiece);
            holdPiece = currentPiece;
            newPiece();
            repaint();
            return true;
        } else
            return false;
    }

    /**
     * Make the dropping piece part of the well, so it is available for collision
     * detection.
     */
    public void fixToWell() {
        for (Point p : Tetramino.Tetraminos[currentPiece][rotation]) {
            well[pieceOrigin.x + p.x][pieceOrigin.y + p.y] = tetraminoColors[currentPiece];
        }
        clearRows();
        nearFix = false;
        canHold = true;
        if (!newPiece()) {
            System.out.println("Gameover!Score:" + score);
            gameOver = true;
        }
    }

    public boolean isGameOver() {
        if (gameOver)
            gameOver();
        return gameOver;
    }

    public void gameOver() {
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                if (well[i][j] != Color.black && well[i][j] != Color.gray)
                    well[i][j] = Color.LIGHT_GRAY;
            }
        }
        repaint();
    }

    public void deleteRow(int row) {
        for (int j = row - 1; j > 0; j--) {
            for (int i = 1; i < boardWidth - 1; i++) {
                well[i][j + 1] = well[i][j];
            }
        }
    }

    /**
     * Clear completed rows from the field and award score according to the number
     * of simultaneously cleared rows.
     */
    public void clearRows() {
        boolean gap;
        int numClears = 0;

        for (int j = boardHeight - 2; j > 0; j--) {
            gap = false;
            for (int i = 1; i < boardWidth - 1; i++) {
                if (well[i][j] == Color.BLACK) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                deleteRow(j);
                j += 1;
                numClears += 1;
            }
        }

        switch (numClears) {
        case 1:
            score += 100;
            break;
        case 2:
            score += 300;
            break;
        case 3:
            score += 500;
            break;
        case 4:
            score += 800;
            break;
        }
        scoreLine += numClears;
    }

    public long getScoreLine() {
        return scoreLine;
    }

    public long getSpeed() {
        if (collidesAt(pieceOrigin.x, pieceOrigin.y + 1, rotation)) {
            return 1000;
        } else
            return 1000 - scoreLine / 10 * 50;
    }

    /**
     * Draw the shadow piece It needs drawing shadow before drawing current piece
     */
    private void drawShadow(Graphics g) {
        int shadow = pieceOrigin.y;
        while (!collidesAt(pieceOrigin.x, shadow + 1, rotation))
            shadow++;
        Color c = tetraminoColors[currentPiece];
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
        for (Point p : Tetramino.Tetraminos[currentPiece][rotation]) {
            g.fillRect((p.x + pieceOrigin.x) * blockSM, (p.y + shadow) * blockSM, blockSize, blockSize);
        }
    }

    /**
     * Draw the falling piece
     */
    private void drawPiece(Graphics g) {
        // draw piece
        if (gameOver)
            g.setColor(Color.LIGHT_GRAY);
        else
            g.setColor(tetraminoColors[currentPiece]);
        for (Point p : Tetramino.Tetraminos[currentPiece][rotation]) {
            g.fillRect((p.x + pieceOrigin.x) * blockSM, (p.y + pieceOrigin.y) * blockSM, blockSize, blockSize);
        }
    }

    private void drawNext(Graphics g) {
        for (int i = 0; i < nextNum; i++) {
            int piece = nextPieces.get(i);
            g.setColor(tetraminoColors[piece]);
            for (Point p : Tetramino.Tetraminos[piece][0]) {
                g.fillRect((p.x + boardWidth+1) * blockSM, (p.y + 1 + 4 * i) * blockSM, blockSize, blockSize);
            }
        }
        if (holdPiece >= 0) {
            g.setColor(tetraminoColors[holdPiece]);
            for (Point p : Tetramino.Tetraminos[holdPiece][0]) {
                g.fillRect((p.x + boardWidth+1) * blockSM, (p.y + 1 + 4 * nextNum) * blockSM, blockSize, blockSize);
            }
        }
    }

    @Override
    public void paintComponent(Graphics g) {
        // Paint the well
        g.fillRect(0, 0, width + blockSM * 4, height);
        for (int i = 0; i < boardWidth; i++) {
            for (int j = 0; j < boardHeight; j++) {
                g.setColor(well[i][j]);
                g.fillRect(blockSM * i, blockSM * j, blockSize, blockSize);
            }
        }

        // Display the score
        g.setColor(Color.WHITE);
        g.drawString("score:" + score, width/2, blockSM);
        g.drawString("line:" + scoreLine, width/2, blockSM*2);
        g.drawString("HOLD",(boardWidth+1)*blockSM,(nextNum*4+1)*blockSM);

        // Draw the currently falling piece
        drawShadow(g);
        drawPiece(g);
        drawNext(g);
    }
}
