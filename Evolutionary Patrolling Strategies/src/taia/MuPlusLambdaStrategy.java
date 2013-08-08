package taia;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import taia.individual.GenericMATPIndividual;
import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;

public class MuPlusLambdaStrategy {

	private int mu;
	private int lambda;
	private int factor;
	private SimulationConstructor sc;



	public MuPlusLambdaStrategy(int mu, int lambda, SimulationConstructor sc){
		//Asserts that lambda is multiple of mu
		//FIXME look for a ceil function for integers
		this.factor = (lambda/mu + 1);
		this.lambda = mu * this.factor;
		this.mu = mu;
		this.sc = sc;
	}

	private void insertOnQ(GenericMATPIndividual pi, GenericMATPIndividual[] Q){
		insertOnQ(pi, Q, 0);
	}

    /* PAS: Este e um procedimento recursivo não muito eficiente.
     * E melhor inserir tudo no fim e ordenar depois (e.g. com quicksort).
     * Para facilitar, use LinkedList e chame o metodo Collections.sort().
     * A rigor, isso eh um tipo de selecao, que poderia ser incluido na
     * classe Selection.
     */
	private void insertOnQ(GenericMATPIndividual pi, GenericMATPIndividual[] Q, int i){
		if(i >= this.mu){
			return;
		}

		if(Q[i] == null){
			Q[i] = pi;
			return;
		}

		if(pi.getMetricValue() < Q[i].getMetricValue()){
			GenericMATPIndividual pj = Q[i];
			Q[i] = pi;
			insertOnQ(pj, Q, i + 1);
			return;
		}

		insertOnQ(pi, Q, i + 1);
		return;

	}

	public GenericMATPIndividual doMuLambdaStrategy(int time){

		Set<GenericMATPIndividual> P;


		P = new HashSet<GenericMATPIndividual>();

		GenericMATPIndividual[] Q;
		
		GenericMATPIndividual best = null;

		//VT&DM Shouldn't be lambda +_mu?
		for(int i = 0; i < this.lambda; i++){
			best = sc.buildNewRandomIndividual();
			P.add( best );
		}

		best.assessFitness(sc.getMetric(), sc.getSimulationTime());

		while(time-- > 0){

			Q = new GenericMATPIndividual[this.mu];

			for(GenericMATPIndividual pi: P){
				pi.assessFitness(sc.getMetric(), sc.getSimulationTime());
				if( best.getMetricValue() > pi.getMetricValue() ){
					System.out.println("New metric value: " + pi.getMetricValue() );
					best = pi;
				}

				insertOnQ(pi, Q);

			}
			
		
			
			P = new HashSet<GenericMATPIndividual>();
			
			
			for(GenericMATPIndividual qi: Q){
				P.add(qi);
				for(int i = 0; i < factor; i++){
					P.add(qi.tweakCopy());
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
		SimulationConstructor sc = new SimulationConstructor();
		sc.setNumAgents(3);
		sc.setIndividualConstructorParameters(new PreCalculedPathGraph(g));
		
		
		MuPlusLambdaStrategy melambe = new MuPlusLambdaStrategy(5, 15, sc);
		
		melambe.doMuLambdaStrategy(1000);

	}

}
