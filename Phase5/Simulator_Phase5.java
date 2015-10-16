import java.util.Random;

public class Simulator_Phase5 {
	
	public static void main(String[] args) {
		
		Simulator_DetermineInitial determineInitial = new Simulator_DetermineInitial();
		determineInitial.run(0,1,2,true);
		
		Simulator_NavigateToFinal navigateToFinal = new Simulator_NavigateToFinal();
		navigateToFinal.run();
		
	}
}