import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;

public class TetrisSimple {
    private final int nextNum = 4;
    private Tetramino current;
    private ArrayList<Tetramino> nextPieces = new ArrayList<>();
    private boolean gameOver;

    private Color[][] well;
    private TetrisAI ai;
    private long lines;
    private long score;

    public static final int boardWidth = 12;
    public static final int boardHeight = 23;

    /**
     * Creates a border around the well and initializes the dropping piece and
     * paramater
     */
    public void init() {
        gameOver = false;
        lines = 0;
        score = 0;
        nextPieces.clear();
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
            Collections.addAll(news, 0, 1, 1, 1, 2, 2, 2, 3, 4, 5, 6);
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
        while (nextPieces.size() <= nextNum) {
            ArrayList<Integer> news = new ArrayList<>();
            Collections.addAll(news, 0, 1, 1, 1, 2, 2, 2, 3, 4, 5, 6);
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
        return fix;
    }

    public void hardDrop() {
        while (!dropDown()) {
            // score += 2;
        }
    }

    /**
     * Make the dropping piece part of the well, so it is available for collision
     * detection.
     */
    public void fixToWell() {
        int pieceHeight = 0;
        for (Point p : current.getPoints()) {
            well[current.getOriginX() + p.x][current.getOriginY() + p.y] = current.getColor();
            if (p.y > pieceHeight)
                pieceHeight = p.y;
        }
        clearRows(pieceHeight);
        if (!newPiece() || lines >= 100) {
            gameOver = true;
        }
    }

    public boolean isGameOver() {
        return gameOver;
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
    private void clearRows(int pieceHeight) {
        boolean gap;
        int numClears = 0;

        for (int j = pieceHeight; j >= 0; j--) {
            gap = false;
            for (int i = 1; i < boardWidth - 1; i++) {
                if (well[i][current.getOriginY() + j] == Color.BLACK) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                deleteRow(current.getOriginY() + j);
                j += 1;
                numClears += 1;
            }
        }
        lines += numClears;
        score += numClears + numClears;
    }

    public void initAI(TetrisAI ai) {
        this.ai = ai;
        init();
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
        return lines;
    }

    public long getScore() {
        return score;
    }
}
