import processing.core.PVector;


public class DetectStates {
	//location of two person
	PVector handL1;
	PVector handR1;
	PVector handL2;
	PVector handR2;
	
	public boolean touches(PVector p1, PVector p2) {
		if (Math.abs(p1.x-p2.x)<0.25f & Math.abs(p2.y-p2.y)<0.25f) {
			return true;
		}
			return false;
	}
	public void changeState(KinectRenderDemo demo, Person p1, Person p2) {
		//TODO: controls states
		handL1 = p1.getHandLeft();
		handR1 = p1.getHandRight();
		handL2 = p2.getHandLeft();
		handR2 = p2.getHandRight();
		// if person hold hands
		if (touches(handL1,handL2) || touches(handR1,handR2)) {
			holdHands();
		}
		else if (touches(handL1,handL2) & touches(handR1,handR2)) {
			holdBothHands();
		}
		//if time elapse 
		if (demo.frameCount%100==0) {
			timeElappsed();
		}
		
	}
	public void holdHands() {
		//TODO: draw based on what hold hands suppose to do 
	}
	public void holdBothHands() {
		//TODO: draw based on what both hold hands suppose to do 
	}
	public void timeElappsed() {
		//TODO: do something that changes by time
		//maybe change background color?
		
	}
	
}
	
	
	
	


