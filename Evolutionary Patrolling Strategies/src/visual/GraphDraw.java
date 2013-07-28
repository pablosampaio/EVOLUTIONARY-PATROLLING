package visual;

import java.io.FileOutputStream;
import java.io.IOException;

import yaps.graph_library.Graph;

/**
 * Draws a graph using the <a href="http://www.graphviz.org/">graphviz software</a>.
 * @author V&iacute;tor de A. Torre&atilde;o
 *
 */
public class GraphDraw {
	
	private Graph graph;
	
	private String location;
	
	public GraphDraw(Graph g) {
		
		this.graph = g;
		this.location = "";
		
	}
	
	public GraphDraw(Graph g, String location) {
		
		this.graph = g;
		this.location = location;
		
	}
	
	public void setLocation(String new_location) {
		
		this.location = new_location;
		
	}

	public Graph getGraph() {
		return graph;
	}

	public String getLocation() {
		return location;
	}
	
	public void drawGraph() throws IOException{
		
		FileOutputStream file = new FileOutputStream(this.location);
		
		file.write("digraph G {".getBytes());
		
		
		
		file.close();
		
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
