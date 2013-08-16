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

public class MuLambdaStrategy {

	private int mu;
	private int lambda;
	private int factor;
	private Mutate mut = new Mutate();
	private MetricFacility mtf = new MetricFacility();
	private IndividualBuilder individualBuilder = null;
	
	
	public MuLambdaStrategy(int mu, int lambda){
		//Asserts that lambda is multiple of mu
		//FIXME look for a ceil function for integers
		this.factor = (lambda/mu + 1);
		this.lambda = mu * this.factor;
		this.mu = mu;

	}

	private void insertOnQ(SimpleIndividual pi, SimpleIndividual[] Q){
		insertOnQ(pi, Q, 0);
	}

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

        //PAS: Estranho - escolhe qualquer um como "best"? Funciona, mas foi proposital?
		for(int i = 0; i < this.lambda; i++){
			best = this.individualBuilder.buildNewIndividual();
			P.add( best );
		}

		/*PAS: Nem precisa desse calculo da metrica, se o valor inicial da metrica for inicializado
		       com Integer.MAX_VALUE. Tambem fica estranho, mas funciona. 
		       Neste caso convem explicar aqui em um comentario. */
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

}
