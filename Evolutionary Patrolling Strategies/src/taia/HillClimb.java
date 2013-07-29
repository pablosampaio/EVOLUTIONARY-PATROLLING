package taia;

import java.io.IOException;
import java.util.ArrayList;

import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;
import yaps.metrics.IntervalMetricsReport;
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


	public SimpleIndividual doHillClimb(SimpleIndividual s, int numberIterations){
		
		SimpleIndividual r = null;
		
		VisitsList vs = s.generateVisitList(simulationTime);
		
		double bestMetric, metric;
		
		IntervalMetricsReport intervalReport = new IntervalMetricsReport(numOFNodes, 1, simulationTime, vs);
		IntervalMetricsReport rIntervalReport;
		
		bestMetric = intervalReport.getAverageInterval();
		
		System.out.println("Initial metric value:  " + bestMetric);
		
		while(numberIterations-- > 0){
			r = s.tweakCopy();
			System.out.println("Before Tweak:"+s+"\nAfter Tweak:"+r);
			rIntervalReport = new IntervalMetricsReport(numOFNodes, 1, simulationTime, r.generateVisitList(simulationTime));
			metric = rIntervalReport.getAverageInterval();
			
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
		
		
		Graph g = GraphReader.readAdjacencyList("./maps/island11");
		
		g.changeRepresentation(GraphDataRepr.LISTS);
		
		HillClimb ch = new HillClimb(g, 1000);
		
		ArrayList<Integer> agentList = new ArrayList<>();
		
		agentList.add(1);
		agentList.add(5);
		agentList.add(9);
		
		SimpleIndividual s = new SimpleIndividual(agentList , new PreCalculedPathGraph(g));
		
		System.out.println("\n================================");
		System.out.println("Initial configuration:");
		System.out.println(s);
		System.out.println("================================\n");
		
		s = ch.doHillClimb(s, 1000);
		
		System.out.println("\n================================");
		System.out.println("Final configuration:");
		System.out.println(s);
		System.out.println("================================\n");
		
		
	}
	

}
