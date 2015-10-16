import java.util.ArrayList;

public class Simulator_NavigateToFinal {
	
	private static int x;
	private static int y;
	private static int t;
	
	private static double distance;
	
	private static Simulator_Map mapClass = new Simulator_Map();
	private static boolean[][] wallMap = mapClass.wallMap();
	
	private static ArrayList<Integer> directions = new ArrayList<Integer>();
	
	// executes navigation to final location
	public static void run() {
		
		Simulator_DetermineInitial determineInitial = new Simulator_DetermineInitial();
		
		x = determineInitial.getX();
		y = determineInitial.getY();
		t = determineInitial.getT();
		x = (3 - x)*30 - 15;
		y = y*30 - 15;
		int temp;
		temp = x;
		x = y;
		y = temp;
		
		if (t == 1) {
			rotateCW();
			printCurrent();
		}
		else if (t == 2) {
			rotateCW();
			printCurrent();
			rotateCW();
			printCurrent();
		}
		else if (t == 3) {
			rotateCCW();
			printCurrent();
		}
		else {
			// do nothing
		}
		
		boolean finished = false;
		
		int counter = 0;
		
		while (finished == false) {
			
			calculateDistance();
			
			// if no wall in north, move forward
			if (distance != 5) {
				directions.add(t);
				travelThirty();
				printCurrent();
			}
			// if wall is north, move east
			else {
				rotateCW();
				printCurrent();
				
				if (distance != 5) {
					directions.add(t);
					travelThirty();
					printCurrent();
					rotateCCW();
					printCurrent();
				}
				// if wall in east, go west
				else {
					rotateCCW();
					printCurrent();
					rotateCCW();
					printCurrent();
					directions.add(t);
					travelThirty();
					printCurrent();
					rotateCW();
					printCurrent();
					
					//if still wall in north, move west again
					if (distance == 5) {
						rotateCCW();
						printCurrent();
						directions.add(t);
						travelThirty();
						printCurrent();
						rotateCW();
						printCurrent();
					}
				}
			}
			
			if (x == 75 && y == 75) {
				finished = true;
			}
			
		}
		
		for (int i=0; i<directions.size()-1; i++) {
			System.out.print(directions.get(i) + ",");
		}
		System.out.print(directions.get(directions.size()-1));
		
		
	}
	
	// prints information in regards to current orientation
	// prints map
	public static void printCurrent() {
		
		int i = (y + 15)/30;
		i = 3-i;
		int j = (x + 15)/30;
		
		printLocation(i,j,t);
		
		System.out.println(x + "," + y + "," + t);
		
		calculateDistance();
		System.out.println("Distance read by US Sensor: " + distance);
		System.out.println();
		
	}
	
	// simulates counter-clockwise rotation
	public static void rotateCCW() {
		if (t == 3) {
			t = 0;
		}
		else {
			t++;
		}
	}
	
	// simulates clockwise rotation
	public static void rotateCW() {
		if (t == 0) {
			t = 3;
		}
		else {
			t--;
		}
	}

	// simulates forward movement
	public static void travelThirty() {
		if (t == 0) {
			y+=30;
		}
		else if (t == 1) {
			x-=30;
		}
		else if (t == 2) {
			y-=30;
		}
		else {
			x+=30;
		}
		
	}
	
	// prints map of where robot exists
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
	}
	
	//prints arrow for orientation of robot
	public static char arrow(int n) {
		if (n == 0) { return '^'; }
		else if (n == 1) { return '<'; }
		else if (n == 2) { return 'v'; }
		else { return '>'; }
	}
	
	//method for System.out.print
	public static void p(String input) {
		System.out.print(input);
	}
	
	// simulates distance read by ultrasonic sensor
	public static void calculateDistance() {
		
		int xLook = (y + 15)/30;
		xLook = 3-xLook;
		int yLook = (x + 15)/30;
		
		if (t == 0) {
			xLook--;
		}
		else if (t == 1) {
			yLook--;
		}
		else if (t == 2) {
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
	
}