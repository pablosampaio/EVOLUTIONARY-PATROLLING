package yaps.graph_library;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;


/**
 * Methods to read graph from different file formats.
 * 
 * @author Pablo A. Sampaio
 */
public class GraphReader {

	public static Graph readAdjacencyList(String file) throws IOException {
		return readAdjacencyList(file, GraphDataRepr.MIXED);
	}
	
	public static Graph readAdjacencyList(String file, GraphDataRepr rep) throws IOException {
		BufferedReader inputFile = new BufferedReader(new FileReader(file));

		StringTokenizer tokenizer;
		String line;

		int numVertices;
		
		line = inputFile.readLine();
		numVertices = Integer.parseInt(line.trim());
		
		int startIndex = file.lastIndexOf('\\') + 1;
		if (startIndex <= 0) {
			startIndex = file.lastIndexOf('/') + 1;
			if (startIndex <= 0) {
				startIndex = 0;
			}
		}
		
		//String graphName = file.substring(startIndex);
		Graph graph = new Graph(numVertices, rep);
		
		String sucessorStr, pesoStr;
		int sucessor, peso;
		
		for (int v = 0; v < numVertices; v ++) {
			line = inputFile.readLine();
			tokenizer = new StringTokenizer(line);
			
			while (tokenizer.hasMoreTokens()) {
				sucessorStr = tokenizer.nextToken();
				sucessor = Integer.parseInt(sucessorStr.trim());
				
				pesoStr = tokenizer.nextToken();
				peso = Integer.parseInt(pesoStr);
				
				if (sucessor != -1) {
					graph.addEdge(v, sucessor, peso);
				}
			}
		}		
		inputFile.close();
		
		return graph;
	}

	public static Graph readEdgeList(String file) throws IOException {
		BufferedReader inputFile = new BufferedReader(new FileReader(file));
		
		String line;
		StringTokenizer tokenizer;
		
		line = inputFile.readLine();
		tokenizer = new StringTokenizer(line);
		
		int numVertices = Integer.parseInt(tokenizer.nextToken()); //number of edges is ignored
		String graphName = file.substring(file.lastIndexOf('\\') + 1, file.lastIndexOf('.'));
		Graph graph = new Graph(numVertices, GraphDataRepr.MIXED);
	
		int v, succ, weight;
		line = inputFile.readLine();

		while (line != null && !line.trim().equals("")) {
			tokenizer = new StringTokenizer(line);
			
			v = Integer.parseInt(tokenizer.nextToken().trim());
			succ = Integer.parseInt(tokenizer.nextToken().trim());
			weight = Integer.parseInt(tokenizer.nextToken().trim());
			
			graph.addEdge(v, succ, weight);

			line = inputFile.readLine();
		}
		inputFile.close();
		
		return graph;
	}
	
	// only for graphml saved with the GraphWriter
	public static Graph readGraphml(String fileName) throws IOException {
		BufferedReader inputFile = new BufferedReader(new FileReader(fileName));
		String line;

		line = inputFile.readLine();
				
		String[] parts = line.split("\"");
		String graphName = parts[1];
		boolean directed = parts[3].equals("directed");
		int numNodes = 0;
		
		line = inputFile.readLine(); //linha do "key" ignorada

		line = inputFile.readLine().trim();		
		while (!line.startsWith("<edge")) {
			numNodes ++;
			line = inputFile.readLine().trim();
		}

		//System.out.printf("Graph named %s, %sdirected, with %s nodes\n", graphName, directed?"":"UN", numNodes);
		Graph graph = new Graph(numNodes, GraphDataRepr.MIXED);
		
		while (line.startsWith("<edge")) {
			parts = line.split("\"");
			
			int source = Integer.parseInt( parts[3].substring(1) );
			int target = Integer.parseInt( parts[5].substring(1) );
			
			line = inputFile.readLine();
			
			int startWeightIndex = line.indexOf('>') + 1; 
			line = line.substring(startWeightIndex, line.indexOf('<', startWeightIndex));
			
			int capacity = Integer.parseInt(line);
			
			//System.out.printf("Aresta: (%s,%s) %s\n", source, target, capacity);
			
			if (directed) {
				graph.addEdge(source, target, capacity);
			} else {
				graph.addEdge(source, target, capacity);
				graph.addEdge(target, source, capacity);
			}
			
			line = inputFile.readLine().trim();
		}
		
		inputFile.close();
		return graph;
	}

	public static final long INFINITE = 9999999;
	
	public static Graph readTspLib(String fileName) throws IOException {
		BufferedReader inputFile = new BufferedReader(new FileReader(fileName));
		String line;

		line = inputFile.readLine();
		String graphName = line.substring(line.indexOf("NAME:")+5).trim();
		
		line = inputFile.readLine(); //field "TYPE" ignored
		line = inputFile.readLine(); //field "COMMENT" ignored
		
		line = inputFile.readLine();		
		line = line.substring(line.indexOf("DIMENSION:")+10).trim();
		int numNodes = Integer.parseInt(line);

		System.out.printf("Graph named %s, with %s nodes\n", graphName, numNodes);
		Graph graph = new Graph(numNodes, GraphDataRepr.MIXED);
		
		line = inputFile.readLine();
		String weightType = line.substring(line.indexOf("EDGE_WEIGHT_TYPE:")+17).trim();
		line = inputFile.readLine();
		String weightFormat = line.substring(line.indexOf("EDGE_WEIGHT_FORMAT:")+19).trim();
		
		if (!weightType.equals("EXPLICIT") || !weightFormat.equals("FULL_MATRIX")) {
			System.out.println("Weight type/format not supported!");
			inputFile.close();
			return null;
		}
		
		line = inputFile.readLine(); //ignore tag "EDGE_WEIGHT_SECTION"
		
		int source = 0;
		int target = 0;
		long cost;
		String[] parts;
		
		line = inputFile.readLine();
		while (line != null && !line.equals("EOF")) {
			parts = line.split("[ \\t]+");
			for (int i = 1; i < parts.length; i++) { //skips index 0 (always an empty string)
				cost = Long.parseLong(parts[i]);
				if (cost < INFINITE) {
					System.out.printf("Aresta: (%s,%s) %s\n", source, target, cost);
					graph.addEdge(source, target, (int)cost);
				}
				target++;
				if (target == numNodes) {
					source ++;
					target = 0;
				}			
			}			
			line = inputFile.readLine();
		}
		
		inputFile.close();
		return graph;
	}

}
