import processing.core.PVector;


public class DetectStates {
	//location of two person
	//Person1
	PVector handL1;
	PVector handR1;
	//Person2
	PVector handL2;
	PVector handR2;

	
	int color = 0;
	
	
	
	public boolean touches(PVector p1, PVector p2) {
		if (Math.abs(p1.x-p2.x)<0.25f & Math.abs(p2.y-p2.y)<0.25f) {
			return true;
		}
			return false;
	}
	public void changeState(KinectRenderDemo demo, Person person1, Person person2) {
		//TODO: controls states
		handL1 = person1.getHandLeft();
		handR1 = person1.getHandRight();
		handL2 = person2.getHandLeft();
		handR2 = person2.getHandRight();
		
		// if person holds one hands
		if (touches(handL1,handL2) ^ touches(handR1,handR2)) {
			holdOneHands(demo);
		}
		else if (touches(handL1,handL2) & touches(handR1,handR2)) {
			holdBothHands(demo);
		}
		
		//if time elapse 
		if (demo.frameCount%100==0) {
			timeElappsed(demo);
		}
		
	}
	public void holdOneHands(KinectRenderDemo demo) {
		//TODO: draw based on what hold hands suppose to do 
		//change background? 
		color = 0;
		demo.background(color);
	}
	public void holdBothHands(KinectRenderDemo demo) {
		//TODO: draw based on what hold hands suppose to do 
		//draw a flower? 
		PVector center = new PVector((handL1.x+handL2.x)/2, (handL1.y+handL2.y)/2);
		//ellipse
		//demo.ellipse(center.x+Math.cos(Math.PI/6)*0.15, center.y+Math.sin(Math.PI/6)*0.15f, 0.25f, 0.25f);
		demo.ellipse(center.x, center.y, 0.15f, 0.15f);
		
		
	}
	public void timeElappsed(KinectRenderDemo demo) {
		//TODO: do something that changes by time
		
	}
	
}
	
	
	
	


