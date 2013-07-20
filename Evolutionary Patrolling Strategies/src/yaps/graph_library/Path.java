package yaps.graph_library;

import java.util.Collection;
import java.util.LinkedList;


/**
 * Represents a path with repetitions of nodes allowed 
 * (i.e. its not necessarily a simple path). 
 *
 * @author Pablo A. Sampaio
 */
public class Path extends LinkedList<Integer> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Path() {
		super();
	}
	
	public Path(Collection<Integer> c) {
		super(c);
	}
	
	
	//Diogo
	//====================================================//
	
	public boolean existNode(int i){
		
		for(Integer j: this){
			if(i==j){
				return true;
			}
		}
		
		return false;
		
	}
	
	
	//===================================================//
	
	
	/**
	 * Tests if the (directed) edges of the path really exist 
	 * in the graph.
	 */
	public boolean isValid(Graph graph) {
		return getCost(graph) != -1;
	}
	
	/**
	 * Calculates the cost of the path in the given graph. 
	 * Returns -1 if this is not a valid path in the graph.
	 */
	public double getCost(Graph graph) {
		int cost = 0;
		
		for (int i = 1; i < this.size(); i++) {
			if (! graph.existsEdge(this.get(i-1), this.get(i)) ) {
				throw new IllegalArgumentException("grafo nao permite caminho de " + this.get(i-1) + " a " + this.get(i));
			}
			cost += graph.getLength(this.get(i-1), this.get(i));
		}
		
		return cost;
	}
	
	/**
	 * Tests if the start and end vertex of the path are the same 
	 * i.e. tests if this is a closed path (not necessarily simple).
	 */
	public boolean isCycle(Graph graph) {
		return (super.getFirst() == super.getLast());
	}

	public String toString() {
		return super.toString();
	}
	
}
