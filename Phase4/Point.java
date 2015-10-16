// Class point to create object Point. Includes useful functions in relations to calculating angles, distances...

public class Point {
    private double X_COORDINATE, Y_COORDINATE;
    
    public Point(double X,double Y) {
	    this.X_COORDINATE=X;
	    this.Y_COORDINATE=Y;
    }
    //Distance of the object its called on to point passed as input 
   
   public double distanceTo(Point P){
       //Compute Hypothenus
	   double ab= this.X_COORDINATE-P.X_COORDINATE; 
	   double bc=this.Y_COORDINATE-P.Y_COORDINATE   ;
	   double distance = Math.sqrt(ab*ab+bc*bc);
	   return distance;
   } 
   
   // Angle from object Point it is called on to Point P passed as input. Accounts for current orientation of robot
   public double angleTo(Point P, double theta){
       
	   double Xi = this.X_COORDINATE;
	   double Yi = this.Y_COORDINATE;
	   double Xf = P.X_COORDINATE;
	   double Yf = P.Y_COORDINATE;
	   
	   double angleOdometer = theta;
	   double angleOdoComp = Math.PI/2 - theta; //phi
	   double angleFinal = Math.atan(Yf/Xf); //alpha
	   
	   if ((Yf/Xf) < 0) {
		   angleFinal += 2*Math.PI;
	   }
	   
	   double angleDifference = 0;
	   
	   angleDifference = angleOdoComp - angleFinal;
	  
	   return angleDifference;
	     
       
	      
   }
   
   //Get the angle theta that robot should have to face Point P
   public double getDesiredAngle(Point P){
	   double deltaX= Math.abs(P.X_COORDINATE-this.X_COORDINATE);
       double deltaY=Math.abs(P.Y_COORDINATE-this.Y_COORDINATE);
       double angle= Math.atan(deltaY/deltaX);
       angle= (Math.PI/2)-angle;
       
       if(this.X_COORDINATE>P.X_COORDINATE && this.Y_COORDINATE<P.Y_COORDINATE){
    	   angle=2*Math.PI-angle;
       }
       else if(X_COORDINATE<P.X_COORDINATE && this.Y_COORDINATE>P.Y_COORDINATE){
    	   angle=Math.PI-angle;
       }
       else if(X_COORDINATE>P.X_COORDINATE && this.Y_COORDINATE>P.Y_COORDINATE){
    	   angle=Math.PI+angle;
       }
       else if(this.X_COORDINATE>P.X_COORDINATE && this.Y_COORDINATE==P.Y_COORDINATE){
    	   angle=1.5*Math.PI;
    	   
       }
       else if(this.X_COORDINATE>P.X_COORDINATE && this.Y_COORDINATE==P.Y_COORDINATE){
    	   angle=1.5*Math.PI;
    	   
       
       
    	   
    	   
       }
       return angle;
	   
   }
   public double getX(){
	   
	   return this.X_COORDINATE;
   }
  public double getY(){
	   
	   return this.Y_COORDINATE;
   }
   
}