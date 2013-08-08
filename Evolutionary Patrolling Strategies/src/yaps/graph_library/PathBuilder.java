package yaps.graph_library;

import java.io.IOException;
import java.util.List;

import taia.PreCalculedPathGraph;
import yaps.graph_library.algorithms.AllPairsShortestPaths;
import yaps.util.ListView;
import yaps.util.RandomUtil;

public class PathBuilder {
	
	public static final int NearestNeighborMethod = 0;
	public static final int NearestInsertionMethod = 1;
	
	private static final int INFINITE = Integer.MAX_VALUE / 2;

	public static Path nearestNeighborMethod(InducedSubGraph g){

		List<Integer> nodes = g.getNodesList();

		if(nodes.size() == 0){
			return new Path();
		}

		if(!g.isConnected()){
			return new Path();
		}

		if(nodes.size() <= 2){
			Path p = new Path(nodes);
			p.add(nodes.get(0));
			return p;
		}



		Path p = new Path();
		AllPairsShortestPaths allPaths = g.getAllPaths();

		int selected = RandomUtil.chooseInteger(0, nodes.size() - 1);
		int bNode = nodes.remove(selected);
		int eNode = bNode;

		p.addFirst(bNode);

		while(!nodes.isEmpty()){

			bNode = p.getFirst();
			eNode = p.getLast();

			selected = 0;
			int sNode = nodes.get(selected);
			double minPath = allPaths.getDistance(bNode, sNode);
			boolean addAtBegin = true;

			for(int j = 0; j < nodes.size(); j++){

				int nNode = nodes.get(j);

				if(allPaths.getDistance(bNode, nNode) < minPath){
					minPath = allPaths.getDistance(bNode, nNode);
					selected = j;
					sNode = nNode;
					addAtBegin = true;
				}

				if(allPaths.getDistance(eNode, nNode) < minPath){
					minPath = allPaths.getDistance(eNode, nNode);
					selected = j;
					sNode = nNode;
					addAtBegin = false;
				}

			}

			if(addAtBegin){
				Path p1 = allPaths.getPath(sNode, p.getFirst());

				p1.removeLast();

				while(!p1.isEmpty()){
					Integer n =  p1.removeLast();
					nodes.remove( (Object) n);
					p.addFirst(n);
				}

			}else{

				Path p1 = allPaths.getPath(p.getLast(), sNode);

				p1.removeFirst();

				while(!p1.isEmpty()){
					Integer n =  p1.removeFirst();
					nodes.remove( (Object) n);
					p.addLast(n);
				}

			}

			//nodes.remove(selected);

		}

		Path p1 = allPaths.getPath(p.getLast(), p.getFirst());

		p1.removeFirst();

		while(!p1.isEmpty()){
			p.addLast(p1.removeFirst());
		}


		return p;
	}


	public static double distanceNodePath(int kNode, Path T, AllPairsShortestPaths allPaths){

		if(T.existNode(kNode)){
			return 0;
		}

		if(T.size() == 0){
			throw new IllegalArgumentException("Path of size 0.");
		}

		double d = allPaths.getDistance(T.getFirst(), kNode);

		for(int sNode: T){
			if(allPaths.getDistance(kNode, sNode) < d){
				d = allPaths.getDistance(kNode, sNode);
			}
		}

		if(d == PathBuilder.INFINITE){
			throw new IllegalArgumentException("Infinity distance node-path size.");
		}

		return d;
	}


	public static double lengthIncreace(int iNode, int jNode, int kNode, Path T, AllPairsShortestPaths allPaths){
		if(!T.existNode(iNode)){
			throw new IllegalArgumentException("The node does not exist in the path.");
		}

		if(!T.existNode(jNode)){
			throw new IllegalArgumentException("The node does not exist in the path.");
		}

		if(allPaths.getPath(iNode, jNode) == null){
			throw new IllegalArgumentException("The nodes are not neighbors.");
		}

		return allPaths.getDistance(iNode, kNode) + allPaths.getDistance(kNode, jNode) - allPaths.getDistance(iNode, jNode);
	}

	public static Path nearestInsertionMethod(InducedSubGraph g){

		List<Integer> nodes = g.getNodesList();

		if(nodes.size() == 0){
			return new Path();
		}

		if(nodes.size() <= 2){
			Path p = new Path(nodes);
			p.add(nodes.get(0));
			return p;
		}

		if(!g.isConnected()){
			return new Path();
		}

		Path p = new Path();
		AllPairsShortestPaths allPaths = g.getAllPaths();

		int selected = RandomUtil.chooseInteger(0, nodes.size() - 1);
		int bNode = nodes.remove(selected);
		int eNode = bNode;

		double d = PathBuilder.INFINITE;

		for(int i = 0; i < nodes.size(); i++){
			int node = nodes.get(i);
			if(allPaths.getDistance(bNode, node) < d){
				d = allPaths.getDistance(bNode, node);
				eNode = node;
				selected = i;
			}

		}

		nodes.remove(selected);
		p.addFirst(bNode);
		p.addLast(eNode);
		p.addLast(bNode);

		while(!nodes.isEmpty()){

			selected = 0;
			int sNode = nodes.get(selected);
			double minPath = PathBuilder.distanceNodePath(sNode, p, allPaths);

			for(int k = 0; k < nodes.size(); k++){

				int node = nodes.get(k);

				if(PathBuilder.distanceNodePath(sNode, p, allPaths) < minPath){
					minPath = PathBuilder.distanceNodePath(sNode, p, allPaths);
					selected = k;
					sNode = node;

				}

			}

			int selectedEdge = 0;
			bNode = p.get(selectedEdge);
			eNode = p.get( (selectedEdge + 1) );
			double minCost = PathBuilder.lengthIncreace(bNode, eNode, sNode, p, allPaths);

			for(int k = 0; k < p.size(); k++){

				bNode = p.get(k);
				eNode = p.get( (k + 1) % p.size() );


				if(PathBuilder.lengthIncreace(bNode, eNode, sNode, p, allPaths) < minCost){
					minCost = PathBuilder.distanceNodePath(eNode, p, allPaths);
					selectedEdge = k;

				}

			}

			bNode = p.get(selectedEdge);
			eNode = p.get( (selectedEdge + 1) % p.size() );


			Path p1 = allPaths.getPath(bNode, sNode);
			Path p2 = allPaths.getPath(sNode, eNode);

			p1.removeFirst();
			p2.removeFirst();
			p2.removeLast();

			while(!p2.isEmpty()){
				Integer n = p2.removeLast();
				if(nodes.remove( (Object) n)){} 
				p.add(selectedEdge + 1, n);
			}

			while(!p1.isEmpty()){
				Integer n =  p1.removeLast();
				nodes.remove( (Object) n);
				p.add(selectedEdge + 1, n);
			}



			//nodes.remove(selected);

		}

		return p;
	}


	//PAS: Poderia ser bem mais simples com ListView...
	public static Path twoChange(int edge1, int edge2, Path p, InducedSubGraph g){


		if(edge1 > edge2){
			int c = edge1;
			edge1 = edge2;
			edge2 = c;
		}

		if(edge1 == edge2 + 1){
			return new Path(p);
		}

		if(edge1 == edge2){
			return new Path(p);
		}


		AllPairsShortestPaths allPath = g.getAllPaths();

		//A - e1 - e2 - reverso B - e1 + 1 - e2 + 1 - C
		
		//edge1 = 3, edge2 = 5
		//(0,1,2) - (nodes 3 to 5) - (5, 4) - (4 to 6) - (6, 7, 8 ....0)

		Path nPath = new Path();
		Integer now;

		for(int k = 0; k < p.size(); k++){

			if( k < edge1 ){
				//Adds block A
				now = p.get(k);
				
				if(nPath.size() == 0){
					nPath.add(now);
				}else if(!nPath.peekLast().equals(now)){
					nPath.add(now);
				}
				
			
			}else if (k == edge1){
				//Adds a path between edge1 and edge2
				Path p1 = allPath.getPath(p.get(edge1), p.get(edge2));
				
				now = p1.removeFirst();
				
				if(nPath.size() == 0){
					nPath.add(now);
				}else if(!nPath.peekLast().equals(now)){
					nPath.add(now);
				}

				
				nPath.addAll(p1);
				
			}else if( k < edge2 ){
				//Adds B reversed
				
				now = p.get(edge1 + edge2 - k);
				
				if(nPath.size() == 0){
					nPath.add(now);
				}else if(!nPath.peekLast().equals(now)){
					nPath.add(now);
				}
				

			}else if(k == edge2){
			
				Path p1 = allPath.getPath(p.get(edge1 + 1), p.get(edge2 + 1));
				
				if(p1.size() > 2){
					p1.removeFirst();
					p1.removeLast();
					nPath.addAll(p1);
				}
				
				
			}else{
				//Adds C
				now = p.get(k);
				if(nPath.size() == 0){
					nPath.add(now);
				}else if(!nPath.peekLast().equals(now)){
					nPath.add(now);
				}
				
				
			}

		}


		return nPath;
	} 	

	//PAS: 2-change com ListView
	/**
	 * Efetua o operador 2-change sem expandir as arestas (trocando-os pelos menores caminhos),
	 * que é deixado para uma etapa posterior. Retorna um novo caminho resultante da operação. 
	 * <br><br>
	 * Exemplo: 
	 * - path = [A, B, C, D, E, A]  (n=6)
	 * - srcNode1 = 1 (foi quebrada a aresta B-C) 
	 * - srcNode2 = 3 (foi quebrada a aresta D-E)
	 * => saida: [E, A, B, D, C, E]
	 * <br><br>
	 * Recebe: <ul>
	 * <li>sourceNode1, índice do nó de origem da 1a areasta "quebrada" (range: [0, n-2])
	 * <li>sourceNode2: índice do nó de origem da 2a areatas "quebrada" (range: [srcNode1, n-2])
	 * <li>p: o caminho</ul> 
	 */
	public static Path twoChange(Path path, int srcNode1, int srcNode2) {
		assert(srcNode2 >= srcNode1);
		assert(path.isCycle());

		//casos especiais: se for a mesma aresta ou se forem consecutivas
		if ((srcNode2 - srcNode1) <= 1) {
			return new Path(path);
		}
	
		ListView<Integer> partAbegin = new ListView<>(path, srcNode2+1);
		ListView<Integer> partAend = new ListView<>(path, 1, srcNode1);  //excludes the first one because it is included in the end of partAbegin
		
		Path newPath = new Path(partAbegin);
		newPath.addAll(partAend);
		//System.out.println("\t-> part A: " + newPath);
		
		ListView<Integer> partBreverted = new ListView<>(path, srcNode2, srcNode1+1); //isso garante a inversão, pq srcNode2 > srcNode1

		newPath.addAll(partBreverted);
		//System.out.println("\t-> part B reverted: " + partBreverted);
		
		newPath.add(newPath.getFirst());
		
		return newPath;
	}
	
	//PAS: Desafio - implementem esse.
	/**
	 * Recebe array srcNodes tal que: para todo i > j --> srcNodes[i] > srcNodes[j].
	 * Alem disso, invert[i] indica se o trecho que termina em srcNodes[i] vai ou não
	 * ser invertido (ou seja, se tudo for true, retorna o mesmo caminho).
	 */
	public static Path kChange(Path path, int[] srcNodes, boolean[] invert) {
		return null;
	}
	
	public static void main(String[] args) throws IOException {
		PreCalculedPathGraph graph = new PreCalculedPathGraph( GraphReader.readAdjacencyList("./maps/complete15") );

		Path path = new Path(); 
		path.add(0); path.add(1); path.add(2); path.add(3); path.add(4); path.add(0);
		
		List<Integer> nodes = (List<Integer>)path.clone();  
		
		InducedSubGraph subg = new InducedSubGraph(nodes, graph);
		
		System.out.printf("ORIGINAL PATH: %s\n", path);
		
//		System.out.printf("=> 2-change (v1): %s\n", PathBuilder.twoChange(1, 3, path, subg));

		Path p = PathBuilder.twoChange(path, 1, 3);
		System.out.printf("=> 2-change (sem expandir): %s\n", p);
		System.out.printf("=> 2-change (expandido): %s\n", p.expandShortestPaths(subg.getAllPaths()));
	}


}
