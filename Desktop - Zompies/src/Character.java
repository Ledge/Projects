import java.awt.*;
import java.awt.geom.*;
import java.util.*;

class damageArc {
	RectangularShape body = null;
	double time;
	int alive = 500;

	damageArc(double x, double y, double w, double h, double s, double d) {
		body = new Arc2D.Double(x, y, w, h, s, d, 0);
		time = new Date().getTime();
	}
}

public class Character {

	class Shot {
		public double[] dir =
		{ 0, 0 };
		Rectangle2D body;
		double framesAlive = 0;
		int type;
		int sizeMod;
		double timeOfShot;
		Object[] shotInfo = new Object[10];

		Shot(int x, int y, double xDir, double yDir) {
			sizeMod = (Integer) weapon[currentWeapon][8];
			body = new Rectangle2D.Double(x - 5 * sizeMod, y - 5 * sizeMod, 10 * sizeMod, 10 * sizeMod);
			dir[0] = xDir;
			dir[1] = yDir;
			shotInfo = weapon[currentWeapon].clone();
			timeOfShot = new Date().getTime();
		}

		boolean checkIfHit(Character target) {
			if (body == null || target.isDead == true)
				return true;
			double targetX = target.body.getCenterX();
			double targetY = target.body.getCenterY();
			double bodyX = body.getCenterX();
			double bodyY = body.getCenterY();
			if (Math.pow(targetX - bodyX, 2) + Math.pow(targetY - bodyY, 2) < Math.pow(body.getWidth() / 2 + target.body.getWidth() / 2, 2)) {
				if (target.damage((Integer) shotInfo[6] * sizeMod, bodyX, bodyY) == true)
					score = score + 1;

				newPealOff(Shot.this, target);
				if ((Integer) shotInfo[7] <= 1) {
					body = null;
					return true;
				} else {
					shotInfo[7] = (Integer) shotInfo[7] - 1;
				}
			}
			return false;
		}

		public void Move() {
			if (new Date().getTime() - timeOfShot > (Integer) shotInfo[4]) {
				body = null;
				return;
			}
			body.setFrame(body.getX() + dir[0] * (Integer) shotInfo[5] / delta, body.getY() + dir[1] * (Integer) shotInfo[5] / delta, body.getWidth(),
					body.getHeight());
		}
	}

	class PealOff {
		Object[][] particles = new Object[10][3];
		double created;
		int timeAlive = 400;
		double rotation = 0;

		PealOff(Shot shot, Character target) {
			for (int i = 0; i < particles.length; i++) {
				particles[i][0] = new Rectangle2D.Double(shot.body.getCenterX(), shot.body.getCenterY(), 8, 8);
				particles[i][1] = shot.dir[0];
				particles[i][2] = shot.dir[1];

				created = System.nanoTime();
			}
		}

		boolean render(Graphics2D map) {
			double diff = (System.nanoTime() - created) / 1000 / 1000;
			if (diff > timeAlive)
				return true;
			map.setColor(new Color(210, 210, 210, (int) (255 - (diff / timeAlive * 250))));
			Stroke oldStroke = map.getStroke();
			map.setStroke(new BasicStroke(0.5f));
			for (int i = 0; i < particles.length; i++) {
				Rectangle2D tmp = (Rectangle2D) particles[i][0];
				double xD = -(Double) particles[i][1] + (rand.nextInt(12) - 6);
				double yD = -(Double) particles[i][2] + (rand.nextInt(12) - 6);
				tmp.setFrame((double) tmp.getX() + xD / delta, (double) tmp.getY() + yD / delta, tmp.getWidth(), tmp.getHeight());
				rotation = rotation + 1;
				map.rotate(rotation / delta, tmp.getCenterX(), tmp.getCenterY());
				map.draw(tmp);
				map.rotate(-rotation / delta, tmp.getCenterX(), tmp.getCenterY());
			}
			map.setStroke(oldStroke);
			return false;
		}
	}

	Random rand = new Random();
	int currentWeapon;
	int health = 100;
	int originHealth;
	int score = 0;
	double lastShot;
	double[] moveDir =
	{ 0, 0 };
	double delta = 1;
	boolean isDead = false;
	String id;
	int rotation;
	int viewDistance = 500;
	int movementDistortion = 20;
	RectangularShape body;
	Shot[] shots = new Shot[1000];
	PealOff[] pealoff = new PealOff[30];
	damageArc[] arcs = new damageArc[20];
	Object[][] weapon = new Object[20][14];
	Point2D messageLocation;
	String message = "";
	long messageCreated = 0;
	int messageTime = 0;
	boolean[] availableWeapons = new boolean[weapon.length];
	boolean[] unlockedWeapons = new boolean[availableWeapons.length];

	boolean paused = false;
	boolean useOuterTurret = false;

	// 1 2 3 4 5 6 7 8 9
	// 0 delay
	// 1 b/s
	// 2 accuracy
	// 3 color
	// 4 alive
	// 5 speed
	// 6 damage
	// 7 hits
	// 8 size
	// 9 type
	// 10 currentLevel
	// 11 upgradable
	// 12 upgrade type
	// 13 name

	/* Init */

	Character(Shape shape, String id, int currentWeapon) {
		initWeapons();
		unlockWeapon(currentWeapon, true);
		SetWeapon(currentWeapon);
		body = (RectangularShape) shape;
		this.id = id;
	}

	void setSpawnHealth(int amt) {
		originHealth = amt;
		health = amt;
	}

	void newMessage(String msg, int time) {
		message = msg;
		messageTime = time;
		messageCreated = System.nanoTime();
		messageLocation = new Point2D.Double(body.getCenterX(), body.getCenterY());
	}

	/* On Update */

	void Update(double offsetx, double offsety, double delta) {
		this.delta = delta;
		if (id != "player") {
			double size = Math.sqrt(health) * 5;
			if (size < 20)
				size = 20;
			double speed = 7200 / size / 60;
			body.setFrame(body.getX() + moveDir[0] / delta * speed, body.getY() + moveDir[1] / delta * speed, size, size);
		} else
			body.setFrame(body.getX() + moveDir[0] / delta * 2, body.getY() + moveDir[1] / delta * 2, body.getWidth(), body.getHeight());
	}

	void Render(Graphics2D map, Character[] targets) {
		Color currentColor = null;
		if (message != "") {
			long counting = (System.nanoTime() - messageCreated) / 1000 / 1000 + 1;
			if (messageTime > counting) {
				Font font = new Font("Courier New", Font.PLAIN, 16);
				FontMetrics textMetrics = map.getFontMetrics(font);

				int centeredX = ((int) messageLocation.getX() - (textMetrics.stringWidth(message) / 2));
				int centeredY = (int) messageLocation.getY() - (int) (body.getHeight() * 0.7);

				map.setFont(font);
				double alpha = (double) counting / (double) messageTime;
				map.setColor(new Color(100, 100, 100, (int) (255 - alpha * 255)));
				map.drawString(message, centeredX, centeredY);
			}
		}

		double curTime = new Date().getTime();
		for (int i = 0; i < arcs.length; i++) {
			if (arcs[i] != null) {
				arcs[i].body.setFrame(body.getX(), body.getY(), body.getWidth(), body.getHeight());
				int alpha = (int) (100 - (curTime - arcs[i].time) / arcs[i].alive * 90);
				if (alpha <= 0)
					alpha = 1;
				map.setColor(new Color(255, 0, 0, alpha));
				// map.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND,
				// BasicStroke.JOIN_ROUND));
				map.draw(arcs[i].body);
				// map.setStroke(new BasicStroke(1, BasicStroke.CAP_ROUND,
				// BasicStroke.JOIN_ROUND));
				if (arcs[i].time <= curTime - arcs[i].alive)
					arcs[i] = null;
			}
		}

		for (int i = 0; i < pealoff.length; i++) {
			if (pealoff[i] != null)
				if (pealoff[i].render(map))
					pealoff[i] = null;

		}

		for (int i = 0; i < shots.length; i++) {
			if (shots[i] != null) {
				if (shots[i].body == null) {
					shots[i] = null;
				} else {
					if (currentColor != (Color) shots[i].shotInfo[3]) {
						map.setColor((Color) shots[i].shotInfo[3]);
						currentColor = (Color) shots[i].shotInfo[3];
					}
					map.draw(shots[i].body);
					shots[i].Move();
					for (int j = 0; j < targets.length; j++) {
						if (targets[j] != null && targets[j].id != id)
							if (targets[j].body == null) {
								targets[j] = null;
							} else if (shots[i].checkIfHit(targets[j])) {
								shots[i] = null;
								break;
							}
					}
				}
			}
		}
	}

	void Folow(Character target) {
		if (paused)
			return;
		if (Math.pow(target.body.getCenterX() - body.getCenterX(), 2) + Math.pow(target.body.getCenterY() - body.getCenterY(), 2) < Math.pow(viewDistance * 2
				+ health / 50, 2)) {
			if (useOuterTurret && Math.pow(target.body.getCenterX() - body.getCenterX(), 2) + Math.pow(target.body.getCenterY() - body.getCenterY(), 2) < Math.pow(
					body.getWidth() / 2 + target.body.getWidth() / 2, 2)) {
				if (weapon[currentWeapon][9] == "range") {
					Shoot(target.body.getCenterX() + rand.nextInt(50) - 25, target.body.getCenterY() + rand.nextInt(50) - 25);
				} else if (Math.pow(target.body.getCenterX() - body.getCenterX(), 2) + Math.pow(target.body.getCenterY() - body.getCenterY(), 2) < Math.pow(
						target.body.getWidth() / 2 + body.getWidth() / 2, 2)) {
					weapon[currentWeapon][8] = (int) body.getWidth() / 25;
					Shoot(target.body.getCenterX(), target.body.getCenterY());
				}
				moveDir[0] = 0;
				moveDir[1] = 0;
				return;
			}
			double x = target.body.getCenterX() + (rand.nextInt(movementDistortion) - movementDistortion / 2);
			double y = target.body.getCenterY() + (rand.nextInt(movementDistortion) - movementDistortion / 2);
			double scale1 = (body.getCenterX() - x) / (body.getCenterY() - y);
			double scale2 = (body.getCenterY() - y) / (body.getCenterX() - x);

			if (scale1 > 1)
				scale1 = 1;
			if (scale2 > 1)
				scale2 = 1;

			if (scale1 < -1)
				scale1 = -1;
			if (scale2 < -1)
				scale2 = -1;

			if (y <= body.getCenterY())
				scale1 = -scale1;

			if (x <= body.getCenterX())
				scale2 = -scale2;

			moveDir[0] = scale1;
			moveDir[1] = scale2;

			if (Math.pow(target.body.getCenterX() - body.getCenterX(), 2) + Math.pow(target.body.getCenterY() - body.getCenterY(), 2) < Math.pow(
					viewDistance / 1.5, 2)) {
				if (weapon[currentWeapon][9] == "range") {
					Shoot(target.body.getCenterX() + rand.nextInt(50) - 25, target.body.getCenterY() + rand.nextInt(50) - 25);
				} else if (Math.pow(target.body.getCenterX() - body.getCenterX(), 2) + Math.pow(target.body.getCenterY() - body.getCenterY(), 2) < Math.pow(
						target.body.getWidth() / 2 + body.getWidth() / 2, 2)) {
					weapon[currentWeapon][8] = (int) body.getWidth() / 25;
					Shoot(target.body.getCenterX(), target.body.getCenterY());
				}
			}

		} else {
			int ran = rand.nextInt(100);
			if (ran == 1)
				moveDir[0] = 1;
			else if (ran == 2)
				moveDir[0] = -1;
			else if (ran == 3)
				moveDir[0] = 0;

			ran = rand.nextInt(100);
			if (ran == 1)
				moveDir[1] = 1;
			else if (ran == 2)
				moveDir[1] = -1;
			else if (ran == 3)
				moveDir[1] = 0;
		}
	}

	/* Weapon */

	void initWeapons() {
		newWeapon(0, 500, 1, 1, Color.black, 50, 10, 100, 1, 2, "melee", false, 1, false, 0, "Melee");

		newWeapon(1, 10, 1, 5, Color.darkGray, 750, 10, 15, 1, 1, "range", true, 1, true, 1, "Machine gun");
		newWeapon(2, 10, 10, 30, Color.red, 500, 6, 2, 1, 1, "range", true, 1, true, 2, "Flame thrower");
		newWeapon(3, 500, 40, 30, Color.gray, 500, 10, 20, 1, 1, "range", true, 1, true, 3, "Shotgun");
		newWeapon(4, 1000, 5, 1, Color.gray, 10000, 0, 5, 200, 3, "range", true, 1, true, 4, "Land bomb");

		newWeapon(19, 1, 300, 999, Color.green, 1000, 10, 100, 10, 1, "range", false, 1, false, 0, "Alien bomb");
	}

	void newWeapon(int id, int d, int bs, int ac, Color co, int al, int sp, int dmg, int hi, int si, String ty, boolean av, int level, boolean upg, int upgt,
			String name) {
		weapon[id][0] = d;
		weapon[id][1] = bs;
		weapon[id][2] = ac;
		weapon[id][3] = co;
		weapon[id][4] = al;
		weapon[id][5] = sp;
		weapon[id][6] = dmg;
		weapon[id][7] = hi;
		weapon[id][8] = si;
		weapon[id][9] = ty;
		weapon[id][10] = level;
		weapon[id][11] = upg;
		weapon[id][12] = upgt;
		weapon[id][13] = name;

		availableWeapons[id] = av;
	}

	void SetWeapon(int id) {
		if (weapon.length > id && (Integer) weapon[id][0] != null && unlockedWeapons[id] == true)
			currentWeapon = id;
	}

	void upgradeWeapon(int id, int amt) {
		Integer damage = 0;
		Integer shots = 0;
		Integer hits = 0;
		for (int i = 0; i < amt; i++) {
			damage = (Integer) weapon[id][6];
			shots = (Integer) weapon[id][1];
			hits = (Integer) weapon[id][7];
			switch ((Integer) weapon[id][12]) {
			case 1:
				weapon[id][6] = (int) Math.round(damage * 1.5);
				break;
			case 2:
				weapon[id][1] = (int) Math.round(shots * 1.3);
				weapon[id][6] = (int) Math.round(damage * 1.2 + 1);
				break;
			case 3:
				weapon[id][1] = (int) Math.round(shots * 1.3);
				weapon[id][6] = (int) Math.round(damage * 1.2);
				break;
			case 4:
				weapon[id][6] = (int) Math.round(damage * 1.3);
				weapon[id][7] = (int) Math.round(hits * 1.2);
				break;
			}
		}
		weapon[id][10] = (Integer) weapon[id][10] + amt;
	}

	void Shoot(double x, double y) {
		double time = new Date().getTime();
		if (time - lastShot >= (Integer) weapon[currentWeapon][0]) {
			lastShot = time;
			y = y - 20;
			double scale1 = (body.getCenterX() - x) / (body.getCenterY() - y);
			double scale2 = (body.getCenterY() - y) / (body.getCenterX() - x);

			int xPos = (int) body.getCenterX();
			int yPos = (int) body.getCenterY();

			if (scale1 > 1)
				scale1 = 1;
			if (scale2 > 1)
				scale2 = 1;

			if (scale1 < -1)
				scale1 = -1;
			if (scale2 < -1)
				scale2 = -1;
			
			if (useOuterTurret) {
				double tmp1 = scale1, tmp2 = scale2;
				if (y <= body.getCenterY())
					tmp1 = -scale1;

				if (x <= body.getCenterX())
					tmp2 = -scale2;

				double deg = -Math.atan2(tmp1, tmp2) + Math.PI / 2;
				xPos = (int) (body.getCenterX() + body.getWidth() / 2 * Math.cos(deg));
				yPos = (int) (body.getCenterY() + body.getWidth() / 2 * Math.sin(deg));
			}
			for (int i = 0; i < (Integer) weapon[currentWeapon][1]; i++) {

				double tmp1 = scale1 + ((double) rand.nextInt((Integer) weapon[currentWeapon][2]) - (Integer) weapon[currentWeapon][2] / 2) / 50;
				double tmp2 = scale2 + ((double) rand.nextInt((Integer) weapon[currentWeapon][2]) - (Integer) weapon[currentWeapon][2] / 2) / 50;

				if (y <= body.getCenterY())
					tmp1 = -tmp1;

				if (x <= body.getCenterX())
					tmp2 = -tmp2;

				for (int j = 0; j < shots.length; j++) {
					if (shots[j] == null) {
						shots[j] = new Shot(xPos, yPos, tmp1, tmp2);
						break;
					}
				}
			}
		}
	}

	void unlockWeapon(int id, boolean unlock) {
		unlockedWeapons[id] = unlock;
	}

	boolean damage(int amt, double tX, double tY) {
		health = (int) (health - amt);
		if (id != "player") {
			if (health <= 0) {
				isDead = true;
				return true;
			}
			body.setFrame(body.getX() + Math.sqrt(amt) / 20, body.getY() + Math.sqrt(amt) / 20, body.getWidth(), body.getHeight());
		}
		double deg = Math.atan2(body.getCenterY() - tY, body.getCenterX() - tX) * 180 / Math.PI;
		deg = -deg + 180;
		double size = (double) (100 / (Math.pow(body.getWidth() / 2, 2) * Math.PI) * 100 * 36);
		newDamageArc(body.getX(), body.getY(), body.getWidth(), body.getHeight(), deg - size / 2, size);
		return false;
	}

	boolean damage(int amt) {
		health = (int) (health - amt);
		if (id != "player") {
			if (health <= 0) {
				isDead = true;
				return true;
			}
			body.setFrame(body.getX() + Math.sqrt(amt) / 20, body.getY() + Math.sqrt(amt) / 20, body.getWidth(), body.getHeight());
		}
		return false;
	}

	void newPealOff(Shot shot, Character target) {
		double oldest = System.nanoTime();
		// int old = 0;
		for (int i = 0; i < pealoff.length; i++) {
			if (pealoff[i] == null) {
				pealoff[i] = new PealOff(shot, target);
				break;
			}
			if (oldest > pealoff[i].created) {
				// old = i;
				oldest = pealoff[i].created;
			}
		}
		// pealoff[old] = new PealOff(shot, target);
	}

	void newDamageArc(double x, double y, double w, double h, double s, double d) {
		double oldestT = new Date().getTime();
		int oldest = 0;
		for (int i = 1; i < arcs.length; i++) {
			if (arcs[i] == null) {
				arcs[i] = new damageArc(x, y, w, h, s, d);
				return;
			} else if (arcs[i].time < oldestT) {
				oldest = i;
				oldestT = arcs[i].time;
			}
		}
		arcs[oldest] = new damageArc(x, y, w, h, s, d);
	}

	/* */
}