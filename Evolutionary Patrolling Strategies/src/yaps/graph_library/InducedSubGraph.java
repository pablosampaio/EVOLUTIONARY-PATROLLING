package yaps.graph_library;

import java.util.ArrayList;
import java.util.List;

import taia.PreCalculedPathGraph;
import yaps.graph_library.algorithms.AllPairsShortestPaths;

public class InducedSubGraph extends Graph {

	// public NodeSelectedSubGraph(int numVertices) {
	// super(numVertices);
	// }

	private List<Integer> nodes;
	private boolean isConnected = false;
	private AllPairsShortestPaths allPaths;
	private PreCalculedPathGraph graph;

	private InducedSubGraph() {
		super(0, null);
	}

	public InducedSubGraph(List<Integer> nodes, PreCalculedPathGraph graph) {

		super(graph.getNumNodes(), graph.getRepresentation());
		this.graph = graph;

		this.nodes = nodes;

		for (Integer i : nodes) {
			for (Integer j : nodes) {
				if (i != j && graph.existsEdge(i, j)) {
					this.addEdge(graph.getEdge(i, j));
				}
			}
		}

		this.allPaths = new AllPairsShortestPaths();
		this.allPaths.computeShortestPaths(this);

		for (Integer i : nodes) {
			for (Integer j : nodes) {
				if (!this.allPaths.existsPath(i, j)) {
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

	public PreCalculedPathGraph getFatherGraph() {
		return graph;
	}

	@Override
	public Object clone(){
		InducedSubGraph other = new InducedSubGraph( new ArrayList<Integer>(this.nodes), this.graph);
		return other;
	}
	
	
	@Override
	public boolean equals(Object obj){
		if(obj == null){
			return false;
		}
		
		if(obj instanceof InducedSubGraph){
			InducedSubGraph other = (InducedSubGraph) obj;
			
			if(this.isConnected != other.isConnected){
				return false;
			}
			
			if(this.nodes.size() != other.nodes.size()){
				return false;
			}
			
			if(!this.nodes.equals(other.nodes)){
				return false;
			}
		
			return this.graph.equals(other.graph);
			
		}
		
		return false;
		
	}
	
}
