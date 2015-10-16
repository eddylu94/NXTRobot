import java.util.ArrayList;

import lejos.nxt.*;

public class Navigator extends Thread {
	private ArrayList<Point> Destinations ;
	private final NXTRegulatedMotor leftMotor = Motor.A, rightMotor = Motor.B, USMotor = Motor.C;
	private Odometer odometer;
	private double currentAngle;
	
	private boolean isNavigating = false;
	public boolean isObstacles=false;
	private static final int updateFrequency= 3;
	
	//Set 2 types of robot movement speed: Speed when it is going straight
	// and speeed when it is rotating
	private static final int DEFAULT_SPEED= 250;
	private static final int ROTATION_SPEED= 150;
	
	//When avoiding obstacles, allow the robot to get as close as 7cm from the obstacle
	private static final int bandCenter = 7;
	
	//Dimensions of the robot to calculate arc-length when rotating
	private final double wheelRadius=2.06;
	private final double width=15.25;
	private UltrasonicSensor usSensor;
	private UltrasonicPoller usPoller;
	private static final SensorPort usPort = SensorPort.S3;
	public Navigator(Odometer odometer,ArrayList<Point> waypoints,boolean obstacles){
	    this.odometer=odometer;
	    this.Destinations=waypoints;
	    this.isObstacles=obstacles;
	     
	     usSensor = new UltrasonicSensor(usPort);
		
	     usPoller = null;
	}
  
	public void run() {
		// if no obstacle in the way
		if (!this.isObstacles) {
			for(int i=0;i<4;i++) {
				travelTo(this.Destinations.get(i));
				
			
			}      
		}

		// if obstacle in the way
		else {
			UltrasonicSensor usSensor = new UltrasonicSensor(usPort);
			
			for(int i=0;i<2;i++){
				travelTo(this.Destinations.get(i));
			}
		}
		//Run finished
		leftMotor.stop();
		rightMotor.stop();
	}
  
	//Main function to travel to waypoints
	public void travelTo(Point waypoint){
	    this.isNavigating=true;
	    boolean finished = false;
	    Point robotPosition = new Point(this.odometer.getX(),this.odometer.getY());
	    double desiredAngle= robotPosition.getDesiredAngle(waypoint);
	   
		
	    this.rotateRobot(desiredAngle);
	    
	    //Robot deviates to the left, so add speed to rightmotor
	    this.rightMotor.setSpeed(DEFAULT_SPEED-2);
	    this.leftMotor.setSpeed(DEFAULT_SPEED);
	    this.rightMotor.forward();
	    this.leftMotor.forward();
	    double distance;
	    
	    // If no obstacles in the way
	    if(!this.isObstacles){
	    
	    while(this.isNavigating && !finished){
	    	double currentAngle = this.odometer.getTheta();
	    	robotPosition = new Point(this.odometer.getX(),this.odometer.getY());
	    	desiredAngle = robotPosition.getDesiredAngle(waypoint);
	    	// display desired angle
	    	//LCD.drawString("D.A:       "+Math.toDegrees((desiredAngle)), 0, 3);
	    	// display odometer measured angle
	    	LCD.drawString("Theta: "+(Math.toDegrees(this.odometer.getTheta())), 0, 4);
	    	distance = robotPosition.distanceTo(waypoint);
	    	
	    	//Check if robot has reached waypoint
	    	finished = checkDistance(robotPosition,waypoint);
	    	
	    	//Apply correction to the robot's path
	    	applyCorrections(currentAngle,desiredAngle);
	      
	    	//Sleep for a couple of ms to avoid jittery movement
	    	try {
	    		Thread.sleep(updateFrequency);
	    	}
	    	catch (InterruptedException e) {
	    		// there is nothing to be done here because it is not
	    		// expected that the odometer will be interrupted by
	    		// another thread
	    	}
	    }
	   
	 
	    
	}
	    
	    //If obstacles in the way
	    else{
	    
	    	 while(this.isNavigating && !finished){
	    		 
	    		 //If obstacle in the way
	    		boolean ifObstacle=checkObstacle();
	    		robotPosition = new Point(this.odometer.getX(),this.odometer.getY());
	    		
	    		if(ifObstacle){
	    			//Enter 'avoid obstacle' mode
	    			avoidObstacle(robotPosition,waypoint);
	    			robotPosition = new Point(this.odometer.getX(),this.odometer.getY());
	    			desiredAngle = robotPosition.getDesiredAngle(waypoint);
	    			//Re-adjust the robot's oritentation after the robot has avoided the object
	    			this.rotateRobot(desiredAngle);
	    			 leftMotor.setSpeed(DEFAULT_SPEED);
		    		 rightMotor.setSpeed(DEFAULT_SPEED-2);
		    		 rightMotor.forward();leftMotor.forward();
	    		}
	    		
	 	    	double currentAngle = this.odometer.getTheta();
	 	    	robotPosition = new Point(this.odometer.getX(),this.odometer.getY());
	 	    	desiredAngle = robotPosition.getDesiredAngle(waypoint);
	 	    	
	 	    	
	 	    
	 	    	distance = robotPosition.distanceTo(waypoint);
	 	    	
	 	    	//Check if robot is finished
	 	    	finished = checkDistance(robotPosition,waypoint);
	 	    	
	 	    	//Apply corrections to the patch
	 	    	applyCorrections(currentAngle,desiredAngle);
	 	      
	 	    	try {
	 	    		Thread.sleep(updateFrequency);
	 	    	}
	 	    	catch (InterruptedException e) {
	 	    		// there is nothing to be done here because it is not
	 	    		// expected that the odometer will be interrupted by
	 	    		// another thread
	 	    	}
	 	    }
	 	    
	    	
	    	
	    }
	}
	public void rotateUS(double ang){
		
		
	}
	
	//Rotate robot to face the waypoint
	public void rotateRobot(double ang){
	
		
		leftMotor.setSpeed(ROTATION_SPEED);
		rightMotor.setSpeed(ROTATION_SPEED);
		double theta = this.odometer.getTheta();
		double angle = ang-theta;
		// when angle is acute
		if(angle<Math.PI){
			
			leftMotor.rotate(convertAngle(wheelRadius, width, Math.toDegrees(angle)), true);
			rightMotor.rotate(-convertAngle(wheelRadius, width, Math.toDegrees(angle)), false);
		}
		
		// when angle is obtuse
		else{
			
			angle=2*Math.PI-angle;
			leftMotor.rotate(-convertAngle(wheelRadius, width, Math.toDegrees(angle)), true);
			rightMotor.rotate(convertAngle(wheelRadius, width, Math.toDegrees(angle)), false);
		}
	}
  
	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}
  
	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}
  
	//Correction method
	public void applyCorrections(double currentAngle, double desAngle){
		double approx = Math.PI/55;
		
		double desiredAngle=desAngle;
		
		// change obtuse angle to negative acute angle
		if(desiredAngle>Math.PI&&desiredAngle<2*Math.PI){
			desiredAngle=(-1)*(2*Math.PI-desiredAngle);
        }
		
		// correct if current angle is not large enough
		if(currentAngle+approx<desiredAngle){
			
			//Sound.beep();
			leftMotor.setSpeed(leftMotor.getSpeed()+1);
			rightMotor.setSpeed(rightMotor.getSpeed());
		}
		
		// correct if current angle is too large
		else if(currentAngle-approx>desiredAngle){
			//Sound.beep();
			leftMotor.setSpeed(leftMotor.getSpeed());
			rightMotor.setSpeed(rightMotor.getSpeed()+1);
		}
	}
	
	
	public boolean checkObstacle(){
		boolean check=false;
		int distance = this.usSensor.getDistance();

		LCD.drawString("Dist:       "+distance, 0, 3);
		
		//If robot is within 15cm of the wall, start object avoidance
		if(distance<this.bandCenter){
			check = true;
		}
		return check;
	}
	
	//Main method to circumvent obstacle in the path
	public void avoidObstacle(Point robot, Point dest){
		int counter=0;
		double X=this.odometer.getX();
		double Y=this.odometer.getY();
		
		double X_DEST= dest.getX();
		double Y_DEST= dest.getY();
		boolean avoiding=true;
		
		//Turn robot 90 degrees
		rotateRobot(this.odometer.getTheta()+Math.PI/2);
		
		//Turn robot slightly over 90degrees
		USMotor.rotate(-94);
		int distance;
		
		int count2=0;
		while(avoiding){
			
			leftMotor.setSpeed(DEFAULT_SPEED);
			rightMotor.setSpeed(DEFAULT_SPEED-2);
			rightMotor.forward();leftMotor.forward();
			distance = this.usSensor.getDistance();
			// sensor senses nothing in front
			if(distance==255){
				counter++;
			}
			// robot within 70 cm of barrier
			else if(distance<80){
				counter=0;
			}
			
			if(counter==50){
				
				if(count2==1)
				{
					avoiding=false;
				}
				counter=0;
				count2++;
				rotateRobot(this.odometer.getTheta()-Math.PI/2);
				USMotor.rotate(20);
			}
			
		}
		
		USMotor.rotate(90);
		
	}
	//Check if robot is near the point where it entered the object avoidance method so as not to call isFinished.
	public boolean isInitialLocation(double X,double Y){
		boolean check=false;
		Point robot= new Point(this.odometer.getX(),this.odometer.getY());
		Point p = new Point(X,Y);
		if(robot.distanceTo(p)<18){
			Sound.twoBeeps();
			check =true;
		}
		return check;

	}
	
	//Check if robot has finished avoiding object
  public boolean checkOstacleAvoidanceFinished(double X,double Y,double X2,double Y2){
	  boolean avoiding = true;
	  double slope=(Y2-Y)/(X2-X);
	  double currentX= this.odometer.getX();
	  double currentY= this.odometer.getY();
	  double currentSlope =(Y2-currentY)/(X2-currentX);
	  if(X2==X){
	  
		  if(Math.abs(currentX)<0.5 ){
		  avoiding=false;
		  
	  }}
	  // if current coordinates lies on original slope, then exit wall following
	  if (Math.abs((currentSlope-slope))<0.1){
		  avoiding=false;
		  Sound.twoBeeps();
	  }
	  
	  return avoiding;
  }
  //Check distance from robot to point
	public boolean checkDistance(Point robot, Point dest){
		boolean finished = false;
		double distance = robot.distanceTo(dest);
    	if(dest.getX()==60&&dest.getY()==0){
    		if (distance < 1){
                finished = true;
    		}
    	}
    	
		if (distance <1){
            finished = true;
		}
		
    	return finished;
	}
}