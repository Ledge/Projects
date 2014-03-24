import java.awt.*;
import java.awt.geom.*;
import java.util.Random;

public class Entetie {

	Runtime parent;
	RectangularShape body;
	Random rand = new Random();
	double[] moveDir = new double[2];
	
	public Entetie(Runtime parent){
		this.parent = parent;
		init();
	}
	
	void init(){
		
	}
	
	public void update() {

	}

	public void render(Graphics2D map) {
		
	}
}
