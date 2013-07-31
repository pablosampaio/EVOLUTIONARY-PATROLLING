package taia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import taia.util.ListUtil;

import yaps.graph_library.GraphReader;
import yaps.graph_library.InducedSubGraph;
import yaps.graph_library.Path;
import yaps.graph_library.PathBuilder;
import yaps.graph_library.algorithms.AllPairsShortestPaths;
import yaps.metrics.VisitsList;
import yaps.util.RandomUtil;

public class SimpleIndividual {

	
	private HashMap<Integer, Path> agents;
	private PreCalculedPathGraph graph;
	private List<Integer> agentList;
	
	
	
	public SimpleIndividual(List<Integer> fixedNodeList, PreCalculedPathGraph graph){
			
		agents = new HashMap<Integer, Path>();
		agentList = fixedNodeList;
		this.graph = graph;
		
		
		randomNewIndividual();
		
	}
	
	
	@SuppressWarnings("unchecked")
	private SimpleIndividual(SimpleIndividual other){
		HashMap<Integer, Path> clone = (HashMap<Integer, Path>)other.agents.clone();
		agents = clone;
		graph = other.graph;
		agentList = other.agentList;
		
	}
	
	
	private void randomNewIndividual(){
		
		int numAddedNodes = 0;
		
		//PAS: (Secundario) Representando as partições com um array? Pode ser inviável para
		//algoritmos de população em grafos muito grandes...
		HashMap<Integer, boolean[]> marksList = new HashMap<Integer, boolean[]>();
		boolean[] nodeMarks = new boolean[graph.getNumNodes()];
		
		AllPairsShortestPaths allp = graph.getAllPaths();
		
		for(Integer n: agentList){
			marksList.put(n, new boolean[graph.getNumNodes()]);
			marksList.get(n)[n] = true;
			nodeMarks[n] = true;
			numAddedNodes++;
		}
		
		//PAS: (Secundario) Esta eh uma maneira perfeitamente valida de inicializar aleatoriamente, 
		// mas tambem pensei em outra: fazer uma particao exclusiva e so incluir os nos necessarios 
		// para tornar cada subgrafo conectado. Qual a melhor? Nao sei. So testando... 
		while(numAddedNodes < graph.getNumNodes()){
			
			Integer node = ListUtil.chooseAtRandom(agentList);
					
			Path path = allp.getPath(node, RandomUtil.chooseInteger(0, graph.getNumNodes() - 1));
			
			for(Integer n: path){
			
				if(!marksList.get(node)[n]){
					marksList.get(node)[n] = true;
					if(!nodeMarks[n]){
						nodeMarks[n] = true;
						numAddedNodes++;
					}
				}
	
			}
			
		}
		
		for(Integer n: agentList){
			
			ArrayList<Integer> nodes = new ArrayList<Integer>();
			
			for(Integer i = 0; i < marksList.get(n).length; i++){
				if(marksList.get(n)[i]){
					nodes.add(i);
				}
			}
			
			//PAS: Tem varias formas de inicializar o ciclo. Esta é uma delas (ok).
			// Veja que inicializar envolve: 1) de particionar, 2) formar os ciclos.
			// Seria bom ter varias formas de fazer cada um e parametrizar a escolha delas. 
			agents.put(n, PathBuilder.nearestInsertionMethod(new InducedSubGraph(nodes, graph)));
			
		}

	} 

	
	
	public SimpleIndividual tweakCopy(){
		SimpleIndividual i = new SimpleIndividual(this);
		
		while(this.equals(i)){
			i.tweak();
			
		}
		
		return i;
	}
	
	public VisitsList generateVisitList(int simulationTime){
		
		VisitsList v = new VisitsList();

		for(Integer n: agentList){
			v.addVisitList( ClosedPathFacility.fromClosedPathToVisitList(this.agents.get(n), this.graph, simulationTime, n));			
		}
		
		return v;
	}
	
	//PAS: Nunca usado!
	public void addRandomNode(Integer agent){
		ArrayList<Integer> nodes = new ArrayList<Integer>();
		
		
		for(Integer n: agents.get(agent)){
			
			if(!nodes.contains(n)){
				nodes.add(n);
			}

		}

		Integer node = ListUtil.chooseAtRandom(graph.getNodesList());
		
		AllPairsShortestPaths allp = graph.getAllPaths();
		
		Path path = allp.getPath(agent, node);
		
		for(Integer n: path){
		
			if(!nodes.contains(n)){
				nodes.add(n);
			}

		}
		
		agents.put(agent, PathBuilder.nearestInsertionMethod(new InducedSubGraph(nodes, graph)));

	}
	
	
	public void removeRandomNode(Integer agent){
		
		LinkedList<Integer> nodes = new LinkedList<Integer>();
		
		for(Integer n:  agents.get(agent) ){
			
			if(!nodes.contains(n)){
				nodes.add(n);
			}

		}
		
		if(nodes.size() == 1){
			return;
		}
		
		Integer electedNode;
		InducedSubGraph sg;
		
		List<Integer> shuffeledNodeList = RandomUtil.shuffle(ListUtil.createIndexList(0, nodes.size(), 1));
		
		for(int i: shuffeledNodeList){
			
			electedNode = nodes.remove(i);
			
			if(electedNode.equals(agent)){
				nodes.add(i, electedNode);
				continue;
			}
			

			
			sg = new InducedSubGraph(nodes, graph);
			
			if(sg.isConnected()){
				agents.put(agent, PathBuilder.nearestInsertionMethod(sg));
				return;
			}
			
			nodes.add(i, electedNode);
			
		}
					
	}
	
	public void tweak(){
		
		Integer agentFixedNode = ListUtil.chooseAtRandom(agentList);
		
		//PAS: (Secundario) Pode atribuir uma probabilidade para cada mudanca.
		// Se houver mais de 2 opcoes, pode inicializar um array das probabilidades e
		// chamar RandomUtil.chooseProportionally().
		if(RandomUtil.getHeadTailTrow()){
			
			
			ArrayList<Integer> nodes = new ArrayList<Integer>();
			
		
			for(Integer n: agents.get(agentFixedNode)){
				
				if(!nodes.contains(n)){
					nodes.add(n);
				}
	
			}

			//PAS: Nao deveria escolher um no FORA do caminho?
			Integer node = ListUtil.chooseAtRandom(graph.getNodesList());
			
			AllPairsShortestPaths allp = graph.getAllPaths();
			
			//PAS: Nao entendi bem a ideia. Escolhe um no que ja esta no caminho e 
			// adiciona todos que estao ao longo deste caminho ao no fixo do agente?
			Path path = allp.getPath(agentFixedNode, node);
			
			for(Integer n: path){
			
				if(!nodes.contains(n)){
					nodes.add(n);
				}
	
			}
			
			agents.put(agentFixedNode, PathBuilder.nearestInsertionMethod(new InducedSubGraph(nodes, graph)));
			

		}else{
			
			
			LinkedList<Integer> nodes = new LinkedList<Integer>();
			
			for(Integer n:  agents.get(agentFixedNode) ){
				
				if(!nodes.contains(n)){
					nodes.add(n);
				}
	
			}
			
			if(nodes.size() == 1){
				return;
			}
			
			Integer electedNode;
			InducedSubGraph sg;
			
			List<Integer> shuffeledNodeList = RandomUtil.shuffle(ListUtil.createIndexList(0, nodes.size(), 1));
			
			for(int i: shuffeledNodeList){
				
				electedNode = nodes.remove(i);
				
				if(electedNode.equals(agentFixedNode)){
					nodes.add(i, electedNode);
					continue;
				}
				

				
				sg = new InducedSubGraph(nodes, graph);
				
				if(sg.isConnected()){
					agents.put(agentFixedNode, PathBuilder.nearestInsertionMethod(sg));
					return;
				}
				
				nodes.add(i, electedNode);
				
			}
						
		}
	
	}
	
	
	private static SimpleIndividual[] fixedCrossOver(SimpleIndividual i1, SimpleIndividual i2, Integer agent){
		
		SimpleIndividual[] out = new SimpleIndividual[2];
		
		Path p1 = i1.agents.get(agent);
		Path p2 = i2.agents.get(agent);
		
		out[0] = new SimpleIndividual(i1);
		out[1] = new SimpleIndividual(i2);
		
		out[1].agents.put(agent, p1);
		out[0].agents.put(agent, p2);
		
		return out;
		
	}
	
	
	public static SimpleIndividual[] crossOver(SimpleIndividual i1, SimpleIndividual i2){
		Integer n = RandomUtil.chooseInteger(0, i1.graph.getNumNodes() - 1 );
		return fixedCrossOver(i1, i2, n);
	}
	
	public static SimpleIndividual[] massiveCrossOver(SimpleIndividual i1, SimpleIndividual i2, List<Integer> agentList){
		
		SimpleIndividual[] out = new SimpleIndividual[agentList.size()];
		SimpleIndividual[] aux;
		int cnt = 0;
		
		for(Integer n: agentList){
			aux = fixedCrossOver(i1, i2, n);
			out[cnt++] = aux[0];
			out[cnt++] = aux[1];
		}
		
		return out;
		
	}
	
	@Override
	public String toString() {
		return this.agents.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == null){
			return false;
		}
		
		if(obj instanceof SimpleIndividual){
			SimpleIndividual other = (SimpleIndividual)obj;
			
			if(this.agentList.size() != other.agentList.size()){
				return false;
			}
			if(this.agents.size() != other.agents.size()){
				return false;
			}
			if(!this.graph.equals(other.graph)){
				return false;
			}
			
			for(int i = 0; i < this.agentList.size(); i++){
				
				Integer n = this.agentList.get(i);
				Integer m = other.agentList.get(i);
				
				if(!m.equals(n)){
					return false;
				}
				
				if(!other.agentList.contains(n)){
					return false;
				}
				
				if(!this.agents.get(n).equals(other.agents.get(n))){
					return false;
				}
				
				
			}
		}
		
		return true;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return new SimpleIndividual(this);
	}

	public static void main(String[] args) throws IOException {
		
		PreCalculedPathGraph g = new PreCalculedPathGraph(GraphReader.readAdjacencyList("./maps/island11"));
		
		List<Integer> centerList = GraphEquipartition.maximumDistance(4, g);	
		
		SimpleIndividual i1 = new SimpleIndividual(centerList, g);
		SimpleIndividual i2 = new SimpleIndividual(centerList, g);
		
		i1.tweak();
		i2.tweak();
		
		 @SuppressWarnings("unused")
		SimpleIndividual[] newS = SimpleIndividual.crossOver(i1, i2);
		 
		 int i = 0;
		 
		 while(true){
			 i++;
			 i1 = new SimpleIndividual(centerList, g);
			 
			 for(Integer j: i1.agentList){
				 if(i1.agents.get(j).isEmpty()){
					 System.out.println("aqui!!!!");
					 System.out.println(" "  + i + "  ");
					 return;
				 }
			 }
			 
			 
		 }
		 
		 

		 
		 
		 
	}



}
