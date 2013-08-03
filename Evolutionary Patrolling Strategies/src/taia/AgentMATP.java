package taia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import taia.util.ListUtil;
import yaps.graph_library.GraphReader;
import yaps.graph_library.InducedSubGraph;
import yaps.graph_library.Path;
import yaps.graph_library.PathBuilder;
import yaps.util.RandomUtil;

//PAS: Nunca usada?
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



	public void removeRandomNodeWithSmallChanges(){

		if(this.path.size() <= 3){
			return;
		}

		//1 - 2 - 3 - 2 -  1

		int nodeIndex;
		List<Integer> indexList = ListUtil.createIndexList(0, this.path.size() - 1, 1);

		do{
			nodeIndex = ListUtil.chooseAtRandom( indexList );	
		}while(path.get(nodeIndex).equals(centerNode));

		if(nodeIndex == 0 || nodeIndex == (this.path.size() - 1)){
			//1x - 2 - 3 - 4 - x1x
			  
			this.path.removeFirst();
			this.path.removeLast();

			Path p = this.inducedSubGraph.getAllPaths().getPath(this.path.peekLast(), this.path.peekFirst());

			p.removeFirst();

			for(Integer np: p){
				//do field medal
				this.path.addLast(np);
			}

			this.coveredNodesSet = new HashSet<Integer>(this.path);

			return;
		}


		Path p = this.inducedSubGraph.getAllPaths().getPath( this.path.get(nodeIndex - 1), this.path.get(nodeIndex + 1));
		p.removeFirst();
		p.removeLast();

		//x1x - 2 - 3 - 4 -  X1X 
		this.path.remove(nodeIndex);

		for(Integer np: p){
			//do field medal
			this.path.add(nodeIndex, np);
		}		

		this.coveredNodesSet = new HashSet<Integer>(this.path);


	}


	public void addRandomNodeWithSmallChanges(){

		PreCalculedPathGraph fatherG = this.inducedSubGraph.getFatherGraph();

		if(this.coveredNodesSet.size() == fatherG.getNumNodes()){
			return;
		}


		Integer node = null;
		List<Integer> shuffeledNodeList = RandomUtil.shuffle(fatherG.getNodesList());

		for(Integer n: shuffeledNodeList){
			if(!this.coveredNodesSet.contains(n)){
				node = n;
				this.coveredNodesSet.add(n);
				break;
			}
		}

		if(node == null){
			return;
		}


		int i  = 0;
		double d = fatherG.getAllPaths().getDistance(this.path.getFirst(), node);
		double dTry = d;

		for(int k = 0; k < this.path.size(); k++){
			
			Integer nTry = this.path.get(k);
			
			dTry = fatherG.getAllPaths().getDistance(nTry, node);

			if(dTry < d){
				d = dTry;
				i = k;
			}

		}


		Path p = fatherG.getAllPaths().getPath(this.path.remove(i), node);
		this.coveredNodesSet.addAll(p);
		// 1 - 3 - 2 - 1
		// add 5 ao 2 path: 2 - 6 - 7 - 5
		// 1 - 3  - 2 - 2 - 1
		// 1 - 3  - 2 - 6 - 6 - 2 - 1
		// 1 - 3  - 2 - 6 - 7 - 7 - 6 - 2 - 1
		// 1 - 3  - 2 - 6 - 7 - 5 - 7 - 6 - 2 - 1
		
		for(int k = 0; k < p.size(); k++){
		
			if(k == p.size() - 1){
				this.path.add(i + k, p.get(k));
				break;
			}
			
			this.path.add(i + k, p.get(k));
			this.path.add(i + k + 1, p.get(k));
			
		}
		

	}


	public void addRandomNodeAndRebuildPath(){


		PreCalculedPathGraph fatherG = this.inducedSubGraph.getFatherGraph();
		if(this.coveredNodesSet.size() == fatherG.getNumNodes()){
			return;
		}

		List<Integer> suffledList = RandomUtil.shuffle(fatherG.getNodesList());

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


		this.coveredNodesSet.addAll( fatherG.getAllPaths().getPath(this.centerNode, node) ); 

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


	public void twoChangeImprove(int nTimes){
		while(nTimes-- > 0){
			this.twoChangeImprove();
		}
	}


	public void twoChangeImprove(){

		Path oldPath = this.path;

		applyRandomTwoChange();

		if(oldPath.getCost(inducedSubGraph) < this.path.getCost(inducedSubGraph)){
			this.path = oldPath;
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
	public Object clone() {
		AgentMATP other = new AgentMATP();

		other.buildMethod = this.buildMethod;
		other.centerNode = this.centerNode;
		other.coveredNodesSet = (HashSet<Integer>)this.coveredNodesSet.clone();
		other.inducedSubGraph = (InducedSubGraph)this.inducedSubGraph.clone();
		other.path	= (Path)this.path.clone();
		
		return other;
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
	
	
	public static void main(String[] args) throws IOException {
		
		PreCalculedPathGraph g = new PreCalculedPathGraph(GraphReader.readAdjacencyList("./maps/island11"));
		
		List<Integer> nodes = new ArrayList<Integer>();
		
		nodes.add(0);nodes.add(1);nodes.add(2);nodes.add(3);		
		
		AgentMATP agent = new AgentMATP(1, new InducedSubGraph(nodes, g));
		
		System.out.println(g);
		
		System.out.println(agent);
		
		agent.addRandomNodeAndRebuildPath();
		
		System.out.println(agent);
		
		agent.removeRandomNodeAndRebuildPath();
		
		System.out.println(agent);
		
		agent.addRandomNodeWithSmallChanges();
		
		System.out.println(agent);
		
		agent.removeRandomNodeWithSmallChanges();
		
		System.out.println(agent);
		
	}

}
