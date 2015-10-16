import java.util.ArrayList;

public class Simulator_DetermineInitial {
	
	private static Simulator_Map mapClass = new Simulator_Map();
	private static boolean[][] wallMap = mapClass.wallMap();
	private static boolean[][][] map = mapClass.initializeMap();
	
	private static int xCount;
	private static int yCount;
	private static int tCount;
	
	private static double initialDistance;
	private static double distance;
	
	private static int iterationCounter = 0;
	
	private static ArrayList<Boolean> path = new ArrayList<Boolean>();
	private static ArrayList<Double> distances = new ArrayList<Double>();
	private static ArrayList<Boolean> pathA = new ArrayList<Boolean>();
	
	public static void circle() {
		//rotate 360 degrees
		for (int r=0; r<4; r++) {
			distances.add(distance);
			rotateCCW();
			iterationCounter = path.size();
			calculateDistance();
			simulate();			
			display();
		}
	}
	
	//allows robot to determine initial orientation
	//boolean indicates whether it is necessary to rotate 360 in beginning for deterministic
	public static void run(int x, int y, int t, boolean circleBoolean) {
		
		boolean finished = false;
		
		xCount = x;
		yCount = y;
		tCount = t;		
		
		calculateDistance();
		simulate();
		display();
		
		if (circleBoolean = true) {
			circle();
		}
		
		//runs until 20 iterations in case there is error in code, prevents robot from running forever
		while (finished == false && iterationCounter < 20) {
			
			distances.add(distance);
			
			// if wall, rotate
			if (distance < 35) { 			
				rotateCCW();
			}
			else {
				// if no wall, rotate or move forward				
				// move forward
				travelThirty(); 
			}
			
			iterationCounter = path.size();
			
			calculateDistance();
			simulate();			
			display();
			
			finished = ifFinished();
			
		}	
	}
	
	//displays number of remaining possible initial orientations
	public static int displayRemaining() {
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
						
						if (path.get(counter) == true) {
							if (calculateArbDistance(a,b,c) != distances.get(counter)) {
								map[i][j][k] = false;
							}
							c = rotateACCW(c);
						}
						else {
							if (calculateArbDistance(a,b,c) != distances.get(counter)) {
								map[i][j][k] = false;
							}
							temp = travelAThirty(a,b,c);
							a = temp[0];
							b = temp[1];
						}
						counter++;
					}
					
					if (calculateArbDistance(a,b,c) != distance) {
						map[i][j][k] = false;
					}
					
					counter = 0;
					
					clearAPath();

				}
			}
		}
		
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
		System.out.println("Remaining: " + displayRemaining());
		
		System.out.println();
		System.out.println();
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
		System.out.println();
	}
	
	//robot rotates counter-clockwise
	public static void rotateCCW() {
		if (tCount == 3) {
			tCount = 0;
		}
		else {
			tCount++;
		}
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
		if (tCount == 0) {
			xCount--;
		}
		else if (tCount == 1) {
			yCount--;
		}
		else if (tCount == 2) {
			xCount++;
		}
		else {
			yCount++;
		}
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
	
	//determines distance in front of robot
	public static void calculateDistance() {
		int xLook = xCount;
		int yLook = yCount;
		
		if (tCount == 0) {
			xLook--;
		}
		else if (tCount == 1) {
			yLook--;
		}
		else if (tCount == 2) {
			xLook++;
		}
		else {
			yLook++;
		}
		
		if ( (xLook==0 && yLook==0) || (xLook==1 && yLook==2) || (xLook==1 && yLook==3) || (xLook==3 && yLook==1)
				|| (xLook==-1) || (xLook==4) || (yLook==-1) || (yLook==4) ) {
			distance = 5;
		}
		else {
			distance = 50;
		}
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
		p("Distance read by US Sensor: " + distance);
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
	
	public int getX() {
		return xCount;
	}
	public int getY() {
		return yCount;
	}
	public int getT() {
		return tCount;
	}

}
