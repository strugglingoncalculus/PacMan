package last.connenction;

import java.net.*;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Client extends JPanel implements Runnable, KeyListener {
	static Socket socket;
	static DataInputStream in;
	static DataOutputStream out;

	int playerid;

	int[] x = new int[10];
	int[] y = new int[10];

	boolean left, down, right, up;

	int playerx;
	int playery;

	public void init() {
		
		try {
			System.out.println("Connencting...");
			socket = new Socket("localhost", 4444);
			System.out.println("Connection successful...");
			in = new DataInputStream(socket.getInputStream());
			playerid = in.readInt();
			out = new DataOutputStream(socket.getOutputStream());

			Input input = new Input(in, this);
			Thread thread = new Thread(input);
			thread.start();
			Thread thread2 = new Thread(this);
			thread2.start();
		} catch (Exception e) {
			System.out.println("Unable to start client");
		}
	}

	public void updateCoordinates(int pid, int x2, int y2) {
		this.x[pid] = x2;
		this.y[pid] = y2;
	}

	public void paint(Graphics g) {
		super.paint(g);
		for (int i = 0; i < 10; i++) {

			g.drawOval(x[i], y[i], 10, 10);
		}
		repaint();
	}

	@Override
	public void run() {
		while (true) {
			if (right == true) {
				playerx += 10;
			}
			if (left == true) {
				playerx -= 10;
			}
			if (down == true) {
				playery += 10;
			}
			if (up == true) {
				playery -= 10;
			}
			if (right || left || up || down) {
				try {
					out.writeInt(playerid);
					out.writeInt(playerx);
					out.writeInt(playery);
				} catch (Exception e) {
					System.out.println("Error sending the Coordinates");
				}
			}

			repaint();

			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {

	}

	@Override
	public void keyPressed(KeyEvent e) {
		
		if (e.getKeyCode() == 37) {
			left = true;
		}
		if (e.getKeyCode() == 38) {
			up = true;
		}
		if (e.getKeyCode() == 39) {
			right = true;
		}
		if (e.getKeyCode() == 40) {
			down = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == 37) {
			left = false;
		}
		if (e.getKeyCode() == 38) {
			up = false;
		}
		if (e.getKeyCode() == 39) {
			right = false;
		}
		if (e.getKeyCode() == 40) {
			down = false;
		}
	}

}

class Input implements Runnable {
	DataInputStream in;
	Client client;

	public Input(DataInputStream in, Client c) {
		this.in = in;
		this.client = c;
	}

	@Override
	public void run() {
		while (true) {
			try {
				int playerid = in.readInt();
				int x = in.readInt();
				int y = in.readInt();
				client.updateCoordinates(playerid, x, y);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
