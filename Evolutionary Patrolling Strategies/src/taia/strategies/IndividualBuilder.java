package taia.strategies;

import java.util.ArrayList;
import java.util.List;

import taia.PreCalculedPathGraph;
import taia.SimpleIndividual;
import yaps.util.RandomUtil;

public class IndividualBuilder {

	private int numAgents = 4;
	private PreCalculedPathGraph graph;
	private List<Integer> agentList;
	private IndividualBuilderType type = IndividualBuilderType.RANDOM_START;

	
	
	public IndividualBuilder(){}
	
	public IndividualBuilder(PreCalculedPathGraph g){
		this.graph = g;
		setUpBuilder();
	}
	
	public void setUpBuilder(){
		
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
	
	public SimpleIndividual buildNewRandomIndividual(){
		switch(this.type){
		case RANDOM_START:
			return new SimpleIndividual(agentList, graph);
		default:
			break;

		
		}
		return null;
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

	public IndividualBuilderType getType() {
		return type;
	}

	public void setType(IndividualBuilderType type) {
		this.type = type;
	}
	
}
