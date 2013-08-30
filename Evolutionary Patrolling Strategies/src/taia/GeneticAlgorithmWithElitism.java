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
	private IndividualBuilder individualBuilder = null;
	private CrossOver	cross =  new CrossOver();
	private Selection select = new Selection();
	private int elitism;

	
	public GeneticAlgorithmWithElitism(Mutate mutate, MetricFacility mtf, IndividualBuilder indB, CrossOver cross, Selection select, int popSize, int elitism){
		
		this(popSize, elitism);
		this.mut = mutate;
		this.mtf = mtf;
		this.individualBuilder = indB;
		this.cross = cross;
		this.select = select;
	
	}
	
	public GeneticAlgorithmWithElitism(int popSize, int elistism){
		this.elitism = elistism;
		this.popSize = elistism + 2 * ((int) Math.ceil((1. * popSize)/2));;
	}

	public SimpleIndividual doEvolvePopulation(int time){


		SimpleIndividual[] P, Q, Elite;


		P = new SimpleIndividual[this.popSize];
		Q = new SimpleIndividual[this.popSize];
		Elite = new SimpleIndividual[this.elitism];
		
		SimpleIndividual best = null;

		
		for(int i = 0; i < this.popSize; i++){
			best = this.individualBuilder.buildNewIndividual();
			P[i] = best;
		}

		this.mtf.assessFitness(best);
		System.out.println("Initial metric value: " + best.getMetricValue() );

		while(time-- > 0){

			/* PAS: Como o livro ja previa, este loop esta tomando muito tempo.
			 * Poderiamos ao menos evitar recalcular fitness ja calculados. 
			 * Isso pode ser feito dentro de "assesFitness()".
			 */
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
				Q[i] = Elite[i].copy(); //PAS: 1) Nao precisa ser a copia. Gera overhead e (acho que) apaga o fitness calculado.
				                        //     2) Bug: ao escrever em Q, está apagando indivíduos de P. (Ver comentario ao final)
			}
			 
			for(int i = 0; i < ((this.popSize - this.elitism)/2); i++){

				SimpleIndividual pi = select.selectOneFromMany( P );
				SimpleIndividual pj = select.selectOneFromMany( P );
				
				SimpleIndividual c[] = cross.crossOver(pi.copy(), pj.copy());
				
				mut.mutate(c[0]);
				mut.mutate(c[1]);

				/*
				 * The indices look strange, we know that. If you look it closely 
				 * you will be convinced that it is just one (among others) 
				 * way to visit all empty elements in the list.
				 */
				Q[this.elitism + 2*i ] = c[0];
				Q[this.elitism + 2*i + 1] =  c[1];
				
			}

			P = Q; //PAS: Cuidado! A partir deste ponto, P e Q apontam para um mesmo array!

			if ((time+1) % 10 == 0) {
				System.out.printf("Remaining iterations: %d\n", time);
			}
		}

		return best;



	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {

		Graph g = GraphReader.readAdjacencyList("./maps/map_a.adj", GraphDataRepr.LISTS);
		
		
		GeneticAlgorithmWithElitism melambe = new GeneticAlgorithmWithElitism(50, 10);
		melambe.individualBuilder = new IndividualBuilder(new PreCalculedPathGraph(g), 3);
		
		melambe.doEvolvePopulation(1000);

	}

	public int getPopSize() {
		return popSize;
	}

}
