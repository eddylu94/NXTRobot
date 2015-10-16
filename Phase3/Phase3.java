/*
 * Lab2.java
 */
import java.util.ArrayList;

import lejos.nxt.*;

public class Phase3 {
	public static void main(String[] args) {
		int buttonChoice;

		// some objects that need to be instantiated
		Odometer odometer = new Odometer();
		OdometryDisplay odometryDisplay = new OdometryDisplay(odometer);
		
		do {
			// clear the display
			LCD.clear();

			// ask the user whether the motors should drive in a square or float
			LCD.drawString("< Left  | Right >", 0, 0);
			LCD.drawString("        |        ", 0, 1);
			LCD.drawString(" Without| With   ", 0, 2);
			LCD.drawString("obstacle| obstacle", 0, 3);
			

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);

		if (buttonChoice == Button.ID_LEFT) {
			ArrayList<Point> waypoints = new ArrayList<Point>();
			waypoints.add(new Point(60,30));
			waypoints.add(new Point(30,30));
			waypoints.add(new Point(30,60));
			waypoints.add(new Point(60,0));
			
			
			Navigator nav = new Navigator(odometer,waypoints,false);
			// start only the odometer and the odometry display
			odometer.start();
			odometryDisplay.start();
			nav.start();
		} else {
			// start the odometer, the odometry display and (possibly) the
			// odometry correction
			ArrayList<Point> waypoints = new ArrayList<Point>();
			waypoints.add(new Point(0,60));
			waypoints.add(new Point(60,0));
			
			Navigator nav = new Navigator(odometer,waypoints,true);
			odometer.start();
			odometryDisplay.start();
			nav.start();
			// spawn a new Thread to avoid SquareDriver.drive() from blocking
			
		}
		
		while (Button.waitForAnyPress() != Button.ID_ESCAPE);
		System.exit(0);
	}
	
	
}