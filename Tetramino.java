
import java.awt.Point;
import java.awt.Color;

/**
 * Tetramino
 */
public class Tetramino {

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

	public static final int SRSnum = 5;
	private static final Point[][][] SRS = {
			// TSZLJ piece
			{
					// 0->3
					{ new Point(0, 0), new Point(1, 0), new Point(1, -1), new Point(0, 2), new Point(1, 2) },
					// 0->1
					{ new Point(0, 0), new Point(-1, 0), new Point(-1, -1), new Point(0, 2), new Point(-1, 2) },
					// 1->0
					{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(0, -2), new Point(1, -2) },
					// 1->2
					{ new Point(0, 0), new Point(1, 0), new Point(1, 1), new Point(0, -2), new Point(1, -2) },
					// 2->1
					{ new Point(0, 0), new Point(-1, 0), new Point(-1, -1), new Point(0, 2), new Point(-1, 2) },
					// 2->3
					{ new Point(0, 0), new Point(1, 0), new Point(1, -1), new Point(0, 2), new Point(1, 2) },
					// 3->2
					{ new Point(0, 0), new Point(-1, 0), new Point(-1, 1), new Point(0, -2), new Point(-1, -2) },
					// 3->0
					{ new Point(0, 0), new Point(-1, 0), new Point(-1, 1), new Point(0, -2), new Point(-1, -2) } },
			// I piece
			{
					// 0->3
					{ new Point(0, 0), new Point(2, 0), new Point(-1, 0), new Point(-1, -2), new Point(2, 1) },
					// 0->1
					{ new Point(0, 0), new Point(-2, 0), new Point(1, 0), new Point(1, -2), new Point(-2, 1) },
					// 1->0
					{ new Point(0, 0), new Point(2, 0), new Point(-1, 0), new Point(2, -1), new Point(-1, 2) },
					// 1->2
					{ new Point(0, 0), new Point(-1, 0), new Point(2, 0), new Point(-1, -2), new Point(2, 1) },
					// 2->1
					{ new Point(0, 0), new Point(-2, 0), new Point(1, 0), new Point(-2, -1), new Point(1, 1) },
					// 2->3
					{ new Point(0, 0), new Point(2, 0), new Point(-1, 0), new Point(2, -1), new Point(-1, 1) },
					// 3->2
					{ new Point(0, 0), new Point(1, 0), new Point(-2, 0), new Point(1, -2), new Point(-2, 1) },
					// 3->0
					{ new Point(0, 0), new Point(-2, 0), new Point(1, 0), new Point(-2, -1), new Point(1, 2) } } };
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

	/**
	 * 0->T,1:->S,2->Z,3->L,4->J,5->O,6->I
	 */
	public int piece;
	private int rotation;
	private Point pieceOrigin;

	public Tetramino(int piece) {
		this.piece = piece;
		rotation = 0;
	}

	public boolean isT(){
		return piece==0;
	}

	public Color getColor() {
		return tetraminoColors[piece];
	}

	public void setRotation(int rotation) {
		this.rotation = rotation;
	}

	public int getRotation() {
		return rotation;
	}

	public Point[] getPoints() {
		return Tetraminos[piece][rotation];
	}

	public Point[] getPoints(int rotation) {
		return Tetraminos[piece][rotation];
	}

	public Point getSRS(int i,int n){
		return SRS[piece<5?0:1][rotation*2+(i<0?0:1)][n];
	}

	public int getOriginX() {
		return pieceOrigin.x;
	}

	public int getOriginY() {
		return pieceOrigin.y;
	}

	public void setOrigin(Point p) {
		pieceOrigin = p;
	}

	public void moveOrigin(int moveX, int moveY) {
		pieceOrigin.move(pieceOrigin.x + moveX, pieceOrigin.y + moveY);
	}
}