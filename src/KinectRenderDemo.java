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
	private Pattern pattern;
	private Person person1;
	private Person person2;
	

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
		pattern = new Pattern();
		kinectReader = new TCPBodyReceiver("138.110.92.93", 8008);
		try {
			kinectReader.start();
		} catch (IOException e) {
			System.out.println("Unable to connect to kinect server");
			exit();
		}

	}
	public void draw(){
		setScale(.5f);
		background(200,200,200);
		KinectBodyData bodyData = kinectReader.getNextData();
		if(bodyData == null) return;
		
		// as of now this is just drawing the people on the screen
		// TODO: find and assign person1 and person2
		if(bodyData != null) {
			tracker.update(bodyData);
			for(Long id : tracker.getEnters()) {
					System.out.println("Person detected");
					people.put(id, new Person(id, this));
			}
			for(Long id: tracker.getExits()) {
				people.remove(id);
				if (people.size() == 0) { //if the last person leaves the 
					pattern.resetVoronoi(this);
				}
			}

			HashMap<Long, Body> idBodyMap = tracker.getPeople();
			for(Entry<Long, Body> entry : idBodyMap.entrySet()) {
					Body body = entry.getValue();
					Person person = people.get(entry.getKey()); 
					if (body != null && person != null) {
						person.setBody(body); //set body and populate all joints
						pattern.drawOnePerson(this, person);
						person.draw(this);
					}
					//PVector head = null;

//					if(body != null) {
//						head = body.getJoint(Body.HEAD);
//						if(head != null) {
//							if (person != null) 
//								person.setLocation(head);
//							
//						}
//					}
				}
			if (people.size() == 0) {
				pattern.drawNoBody(this);
			}

//			if (person1 != null && person2 != null){
//				pattern.drawTwoPeople(this, person1, person2);
//			} else if (person1 == null && person2 != null){
//				pattern.drawOnePerson(this, person2);
//			} else if (person2 == null && person1 !=null){
//				pattern.drawOnePerson(this, person1);
//			} else {
//				pattern.drawNoBody(this);
//			}

		}
	}	
	public static void main(String[] args) {
		PApplet.main(KinectRenderDemo.class.getName());
	}

}