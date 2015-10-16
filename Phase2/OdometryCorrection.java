/* 
 * OdometryCorrection.java
 */
import lejos.nxt.*;
public class OdometryCorrection extends Thread {
	private static final long CORRECTION_PERIOD = 10;
	private Odometer odometer;
	//Check if the first line crossed 
	private boolean firstLineCrossed=false;
	//Check if robot has increasing or decreasing X&Y components
	private boolean isReturning=false;
	//First Grid line distance
	private final int firstLineDistance=15;
	//Second Grid line distance
	private final int secondLineDistance=45;
	//Distance between the light sensor and the center of the robot
	private double sensorDistance=6;
	// constructor
	public OdometryCorrection(Odometer odometer) {
		this.odometer = odometer;
	}

	// run method (required for Thread)
	public void run() {
		long correctionStart, correctionEnd;
	
		ColorSensor sensor = new ColorSensor(SensorPort.S1);
		sensor.setFloodlight(true);
		double lightValue;
		//Count number of lines crossed (for testing)
		int lineCounter=0;
		
		while (true) {
			correctionStart = System.currentTimeMillis();
			lightValue = sensor.getNormalizedLightValue();
			
			LCD.drawString("count:   "+lineCounter, 0, 4);
			
			//Grid line detected - Adjust odometer
			if(lightValue<400){

double orientationAngle= this.odometer.getTheta();
// Adjust the odometer in function of the current angle of the robot
adjustOdometer(orientationAngle);

lineCounter++;

//Implement a pause (sleep) to avoid detecting multiple times the same grid line
try{ 
					Thread.sleep(150);
				}
				catch (InterruptedException e){
					// do nothing in the catch statement
				}
}
			// put your correction code here

			// this ensure the odometry correction occurs only once every period
			correctionEnd = System.currentTimeMillis();
			if (correctionEnd - correctionStart < CORRECTION_PERIOD) {
				try {
					Thread.sleep(CORRECTION_PERIOD
							- (correctionEnd - correctionStart));
				} catch (InterruptedException e) {
					// there is nothing to be done here because it is not
					// expected that the odometry correction will be
					// interrupted by another thread
				}
			}
		}
	}
	
	//Function to determine if robot's movement is vertical or horizontal
	private boolean isVertical(double angle){
		double approx = Math.PI/4;
		//Robot going straight - Vertical
		if((angle+approx)<(Math.PI)/2){
			
			return true;
		}
		//First turn (90degrees) - Horizontal
		else if((angle+approx)>((Math.PI)/2)&&(angle)<(Math.PI-approx)){
			
			return false;
		}
		//Second turn (180 degrees) - Vertical
		else if(((angle+approx)>(Math.PI))&&(angle<(((1.5)*Math.PI)-approx))){
			
			this.isReturning=true;
			return true;
		}
		// Last turn (280 degrees) - Horizontal
		else {
		
			return false;
		}
	}
	// Implementation of the correction based on the angle the robot is facing.
	// Once a grid line is crossed, the odomoter is adjusted 
	private void adjustOdometer(double angle){
	
	// Determine if robot is vertical (adjust Y) or horizontal (adjust X)	
	boolean isVertical = isVertical(angle);
	if(isVertical){
		
		// Adjust the Y Component of the robot
		if(!isReturning){
		//First grid line crossed	
	if(!firstLineCrossed){
		
		this.odometer.setY(firstLineDistance-sensorDistance);
		this.firstLineCrossed=true;
	}
		//Second grid line crossed
	else if(firstLineCrossed) {
		
		
		this.odometer.setY(secondLineDistance-sensorDistance);
		this.firstLineCrossed=false;
	}
		}
		
		//Robot is in 'returning' phase. (Y is decreasing)
		else{
			//First line facing downward is crossed (Y=45)
			if(!firstLineCrossed){
				this.odometer.setY(secondLineDistance+sensorDistance);
				this.firstLineCrossed=true;
			}
			//Second line facing downward is crossed (Y=15)
			else if(firstLineCrossed) { 
				this.odometer.setY(firstLineDistance+sensorDistance);
				this.firstLineCrossed=false;
			}
			
		}
	}	
	else{
		// Adjust the X Component of the robot
		if(!isReturning){
			//First grid line crossed (X=15)
			if(!firstLineCrossed){
				
				this.odometer.setX(firstLineDistance-sensorDistance);
				this.firstLineCrossed=true;
			}
			//Second grid line crossed (X=45)
			else if (firstLineCrossed) { 
				
				this.odometer.setX(secondLineDistance-sensorDistance);
				this.firstLineCrossed=false;
			}
				}
				else{
					// The robot is in returning phase. X decreasing
					
					//First line facing to the left is crossed(X=45)
					if(!firstLineCrossed){
						this.odometer.setX(secondLineDistance+sensorDistance);
						this.firstLineCrossed=true;
					}
					//Second line facing to the left is crossed(X=15)
					else if(firstLineCrossed) { 
						this.odometer.setX(firstLineDistance+sensorDistance);
						this.firstLineCrossed=false;
					}
					
				}
	}
	}
	// Function to determine which line of the grid the robot is crossing in function
	// of the coordinate value (retrieved from odometer class) passed as parameter
	// Safety measure to avoid confusing different grid lines
	private boolean isInRange(int value,String coord){
		if(coord.equals("Y")){
			if(value==15){
			if(0<this.odometer.getY()&&this.odometer.getY()<35){
				return true;
			}
			else{
				return false;
			}
				}
			if(value==45){
				if(35<this.odometer.getY()&&this.odometer.getY()<55){
					return true;
				}
				else{
					return false;
				}
					}
			
			
		}
		if(coord.equals("X")){
			if(value==15){
			if(0<this.odometer.getX()&&this.odometer.getX()<35){
				return true;
			}
			else{
				return false;
			}
				}
			if(value==45){
				if(35<this.odometer.getX()&&this.odometer.getX()<55){
					return true;
				}
				else{
					return false;
				}
					}
			
			
		}
		return false;
	}
}