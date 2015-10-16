public class Map {
	
	// initialize map of all 48 possible initial orientations
	// i represents row
	// j represents column
	// k represents direction (0 N, 1 W, 2 S, 3 E)
	// all except walls start as true
	// when determined that element is no longer possible initial orientation, set to false
	public static boolean[][][] initializeMap() {
		
		boolean[][][] map = new boolean[4][4][4];
		
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				for (int k=0; k<4; k++) {
					if ( (i==0 && j==0) || (i==1 && j==2) || (i==1 && j==3) || (i==3 && j==1) ) {
						map[i][j][k] = false;
					}
					else {
						map[i][j][k] = true;
					}
				}
			}
		}
		
		return map;
		
	}
	
	// map that indicates which squares have blocks
	// true represents block that exists
	// false represents empty
	// i represents row
	// j represents column
	public static boolean[][] wallMap() {
		
		boolean[][] wallMap = new boolean[4][4];
		
		for (int i=0; i<4; i++) {
			for (int j=0; j<4; j++) {
				if ( (i==0 && j==0) || (i==1 && j==2)
						|| (i==1 && j==3) || (i==3 && j==1) ) {
					wallMap[i][j] = true;
				}
				else {
					wallMap[i][j] = false;
				}
			}
		}

		return wallMap;
		
	}
	
}