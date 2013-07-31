package taia;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.algorithms.AllPairsShortestPaths;

//PAS: 
// 1 - (Secundario) Precisava mesmo estender Graph? Esta filha nao se comporta 
//     coerente com a classe pai.  
// 2 - Falta comentar (mesmo que de forma muito curta) o objetivo das classes...
// 3 - Veja se AllPairsShortestPaths.toCompleteDistancesGraph() nao faz o que vcs querem...
public class PreCalculedPathGraph extends Graph{

	private AllPairsShortestPaths allPaths;

	
	public PreCalculedPathGraph(Graph g){
		super(g.getNumNodes(), g.getRepresentation());

		for(Integer i: g.getNodesList()){
			for(Integer j: g.getNodesList()){
				if(i == j){
					continue;
				}
			
				if(g.existsEdge(i, j)){
					if(!this.existsEdge(i, j)){
						this.addEdge(g.getEdge(i, j));
					}
					
				}
			}
		}

		this.allPaths = new AllPairsShortestPaths();
		this.allPaths.computeShortestPaths(this);

	}
	
	public PreCalculedPathGraph(int numVertices) {
		super(numVertices);
		this.allPaths = new AllPairsShortestPaths();
		this.allPaths.computeShortestPaths(this);
	}

	public PreCalculedPathGraph(int numVertices, GraphDataRepr r) {
		super(numVertices, r);
		this.allPaths = new AllPairsShortestPaths();
		this.allPaths.computeShortestPaths(this);
	}

	public List<Integer> orderedNeighborList(int node){
		List<Integer> lNeigh = this.getSuccessors(node);

		for(int i = 0; i < lNeigh.size() - 1; i++){
			for(int j = i + 1; j < lNeigh.size(); j++){
				int ni = lNeigh.get(i);
				int nj = lNeigh.get(j);
				if(allPaths.getDistance(node, nj) < allPaths.getDistance(node, ni)){
					lNeigh.set(i, nj);
					lNeigh.set(j, ni);
				}
			}
		}



		return lNeigh;
	}

	public AllPairsShortestPaths getAllPaths() {
		return allPaths;
	}

	public List<Integer> getOrderedDistanceList(Integer source){

		LinkedList<NodeDistance> list = new LinkedList<PreCalculedPathGraph.NodeDistance>();
		
		for(Integer node: this.getNodesList()){
			if(source.equals(node)){
				continue;
			}
			orderedAddnode(new NodeDistance(node, this.allPaths.getDistance(source, node)), list);
		}
		
		List<Integer> out = new ArrayList<Integer>();
		
		while(!list.isEmpty()){
			out.add(list.removeFirst().node);
		}
		

		return out;
	}



	//Diogo
	//ordered insert
	//============================================================//    	            		
	private static void orderedAddnode(NodeDistance v, List<NodeDistance> list){

		if(list.size() == 0){
			list.add(v);
			return;
		}

		int left = 0;
		int right = list.size();
		int midle = (left + right)/2;

		NodeDistance u = list.get(midle);

		while(left < right - 1){
			if(v.dist > u.dist){
				left = midle;
			}else{
				right = midle;
			}

			midle = (left + right)/2;
			u = list.get(midle);
		}

		if(v.dist > u.dist){
			list.add(midle + 1, v);
		}else{
			list.add(midle, v);
		}


	}



	private class NodeDistance{
		
		public NodeDistance(Integer node, double dist){
			this.node = node;
			this.dist = dist;
		}
		
		public Integer node;
		public double dist;
	}

}
