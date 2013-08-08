package taia;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import taia.individual.GenericMATPIndividual;
import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;

public class MuLambdaStrategy {

	private int mu;
	private int lambda;
	private int factor;
	private SimulationConstructor sc;



	public MuLambdaStrategy(int mu, int lambda, SimulationConstructor sc){
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

        //PAS: Estranho - escolhe qualquer um como "best"? Funciona, mas foi proposital?
		for(int i = 0; i < this.lambda; i++){
			best = sc.buildNewRandomIndividual();
			P.add( best );
		}

		/*PAS: Nem precisa desse calculo da metrica, se o valor inicial da metrica for inicializado
		       com Integer.MAX_VALUE. Tambem fica estranho, mas funciona. 
		       Neste caso convem explicar aqui em um comentario. */  
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
		
		
		MuLambdaStrategy melambe = new MuLambdaStrategy(5, 15, sc);
		
		melambe.doMuLambdaStrategy(1000);

	}

}
