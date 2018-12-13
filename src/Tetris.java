/*
 * Raymond Zhang
 * Mr. Benum
 * ICS4UE
 * December 12, 2018
 */
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class Tetris extends JFrame {

	private JLabel statusbar;
	private BlockHolder placeholder;
	private JButton start, instructions;

	public Tetris() {
		setTitle("Tetris");
		setSize(350, 940);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		
		reset();
	}
	
	public void reset() {
		initUI();
	}
	
	private void initUI() {
		start = new JButton("Start");
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				remove(start);
				initGame();
			}
		});
		
		add(start);
	}

	private void initGame() {
		placeholder = new BlockHolder();
		add(placeholder, BorderLayout.NORTH);
		
		statusbar = new JLabel("Score: 0");
		add(statusbar, BorderLayout.SOUTH);

		Board board = new Board(this);
		add(board);
		board.start();
		
		board.requestFocus();
		
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