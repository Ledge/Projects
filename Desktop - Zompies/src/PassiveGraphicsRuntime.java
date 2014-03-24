import java.awt.*;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.RenderingHints;

public class PassiveGraphicsRuntime extends JPanel {

	private static final long serialVersionUID = 1L;
	Random rand = new Random();
	KeyboardInput keyboard;
	MouseInput mouse;
	JFrame frame;
	Runtime runtime;

	public static int GetScreenWorkingWidth() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}

	public static int GetScreenWorkingHeight() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}

	public PassiveGraphicsRuntime(int width, int height) {
		frame = new JFrame();
		frame.add(panel, BorderLayout.CENTER);
		frame.setSize(width, height);
		frame.setResizable(false);
		frame.isUndecorated();
		frame.setTitle("It's Just a Game");
		frame.setIgnoreRepaint(true);
		frame.setLocation(GetScreenWorkingWidth() / 2 - width / 2, GetScreenWorkingHeight() / 2 - height / 2);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		keyboard = new KeyboardInput();
		addKeyListener(keyboard);
		frame.addKeyListener(keyboard);

		mouse = new MouseInput(frame);
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		frame.addMouseListener(mouse);
		frame.addMouseMotionListener(mouse);

		runtime = new Runtime(frame, keyboard, mouse);
		runtime.startup();

		Thread renderer = new Thread(new RenderingLoop());
		//Thread updater = new Thread(new UpdateLoop());
		renderer.start();
		//updater.start();
	}

	public class RenderingLoop implements Runnable {
		public void run() {
			while (true) {
				panel.repaint();
				try {
					Thread.currentThread();
					Thread.sleep(16);
				} catch (InterruptedException ie) {
				}
			}
		}
	}

	JPanel panel = new JPanel() {
		private static final long serialVersionUID = 1L;

		public void paint(Graphics g) {
			Graphics2D g2d = (Graphics2D) g;
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			keyboard.poll();
			mouse.poll();

			int width = frame.getWidth();
			int height = frame.getHeight() - 30;

			runtime.renderer(g2d);
			g2d.setColor(Color.gray);
			g2d.setFont(new Font("Courier New", Font.PLAIN, 10));
			g2d.drawString("\u00a9 Rikard Legge 2013", width - 128, height - 4);

			g2d.dispose();
		}
	};
}