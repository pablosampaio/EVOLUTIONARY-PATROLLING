package taia;

import java.io.IOException;

import taia.strategies.CrossOver;
import taia.strategies.IndividualBuilder;
import taia.strategies.Mutate;
import taia.strategies.Selection;
import taia.util.MetricFacility;
import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;

public class GeneticAlgorithmWithElitism {

	private int popSize;
	private Mutate mut = new Mutate();
	private MetricFacility mtf = new MetricFacility();
	private IndividualBuilder indb = new IndividualBuilder();
	private CrossOver	cross =  new CrossOver();
	private Selection select = new Selection();
	private int elitism;

	
	public GeneticAlgorithmWithElitism(int popSize, int elistism){
		this.elitism = elistism;
		this.popSize = elistism + 2 * ((popSize - elistism)/2 + 1);
	}

	public SimpleIndividual doEvolvePopulation(int time){

		
		SimpleIndividual[] P, Q, Elite;


		P = new SimpleIndividual[this.popSize];
		Q = new SimpleIndividual[this.popSize];
		Elite = new SimpleIndividual[this.elitism];
		
		SimpleIndividual best = null;

		
		for(int i = 0; i < this.popSize; i++){
			best = this.indb.buildNewRandomIndividual();
			P[i] = best;
		}

		this.mtf.assessFitness(best);
		System.out.println("Initial metric value: " + best.getMetricValue() );

		while(time-- > 0){


			for(SimpleIndividual pi: P){
				this.mtf.assessFitness(pi);

				if( best.getMetricValue() > pi.getMetricValue() ){
					System.out.println("Old Metric value:"  + best.getMetricValue());
					System.out.println("New metric value: " + pi.getMetricValue() );
					best = pi;
				}

		
			}
		
			Elite = this.select.selectManyFromMany(P, elitism);
			
			for(int i = 0; i < this.elitism; i++){
				Q[i] = Elite[i].copy();
			}
			 
			for(int i = 0; i < ((this.popSize - this.elitism)/2); i++){

				SimpleIndividual pi = select.selectOneFromMany( P );
				SimpleIndividual pj = select.selectOneFromMany( P );
				
				SimpleIndividual c[] = cross.crossOver(pi.copy(), pj.copy());
				
				mut.mutate(c[0]);
				mut.mutate(c[1]);

				
				Q[this.elitism + 2*i ] = c[0];
				Q[this.elitism + 2*i + 1] =  c[1];
				
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
		
		
		GeneticAlgorithmWithElitism melambe = new GeneticAlgorithmWithElitism(50, 10);
		melambe.indb.setGraph(new PreCalculedPathGraph(g));
		melambe.indb.setUpBuilder();
		
		melambe.doEvolvePopulation(1000);

	}

}
