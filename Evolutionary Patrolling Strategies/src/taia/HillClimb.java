package taia;

import java.io.IOException;

import taia.strategies.IndividualBuilder;
import taia.strategies.Mutate;
import taia.util.MetricFacility;
import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;

public class HillClimb {
	
	private int simulationTime = 10000;
	private int numOFNodes;
	private PreCalculedPathGraph graph;
	
	private double bestMetric;

	private Mutate mut = new Mutate();
	private MetricFacility mtf = new MetricFacility();
	private IndividualBuilder indb = new IndividualBuilder();


	public Mutate getMutate() { return mut;}

	public void setMutate(Mutate mut) { this.mut = mut; }

	public MetricFacility getMetricFacility() { return mtf; }

	public void setMetricFacility(MetricFacility mtf) { this.mtf = mtf; }

	public IndividualBuilder getIndividualBuilder() { return indb; 	}

	public void setIndividualBuilder(IndividualBuilder indb) { this.indb = indb; }

	
	public HillClimb(String graphFileName) throws IOException{
		 this(GraphReader.readAdjacencyList(graphFileName));
	}
	
	
	public HillClimb(String graphFileName, int simulationTime) throws IOException{
		this(GraphReader.readAdjacencyList(graphFileName));
		this.simulationTime = simulationTime;

	}

	public HillClimb(Graph g, int simulationTime){
		this(g);
		this.mtf.setSimulationTime(simulationTime);
		this.simulationTime = simulationTime;
	}
	
	public HillClimb(Graph g){
		this.numOFNodes = g.getNumNodes();
		this.graph = new PreCalculedPathGraph(g);
		this.indb.setGraph(graph);
		this.indb.setUpBuilder();
	}
	
	public int getSimulationTime() {
		return simulationTime;
	}


	public PreCalculedPathGraph getGraph() {
		return graph;
	}
	
	public double getBestMetric() {
		
		return this.bestMetric;
		
	}
	
	public int getNumOFNodes() {
		return numOFNodes;
	}


	public SimpleIndividual doHillClimb(SimpleIndividual s, int numberIterations){
		
		SimpleIndividual r = null;
		
		
		
		double bestMetric, metric;
		
	
		bestMetric = mtf.assessFitness(s);
		
		System.out.println("Initial metric value:  " + bestMetric);
		//System.out.println(intervalReport);
		
		while(numberIterations-- > 0){
			//System.out.println("Before Tweak:"+s);
			r = s.copy();
			mut.mutate(r);
			//System.out.println("After Tweak:"+r);
			
			metric = mtf.assessFitness(r);
			
			if(metric < bestMetric){
				bestMetric = metric;
				s = r;
				System.out.println("New metric value:  " + bestMetric);
			}
			
			
		}
		
		this.bestMetric = bestMetric;
	
		return s;
	
	}
	
	public SimpleIndividual doHillClimb(int numberIterations) {
		//Random initial solution generated
		return doHillClimb(indb.buildNewRandomIndividual(), numberIterations);
		
	}
	
	public static void main(String[] args) throws IOException {
		

		Graph g = GraphReader.readAdjacencyList("./maps/island11", GraphDataRepr.LISTS);
		
		HillClimb ch = new HillClimb(g, 1000);
		
		/*ArrayList<Integer> agentList = new ArrayList<Integer>();
		
		agentList.add(1);
		agentList.add(5);
		agentList.add(9);
		
		SimpleIndividual s = new SimpleIndividual(agentList , new PreCalculedPathGraph(g));*/
		
		
		//ch.indb.setIndividualConstructorParameters(new PreCalculedPathGraph(g));
		ch.indb.setNumAgents(3);
	
		SimpleIndividual result = ch.doHillClimb(100000);
		
		System.out.println("\n================================");
		System.out.println("Final configuration:");
		System.out.println(result);
		System.out.println("================================\n");
		
		
	}
	

}
