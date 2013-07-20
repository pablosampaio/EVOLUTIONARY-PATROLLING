package yaps.genetic.generic;

public interface Individual {
	
	
	/**
	 * Small mutation.
	 * 
	 */
	public Individual tweak();
	
	/**
	 * Relatively large mutation
	 * 
	 */
	public Individual mutate();
	
	/**
	 * Access individual fitness.
	 * 
	 */
	public double acessFitness();

}
