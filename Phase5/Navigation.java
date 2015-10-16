import lejos.nxt.LCD;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;


public class Navigation {
	// put your navigation code here 

	private NXTRegulatedMotor leftMotor, rightMotor;
	
	private Odometer odo;
	private TwoWheeledRobot robot;
	
	private Lab5 lab5 = new Lab5();
	
	public Navigation(Odometer odo) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
	}
	
	// rotate counter-clockwise 90 degrees
	public void rotateCCW() {
		
		double initialAngle = odo.getAngle();
		double finalAngle = initialAngle - 90;
		
		turnTo(finalAngle, initialAngle);
		
	}
	
	// rotate clockwise 90 degrees
	public void rotateCW() {
		double initialAngle = odo.getAngle();
		double finalAngle = initialAngle + 90;
		
		turnTo(finalAngle, initialAngle);
	}
	
	// move forward 30 cm
	public void travelThirty() {
		
		double x = odo.getX();
		double y = odo.getY();
		
		boolean finished = false;
		double approx = 1;
		
		if (odo.getAngle() > 315 && odo.getAngle() <= 360 
				|| odo.getAngle() >= 0 && odo.getAngle() <= 45) {
			y += 30;
			
			robot.setForwardSpeed(5);
			robot.setForwardSpeed(5);
			robot.setForwardSpeed(5);
			
			while (finished == false) {
				if (Math.abs(y - odo.getY()) < approx) {
					finished = true;
				}
			}
			
		}
		else if (odo.getAngle() > 45 && odo.getAngle() <= 135) {
			x += 30;
			
			robot.setForwardSpeed(5);
			robot.setForwardSpeed(5);
			robot.setForwardSpeed(5);
			
			while (finished == false) {
				if (Math.abs(x - odo.getX()) < approx) {
					finished = true;
				}
			}
			
		}
		else if (odo.getAngle() > 135 && odo.getAngle() <= 225) {
			y += -30;
			
			robot.setForwardSpeed(5);
			robot.setForwardSpeed(5);
			robot.setForwardSpeed(5);
			
			while (finished == false) {
				if (Math.abs(y - odo.getY()) < approx) {
					finished = true;
				}
			}
			
		}
		else {
			x += -30;
			
			robot.setForwardSpeed(5);
			robot.setForwardSpeed(5);
			robot.setForwardSpeed(5);
			
			while (finished == false) {
				if (Math.abs(x - odo.getX()) < approx) {
					finished = true;
				}
			}
			
		}
		
		robot.setForwardSpeed(0);
		lab5.setFinishedNavigation(true);
		
	}
	
	//Get the angle we want the robot to be facing
	
	public double getDesiredAngle(double x,double y){
		double currentX = odo.getX();
		double currentY = odo.getY();
		
		double diffX = x - currentX;
		double diffY = y - currentY;
		
		double angle;
		
		if (diffX <= 0) {
			if (diffY <= 0) {
				angle = 0 - Math.atan(Math.abs(diffY/diffX) * 180 / Math.PI); // -X -Y
			}
			else {
				angle = 0 + Math.atan(Math.abs(diffY/diffX) * 180 / Math.PI); // -X +Y
			}
		}
		else {
			if (diffY <= 0) {
				angle = 45 + Math.atan(Math.abs(diffY/diffX) * 180 / Math.PI); // +X -Y
			}
			else {
				angle = 45 - Math.atan(Math.abs(diffY/diffX) * 180 / Math.PI); // +X +Y
			}
		}
		return angle;
	}
	public void travelTo(double x, double y) {
		// USE THE FUNCTIONS setForwardSpeed and setRotationalSpeed from TwoWheeledRobot!
	    
		//Get current coordinates
		double currentX = odo.getX();
		double currentY = odo.getY();
		
		// Compute difference between current coordinates and coordinates of destination
		double diffX = x - currentX;
		double diffY = y - currentY;
		
		
		//Get the angle we want the robot to be facing
		double angle = getDesiredAngle(x,y);
		
		//Turn the robot to face desired angle
		turnTo(angle, nfa(odo.getAngle()));
		
		this.setForwardSpeed(3);
		
		boolean finishedMoving = false;
	
		LCD.clear();
		
		double initialX = odo.getX();
		double initialY = odo.getY();
		
		//Loop to be executed while the robot is still not finished moving
		while (finishedMoving == false) {	
			
			//Display values
			this.drawToLCD(initialX, initialY);
			
			// Condition to be met for the robot to stop moving (destination reached)
			if (isFinished(diffX,diffY,initialX,initialY)) {
				finishedMoving = true;
			}
			
		}
		
		//Stop Robot
		this.setForwardSpeed(0);
		
		lab5.setFinishedNavigation(true);

	}
	//Set the forward speed (robot skipped over the speed setting so a quick fix was to set it multiple times)
	public boolean isFinished(double diffX,double diffY,double initialX,double initialY){
		if (Math.abs(Math.sqrt(diffX*diffX + diffY*diffY)
				- Math.sqrt((initialX-odo.getX())*(initialX-odo.getX())
						+ (initialY-odo.getY())*(initialY-odo.getY()))) < 1) {
			return true;
		}
		else{
			return false;
		}
	}
	
	//Set forward speed of robot
	public void setForwardSpeed(int speed){
		for(int i=0;i<3;i++){
			robot.setForwardSpeed(speed);

		}
		
	}
	//Set rotation speed of robot
	public void setRotationSpeed(int speed){
		for(int i=0;i<3;i++){
			robot.setRotationSpeed(speed);

		}
		
	}
	
	//Draw values to screen
	public void drawToLCD(double x,double y){
		LCD.drawString("initial X: " + odo.getX(), 0, 1);
		LCD.drawString("initial Y: " + odo.getY(), 0, 2);
		LCD.drawString("final X: " + x, 0, 3);
		LCD.drawString("final Y: " + y, 0, 4);
		
	}
	public void turnTo(double angle, double currentAngle) {
		// USE THE FUNCTIONS setForwardSpeed and setRotationalSpeed from TwoWheeledRobot!
		
		double approx = 0.6;
		
		boolean finishedTurning = false;
		
		//current45 angle10
		
		if (currentAngle < angle) {
			if (angle-currentAngle <= 180) {
				this.setRotationSpeed(35);
			}
			else {
				this.setRotationSpeed(-35);
			}
		}
		else {
			if (currentAngle-angle <= 180) {
				this.setRotationSpeed(-35);
			}
			else {
				this.setRotationSpeed(35);
			}
		}
		
		double difference = 0;
		
//		LCD.clear();
			
		while (finishedTurning == false) {
			
			difference = Math.abs(nfa(odo.getAngle()) - nfa(angle));
			
			if ( (difference >= 0 && difference <= approx) || difference <= 360 && difference >= 360-approx) {
				finishedTurning = true;
			}
			LCD.drawString("Current: " + odo.getAngle(), 0, 1);
			LCD.drawString("initial: " + currentAngle, 0, 2);
			LCD.drawString("final: " + angle, 0, 3);
			LCD.drawString("diff: " + difference, 0, 4);
		}
		
		// Stop turning
		this.setRotationSpeed(0);
		
		lab5.setFinishedNavigation(true);
		
	}
	
	
	// Get normalized angle (in accord with the robot's theta from the odometer class)
	public double nfa(double angle) {
		if (angle < 0) {
			angle = 360 + angle;
			return angle;
		}
		else if (angle > 360) {
			angle = angle - 360;
			return angle;
		}
		else {
			return angle;
		}
	}
	
}
