import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.util.*;

public class Runtime extends PassiveGraphicsRuntime {

	public Runtime(int width, int height, Map<String, String> settings) {
		super(width, height, settings);
	}

	private static final long serialVersionUID = 1L;

	int fps = 0;
	int frames = 0;
	long totalTime = 0;
	long curTime = System.currentTimeMillis();
	long lastTime = curTime;
	double timeOffset = 0;

	double xOffset;
	double yOffset;

	boolean displayDebug = true;
	boolean isPaused = false;

	double[][] background;
	int backgroundWidth;
	int backgroundSpacing;

	Entetie[] enteties;

	@Override
	public void init() {
		generateBackground(3000, 100);
		enteties = new Entetie[100];
		enteties[0] = new Player(this);
	}

	void inputListener() {
		if (keyboard.keyDown(KeyEvent.VK_W)) {
		} else if (keyboard.keyDown(KeyEvent.VK_S)) {
		} else {
		}

		if (keyboard.keyDown(KeyEvent.VK_A)) {
			enteties[0].moveDir[0] = -1;
		} else if (keyboard.keyDown(KeyEvent.VK_D)) {
			enteties[0].moveDir[0] = 1;
		} else {
			enteties[0].moveDir[0] = 0;
		}

		if (keyboard.keyDown(KeyEvent.VK_1)) {
		}
		if (keyboard.keyDown(KeyEvent.VK_2)) {
		}
		if (keyboard.keyDown(KeyEvent.VK_3)) {
		}
		if (keyboard.keyDown(KeyEvent.VK_4)) {
		}
		if (keyboard.keyDown(KeyEvent.VK_5)) {
		}
		if (keyboard.keyDown(KeyEvent.VK_6)) {
		}
		if (keyboard.keyDown(KeyEvent.VK_7)) {
		}
		if (keyboard.keyDown(KeyEvent.VK_8)) {
		}
		if (keyboard.keyDown(KeyEvent.VK_9)) {
		}

		if (keyboard.keyDownOnce(KeyEvent.VK_Z)) {
			if (displayDebug)
				displayDebug = false;
			else
				displayDebug = true;
		}
		if (keyboard.keyDownOnce(KeyEvent.VK_ESCAPE))
			isPaused = true;
		if (mouse.buttonDown(1)) {
		}
	}

	boolean UIHandeler(Graphics2D g2d) {

		if (keyboard.keyDownOnce(KeyEvent.VK_Q)) {
			window.setState(Frame.ICONIFIED);
			isPaused = true;
		}

		if (window.isFocused() == false)
			isPaused = true;

		if (isPaused) {
			g2d.setColor(Color.white);
			g2d.fill(new Rectangle(0, 0, width, height));
			if (keyboard.keyDownOnce(KeyEvent.VK_ESCAPE))
				isPaused = false;
			g2d.setColor(Color.black);
			g2d.setFont(new Font("Courier", Font.PLAIN, 50));
			g2d.drawString("Paused", 200, 230);
			g2d.setFont(new Font("Courier New", Font.PLAIN, 30));
			g2d.setColor(Color.gray);
			g2d.drawString(String.format("Press ESC to resume"), 150, 330);
			return true;
		}

		return false;
	}

	void generateBackground(int size, int spacing) {
		backgroundWidth = size;
		backgroundSpacing = spacing;
		background = new double[size / spacing * 2][9];
		int id = 0;
		for (int i = 0; i < backgroundWidth / backgroundSpacing; i++) {
			background[id][0] = backgroundSpacing * (i + 1) - backgroundWidth / 2;
			background[id][1] = -backgroundWidth / 2;
			background[id][2] = backgroundSpacing * (i + 1) - backgroundWidth / 2;
			background[id][3] = 0;
			background[id][4] = backgroundSpacing * (i + 1) - backgroundWidth / 2;
			background[id][5] = backgroundWidth;

			background[id][6] = rand.nextInt(3) - 1;
			background[id][7] = rand.nextInt(3) - 1;
			background[id][8] = 1;
			id++;
		}
		for (int i = 0; i < backgroundWidth / backgroundSpacing; i++) {
			background[id][0] = -backgroundWidth / 2;
			background[id][1] = backgroundSpacing * (i + 1) - backgroundWidth / 2;
			background[id][2] = 0;
			background[id][3] = backgroundSpacing * (i + 1) - backgroundWidth / 2;
			background[id][4] = backgroundWidth;
			background[id][5] = backgroundSpacing * (i + 1) - backgroundWidth / 2;

			background[id][6] = rand.nextInt(3) - 1;
			background[id][7] = rand.nextInt(3) - 1;
			background[id][8] = 1;
			id++;
		}
	}

	void renderBackground(Graphics2D g2d) {
		g2d.setColor(new Color(0, 0, 0, 30));
		for (int i = 0; i < background.length; i++) {
			if (background[i][8] == 1) {
				if (Math.round(rand.nextInt(200) * timeOffset) == 1)
					background[i][6] = (rand.nextInt(3) - 1);
				if (Math.round(rand.nextInt(200) * timeOffset) == 1)
					background[i][7] = (rand.nextInt(3) - 1);

				if (background[i][6] < 0)
					background[i][6] = background[i][6] - 0.01;
				else if (background[i][6] > 0)
					background[i][6] = background[i][6] + 0.01;

				if (background[i][7] < 0)
					background[i][7] = background[i][7] - 0.01;
				else if (background[i][7] > 0)
					background[i][7] = background[i][7] + 0.01;

				if (background[i][2] < background[i][0] - backgroundWidth * 3)
					background[i][6] = rand.nextInt(2);
				if (background[i][2] > background[i][0] + backgroundWidth * 3)
					background[i][6] = -rand.nextInt(2);

				if (background[i][3] < background[i][1] - backgroundWidth * 3)
					background[i][7] = rand.nextInt(2);
				if (background[i][3] > background[i][1] + backgroundWidth * 3)
					background[i][7] = -rand.nextInt(2);

				background[i][2] = background[i][2] + background[i][6] / timeOffset / 10;
				background[i][3] = background[i][3] + background[i][7] / timeOffset / 10;

				Path2D tmp = new Path2D.Double();
				tmp.moveTo(background[i][0], background[i][1]);
				tmp.curveTo(background[i][0], background[i][1], background[i][2], background[i][3], background[i][4], background[i][5]);
				g2d.draw(tmp);
			}
		}
	}

	void renderUI(Graphics2D g2d) {
		g2d.setFont(new Font("Courier New", Font.PLAIN, 12));
		g2d.setColor(Color.black);
		if (displayDebug)
			g2d.drawString(String.format("FPS: %s", fps), 10, 20);
	}

	private void renderEnteties(Graphics2D map) {
		for (int i = 0; i < enteties.length; i++) {
			if (enteties[i] != null) {
				enteties[i].render(map);
			}
		}
	}

	void calculateTimeOffset() {
		lastTime = curTime;
		curTime = System.currentTimeMillis();
		totalTime += curTime - lastTime;
		if (totalTime > 1000) {
			totalTime -= 1000;
			fps = frames;
			frames = 0;
		}
		++frames;

		if (curTime - lastTime > 1)
			timeOffset = 16.0 / (curTime - lastTime);
		if (timeOffset <= 0.001)
			timeOffset = 1;
	}

	@Override
	public void update() {
		calculateTimeOffset();
		inputListener();
		if (keyboard.keyDown(KeyEvent.VK_ESCAPE))
			return;
		for (int i = 0; i < enteties.length; i++) {
			if (enteties[i] != null) {
				enteties[i].update();
			}
		}
	}

	@Override
	public void renderer(Graphics2D g2d) {

		g2d.setColor(Color.white);
		g2d.fill(new Rectangle(0, 0, width, height));

		if (UIHandeler(g2d)) {
			renderBackground(g2d);
			return;
		}

		Graphics2D map = (Graphics2D) g2d.create();
		// xOffset = -player.body.getCenterX() + width / 2;
		// yOffset = -player.body.getCenterY() + height / 2;

		// if (-xOffset < bounds.getBounds2D().getX())
		// xOffset = -bounds.getBounds2D().getX();
		// else if (-xOffset > bounds.getBounds2D().getMaxX() - width)
		// xOffset = -bounds.getBounds2D().getMaxX() + width;

		// if (-yOffset < bounds.getBounds2D().getY())
		// yOffset = -bounds.getBounds2D().getY();
		// else if (-yOffset > bounds.getBounds2D().getMaxY() - height + 3)
		// yOffset = -bounds.getBounds2D().getMaxY() + height - 3;

		map.translate(xOffset, yOffset);
		renderBackground(map);
		renderEnteties(map);

		renderUI(g2d);
	}
}