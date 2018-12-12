
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import edu.mtholyoke.cs.comsc243.kinect.Body;
import edu.mtholyoke.cs.comsc243.kinect.KinectBodyData;
import edu.mtholyoke.cs.comsc243.kinectTCP.TCPBodyReceiver;
import edu.mtholyoke.cs.comsc243em.emendelo.calibration.Calibrator;
import processing.core.PApplet;
import processing.core.PVector;
import edu.mtholyoke.cs.comsc243.kinect.PersonTracker;

/**
 * @author eitan
 *
 */
public class KinectRenderDemo extends PApplet {
	
	public static int PROJECTOR_WIDTH = 1024;
	public static int PROJECTOR_HEIGHT = 786;
	private Calibrator calibrator;
	private PersonTracker tracker;
	private int numPeople = 0;
	private boolean invalidated = false;

	private HashMap<Long, Person> people = new HashMap<Long, Person>();
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
		createWindow(true, false, .8f);
	}

	public void setup(){

		/*
		 * use this code to run your PApplet from data recorded by recorder 
		 */
		/*
		try {
			kinectReader = new KinectBodyDataProvider("test.kinect", 10);
		} catch (IOException e) {
			System.out.println("Unable to creat e kinect producer");
		}
		 */
		tracker = new PersonTracker();	
		
		kinectReader = new TCPBodyReceiver("138.110.92.93", 8008);
		try {
			kinectReader.start();
		} catch (IOException e) {
			System.out.println("Unable to connect to kinect server");
			exit();
		}
		try {
			calibrator = new  Calibrator(kinectReader, "none.calibration");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}
	public void draw(){
		setScale(.4f);
		
		noStroke();



		background(200,200,200);

		// leave trails instead of clearing background \ 
		//noStroke();
		//fill(0,0,0, 10);
		//rect(-1,-1, 2, 2); //draw transparent rect of the window

//		KinectBodyData bodyData = kinectReader.getMostRecentData();
		KinectBodyData bodyData = kinectReader.getNextData();
		if(bodyData == null) return;
//		Body person = bodyData.getPerson(0);
//		if(person != null){
//			PVector head = person.getJoint(Body.HEAD);
//			PVector spine = person.getJoint(Body.SPINE_SHOULDER);
//			PVector spineBase = person.getJoint(Body.SPINE_BASE);
//			PVector shoulderLeft = person.getJoint(Body.SHOULDER_LEFT);
//			PVector shoulderRight = person.getJoint(Body.SHOULDER_RIGHT);
//			PVector footLeft = person.getJoint(Body.FOOT_LEFT);
//			PVector footRight = person.getJoint(Body.FOOT_RIGHT);
//			PVector handLeft = person.getJoint(Body.HAND_LEFT);
//			PVector handRight = person.getJoint(Body.HAND_RIGHT);
//			PVector elbowLeft = person.getJoint(Body.ELBOW_LEFT);
//			PVector elbowRight = person.getJoint(Body.ELBOW_RIGHT);
//
//
//			fill(255,0,0);
//			noStroke();
//			drawIfValid(head);
//			drawIfValid(spineBase);
//			
//			PVector t1 = calibrator.transformPoint(head);
//			fill (0,0,0);
//			drawIfValid(t1);
//
//			if( 
//					(footRight != null) &&
//					(footLeft != null) &&
//					(handLeft != null) &&
//					(handRight != null) 
//					) {
//				stroke(255,0,0, 100);
//				noFill();
//				strokeWeight(.05f); // because of scale weight needs to be much thinner
//				quad(footLeft.x, footLeft.y, 
//						handLeft.x, handLeft.y, 
//						handRight.x, handRight.y,
//						footRight.x, footRight.y);
//			}

		if(bodyData != null) {
			tracker.update(bodyData);
			for(Long id : tracker.getEnters()) {
				if (people.size() < 2) { //make sure that there's only 2 ppl in the space
					people.put(id, new Person(id, this));
					//numPeople++;
				}
			}
			for(Long id: tracker.getExits()) {
				if (!people.get(id).isValidated()) { //if the person that walks out is invalidated
					invalidated = false;
				}
				people.remove(id);
			}

			HashMap<Long, Body> idBodyMap = tracker.getPeople();

			for(Entry<Long, Body> entry : idBodyMap.entrySet()) {
				Body body = entry.getValue();
				Person person = people.get(entry.getKey()); 
				if (!invalidated) {
					person.setValidate(false);//set random person as validated
					invalidated = true;
				}
				PVector head = null;
				PVector t1 = null;

				if(body != null) {
					head = body.getJoint(Body.HEAD);
					if(head != null) {
						t1 = calibrator.transformPoint(head);
						person.setLocation(t1);
						
					}
				}
				if (person!= null) {
					person.draw(this);
				}
				//drawIfValid(head);
			}
			
			//1 person started out as invalidated and 1 is validated
			
			
			
		}
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
			ellipse(vec.x, vec.z, .1f,.1f);
		}

	}


	public static void main(String[] args) {
		PApplet.main(KinectRenderDemo.class.getName());
	}

}