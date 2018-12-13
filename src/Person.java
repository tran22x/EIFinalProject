import processing.core.PApplet;
import processing.core.PVector;

public class Person {
	Long id;
	PVector loc;
	int color;
	float radius;
	boolean validated = true;
	final double THRESHOLD = .2f;//threshold to check if the person is standing at a position
	
	public Person(Long id, PApplet app) {
		this.id = id;
		color = app.color(app.random(0, 255), 255, 255);
		radius = app.random(.1f, .3f);
	}
	
	public void setLocation(PVector loc) {
		this.loc = loc;
	}
	
	public void draw(PApplet app) {
		if (validated) { //different colours for validated and non validated ppl
			app.fill(color);
		}
		else {
			app.fill(255,0,0);
		}
		if (loc != null)
			app.ellipse(loc.x, loc.y, 0.08f, 0.08f);
		//System.out.println("Loc z: " + loc.z);
	}
	
	/**
	 * Method to check if the person is standing at a specific location
	 * @param v
	 */
	public boolean checkLocation(PVector v) {
		if (v != null && Math.abs(v.x - loc.x) < THRESHOLD && Math.abs(v.y - loc.y) < THRESHOLD) {
			return true;
		}
		return false;
	}
	

}
