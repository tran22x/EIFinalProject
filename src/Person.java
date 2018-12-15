import java.util.ArrayList;

import edu.mtholyoke.cs.comsc243.kinect.Body;
import processing.core.PApplet;
import processing.core.PVector;

public class Person {
	Long id;
	PVector head;
	//all body joints
	PVector spine;
	PVector shoulderLeft;
	PVector shoulderRight;
	PVector handRight;
	PVector handLeft;
	PVector elbowRight;
	PVector elbowLeft;
	PVector hipRight;
	PVector hipLeft;
	PVector kneeRight;
	PVector kneeLeft;
	PVector footRight;
	PVector footLeft;
	PApplet app;
	Body body;
	PVector[] allJoints = {head, spine, shoulderLeft, shoulderRight, handRight, handLeft, elbowRight, elbowLeft, hipRight, hipLeft, kneeRight, kneeLeft, footRight, footLeft};
	
	int color;
	float radius;
	boolean validated = true;
	final double THRESHOLD = .2f;//threshold to check if the person is standing at a position
	
	public Person(Long id, PApplet app) {
		this.id = id;
		this.app = app;
		color = app.color(app.random(0, 255), 255, 255);
		radius = app.random(.1f, .3f);
	}
	
	public PVector[] getAllJoints() {
		System.out.println("Head is: " + allJoints[0]);
		return allJoints;
	}
	
	
	
	public void setLocation(PVector loc) {
		//this.loc = loc;
	}
	
	private void drawIfValid(PVector v) {
		if (v != null){
			app.fill(255, 0, 0);
			app.noStroke();
			app.ellipse(v.x, v.y, 0.1f, 0.1f);
		}	
	}
	public void draw(PApplet app) {
			app.fill(255,0,0);
			drawIfValid(head);
//			drawIfValid(spine);
//			drawIfValid(shoulderLeft);
//			drawIfValid(shoulderRight);
			drawIfValid(handLeft);
			drawIfValid(handRight);
	}
	
	public void setBody (Body b) {
		if (b != null) {
			this.body = b;
			setAllJoints();
		}		
	}
	
	/**
	 * Methods to set coordinates of all joints
	 */
	private void setAllJoints() {
		if (body != null) {
			setHead(body.getJoint(Body.HEAD));
			setSpine(body.getJoint(Body.SPINE_BASE));
			setShoulderLeft(body.getJoint(Body.SHOULDER_LEFT));
			setShoulderRight(body.getJoint(Body.SHOULDER_RIGHT));
			setHandLeft(body.getJoint(Body.HAND_LEFT));
			setHandRight(body.getJoint(Body.HAND_RIGHT));
			setElbowLeft(body.getJoint(Body.ELBOW_LEFT));
			setElbowRight(body.getJoint(Body.ELBOW_RIGHT));
			setHipRight(body.getJoint(Body.HIP_RIGHT));
			setHipLeft(body.getJoint(Body.HIP_LEFT));
			setKneeLeft(body.getJoint(Body.KNEE_LEFT));
			setKneeRight(body.getJoint(Body.KNEE_RIGHT));
			setFootRight(body.getJoint(Body.FOOT_RIGHT));
			setFootLeft(body.getJoint(Body.FOOT_LEFT));	
		}
	}
	
	
	/** Getters and setters for each joint **/
	
	public PVector getHead() {
		return head;
	}

	public void setHead(PVector head) {
		if (head != null) {
			allJoints[0] = head;
			this.head = head;
		}
	}

	public PVector getSpine() {
		return spine;
	}

	public void setSpine(PVector spine) {
		if (spine != null)
			allJoints[1] = spine;
			this.spine = spine;
	}

	public PVector getShoulderLeft() {
		return shoulderLeft;
	}

	public void setShoulderLeft(PVector shoulderLeft) {
		if (shoulderLeft != null)
			allJoints[2] = shoulderLeft;
			this.shoulderLeft = shoulderLeft;
	}

	public PVector getShoulderRight() {
		return shoulderRight;
	}

	public void setShoulderRight(PVector shoulderRight) {
		if (shoulderRight != null)
			allJoints[3] = shoulderRight;
			this.shoulderRight = shoulderRight;
	}

	public PVector getHandRight() {
			return handRight;
	}

	public void setHandRight(PVector handRight) {
		if (handRight != null)
			allJoints[4] = handRight;
			this.handRight = handRight;
	}

	public PVector getHandLeft() {
		return handLeft;
	}

	public void setHandLeft(PVector handLeft) {
		if (handLeft != null)
			allJoints[5] = handLeft;
			this.handLeft = handLeft;
	}

	public PVector getElbowRight() {
		return elbowRight;
	}

	public void setElbowRight(PVector elbowRight) {
		if (elbowRight != null) 
			allJoints[6] = elbowRight;
			this.elbowRight = elbowRight;
	}

	public PVector getElbowLeft() {
		return elbowLeft;
	}

	public void setElbowLeft(PVector elbowLeft) {
		if (elbowLeft != null){
			allJoints[7] = elbowRight;
			this.elbowLeft = elbowLeft;
		}
			
	}

	public PVector getHipRight() {
		return hipRight;
	}

	public void setHipRight(PVector hipRight) {
		if (hipRight != null) {
			allJoints[8] = hipRight;
			this.hipRight = hipRight;
		}
			
	}

	public PVector getHipLeft() {
		return hipLeft;
	}

	public void setHipLeft(PVector hipLeft) {
		if (hipLeft != null) {
			this.hipLeft = hipLeft;
			allJoints[9] = hipLeft;
		}
			
	}

	public PVector getKneeRight() {
		return kneeRight;
	}

	public void setKneeRight(PVector kneeRight) {
		if (kneeRight != null) {
			allJoints[10] = kneeRight;
			this.kneeRight = kneeRight;
		}
			
	}

	public PVector getKneeLeft() {
		return kneeLeft;
	}

	public void setKneeLeft(PVector kneeLeft) {
		if (kneeLeft != null) {
			this.kneeLeft = kneeLeft;
			allJoints[11] = kneeLeft;
		}
	}

	public PVector getFootRight() {
		return footRight;
	}

	public void setFootRight(PVector footRight) {
		if (footRight != null) {
			allJoints[12] = footRight;
			this.footRight = footRight;
		}
	}

	public PVector getFootLeft() {
		return footLeft;
	}

	public void setFootLeft(PVector footLeft) {
		if (footLeft != null)
			allJoints[13] = footLeft;
			this.footLeft = footLeft;
	}

	public Body getBody() {
		return body;
	}
//	/**
//	 * Method to check if the person is standing at a specific location
//	 * @param v
//	 */
//	public boolean checkLocation(PVector v) {
//		if (v != null && Math.abs(v.x - loc.x) < THRESHOLD && Math.abs(v.y - loc.y) < THRESHOLD) {
//			return true;
//		}
//		return false;
//	}
	

}
