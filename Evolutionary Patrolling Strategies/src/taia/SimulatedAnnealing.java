package taia;

import java.io.IOException;
import java.util.ArrayList;

import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;
import yaps.metrics.Metric;
import yaps.util.RandomUtil;

public class SimulatedAnnealing extends HillClimb {
	
	

	public SimulatedAnnealing(Graph g, int simulationTime) {
		super(g, simulationTime);
	}
	
	public SimpleIndividual simulatedAnnealing(int numberOfAgents, int numberOfIterations, double temperature, double tDecreaseFactor, Metric metrica) {
		
		int iterations = 0;
		
		//Generating a random initial solution
		ArrayList<Integer> agentList = new ArrayList<Integer>();
		
		for(int i = 0; i < numberOfAgents; i++) {
			
			int agent = RandomUtil.chooseInteger(0, getNumOFNodes() -1);
			
			while(agentList.contains(agent)){
				agent = RandomUtil.chooseInteger(0, getNumOFNodes() -1);
			}
			
			agentList.add(agent);
			
		}
		
		SimpleIndividual s = new SimpleIndividual(agentList, getGraph());
		
		//Random initial solution generated

		double qualityOfS = metrica.calculate(s.generateVisitList(this.getSimulationTime()), getNumOFNodes(), 1, getSimulationTime());
		
		SimpleIndividual best = s;
		double qualityOfBest = qualityOfS;
		
		System.out.println("Initial Best Individual: "+best+"\n"+
							"Initial Best Metric Value: "+qualityOfBest
							);
		
		SimpleIndividual r = null;
		
		do{
			iterations++;
			
			r = s.tweakCopy();
			
			double qualityOfR = metrica.calculate(r.generateVisitList(getSimulationTime()), getNumOFNodes(), 1, getSimulationTime());
			
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
		
		SimpleIndividual result = sa.simulatedAnnealing(3, 20000, 4.225, 0.0001, Metric.MAXIMUM_INTERVAL);
		
		double quality = Metric.MAXIMUM_INTERVAL.calculate(result.generateVisitList(sa.getSimulationTime()), sa.getNumOFNodes(), 1, sa.getSimulationTime());
		
		System.out.println("=========================================================\nFinal configuration: "+result+"\n"+
		"Final configuration metric: "+quality);
		
		

	}

}
