import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.*;

public class BangBangController implements UltrasonicController{
	private final int bandCenter, bandwith;
	private final int motorLow, motorHigh;
	private final int motorStraight = 200;
	// Speed levels
	private final int SLOW_SPEED=80;
	private final int MEDIUM_SPEED=180;
	private final int FAST_SPEED=320;
	
	// Gap counter to distinguish between a gap and convex turn. Allow a determined number of sensor
	// iterations before altering robot movement
	private int GAP_COUNTER=0;
	private int GAP_THRESHOLD=10;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;
	private int distance;
	private int currentLeftSpeed;
	
	public BangBangController(int bandCenter, int bandwith, int motorLow, int motorHigh) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		this.motorLow = motorLow;
		this.motorHigh = motorHigh;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		
		// Compute closest distance between robot and wall from hypotenuse distance read by sensor
		int PARALLEL_DISTANCE= (int) (distance*Math.cos(45));
		// Difference between desired distance and actual distance
		int error = PARALLEL_DISTANCE- bandCenter;
		LCD.clear();
		LCD.drawString("Distance: " +distance , 0, 3);
		
		//If too far from wall (or gap)
		if(error>bandwith){
			if(GAP_COUNTER<GAP_THRESHOLD){
						GAP_COUNTER++;
						this.goStraight();
			}
			else {
				this.goLeft();
			}
		}
		//If too close to wall
		else if(error<-bandwith){
			resetCounter();
			this.goRight();
		}
		//If close to desired distance maintain straight movement
		else {
			resetCounter();
			this.goStraight();
		}		
	}

	@Override
	public int readUSDistance() {
		return this.distance;
	}

	private void resetCounter(){
		this.GAP_COUNTER=0;
	}
	
	private void goStraight() {
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
	}

	private void goLeft() {
		leftMotor.setSpeed(MEDIUM_SPEED);
		rightMotor.setSpeed(FAST_SPEED);
	}
	
	private void goRight() {
		leftMotor.setSpeed(FAST_SPEED);
		rightMotor.setSpeed(SLOW_SPEED);
	}
		
}
