package taia;

import java.io.IOException;
import java.util.ArrayList;

import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;
import yaps.metrics.Metric;
import yaps.metrics.VisitsList;

public class HillClimb {
	
	private int simulationTime = 10000;
	private int numOFNodes;
	private PreCalculedPathGraph graph;
	
	private double bestMetric;

	
	
	
	public HillClimb(String graphFileName) throws IOException{
		 this(GraphReader.readAdjacencyList(graphFileName));
	}
	
	
	public HillClimb(String graphFileName, int simulationTime) throws IOException{
		this(GraphReader.readAdjacencyList(graphFileName));
		this.simulationTime = simulationTime;

	}

	public HillClimb(Graph g, int simulationTime){
		this(g);
		this.simulationTime = simulationTime;
	}
	
	public HillClimb(Graph g){
		this.numOFNodes = g.getNumNodes();
		this.graph = new PreCalculedPathGraph(g);
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


	public SimpleIndividual doHillClimb(SimpleIndividual s, int numberIterations, Metric metrica){
		
		SimpleIndividual r = null;
		
		VisitsList vs = s.generateVisitList(simulationTime);
		
		double bestMetric, metric;
		Metric metricCaltulator = metrica;
		
		
		// PAS: As metricas boas individualmente sao: maximum interval / quadratic mean of intervals.
		// Ajustar tambem em HillClimbWithRandomRestarts 
		bestMetric = metricCaltulator.calculate(vs, numOFNodes, 1, simulationTime);
		
		System.out.println("Initial metric value:  " + bestMetric);
		//System.out.println(intervalReport);
		
		while(numberIterations-- > 0){
			//System.out.println("Before Tweak:"+s);
			r = s.tweakCopy();
			//System.out.println("After Tweak:"+r);
			
			metric = metricCaltulator.calculate( r.generateVisitList(simulationTime), numOFNodes, 1, simulationTime);
			
			if(metric < bestMetric){
				bestMetric = metric;
				s = r;
				System.out.println("New metric value:  " + bestMetric);
			}
			
			
		}
		
		this.bestMetric = bestMetric;
	
		return s;
	
	}
	
	public static void main(String[] args) throws IOException {
		
		//PAS: simplifiquei a chamada. mas não entendi pq vcs só querem listas. 
		//o default é o mais otimizado, pois mantem listas AND matriz!
		Graph g = GraphReader.readAdjacencyList("./maps/island11", GraphDataRepr.LISTS);
		
		HillClimb ch = new HillClimb(g, 1000);
		
		ArrayList<Integer> agentList = new ArrayList<Integer>();
		
		agentList.add(1);
		agentList.add(5);
		agentList.add(9);
		
		SimpleIndividual s = new SimpleIndividual(agentList , new PreCalculedPathGraph(g));
		
		System.out.println("\n================================");
		System.out.println("Initial configuration:");
		System.out.println(s);
		System.out.println("================================\n");
		
		s = ch.doHillClimb(s, 1000, Metric.MAXIMUM_INTERVAL);
		
		System.out.println("\n================================");
		System.out.println("Final configuration:");
		System.out.println(s);
		System.out.println("================================\n");
		
		
	}
	

}
