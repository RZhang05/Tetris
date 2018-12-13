/*
 * Raymond Zhang
 * Mr. Benum
 * ICS4UE
 * December 12, 2018
 */
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

public class Board extends JPanel implements ActionListener {
	//fields
	private final int BOARD_WIDTH = 20, BOARD_HEIGHT = 40, DELAY = 400;
	private Timer timer;
	private boolean isFallingFinished = false, isStarted = false, isPaused = false;
	private int curScore = 0, curX = 0, curY = 0;
	private JLabel statusbar;
	private Block curPiece, pieceHeld;
	private Block.Shape[] board;

	//constructors
	public Board(Tetris parent) {
		initBoard(parent);
	}
	
	//methods
	private void initBoard(Tetris parent) {
		setFocusable(true);
		curPiece = new Block();
		pieceHeld = new Block();
		timer = new Timer(DELAY, this);
		timer.start(); 

		statusbar =  parent.getStatusBar();
		board = new Block.Shape[BOARD_WIDTH * BOARD_HEIGHT];
		addKeyListener(new TAdapter());
		clearBoard();
		setBackground(Color.BLACK	);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else oneLineDown();
	}

	private int squareWidth() { return (int) getSize().getWidth() / BOARD_WIDTH; }
	private int squareHeight() { return (int) getSize().getHeight() / BOARD_HEIGHT; }
	private Block.Shape shapeAt(int x, int y) { return board[(y * BOARD_WIDTH) + x]; }


	public void start()  {
		if (isPaused)
			return;

		isStarted = true;
		isFallingFinished = false;
		curScore = 0;
		clearBoard();

		newPiece();
		timer.start();
	}

	private void pause()  {
		if (!isStarted) return;

		isPaused = !isPaused;

		if (isPaused) {
			timer.stop();
			statusbar.setText("paused");
		} else {
			timer.start();
			statusbar.setText("Score: " + curScore);
		}

		repaint();
	}

	private void draw(Graphics g) {
		Dimension size = getSize();
		int boardTop = (int) size.getHeight() - BOARD_HEIGHT * squareHeight();

		for (int i=0;i<BOARD_HEIGHT;i++) {
			for (int j=0;j<BOARD_WIDTH;j++) {
				Block.Shape shape = shapeAt(j, BOARD_HEIGHT - i - 1);

				if (shape != Block.Shape.NoShape) drawSquare(g, 0 + j * squareWidth(), boardTop + i * squareHeight(), shape);
			}
		}
		ArrayList<Integer> xs = new ArrayList<Integer>();
		if (curPiece.getShape() != Block.Shape.NoShape) {
			for (int i=0;i<4;i++) {
				int x = curX + curPiece.x(i);
				if(!xs.contains(x)) xs.add(x);
			}
			for(int i=0;i<xs.size();i++) drawIndicator(g, 0+xs.get(i)*squareWidth());
			for (int i=0;i<4;i++) {
				int x = curX + curPiece.x(i);
				if(!xs.contains(x)) xs.add(x);
				int y = curY - curPiece.y(i);
				drawSquare(g, 0 + x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape());
			}
		}        
	}

	@Override
	public void paintComponent(Graphics g) { 
		super.paintComponent(g);
		draw(g);
	}

	private void dropDown() {
		int newY = curY;
		while (newY > 0) {
			if (!tryMove(curPiece, curX, newY - 1)) break;
			--newY;
		}
		pieceDropped();
	}

	private void oneLineDown()  {
		if (!tryMove(curPiece, curX, curY - 1)) pieceDropped();
	}


	private void clearBoard() {
		for (int i=0;i<BOARD_HEIGHT * BOARD_WIDTH;i++) board[i] = Block.Shape.NoShape;
	}

	private void pieceDropped() {
		for (int i=0;i<4;i++) {
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
		}

		removeFullLines();

		if (!isFallingFinished) newPiece();
	}

	private void newPiece()  {
		curPiece.setRandomShape();
		curX = BOARD_WIDTH / 2 + 1;
		curY = BOARD_HEIGHT - 1 + curPiece.minY();

		if (!tryMove(curPiece, curX, curY)) {
			curPiece.setShape(Block.Shape.NoShape);
			timer.stop();
			isStarted = false;
			statusbar.setText("game over");
		}
	}
	
	private void holdBlock() {
		pieceHeld = curPiece;
	}

	private boolean tryMove(Block newPiece, int newX, int newY) {
		for (int i=0;i<4;i++) {
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);

			if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) return false;

			if (shapeAt(x, y) != Block.Shape.NoShape) return false;
		}

		curPiece = newPiece;
		curX = newX;
		curY = newY;

		repaint();

		return true;
	}

	private void removeFullLines() {
		int numFullLines = 0;

		for (int i = BOARD_HEIGHT - 1; i >= 0; --i) {
			boolean lineIsFull = true;

			for (int j = 0; j < BOARD_WIDTH; ++j) {
				if (shapeAt(j, i) == Block.Shape.NoShape) {
					lineIsFull = false;
					break;
				}
			}

			if (lineIsFull) {
				++numFullLines;
				for (int k = i; k < BOARD_HEIGHT - 1; ++k) {
					for (int j = 0; j < BOARD_WIDTH; ++j) {
						board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
					}
				}
			}
		}

		if (numFullLines > 0) {
			if(numFullLines == 1) curScore += 100;
			else if(numFullLines == 2) curScore += 300;
			else if(numFullLines == 3) curScore += 500;
			else curScore += 800;
			statusbar.setText("Score: " + curScore);
			isFallingFinished = true;
			curPiece.setShape(Block.Shape.NoShape);
			repaint();
		}
	}

	private void drawSquare(Graphics g, int x, int y, Block.Shape shape)  {
		Color colors[] = { new Color(0, 0, 0), new Color(63, 61, 68), 
				new Color(63, 61, 68), new Color(221, 95, 93), 
				new Color(204, 76, 22), new Color(237, 165, 158), 
				new Color(237, 165, 158), new Color(204, 76, 22)
		};

		Color color = colors[shape.ordinal()];

		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareWidth() - 1, y);

		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
	}
	
	private void drawIndicator(Graphics g, int x) {
		Color color = new Color(255,255,255,100);
		g.setColor(color);
		g.fillRect(x, 0, squareWidth()-1, (int)getSize().getHeight());
	}

	class TAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {

			if (!isStarted || curPiece.getShape() == Block.Shape.NoShape) return;

			int keycode = e.getKeyCode();

			if (keycode == 'P') {
				pause();
				return;
			}

			if (isPaused) return;

			switch (keycode) {

			case KeyEvent.VK_LEFT:
				tryMove(curPiece, curX - 1, curY);
				break;

			case KeyEvent.VK_RIGHT:
				tryMove(curPiece, curX + 1, curY);
				break;

			case 'Z':
				tryMove(curPiece.rotateRight(), curX, curY);
				break;

			case KeyEvent.VK_UP:
				tryMove(curPiece.rotateLeft(), curX, curY);
				break;

			case KeyEvent.VK_SPACE:
				dropDown();
				break;

			case KeyEvent.VK_DOWN:
				oneLineDown();
				break;
			}
		}
	}
}