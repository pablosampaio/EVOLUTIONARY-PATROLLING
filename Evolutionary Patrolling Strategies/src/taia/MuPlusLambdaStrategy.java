package taia;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import taia.strategies.IndividualBuilder;
import taia.strategies.Mutate;
import taia.strategies.Selection;
import taia.util.MetricFacility;
import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;

public class MuPlusLambdaStrategy {

	private int mu;
	private int lambda;
	private int factor;

	private Mutate mut = new Mutate();
	private MetricFacility mtf = new MetricFacility();
	public Selection select = new Selection();
	private IndividualBuilder individualBuilder;


	public MuPlusLambdaStrategy(int mu, int lambda){

		this.factor = (int)Math.ceil((1. * lambda)/mu);
		this.lambda = mu * this.factor;
		this.mu = mu;
	}



	public SimpleIndividual doMuLambdaStrategy(int time){

		Set<SimpleIndividual> P;


		P = new HashSet<SimpleIndividual>();

		SimpleIndividual[] Q;
		
		SimpleIndividual best = null;

		for(int i = 0; i < this.lambda; i++){
			best = this.individualBuilder.buildNewIndividual();
			P.add( best );
		}

		this.mtf.assessFitness(best);

		while(time-- > 0){

			Q = new SimpleIndividual[this.mu];
			int k = 0;
			
			for(SimpleIndividual pi: P){
				this.mtf.assessFitness(pi);
				
				if( best.getMetricValue() > pi.getMetricValue() ){
					System.out.println("New metric value: " + pi.getMetricValue() );
					best = pi;
				}

				Q[k] = pi;
				k++;
				

			}
			
			Q = Selection.muIndividualsBestFitnessSelection(Q, mu);
			
			P = new HashSet<SimpleIndividual>(this.lambda);
			
			
			for(SimpleIndividual qi: Q){
				P.add(qi);
				for(int i = 0; i < factor; i++){
					SimpleIndividual q = qi.copy();
					this.mut.mutate(q);
					P.add(q);
				}

			}



		}

		return best;



	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		Graph g = GraphReader.readAdjacencyList("./maps/island11", GraphDataRepr.LISTS);

	
		MuPlusLambdaStrategy melambe = new MuPlusLambdaStrategy(5, 15);
		melambe.individualBuilder = new IndividualBuilder(new PreCalculedPathGraph(g));
		
		
		melambe.doMuLambdaStrategy(1000);

	}

}
