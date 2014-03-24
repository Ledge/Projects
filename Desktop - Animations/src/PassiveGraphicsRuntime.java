import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class PassiveGraphicsRuntime extends JPanel {

	private static final long serialVersionUID = 1L;
	Map<String, String> settings;
	Random rand = new Random();
	KeyboardInput keyboard;
	MouseInput mouse;
	JFrame window;

	int width;
	int height;

	public static int GetScreenWorkingWidth() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}

	public static int GetScreenWorkingHeight() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}

	public void init() {

	}

	public void update() {

	}

	public void renderer(Graphics2D g2d) {

	}

	public PassiveGraphicsRuntime(int width, int height, Map<String, String> settings) {
		this.width = width;
		this.height = height;
		this.settings = settings;

		window = new JFrame();
		window.add(panel, BorderLayout.CENTER);
		window.setSize(width, height);
		window.setResizable(false);
		window.isUndecorated();
		window.setTitle("It's Just a Game");
		window.setIgnoreRepaint(true);
		window.setLocation(GetScreenWorkingWidth() / 2 - width / 2, GetScreenWorkingHeight() / 2 - height / 2);
		window.setVisible(true);

		keyboard = new KeyboardInput();
		addKeyListener(keyboard);
		window.addKeyListener(keyboard);

		mouse = new MouseInput(window);
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		window.addMouseListener(mouse);
		window.addMouseMotionListener(mouse);

		init();

		Thread t = new Thread(new MainLoop());
		t.start();
	}

	private class MainLoop implements Runnable {
		public MainLoop() {
		}

		@Override
		public void run() {
			while (true) {
				update();
				panel.repaint();
				try {
					Thread.currentThread();
					Thread.sleep(16);
				} catch (InterruptedException ie) {
				}

				// Thread.yield();
			}
		}
	}

	JPanel panel = new JPanel() {
		private static final long serialVersionUID = 1L;

		@Override
		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			keyboard.poll();
			mouse.poll();

			width = window.getWidth();
			height = window.getHeight() - 20;

			renderer(g2d);

			g2d.setColor(Color.gray);
			g2d.setFont(new Font("Courier New", Font.PLAIN, 10));
			g2d.drawString("\u00a9 Rikard Legge 2013", width - 128, height - 4);

			g2d.dispose();
		}
	};
}