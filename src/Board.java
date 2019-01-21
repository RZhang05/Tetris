/*
 * Raymond Zhang
 * Mr. Benum
 * ICS4UE
 * December 12, 2018
 */
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Board extends JPanel implements ActionListener {
	//fields
	private final int BOARD_WIDTH = 10, BOARD_HEIGHT = 24;
	private Timer timer;
	private boolean isFallingFinished = false, isStarted = false, isPaused = false;
	private int curScore = 0, curX = 0, curY = 0, curms, delay = 400;
	private Tetris parent;
	private JLabel statusbar;
	private BlockHolder placeholder;
	private Block curPiece, blockHeld, nextBlock;
	private Block.Shape[] board;
	private Scanner sc;
	private Clip clip;
	private URL url;

	//constructors
	public Board(Tetris parent) {
		initBoard(parent);
	}

	//methods

	/**
	 * Starts the game and initializes all variables
	 * @param parent
	 */
	private void initBoard(Tetris parent) {
		setFocusable(true);
		//initialize all the blocks
		curPiece = new Block();
		blockHeld = new Block();
		nextBlock = new Block();
		//set the shape of the first block
		nextBlock.setRandomShape();

		//initialize timer
		timer = new Timer(delay, this);
		timer.start();

		//initialize all global variables
		this.parent = parent;
		statusbar =  parent.getStatusBar();
		placeholder = parent.getBlockHolder();
		placeholder.updateNextBlock(nextBlock);
		board = new Block.Shape[BOARD_WIDTH * BOARD_HEIGHT];

		//prepare game screen
		addKeyListener(new TAdapter());
		clearBoard();
		setBackground(Color.BLACK);
		setPreferredSize(new Dimension(200,480));

		//set Scanner to read in highscores text file
		try {
			File f = new File("highscores.txt");
			sc = new Scanner(f);
		} catch (Exception e) {System.out.println(e);};
	}

	/**
	 * When an action occurs, move the current block down or make a new one
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		//if the current block has reached the end
		if (isFallingFinished) {
			isFallingFinished = false;
			newPiece();
		} else {
			pieceFallNoise();
			oneLineDown(); //else move the current block down one line
		}
		//keep count of current milliseconds
		curms += 400;
		//every 20 seconds timer speeds up so blocks fall faster (difficulty)
		if(curms >= 20000 && delay > 50) {
			curms = 0;
			delay -= 50;
			timer.setDelay(delay);
		}
	}

	/**
	 * Calculates the width of a board square
	 * @return width of a board square
	 */
	private int squareWidth() { 
		return (int) getSize().getWidth() / BOARD_WIDTH; 
	}
	/**
	 * Calculate the height of a board square
	 * @return height of a board square
	 */
	private int squareHeight() { 
		return (int) getSize().getHeight() / BOARD_HEIGHT; 
	}
	/**
	 * Identifies what shape is at the current coordinates
	 * @param x
	 * @param y
	 * @return Shape of the Block at the specified location
	 */
	private Block.Shape shapeAt(int x, int y) { 
		//1d array used to represent 2d array
		return board[(y * BOARD_WIDTH) + x]; 
	}

	/**
	 * Starts the game
	 */
	public void start()  {
		if (isPaused)
			return;

		isStarted = true;
		isFallingFinished = false;
		clearBoard();

		newPiece();
		timer.start();
	}

	/**
	 * Pauses the game
	 */
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

	/**
	 * Draw the game
	 * @param g
	 */
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
				int y = curY - curPiece.y(i);
				drawSquare(g, 0 + x * squareWidth(), boardTop + (BOARD_HEIGHT - y - 1) * squareHeight(), curPiece.getShape());
			}
		}        
	}

	/**
	 * Draws the object
	 */
	@Override
	public void paintComponent(Graphics g) { 
		super.paintComponent(g);
		draw(g);
	}

	/**
	 * Drops the current block down
	 */
	private void dropDown() {
		int newY = curY;
		while (newY > 0) {
			if (!tryMove(curPiece, curX, newY - 1)) break;
			--newY;
		}
		blockDropped();
	}

	/**
	 * Move the current block down one line
	 */
	private void oneLineDown()  {
		if (!tryMove(curPiece, curX, curY - 1)) blockDropped();
	}

	/**
	 * Clears the board
	 */
	private void clearBoard() {
		for (int i=0;i<BOARD_HEIGHT * BOARD_WIDTH;i++) board[i] = Block.Shape.NoShape;
	}

	/**
	 * When a block is dropped remove all full lines and make a new block
	 */
	private void blockDropped() {
		for (int i=0;i<4;i++) {
			int x = curX + curPiece.x(i);
			int y = curY - curPiece.y(i);
			board[(y * BOARD_WIDTH) + x] = curPiece.getShape();
		}

		removeFullLines();

		if (!isFallingFinished) newPiece();
	}

	/**
	 * Sets the shape of the next block and also checks if game is over
	 */
	private void newPiece()  {
		//update the current block
		curPiece.setShape(nextBlock.getShape());
		//place the block roughly in the center at the top
		curX = BOARD_WIDTH / 2 + 1;
		curY = BOARD_HEIGHT - 1 + curPiece.minY();
		//update the next block
		nextBlock.setRandomShape();
		placeholder.updateNextBlock(nextBlock);

		//if the new block can not be placed game is over
		if (!tryMove(curPiece, curX, curY)) gameOver();
	}

	/**
	 * Load in a piece at the top
	 */
	private void loadPiece() {
		curX = BOARD_WIDTH / 2 + 1;
		curY = BOARD_HEIGHT - 1 + curPiece.minY();

		//new piece can not be placed
		if (!tryMove(curPiece, curX, curY)) gameOver();
	}

	/**
	 * Runs the end game process
	 */
	private void gameOver() {
		gameOverNoise();
		curPiece.setShape(Block.Shape.NoShape);
		timer.stop();
		isStarted = false;
		statusbar.setText("game over");
		parent.reset();

		//Allows you to save your score
		String name = JOptionPane.showInputDialog("Please enter your username: ");
		try {
			boolean done = false;
			ArrayList<String> toDo = new ArrayList<String>();
			//loop through and find where your score belongs
			while(sc.hasNextLine()) {
				String cur = sc.next();
				int score = sc.nextInt();
				if(score <= curScore && !done) { //where you score belongs
					done = true;
					toDo.add(name + " " + curScore);
				}
				toDo.add(cur + " " + score);
				if(toDo.size()==10) break;
			}
			
			System.setOut(new PrintStream("highscores.txt"));
			//set output to file
			//replace old scores
			for(int i=0;i<9;i++) System.out.println(toDo.get(i));
			System.out.print(toDo.get(9)); //for no extra line which breaks the program
		} catch(Exception e) {JOptionPane.showMessageDialog(null, e);};
	}

	/**
	 * Hold a block
	 */
	private void holdBlock() {
		//if no block is currently held
		if(blockHeld.getShape() == Block.Shape.NoShape) {
			blockHeld.setShape(curPiece.getShape());
			newPiece();
			placeholder.updateBlockHeld(blockHeld);
			return;
		}
		//switch current block and the block held
		Block temp = new Block();
		temp.setShape(curPiece.getShape());
		curPiece.setShape(blockHeld.getShape());
		blockHeld.setShape(temp.getShape());
		loadPiece();
		placeholder.updateBlockHeld(blockHeld);
	}

	/**
	 * Check if block move is valid
	 * @param newPiece
	 * @param newX
	 * @param newY
	 * @return if the move is valid
	 */
	private boolean tryMove(Block newPiece, int newX, int newY) {
		//check all points
		for (int i=0;i<4;i++) {
			int x = newX + newPiece.x(i);
			int y = newY - newPiece.y(i);
			//if its outside the board
			if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) {
				failNoise();
				return false;
			}
			//if there is a block at that location
			if (shapeAt(x, y) != Block.Shape.NoShape) {
				failNoise();
				return false;
			}
		}

		//update curBlock
		curPiece = newPiece;
		curX = newX;
		curY = newY;

		repaint();

		pieceMovingNoise();
		//move was valid
		return true;
	}

	/**
	 * Removes all full lines and updates score based on amount
	 */
	private void removeFullLines() {
		//for scoring
		int numFullLines = 0;

		//loop through the board
		for (int i = BOARD_HEIGHT - 1; i >= 0; i--) {
			boolean lineIsFull = true;

			for (int j = 0; j < BOARD_WIDTH; j++) {
				//if there is no block in the line
				if (shapeAt(j, i) == Block.Shape.NoShape) {
					lineIsFull = false;
					break;
				}
			}

			//remove the line and set it to be the line above
			if (lineIsFull) {
				++numFullLines;
				for (int k = i; k < BOARD_HEIGHT - 1; k++) {
					for (int j = 0; j < BOARD_WIDTH; j++) {
						board[(k * BOARD_WIDTH) + j] = shapeAt(j, k + 1);
					}
				}
			}
		}

		//update score based on number of lines removed
		if (numFullLines > 0) {
			//classic scoring system
			if(numFullLines == 1) {
				oneLineClear();
				curScore += 100;
			} else if(numFullLines == 2) {
				doubleLineClear();
				curScore += 300;
			} else if(numFullLines == 3) {
				tripleLineClear();
				curScore += 500;
			} else {
				quadLineClear();
				curScore += 800;
			}
			statusbar.setText("Score: " + curScore);
			isFallingFinished = true;
			curPiece.setShape(Block.Shape.NoShape);
			repaint();
		}
	}

	/**
	 * Draw a square with appropriate dimensions
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

		//drawing to give bevel look
		g.setColor(color);
		g.fillRect(x + 1, y + 1, squareWidth() - 2, squareHeight() - 2);

		g.setColor(color.brighter());
		g.drawLine(x, y + squareHeight() - 1, x, y);
		g.drawLine(x, y, x + squareWidth() - 1, y);

		g.setColor(color.darker());
		g.drawLine(x + 1, y + squareHeight() - 1, x + squareWidth() - 1, y + squareHeight() - 1);
		g.drawLine(x + squareWidth() - 1, y + squareHeight() - 1, x + squareWidth() - 1, y + 1);
	}

	/**
	 * Draw the indicator to show where the block would fall
	 * @param g
	 * @param x
	 */
	private void drawIndicator(Graphics g, int x) {
		Color color = new Color(255,255,255,100);
		g.setColor(color);
		g.fillRect(x, 0, squareWidth()-1, (int)getSize().getHeight());
	}

	/**
	 * Play the game over sound
	 */
	private void gameOverNoise() {
		try {
			if(clip != null && clip.isOpen()) clip.stop();
			// Open an audio input stream.
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(Tetris.class.getResourceAsStream("/resources/sfx/SFX_GameOver.wav")));              
			// Get a sound clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch(Exception e) {JOptionPane.showMessageDialog(null, e);};
	}

	/**
	 * Play the piece falling sound
	 */
	private void pieceFallNoise() {
		try {
			if(clip != null && clip.isOpen()) clip.stop();
			// Open an audio input stream.
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(Tetris.class.getResourceAsStream("/resources/sfx/SFX_PieceFall.wav")));              
			// Get a sound clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch(Exception e) {JOptionPane.showMessageDialog(null, e);};
	}

	/**
	 * Play the piece falling sound
	 */
	private void pieceDropNoise() {
		try {
			if(clip != null && clip.isOpen()) clip.stop();
			// Open an audio input stream.        
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(Tetris.class.getResourceAsStream("/resources/sfx/SFX_PieceHardDrop.wav")));            
			// Get a sound clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch(Exception e) {JOptionPane.showMessageDialog(null, e);};
	}

	/**
	 * Play the piece hold sound
	 */
	private void pieceHoldNoise() {
		try {
			if(clip != null && clip.isOpen()) clip.stop();
			// Open an audio input stream.           
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(Tetris.class.getResourceAsStream("/resources/sfx/SFX_PieceHold.wav")));               
			// Get a sound clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch(Exception e) {JOptionPane.showMessageDialog(null, e);};
	}

	/**
	 * Play the piece moving sound
	 */
	private void pieceMovingNoise() {
		try {
			if(clip != null && clip.isOpen()) clip.stop();
			// Open an audio input stream.           
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(Tetris.class.getResourceAsStream("/resources/sfx/SFX_PieceMoveLR.wav")));              
			// Get a sound clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch(Exception e) {JOptionPane.showMessageDialog(null, e);};
	}

	/**
	 * Play the illegal move sound
	 */
	private void failNoise() {
		try {
			if(clip != null && clip.isOpen()) clip.stop();
			// Open an audio input stream.           
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(Tetris.class.getResourceAsStream("/resources/sfx/SFX_PieceRotateFail.wav")));               
			// Get a sound clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch(Exception e) {JOptionPane.showMessageDialog(null, e);};
	}

	/**
	 * Play the one line cleared sound
	 */
	private void oneLineClear() {
		try {
			if(clip != null && clip.isOpen()) clip.stop();
			// Open an audio input stream.           
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(Tetris.class.getResourceAsStream("/resources/sfx/SFX_SpecialLineClearSingle.wav")));             
			// Get a sound clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch(Exception e) {JOptionPane.showMessageDialog(null, e);};
	}

	/**
	 * Play the double line cleared sound
	 */
	private void doubleLineClear() {
		try {
			if(clip != null && clip.isOpen()) clip.stop();
			// Open an audio input stream.           
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(Tetris.class.getResourceAsStream("/resources/sfx/SFX_SpecialLineClearDouble.wav")));              
			// Get a sound clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch(Exception e) {JOptionPane.showMessageDialog(null, e);};
	}

	/**
	 * Play the triple line cleared sound
	 */
	private void tripleLineClear() {
		try {
			if(clip != null && clip.isOpen()) clip.stop();
			// Open an audio input stream.           
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(Tetris.class.getResourceAsStream("/resources/sfx/SFX_SpecialLineClearTriple.wav")));                
			// Get a sound clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch(Exception e) {JOptionPane.showMessageDialog(null, e);};
	}

	/**
	 * Play the quad line cleared sound
	 */
	private void quadLineClear() {
		try {
			if(clip != null && clip.isOpen()) clip.stop();
			// Open an audio input stream.       
			AudioInputStream audioIn = AudioSystem.getAudioInputStream(new BufferedInputStream(Tetris.class.getResourceAsStream("/resources/sfx/SFX_SpecialTetris.wav")));                        
			// Get a sound clip resource.
			clip = AudioSystem.getClip();
			// Open audio clip and load samples from the audio input stream.
			clip.open(audioIn);
			clip.start();
		} catch(Exception e) {JOptionPane.showMessageDialog(null, e);};
	}

	//Keyboard listener for controls
	class TAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {

			//if game is not running
			if (!isStarted || curPiece.getShape() == Block.Shape.NoShape) return;

			int keycode = e.getKeyCode();

			if (keycode == 'P') {
				pause();
				return;
			}

			if (isPaused) return;

			//controls
			switch (keycode) {

			//move left
			case KeyEvent.VK_LEFT:
				tryMove(curPiece, curX - 1, curY);
				break;

				//move right
			case KeyEvent.VK_RIGHT:
				tryMove(curPiece, curX + 1, curY);
				break;

				//rotate right
			case 'Z':
				tryMove(curPiece.rotateRight(), curX, curY);
				break;

				//rotate left
			case KeyEvent.VK_UP:
				tryMove(curPiece.rotateLeft(), curX, curY);
				break;

				//drop the block down
			case KeyEvent.VK_SPACE:
				dropDown();
				pieceDropNoise();
				break;

				//move the block one line down
			case KeyEvent.VK_DOWN:
				oneLineDown();
				pieceFallNoise();
				break;

				//hold the block
			case KeyEvent.VK_SHIFT:
				holdBlock();
				pieceHoldNoise();
				break;
			}
		}
	}
}