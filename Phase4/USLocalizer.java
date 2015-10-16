import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private TwoWheeledRobot robot;
	private UltrasonicSensor us;
	private LocalizationType locType;
	
	public USLocalizer(Odometer odo, UltrasonicSensor us, LocalizationType locType) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.us = us;
		this.locType = locType;
		
		// switch off the ultrasonic sensor
		us.off();
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA = 0, angleB = 0;
		double dTheta = 0;
		double approx = 1;
		boolean finishRotating = false;
		
		// LCD.drawString("DIST: " + getFilteredData(), 0, 5);
		
		LCD.clear();
		
		robot.setRotationSpeed(25);
		if (getFilteredData() <= 30) {
			locType = LocalizationType.FALLING_EDGE;
		}
		else {
			locType = LocalizationType.RISING_EDGE;
		}
		
		if (locType == LocalizationType.FALLING_EDGE) {
			
			// rotate the robot until it sees no wall			
		
			while (getFilteredData() <= 30) {
				LCD.drawString("DIST: " + getFilteredData(), 0, 1);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while (getFilteredData() > 30) {
				LCD.drawString("DIST: " + getFilteredData(), 0, 1);
			}
			
			// keep rotating until the robot sees a wall, then latch the angle
			if (getFilteredData() <= 30) {
				LCD.drawString("DIST: " + getFilteredData(), 0, 1);
				angleA = odo.getAngle();
				LCD.drawString("A: " + angleA, 0, 3);
				Sound.beep();
				// switch direction
				robot.setRotationSpeed(-25);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// wait until it sees no wall and keep rotating until the robot sees a wall, then latch the angle
			if (getFilteredData() <= 30) {
				while (getFilteredData() <= 30) {
					LCD.drawString("DIST: " + getFilteredData(), 0, 1);
					LCD.drawString("A: " + angleA, 0, 3);
				}
			}
			
			if (getFilteredData() > 30) {
				while (getFilteredData() > 30) {
					LCD.drawString("DIST: " + getFilteredData(), 0, 1);
					LCD.drawString("A: " + angleA, 0, 3);
				}
			}
			
			if (getFilteredData() <= 30) {
				LCD.drawString("DIST: " + getFilteredData(), 0, 1);
				LCD.drawString("A: " + angleA, 0, 3);
				angleB = odo.getAngle();
				LCD.drawString("B: " + angleB, 0, 4);
				Sound.twoBeeps();
				robot.setRotationSpeed(0);
			}
			
			boolean BgreaterThanA = false;
			
			if (angleA < angleB) {
				dTheta = 225 - (angleA + angleB)/2;
				BgreaterThanA = true;
			}
			else {
				dTheta = 45  - (angleA + angleB)/2;
			}
			
			if (odo.getAngle()+dTheta > 360) {
				odo.setPosition(new double [] {0.0, 0.0, odo.getAngle()+dTheta-360}, new boolean [] {true, true, true});
			}
			else {
				odo.setPosition(new double [] {0.0, 0.0, odo.getAngle()+dTheta}, new boolean [] {true, true, true});
			}		
			
			robot.setRotationSpeed(15);
			robot.setRotationSpeed(15);
			robot.setRotationSpeed(15);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while (finishRotating == false) {
				if ((odo.getAngle() >= 0 && odo.getAngle() <= approx)
						|| (odo.getAngle() <= 365 && odo.getAngle() >= 365 - approx)) {
					finishRotating = true;
				}
				LCD.drawString("Calculated: " + dTheta, 0, 1);
				LCD.drawString("getAngle: " + odo.getAngle(), 0, 2);
				LCD.drawString("A: " + angleA, 0, 3);
				LCD.drawString("B: " + angleB, 0, 4);
				if (BgreaterThanA) {
					LCD.drawString("NONE B > A: " + angleA, 0, 5);
				}
				else {
					LCD.drawString("NONE A > B: " + angleA, 0, 5);
				}
			}
			Sound.twoBeeps();
			robot.setRotationSpeed(0);
			robot.setRotationSpeed(0);
			robot.setRotationSpeed(0);
							
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			
			// update the odometer position (example to follow:)
			
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			while (getFilteredData() > 30) {
				LCD.drawString("DIST: " + getFilteredData(), 0, 1);
			}
			
			// keep rotating until the robot sees a wall, then latch the angle
			if (getFilteredData() <= 30) {
				LCD.drawString("DIST: " + getFilteredData(), 0, 1);
				angleA = odo.getAngle();
				LCD.drawString("A: " + angleA, 0, 3);
				Sound.beep();
				// switch direction
				robot.setRotationSpeed(-25);
			}
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			// wait until it sees no wall and keep rotating until the robot sees a wall, then latch the angle
			if (getFilteredData() <= 30) {
				while (getFilteredData() <= 30) {
					LCD.drawString("DIST: " + getFilteredData(), 0, 1);
					LCD.drawString("A: " + angleA, 0, 3);
				}
			}
			
			if (getFilteredData() > 30) {
				while (getFilteredData() > 30) {
					LCD.drawString("DIST: " + getFilteredData(), 0, 1);
					LCD.drawString("A: " + angleA, 0, 3);
				}
			}
			
			if (getFilteredData() <= 30) {
				LCD.drawString("DIST: " + getFilteredData(), 0, 1);
				LCD.drawString("A: " + angleA, 0, 3);
				angleB = odo.getAngle();
				LCD.drawString("B: " + angleB, 0, 4);
				Sound.twoBeeps();
				robot.setRotationSpeed(0);
			}
			
			boolean BgreaterThanA = false;
			
			if (angleA < angleB) {
				dTheta = 225 - (angleA + angleB)/2;
				BgreaterThanA = true;
			}
			else {
				dTheta = 45  - (angleA + angleB)/2;
			}
			
			if (odo.getAngle()+dTheta > 360) {
				odo.setPosition(new double [] {0.0, 0.0, odo.getAngle()+dTheta-360}, new boolean [] {true, true, true});
			}
			else {
				odo.setPosition(new double [] {0.0, 0.0, odo.getAngle()+dTheta}, new boolean [] {true, true, true});
			}		
			
			robot.setRotationSpeed(15);
			robot.setRotationSpeed(15);
			robot.setRotationSpeed(15);
			
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while (finishRotating == false) {
				if ((odo.getAngle() >= 0 && odo.getAngle() <= approx)
						|| (odo.getAngle() <= 365 && odo.getAngle() >= 365 - approx)) {
					finishRotating = true;
				}
				LCD.drawString("Calculated: " + dTheta, 0, 1);
				LCD.drawString("getAngle: " + odo.getAngle(), 0, 2);
				LCD.drawString("A: " + angleA, 0, 3);
				LCD.drawString("B: " + angleB, 0, 4);
				if (BgreaterThanA) {
					LCD.drawString("NONE B > A: " + angleA, 0, 5);
				}
				else {
					LCD.drawString("NONE A > B: " + angleA, 0, 5);
				}
			}
			Sound.twoBeeps();
			robot.setRotationSpeed(0);
			robot.setRotationSpeed(0);
			robot.setRotationSpeed(0);
							
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			
			// update the odometer position (example to follow:)
			
			//
			// FILL THIS IN
			//
		}
		
		odo.setPosition(new double [] {3, 3, 0}, new boolean [] {true, true, true});
		
		Lab4 lab4 = new Lab4();
		lab4.setFinishedLocalization(true);
		
	}
	
	private int getFilteredData() {
		int distance;
		
		// do a ping
		us.ping();
		
		// wait for the ping to complete
		try { Thread.sleep(50); } catch (InterruptedException e) {}
		
		// there will be a delay here
		distance = us.getDistance();
		
		if (distance > 35) {
			distance = 35;
		}
				
		return distance;
	}
	
	public int data() {
		return getFilteredData();
	}

}
