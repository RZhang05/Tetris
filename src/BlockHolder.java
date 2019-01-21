/*
 * Raymond Zhang
 * Mr. Benum
 * ICS4UE
 * December 13, 2018
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class BlockHolder extends JPanel {
	//fields
	private final int BOARD_WIDTH = 21, BOARD_HEIGHT = 6, OFFSET_BH = 4, OFFSET_NB = 9;
	private Block blockHeld, nextBlock;
	private Block.Shape[] board;
	private Font defFont = new Font("TimesRoman", Font.PLAIN, 12);

	//constructors
	public BlockHolder() {
		init();
	}

	//methods

	/**
	 * Change the block held
	 * @param newBlock
	 */
	public void updateBlockHeld(Block newBlock) {
		blockHeld = newBlock;
		repaint();
	}

	/**
	 * Change the next block
	 * @param newBlock
	 */
	public void updateNextBlock(Block newBlock) {
		nextBlock = newBlock;
		repaint();
	}

	/**
	 * Initialize the holder
	 */
	private void init() {
		blockHeld = new Block();
		nextBlock = new Block();
		board = new Block.Shape[BOARD_WIDTH * BOARD_HEIGHT];
		clearBoard();
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(200,100));
		JLabel info = new JLabel("Current block held:   ");
		info.setFont(defFont);
		add(info);
		info = new JLabel("Next block: ");
		info.setFont(defFont);
		add(info);
	}

	/**
	 * Return the size of a block square
	 * @return length of square side
	 */
	private int squareHeight() { 
		return (int) getSize().getHeight() / BOARD_HEIGHT; 
	}

	/**
	 * Retrieve the shape of the block at the specified coordinates
	 * @param x
	 * @param y
	 * @return Shape of block 
	 */
	private Block.Shape shapeAt(int x, int y) { 
		return board[(y * BOARD_WIDTH) + x]; 
	}

	/**
	 * Draw the blockholder
	 * @param g
	 */
	private void draw(Graphics g) {
		int boardTop = -25;

		for (int i=0;i<BOARD_HEIGHT;i++) {
			for (int j=0;j<BOARD_WIDTH;j++) {
				Block.Shape shape = shapeAt(j, BOARD_HEIGHT - i - 1);

				if (shape != Block.Shape.NoShape) drawSquare(g, 0 + j * squareHeight(), boardTop + i * squareHeight(), shape);
			}
		}
		if (blockHeld.getShape() != Block.Shape.NoShape) {
			for (int i=0;i<4;i++) {
				int x = OFFSET_BH + blockHeld.x(i);
				int y = blockHeld.y(i);
				drawSquare(g, 0 + x * squareHeight(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), blockHeld.getShape());
			}   
		}
		if (nextBlock.getShape() != Block.Shape.NoShape) {
			for (int i=0;i<4;i++) {
				int x = OFFSET_NB + nextBlock.x(i);
				int y = nextBlock.y(i);
				drawSquare(g, 0 + x * squareHeight(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), nextBlock.getShape());
			}   
		}
	}

	/**
	 * Draw the object
	 */
	@Override
	public void paintComponent(Graphics g) { 
		super.paintComponent(g);
		draw(g);
	}

	/**
	 * Clear the board
	 */
	private void clearBoard() {
		for (int i=0;i<BOARD_HEIGHT * BOARD_WIDTH;i++) board[i] = Block.Shape.NoShape;
	}

	/**
	 * Draws a square
	 * @param g
	 * @param x
	 * @param y
	 * @param shape
	 */
	private void drawSquare(Graphics g, int x, int y, Block.Shape shape)  {
		//Tetris block colours
		Color colors[] = { new Color(0, 0, 0), new Color(170, 6, 6), 
				new Color(3, 165, 43), new Color(1, 193, 181), 
				new Color(109, 1, 191), new Color(191, 178, 0), 
				new Color(201, 115, 2), new Color(0, 33, 201)
		};

		Color color = colors[shape.ordinal()];

		//draw with bevel
		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareHeight() - 2, squareHeight() - 2);

		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareHeight() - 1, y);

		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareHeight() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareHeight() - 1, y + squareHeight() - 1, x + squareHeight() - 1, y + 1);
	}
}