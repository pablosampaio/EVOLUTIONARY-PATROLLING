package yaps.graph_library;


/**
 * Represents the directed edge "source -> target".
 *   
 * @author Pablo A. Sampaio
 */
public class Edge {
	private int identifier;
	
	private int sourceId;
	private int targetId;
	
	private double length;
	private boolean directed;
	
	Edge(int ident, int source, int target, double weight, boolean directed) {
		this.identifier = ident;
		this.sourceId = source;
		this.targetId = target;
		this.length = weight;
		this.directed = directed;
	}

	Edge(int ident, int source, int target, double weight) {
		this(ident, source, target, weight, true);
	}
	
	Edge(int source, int target) {
		this(-1, source, target, 1.0d, true);
	}
	
	public int getId() {
		return this.identifier;
	}
	
	public int getSourceId() {
		return sourceId;
	}

	public int getTargetId() {
		return targetId;
	}

	public double getLength() {
		return length;
	}
	
	public boolean isDirected() {
		return directed;
	}
	
	public boolean equalsIgnoreDirection(Edge e) {
		return ((this.sourceId == e.sourceId && this.targetId == e.targetId) 
					|| (this.sourceId == e.targetId && this.targetId == e.sourceId));
	}

	public boolean equals(Object o) {
		if (!(o instanceof Edge)) {
			return false;
		}
		
		Edge e = (Edge)o;
		
		return (this.sourceId == e.sourceId
				&& this.targetId == e.targetId);
	}
	
	public String toString() {
		String edgeRepr = directed? "->" : "--";
		return "e" + this.identifier + "(n" + this.sourceId + edgeRepr + "n" + this.targetId + ", " + this.length +")";
	}
	
}
