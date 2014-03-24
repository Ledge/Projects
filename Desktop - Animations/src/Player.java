import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RectangularShape;

public class Player extends Entetie {

	RectangularShape[] feet;
	RectangularShape[] hands = new RectangularShape[2];
	double[][] feetLocation;
	double virticleMovement;
	RectangularShape[] debug;
	RectangularShape head;

	public Player(Runtime parent) {
		super(parent);
	}

	private void updateFoot(double[] foot) {
		double x = 0, y = 0;

		int nextFoot;
		if (foot[9] == 0) {
			nextFoot = 1;
		} else {
			nextFoot = 0;
		}

		if (foot[6] == 1) {
			double dir;
			if (body.getX() - foot[0] + foot[9] * body.getWidth() > 0)
				dir = -1;
			else
				dir = 1;
			if (Math.abs(body.getX() - foot[0] + foot[9] * body.getWidth()) < 5) {
				dir = dir * 1;
			}

			if (Math.abs(body.getX() - foot[0] + foot[9] * body.getWidth()) > 120) {
				foot[7] = 120 * dir;
			//	foot[10] = 0.1;
			} else {
				if(foot[8] == 0){
					foot[8] = foot[7];
				}
				if (foot[8] - Math.abs(body.getX() - foot[0] + foot[9] * body.getWidth()) * dir > 2) {
					foot[7] = foot[7] + 2 * dir;
			//		foot[10] = 0.075;
				} else {
					foot[7] = Math.abs(body.getX() - foot[0] + foot[9] * body.getWidth()) * dir;
			//		foot[10] = 0.05;
				}
			}
			foot[8] = foot[7];

			x = foot[2] - foot[7] + foot[7] * Math.cos(-foot[5]);
			y = foot[3] + Math.abs(foot[7]) / 2 * Math.sin(-foot[5]);
			foot[0] = x;
			foot[1] = y;
			foot[5] = foot[5] + foot[10] / parent.timeOffset;
		} else {
			return;
		}

		if (foot[5] > Math.PI) {
			foot[5] = 0;
			foot[6] = 0;
			foot[8] = 0;

			feetLocation[nextFoot][6] = 1;
			feetLocation[nextFoot][2] = feetLocation[nextFoot][0];// feetLocation[nextFoot][2]
																	// - foot[7]
																	// * 2 *
																	// foot[8];
			// feetLocation[nextFoot][3] = feetLocation[nextFoot][1];
		}
		feet[(int) foot[9]].setFrame(x - feet[0].getWidth() / 2, y - feet[0].getHeight() / 2, feet[0].getWidth(), feet[0].getHeight());

		for (int i = 0; i < debug.length; i++) {
			if (debug[i] == null) {
				// debug[i] = new Rectangle2D.Double(x, y, 2, 2);
				break;
			}
		}
	}

	private void createFoot(int id) {
		feetLocation[id][0] = parent.width / 2; // Current x
		feetLocation[id][1] = 200; // Current y
		feetLocation[id][2] = parent.width / 2; // goal x
		feetLocation[id][3] = 200; // goal y
		feetLocation[id][4] = 5; // Cirviture
		feetLocation[id][5] = 0; // curve value
		feetLocation[id][6] = 0; // isActive
		feetLocation[id][7] = 60; // speed
		feetLocation[id][8] = 0; // speed cache
		feetLocation[id][9] = id; // id
		feetLocation[id][10] = 0.1; // Curve inc.
	}

	@Override
	void init() {
		feet = new RectangularShape[2];
		feetLocation = new double[2][15];
		virticleMovement = .5;
		debug = new RectangularShape[1000];

		head = new Rectangle2D.Double(parent.width / 2 - 20, parent.height - 400, 40, 40);
		body = new Rectangle2D.Double(parent.width / 2 - 50, parent.height - 350, 50, 50);
		feet[0] = new Rectangle2D.Double(100, 100, 20, 20);
		feet[1] = new Rectangle2D.Double(100, 100, 20, 20);

		createFoot(0);
		createFoot(1);
		feetLocation[0][6] = 1;
	}

	@Override
	public void update() {
		updateFoot(feetLocation[0]);
		updateFoot(feetLocation[1]);
		body.setFrame(body.getX() + moveDir[0], body.getY(), body.getWidth(), body.getHeight());
		head.setFrame(body.getX() + 5 + moveDir[0] * 10, head.getY(), head.getWidth(), head.getHeight());
	}

	@Override
	public void render(Graphics2D map) {
		map.setColor(Color.black);
		map.draw(body);
		map.draw(feet[0]);
		map.draw(feet[1]);
		map.draw(head);

		for (int i = 0; i < debug.length; i++) {
			if (debug[i] != null) {
				map.draw(debug[i]);
			}
		}
	}
}
