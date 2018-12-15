import processing.core.PVector;


public class DetectStates {
	//location of two person
	PVector handL1;
	PVector handR1;
	PVector handL2;
	PVector handR2;
	
	int count;
	KinectRenderDemo demo;
	
	
	
	public boolean touches(PVector p1, PVector p2) {
		if (Math.abs(p1.x-p2.x)<0.25f & Math.abs(p2.y-p2.y)<0.25f) {
			return true;
		}
			return false;
	}
	public void changeState() {
		//TODO: controls states
		
		// if person hold hands
		if (touches(handL1,handL2) || touches(handR1,handR2)) {
			holdHands();
		}
		
		//if time elapse 
		if (demo.frameCount%100==0) {
			//Do something lol
		}
		
	}
	public void holdHands() {
		//TODO: draw based on what hold hands suppose to do 
	}
	public void timeElappsed() {
		//TODO: do something that changes by time
	}
	
}
	
	
	
	


