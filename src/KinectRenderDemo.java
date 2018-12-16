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
	
	KinectMsgHandler kinectReader;
	private PersonTracker tracker;
	private HashMap<Long, Person> people = new HashMap<Long, Person>();
	private Pattern pattern;
	private Person person1;
	private Person person2;
	private int wait = 1000;
	private int time = Integer.MAX_VALUE;

	//TCPBodyReceiver kinectReader;
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
		
		String filename = "bodyPose.kinect";
		int loopCnt = -1; // use negative number to loop forever
		try {
			System.out.println("Trying to read " + filename + " loops:"  +loopCnt);
			kinectReader = new PoseFileReader(filename, loopCnt);
		} catch (FileNotFoundException e) {
			System.out.println("Unable to open file: " + filename);
		}

		
		try {
			kinectReader.start();
		} catch (IOException e) {
			System.out.println("Unable to start kinect reader");
			exit();
		}

		tracker = new PersonTracker();	
		pattern = new Pattern(); //set up new voronoi pattern
//		kinectReader = new TCPBodyReceiver("138.110.92.93", 8008);
//		try {
//			kinectReader.start();
//		} catch (IOException e) {
//			System.out.println("Unable to connect to kinect server");
//			exit();
//		}

	}
	public void draw(){
		setScale(.5f);
		background(200,200,200);
		KinectBodyData bodyData = kinectReader.getNextData();
		if(bodyData == null){
			pattern.drawNoBody(this);
			return;
		}

		if(bodyData != null) {
			tracker.update(bodyData);
			for(Long id : tracker.getEnters()) {
					if (person1 == null || person2 == null) { //only recognize 2 people
						people.put(id, new Person(id, this));
					}	
			}
			for(Long id: tracker.getExits()) {
				if (people.get(id) != null && people.get(id).equals(person1)) { //when person exits remove from hashmap and set person to null
					people.remove(id);
					person1 = null;
					System.out.println("Person 1 removed");
				}
				else if (people.get(id) != null && people.get(id).equals(person2)){
					people.remove(id);
					person2 = null;
					System.out.println("Person 2 removed");
				}
				if (people.size() == 0) { //if the last person leaves then reset the voronoi
					pattern.resetVoronoi(this);
				}
			}

			HashMap<Long, Body> idBodyMap = tracker.getPeople();
			for(Entry<Long, Body> entry : idBodyMap.entrySet()) {
					Body body = entry.getValue();
					Person person = people.get(entry.getKey()); 
					if (body != null && person != null) {
						if (person1 == null && person != person2) {
							person1 = person;
							//System.out.println("person1 added");
						}
						else if (person2 == null && person != person1) {
							//System.out.println("person2 added");
							person2 = person;
						}
						person.setBody(body); //set body and populate all joints
						}
					}
				}
			
			if (person1 != null && person2 != null) { //if two people are available
				if (touchingHands(person1, person2)) { //if their hands are touching then change color constantly
					System.out.println("Fuck they are holding hands how sweet");
					//TODO: Implement a wait time to prevent epilepsy - as of now the colors is changing too fast
					
					pattern.drawVoronoiRandom(this); //changing the color constantly
				}
				else { //else the points can stick to them
					pattern.drawTwoPeople(this, person1, person2);
				}	
			}
			//if one person is available then draw one person
			else if (person1 == null && person2 != null || person1 != null && person2 == null) {
				if (person1 != null) pattern.drawOnePerson(this, person1);
				else if (person2 != null) pattern.drawOnePerson(this, person2);
				
			}
			//if there's nobody on the screen then render the background only
			else if (person1 == null && person2 == null) {
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
	
	/**
	 * Method to detect if people's hands are touching
	 * @param person1
	 * @param person2
	 * @return
	 */
	public boolean touchingHands (Person person1, Person person2) {
		PVector handL1 = person1.getHandLeft();
		PVector handR1 = person1.getHandRight();
		PVector handL2 = person2.getHandLeft();
		PVector handR2 = person2.getHandRight();
		
		// if people holds one hands
		if (handL1 != null && handR1 != null && handL2 != null && handR2 != null) {
			if (touches(handL1,handR2) || touches(handR1,handL2) ||
					touches(handL1,handL2) & touches(handR1,handR2)) {
					 return true;
				}
		}
		return false;
	}
	
	/**
	 * Method to detect if two vectors are close to eachother
	 * @param p1
	 * @param p2
	 * @return
	 */
	public boolean touches(PVector p1, PVector p2) {
		if (Math.abs(p1.x-p2.x)<0.2f & Math.abs(p2.y-p2.y)<0.2f) {
			return true;
		}
			return false;
	}
	
	public static void main(String[] args) {
		PApplet.main(KinectRenderDemo.class.getName());
	}

}