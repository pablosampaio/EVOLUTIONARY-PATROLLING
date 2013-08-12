package taia;

import java.io.IOException;

import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;
import yaps.util.RandomUtil;

public class SimulatedAnnealing extends HillClimb {
	
	

	public SimulatedAnnealing(Graph g, int simulationTime) {
		super(g, simulationTime);
	}
	
	public SimpleIndividual simulatedAnnealing(int numberOfIterations, double temperature, double tDecreaseFactor) {
		
		int iterations = 0;
		
		//Generating a random initial solution
		SimpleIndividual s = this.getIndividualBuilder().buildNewRandomIndividual();
		
		//Random initial solution generated

		double qualityOfS = this.getMetricFacility().assessFitness(s);
		
		SimpleIndividual best = s;
		double qualityOfBest = qualityOfS;
		
		System.out.println("Initial Best Individual: "+best+"\n"+
							"Initial Best Metric Value: "+qualityOfBest
							);
		
		SimpleIndividual r = null;
		
		do{
			iterations++;
			
			r = s.copy();
			
			this.getMutate().mutate(r);
			
			double qualityOfR = this.getMetricFacility().assessFitness(r);
			
			//System.out.print("Generated individual has the following metric: "+qualityOfR+"\r");
			
			if(
					qualityOfR < qualityOfS ||
					RandomUtil.chooseDouble() < Math.pow(Math.E, (qualityOfS - qualityOfR)/temperature)
			  ) {
				
				s = r;
				qualityOfS = qualityOfR;
				
			}
			
			temperature -= tDecreaseFactor;
			
			if(qualityOfS < qualityOfBest) {
				best = s;
				qualityOfBest = qualityOfS;
				System.out.println("\nRecently found best individual: "+best+"\n"+
						"Recently found best metric value: "+qualityOfBest
						);
			}		
			
		}while(--numberOfIterations > 0 && temperature >= 0.0);
		
		System.out.println("Finished after "+iterations+" iterations.");
		
		return best;
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException{
		
		
		Graph g = GraphReader.readAdjacencyList("./maps/island11");
		
		g.changeRepresentation(GraphDataRepr.LISTS);
		
		SimulatedAnnealing sa = new SimulatedAnnealing(g, 1000);
		
		SimpleIndividual result = sa.simulatedAnnealing(20000, 4.225, 0.0001);
		
		
		System.out.println("=========================================================\nFinal configuration: "+result+"\n"+
		"Final configuration metric: "+result.getMetricValue());
		
		

	}

}
