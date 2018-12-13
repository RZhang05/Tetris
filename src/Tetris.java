/*
 * Raymond Zhang
 * Mr. Benum
 * ICS4UE
 * December 12, 2018
 */
import java.awt.*;
import javax.swing.*;

public class Tetris extends JFrame {

	private JLabel statusbar;
	private BlockHolder placeholder;

	public Tetris() {
		initGame();
	}

	private void initGame() {
		
		placeholder = new BlockHolder();
		add(placeholder, BorderLayout.NORTH);
		
		statusbar = new JLabel("Score: 0");
		add(statusbar, BorderLayout.SOUTH);

		Board board = new Board(this);
		add(board);
		board.start();

		setTitle("Tetris");
		setSize(350, 940);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		pack();
	}

	public JLabel getStatusBar() {
		return statusbar;
	}
	
	public BlockHolder getBlockHolder() {
		return placeholder;
	}
	
	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			Tetris game = new Tetris();
			game.setVisible(true);
		});
	}
}