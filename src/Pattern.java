import java.util.Random;
import processing.core.PApplet;
import processing.core.PVector;
import voronoi.Voronoi;
import voronoi.MPolygon;

public class Pattern {
	private static final float SHIFT_THRESHOLD = 0.005f;
	private Voronoi voronoi;
	private int color;
	private MPolygon[] voronoiRegions;
	private final int NUMPOINTS = 300;
	private float[][] voronoiPoints = new float[NUMPOINTS][2];
	private Random random = new Random();
	public float stroke = .02f;
	//constrols strokeweights
	float strokeControl = 1;
	boolean strokeIncrease = true;
	private PApplet app;
	
	private int[] colorStack;
	
	private final double THREADHOLD = 0.05f;
	
	public Pattern(PApplet app) {
		this.app = app;
		setUpVoronoi();
	}
	
	/**
	 * If there are 2 people, their joints attract voronoi points
	 * @param app
	 * @param person1
	 * @param person2
	 */
	public void drawTwoPeople(PApplet app, Person person1, Person person2) {
		if (person1 != null) {
			PVector[] j = person1.getAllJoints();
			for (int i = 0; i < j.length; i++) { //limits the number of joints that can be attached to - this prevents points being shifted constantly between 2 joints
				if (j[i] != null) {
					findClosestPoint(j[i]);
			}
		}
	}
		if (person2 != null) {
			PVector[] j = person2.getAllJoints();
			for (int i = 0; i < j.length; i++) { //limits the number of joints that can be attached to
				if (j[i] != null) {
					findClosestPoint(j[i]);
				}
			}
		}
		drawVoronoi(app);
	}
	
	public void drawOnePerson(PApplet app, Person person) {
		if (app.frameCount%10==0) {
			if (person != null) {
				PVector[] j = person.getAllJoints();
				for (int i = 0; i < j.length; i++) {
					if (j[i] != null) {
						pushClosestPoint(j[i]);
					}
				}
			}
		} 
		drawVoronoi(app);
	}
	
	public void resetVoronoi(PApplet app) {
		app.fill(255,255,255);
		app.stroke(0);
		app.strokeWeight(.02f);
		for(int i = 0; i < voronoiRegions.length; i++){
			voronoiRegions[i].setCoordis(voronoiRegions[i].getInitCoords());
			voronoiRegions[i].draw(app); // draw this shape
		}
	}
	
	public void drawNoBody(PApplet app) {
		drawVoronoi(app);
	}
	
	private void setUpVoronoi() {
		float minW = -2f;
		float maxW = 2f;
		float minH = -1.5f;
		float maxH = 1.5f;
		float randomPos;
		for (int i=0; i < NUMPOINTS; i++) {
			randomPos = minW + random.nextFloat() * (maxW - minW);
			voronoiPoints[i][0] = randomPos;
			randomPos = minH + random.nextFloat() * (maxH - minH);
			voronoiPoints[i][1] = randomPos;
		}
		voronoi = new Voronoi(voronoiPoints);
		voronoiRegions = voronoi.getRegions();
		colorStack = new int [voronoiRegions.length];
		for (int i = 0; i < colorStack.length; i++) {
			colorStack[i] = app.color(255,255,255);
		}
	}
	
	public void setStrokeWeight() {
		//stroke range: 0.05~0.21
		//strokecontrol range: 1~160 (0.01~1.6)(stroke weight range is 0.05~0.18)
		if (strokeIncrease==true) {
			strokeControl =(strokeControl+1);
		}
		else {
			strokeControl =strokeControl-1;
		}
		if (strokeControl>=160){
			strokeIncrease=false;
		}
		else if(strokeControl<=1){
			strokeIncrease=true;
		}
		this.stroke = (strokeControl*0.0001f+0.01f);
	}
	
	public void drawVoronoiRandom(PApplet app) {
		app.stroke(0);
		app.strokeWeight(stroke);
		for(int i = 0; i < voronoiRegions.length; i++){
			if(app.frameCount%15==0) {
			color = app.color(app.random(0, 255), app.random(0,255), app.random(0,255));
			colorStack[i] = color;
			}
			app.fill(colorStack[i]);
			voronoiRegions[i].draw(app); // draw this shape
		}
	}
	
	public void drawVoronoi(PApplet app) {
		app.fill(255,255,255);
		app.stroke(0);
		app.strokeWeight(stroke);
		for(int i = 0; i < voronoiRegions.length; i++){
			voronoiRegions[i].draw(app); // draw this shape
		}	
	}
	
	public void findClosestPoint(PVector vector) {
		for (MPolygon piece : voronoiRegions) {
			for (float[] point : piece.getCoords()) {
				if (computeDistance(point[0], point[1], vector.x, vector.y) < THREADHOLD) {
					point[0] = vector.x;
					point[1] = vector.y;
				}
			}
		}
	}
	
	public void pushClosestPoint (PVector vector) {
		for (MPolygon piece : voronoiRegions) {
			for (float[] point : piece.getCoords()) {
				if (computeDistance(point[0], point[1], vector.x, vector.y) < THREADHOLD) {
					//push it away
					if (point[0] - vector.x > 0) {
						point[0] = point[0] + SHIFT_THRESHOLD;
					} else if (point[0] - vector.x < 0) {
						point[0] = point[0] - SHIFT_THRESHOLD;
					}
					if (point[1] - vector.y > 0) {
						point[1] = point[1] + SHIFT_THRESHOLD;
					} else if (point[1] - vector.y < 0) {
						point[1] = point[1] - SHIFT_THRESHOLD;
					}
				}
			}
		}
	}
	
	public void resetPoint () {
		for (int i = 0; i < voronoiRegions.length; i++) {
			for (int j = 0; j<voronoiRegions[i].getCoords().length; j++) {
				float x = voronoiRegions[i].getCoords()[j][0];
				float y = voronoiRegions[i].getCoords()[j][1];
				float initX = voronoiRegions[i].getInitCoords()[j][0];
				float initY = voronoiRegions[i].getInitCoords()[j][1];
				if (x - initX > 0) {
					x = x - SHIFT_THRESHOLD;
				} else if (x - initX < 0) {
					x = x + SHIFT_THRESHOLD;
				}
				if (y - initY > 0) {
					y = y - SHIFT_THRESHOLD;
				} else if (y - initY < 0) {
					y = y + SHIFT_THRESHOLD;
				}
			}
		}
	}
	
	/**
	 * Compute the distance of 2 points
	 * @param firstX x coordinate of first point
	 * @param firstY y coordinate of first point
	 * @param secondX x coordinate of second point
	 * @param secondY y x coordinate of second point
	 * @return sum of square distance
	 */
	public float computeDistance (float firstX, float firstY, float secondX, float secondY) {
		return ((firstX - secondX)*(firstX-secondX) + (firstY - secondY)*(firstY - secondY));
	}
	
	/**
	 * Draw the pieces at the people's place
	 * @param app
	 * @param person1
	 * @param person2
	 */
	public void drawHodingTwoHands(PApplet app, Person person1, Person person2) {
		app.stroke(0);
		app.strokeWeight(.02f);
		for(int i = 0; i < voronoiRegions.length; i++){
			boolean contains = voronoiRegions[i].contains(person1.handLeft, person1.elbowLeft)
					|| voronoiRegions[i].contains(person1.shoulderLeft, person1.elbowLeft)
					|| voronoiRegions[i].contains(person1.shoulderLeft, person1.spine)
					|| voronoiRegions[i].contains(person1.hipLeft, person1.spine)
					|| voronoiRegions[i].contains(person1.hipLeft, person1.shoulderLeft)
					|| voronoiRegions[i].contains(person1.hipLeft, person1.kneeLeft)
					|| voronoiRegions[i].contains(person1.kneeLeft, person1.footLeft)
					
					|| voronoiRegions[i].contains(person1.handRight, person1.elbowRight)
					|| voronoiRegions[i].contains(person1.shoulderRight, person1.elbowRight)
					|| voronoiRegions[i].contains(person1.shoulderRight, person1.spine)
					|| voronoiRegions[i].contains(person1.hipRight, person1.spine)
					|| voronoiRegions[i].contains(person1.hipRight, person1.shoulderRight)
					|| voronoiRegions[i].contains(person1.hipRight, person1.kneeRight)
					|| voronoiRegions[i].contains(person1.kneeRight, person1.footRight)
					
					|| voronoiRegions[i].contains(person2.handLeft, person2.elbowLeft)
					|| voronoiRegions[i].contains(person2.shoulderLeft, person2.elbowLeft)
					|| voronoiRegions[i].contains(person2.shoulderLeft, person2.spine)
					|| voronoiRegions[i].contains(person2.hipLeft, person2.spine)
					|| voronoiRegions[i].contains(person2.hipLeft, person2.shoulderLeft)
					|| voronoiRegions[i].contains(person2.hipLeft, person2.kneeLeft)
					|| voronoiRegions[i].contains(person2.kneeLeft, person2.footLeft)
					
					|| voronoiRegions[i].contains(person2.handRight, person2.elbowRight)
					|| voronoiRegions[i].contains(person2.shoulderRight, person2.elbowRight)
					|| voronoiRegions[i].contains(person2.shoulderRight, person2.spine)
					|| voronoiRegions[i].contains(person2.hipRight, person2.spine)
					|| voronoiRegions[i].contains(person2.hipRight, person2.shoulderRight)
					|| voronoiRegions[i].contains(person2.hipRight, person2.kneeRight)
					|| voronoiRegions[i].contains(person2.kneeRight, person2.footRight);
			if (contains) {
				color = app.color(app.random(0, 255), app.random(0,255), app.random(0,255));
				app.fill(color);
			} else {
				app.fill(255,255,255);
			}
			voronoiRegions[i].draw(app); // draw this shape
		}
	}
}