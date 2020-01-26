
import java.awt.Color;
import java.awt.Point;
import java.util.Random;

/**
 * TetrisAI
 */

public class TetrisAI {
    /**
     * refer to ./tetraminoRotaion.jpg
     * <p>
     * 0->T,1:->S,2->Z,3->L,4->J,5->O,6->I
     * </p>
     */
    private static final Point[][][] Tetraminos = {
            // T piece
            { { new Point(0, 1), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) } },
            // S piece
            { { new Point(1, 0), new Point(2, 0), new Point(0, 1), new Point(1, 1) },
                    { new Point(1, 0), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
                    { new Point(1, 1), new Point(2, 1), new Point(0, 2), new Point(1, 2) },
                    { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(1, 2) } },
            // Z piece
            { { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(2, 1) },
                    { new Point(2, 0), new Point(1, 1), new Point(2, 1), new Point(1, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
                    { new Point(1, 0), new Point(0, 1), new Point(1, 1), new Point(0, 2) } },
            // L piece
            { { new Point(2, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(2, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(0, 2) },
                    { new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(1, 2) } },
            // J piece
            { { new Point(0, 0), new Point(0, 1), new Point(1, 1), new Point(2, 1) },
                    { new Point(1, 0), new Point(2, 0), new Point(1, 1), new Point(1, 2) },
                    { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(2, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(0, 2), new Point(1, 2) } },
            // O piece
            { { new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1) },
                    { new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1) },
                    { new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1) },
                    { new Point(0, 0), new Point(1, 0), new Point(0, 1), new Point(1, 1) } },
            // I piece
            { { new Point(0, 1), new Point(1, 1), new Point(2, 1), new Point(3, 1) },
                    { new Point(2, 0), new Point(2, 1), new Point(2, 2), new Point(2, 3) },
                    { new Point(0, 2), new Point(1, 2), new Point(2, 2), new Point(3, 2) },
                    { new Point(1, 0), new Point(1, 1), new Point(1, 2), new Point(1, 3) } } };

    private static final Color[] tetraminoColors = {
            // T piece
            Color.magenta,
            // S piece
            Color.green,
            // Z piece
            Color.red,
            // L piece
            Color.orange,
            // J piece
            Color.blue,
            // O piece
            Color.yellow,
            // I piece
            Color.cyan };

    private static final int boardWidth = 12;
    private static final int boardHeight = 23;
    private int midUnitNum = 5;
    private double[][] inputWeight;
    private double[] outputWeight;

    public TetrisAI() {
        setRandomWeight();
    }

    public TetrisAI(double[][] inputWeight,double[] outputWeight){
        this.inputWeight = inputWeight;
        this.outputWeight = outputWeight;
    }

    public void GAsearch() {
        return;
    }

    private void setRandomWeight() {
        Random rand = new Random();
        inputWeight = new double[8][midUnitNum];
        outputWeight = new double[midUnitNum];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < midUnitNum; j++) {
                inputWeight[i][j] = 1.0 - rand.nextDouble() * 2.0;
                outputWeight[j] = 1.0 - rand.nextDouble() * 2.0;
            }
        }
    }

    private double evalue(int[] values) {
        if (values == null)
            return Double.NEGATIVE_INFINITY;
        double sum = 0.0;
        for (int j = 0; j < midUnitNum; j++) {
            double unitSum = 0.0;
            for (int i = 0; i < values.length; i++) {
                unitSum += values[i] * inputWeight[i][j];
            }
            sum += unitSum * outputWeight[j];
        }
        return sum;
    }

    public TetrisCtrl ctrl(Color[][] well, int currentPiece, int nextPiece) {
        int ctrlX = 1;
        int ctrlRotation = 0;
        double maxValue = Double.NEGATIVE_INFINITY;
        for (int currentX = 1; currentX < Tetris.boardWidth; currentX++) {
            for (int currentRotation = 0; currentRotation < 4; currentRotation++) {
                Color[][] addCurrent = well.clone();
                for (int i = 0; i < addCurrent.length; i++) {
                    addCurrent[i] = well[i].clone();
                }
                if (null == addPiece(addCurrent, currentPiece, currentX, currentRotation))
                    continue;
                for (int nextX = 1; nextX < Tetris.boardWidth; nextX++) {
                    for (int nextRotation = 0; nextRotation < 4; nextRotation++) {
                        Color[][] addNext = addCurrent.clone();
                        for (int i = 0; i < addNext.length; i++) {
                            addNext[i] = addCurrent[i].clone();
                        }
                        double value = evalue(addPiece(addNext, nextPiece, nextX, nextRotation));
                        if (maxValue < value) {
                            maxValue = value;
                            ctrlX = currentX;
                            ctrlRotation = currentRotation;
                        }
                    }
                }
            }
        }
        if (maxValue != Double.NEGATIVE_INFINITY)
            return new TetrisCtrl(ctrlX, ctrlRotation);
        else
            return null;
    }

    private int[] addPiece(Color[][] well, int piece, int pieceOriginX, int rotation) {

        Values values = new Values();

        // try dropping piece
        int pieceOriginY = 2;
        if (collidesAt(well, pieceOriginX, pieceOriginY, rotation, piece))
            return null;
        while (!collidesAt(well, pieceOriginX, pieceOriginY + 1, rotation, piece))
            pieceOriginY++;

        // fix piece to well and check piece height
        int pieceHeight = 0;
        for (Point p : Tetraminos[piece][rotation]) {
            well[pieceOriginX + p.x][pieceOriginY + p.y] = tetraminoColors[piece];
            if (p.y > pieceHeight)
                pieceHeight = p.y;
        }
        values.setLandingHeight(boardHeight - pieceOriginY - pieceHeight);

        // clear no gap lines and count eroded
        int numClears = 0;
        int erodedPiece = 0;

        for (int j = pieceHeight; j >= 0; j--) {
            boolean gap = false;
            for (int i = 1; i < boardWidth - 1; i++) {
                if (well[i][pieceOriginY + j] == Color.BLACK) {
                    gap = true;
                    break;
                }
            }
            if (!gap) {
                for (Point p : Tetraminos[piece][rotation]) {
                    if (p.y == j)
                        erodedPiece++;
                }
                deleteRow(well, pieceOriginY + j);
                pieceOriginY++;
                numClears++;
            }
        }
        values.setErodedPieceCells(numClears * erodedPiece);

        // scan by row
        int rowTransitions = 0;
        int numHoles = 0;
        int rowsWithHoles = 0;
        for (int j = boardHeight - 2; j > 0; j--) {
            boolean trans = (well[1][j] == Color.BLACK);
            int hole = 0;
            for (int i = 1; i < boardWidth - 1; i++) {
                boolean next = (well[i][j] == Color.BLACK);
                if (trans != next)
                    rowTransitions++;
                trans = next;
                if (next && well[i][j - 1] != Color.BLACK && well[i][j + 1] != Color.BLACK
                        && well[i - 1][j] != Color.BLACK && well[i + 1][j] != Color.BLACK)
                    hole++;
            }
            numHoles += hole;
            if (hole != 0)
                rowsWithHoles++;
        }
        values.setRowTransitions(rowTransitions);
        values.setNumHoles(numHoles);
        values.setRowsWithHoles(rowsWithHoles);

        // scan by col
        int colTransitions = 0;
        int cumulativeWells = 0;
        int holeDepth = 0;
        for (int i = 1; i < boardWidth - 1; i++) {
            boolean trans = (well[i][1]==Color.BLACK);
            for (int j = 1; j < boardHeight - 1; j++) {
                boolean next = (well[i][j]==Color.BLACK);
                if(trans!=next)colTransitions++;
                trans=next;
            }
        }
        values.setColTransitions(colTransitions);
        values.setCumulativeWells(cumulativeWells);
        values.setHoleDepth(holeDepth);

        // return values
        return values.getValues();
    }

    private void deleteRow(Color[][] well, int row) {
        for (int j = row - 1; j > 0; j--) {
            for (int i = 1; i < boardWidth - 1; i++) {
                well[i][j + 1] = well[i][j];
            }
        }
    }

    private boolean collidesAt(Color[][] well, int x, int y, int rotation, int piece) {
        for (Point p : Tetraminos[piece][rotation]) {
            if (x < 0 || y < 0 || x + p.x >= boardWidth || y + p.y >= boardHeight
                    || well[p.x + x][p.y + y] != Color.BLACK) {
                return true;
            }
        }
        return false;
    }
}