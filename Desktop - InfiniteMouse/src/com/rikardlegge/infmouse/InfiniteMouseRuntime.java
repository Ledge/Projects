package com.rikardlegge.infmouse;

import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;

public class InfiniteMouseRuntime {
	static int sleepTime;
	static int boundLeft;
	static int boundRight;
	static boolean debug;
	static Robot robot;
	static Point position;
	static long deltaTime;

	public static void main(String[] args) {

		debug = false;
		sleepTime = 100;
		boundLeft = -1200;
		boundRight = 1200+1920+1920-1;

		try {
			
			robot = new Robot();
			
			for (int i = 0; i < args.length; i++) {
				String arg = args[i];
				switch (arg) {
				case "-debug":
					debug = true;
					break;
				case "-sleep":
					sleepTime = Integer.parseInt(args[i + 1]);
					break;
				case "-left":
					boundLeft = Integer.parseInt(args[i + 1]);
					break;
				case "-right":
					boundRight = Integer.parseInt(args[i + 1]);
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		while (true) {
			try {
				position = MouseInfo.getPointerInfo().getLocation();
				if (position.x == boundLeft)
					robot.mouseMove(boundRight - 1, position.y);
				else if (position.x == boundRight)
					robot.mouseMove(boundLeft + 1, position.y);
				else if (position.y <= 1920-1080-10)
					if (position.x == -1)
						robot.mouseMove(1920*2, position.y);
					else if (position.x == 1920*2+1)
						robot.mouseMove(-2, position.y);

				if (debug) {
					deltaTime = System.nanoTime();
					MouseInfo.getPointerInfo().getLocation();
					System.out.println("(" + position.x + ", " + position.y + ") ... " + (System.nanoTime() - deltaTime) * 0.000001 + " milliseconds");
				}

				Thread.sleep(sleepTime);
			} catch (InterruptedException e) {
				e.printStackTrace();
				System.exit(-2);
			}
		}
	}
}