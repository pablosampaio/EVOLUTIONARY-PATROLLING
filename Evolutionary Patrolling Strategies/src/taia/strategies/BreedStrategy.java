package taia.strategies;

import java.util.HashSet;

import taia.SimpleIndividual;

public class BreedStrategy {
	
	private int targetPopulationSize;
	private CrossOver crossOver = new CrossOver();
	private IndividualBuilder individualBuilder;
	private Mutate mutate = new Mutate();
	private Selection select = new Selection();
	private ParetoFacility pareto = new ParetoFacility();
	
	public BreedStrategy(IndividualBuilder individualBuilder, int targetPopulationSize) {
		this.targetPopulationSize = targetPopulationSize;
		this.individualBuilder = individualBuilder;
	}
	
	
	public HashSet<SimpleIndividual> verySimpleBreedStraegy(HashSet<SimpleIndividual> oldGeneration){
		
		HashSet<SimpleIndividual> newGeneration = new HashSet<SimpleIndividual>(oldGeneration.size());
		SimpleIndividual[] P = oldGeneration.toArray(new SimpleIndividual[0]);
		
		while(newGeneration.size() < oldGeneration.size()){
			
			SimpleIndividual[] s = this.select.selectManyFromMany(P, 2);
			s[0] = s[0].copy();
			s[1] = s[1].copy();
			
			s = this.crossOver.crossOver(s[0], s[1]);
			
			this.mutate.mutate(s[0]);
			this.mutate.mutate(s[1]);
			
			if( (oldGeneration.size() - newGeneration.size()) == 1){
				newGeneration.add(s[0]);
				break;
			}
			newGeneration.add(s[0]);
			newGeneration.add(s[1]);
		}
		
		return newGeneration;
	}
	
	
	public void setSelection(Selection selection){
		this.select = selection;
	}

}
