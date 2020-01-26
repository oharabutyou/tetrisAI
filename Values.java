public class Values {
    private int LandingHeight, ErodedPieceCells, RowTransitions, ColTransitions, NumHoles, CumulativeWells, HoleDepth,
            RowsWithHoles;

    public int getLandingHeight() {
        return LandingHeight;
    }

    public void setLandingHeight(int landingHeight) {
        LandingHeight = landingHeight;
    }

    public int getErodedPieceCells() {
        return ErodedPieceCells;
    }

    public void setErodedPieceCells(int erodedPieceCells) {
        ErodedPieceCells = erodedPieceCells;
    }

    public int getRowTransitions() {
        return RowTransitions;
    }

    public void setRowTransitions(int rowTransitions) {
        RowTransitions = rowTransitions;
    }

    public int getColTransitions() {
        return ColTransitions;
    }

    public void setColTransitions(int colTransitions) {
        ColTransitions = colTransitions;
    }

    public int getNumHoles() {
        return NumHoles;
    }

    public void setNumHoles(int numHoles) {
        NumHoles = numHoles;
    }

    public int getCumulativeWells() {
        return CumulativeWells;
    }

    public void setCumulativeWells(int cumulativeWells) {
        CumulativeWells = cumulativeWells;
    }

    public int getHoleDepth() {
        return HoleDepth;
    }

    public void setHoleDepth(int holeDepth) {
        HoleDepth = holeDepth;
    }

    public int getRowsWithHoles() {
        return RowsWithHoles;
    }

    public void setRowsWithHoles(int rowsWithHoles) {
        RowsWithHoles = rowsWithHoles;
    }

    public int[] getValues() {
        int[] values = { LandingHeight, ErodedPieceCells, RowTransitions, ColTransitions, NumHoles, CumulativeWells,
                HoleDepth, RowsWithHoles };
        return values;
    }
}