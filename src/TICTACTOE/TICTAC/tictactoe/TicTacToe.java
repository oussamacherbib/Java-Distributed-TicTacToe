package TICTACTOE.TICTAC.tictactoe;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class TicTacToe implements Runnable {

	private JFrame frame;
	private Painter painter;
	private final int WIDTH = 505;
	private final int HEIGHT = 525;
	
	
	private BufferedImage cerclerouge;
	private BufferedImage cercleBleu;
	private String[] spaces = new String[9];
	private BufferedImage borde;
	private BufferedImage xrouge;
	private BufferedImage xbleu;


	private String attendre = "Attendre l'autre joueur";
	private String perdant = "Tu as perdu";
	private String gagnant = "Tu as gagné";
	private String terminé = "Le jeu a terminé";

	private int longueur = 160;
	private int err = 0;
	private int premierSpot = -1;
	private int deuxiemeSpot = -1;
	private int[][] commentGagné = new int[][] { { 0, 1, 2 }, { 3, 4, 5 }, { 6, 7, 8 }, { 0, 3, 6 }, { 1, 4, 7 }, { 2, 5, 8 }, { 0, 4, 8 }, { 2, 4, 6 } };


	private Font font = new Font("Verdana", Font.ITALIC, 32);
	private Font sf = new Font("Verdana", Font.ITALIC, 20);
	private Font lf = new Font("Verdana", Font.ITALIC, 50);



	

	private boolean montour = false;
	private boolean circle = true;
	private boolean accepté = false;
	private boolean unableToCommunicateWithOpponent = false;
	private boolean gain = false;
	private boolean perdue = false;
	private boolean completé = false;
	private String erreurdecommunication = "on peut pas communiquer avec l'autre joueur";
	
	
	

	private void importerImage() {
		try {
			xbleu = ImageIO.read(getClass().getResourceAsStream("/blueX.png"));
			cercleBleu = ImageIO.read(getClass().getResourceAsStream("/blueCircle.png"));
			borde = ImageIO.read(getClass().getResourceAsStream("/board.png"));
			xrouge = ImageIO.read(getClass().getResourceAsStream("/redX.png"));
			cerclerouge = ImageIO.read(getClass().getResourceAsStream("/redCircle.png"));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void gain() {
		for (int i = 0; i < commentGagné.length; i++) {
			if (circle) {
				if (spaces[commentGagné[i][0]] == "O" && spaces[commentGagné[i][1]] == "O" && spaces[commentGagné[i][2]] == "O") {
					premierSpot = commentGagné[i][0];
					deuxiemeSpot = commentGagné[i][2];
					gain = true;
				}
			} else {
				if (spaces[commentGagné[i][0]] == "X" && spaces[commentGagné[i][1]] == "X" && spaces[commentGagné[i][2]] == "X") {
					premierSpot = commentGagné[i][0];
					deuxiemeSpot = commentGagné[i][2];
					gain = true;
				}
			}
		}
	}

	private void perdre() {
		for (int i = 0; i < commentGagné.length; i++) {
			if (circle) {
				if (spaces[commentGagné[i][0]] == "X" && spaces[commentGagné[i][1]] == "X" && spaces[commentGagné[i][2]] == "X") {
					premierSpot = commentGagné[i][0];
					deuxiemeSpot = commentGagné[i][2];
					perdue = true;
				}
			} else {
				if (spaces[commentGagné[i][0]] == "O" && spaces[commentGagné[i][1]] == "O" && spaces[commentGagné[i][2]] == "O") {
					premierSpot = commentGagné[i][0];
					deuxiemeSpot = commentGagné[i][2];
					perdue = true;
				}
			}
		}
	}

	private void siTermine() {
		for (int i = 0; i < spaces.length; i++) {
			if (spaces[i] == null) {
				return;
			}
		}
		completé = true;
	}

	
	
	private void ihm(Graphics g) {
		g.drawImage(borde, 0, 0, null);
		if (unableToCommunicateWithOpponent) {
			g.setColor(Color.ORANGE);
			g.setFont(sf);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(erreurdecommunication);
			g.drawString(erreurdecommunication, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
			return;
		}

		if (accepté) {
			for (int i = 0; i < spaces.length; i++) {
				if (spaces[i] != null) {
					if (spaces[i].equals("X")) {
						if (circle) {
							g.drawImage(xrouge, (i % 3) * longueur + 10 * (i % 3), (int) (i / 3) * longueur + 10 * (int) (i / 3), null);
						} else {
							g.drawImage(xbleu, (i % 3) * longueur + 10 * (i % 3), (int) (i / 3) * longueur + 10 * (int) (i / 3), null);
						}
					} else if (spaces[i].equals("O")) {
						if (circle) {
							g.drawImage(cercleBleu, (i % 3) * longueur + 10 * (i % 3), (int) (i / 3) * longueur + 10 * (int) (i / 3), null);
						} else {
							g.drawImage(cerclerouge, (i % 3) * longueur + 10 * (i % 3), (int) (i / 3) * longueur + 10 * (int) (i / 3), null);
						}
					}
				}
			}
			if (gain || perdue) {
				Graphics2D g2 = (Graphics2D) g;
				g2.setStroke(new BasicStroke(10));
				g.setColor(Color.WHITE);
				g.drawLine(premierSpot % 3 * longueur + 10 * premierSpot % 3 + longueur / 2, (int) (premierSpot / 3) * longueur + 10 * (int) (premierSpot / 3) + longueur / 2, deuxiemeSpot % 3 * longueur + 10 * deuxiemeSpot % 3 + longueur / 2, (int) (deuxiemeSpot / 3) * longueur + 10 * (int) (deuxiemeSpot / 3) + longueur / 2);

				
				g.setFont(lf);
				if (gain) {
					g.setColor(Color.GREEN);
					int stringWidth = g2.getFontMetrics().stringWidth(gagnant);
					g.drawString(gagnant, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
				} else if (perdue) {
					g.setColor(Color.RED);
					int stringWidth = g2.getFontMetrics().stringWidth(perdant);
					g.drawString(perdant, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
				}
			}
			if (completé) {
				Graphics2D g2 = (Graphics2D) g;
				g.setColor(Color.RED);
				g.setFont(lf);
				int stringWidth = g2.getFontMetrics().stringWidth(terminé);
				g.drawString(terminé, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
			}
		} else {
			g.setColor(Color.RED);
			g.setFont(font);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			int stringWidth = g2.getFontMetrics().stringWidth(attendre);
			g.drawString(attendre, WIDTH / 2 - stringWidth / 2, HEIGHT / 2);
		}

	}
	private void tour() {
		if (err >= 10) unableToCommunicateWithOpponent = true;

		if (!montour && !unableToCommunicateWithOpponent) {
			try {
				int space = DataInputStream.readInt();
				if (circle) spaces[space] = "X";
				else spaces[space] = "O";
				perdre();
				siTermine();
				montour = true;
			} catch (IOException e) {
				e.printStackTrace();
				err++;
			}
		}
	}
	private class Painter extends JPanel implements MouseListener {

		public Painter() {
			setFocusable(true);
			requestFocus();
			setBackground(Color.DARK_GRAY);
			addMouseListener(this);
		}

		@Override
		public void paintComponent(Graphics g) {
			super.paintComponent(g);
			ihm(g);
		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (accepté) {
				if (montour && !unableToCommunicateWithOpponent && !gain && !perdue) {
					int x = e.getX() / longueur;
					int y = e.getY() / longueur;
					y *= 3;
					int position = x + y;

					if (spaces[position] == null) {
						if (!circle) spaces[position] = "X";
						else spaces[position] = "O";
						montour = false;
						repaint();
						Toolkit.getDefaultToolkit().sync();

						try {
							DataOutputStream.writeInt(position);
							DataOutputStream.flush();
						} catch (IOException e1) {
							err++;
							e1.printStackTrace();
						}

						System.out.println(" Données envoyées");
						gain();
						siTermine();

					}
				}
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {

		}

		@Override
		public void mouseReleased(MouseEvent e) {

		}

		@Override
		public void mouseEntered(MouseEvent e) {

		}

		@Override
		public void mouseExited(MouseEvent e) {

		}

	}

	
	public TicTacToe() {

		importerImage();

		painter = new Painter();
		painter.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		if (!connexionClient()) initServeur();

		th = new Thread(this, "TicTacToe");
		th.start();
		frame = new JFrame();
		frame.setResizable(false);
		frame.setVisible(true);
		frame.setTitle("Tic-Tac-Toe Game ");
		frame.setContentPane(painter);
		frame.setSize(WIDTH, HEIGHT);
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);


	}



	private Thread th;
	private Socket socket;
	private DataOutputStream DataOutputStream;
	private DataInputStream DataInputStream;
	private ServerSocket serverSocket;
	
	private void initServeur() {
		try {
			serverSocket = new ServerSocket(33333 );
		} catch (Exception e) {
			e.printStackTrace();
		}
		montour = true;
		circle = false;
	}
	
	private void connexionAvecServeur() {
		Socket socket = null;
		try {
			socket = serverSocket.accept();
			DataOutputStream = new DataOutputStream(socket.getOutputStream());
			DataInputStream = new DataInputStream(socket.getInputStream());
			accepté = true;
			System.out.println("Client veut rejoindre, serveur accepte");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean connexionClient() {
		try {
			socket = new Socket("localhost", 33333);
			DataOutputStream = new DataOutputStream(socket.getOutputStream());
			DataInputStream = new DataInputStream(socket.getInputStream());
			accepté = true;
		} catch (IOException e) {
			return false;
		}
		return true;
	}



	public void run() {
		while (true) {
			tour();
			painter.repaint();

			if (!circle && !accepté) {
				connexionAvecServeur();
			}

		}
	}
	

	public static void main(String[] args) {
		TicTacToe GAME = new TicTacToe();
	}

	}
