/*
 * Raymond Zhang
 * Mr. Benum
 * ICS4UE
 * December 12, 2018
 */
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.imageio.*;
import javax.swing.*;

public class Tetris extends JFrame {

	private JLabel statusbar;
	private BlockHolder placeholder;
	private JButton start, end;
	private Board board;
	private PrintStream stdout = System.out;

	public Tetris() {
		setTitle("Tetris");
		setSize(222, 655);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		addKeyListener(new TAdapter());
		setFocusable(true);
		initUI();
	}

	public void reset() {
		getContentPane().removeAll();
		revalidate();
		initEnd();
	}

	private void initEnd() {
		end = new JButton();
		try {
			end.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("resources/endscreen.png"))));
		} catch (Exception ex) {System.out.println(ex);};
		end.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getContentPane().removeAll();
				revalidate();
				initUI();
			}
		});
		add(end);
		revalidate();
	}

	private void initUI() {
		start = new JButton();
		try {
			start.setIcon(new ImageIcon(ImageIO.read(getClass().getResource("resources/homescreen.png"))));
		} catch (Exception ex) {System.out.println(ex);};
		start.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				getContentPane().removeAll();
				revalidate();
				initGame();
			}
		});
		add(start);
		revalidate();
	}

	private Image resizeImage(Image srcImg, int w, int h){
		BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2 = resizedImg.createGraphics();

		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		g2.drawImage(srcImg, 0, 0, w, h, null);
		g2.dispose();

		return resizedImg;
	}

	private void initGame() {
		placeholder = new BlockHolder();
		add(placeholder, BorderLayout.SOUTH);

		statusbar = new JLabel("Score: 0");
		add(statusbar, BorderLayout.NORTH);

		board = new Board(this);
		add(board);
		board.start();

		board.requestFocus();

		pack();
		revalidate();
	}

	public JLabel getStatusBar() {
		return statusbar;
	}

	public BlockHolder getBlockHolder() {
		return placeholder;
	}

	public void openLink(URI uri) {
		Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
		if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
			try {
				desktop.browse(uri);
			} catch (Exception e) {};
		}
	}

	class TAdapter extends KeyAdapter {
		@Override
		public void keyPressed(KeyEvent e) {

			int keycode = e.getKeyCode();

			if(keycode == 'H') {
				try {
					openLink(new URL("https://github.com/SlowestLoris/Tetris/wiki").toURI());
				} catch (Exception err) {};
			} else if(keycode == 'T') {
				System.setOut(stdout);
				try {
					Scanner sc = new Scanner(new File("src/resources/highscores.txt"));
					String S = "These are the top scores:\n";
					while(sc.hasNextLine()) {
						String cur = sc.next();
						int score = sc.nextInt();
						S += cur + " " + score + "\n";
					}
					JOptionPane.showMessageDialog(null, S);
				} catch (Exception err) {System.out.println(err);};
			}
		}
	}

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			Tetris game = new Tetris();
			game.setVisible(true);
		});
	}


}