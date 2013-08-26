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

public class MuLambdaStrategy {

	private int mu;
	private int lambda;
	private int factor;
	private Mutate mut = new Mutate();
	private MetricFacility mtf = new MetricFacility();
	private IndividualBuilder individualBuilder = null;
	
	public MuLambdaStrategy(){}
	
	public MuLambdaStrategy(Mutate mutate, MetricFacility mtf, IndividualBuilder indB, int mu, int lambda){
		
		this(mu, lambda);
		this.mut = mutate;
		this.mtf = mtf;
		this.individualBuilder = indB;
		

	}
	
	public MuLambdaStrategy(int mu, int lambda){

		this.factor = (int) Math.ceil((1. * lambda)/mu);
		this.lambda = mu * this.factor;
		this.mu = mu;

	}

	public SimpleIndividual doMuLambdaStrategy(int time){

		Set<SimpleIndividual> P = new HashSet<SimpleIndividual>(this.lambda);

		SimpleIndividual[] Q = new SimpleIndividual[this.lambda];
		SimpleIndividual[] Qs;
		
		SimpleIndividual best = null;

        
		for(int i = 0; i < this.lambda; i++){
			best = this.individualBuilder.buildNewIndividual();
			P.add( best );
		}

		/*
		 * Dirty and Quick! You could set best to null here and add a null test
		 * latter to calculate who is the best solution... or you can choose anyone
		 * here and let the test do his job.  
		 */
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
					
			P.clear();
			Qs = Selection.muIndividualsBestFitnessSelection(Q, mu);
			
			for(SimpleIndividual qi: Qs){
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
		
		
		MuLambdaStrategy melambe = new MuLambdaStrategy(5, 15);
		
		
		melambe.individualBuilder = new IndividualBuilder((new PreCalculedPathGraph(g)));
		
		
		melambe.doMuLambdaStrategy(1000);

	}

	public int getLambda() {
		return lambda;
	}

}
