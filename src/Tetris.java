/*
 * Raymond Zhang
 * Mr. Benum
 * ICS4UE
 * December 12, 2018
 */
import java.awt.*;
import javax.swing.*;

public class Tetris extends JFrame {

	private JLabel statusbar, instructions;

	public Tetris() {
		initGame();
	}

	private void initGame() {
		
		statusbar = new JLabel("Score: 0");
		add(statusbar, BorderLayout.NORTH);
		
		instructions = new JLabel("<html>\'p\' to pause<br/>"
				+ "\'left arrow\' to move left<br/>"
				+ "\'right arrow\' to move right<br/>"
				+ "\'up arrow\' to rotate left<br/>"
				+ "\'down arrow to drop block down faster<br/>"
				+ "\'z\' to rotate right<br/>"
				+ "\'space\' to drop block down all the way<html>");
				
		instructions.setHorizontalAlignment(SwingConstants.CENTER);
		add(instructions,BorderLayout.SOUTH);

		Board board = new Board(this);
		add(board);
		board.start();

		setTitle("Tetris");
		setSize(424, 1000);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

	public JLabel getStatusBar() {
		return statusbar;
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			Tetris game = new Tetris();
			game.setVisible(true);
		});
	}
}