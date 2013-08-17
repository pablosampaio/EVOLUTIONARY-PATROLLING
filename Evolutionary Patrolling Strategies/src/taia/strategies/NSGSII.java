package taia.strategies;

import java.util.ArrayList;
import java.util.HashSet;

import taia.SimpleIndividual;
import taia.util.MetricFacility;

public class NSGSII {
	
	private ParetoFacility paleto = new ParetoFacility();
	private Mutate mut = new Mutate();
	private MetricFacility mtf = new MetricFacility();
	private IndividualBuilder individualBuilder = null;
	private int archiveSize = 20;
	private int populationSize = 50;

	
	public NSGSII(){
		
	}
	
	public NSGSII(int archiveSize, int populationSize){
		this.archiveSize = archiveSize;
		this.populationSize = populationSize;
	}

	public HashSet<SimpleIndividual> doNSGSII(int time){
		
		HashSet<SimpleIndividual> P = new HashSet<SimpleIndividual>(this.populationSize);
		
		for(int i = 0; i < this.populationSize; i++){
			P.add(individualBuilder.buildNewIndividual());
		}
		
		HashSet<SimpleIndividual> best = null;
		ArrayList<HashSet<SimpleIndividual>> R;
		
		while(time-- > 0){
			
			for(SimpleIndividual p: P){
				this.mtf.assessComplexFitness(p);
			}			
			
			R = this.paleto.FrontRankAssignmentByNondominatingSort(P);
			
			best = R.get(0);
			
			HashSet<SimpleIndividual> A = new HashSet<SimpleIndividual>();
			
			for(int i = 0; i < R.size(); i++){
				HashSet<SimpleIndividual> Ri = R.get(i);
				
				if((Ri.size() + A.size()) > this.archiveSize){
					A.addAll(this.paleto.SparsitySelection(this.archiveSize - A.size(), Ri));
				}else{
					A.addAll(Ri);
				}
			}
			
			//FIXME falta o selection!!!!!!!!!!
			
			
		}
		
		
		return best;
	}
	
}







































