package yaps.graph_library;

import java.util.ArrayList;
import java.util.List;

import yaps.graph_library.algorithms.AllPairsShortestPaths;

public class NodeSelectedSubGraph extends Graph {

	//public NodeSelectedSubGraph(int numVertices) {
	//	super(numVertices);
		// TODO Auto-generated constructor stub
	//}

	private List<Integer> nodes;
	private boolean isConnected = false;
	private AllPairsShortestPaths allPaths;
	
	public NodeSelectedSubGraph(List<Integer> nodes, Graph graph) {

		super(graph.getNumNodes(), graph.getRepresentation());

		this.nodes = nodes;
		
		for(Integer i: nodes){
			for(Integer j: nodes){
				if(i == j){
					continue;
				}
			
				if(graph.existsEdge(i, j)){
					this.addEdge(graph.getEdge(i, j));
				}
			}
		}

		this.allPaths = new AllPairsShortestPaths();
		this.allPaths.computeShortestPaths(this);
		
		for(Integer i: nodes){
			for(Integer j: nodes){
				if(i == j){
					continue;
				}
			
				if(!this.allPaths.existsPath(i, j)){
					this.isConnected = false;
					return;
				}
			}
		}
		
		this.isConnected = true;
		
	}

	public boolean isConnected() {
		return isConnected;
	}

	@Override
	public List<Integer> getNodesList() {
		return new ArrayList<Integer>(this.nodes);
	}

	public AllPairsShortestPaths getAllPaths() {
		return allPaths;
	}
	
}
