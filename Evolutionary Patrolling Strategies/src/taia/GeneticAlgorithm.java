package taia;

import java.io.IOException;
import java.util.List;

import taia.strategies.CrossOver;
import taia.strategies.IndividualBuilder;
import taia.strategies.Mutate;
import taia.strategies.Selection;
import taia.util.ListUtil;
import taia.util.MetricFacility;
import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;
import yaps.util.RandomUtil;

public class GeneticAlgorithm {

	private int popSize;
	private Mutate mut = new Mutate();
	private MetricFacility mtf = new MetricFacility();
	private IndividualBuilder individualBuilder = null;
	private CrossOver	cross =  new CrossOver();
	private Selection select = new Selection();

	public GeneticAlgorithm(Mutate mutate, MetricFacility mtf, IndividualBuilder indB, CrossOver cross, Selection select, int popSize){
		
		this(popSize);
		this.mut = mutate;
		this.mtf = mtf;
		this.individualBuilder = indB;
		this.cross = cross;
		this.select = select;
	
	}
	
	public GeneticAlgorithm(int popSize){
		this.popSize = 2 * ((int) Math.ceil((1. * popSize)/2));
	}

	public SimpleIndividual doEvolvePopulation(int time){

		
		SimpleIndividual[] P, Q;


		P = new SimpleIndividual[this.popSize];
		Q = new SimpleIndividual[this.popSize];
		
		List<Integer> indexList =ListUtil.createIndexList(0, this.popSize, 1);
		
		SimpleIndividual best = null;

		
		for(int i = 0; i < this.popSize; i++){
			best = this.individualBuilder.buildNewIndividual();
			P[i] = best;
		}

		this.mtf.assessFitness(best);
		System.out.println("Initial metric value: " + best.getMetricValue() );

		while(time-- > 0){


			for(SimpleIndividual pi: P){
				this.mtf.assessFitness(pi);

				if( best.getMetricValue() > pi.getMetricValue() ){
					System.out.println("New metric value: " + pi.getMetricValue() );
					best = pi;
				}

		
			}
			
			
			RandomUtil.shuffleInline(indexList);
			 
			for(int i = 0; i < (this.popSize/2); i++){

				SimpleIndividual pi = select.selectOneFromMany( P );
				SimpleIndividual pj = select.selectOneFromMany( P );
				
				SimpleIndividual c[] = cross.crossOver(pi.copy(), pj.copy());
				
				mut.mutate(c[0]);
				mut.mutate(c[1]);

				
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

		Graph g = GraphReader.readAdjacencyList("./maps/map_a.adj", GraphDataRepr.LISTS);
		
		
		GeneticAlgorithm melambe = new GeneticAlgorithm(200);
		melambe.individualBuilder = new IndividualBuilder(new PreCalculedPathGraph(g), 3);
		
		
		melambe.doEvolvePopulation(1000);

	}

	public int getPopulationSize() {
		return this.popSize;
	}

}
