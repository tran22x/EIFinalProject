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
	private final int NUMPOINTS = 1000;
	private float[][] voronoiPoints = new float[NUMPOINTS][2];
	private Random random = new Random();
	public float stroke = .02f;
	
	private final double THREADHOLD = 0.05f;
	
	public Pattern() {
		setUpVoronoi();
	}
	
	public void drawTwoPeople(PApplet app, Person person1, Person person2) {
		if (person1 != null) {
			//for (PVector joint : person.getAllJoints()) {
			PVector[] j = person1.getAllJoints();
			int jointDetected = 5;
			for (int i = 0; i < j.length; i++) { //limits the number of joints that can be attached to - this prevents points being shifted constantly between 2 joints
				if (j[i] != null && jointDetected > 0) {
						findClosestPoint(j[i]);
						jointDetected--; 
			}
		}
	}
		if (person2 != null) {
			//for (PVector joint : person.getAllJoints()) {
			PVector[] j = person2.getAllJoints();
			int jointDetected = 5;
			for (int i = 0; i < j.length; i++) { //limits the number of joints that can be attached to
				if (j[i] != null && jointDetected > 0) {
						findClosestPoint(j[i]);
						jointDetected--;
			}
			}
		}
		drawVoronoi(app);
	}
	
	public void drawOnePerson(PApplet app, Person person) {
		if (person != null) {
			PVector[] j = person.getAllJoints();
			for (int i = 0; i < j.length; i++) {
				if (j[i] != null) {
						pushClosestPoint(j[i], i);
			}
			drawVoronoi(app);
			}
		}
	}
	
	public void resetVoronoi(PApplet app) {
		setUpVoronoi();
		drawVoronoi(app);
		
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
	}
	
	public void setStrokeWeight(float strokeWeight) {
		this.stroke = strokeWeight;
	}
	
	public void drawVoronoiRandom(PApplet app) {
		app.stroke(0);
		app.strokeWeight(stroke);
		for(int i = 0; i < voronoiRegions.length; i++){
			color = app.color(app.random(0, 255), app.random(0,255), app.random(0,255));
			app.fill(color);
			voronoiRegions[i].draw(app); // draw this shape
		}
	}
	
	public void drawVoronoi(PApplet app) {
		app.fill(255,100,0);
		app.stroke(0);
		app.strokeWeight(stroke);
		for(int i = 0; i < voronoiRegions.length; i++){
			voronoiRegions[i].draw(app); // draw this shape
		}	
//		app.fill(1,1,1);
//		app.stroke(0);
//		app.strokeWeight(.02f);
//		for(int i = 0; i < voronoiEdges.length; i++){
//			float startX = voronoiEdges[i][0];
//			float startY = voronoiEdges[i][1];
//			float endX = voronoiEdges[i][2];
//			float endY = voronoiEdges[i][3];
//			app.line( startX, startY, endX, endY);
//		}
		
//		for (int i = 0; i < NUMPOINTS; i++) {
//			app.ellipse(voronoiPoints[i][0], voronoiPoints[i][1], .01f, .01f);
//		}
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
	
	public void pushClosestPoint (PVector vector, int bodyPartid) {
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
				app.fill(255,100,0);
			}
			voronoiRegions[i].draw(app); // draw this shape
		}
	}
}