/*
 * Raymond Zhang
 * Mr. Benum
 * ICS4UE
 * December 13, 2018
 */
import java.awt.*;
import javax.swing.*;

public class BlockHolder extends JPanel {
	//fields
	private final int BOARD_WIDTH = 21, BOARD_HEIGHT = 6;
	private Block curPiece;
	private Block.Shape[] board;

	//constructors
	public BlockHolder() {
		init();
	}
	
	public void update(Block newBlock) {
		curPiece = newBlock;
		repaint();
	}

	//methods
	private void init() {
		curPiece = new Block();
		board = new Block.Shape[BOARD_WIDTH * BOARD_HEIGHT];
		clearBoard();
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(350,100));
		JLabel info = new JLabel("Current block held: ");
		add(info);
	}

	private int squareHeight() { return (int) getSize().getHeight() / BOARD_HEIGHT; }
	private Block.Shape shapeAt(int x, int y) { return board[(y * BOARD_WIDTH) + x]; }

	private void draw(Graphics g) {
		Dimension size = getSize();
		int boardTop = -25;

		for (int i=0;i<BOARD_HEIGHT;i++) {
			for (int j=0;j<BOARD_WIDTH;j++) {
				Block.Shape shape = shapeAt(j, BOARD_HEIGHT - i - 1);

				if (shape != Block.Shape.NoShape) drawSquare(g, 0 + j * squareHeight(), boardTop + i * squareHeight(), shape);
			}
		}
		if (curPiece.getShape() != Block.Shape.NoShape) {
			for (int i=0;i<4;i++) {
				int x = 11 + curPiece.x(i);
				int y = curPiece.y(i);
				drawSquare(g, 0 + x * squareHeight(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape());
			}   
		}
	}

	@Override
	public void paintComponent(Graphics g) { 
		super.paintComponent(g);
		draw(g);
	}


	private void clearBoard() {
		for (int i=0;i<BOARD_HEIGHT * BOARD_WIDTH;i++) board[i] = Block.Shape.NoShape;
	}

	private void drawSquare(Graphics g, int x, int y, Block.Shape shape)  {
		Color colors[] = { Color.WHITE, new Color(63, 61, 68), 
				new Color(63, 61, 68), new Color(221, 95, 93), 
				new Color(204, 76, 22), new Color(237, 165, 158), 
				new Color(237, 165, 158), new Color(204, 76, 22)
		};

		Color color = colors[shape.ordinal()];

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