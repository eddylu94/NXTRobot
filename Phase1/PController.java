import lejos.nxt.*;

public class PController implements UltrasonicController {
	
	private final int bandCenter, bandwith;
	private final int motorStraight = 300, FILTER_OUT = 20;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.C;	
	private int distance;
	//Initial speed of both motors
	private int LEFT_SPEED=300;
	private int RIGHT_SPEED=300;
	// Checks previous state to avoid alternating movements between left and right
	// Helps reduce random jittery movement
	private boolean PREV_RIGHT=false;
	private boolean PREV_STRAIGHT=true;
	private boolean PREV_LEFT=false;
	// Gap counter to distinguish between a gap and convex turn. Allow a determined number of sensor
	// iterations before altering robot movement
	private int GAP_COUNTER=0;
	private int GAP_THRESHOLD=20;
	// Set floor/roof speed to avoid too aggressive movements 
	private int MAXIMUM_SPEED=200;
	private int MINIMUM_SPEED=450;
	// Maximum/Minimum robot acceleration
	private int MAXIMUM_INCR=13;
	private int MINIMUM_INCR=8;
	private int currentLeftSpeed;
	private int filterControl;
	private int temp=0;
	
	public PController(int bandCenter, int bandwith) {
		//Default Constructor
		this.bandCenter = bandCenter;
		this.bandwith = bandwith;
		leftMotor.setSpeed(motorStraight);
		rightMotor.setSpeed(motorStraight);
		leftMotor.forward();
		rightMotor.forward();
		currentLeftSpeed = 0;
		filterControl = 0;
	}
	
	@Override
	public void processUSData(int distance) {
		this.distance = distance;
		
		// Compute closest distance between robot and wall from hypotenuse distance read by sensor
		int PARALLEL_DISTANCE= (int) (distance*Math.cos(45));
		// Difference between desired distance and actual distance
		int error = PARALLEL_DISTANCE- bandCenter;
		
		//If too far from wall (or gap)
		if(error>bandwith){
			// If gap threshold not reached maintain straight movement
			if( GAP_COUNTER<GAP_THRESHOLD){
						GAP_COUNTER++;
						this.goStraight();
						
			}
			// Concave turn (not gap so begin turn)
			else{
				if (PREV_RIGHT){ 
					this.goStraight();
				}
				this.goLeft();
			}
		}

		//If too close to wall
		else if(error<-bandwith) {
			if(PREV_LEFT) {
				goStraight();
			}
			GAP_COUNTER=0;
			this.goRight();
		}
		
		//If close to desired distance maintain straight movement
		else{
			this.goStraight();
			GAP_COUNTER=0;
		}	
	}
	
	// Reset state booleans
	private void resetCounter(){
			this.PREV_RIGHT=false;
			this.PREV_LEFT=false;
			this.PREV_STRAIGHT=false;
	
		}
	
	//Go straight
	private void goStraight() {
		    this.resetCounter();
			this.PREV_STRAIGHT=true;
			leftMotor.setSpeed(motorStraight);
			rightMotor.setSpeed(motorStraight);
		}
	
	//Turn left
	private void goLeft() {
		this.resetCounter();
		this.PREV_LEFT=true;	
			leftMotor.setSpeed(Math.min((LEFT_SPEED+MAXIMUM_INCR),MAXIMUM_SPEED));
			rightMotor.setSpeed(Math.max((RIGHT_SPEED-MINIMUM_INCR),MINIMUM_SPEED));
	}
	
	//Turn right
	private void goRight() {
		this.resetCounter();
		this.PREV_RIGHT=true;
		rightMotor.setSpeed(Math.min((RIGHT_SPEED+MAXIMUM_INCR),MAXIMUM_SPEED));
		leftMotor.setSpeed(Math.max((LEFT_SPEED-MINIMUM_INCR),MINIMUM_SPEED));
		
	}
	
		
	

	
	@Override
	public int readUSDistance() {
		return this.distance;
	}

}
