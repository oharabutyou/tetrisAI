import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import javax.swing.JPanel;

public class Tetris extends JPanel {

    private static final long serialVersionUID = -8715353373678321308L;

    private final int nextNum = 4;
    private Tetramino current;
    private Tetramino hold;
    private ArrayList<Tetramino> nextPieces = new ArrayList<>();
    private boolean canHold;
    private boolean nearFix;
    private boolean gameOver;
    private Tetris TetrisVS;

    private TetrisScore score;
    private Color[][] well;
    private TetrisAI ai;

    public static final int boardWidth = 12;
    public static final int boardHeight = 23;
    public static final int blockSize = 25;
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
        nearFix = false;
        hold = null;
        canHold = true;
        score = new TetrisScore(1);
        nextPieces.clear();
        setBounds(0, 0, width + 2 + blockSM * 6, height + 30);
        setPreferredSize(new Dimension(width + 2 + blockSM * 6, height + 30));
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

    public Tetramino getNextPiece() {
        while (nextPieces.size() <= nextNum + 1) {
            ArrayList<Integer> news = new ArrayList<>();
            Collections.addAll(news, 0, 1, 2, 3, 4, 5, 6);
            Collections.shuffle(news);
            for (Integer integer : news) {
                nextPieces.add(new Tetramino(integer));
            }
        }
        return nextPieces.get(0);
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
        return score.setTspin(count >= 3);
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
        score.setTspin(false);
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
                score.increment(1);
        return !nearFix;
    }

    public void hardDrop() {
        while (!dropDown()) {
            score.increment(2);
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
        push(score.getDamage());
        nearFix = false;
        canHold = true;
        if (!newPiece() || score.getLines() >= 1000) {
            score.gameOver();
            System.out.println("Gameover!\n" + score.getResult());
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

    private void push(int damage) {
        if (damage > 0) {
            int hole = (new Random()).nextInt(boardWidth - 3) + 1;
            for (int j = damage; j < boardHeight - 1; j++) {
                for (int i = 1; i < boardWidth - 1; i++) {
                    well[i][j - damage] = well[i][j];
                }
            }
            for (int j = damage; j >0; j--) {
                for(int i=1;i<boardWidth-1;i++){
                    if(i==hole)well[i][boardHeight-1-j]=Color.black;
                    else well[i][boardHeight-1-j]=Color.lightGray;
                }
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
        int power = score.addScore(numClears);
        if (TetrisVS != null) {
            TetrisVS.score().attack(power);
            score.recover(power);
        }
    }

    public long getSpeed() {
        if (collidesAt(0, 1, current.getRotation())) {
            return 1000;
        } else {
            // return 1000 - scoreLine / 10 * 50;
            long speed = 1000;
            for (int i = 0; i < score.getLevel(); i++) {
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
            getNextPiece();
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
        g.drawString(score.getMsg(), blockMargin, blockSM);
        g.drawString("score:" + score.getScore(), width / 2, blockSM);
        g.drawString("line:" + score.getLines() + " level:" + score.getLevel(), width / 2, blockSM * 2);
        g.drawString("HOLD", (boardWidth + 1) * blockSM, (nextNum * 4 + 1) * blockSM);
        g.drawString("NEXT", (boardWidth + 1) * blockSM, blockSM);

        // Draw the currently falling piece
        if (!gameOver)
            drawShadow(g);
        drawPiece(g);
        drawNext(g);
    }

    public void initAI(TetrisAI ai) {
        this.ai = ai;
        init();
    }

    public void setVS(Tetris TetrisVS) {
        this.TetrisVS = TetrisVS;
    }

    public void AIPlay() {
        Color[][] cpy = well.clone();
        for (int i = 0; i < cpy.length; i++) {
            cpy[i] = well[i].clone();
        }
        TetrisCtrl ctrl = ai.ctrl(cpy, current.getPiece(), getNextPiece().getPiece());
        if (ctrl != null)
            AICtrl(ctrl);
        else
            dropDown();
    }

    public void AICtrl(TetrisCtrl ctrl) {
        current.setRotation(ctrl.getRotation());
        current.setOrigin(new Point(ctrl.getPieceOriginX(), current.getOriginY()));
        hardDrop();
    }

    public long getLines() {
        return score.getLines();
    }

    public TetrisScore score() {
        return score;
    }
}
