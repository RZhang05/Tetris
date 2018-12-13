/*
 * Raymond Zhang
 * Mr. Benum
 * ICS4UE
 * December 12, 2018
 */
import java.util.Random;

public class Block {

	protected enum Shape { NoShape, ZShape, SShape, LineShape, 
		TShape, OShape, LShape, MirroredLShape };

		private Shape pieceShape;
		private int coords[][];
		private int[][][] coordsTable;


		public Block() {
			coords = new int[4][2];
			setShape(Shape.NoShape);
		}

		public void setShape(Shape shape) {

			coordsTable = new int[][][] {
				{ { 0, 0 },   { 0, 0 },   { 0, 0 },   { 0, 0 } }, //NoShape
				{ { 0, -1 },  { 0, 0 },   { -1, 0 },  { -1, 1 } }, //ZShape
				{ { 0, -1 },  { 0, 0 },   { 1, 0 },   { 1, 1 } }, //SShape
				{ { 0, -1 },  { 0, 0 },   { 0, 1 },   { 0, 2 } }, //LineShape
				{ { -1, 0 },  { 0, 0 },   { 1, 0 },   { 0, 1 } }, //TShape
				{ { 0, 0 },   { 1, 0 },   { 0, 1 },   { 1, 1 } }, //OShape
				{ { -1, -1 }, { 0, -1 },  { 0, 0 },   { 0, 1 } }, //LShape
				{ { 1, -1 },  { 0, -1 },  { 0, 0 },   { 0, 1 } } //MirroredLShape
			};

			for (int i=0;i<4;i++) {
				for (int j=0;j<2;j++) {
					coords[i][j] = coordsTable[shape.ordinal()][i][j];
				}
			}

			pieceShape = shape;
		}

		private void setX(int index, int x) { coords[index][0] = x; }
		private void setY(int index, int y) { coords[index][1] = y; }
		public int x(int index) { return coords[index][0]; }
		public int y(int index) { return coords[index][1]; }
		public Shape getShape()  { return pieceShape; }

		public void setRandomShape() {
			Random r = new Random();
			int x = r.nextInt(7)+1;
			Shape[] values = Shape.values(); 
			setShape(values[x]);
		}

		public int minX() {
			int x = coords[0][0];
			for (int i=0;i<4;i++) {
				x = Math.min(x, coords[i][0]);
			}
			return x;
		}


		public int minY() {
			int y = coords[0][1];
			for (int i=0;i<4;i++) {
				y = Math.min(y, coords[i][1]);
			}
			return y;
		}

		public Block rotateLeft() {
			if (pieceShape == Shape.OShape)
				return this;

			Block result = new Block();
			result.pieceShape = pieceShape;

			for (int i=0;i<4;i++) {
				result.setX(i, y(i));
				result.setY(i, -x(i));
			}

			return result;
		}

		public Block rotateRight() {
			if (pieceShape == Shape.OShape)
				return this;

			Block result = new Block();
			result.pieceShape = pieceShape;

			for (int i=0;i<4;i++) {
				result.setX(i, -y(i));
				result.setY(i, x(i));
			}

			return result;
		}
}