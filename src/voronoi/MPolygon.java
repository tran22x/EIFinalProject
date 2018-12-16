package voronoi;

import processing.core.*;

public class MPolygon {

	float[][] coords;
	int count;
	
	public MPolygon(){
		this(0);
	}

	public MPolygon(int points){
		coords = new float[points][2];
		count = 0;
	}

	public void add(float x, float y){
		coords[count][0] = x;
		coords[count][1] = y;
		count++;
	}

	public void draw(PApplet p){
		draw(p.g);
	}

	public void draw(PGraphics g){
		g.beginShape();
		for(int i=0; i<count; i++){
			g.vertex(coords[i][0], coords[i][1]);
		}
		g.endShape(PApplet.CLOSE);
	}

	public int count(){
		return count;
	}

	public float[][] getCoords(){
		return coords;
	}
	
	/**
	 * Checking if the region is between 2 points
	 * @param begin the begin point
	 * @param end the end point
	 * @return true if so
	 */
	public boolean contains(PVector begin, PVector end) {
		if (begin != null && end != null) {
			return contains(begin.x, begin.y, end.x, end.y);
		} else {
			return false;
		}
	}
	
	private boolean contains (float beginX, float beginY, float endX, float endY) {
		for (float[] point : coords) {
			if (beginX < point[0] && point[0] < endX && beginY < point[0] && point[0] < endY) {
				return true;
			} else if (beginX > point[0] && point[0] > endX && beginY > point[0] && point[0] > endY) {
				return true;
			} else if (beginX < point[0] && point[0] < endX && beginY > point[0] && point[0] > endY) {
				return true;
			} else if (beginX > point[0] && point[0] > endX && beginY < point[0] && point[0] < endY) {
				return true;
			}
		}
		return false;
	}

}