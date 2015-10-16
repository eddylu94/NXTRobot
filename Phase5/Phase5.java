import java.util.Random;

import lejos.nxt.*;

public class Phase5 {

	private static Computate compute = new Computate();
	private static FinalNavigation navigateToFinal = new FinalNavigation();
	
	private static boolean finishedLocalization = false;
	private static boolean finishedNavigation = false;
	
	public void setFinishedLocalization(boolean trueOrFalse) {
		this.finishedLocalization = trueOrFalse;
	}
	public void setFinishedNavigation(boolean trueOrFalse) {
		this.finishedNavigation = trueOrFalse;
	}
	
	public static void main(String[] args) {
		int buttonChoice;
		
		Random random = new Random();
		int randomInt = 0;

		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);

		Navigation navigate = new Navigation(odo);
		
		do {
			// clear the display
			LCD.clear();

			LCD.drawString("   Left | Right ", 0, 0);
			LCD.drawString("        |       ", 0, 1);
			LCD.drawString("  Stoch | Deter ", 0, 2);
			LCD.drawString("        |       ", 0, 3);
			LCD.drawString("        |       ", 0, 4);
			LCD.drawString("        |       ", 0, 5);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);
		
		if (buttonChoice == Button.ID_RIGHT){
			
			//DETERMINISTIC	
			
			//allows robot to run until determines initial position
			//boolean input indicates whether to travel 360 before doing deterministic algorithm
			//in case there is location identical to another location
			compute.run(true);
			
			//allows robot to travel to final destination
			navigateToFinal.run();
			
		} else {
			
			//STOCHASTIC
			
			//allows robot to run until determines initial position
			//boolean input indicates no need to travel 360
			//causes the Computate class to run random turn/move method
			compute.run(false);
			
			//allows robot to travel to final destination
			navigateToFinal.run();
			
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			System.exit(0);
	}

}
