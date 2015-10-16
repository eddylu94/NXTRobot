import java.util.ArrayList;
import java.util.Random;

import lejos.nxt.ColorSensor;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;

public class Computate {
	
	private static TwoWheeledRobot patBot = new TwoWheeledRobot(Motor.A, Motor.B);
	private static Odometer odo = new Odometer(patBot, true);
	
	private static UltrasonicSensor us = new UltrasonicSensor(SensorPort.S2);
	private static ColorSensor ls = new ColorSensor(SensorPort.S1);

	private static Navigation navigate = new Navigation(odo);
	
	private static Map mapClass = new Map();
	private static boolean[][] wallMap = mapClass.wallMap();
	private static boolean[][][] map = mapClass.initializeMap();
	
	private static int xCount;
	private static int yCount;
	private static int tCount;
	
	private static double initialDistance;
	
	private static int iterationCounter = 0;
	
	//initialize array with every possible initial orientation
	private static ArrayList<Boolean> path = new ArrayList<Boolean>();
	//initialize arraylist of path of robot
	private static ArrayList<Double> distances = new ArrayList<Double>();
	//initialize arraylist of simulated paths
	private static ArrayList<Boolean> pathA = new ArrayList<Boolean>();
	
	private static String numRemaining = "";
	
	private static Random random = new Random();
	
	//spins robot 360 in very beginning to prevent confusion of two identical spots
	public static void circle() {
		//rotate 360 degrees
		for (int r=0; r<4; r++) {
			rotateCCW();
			iterationCounter = path.size();
			distances.add(us.getDistance()+0.0);
			simulate();			
			numRemaining = calculateRemaining() + "";
			LCD.clear();
			LCD.drawString("Remaining: " + numRemaining, 0, 4);
			LCD.drawString("Read Dist: " + us.getDistance(), 0, 5);
			Sound.beep(); 
		}
	}
	
	//executes computations and determines where the robot should go
	public static void run(boolean circleBoolean) {
		
		boolean finished = false;
		
		distances.add(us.getDistance()+0.0);
		simulate();
		LCD.clear();
		numRemaining = calculateRemaining() + "";
		LCD.drawString("Remaining: " + numRemaining, 0, 5);
		LCD.drawString("Read Dist: " + us.getDistance(), 0, 6);
		
		if (circleBoolean == true) {
			circle();
		}
		
		//while not finished, code keeps executing
		//iteration limit at 20 in case code malfunctions and runs forever
		while (finished == false && iterationCounter < 20) {
						
			// if wall, rotate
			if (us.getDistance()+0.0 < 35) { 			
				rotateCCW();
				iterationCounter = path.size();
				distances.add(us.getDistance()+0.0);
				simulate();	
				numRemaining = calculateRemaining() + "";
				LCD.clear();
				LCD.drawString("Remaining: " + numRemaining, 0, 4);
				LCD.drawString("Read Dist: " + us.getDistance(), 0, 5);
				Sound.beep();
			}
			// if no wall, rotate or move forward				
			else {
				// for deterministic, move forward
				if (circleBoolean) {
					travelThirty();
				}
				else {
					// for stochastic, rotate of move forward
					if (random.nextInt(2) == 0) {
						travelThirty();
					}
					else {
						rotateCCW();
					}
				}
		
				iterationCounter = path.size();
				distances.add(us.getDistance()+0.0);
				simulate();	
				numRemaining = calculateRemaining() + "";
				LCD.clear();
				LCD.drawString("Remaining: " + numRemaining, 0, 4);
				LCD.drawString("Read Dist: " + us.getDistance(), 0, 5);
				Sound.beep();
			}
			
			iterationCounter = path.size();
			
			simulate();			
			finished = ifFinished();

		}	
		
		String originalPosition = displayFinal();
		Sound.buzz(); // indicates end of determining initial orientation
		getXYT();
		
	}
	
	//calculate number of remaining possible initial orientation
	public static int calculateRemaining() {
		int counter = 0;
		
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				for (int k=0; k<4; k++) { 
					if (map[i][j][k] == true) {
						counter++;
					}
				}
			}
		}

		return counter;
	}
	
	// for remaining possible starting positions, simulate if matches real orientation
	public static void simulate() {
	
		int counter = 0;
		
		int a;
		int b;
		int c;
		
		int[] temp = new int[2];
		
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				for (int k=0; k<4; k++) { 
					
					a = i;
					b = j;
					c = k;
					
					while (counter < iterationCounter) {
						
						// if simulated paths of every remaining initial orientation does not match
						// actual path of robot, then set to false
						if (path.get(counter) == true) {
							if ( (calculateArbDistance(a,b,c) == 50 && distances.get(counter) < 35)
									|| (calculateArbDistance(a,b,c) == 5 && distances.get(counter) >= 35) ) {
								map[i][j][k] = false;
							}
							c = rotateACCW(c);
						}
						else {
							if ( (calculateArbDistance(a,b,c) == 50 && distances.get(counter) < 35)
									|| (calculateArbDistance(a,b,c) == 5 && distances.get(counter) >= 35) ) {
								map[i][j][k] = false;
							}
							temp = travelAThirty(a,b,c);
							a = temp[0];
							b = temp[1];
						}
						counter++;
					}
					
					if ( (calculateArbDistance(a,b,c) == 50 && distances.get(counter) < 35)
							|| (calculateArbDistance(a,b,c) == 5 && distances.get(counter) >= 35) ) { 
						map[i][j][k] = false;
					}
					
					counter = 0;
		
					clearAPath();

				}
			}
		}
		
	}

	//displays the original position
	//i represents row
	//j represents column
	//k represents direction (0 N, 1 W, 2 S, 3 E)
	public static String displayFinal() {
		
		String originalPosition = "";
		
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				for (int k=0; k<4; k++) {
					if (map[i][j][k] == true) {
						originalPosition = i + "," + j + "," + k;
					}
				}
			}
		}
		
		return originalPosition;
		
	}
	
	//checks if only one possible starting position remains
	public static boolean ifFinished() {
		int counter = 0;
		
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				for (int k=0; k<4; k++) { 
					if (map[i][j][k] == true) {
						counter++;
					}
				}
			}
		}
		
		if (counter == 1) {
			return true;
		}
		return false;
	}
	
	//empties arraylist that stores path for each possible orientation
	public static void clearAPath() {
		while (!(pathA.isEmpty())) {
			pathA.remove(0);
		}
	}
	
	public static void LCDdisplay() {
//		LCD.drawString(" X: " + odo.getX(), 0, 0);
//		LCD.drawString(" Y: " + odo.getY(), 0, 1);
//		LCD.drawString(" T: " + odo.getAngle(), 0, 2);
//		LCD.drawString("        |       ", 0, 3);
//		LCD.drawString("        |       ", 0, 4);
//		LCD.drawString("        |       ", 0, 5);
	}
	
	//prints simulation results
	public static void display() {
		System.out.println();
		
		System.out.println("Iteration: " + iterationCounter);
		
		System.out.println();
		
		System.out.println("Remaining possible orientations:");
		System.out.println();
		
		printMap();
		
		System.out.println();
		System.out.println();
		
		System.out.println("Actual orientation:");
		System.out.println();
		printLocation(xCount, yCount, tCount);
		System.out.println();
		System.out.println("Orientation: " + xCount + "," + yCount + "," + tCount);
		printPath();
		
		System.out.println();
		System.out.println();
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println();
	}
	
	//robot rotates counter-clockwise
	public static void rotateCCW() {
		navigate.rotateCCW();
		path.add(true);
	}
	//simulated robot rotates counter-clockwise
	public static int rotateACCW(int t) {
		if (t == 3) {
			t = 0;
		}
		else {
			t++;
		}
		pathA.add(true);
		return t;
	}
	
	//robot moves forward by 30 cm
	public static void travelThirty() {
		navigate.travelThirty();
		path.add(false);
	}
	//simulated robot moves forward by 30 cm
	public static int[] travelAThirty(int x, int y, int t) {
		if (t == 0) {
			x--;
		}
		else if (t == 1) {
			y--;
		}
		else if (t == 2) {
			x++;
		}
		else {
			y++;
		}
		pathA.add(false);
		
		int[] output = new int[2];
		output[0] = x; 
		output[1] = y;
		
		return output;
	}
	
	//determines distance in front of simulated robot
	public static int calculateArbDistance(int i, int j, int k) {
		int arbDistance = 0;
		
		int xLook = i;
		int yLook = j;
		int tLook = k;
		
		if (tLook == 0) {
			xLook--;
		}
		else if (tLook == 1) {
			yLook--;
		}
		else if (tLook == 2) {
			xLook++;
		}
		else {
			yLook++;
		}
		
		if ( (xLook==0 && yLook==0) || (xLook==1 && yLook==2) || (xLook==1 && yLook==3) || (xLook==3 && yLook==1)
				|| (xLook==-1) || (xLook==4) || (yLook==-1) || (yLook==4) ) {
			arbDistance = 5;
		}
		else {
			arbDistance = 50;
		}
		return arbDistance;
	}
	
	//prints map
	public static void printMap() {
		p("X X X    "); dc(0,1,0,'^'); p("      "); dc(0,2,0,'^'); p("      "); dc(0,3,0,'^'); p("  ");
		System.out.println();
		p("X X X  "); dc(0,1,1,'<'); p("   "); dc(0,1,3,'>'); p("  "); dc(0,2,1,'<'); p("   "); dc(0,2,3,'>'); p("  ");
		dc(0,3,1,'<'); p("   "); dc(0,3,3,'>');
		System.out.println();
		p("X X X    "); dc(0,1,2,'v'); p("      "); dc(0,2,2,'v'); p("      "); dc(0,3,2,'v'); p("  ");
		System.out.println();
		System.out.println();
		p("  "); dc(1,0,0,'^'); p("      "); dc(1,1,0,'^'); p("    X X X  X X X");
		System.out.println();
		dc(1,0,1,'<'); p("   "); dc(1,0,3,'>'); p("  "); dc(1,1,1,'<'); p("   "); dc(1,1,3,'>'); p("  X X X  X X X");
		System.out.println();
		p("  "); dc(1,0,2,'v'); p("      "); dc(1,1,2,'v'); p("    X X X  X X X");
		System.out.println();
		System.out.println();
		p("  "); dc(2,0,0,'^'); p("      "); dc(2,1,0,'^'); p("      "); dc(2,2,0,'^'); p("      "); dc(2,3,0,'^'); p("  ");
		System.out.println();
		dc(2,0,1,'<'); p("   "); dc(2,0,3,'>'); p("  "); dc(2,1,1,'<'); p("   "); dc(2,1,3,'>');
		p("  "); dc(2,2,1,'<'); p("   "); dc(2,2,3,'>'); p("  "); dc(2,3,1,'<'); p("   "); dc(2,3,3,'>');
		System.out.println();
		p("  "); dc(2,0,2,'v'); p("      "); dc(2,1,2,'v'); p("      "); dc(2,2,2,'v'); p("      "); dc(2,3,2,'v'); p("  ");
		System.out.println();
		System.out.println();
		p("  "); dc(3,0,0,'^'); p("    X X X    "); dc(3,2,0,'^'); p("      "); dc(3,3,0,'^'); p("  ");
		System.out.println();
		dc(3,0,1,'<'); p("   "); dc(3,0,3,'>'); p("  X X X  "); dc(3,2,1,'<'); p("   "); dc(3,2,3,'>');
		p("  "); dc(3,3,1,'<'); p("   "); dc(3,3,3,'>');
		System.out.println();
		p("  "); dc(3,0,2,'v'); p("    X X X    "); dc(3,2,2,'v'); p("      "); dc(3,3,2,'v'); p("  ");
		System.out.println();
	}
	
	//determines arrow character used to represent robot in map
	public static char arrow(int n) {
		if (n == 0) { return '^'; }
		else if (n == 1) { return '<'; }
		else if (n == 2) { return 'v'; }
		else { return '>'; }
	}
	
	//prints small map with location of actual robot
	public static void printLocation(int i, int j, int k) { //print location
		char c = arrow(k);
		p("X "); if (i==0 && j==1) { p(c + " "); } else { p("_ "); } if (i==0 && j==2) { p(c + " "); } 
		else { p("_ "); } if (i==0 && j==3) { p(c + " "); } else { p("_ "); }
		System.out.println();
		if (i==1 && j==0) { p(c + " "); } else { p("_ "); } if (i==1 && j==1) { p(c + " "); } 
		else { p("_ "); } p("X "); p("X ");
		System.out.println();
		if (i==2 && j==0) { p(c + " "); } else { p("_ "); } if (i==2 && j==1) { p(c + " "); }
		else { p("_ "); } if (i==2 && j==2) { p(c + " "); } else { p("_ "); } if (i==2 && j==3) { p(c + " "); } else { p("_ "); }
		System.out.println();
		if (i==3 && j==0) { p(c + " "); } else { p("_ "); } p("X "); if (i==3 && j==2) { p(c + " "); } 
		else { p("_ "); } if (i==3 && j==3) { p(c + " "); } else { p("_ "); }
		System.out.println();
		System.out.println();
		p("Distance read by US Sensor: " + us.getDistance()+0.0);
	}
	
	//display character
	public static void dc(int i, int j, int k, char c) {
		if (map[i][j][k]) {
			System.out.print(c);
		}
		else {
			System.out.print(' ');
		}
	}
	
	//prints input
	public static void p(String input) {
		System.out.print(input);
	}
	
	//prints path of robot
	// T means turn
	// M means move forward
	public static void printPath() {
		String pathString = "Path: ";
		for (int i=0; i<path.size(); i++) {
			if (path.get(i) == false) {
				pathString += "M ";
			}
			else {
				pathString += "T ";
			}
		}
		System.out.println(pathString);
	}
	
	//gets String that displays list of T's and M's
	//T represents turn
	//M represents translational movement of 30 cm
	public String getPath() {
		String pathString = "";
		for (int i=0; i<path.size(); i++) {
			if (path.get(i) == false) {
				pathString += "M";
			}
			else {
				pathString += "T";
			}
		}
		return pathString;
	}
	
	//gets orientation
	//i represents row
	//j represents column
	//k represents direction (0 N, 1 W, 2 S, 3 E)
	public static void getXYT() {
		
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				for (int k=0; k<4; k++) {
					if (map[i][j][k] == true) {
						xCount = i;
						yCount = j;
						tCount = k;
					}
				}
			}
		}
	}
	//gets row value
	public int getX() {
		return xCount;
	}
	//gets column value
	public int getY() {
		return yCount;
	}
	//gets direction value (0 N, 1 W, 2 S, 3 E)
	public int getT() {
		return tCount;
	}
	
}
