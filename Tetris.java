import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.JPanel;

public class Tetris extends JPanel {

    private static final long serialVersionUID = -8715353373678321308L;

    private final int nextNum = 4;
    private Tetramino current;
    private Tetramino hold;
    private ArrayList<Tetramino> nextPieces = new ArrayList<>();
    private boolean canHold;
    private boolean nearFix;
    private boolean tspin;
    private boolean gameOver;

    private long score;
    private long scoreLine;
    private long level;
    private String msg;
    private long ren;
    private boolean BtoB;
    private Color[][] well;
    /**
     * 0->tspin,1->single,2->double,3->triple,4->tetris
     */
    private long[] counts;

    public static final int boardWidth = 12;
    public static final int boardHeight = 23;
    public static final int blockSize = 35;
    public static final int blockMargin = 2;
    public static final int blockSM = blockMargin + blockSize;
    public static final int width = (boardWidth + 1) * blockSM;
    public static final int height = boardHeight * blockSM;

    /**
     * Creates a border around the well and initializes the dropping piece and
     * paramater
     */
    public void init() {
        gameOver = false;
        score = 0;
        scoreLine = 0;
        ren = 0;
        level = 1;
        BtoB = false;
        msg = "START!";
        counts = new long[5];
        nearFix = false;
        hold = null;
        canHold = true;
        tspin = false;
        nextPieces.clear();
        setBounds(0, 0, width + 2 + blockSM * 6, height + 30);
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
        while (nextPieces.size() <= nextNum) {
            ArrayList<Integer> news = new ArrayList<>();
            Collections.addAll(news, 0, 1, 2, 3, 4, 5, 6);
            Collections.shuffle(news);
            for (Integer integer : news) {
                nextPieces.add(new Tetramino(integer));
            }
        }
        current = nextPieces.remove(0);
        current.setOrigin(new Point(5, 2));
        if (collidesAt(0, 0, current.getRotation()))
            return false;
        return true;
    }

    /**
     * Collision test for the dropping piece
     * 
     * @param moveX
     * @param moveY
     * @param rotation
     * @return
     */
    private boolean collidesAt(int moveX, int moveY, int rotation) {
        for (Point p : current.getPoints(rotation)) {
            int x = p.x + current.getOriginX() + moveX;
            int y = p.y + current.getOriginY() + moveY;
            if (x < 0 || y < 0 || x >= boardWidth || y >= boardHeight || well[x][y] != Color.BLACK) {
                return true;
            }
        }
        return false;
    }

    /**
     * Collision test for super rotation system
     * 
     * @param movePoint
     * @param rotation
     * @return
     */
    private boolean collidesAt(Point movePoint, int rotation) {
        return collidesAt(movePoint.x, movePoint.y, rotation);
    }

    /**
     * Rotate the piece clockwise or counterclockwise +1 means clockwise and -1
     * means counterclockwise
     * 
     * @return if interruption is needed
     */
    public boolean rotate(int i) {
        boolean rotated = false;
        int newRotation = (current.getRotation() + i) % 4;
        if (newRotation < 0) {
            newRotation = 3;
        }
        for (int n = 0; n < Tetramino.SRSnum; n++) {
            Point srs = current.getSRS(i, n);
            if (!collidesAt(srs, newRotation)) {
                current.setRotation(newRotation);
                current.moveOrigin(srs.x, srs.y);
                rotated = true;
                if (current.isT())
                    checkTspin();
                break;
            }
        }
        repaint();
        return !setNearFix() && rotated;
    }

    private boolean checkTspin() {
        int count = 0;
        for (int i = 0; i < 4; i++) {
            if (well[current.getOriginX() + (i / 2) * 2][current.getOriginY() + (i % 2) * 2] != Color.black)
                count++;
        }
        return tspin = count >= 3;
    }

    /**
     * Move the piece left or right
     * 
     * @return if interruption is needed
     */
    public boolean move(int i) {
        boolean moved = false;
        if (!collidesAt(i, 0, current.getRotation())) {
            current.moveOrigin(i, 0);
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
        if (!collidesAt(0, 1, current.getRotation())) {
            current.moveOrigin(0, 1);
        } else {
            fixToWell();
            fix = true;
        }
        tspin = false;
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
        if (collidesAt(0, 1, current.getRotation())) {
            nearFix = true;
        } else {
            nearFix = false;
        }
        return !nearFix && !preNearFix;
    }

    /**
     * interrupt and drop if nearFix is false
     * 
     * @return if interruption is needed
     */
    public boolean fastDrop() {
        if (!nearFix)
            if (!dropDown())
                score++;
        return !nearFix;
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
            if (hold != null)
                nextPieces.add(0, hold);
            current.setRotation(0);
            hold = current;
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
        for (Point p : current.getPoints()) {
            well[current.getOriginX() + p.x][current.getOriginY() + p.y] = current.getColor();
        }
        clearRows();
        nearFix = false;
        canHold = true;
        if (!newPiece() || scoreLine >= 20) {
            msg = "Gameover!Score:" + score;
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
    private void clearRows() {
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
        addScore(numClears);
    }

    private void addScore(int numClears) {
        msg = "";
        boolean preBtoB = BtoB;
        if (tspin) {
            counts[0]++;
            msg = "T-Spin ";
        }
        if (numClears > 0) {
            BtoB = false;
            counts[numClears]++;
            ren++;
        } else
            ren = 0;

        switch (numClears) {
        case 0:
            if (tspin)
                score += 100 * level;
            break;
        case 1:
            score += 100 * level;
            if (tspin)
                score += 100 * level;
            BtoB = tspin;
            msg += "single";
            break;
        case 2:
            score += 200 * level;
            if (tspin)
                score += 200 * level;
            BtoB = tspin;
            msg += "double";
            break;
        case 3:
            score += 400 * level;
            if (tspin)
                score += 400 * level;
            BtoB = tspin;
            msg += "triple";
            break;
        case 4:
            score += 800 * level;
            BtoB = true;
            msg += "TETRIS!";
            break;
        }
        if (scoreLine % 10 + numClears >= 10)
            level++;
        scoreLine += numClears;
        if (ren > 1)
            msg += " ren:" + ren;
        if (BtoB && preBtoB && numClears>0)
            msg += " Back to Back!";
    }

    public long getSpeed() {
        if (collidesAt(0, 1, current.getRotation())) {
            return 1000;
        } else {
            // return 1000 - scoreLine / 10 * 50;
            long speed = 1000;
            for (int i = 0; i < level; i++) {
                speed = speed * 3 / 4;
            }
            return speed;
        }
    }

    /**
     * Draw the shadow piece It needs drawing shadow before drawing current piece
     */
    private void drawShadow(Graphics g) {
        int shadow = 0;
        while (!collidesAt(0, shadow + 1, current.getRotation()))
            shadow++;
        Color c = current.getColor();
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), 100));
        for (Point p : current.getPoints()) {
            int x = p.x + current.getOriginX();
            int y = p.y + current.getOriginY() + shadow;
            g.fillRect(x * blockSM, y * blockSM, blockSize, blockSize);
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
            g.setColor(current.getColor());
        for (Point p : current.getPoints()) {
            g.fillRect((p.x + current.getOriginX()) * blockSM, (p.y + current.getOriginY()) * blockSM, blockSize,
                    blockSize);
        }
    }

    private void drawNext(Graphics g) {
        for (int i = 0; i < nextNum; i++) {
            Tetramino piece = nextPieces.get(i);
            g.setColor(piece.getColor());
            for (Point p : piece.getPoints(0)) {
                g.fillRect((p.x + boardWidth + 1) * blockSM, (p.y + 1 + 4 * i) * blockSM, blockSize, blockSize);
            }
        }
        if (hold != null) {
            g.setColor(hold.getColor());
            for (Point p : hold.getPoints(0)) {
                g.fillRect((p.x + boardWidth + 1) * blockSM, (p.y + 1 + 4 * nextNum) * blockSM, blockSize, blockSize);
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
        g.drawString(msg, blockMargin, blockSM);
        g.drawString("score:" + score, width / 2, blockSM);
        g.drawString("line:" + scoreLine + " level:" + level, width / 2, blockSM * 2);
        g.drawString("HOLD", (boardWidth + 1) * blockSM, (nextNum * 4 + 1) * blockSM);
        g.drawString("NEXT", (boardWidth + 1) * blockSM, blockSM);

        // Draw the currently falling piece
        if (!gameOver)
            drawShadow(g);
        drawPiece(g);
        drawNext(g);
    }
}
