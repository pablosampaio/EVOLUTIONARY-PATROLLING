package yaps.graph_library;

import java.util.List;

import yaps.graph_library.algorithms.AllPairsShortestPaths;


/**
 * Auxiliary methods related to paths. 
 *  
 * @author Pablo A. Sampaio
 */
public class PathFunctions {

	/**
	 * Calculates the cost of the path in the given graph, considering
	 * that each (u,v) edge has the cost of the shortest path from u to v.
	 * Returns -1 if one the paths is not possible in the graph (e.g. if 
	 * the graph is not strongly connected).
	 * <br><br>
	 * It should return the same value as: <br>
	 * getCost( extractShortestPaths(path,graph) , graph) .
	 */
	public static double getCostExpandingShortestPaths(Path thisp, Graph graph) {
		AllPairsShortestPaths shortest = new AllPairsShortestPaths();
		
		shortest.computeShortestPaths(graph);
		
		int n = thisp.size();
		int totalCost = 0;
		double distance;
		
		for (int i = 1; i < n; i++) {
			distance = shortest.getDistance(thisp.get(i-1), thisp.get(i));
			
			if (distance == Integer.MAX_VALUE) {
				throw new IllegalArgumentException("grafo nao permite caminho de " + thisp.get(i-1) + " a " + thisp.get(i));
			}
			
			totalCost += distance;
		}

		return totalCost;
	}
	
	/**
	 * Expand the given path by exchanging each edge (u,v) by the shortest 
	 * path from u to v. 
	 * Returns null if one of the paths is not possible in the graph (e.g. if 
	 * the graph is not strongly connected).
	 */
	public static Path expandShortestPaths(Path path, Graph graph) {
		AllPairsShortestPaths shortest = new AllPairsShortestPaths();
		Path realPath = new Path();	
		
		shortest.computeShortestPaths(graph);
		
		realPath.add(path.get(0));

		List<Integer> partialPath;

		for (int i = 1; i < path.size(); i++) {
			partialPath = shortest.getPath(path.get(i-1), path.get(i));
			
			if (partialPath == null) {
				throw new IllegalArgumentException("grafo nao permite caminho de " + path.get(i-1) + " a " + path.get(i));
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
	
}
