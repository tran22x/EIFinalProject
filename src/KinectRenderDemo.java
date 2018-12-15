import java.awt.Color;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Random;

import edu.mtholyoke.cs.comsc243.kinect.Body;
import edu.mtholyoke.cs.comsc243.kinect.KinectBodyData;
import edu.mtholyoke.cs.comsc243.kinect.KinectBodyDataProvider;
import edu.mtholyoke.cs.comsc243.kinectTCP.TCPBodyReceiver;
import megamu.mesh.MPolygon;
import megamu.mesh.Voronoi;
import processing.core.PApplet;
import processing.core.PVector;
import edu.mtholyoke.cs.comsc243.kinect.PersonTracker;
import edu.mtholyoke.cs.comsc243.kinect.util.KinectMsgHandler;
import edu.mtholyoke.cs.comsc243.kinectTCP.PoseFileReader;

/**
 * @author eitan
 *
 */
public class KinectRenderDemo extends PApplet {
	
	public static int PROJECTOR_WIDTH = 1024;
	public static int PROJECTOR_HEIGHT = 786;
	
	//KinectMsgHandler kinectReader;
	private PersonTracker tracker;
	private HashMap<Long, Person> people = new HashMap<Long, Person>();
	private Random random = new Random();
	private final int NUMLAVA = 100;
	private float[][] voronoiPoints = new float[NUMLAVA][2];
	private Voronoi voronoi;
	private MPolygon[] voronoiRegions;
	private float[][] voronoiEdges;
	//private LinkedList<PVector> lastPos;
	

	TCPBodyReceiver kinectReader;
	public static float PROJECTOR_RATIO = (float)PROJECTOR_HEIGHT/(float)PROJECTOR_WIDTH;

	public void createWindow(boolean useP2D, boolean isFullscreen, float windowsScale) {
		if (useP2D) {
			if(isFullscreen) {
				fullScreen(P2D);  			
			} else {
				size((int)(PROJECTOR_WIDTH * windowsScale), (int)(PROJECTOR_HEIGHT * windowsScale), P2D);
			}
		} else {
			if(isFullscreen) {
				fullScreen();  			
			} else {
				size((int)(PROJECTOR_WIDTH * windowsScale), (int)(PROJECTOR_HEIGHT * windowsScale));
			}
		}		
	}
	
	// use lower numbers to zoom out (show more of the world)
	// zoom of 1 means that the window is 2 meters wide and appox 1 meter tall in real world units
	// sets 0,0 to center of screen
	public void setScale(float zoom) {
		scale(zoom* width/2.0f, zoom * -width/2.0f);
		translate(1f/zoom , -PROJECTOR_RATIO/zoom );		
	}

	public void settings() {
		createWindow(true, true, .5f);
	}

	public void setup(){
		/*
		 * use this code to run your PApplet from data recorded by recorder 
		 */
		
//		String filename = "bodyPose.kinect";
//		int loopCnt = -1; // use negative number to loop forever
//		try {
//			System.out.println("Trying to read " + filename + " loops:"  +loopCnt);
//			kinectReader = new PoseFileReader(filename, loopCnt);
//		} catch (FileNotFoundException e) {
//			System.out.println("Unable to open file: " + filename);
//		}
//
//		
//		try {
//			kinectReader.start();
//		} catch (IOException e) {
//			System.out.println("Unable to start kinect reader");
//			exit();
//		}

		 
		tracker = new PersonTracker();	
		
		kinectReader = new TCPBodyReceiver("138.110.92.93", 8008);
		try {
			kinectReader.start();
		} catch (IOException e) {
			System.out.println("Unable to connect to kinect server");
			exit();
		}
		gameSetup(); //setup lava and random positions for people to stand and start

	}
	public void draw(){
		setScale(.5f);
		background(200,200,200);
		
		fill(255,100,0);
		for(int i = 0; i < voronoiRegions.length; i++){
			voronoiRegions[i].draw(this); // draw this shape
		}
		
		fill(1,1,1);
		stroke(0);
		strokeWeight(.02f);
		for(int i = 0; i < voronoiEdges.length; i++){
			float startX = voronoiEdges[i][0];
			float startY = voronoiEdges[i][1];
			float endX = voronoiEdges[i][2];
			float endY = voronoiEdges[i][3];
			line( startX, startY, endX, endY);
		}
		
		for (int i = 0; i<NUMLAVA; i++) {
			ellipse(voronoiPoints[i][0], voronoiPoints[i][1], .01f, .01f);
		}
		KinectBodyData bodyData = kinectReader.getNextData();
		if(bodyData == null) return;
		
		// as of now this is just drawing the people on the screen
		if(bodyData != null) {
			tracker.update(bodyData);
			for(Long id : tracker.getEnters()) {
					System.out.println("Person detected");
					people.put(id, new Person(id, this));
			}
			for(Long id: tracker.getExits()) {
				people.remove(id);
			}

			HashMap<Long, Body> idBodyMap = tracker.getPeople();

			for(Entry<Long, Body> entry : idBodyMap.entrySet()) {
				Body body = entry.getValue();
				Person person = people.get(entry.getKey()); 
				if (body != null && person != null) {
					//System.out.println("Inside loops");
					person.setBody(body); //set body and populate all joints
					person.draw(this);
				}
				//PVector head = null;

//				if(body != null) {
//					head = body.getJoint(Body.HEAD);
//					if(head != null) {
//						if (person != null) 
//							person.setLocation(head);
//						
//					}
//				}
			}

		}
	}
	
	/**Method to set up the beginning: Two people are instructed to stand at specific points on the screen. Don't let more than 2 person start */
	private void gameSetup() {
		//call method to setup lava
		setUpLava();
		
		//method to setup specific locations for people to stand, make sure that they are different and not too close
		
			//startingPoint1 = lava.getRandomPos();
			//startingPoint2 = lava.getRandomPos();
	}	
	
	private void setUpLava() {
		float minW = -2f;
		float maxW = 2f;
		float minH = -1.5f;
		float maxH = 1.5f;
		float randomPos;
		for (int i=0; i < NUMLAVA; i++) {
			randomPos = minW + random.nextFloat() * (maxW - minW);
			voronoiPoints[i][0] = randomPos;
			randomPos = minH + random.nextFloat() * (maxH - minH);
			voronoiPoints[i][1] = randomPos;
		}
		voronoi = new Voronoi(voronoiPoints);
		voronoiRegions = voronoi.getRegions();
		voronoiEdges = voronoi.getEdges();
	}
	

	/**
	 * Draws an ellipse in the x,y position of the vector (it ignores z).
	 * Will do nothing is vec is null.  This is handy because get joint 
	 * will return null if the joint isn't tracked. 
	 * @param vec
	 */
	public void drawIfValid(PVector vec) {
		if(vec != null) {
			fill(0,0,0);
			ellipse(vec.x, vec.z, .01f,.01f);
		}

	}


	public static void main(String[] args) {
		PApplet.main(KinectRenderDemo.class.getName());
	}

}