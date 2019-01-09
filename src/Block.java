/*
 * Raymond Zhang
 * Mr. Benum
 * ICS4UE
 * December 12, 2018
 */
import java.util.Random;

public class Block {

	//Possible Shapes
	protected enum Shape { NoShape, ZShape, SShape, LineShape, 
		TShape, OShape, LShape, MirroredLShape };

		//fields
		private Shape pieceShape;
		private int coords[][];
		private int[][][] coordsTable;

		//constructor
		public Block() {
			coords = new int[4][2];
			setShape(Shape.NoShape);
		}

		//methods

		/**
		 * Sets the coordinates for the block
		 * @param shape
		 */
		public void setShape(Shape shape) {

			//Initial coordinates for each block
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

			//initialize the shape's coordinates
			for (int i=0;i<4;i++) {
				for (int j=0;j<2;j++) {
					coords[i][j] = coordsTable[shape.ordinal()][i][j];
				}
			}

			pieceShape = shape;
		}

		/**
		 * Set the x coordinate
		 * @param index
		 * @param x
		 */
		private void setX(int index, int x) { 
			coords[index][0] = x; 
		}
		
		/**
		 * Set the y coordinate
		 * @param index
		 * @param y
		 */
		private void setY(int index, int y) { 
			coords[index][1] = y; 
		}
		
		/**
		 * Retrieve the x coordinate
		 * @param index
		 * @return x coordinate
		 */
		public int x(int index) { 
			return coords[index][0];
		}
		
		/**
		 * Retrieve the y coordinate
		 * @param index
		 * @return y coordinate
		 */
		public int y(int index) { 
			return coords[index][1]; 
		}
		
		/**
		 * Retrieve the shape
		 * @return shape of the block
		 */
		public Shape getShape() { 
			return pieceShape; 
		}

		/**
		 * Set the shape of the block to a random shape
		 */
		public void setRandomShape() {
			Random r = new Random();
			int x = r.nextInt(7)+1;
			Shape[] values = Shape.values(); 
			setShape(values[x]);
		}

		/**
		 * Retrieve the smallest x coordinate
		 * @return smallest x coordinate
		 */
		public int minX() {
			int x = coords[0][0];
			for (int i=0;i<4;i++) {
				x = Math.min(x, coords[i][0]);
			}
			return x;
		}

		/**
		 * Retrieve the smallest y coordinate
		 * @return smallest y coordinate
		 */
		public int minY() {
			int y = coords[0][1];
			for (int i=0;i<4;i++) {
				y = Math.min(y, coords[i][1]);
			}
			return y;
		}

		/**
		 * Rotate the block counterclockwise
		 * @return rotated block
		 */
		public Block rotateLeft() {
			//if its a square no need to rotate
			if (pieceShape == Shape.OShape)
				return this;

			Block result = new Block();
			result.pieceShape = pieceShape;

			//switch coordinate places
			for (int i=0;i<4;i++) {
				result.setX(i, y(i));
				result.setY(i, -x(i));
			}

			return result;
		}

		/**
		 * Rotate the block clockwise
		 * @return rotated block
		 */
		public Block rotateRight() {
			if (pieceShape == Shape.OShape)
				return this;

			Block result = new Block();
			result.pieceShape = pieceShape;

			//switch coordinate places
			for (int i=0;i<4;i++) {
				result.setX(i, -y(i));
				result.setY(i, x(i));
			}

			return result;
		}
}