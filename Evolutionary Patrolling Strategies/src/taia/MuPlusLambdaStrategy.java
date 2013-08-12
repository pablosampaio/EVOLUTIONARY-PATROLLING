package taia;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import taia.strategies.IndividualBuilder;
import taia.strategies.Mutate;
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
	private IndividualBuilder indb = new IndividualBuilder();


	public MuPlusLambdaStrategy(int mu, int lambda){
		//Asserts that lambda is multiple of mu
		//FIXME look for a ceil function for integers
		this.factor = (lambda/mu + 1);
		this.lambda = mu * this.factor;
		this.mu = mu;
	}

	private void insertOnQ(SimpleIndividual pi, SimpleIndividual[] Q){
		insertOnQ(pi, Q, 0);
	}

    /* PAS: Este e um procedimento recursivo não muito eficiente.
     * E melhor inserir tudo no fim e ordenar depois (e.g. com quicksort).
     * Para facilitar, use LinkedList e chame o metodo Collections.sort().
     * A rigor, isso eh um tipo de selecao, que poderia ser incluido na
     * classe Selection.
     */
	private void insertOnQ(SimpleIndividual pi, SimpleIndividual[] Q, int i){
		if(i >= this.mu){
			return;
		}

		if(Q[i] == null){
			Q[i] = pi;
			return;
		}

		if(pi.getMetricValue() < Q[i].getMetricValue()){
			SimpleIndividual pj = Q[i];
			Q[i] = pi;
			insertOnQ(pj, Q, i + 1);
			return;
		}

		insertOnQ(pi, Q, i + 1);
		return;

	}

	public SimpleIndividual doMuLambdaStrategy(int time){

		Set<SimpleIndividual> P;


		P = new HashSet<SimpleIndividual>();

		SimpleIndividual[] Q;
		
		SimpleIndividual best = null;

		//VT&DM Shouldn't be lambda +_mu?
		for(int i = 0; i < this.lambda; i++){
			best = this.indb.buildNewRandomIndividual();
			P.add( best );
		}

		this.mtf.assessFitness(best);

		while(time-- > 0){

			Q = new SimpleIndividual[this.mu];

			for(SimpleIndividual pi: P){
				this.mtf.assessFitness(pi);
				
				if( best.getMetricValue() > pi.getMetricValue() ){
					System.out.println("New metric value: " + pi.getMetricValue() );
					best = pi;
				}

				insertOnQ(pi, Q);

			}
			
		
			
			P = new HashSet<SimpleIndividual>();
			
			
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
		melambe.indb.setGraph(new PreCalculedPathGraph(g));
		melambe.indb.setUpBuilder();
		
		melambe.doMuLambdaStrategy(1000);

	}

}
