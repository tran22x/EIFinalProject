import processing.core.PApplet;
import processing.core.PVector;

public class Person {
	Long id;
	PVector loc;
	int color;
	float radius;
	
	public Person(Long id, PApplet app) {
		this.id = id;
		color = app.color(app.random(0, 255), 255, 255);
		radius = app.random(.1f, .3f);
	}
	
	public void setLocation(PVector loc) {
		this.loc = loc;
	}
	
	public void draw(PApplet app) {
		app.fill(color);
		app.ellipse(loc.x, loc.y, radius*2, radius*2);
		
	}

}
