import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;

public class LightLocalizer {
	private Odometer odo;
	private TwoWheeledRobot robot;
	private ColorSensor ls;
	
	public LightLocalizer(Odometer odo, ColorSensor ls) {
		this.odo = odo;
		this.robot = odo.getTwoWheeledRobot();
		this.ls = ls;
		
		// turn on the light
		ls.setFloodlight(true);
	}
	
	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
		
		odo.setPosition(new double [] {0.0, 0.0, 0}, new boolean [] {true, true, true});
		
		robot.setRotationSpeed(15);
		robot.setRotationSpeed(15);
		robot.setRotationSpeed(15);
		
		//37 127 244 327
		
		
		LCD.clear();
		
		int counter = 0;
		
		while (odo.getAngle() < 5) {
			
			LCD.drawString("color: " + ls.getNormalizedLightValue(), 0, 1);
			
		}
		
		//Set each grid line
		double first = 0;
		double second = 0;
		double third = 0;
		double fourth = 0;
		
		double difference180 = 0;
		
		while (!(odo.getAngle() <= 1 && odo.getAngle() >= 0) ||  (odo.getAngle() <= 365 && odo.getAngle() >= 364)) {
			
			LCD.drawString("color: " + ls.getNormalizedLightValue(), 0, 1);
			
			if (ls.getNormalizedLightValue() < 500) {
				Sound.buzz();
				counter++;
				//First grid line
				if (counter == 1) {
					first = odo.getAngle();
				}
				//Second grid line
				else if (counter == 2) {
					second = odo.getAngle();
				}
				//Third grid line
				else if (counter == 3) {
					third = odo.getAngle();
				}
				//Fourth grid line
				else {
					fourth = odo.getAngle();
					difference180 = fourth - 180;
				}
				
				// Implement pause to avoid detecting many times same grid line
				try {
					Thread.sleep(200);
				} 
				catch (InterruptedException e) {
					e.printStackTrace();
				}
			} 
		}
		
		robot.setRotationSpeed(0);
		robot.setRotationSpeed(0);
		robot.setRotationSpeed(0);
		
		double d = 12.5;
		
		double thetaX = third-first;
		double thetaY = fourth-second;
		
		double errorY = -d*Math.cos(thetaX/2 / 180 * Math.PI);
		double errorX = -d*Math.cos(thetaY/2 / 180 * Math.PI);
		
		double newAngle = 0;
		
	
	
		odo.setPosition(new double [] {errorX, errorY, odo.getAngle()-fourth}, new boolean [] {true, true, true});
		
		Lab4 lab4 = new Lab4();
		lab4.setFinishedLocalization(true);
		
	}

}
