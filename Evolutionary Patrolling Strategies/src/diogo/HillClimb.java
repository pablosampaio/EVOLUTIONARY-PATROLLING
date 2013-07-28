package diogo;

public class HillClimb {
	
	private static int simulationTime = 10000;
	
	public static SimpleIndividual doHillClimb(SimpleIndividual s, int numberIterations){
		
		SimpleIndividual r = null;
		
		while(numberIterations-- > 0){
			r = s.tweakCopy();
			
			
			
		}
		
	
		return r;
	
	}
	
	
	
	
	

}
