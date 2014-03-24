import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Date;

public class Item {
	double created;
	int dir = 1;
	int type;
	double x;
	double y;

	Ellipse2D body;

	Item(double x, double y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
		created = new Date().getTime();
		body = new Ellipse2D.Double(x, y, 25, 25);
	}

	void Runtime(Graphics2D map, Character player, double delta) {
		if (type == 1)
			map.setColor(Color.red);
		else if (type == 2)
			map.setColor(Color.green);
		else if (type == 3)
			map.setColor(Color.blue);
		map.draw(body);

		double speed = dir / delta / 3;
		body.setFrame(x - body.getWidth() / 2, y - body.getHeight() / 2, body.getWidth() + speed, body.getHeight() + speed);

		if (new Date().getTime() - created > 12000)
			dir = -1;
		else if (body.getWidth() > 30) {
			dir = -1;
		} else if (body.getWidth() < 20) {
			dir = 1;
		}

		if (Math.abs(body.getCenterX() - player.body.getCenterX()) < Math.abs(body.getWidth() / 2 + player.body.getWidth() / 2)) {
			if (Math.abs(body.getCenterY() - player.body.getCenterY()) < Math.abs(body.getHeight() / 2 + player.body.getHeight() / 2)) {
				if (type == 1)
					player.damage(-1000);
				if (type == 2) {
					int w = player.currentWeapon;
					player.unlockWeapon(19, true);
					player.SetWeapon(19);
					player.Shoot(player.body.getCenterX() - 1, player.body.getCenterY() + 1);
					player.unlockWeapon(19, false);
					player.SetWeapon(w);
				}
				if (type == 3) {
					boolean[] tmp = player.unlockedWeapons;
					boolean upgraded = false;
					for (int i = 0; i < tmp.length; i++) {
						if (tmp[i] == false && player.availableWeapons[i] == true) {
							player.unlockWeapon(i, true);
							upgraded = true;
							player.newMessage("New Weapon: "+player.weapon[i][13]+" <"+i+">", 3000);
							break;
						}
					}
					if (upgraded == false) {
						int toUpgrade = 0;
						int curLev = 100;
						for (int i = 0; i < player.weapon.length; i++) {
							if (player.weapon[i][11] != null && (Boolean) player.weapon[i][11] == true) {
								if ((Integer) player.weapon[i][10] < curLev) {
									curLev = (Integer) player.weapon[i][10];
									toUpgrade = i;
								}
							}
						}
						if(curLev < 5){
							player.upgradeWeapon(toUpgrade, 1);
							player.newMessage("Upgraded the "+player.weapon[toUpgrade][13]+" to level "+(curLev+1), 2000);
						}
					}
				}
				body = null;
				return;
			}
		}

		if (body.getWidth() <= 1)
			body = null;
	}
}
