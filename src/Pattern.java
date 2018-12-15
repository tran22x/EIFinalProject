import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import processing.core.PApplet;
import processing.core.PVector;
import voronoi.Voronoi;
import voronoi.MPolygon;

public class Pattern {
	private Person person1;
	private Person person2;
	private Voronoi voronoi;
	private MPolygon[] voronoiRegions;
	private final int NUMPOINTS = 150;
	private float[][] voronoiPoints = new float[NUMPOINTS][2];
	private float[][] voronoiEdges;
	private Random random = new Random();
	private Map<PVector, Integer> m = new HashMap<>();
	
	private final double THREADHOLD = 0.05f;
	public Pattern (Person person1, Person person2) {
		this.person1 = person1;
		this.person2 = person2;
	}
	
	public Pattern() {
		setUpVoronoi();
	}
	
	public void drawTwoPeople(PApplet app, Person person1, Person person2) {
		
	}
	
	public void drawOnePerson(PApplet app, Person person) {
		if (person != null) {
			for (PVector joint : person.getAllJoints()) {
				if (joint != null) {
						findClosestPoint(joint);
				}
			}	
			drawVoronoi(app);
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
		voronoiEdges = voronoi.getEdges();
	}
	
	public void drawVoronoi(PApplet app) {
		app.fill(255,100,0);
		app.stroke(0);
		app.strokeWeight(.02f);
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
	
	public float computeDistance (float firstX, float firstY, float secondX, float secondY) {
		return ((firstX - secondX)*(firstX-secondX) + (firstY - secondY)*(firstY - secondY));
	}
}