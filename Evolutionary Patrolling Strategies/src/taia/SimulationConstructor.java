package taia;

import java.util.ArrayList;
import java.util.List;

import taia.individual.GenericMATPIndividual;
import taia.individual.IndividualType;
import taia.individual.SimpleIndividual;
import yaps.metrics.Metric;
import yaps.util.RandomUtil;

public class SimulationConstructor {
	
	
	private int simulationTime = 1000;
	
	private IndividualType indType = IndividualType.SIMPLEINDIVIDUAL;
	private int numAgents = 4;
	private PreCalculedPathGraph graph;
	private List<Integer> agentList;
	
	private Metric metric = Metric.MAXIMUM_INTERVAL;
	
	public void setIndividualConstructorParameters(PreCalculedPathGraph graph){
		this.graph = graph;
		
		ArrayList<Integer> agentList = new ArrayList<Integer>();

		for(int i = 0; i < numAgents; i++) {

			int agent = RandomUtil.chooseInteger(0, graph.getNumNodes() -1);

			while(agentList.contains(agent)){
				agent = RandomUtil.chooseInteger(0, graph.getNumNodes() -1);
			}

			agentList.add(agent);

		}
		this.agentList = agentList;
	}
	
	public void setIndividualConstructorParameters( List<Integer> agentList, PreCalculedPathGraph graph ){
		this.graph = graph;
		this.agentList = agentList;
	}
	
	public GenericMATPIndividual buildNewRandomIndividual(){
		switch(this.indType){
		case SIMPLEINDIVIDUAL:
			return new SimpleIndividual(agentList, graph);
		default:
			break;
		
		}
		return null;
	}
	
	public IndividualType getIndType() {
		return indType;
	}

	public void setIndType(IndividualType indType) {
		this.indType = indType;
	}

	public int getNumAgents() {
		return numAgents;
	}

	public void setNumAgents(int numAgents) {
		this.numAgents = numAgents;
	}

	public PreCalculedPathGraph getGraph() {
		return graph;
	}

	public void setGraph(PreCalculedPathGraph graph) {
		this.graph = graph;
	}

	public List<Integer> getAgentList() {
		return agentList;
	}

	public void setAgentList(List<Integer> agentList) {
		this.agentList = agentList;
	}

	public int getSimulationTime() {
		return simulationTime;
	}

	public void setSimulationTime(int simulationTime) {
		this.simulationTime = simulationTime;
	}

	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}
	
}
