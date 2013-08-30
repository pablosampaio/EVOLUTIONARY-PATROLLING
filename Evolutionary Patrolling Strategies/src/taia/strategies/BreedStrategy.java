package taia.strategies;

import java.util.HashSet;

import taia.SimpleIndividual;

//PAS: considerando seu uso, acho que toda esta classe poderia ser incluida diretamente no NSGA-II
public class BreedStrategy {
	
	private int targetPopulationSize;
	private CrossOver crossOver = null;
	private IndividualBuilder individualBuilder = null;
	private Mutate mutate = null;
	private Selection select = new Selection();
	private ParetoFacility pareto = null;
	
	public BreedStrategy(IndividualBuilder individualBuilder, int targetPopulationSize) {
		this.targetPopulationSize = targetPopulationSize;
		this.individualBuilder = individualBuilder;
	}
	
	
	public HashSet<SimpleIndividual> verySimpleBreedStraegy(HashSet<SimpleIndividual> oldGeneration){
		
		HashSet<SimpleIndividual> newGeneration = new HashSet<SimpleIndividual>(oldGeneration.size());
		SimpleIndividual[] P = oldGeneration.toArray(new SimpleIndividual[0]);
		
		
		while(newGeneration.size() < targetPopulationSize){
			
			SimpleIndividual[] s = this.select.selectManyFromMany(P, 2);
			s[0] = s[0].copy();
			s[1] = s[1].copy();
			
			s = this.crossOver.crossOver(s[0], s[1]);
			
			this.mutate.mutate(s[0]);
			this.mutate.mutate(s[1]);
			
			if( (targetPopulationSize - newGeneration.size()) == 1){
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


	public void setTargetPopulationSize(int targetPopulationSize) {
		this.targetPopulationSize = targetPopulationSize;
	}


	public void setCrossOver(CrossOver crossOver) {
		this.crossOver = crossOver;
	}


	public void setIndividualBuilder(IndividualBuilder individualBuilder) {
		this.individualBuilder = individualBuilder;
	}


	public void setMutate(Mutate mutate) {
		this.mutate = mutate;
	}


	public void setSelect(Selection select) {
		this.select = select;
	}


	public void setPareto(ParetoFacility pareto) {
		this.pareto = pareto;
	}

}
