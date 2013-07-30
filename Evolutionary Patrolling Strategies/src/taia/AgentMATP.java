package taia;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import taia.util.ListUtil;
import yaps.graph_library.InducedSubGraph;
import yaps.graph_library.Path;
import yaps.graph_library.PathBuilder;
import yaps.util.RandomUtil;

public class AgentMATP {

	private Path path;
	private Integer centerNode;
	private HashSet<Integer> coveredNodesSet;
	private int buildMethod = PathBuilder.NearestInsertionMethod;
	private InducedSubGraph inducedSubGraph;


	public int getBuildMethod() {
		return buildMethod;
	}


	public void setBuildMethod(int buildMethod) {

		switch(buildMethod){

		case PathBuilder.NearestInsertionMethod:
		case PathBuilder.NearestNeighborMethod:
			this.buildMethod = buildMethod;
			break;
		default:
			this.buildMethod = PathBuilder.NearestInsertionMethod;
		}	

	}

	private AgentMATP(){}

	public AgentMATP(Integer center, InducedSubGraph graph){
		this.centerNode = center;
		this.inducedSubGraph = graph;
		this.coveredNodesSet = new HashSet<Integer>(this.inducedSubGraph.getNodesList());
		this.builNewPath();
	}


	public AgentMATP(Integer center, Path path, InducedSubGraph graph){
		this.path = path;
		this.centerNode = center;
		this.inducedSubGraph = graph;
		this.coveredNodesSet = new HashSet<Integer>(path);
	}


	public void addRandomNodeAndRebuildPath(){

		if(this.coveredNodesSet.size() == this.inducedSubGraph.getFatherGraph().getNumNodes()){
			return;
		}

		List<Integer> suffledList = RandomUtil.shuffle(this.inducedSubGraph.getFatherGraph().getNodesList());

		Integer node = null;

		for(Integer n: suffledList){
			if(!this.coveredNodesSet.contains(n)){
				node = n;
				this.coveredNodesSet.add(n);
				break;
			}
		}

		if(node == null){
			return;
		}


		Path path = inducedSubGraph.getAllPaths().getPath(this.centerNode, node);

		for(Integer n: path){

			if(!this.coveredNodesSet.contains(n)){
				node = n;
				this.coveredNodesSet.add(n);	
			}
		}

		this.inducedSubGraph = new InducedSubGraph(new ArrayList<Integer>(this.coveredNodesSet) , this.inducedSubGraph.getFatherGraph());

		this.builNewPath();

	}


	public void removeRandomNodeAndRebuildPath(){

		if(this.coveredNodesSet.size() < 2){
			return;
		}

		LinkedList<Integer> nodes = new LinkedList<Integer>( this.coveredNodesSet );

		Integer electedNode;
		InducedSubGraph sg;

		List<Integer> shuffeledNodeList = RandomUtil.shuffle(ListUtil.createIndexList(0, nodes.size(), 1));

		for(int i: shuffeledNodeList){

			electedNode = nodes.remove(i);

			if(electedNode.equals(this.centerNode)){
				nodes.add(i, electedNode);
				continue;
			}

			sg = new InducedSubGraph(nodes, this.inducedSubGraph.getFatherGraph());

			if(sg.isConnected()){
				this.inducedSubGraph = sg;
				this.coveredNodesSet.remove(electedNode);
				this.builNewPath();
				return;
			}

			nodes.add(i, electedNode);

		}

	}


	public void builNewPath(){

		switch(buildMethod){

		case PathBuilder.NearestInsertionMethod:
			this.path = PathBuilder.nearestInsertionMethod(inducedSubGraph);
			break;
		case PathBuilder.NearestNeighborMethod:
			this.path = PathBuilder.nearestNeighborMethod(inducedSubGraph);
			break;
		default:
			this.path = PathBuilder.nearestInsertionMethod(inducedSubGraph);
			break;
		}	


	}


	public void applyRandomTwoChange(){

		//At least four nodes!
		if(this.path.size() < 5){
			return;
		}


		int e1 = 0;
		int e2 = 0;
		int aux;

		while( (e1 == e2) || (e1 + 1 == e2) ){

			e1 = RandomUtil.chooseInteger(0, this.path.size() - 2);
			e2 = RandomUtil.chooseInteger(0, this.path.size() - 2);

			if(e1 > e2){
				aux = e1;
				e1 = e2;
				e2 = aux;
			}

		}

		this.path = PathBuilder.twoChange(e1, e2, this.path, this.inducedSubGraph);
	}


	public Path getPath() {
		return path;
	}


	public Integer getCenterNode() {
		return centerNode;
	}


	public HashSet<Integer> getCoveredNodesSet() {
		return coveredNodesSet;
	}


	public InducedSubGraph getInducedSubGraph() {
		return inducedSubGraph;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Object clone() throws CloneNotSupportedException {
		AgentMATP other = new AgentMATP();

		other.buildMethod = this.buildMethod;
		other.centerNode = this.centerNode;
		other.coveredNodesSet = (HashSet<Integer>)this.coveredNodesSet.clone();
		other.inducedSubGraph = (InducedSubGraph)this.inducedSubGraph.clone();

		return super.clone();
	}


	@Override
	public boolean equals(Object obj) {
		if(obj == null){
			return false;
		}

		if(obj instanceof AgentMATP){
			AgentMATP other = (AgentMATP)obj;

			if(this.path.size() != other.path.size()){
				return false;
			}

			if(this.buildMethod != other.buildMethod){
				return false;
			}

			if(this.coveredNodesSet.size() != other.coveredNodesSet.size()){
				return false;
			}

			if(!this.path.equals(other.path)){
				return false;
			}

			if(!this.coveredNodesSet.equals(other.coveredNodesSet)){
				return false;
			}
			
			if(!this.inducedSubGraph.equals(other.inducedSubGraph)){
				return false;
			}
			
			return true;
			
		}
		return false;
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append("Center node " + this.centerNode);
		sb.append("\n");
		sb.append("Path: ");
		sb.append(this.path);
		sb.append("\n");

		return sb.toString();
	}





}
