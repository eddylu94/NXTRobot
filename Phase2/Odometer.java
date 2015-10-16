/*
 * Odometer.java
 */
import lejos.nxt.*;

public class Odometer extends Thread {
	// robot position
	private double x, y, theta;

	// odometer update period, in ms
	private static final long ODOMETER_PERIOD = 25;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B;
	// lock object for mutual exclusion
	private Object lock;
	
	// intitialize left, right counts
	private int currentLeftCount=0;
	private int currentRightCount=0;
	
	//Radius of the robot's wheels
	private final double WHEEL_RADIUS=2.1;
	// Distance between the robot's wheels
	private final double WHEEL_SEPERATION=15;
	private double robotCenterDisplacement;
	private double robotAngleVariation;

	// default constructor
	public Odometer() {
		x = 0.0;
		y = 0.0;
		theta = 0.0;
		lock = new Object();
	}

	// run method (required for Thread)
	public void run() {
		long updateStart, updateEnd;
		 
		while (true) {
			updateStart = System.currentTimeMillis();
			//Get rotation of both motors in degrees
			int updatedLeftCount = leftMotor.getTachoCount();
			int updatedRightCount = rightMotor.getTachoCount();
			
			// Compute difference between current rotation degree and the previous 
			// measure
			int diffLeft = updatedLeftCount - currentLeftCount;
			int diffRight = updatedRightCount - currentRightCount;
			
			this.currentLeftCount = updatedLeftCount;
			this.currentRightCount = updatedRightCount;
			
			//Calculte arc lenghts of both wheels
			double leftDistance = (Math.toRadians(diffLeft)) * WHEEL_RADIUS;
			double rightDistance =(Math.toRadians(diffRight)) * WHEEL_RADIUS;
			
			//Arc length of robot's center
			robotCenterDisplacement = (leftDistance+rightDistance)/2;
			// Compute delta of theta (variation of the angle)
			robotAngleVariation = (leftDistance-rightDistance)/WHEEL_SEPERATION;
			synchronized (lock) {
				// don't use the variables x, y, or theta anywhere but here!
			setTheta(this.theta+robotAngleVariation);
			// Compute change in X&Y component of robot
			double deltaX = robotCenterDisplacement*Math.sin(this.theta);
			double deltaY= robotCenterDisplacement*Math.cos(this.theta);
			//Update X&Y component
			this.x+= deltaX;
			this.y+=deltaY;
			
			
				
			}
			

			// this ensures that the odometer only runs once every period
			updateEnd = System.currentTimeMillis();
			if (updateEnd - updateStart < ODOMETER_PERIOD) {
				try {
					Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometer will be interrupted by
					// another thread
				}
			}
		}
	}

	// accessors
	public void getPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				position[0] = x;
			if (update[1])
				position[1] = y;
			if (update[2])
				position[2] = theta;
		}
	}

	public double getX() {
		double result;

		synchronized (lock) {
			result = x;
		}

		return result;
	}

	public double getY() {
		double result;

		synchronized (lock) {
			result = y;
		}

		return result;
	}

	public double getTheta() {
		double result;

		synchronized (lock) {
			result = theta;
		}

		return result;
	}

	// mutators
	public void setPosition(double[] position, boolean[] update) {
		// ensure that the values don't change while the odometer is running
		synchronized (lock) {
			if (update[0])
				x = position[0];
			if (update[1])
				y = position[1];
			if (update[2])
				theta = position[2];
		}
	}

	public void setX(double x) {
		synchronized (lock) {
			this.x = x;
		}
	}

	public void setY(double y) {
		synchronized (lock) {
			this.y = y;
		}
	}

	public void setTheta(double theta) {
		synchronized (lock) {
			this.theta = theta;
		}
	}
}