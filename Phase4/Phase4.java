import lejos.nxt.*;

public class Phase4 {

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

		// setup the odometer, display, and ultrasonic and light sensors
		TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
		Odometer odo = new Odometer(patBot, true);
		
		UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
		ColorSensor ls = new ColorSensor(SensorPort.S1);

		do {
			// clear the display
			LCD.clear();

			LCD.drawString("   Left | Right ", 0, 0);
			LCD.drawString("        |       ", 0, 1);
			LCD.drawString("  Light | Ultra ", 0, 2);
			LCD.drawString("        | Sonic ", 0, 3);
			LCD.drawString("        |       ", 0, 4);
			LCD.drawString("        |       ", 0, 5);

			buttonChoice = Button.waitForAnyPress();
		} while (buttonChoice != Button.ID_LEFT
				&& buttonChoice != Button.ID_RIGHT);
		
		if (buttonChoice == Button.ID_RIGHT){
			// perform the ultrasonic localization
			
			USLocalizer usl = new USLocalizer(odo, us, USLocalizer.LocalizationType.FALLING_EDGE);
			try {
				usl.doLocalization();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			while (finishedLocalization == false) {
				//do nothing
			}
			finishedLocalization = false;
			
			Sound.buzz();
			
			Navigation navigate = new Navigation(odo);
			navigate.travelTo(15,15);
			
			while (finishedNavigation == false) {
				//do nothing
			}
			finishedNavigation = false;
			
			LightLocalizer lsl = new LightLocalizer(odo, ls);
			lsl.doLocalization();
			
			while (finishedLocalization == false) {
				//do nothing
			}
			finishedLocalization = false;
			
			//LCDInfo lcd = new LCDInfo(odo);
			
			Sound.beep();
		
			navigate.travelTo(0,0);
			
			while (finishedNavigation == false) {
				//do nothing
			}
			finishedNavigation = false;
			
			navigate.turnTo(0, navigate.nfa(odo.getAngle()));
			
			while (finishedNavigation == false) {
				//do nothing
			}
			finishedNavigation = false;
			
			boolean display = true;
			
			LCD.clear();
			
			if (display) {
				LCD.drawString("X: " + odo.getX(), 0, 2);
				LCD.drawString("Y: " + odo.getY(), 0, 3);
				LCD.drawString("theta: " + odo.getAngle(), 0, 4);
			}
			
		} else {
			
		}

		while (Button.waitForAnyPress() != Button.ID_ESCAPE)
			System.exit(0);
	}

}
