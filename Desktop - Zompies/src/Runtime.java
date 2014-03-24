import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.geom.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class Runtime {

	class CExplode {
		Object[][] particles = new Object[30][3];
		double created;
		int timeAlive = 1000;

		CExplode(double x, double y) {
			for (int i = 0; i < particles.length; i++) {
				// particles[i][0] = new Ellipse2D.Double(x, y, 10, 10);
				particles[i][0] = new Arc2D.Double(x - 25, y - 25, 50, 50, 360 / particles.length * i, 360 / particles.length * 2, 0);
				particles[i][1] = rand.nextDouble() + rand.nextInt(3) - 1;
				particles[i][2] = rand.nextDouble() + rand.nextInt(3) - 1;

				created = System.nanoTime();
			}
		}

		boolean render(Graphics2D map) {
			double diff = (System.nanoTime() - created) / 1000 / 1000;
			if (diff > timeAlive)
				return true;
			map.setColor(new Color(100, 100, 100, (int) (255 - (diff / timeAlive * 250))));
			for (int i = 0; i < particles.length; i++) {
				RectangularShape tmp = (RectangularShape) particles[i][0];
				double xD = (Double) particles[i][1];
				double yD = (Double) particles[i][2];
				tmp.setFrame((double) tmp.getX() + xD / delta, (double) tmp.getY() + yD / delta, tmp.getWidth(), tmp.getHeight());
				map.draw(tmp);
			}
			return false;
		}
	}

	JFrame window;
	Canvas canvas;
	Random rand = new Random();
	KeyboardInput keyboard;
	MouseInput mouse;

	int fps = 0;
	int frames = 0;
	long totalTime = 0;
	long curTime = System.currentTimeMillis();
	long lastTime = curTime;
	double delta;
	boolean newHighscore = false;
	int oldHighScore = 0;
	boolean cheated = false;
	boolean showDebug = false;

	int height;
	int width;
	double xOffset;
	double yOffset;
	boolean removeMode = true;

	int difficulty;
	boolean paused = false;
	boolean firstStartup = true;
	int startupPage = 1;
	Character player;
	double[][] background = new double[50][9];
	int backgroundWidth;
	RectangularShape bounds;
	CExplode[] characterExplosions = new CExplode[10];
	Item[] items = new Item[10];
	Character[] characters = new Character[50];

	@SuppressWarnings("deprecation")
	Runtime(JFrame app, KeyboardInput keyboard, MouseInput mouse) {
		window = app;
		window.setCursor(Cursor.CROSSHAIR_CURSOR);
		this.mouse = mouse;
		this.keyboard = keyboard;

		height = window.getHeight() - 20;
		width = window.getWidth();
	}

	void newCExplosion(double x, double y) {
		for (int i = 0; i < characterExplosions.length; i++) {
			if (characterExplosions[i] == null) {
				characterExplosions[i] = new CExplode(x, y);
				break;
			}
		}
	}

	void spawnEnemie(double x, double y, int size, int weapon) {
		for (int i = 0; i < characters.length; i++) {
			if (characters[i] == null) {
				characters[i] = new Character(new Ellipse2D.Double(x, y, 1, 1), "enemie", weapon);
				characters[i].setSpawnHealth(size);
				break;
			}
		}
	}

	void spawnBounds(int size) {
		bounds = new Rectangle2D.Double(-size, -size, size * 2, size * 2);
		generateBackground(size);
	}

	void spawnItem(double x, double y, int type) {
		for (int i = 0; i < items.length; i++) {
			if (items[i] == null) {
				items[i] = new Item(x, y, type);
				double[] pos = checkIfInsideBounds(items[i]);
				items[i].body.setFrame(pos[0], pos[1], items[i].body.getWidth(), items[i].body.getHeight());
				//items[i].x = pos[0] + rand.nextInt(100) + items[i].body.getWidth() / 2;
				//items[i].y = pos[1] + rand.nextInt(100) + items[i].body.getHeight() / 2;
				return;
			}
		}
	}

	void restart() {
		for (int i = 0; i < characters.length; i++) {
			characters[i] = null;
		}
		for (int i = 0; i < items.length; i++) {
			items[i] = null;
		}
		startup();
	}

	void startup() {
		cheated = false;
		spawnBounds(1000);
		characters[0] = new Character(new Ellipse2D.Double(width / 2 - 25, height / 2 - 25, 50, 50), "player", 1);
		characters[0].health = 10000;
		difficulty = 1;
		player = characters[0];
		newHighscore = false;
	}

	void inputListener() {
		if (keyboard.keyDown(KeyEvent.VK_W))
			player.moveDir[1] = -1;
		else if (keyboard.keyDown(KeyEvent.VK_S))
			player.moveDir[1] = 1;
		else
			player.moveDir[1] = 0;

		if (keyboard.keyDown(KeyEvent.VK_A))
			player.moveDir[0] = -1;
		else if (keyboard.keyDown(KeyEvent.VK_D))
			player.moveDir[0] = 1;
		else
			player.moveDir[0] = 0;

		if (keyboard.keyDown(KeyEvent.VK_1))
			player.SetWeapon(1);
		if (keyboard.keyDown(KeyEvent.VK_2))
			player.SetWeapon(2);
		if (keyboard.keyDown(KeyEvent.VK_3))
			player.SetWeapon(3);
		if (keyboard.keyDown(KeyEvent.VK_4))
			player.SetWeapon(4);
		if (keyboard.keyDown(KeyEvent.VK_5))
			player.SetWeapon(5);
		if (keyboard.keyDown(KeyEvent.VK_6))
			player.SetWeapon(6);
		if (keyboard.keyDown(KeyEvent.VK_7))
			player.SetWeapon(7);
		if (keyboard.keyDown(KeyEvent.VK_8))
			player.SetWeapon(8);
		if (keyboard.keyDown(KeyEvent.VK_9))
			player.SetWeapon(9);

		if (keyboard.keyDownOnce(KeyEvent.VK_Z)) {
			if (showDebug)
				showDebug = false;
			else
				showDebug = true;
		}
		if (keyboard.keyDownOnce(KeyEvent.VK_F9)) {
			cheated = true;
			player.health = 1000000;
		}
		if (keyboard.keyDownOnce(KeyEvent.VK_F10)) {
			cheated = true;
			player.unlockWeapon(2, true);
			player.unlockWeapon(3, true);
			player.unlockWeapon(4, true);
		}
		if (keyboard.keyDownOnce(KeyEvent.VK_F11)) {
			cheated = true;
			player.upgradeWeapon(1, 1);
			player.upgradeWeapon(2, 1);
			player.upgradeWeapon(3, 1);
			player.upgradeWeapon(4, 1);
		}
		if (keyboard.keyDown(KeyEvent.VK_F12)) {
			cheated = true;
			player.score = (int) (player.score + 1 / delta);
		}

		if (keyboard.keyDownOnce(KeyEvent.VK_MINUS))
			if (difficulty < 19)
				difficulty = difficulty + 1;
		if (keyboard.keyDownOnce(KeyEvent.VK_BACK_SLASH))
			if (difficulty > 1)
				difficulty = difficulty - 1;
		if (keyboard.keyDownOnce(KeyEvent.VK_ESCAPE))
			paused = true;
		if (keyboard.keyDownOnce(KeyEvent.VK_M))
			if (removeMode)
				removeMode = false;
			else
				removeMode = true;

		if (mouse.buttonDown(1))
			player.Shoot(mouse.getPosition().getX() - xOffset, mouse.getPosition().getY() - yOffset);

	}

	void itemHandeler(Graphics2D map) {
		boolean tmp = false;
		if (rand.nextInt((int) (300 * delta + difficulty * 40)) <= 1 && tmp) {
			int x = rand.nextInt(4) - 2;
			int y = rand.nextInt(4) - 2;
			int type = 1;
			if (x == 0 && y == 0) {
				x = 0;
				y = 1;
			}
			if (rand.nextInt(10) == 1) {
				type = 2;
			}
			if (rand.nextInt(5) == 1)
				for (int i = 0; i < player.weapon.length; i++)
					if (player.weapon[i][11] != null && (Boolean) player.weapon[i][11] == true)
						if ((Integer) player.weapon[i][10] < 5)
							type = 3;

			spawnItem(player.body.getCenterX() + 200 * x, player.body.getCenterY() + 200 * y, type);
		}

		for (int i = 1; i < items.length; i++) {
			if (items[i] != null) {
				if (items[i].body == null) {
					items[i] = null;
				} else {
					items[i].Runtime(map, player, delta);
				}
			}
		}
	}

	void enemieHandeler() {
		int ran = (int) ((70 - difficulty) * delta);
		if (ran <= 0)
			return;
		if (rand.nextInt(ran) == 1) {
			int x = rand.nextInt(2);
			int y = rand.nextInt(2);
			int size = rand.nextInt(1000) + 50;
			if (x == 0 && y == 0)
				if (rand.nextBoolean())
					x = rand.nextInt(4) + 1;
				else
					y = rand.nextInt(4) + 1;

			if (rand.nextBoolean())
				x = -x;
			if (rand.nextBoolean())
				y = -y;
			size = size * (rand.nextInt(difficulty) + 1);

			x = (int) (x * 400 + player.body.getCenterX());
			y = (int) (y * 400 + player.body.getCenterY());

			int type = 0;

			if (rand.nextInt(60 / difficulty) <= 1)
				type = rand.nextInt(3) + 1;

			spawnEnemie(x, y, size, type);
		}
	}

	double[] checkIfInsideBounds(Character target) {
		double[] targetFixInsideBounds = new double[2];
		targetFixInsideBounds[0] = target.body.getX();
		targetFixInsideBounds[1] = target.body.getY();
		if (target.body.getX() < bounds.getBounds2D().getX())
			targetFixInsideBounds[0] = bounds.getBounds2D().getX();
		else if (target.body.getX() > bounds.getBounds2D().getMaxX() - target.body.getWidth())
			targetFixInsideBounds[0] = bounds.getBounds2D().getMaxX() - target.body.getWidth();

		if (target.body.getY() < bounds.getBounds2D().getY())
			targetFixInsideBounds[1] = bounds.getBounds2D().getY();
		else if (target.body.getY() > bounds.getBounds2D().getMaxY() - target.body.getHeight())
			targetFixInsideBounds[1] = bounds.getBounds2D().getMaxY() - target.body.getHeight();
		return targetFixInsideBounds;
	}

	double[] checkIfInsideBounds(Item target) {
		double[] targetFixInsideBounds = new double[2];
		targetFixInsideBounds[0] = target.body.getX();
		targetFixInsideBounds[1] = target.body.getY();
		if (target.body.getX() < bounds.getBounds2D().getX())
			targetFixInsideBounds[0] = bounds.getBounds2D().getX();
		else if (target.body.getX() > bounds.getBounds2D().getMaxX() - target.body.getWidth())
			targetFixInsideBounds[0] = bounds.getBounds2D().getMaxX() - target.body.getWidth();

		if (target.body.getY() < bounds.getBounds2D().getY())
			targetFixInsideBounds[1] = bounds.getBounds2D().getY();
		else if (target.body.getY() > bounds.getBounds2D().getMaxY() - target.body.getHeight())
			targetFixInsideBounds[1] = bounds.getBounds2D().getMaxY() - target.body.getHeight();
		return targetFixInsideBounds;
	}

	void characterHandeler(Graphics2D map) {
		for (int i = 0; i < characterExplosions.length; i++) {
			if (characterExplosions[i] != null) {
				if (characterExplosions[i].render(map))
					characterExplosions[i] = null;
			}
		}
		for (int i = 0; i < characters.length; i++) {
			if (characters[i] != null) {
				if (characters[i].isDead == true) {
					newCExplosion(characters[i].body.getCenterX(), characters[i].body.getCenterY());
					if(rand.nextInt(5) == 1){
						int type = 1;
						if (rand.nextInt(10) == 1) {
							type = 2;
						}
						if (rand.nextInt(4) == 1)
							for (int j = 0; j < player.weapon.length; j++)
								if (player.weapon[j][11] != null && (Boolean) player.weapon[j][11] == true)
									if ((Integer) player.weapon[j][10] < 5)
										type = 3;
						spawnItem(characters[i].body.getCenterX(), characters[i].body.getCenterY(), type);
					}
					characters[i] = null;
				} else {
					if (characters[i].id != "player") {
						characters[i].Folow(player);
						double[] pos = checkIfInsideBounds(characters[i]);
						characters[i].body.setFrame(pos[0], pos[1], characters[i].body.getWidth(), characters[i].body.getHeight());
						characters[i].Update(xOffset, yOffset, delta);
					}
					characters[i].Render(map, characters);
					if (Math.pow(characters[i].body.getCenterX() - characters[i].body.getCenterX(), 2)
							+ Math.pow(player.body.getCenterY() - player.body.getCenterY(), 2) < Math.pow(width * 1.5, 2)) {
						map.setColor(Color.darkGray);
						map.draw(characters[i].body);
					}
				}
			}
		}
	}

	boolean setHighscore() {
		int high = 0;
		boolean newh = false;

		String path = "";
		if (System.getProperty("os.name").toLowerCase().indexOf("mac") >= 0)
			path = new JFileChooser().getFileSystemView().getDefaultDirectory().toString() + "/Library/Application Support/ByRikard/IJAG";
		else
			path = System.getenv("APPDATA") + "/ByRikard/IJAG";
		String highscorePath = path + "/highscore";

		try {
			FileInputStream fstream = new FileInputStream(highscorePath);
			DataInputStream in = new DataInputStream(fstream);
			BufferedReader br = new BufferedReader(new InputStreamReader(in));
			high = Integer.parseInt(br.readLine());
			in.close();
		} catch (Exception e) {
		}
		oldHighScore = high;
		if (high < player.score) {
			high = player.score;
			newh = true;
		}

		try {
			String content = "" + high;
			File file = new File(highscorePath);

			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(content);
			bw.close();

		} catch (IOException e) {
		}
		return newh;
	}

	void playerHandeler() {

		inputListener();

		if (player.health <= 0) {
			player.isDead = true;
			if (cheated == false)
				newHighscore = setHighscore();
		}

		player.Update(xOffset, yOffset, delta);
		double[] pos = checkIfInsideBounds(player);
		player.body.setFrame(pos[0], pos[1], player.body.getWidth(), player.body.getHeight());

		int levelupDif = 10;
		double val = levelupDif;
		for (int i = 0; i < difficulty; i++) {
			val = val + val / levelupDif + levelupDif;
		}
		if (player.score > val) {
			if (difficulty < 19) {
				difficulty = difficulty + 1;

				int toUpgrade = 0;
				int curLev = 100;
				for (int i = 0; i < player.weapon.length; i++) {
					if (player.weapon[i][11] != null && (Boolean) player.weapon[i][11] == true && player.unlockedWeapons[i] == true) {
						if ((Integer) player.weapon[i][10] < curLev) {
							curLev = (Integer) player.weapon[i][10];
							toUpgrade = i;
						}
					}
				}
				if (curLev < 5 && curLev < difficulty / 2) {
					player.upgradeWeapon(toUpgrade, 1);
					player.newMessage("Level up! The " + player.weapon[toUpgrade][13] + " was uppgraded to level " + (curLev + 1), 2000);
				}
			}
		}
	}

	boolean eventHandeler(Graphics2D g2d) {

		if (keyboard.keyDownOnce(KeyEvent.VK_Q)) {
			window.setState(Frame.ICONIFIED);
			paused = true;
		}

		if (firstStartup) {
			g2d.setColor(Color.white);
			g2d.fill(new Rectangle(0, 0, width, height));

			g2d.setFont(new Font("Courier New", Font.PLAIN, 20));
			g2d.setColor(Color.black);
			switch (startupPage) {
			case 1:
				if (keyboard.keyDownOnce(KeyEvent.VK_SPACE))
					startupPage = 2;

				g2d.drawString("The goal of this game is to kill as", 100, 170);
				g2d.drawString("many *zombies* as possible. You are", 100, 200);
				g2d.drawString("given weapons and power-ups to help", 100, 230);
				g2d.drawString("you succeed. Good luck.", 100, 260);

				g2d.setColor(Color.gray);
				g2d.drawString("Press SPACE to continue", 150, 330);
				g2d.drawString("Ø O O", 280, 290);

				g2d.setColor(Color.black);
				g2d.setFont(new Font("Courier New", Font.PLAIN, 34));
				g2d.drawString("Story", 265, 110);
				break;
			case 2:
				if (keyboard.keyDownOnce(KeyEvent.VK_SPACE))
					startupPage = 3;

				g2d.drawString("Use A,S,D,W to stear", 100, 170);
				g2d.drawString("Use 1,2,3,4 to change weapon", 100, 200);
				g2d.drawString("Use the MOUSE to aim", 100, 230);
				g2d.drawString("Use the LEFT MOUSE BUTTON to fire", 100, 260);

				g2d.setColor(Color.gray);
				g2d.drawString("Press SPACE to continue", 150, 330);
				g2d.drawString("O Ø O", 280, 290);

				g2d.setColor(Color.black);
				g2d.setFont(new Font("Courier New", Font.PLAIN, 34));
				g2d.drawString("Controlls", 230, 110);
				break;
			case 3:
				if (keyboard.keyDownOnce(KeyEvent.VK_SPACE))
					firstStartup = false;

				g2d.setColor(Color.black);
				g2d.draw(new Ellipse2D.Double(100, 170 - 15, 20, 20));
				g2d.setColor(Color.red);
				g2d.draw(new Ellipse2D.Double(100, 200 - 15, 20, 20));
				g2d.setColor(Color.blue);
				g2d.draw(new Ellipse2D.Double(100, 230 - 15, 20, 20));
				g2d.setColor(Color.green);
				g2d.draw(new Ellipse2D.Double(100, 260 - 15, 20, 20));

				g2d.setColor(Color.black);
				g2d.drawString("Is you", 130, 170);
				g2d.drawString("Heals you", 130, 200);
				g2d.drawString("Unlocks / upgrades a weapon", 130, 230);
				g2d.drawString("Causes an explosion around you", 130, 260);

				g2d.setColor(Color.gray);
				g2d.drawString("Press SPACE to start", 150, 330);
				g2d.drawString("O O Ø", 280, 290);

				g2d.setColor(Color.black);
				g2d.setFont(new Font("Courier New", Font.PLAIN, 34));
				g2d.drawString("Power-ups", 230, 110);
				break;
			}
			return true;
		}

		if (player.isDead) {
			g2d.setColor(Color.white);
			g2d.fill(new Rectangle(0, 0, width, height));
			if (keyboard.keyDownOnce(KeyEvent.VK_R))
				restart();

			if (newHighscore) {
				int x = 20;
				int y = 130;
				g2d.rotate(-.4, x, y);
				g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, 40));
				g2d.setColor(Color.black);
				Stroke oldStroke = g2d.getStroke();
				float dash[] =
				{ 40.0f };
				g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, dash, 40.0f));
				g2d.drawRoundRect(x, y, 320, 75, 20, 20);
				g2d.setColor(Color.red);
				g2d.drawString(String.format("New Highscore"), x + 20, y + 50);
				g2d.rotate(.4, x, y);
				g2d.setStroke(oldStroke);

				g2d.setColor(Color.lightGray);
				g2d.setFont(new Font("Courier", Font.PLAIN, 20));
				g2d.drawString("Your record was " + oldHighScore + " kills", 210, 260);
			} else if (cheated) {
				int x = 20;
				int y = 130;
				g2d.rotate(-.4, x, y);
				g2d.setFont(new Font("Comic Sans MS", Font.PLAIN, 40));
				g2d.setColor(Color.black);
				Stroke oldStroke = g2d.getStroke();
				float dash[] =
				{ 40.0f };
				g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 0.0f, dash, 40.0f));
				g2d.drawRoundRect(x, y, 270, 75, 20, 20);
				g2d.setColor(Color.red);
				g2d.drawString(String.format("You cheated"), x + 20, y + 50);
				g2d.rotate(.4, x, y);
				g2d.setStroke(oldStroke);

				g2d.setColor(Color.lightGray);
				g2d.setFont(new Font("Courier", Font.PLAIN, 20));
				g2d.drawString("Highscore not saved", 210, 260);
			} else {
				g2d.setColor(Color.lightGray);
				g2d.setFont(new Font("Courier", Font.PLAIN, 20));
				g2d.drawString("Your record is " + oldHighScore + " kills", 210, 260);
			}

			g2d.setColor(Color.black);
			g2d.setFont(new Font("Courier", Font.PLAIN, 50));

			if (player.score == 1)
				g2d.drawString(String.format(player.score + " kill"), 200, 230);
			else
				g2d.drawString(String.format(player.score + " kills"), 200, 230);
			g2d.setFont(new Font("Courier New", Font.PLAIN, 30));
			g2d.setColor(Color.gray);
			g2d.drawString(String.format("Press R to restart"), 150, 330);
			return true;
		}

		if (window.isFocused() == false)
			paused = true;

		if (paused) {
			g2d.setColor(Color.white);
			g2d.fill(new Rectangle(0, 0, width, height));
			if (keyboard.keyDownOnce(KeyEvent.VK_ESCAPE))
				paused = false;
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

	void generateBackground(int size) {
		backgroundWidth = 100;
		int spacing = backgroundWidth;
		int id = 0;
		for (int i = 0; i < bounds.getWidth() / spacing; i++) {
			background[id][0] = spacing * (i + 1) - bounds.getWidth() / 2;
			background[id][1] = -bounds.getHeight() / 2;
			background[id][2] = spacing * (i + 1) - bounds.getWidth() / 2;
			background[id][3] = 0;
			background[id][4] = spacing * (i + 1) - bounds.getWidth() / 2;
			background[id][5] = bounds.getHeight();

			background[id][6] = rand.nextInt(3) - 1;
			background[id][7] = rand.nextInt(3) - 1;
			background[id][8] = 1;
			id++;
		}
		for (int i = 0; i < bounds.getHeight() / spacing; i++) {
			background[id][0] = -bounds.getHeight() / 2;
			background[id][1] = spacing * (i + 1) - bounds.getHeight() / 2;
			background[id][2] = 0;
			background[id][3] = spacing * (i + 1) - bounds.getWidth() / 2;
			background[id][4] = bounds.getWidth();
			background[id][5] = spacing * (i + 1) - bounds.getHeight() / 2;

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
				if (Math.round(rand.nextInt(200) * delta) == 1)
					background[i][6] = (rand.nextInt(3) - 1);
				if (Math.round(rand.nextInt(200) * delta) == 1)
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

				background[i][2] = background[i][2] + background[i][6] / delta / 10;
				background[i][3] = background[i][3] + background[i][7] / delta / 10;

				Path2D tmp = new Path2D.Double();
				tmp.moveTo(background[i][0], background[i][1]);
				tmp.curveTo(background[i][0], background[i][1], background[i][2], background[i][3], background[i][4], background[i][5]);
				g2d.draw(tmp);
			}
		}
	}

	void renderUI(Graphics2D g2d) {
		g2d.setFont(new Font("Courier New", Font.PLAIN, 12));
		g2d.setColor(Color.white);
		if (removeMode)
			g2d.setColor(Color.black);
		if (player.health < 0)
			player.health = 0;
		g2d.drawString(String.format("Helath: %s", player.health), 10, 20);
		g2d.drawString(String.format("Kills: %s", player.score), 10, 40);
		int WLevel = (Integer) player.weapon[player.currentWeapon][10];
		if (WLevel == 5)
			g2d.drawString(String.format("Current Weapon: %s:Max", player.currentWeapon), 10, 60);
		else
			g2d.drawString(String.format("Current Weapon: %s:%s", player.currentWeapon, WLevel), 10, 60);
		g2d.setColor(Color.gray);
		g2d.drawString(String.format("Level: %s", difficulty), 10, 80);
		g2d.setColor(Color.black);
		if (showDebug)
			g2d.drawString(String.format("FPS: %s", fps), 10, 100);
		if (cheated)
			g2d.drawString("You cheated, no highscore will be saved", 200, height - 15);
	}

	void renderer(Graphics2D g2d) {
		height = window.getHeight() - 20;
		width = window.getWidth();

		lastTime = curTime;
		curTime = System.currentTimeMillis();
		totalTime += curTime - lastTime;
		if (totalTime > 1000) {
			totalTime -= 1000;
			fps = frames;
			frames = 0;
		}
		++frames;

		delta = 16.0 / (curTime - lastTime);
		if (delta < 0)
			delta = 0;

		g2d.setColor(Color.white);
		if (removeMode)
			g2d.fill(new Rectangle(0, 0, width, height));

		if (eventHandeler(g2d)) {
			renderBackground(g2d);
			return;
		}

		Graphics2D map = (Graphics2D) g2d.create();
		playerHandeler();
		xOffset = -player.body.getCenterX() + width / 2;
		yOffset = -player.body.getCenterY() + height / 2;

		if (-xOffset < bounds.getBounds2D().getX())
			xOffset = -bounds.getBounds2D().getX();
		else if (-xOffset > bounds.getBounds2D().getMaxX() - width)
			xOffset = -bounds.getBounds2D().getMaxX() + width;

		if (-yOffset < bounds.getBounds2D().getY())
			yOffset = -bounds.getBounds2D().getY();
		else if (-yOffset > bounds.getBounds2D().getMaxY() - height + 3)
			yOffset = -bounds.getBounds2D().getMaxY() + height - 3;

		map.translate(xOffset, yOffset);

		renderBackground(map);
		itemHandeler(map);
		enemieHandeler();
		characterHandeler(map);

		map.setColor(Color.black);
		map.draw(bounds);

		renderUI(g2d);
	}
}