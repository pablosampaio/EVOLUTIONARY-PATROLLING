package taia;

import java.io.IOException;

import taia.individual.GenericMATPIndividual;
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


	public GenericMATPIndividual doHillClimb(GenericMATPIndividual s, int numberIterations, Metric metrica){
		
		GenericMATPIndividual r = null;
		
		VisitsList vs = s.generateVisitList(simulationTime);
		
		double bestMetric, metric;
		Metric metricCaltulator = metrica;
		
		
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
	
	public GenericMATPIndividual doHillClimb(int numberIterations, Metric metrica, SimulationConstructor sc) {

		
		GenericMATPIndividual s = sc.buildNewRandomIndividual();

		//Random initial solution generated
		
		
		GenericMATPIndividual r = null;
		
		VisitsList vs = s.generateVisitList(simulationTime);
		
		double bestMetric, metric;
		Metric metricCaltulator = metrica;
		
		
		bestMetric = metricCaltulator.calculate(vs, numOFNodes, 1, simulationTime);
		
		System.out.println("Initial Best Individual: "+s+"\n"+
				"Initial Best Metric Value: "+bestMetric
				);
		//System.out.println(intervalReport);
		
		while(numberIterations-- > 0){
			//System.out.println("Before Tweak:"+s);
			r = s.tweakCopy();
			//System.out.println("After Tweak:"+r);
			
			metric = metricCaltulator.calculate( r.generateVisitList(simulationTime), numOFNodes, 1, simulationTime);
			
			
			//System.out.println("New metric value:  " + metric);
			
			
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
		

		Graph g = GraphReader.readAdjacencyList("./maps/island11", GraphDataRepr.LISTS);
		
		HillClimb ch = new HillClimb(g, 1000);
		
		/*ArrayList<Integer> agentList = new ArrayList<Integer>();
		
		agentList.add(1);
		agentList.add(5);
		agentList.add(9);
		
		SimpleIndividual s = new SimpleIndividual(agentList , new PreCalculedPathGraph(g));*/
		
		SimulationConstructor sc = new SimulationConstructor();
		sc.setIndividualConstructorParameters(new PreCalculedPathGraph(g));
		sc.setNumAgents(3);
		GenericMATPIndividual result = ch.doHillClimb(100000, Metric.MAXIMUM_INTERVAL, sc);
		
		System.out.println("\n================================");
		System.out.println("Final configuration:");
		System.out.println(result);
		System.out.println("================================\n");
		
		
	}
	

}
