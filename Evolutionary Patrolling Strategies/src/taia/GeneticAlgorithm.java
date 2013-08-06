package taia;

import java.io.IOException;
import java.util.List;

import taia.individual.GenericMATPIndividual;
import taia.util.ListUtil;
import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;
import yaps.metrics.Metric;
import yaps.util.RandomUtil;

public class GeneticAlgorithm {

	private int popSize;
	private SimulationConstructor sc;



	public GeneticAlgorithm(int popSize, SimulationConstructor sc){
		//Asserts that lambda is multiple of mu
		//FIXME look for a ceil function for integers
		this.popSize = 2 * (popSize/2 + 1);
		this.sc = sc;
	}

	public GenericMATPIndividual doMuLambdaStrategy(int time){

		GenericMATPIndividual[] P, Q;


		P = new GenericMATPIndividual[this.popSize];
		Q = new GenericMATPIndividual[this.popSize];
		
		List<Integer> indexList =ListUtil.createIndexList(0, this.popSize, 1);
		
		GenericMATPIndividual best = null;

		
		for(int i = 0; i < this.popSize; i++){
			best = sc.buildNewRandomIndividual();
			P[i] = best;
		}

		best.assessFitness(sc.getMetric(), sc.getSimulationTime());
		System.out.println("Initial metric value: " + best.getMetricValue() );

		while(time-- > 0){


			for(GenericMATPIndividual pi: P){
				pi.assessFitness(sc.getMetric(), sc.getSimulationTime());
				if( best.getMetricValue() > pi.getMetricValue() ){
					System.out.println("New metric value: " + pi.getMetricValue() );
					best = pi;
				}

		
			}
			
			
			RandomUtil.shuffleInline(indexList);
			 
			for(int i = 0; i < (this.popSize/2); i++){
				
				GenericMATPIndividual pi = P[ indexList.get(2*i) ];
				GenericMATPIndividual pj = P[ indexList.get(2*i+1) ];
				
				GenericMATPIndividual c[] = GenericMATPIndividual.crossOver(pi, pj);
				
				c[0].tweak();
				c[1].tweak();
				
				Q[ 2*i ] = c[0];
				Q[ 2*i + 1] =  c[1];
				
			}

			P = Q;


		}

		return best;



	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		Graph g = GraphReader.readAdjacencyList("./maps/island11", GraphDataRepr.LISTS);
		SimulationConstructor sc = new SimulationConstructor();
		sc.setNumAgents(3);
		sc.setMetric(Metric.MAXIMUM_INTERVAL);
		sc.setIndividualConstructorParameters(new PreCalculedPathGraph(g));
		
		
		GeneticAlgorithm melambe = new GeneticAlgorithm(200, sc);
		
		melambe.doMuLambdaStrategy(1000);

	}

}
