public class TetrisCtrl {
    int pieceOriginX;
    int rotation;

    public TetrisCtrl(int pieceOriginX, int rotation) {
        this.pieceOriginX = pieceOriginX;
        this.rotation = rotation;
    }

    public int getPieceOriginX() {
        return pieceOriginX;
    }

    public void setPieceOriginX(int pieceOriginX) {
        this.pieceOriginX = pieceOriginX;
    }

    public int getRotation() {
        return rotation;
    }

    public void setRotation(int rotation) {
        this.rotation = rotation;
    }
}