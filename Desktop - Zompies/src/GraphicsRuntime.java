import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
//import java.awt.image.BufferedImage;

import javax.swing.JFrame;

public class GraphicsRuntime extends JFrame {

	private static final long serialVersionUID = 1L;
	KeyboardInput keyboard;
	MouseInput mouse;

	public static int GetScreenWorkingWidth() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().width;
	}

	public static int GetScreenWorkingHeight() {
		return java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds().height;
	}

	GraphicsRuntime(int width, int height) {
		// Create game window...
		JFrame app = new JFrame();
		app.setIgnoreRepaint(true);
		app.setResizable(false);
		app.setLocation(GetScreenWorkingWidth() / 2 - width / 2, GetScreenWorkingHeight() / 2 - height / 2);
		app.setTitle("It's Just a Game");

		// Create canvas for painting...
		Canvas canvas = new Canvas();
		canvas.setIgnoreRepaint(true);
		canvas.setSize(width, height);

		// Add canvas to game window...
		app.add(canvas);
		app.pack();
		app.setVisible(true);

		if (app.getWidth() < 100) {
			app.removeAll();
			new WindowEvent(this, WindowEvent.WINDOW_CLOSING);
			app = null;
			return;
		}

		app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		requestFocus();

		keyboard = new KeyboardInput();
		addKeyListener(keyboard);
		canvas.addKeyListener(keyboard);

		mouse = new MouseInput(canvas);
		addMouseListener(mouse);
		addMouseMotionListener(mouse);
		canvas.addMouseListener(mouse);
		canvas.addMouseMotionListener(mouse);

		canvas.createBufferStrategy(3);
		BufferStrategy bs = canvas.getBufferStrategy();

		// Create BackBuffer...
		// canvas.createBufferStrategy(2);
		// BufferStrategy buffer = canvas.getBufferStrategy();

		// Get graphics configuration...
		// GraphicsEnvironment ge =
		// GraphicsEnvironment.getLocalGraphicsEnvironment();
		// GraphicsDevice gd = ge.getDefaultScreenDevice();
		// GraphicsConfiguration gc = gd.getDefaultConfiguration();

		// Create off-screen drawing surface
		// BufferedImage bi = gc.createCompatibleImage(GetScreenWorkingWidth(),
		// GetScreenWorkingHeight());
		// BufferedImage bi = gc.createCompatibleImage(width, height);

		// Objects needed for rendering...
		Graphics graphics = null;
		Graphics2D g2d = null;

		Runtime runtime = new Runtime(app, keyboard, mouse);
		runtime.startup();
		
		while (true) {
			try {

				keyboard.poll();
				mouse.poll();

				width = app.getWidth();
				height = app.getHeight() - 30;

				// clear back buffer...
				// g2d = bi.createGraphics();
				// g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				// RenderingHints.VALUE_ANTIALIAS_ON);

					g2d = (Graphics2D) bs.getDrawGraphics();
					g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

				runtime.renderer(g2d);
				g2d.setColor(Color.gray);
				g2d.setFont(new Font("Courier New", Font.PLAIN, 10));
				g2d.drawString("\u00a9 Rikard Legge 2013", width - 128, height - 4);

				bs.show();
				Toolkit.getDefaultToolkit().sync();
				g2d.dispose();

				// Blit image and flip...
				// graphics = buffer.getDrawGraphics();
				// graphics.drawImage(bi, 0, 0, null);

				// if (!buffer.contentsLost())
				// buffer.show();

				// Let the OS have a little time...*/
				/*
				 * try { Thread.currentThread().sleep(1); } catch
				 * (InterruptedException ie) { }
				 */

				Thread.yield();
			} finally {
				// release resources
				if (graphics != null)
					graphics.dispose();
				if (g2d != null)
					g2d.dispose();
			}
		}
	}

	public void paint(Graphics g) {

	}
}
