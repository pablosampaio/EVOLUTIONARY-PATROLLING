package yaps.graph_library;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import yaps.graph_library.algorithms.AllPairsShortestPaths;


/**
 * Represents a path with repetitions of nodes allowed 
 * (i.e. its not necessarily a simple path). 
 *
 * @author Pablo A. Sampaio
 */
@SuppressWarnings("serial")
public class Path extends LinkedList<Integer> {

	public Path() {
		super();
	}
	
	public Path(Collection<Integer> c) {
		super(c);
	}
	
	public boolean existNode(int node){
		return super.contains(node);
		
	}
	
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
	public boolean isCycle() {
		return (super.getFirst() == super.getLast());
	}
	
	/**
	 * Calculates the cost of the path in the given graph, considering that each (u,v) 
	 * edge has the cost of the shortest path from u to v. <br>
	 * Returns -1 if one the paths is not possible in the graph (e.g. if the graph is 
	 * not strongly connected).
	 * <br><br>
	 * It should return the same value as: <br>
	 * expandShortestPaths(shortest).getCost(graph).
	 */
	public double getCostExpandingShortestPaths(AllPairsShortestPaths shortest) {
		int n = this.size();
		int totalCost = 0;
		double distance;
		
		for (int i = 1; i < n; i++) {
			distance = shortest.getDistance(this.get(i-1), this.get(i));
			
			if (distance == Integer.MAX_VALUE) {
				throw new IllegalArgumentException("grafo nao permite caminho de " + this.get(i-1) + " a " + this.get(i));
			}
			
			totalCost += distance;
		}

		return totalCost;
	}
	
	/**
	 * Expand the given path by exchanging each edge (u,v) by the shortest 
	 * path from u to v. <br> 
	 * Returns null if one of the paths is not possible in the graph (e.g. if 
	 * the graph is not strongly connected).
	 */
	public Path expandShortestPaths(AllPairsShortestPaths shortest) {
		Path realPath = new Path();	
		realPath.add(this.get(0));

		List<Integer> partialPath;

		for (int i = 1; i < this.size(); i++) {
			partialPath = shortest.getPath(this.get(i-1), this.get(i));
			
			if (partialPath == null) {
				throw new IllegalArgumentException("grafo nao permite caminho de " + this.get(i-1) + " a " + this.get(i));
			}
			
			partialPath.remove(0);
			realPath.addAll(partialPath);
		}
		
		return realPath;
	}	
	
	/**
	 * If the start and end vertex of the list are the same, rotates the
	 * list to start with the given vertex. Otherwise, returns null.
	 */
	public static Path rotateList(Path thish, int start) {
		if (thish.get(0) != thish.get(thish.size()-1)) {
			return null;
		}
		
		// removes the last, which is a repetition of the first
		thish.remove(thish.size() - 1);

		int startIndex = thish.indexOf(start);
		Path newPath = new Path();
		
		for (int i = startIndex; i < thish.size(); i++) {
			newPath.add(thish.get(i));
		}
		
		// vertex start is reinserted in the end
		for (int i = 0; i <= startIndex; i++) {
			newPath.add(thish.get(i));
		}
		
		return newPath;
	}

	public String toString() {
		return super.toString();
	}
	
}
