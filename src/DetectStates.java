import processing.core.PVector;


public class DetectStates {
	//location of two person
	//Person1
	PVector handL1;
	PVector handR1;
	//Person2
	PVector handL2;
	PVector handR2;

	//control colors
	int R = 255;
	int G = 50;
	int B = 0;
	
	//frameTrack
	int track = 0;
	
	//control stroke weight
	float strokeControl=0;
	
	
	
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
		if (touches(handL1,handL2) & touches(handR1,handR2)) {
			holdOneHands(demo);
		}
//		else if (touches(handL1,handL2) & touches(handR1,handR2)) {
//			holdBothHands(demo);
//		}
		
		//if time elapse 
		else if (demo.frameCount%100==0) {
			timeElappsed(demo);
		}
		
	}
	public void holdOneHands(KinectRenderDemo demo) {
		//TODO: 
		//changes the color of background by time in the red-ish range
		//if statement is here so that the change doesn't happen too fast
		if(demo.frameCount-track>20) {
			G = (G+1)%100;
			B = (B+1)%150;
			track = demo.frameCount;
		}
		demo.background(R,G,B);
	}
//	public void holdBothHands(KinectRenderDemo demo) {
//		//TODO: maybe some diagram???
//		
//		
//	}
	public void timeElappsed(KinectRenderDemo demo) {
		//TODO: play with the stroke weight?
		//stroke range: 0.05~0.21
		//5-21
		//16
		strokeControl =(strokeControl+1)%16;
		demo.strokeWeight(strokeControl*0.01f+0.05f);
		
		
	}
	
}
	
	
	
	


