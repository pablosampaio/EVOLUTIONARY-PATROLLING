package yaps.genetic;

import yaps.genetic.generic.Individual;
import yaps.graph_library.Graph;

public class FixedNodeIndividual implements Individual {

	
	
	
	public FixedNodeIndividual(int node, Graph graph, int maxSize){
		
		if(node > graph.getNumEdges()){
			
		}
		
		
		
		
	}
	
	@Override
	public Individual tweak() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Individual mutate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double acessFitness() {
		// TODO Auto-generated method stub
		return 0;
	}

}
