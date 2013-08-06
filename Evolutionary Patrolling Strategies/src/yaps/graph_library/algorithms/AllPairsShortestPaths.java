package yaps.graph_library.algorithms;

import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.Path;


public class AllPairsShortestPaths {
	private double[][] distance;
	private int[][] predecessor;
	private int[][] successor;

	private static final int INFINITE = Integer.MAX_VALUE / 2;
	
	public AllPairsShortestPaths() {
	}
	
	public void computeShortestPaths(Graph g) {
		int numVertices = g.getNumNodes();

		distance = new double[numVertices][numVertices];
		predecessor = new int[numVertices][numVertices];
		successor = new int[numVertices][numVertices];

		for (int v = 0; v < numVertices; v ++) {
			for (int u = 0; u < numVertices; u++) {
				if (v == u) {
					distance[v][u] = 0;
					predecessor[v][u] = -1;
					successor  [v][u] = -1;
				
				} else if (g.existsEdge(v, u)) {
					distance[v][u] = g.getLength(v,u);
					predecessor[v][u] = v;
					successor  [v][u] = u;
					
				} else {
					distance[v][u] = INFINITE;
					predecessor[v][u] = -1;
					successor  [v][u] = -1;
				}
			}
		}
		
		// escolhe vertice intermediario
		for (int k = 0; k < numVertices; k++) {
			// escolhe origem
	  		for (int i = 0; i < numVertices; i++) {
				// escolhe destino
	    		for (int j = 0; j < numVertices; j++) {
	    			// se i-->k + k-->j for menor do que o caminho atual i-->j
	      			if ((distance[i][k] + distance[k][j]) < distance[i][j]) {
	        			// entao reduz a distancia do caminho i-->j fazendo i-->k-->j
	        			distance[i][j] = distance[i][k] + distance[k][j];
	        			
	        			predecessor[i][j] = predecessor[k][j];
	        			successor  [i][j] = successor[i][k];
	      			}
	    		}
	  		}
		}

	}
	
	/**
	 * Returns "true" iff there is a path from 'source' to 'destiny'.
	 */
	public boolean existsPath(int source, int destiny) {
		return distance[source][destiny] != INFINITE;
	}

	/**
	 * Returns the path from 'source' to 'destiny' or null if no such path exists.
	 */
	public Path getPath(int source, int destiny) {
		Path path;
		
		if (distance[source][destiny] != INFINITE) {
			path =  new Path();
			getPathInternal(source, destiny, path);
		} else {
			path = null;
		}

		return path;
	}

	private void getPathInternal(int source, int destiny, Path path) {
		if (destiny == source) {
			path.add(destiny);
		} else {
			getPathInternal(source, predecessor[source][destiny], path);
			path.add(destiny);
		}
	}
	
	/**
	 * Returns the node immediately after 'source' in the minimum-cost path from 'source' to 'destiny'.
	 * Nodes 'source' and 'destiny' can't be the same.
	 */
	public int getSourceSuccessor(int source, int destiny) {
		return successor[source][destiny];
	}
	
	/**
	 * Returns the node immediately before 'destiny' in the minimum-cost path from 'source' to 'destiny'.
	 * Nodes 'source' and 'destiny' can't be the same.
	 */
	public int getDestinyPredecessor(int source, int destiny) {
		return predecessor[source][destiny];
	}
	
	/**
	 * Returns the minimum distance from 'source' to 'destiny'.
	 */
	public double getDistance(int source, int destiny) {
		if (distance[source][destiny] == INFINITE) {
			return Integer.MAX_VALUE;
		} else {
			return distance[source][destiny];
		}
	}

	/**
	 * Returns a complete graph where the edges are weighted by
	 * the minimum distances between nodes.
	 */
	public Graph toCompleteDistancesGraph() {
		int order = this.distance.length;
		Graph graph = new Graph(order, GraphDataRepr.MIXED);
		
		for (int v = 0; v < order; v++) {
			for (int x = 0; x < order; x++) {
				if (distance[v][x] != INFINITE) {
					graph.addEdge(v, x, distance[v][x]);
				}
			}
		}
		
		return graph;
	}
	
}
