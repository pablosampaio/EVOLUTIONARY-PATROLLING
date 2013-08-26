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

	public MuPlusLambdaStrategy(Mutate mutate, MetricFacility mtf, IndividualBuilder indB, int mu, int lambda){
		
		this(mu, lambda);
		this.mut = mutate;
		this.mtf = mtf;
		this.individualBuilder = indB;
		

	}
	

	public SimpleIndividual doMuLambdaStrategy(int time){

		Set<SimpleIndividual> P = new HashSet<SimpleIndividual>(this.lambda);

		SimpleIndividual[] Q = new SimpleIndividual[this.lambda];
		
		SimpleIndividual best = null;

		for(int i = 0; i < this.lambda; i++){
			best = this.individualBuilder.buildNewIndividual();
			P.add( best );
		}

		this.mtf.assessFitness(best);

		while(time-- > 0){
		
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
			
			Selection.sortByFitness(Q);
			
			P.clear();
			
			for(int i = 0; i < mu; i++){
				SimpleIndividual qi = Q[i];
				P.add(qi);
				for(int j = 1; j < factor; j++){
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

	
		MuPlusLambdaStrategy melambe = new MuPlusLambdaStrategy(3, 6);
		melambe.individualBuilder = new IndividualBuilder(new PreCalculedPathGraph(g));
		
		
		melambe.doMuLambdaStrategy(20);

	}

	public int getLambda() {
		return lambda;
	}

}
