
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

    public TetrisAI(double[][] inputWeight, double[] outputWeight) {
        this.inputWeight = inputWeight;
        this.outputWeight = outputWeight;
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

    private double evalue(Values values) {
        if (values == null)
            return Double.NEGATIVE_INFINITY;
        int[] vals = values.getValues();
        double sum = 0.0;
        for (int j = 0; j < midUnitNum; j++) {
            double unitSum = 0.0;
            for (int i = 0; i < vals.length; i++) {
                unitSum += vals[i] * inputWeight[i][j];
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
                double value = evalue(addPiece(addCurrent, currentPiece, currentX, currentRotation));
                if (maxValue < value) {
                    maxValue = value;
                    ctrlX = currentX;
                    ctrlRotation = currentRotation;
                }
            }
        }
        if (maxValue != Double.NEGATIVE_INFINITY)
            return new TetrisCtrl(ctrlX, ctrlRotation);
        else
            return null;
    }

    private Values addPiece(Color[][] well, int piece, int pieceOriginX, int rotation) {

        Values values = new Values();

        // try dropping piece
        int pieceOriginY = 3;
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

        boolean[][] boolWell = setBoolWell(well);
        holeSize(well, boolWell, 1, 1);

        // scan by col
        int colTransitions = 0;
        int holeDepth = 0;
        int[] colsHeight = new int[boardWidth];
        for (int i = 1; i < boardWidth - 1; i++) {
            boolean trans = (well[i][1] == Color.BLACK);
            int depth = 0;
            for (int j = 1; j < boardHeight - 1; j++) {
                boolean next = (well[i][j] == Color.BLACK);
                if (trans != next) {
                    colTransitions++;
                    if (next) {
                        holeDepth += depth;
                        depth = 0;
                    }
                }
                if (!next) {
                    depth++;
                    if (colsHeight[i] == 0)
                        colsHeight[i] = boardHeight - j - 1;
                }
                trans = next;
            }
        }
        values.setColTransitions(colTransitions);
        values.setHoleDepth(holeDepth);

        int cumulativeWells = 0;
        colsHeight[0] = boardHeight;
        colsHeight[boardWidth - 1] = boardHeight;
        for (int i = 1; i < boardWidth - 1; i++) {
            int height = (colsHeight[i - 1] < colsHeight[i + 1]) ? (colsHeight[i - 1]) : (colsHeight[i + 1]);
            height -= colsHeight[i];
            for (; height > 0; height--) {
                cumulativeWells += height;
            }
        }
        values.setCumulativeWells(cumulativeWells);

        // scan by row
        int rowTransitions = 0;
        int numHoles = 0;
        int rowsWithHoles = 0;
        for (int j = boardHeight - 2; j > 0; j--) {
            boolean trans = (well[1][j] == Color.BLACK);
            for (int i = 1; i < boardWidth - 1; i++) {
                boolean next = (well[i][j] == Color.BLACK);
                if (trans != next)
                    rowTransitions++;
                trans = next;
                if (next && !boolWell[i][j]) {
                    if (rowsWithHoles == 0)
                        rowsWithHoles = boardHeight - j - 1;
                    holeSize(well, boolWell, i, j);
                    numHoles++;
                }
            }
        }
        values.setRowTransitions(rowTransitions);
        values.setNumHoles(numHoles);
        values.setRowsWithHoles(rowsWithHoles);
        // return values
        return values;
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

    private boolean[][] setBoolWell(Color[][] well) {
        // return if scanning is not necessary
        boolean[][] boolWell = new boolean[well.length][well[0].length];
        for (int i = 0; i < well.length; i++) {
            for (int j = 0; j < well[i].length; j++) {
                if (well[i][j] == Color.BLACK)
                    boolWell[i][j] = false;
                else
                    boolWell[i][j] = true;
            }
        }
        return boolWell;
    }

    private int holeSize(Color[][] well, boolean[][] boolWell, int x, int y) {
        if (x < 0 || y < 0 || x >= boardWidth || y >= boardHeight || boolWell[x][y])
            return 0;
        else {
            boolWell[x][y] = true;
            return 1 + holeSize(well, boolWell, x - 1, y) + holeSize(well, boolWell, x, y - 1)
                    + holeSize(well, boolWell, x + 1, y) + holeSize(well, boolWell, x, y + 1);
        }
    }

    public double[][] getInputWeight() {
        return inputWeight;
    }

    public double[] getOutputWeight() {
        return outputWeight;
    }

    public Individual getWeight() {
        return new Individual(inputWeight, outputWeight);
    }
}