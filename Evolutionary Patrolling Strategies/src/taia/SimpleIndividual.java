package taia;

import java.util.HashMap;
import java.util.List;

public class SimpleIndividual implements Comparable<SimpleIndividual>{

	protected HashMap<Integer, AgentMATP> agents;
	protected PreCalculedPathGraph graph;
	protected List<Integer> agentList;
	//private double metricValue;
	private double[] multiObjectiveMetricValues;

	//PAS: Falta implementar a função hashCode(), para usar adequadamente em HashSets (como no MuLambda).

	private SimpleIndividual(){}
	

	public static SimpleIndividual buildIndividualData(List<Integer> fixedNodeList, HashMap<Integer, AgentMATP> agents, PreCalculedPathGraph graph){
		
		SimpleIndividual i = new SimpleIndividual();
		
		i.agentList = fixedNodeList;
		i.graph = graph;
		i.agents = agents;
	
		return i;
		
	}
	
	
	private SimpleIndividual(SimpleIndividual other){
		HashMap<Integer, AgentMATP> clone = new HashMap<Integer, AgentMATP>();
		for(Integer n: other.agentList){
			AgentMATP ag = (AgentMATP)other.agents.get(n).clone();
			clone.put(n, ag);
		}
		
		agents = clone;
		graph = other.graph;
		agentList = other.agentList;
		
	}
	
		
	public AgentMATP getAgentMATP(Integer agent){
		return this.agents.get(agent);
	} 

	public void setgetAgentMATP(Integer agent, AgentMATP rep){
		this.agents.put(agent, rep);
	}
	
	public void addRandomNodeAndRebuilOnAgent(Integer agent){
		this.agents.get(agent).addRandomNodeAndRebuildPath();
	}

	public void addRandomNodeWithSmallChangesOnAgent(Integer agent){
		this.agents.get(agent).addRandomNodeWithSmallChanges();
	}
	
	public void removeRandomNodeAndRebuildOnAgent(Integer agent){
		this.agents.get(agent).removeRandomNodeAndRebuildPath();
	}
	
	public void removeRandomNodeWithSmallChangesOnAgent(Integer agent){
		this.agents.get(agent).removeRandomNodeWithSmallChanges();
	}
	
	
	public void improveAgentByTwoChange(Integer agent){
		this.agents.get(agent).twoChangeImprove();
	}
	
	public List<Integer> getAgentsCentralNodesList(){
		return this.agentList;
	}
	
	
	public PreCalculedPathGraph getGraph() {
		return graph;
	}



	public void setGraph(PreCalculedPathGraph graph) {
		this.graph = graph;
	}



	@Override
	public boolean equals(Object obj) {
		
		if(obj == null){
			return false;
		}
		
		if(obj instanceof SimpleIndividual){
			SimpleIndividual other = (SimpleIndividual)obj;
			
			if(this.agentList.size() != other.agentList.size()){
				return false;
			}
			if(this.agents.size() != other.agents.size()){
				return false;
			}
			if(!this.graph.equals(other.graph)){
				return false;
			}
			
			for(int i = 0; i < this.agentList.size(); i++){
				
				Integer n = this.agentList.get(i);
				Integer m = other.agentList.get(i);
				
				if(!m.equals(n)){
					return false;
				}
				
				if(!other.agentList.contains(n)){
					return false;
				}
				
				if(!this.agents.get(n).equals(other.agents.get(n))){
					return false;
				}
				
				
			}
		}
		
		return true;
	}

	public SimpleIndividual copy() {
		return new SimpleIndividual(this);
	}

	public double getMetricValue() {
		if(this.multiObjectiveMetricValues == null){
			this.multiObjectiveMetricValues = new double[1];
			this.multiObjectiveMetricValues[0] = 0;
		}
		
		return this.multiObjectiveMetricValues[0];
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		for(double d: this.getMultiObjectiveMetricValues()){
			sb.append( " " + d + " ");
		}
		sb.append("]");
		
		return this.agents.toString() + "\nmetric: " + sb.toString() + "\n";
	}

	public void setMetricValue(double metricValue) {
		if(this.multiObjectiveMetricValues == null){
			this.multiObjectiveMetricValues = new double[1];
		}
		
		this.multiObjectiveMetricValues[0] = metricValue;
	}


	@Override
	public int compareTo(SimpleIndividual o) {
		if(this.getMetricValue() < o.getMetricValue()){
			return -1;
		}else if(this.getMetricValue() == o.getMetricValue()){
			return 0;
		}
		return 1;
	}


	public double[] getMultiObjectiveMetricValues() {
		return multiObjectiveMetricValues;
	}


	public void setMultiObjectiveMetricValues(double[] multiObjectiveValues) {
		this.multiObjectiveMetricValues = multiObjectiveValues;
	}
	
}
