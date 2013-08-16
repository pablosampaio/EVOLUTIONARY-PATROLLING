package taia.strategies;

import java.util.Set;

import taia.SimpleIndividual;
import yaps.graph_library.algorithms.AllPairsShortestPaths;

public class Diversification {
	
	private DiversificationType type = DiversificationType.IntersectionDistance;
	
	
	public void setDiversificationType(DiversificationType t){
		this.type = t;
	}
	
	public double getDiversificationMetric(SimpleIndividual i1, SimpleIndividual i2){
		
		switch(this.type){
		case Hausdorff:
			return HausdorffDistanceBetweenIndividuals(i1, i2);
		case IntersectionDistance:
			return SetDistanceBetweenIndividuals(i1, i2);
		default:
			break;
		
		}
		
		return Double.MAX_VALUE;
	}
	
	/*
	 * 
	1.	h = 0 
	2.	for every point ai of A,
	2.1		shortest = Inf ;
	2.2		for every point bj of B
				dij = d (ai , bj )
				if dij < shortest then
					shortest = dij
	2.3  if shortest > h then 
                    h = shortest 
                    
	 */
	public static Double DirectHausdorffSetDistance(Set<Integer> A, Set<Integer> B, AllPairsShortestPaths allp){
		
		double h = 0;
		
		for(Integer a : A){
			
			double shortest = Double.MAX_VALUE;
			
			for(Integer b: B){
				double d = allp.getDistance(a, b);
				
				if(d < shortest){
					shortest = d;
				}
				
			}
			
			if(shortest > h){
				h = shortest;
			}
			
		}
		
		return h;
	}
	
	
	
	public static Double HausdorffSetDistance(Set<Integer> A, Set<Integer> B, AllPairsShortestPaths allp){
		return Math.max(DirectHausdorffSetDistance(A, B, allp), DirectHausdorffSetDistance(B, A, allp));
	}
	
	public static double HausdorffDistanceBetweenIndividuals(SimpleIndividual i1, SimpleIndividual i2){
		
		
		double d = 0;
		
		for(Integer agent: i1.getAgentsCentralNodesList()){
			d += HausdorffSetDistance(
					i1.getAgentMATP(agent).getCoveredNodesSet(), 
					i2.getAgentMATP(agent).getCoveredNodesSet(),
					i1.getGraph().getAllPaths()
					);
		}
		
		return d;
	}
	

	
	public static double IntersectionDistanceBetweenSets(Set<Integer> A, Set<Integer> B){
		
		int cnt =  0;
		
		for(Integer a : A){
			if(B.contains(a)){
				cnt++;
			}
		}
		
		for(Integer b: B){
			if(A.contains(b)){
				cnt++;
			}
		}
		
		return (double)(A.size() + B.size() - cnt);
	}
	
	public static double SetDistanceBetweenIndividuals(SimpleIndividual i1, SimpleIndividual i2){
		
		
		double d = 0;
		
		for(Integer agent: i1.getAgentsCentralNodesList()){
			d += IntersectionDistanceBetweenSets(
					i1.getAgentMATP(agent).getCoveredNodesSet(), 
					i2.getAgentMATP(agent).getCoveredNodesSet()
					);
		}
		
		return d;
	}
	
	
	
}
