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
	
	private IndividualBuilderCenterChooseType centerType = IndividualBuilderCenterChooseType.APROXIMATED_MAXIMUM_DISTANCE;
	private IndividualBuilderPartitionType partitionType = IndividualBuilderPartitionType.RANDOM_START;
	private IndividualBuilderPathConstructorType pathType = IndividualBuilderPathConstructorType.NearestNeighborMethod;
	
	
	public void setConstructorParameters(IndividualBuilderCenterChooseType centerType, IndividualBuilderPartitionType partitionType, IndividualBuilderPathConstructorType pathType){
		this.partitionType = partitionType;
		this.centerType = centerType;
		this.pathType = pathType;
		this.agentList = null;
	}
	
	
		
	public IndividualBuilder(PreCalculedPathGraph g){
		this.graph = g;
		//setUpBuilder();
	}
	
	public IndividualBuilder(PreCalculedPathGraph g, int numberOfAgents){
		this.numAgents = numberOfAgents;
		this.graph = g;
		//setUpBuilder();
	}
	
	public void setUpBuilder(){
		

		switch(this.centerType){
		case APROXIMATED_MAXIMUM_DISTANCE:
			
			this.agentList = GraphEquipartition.maximumDistance(this.numAgents, this.graph);
			
			
			break;
		case RANDOM:
			
			ArrayList<Integer> agentList = new ArrayList<Integer>();
			
			for(int i = 0; i < numAgents; i++) {

				int agent = RandomUtil.chooseInteger(0, graph.getNumNodes() -1);

				while(agentList.contains(agent)){
					agent = RandomUtil.chooseInteger(0, graph.getNumNodes() -1);
				}

				agentList.add(agent);

			}
			
			this.agentList = agentList;
			break;
			
		default:
			this.agentList = GraphEquipartition.maximumDistance(this.numAgents, this.graph);
			break;
		}
		

	}
	
	/*public void setIndividualConstructorParameters( List<Integer> agentList, PreCalculedPathGraph graph ){
		this.graph = graph;
		this.agentList = agentList;
	}*/
	
	public SimpleIndividual buildNewIndividual(){
		
		if(this.agentList == null){
			this.setUpBuilder();
		}
		
		
		switch(this.partitionType){
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


	public PreCalculedPathGraph getGraph() {
		return graph;
	}

	public void setGraph(PreCalculedPathGraph graph) {
		this.graph = graph;
	}

	public IndividualBuilderPartitionType getType() {
		return partitionType;
	}

	public void setType(IndividualBuilderPartitionType type) {
		this.partitionType = type;
	}
	
}
