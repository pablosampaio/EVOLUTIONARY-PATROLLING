package taia.individual;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import taia.AgentMATP;
import taia.GraphEquipartition;
import taia.PreCalculedPathGraph;
import taia.util.ListUtil;
import yaps.graph_library.GraphReader;
import yaps.graph_library.InducedSubGraph;
import yaps.graph_library.Path;
import yaps.graph_library.algorithms.AllPairsShortestPaths;
import yaps.util.RandomUtil;

public class SimpleIndividual extends GenericMATPIndividual{

	

	
	
	
	public SimpleIndividual(List<Integer> fixedNodeList, PreCalculedPathGraph graph){
			
		agentList = fixedNodeList;
		this.graph = graph;
		
		rebuilIndiviual();
		
	}
	

	
	private SimpleIndividual(SimpleIndividual other){
		HashMap<Integer, AgentMATP> clone = new HashMap<Integer, AgentMATP>();
		for(Integer n: other.agentList){
			AgentMATP ag = (AgentMATP)other.agents.get(n).clone();
			clone.put(n, ag);
		}
		
		agents = clone;
		graph = other.graph;
		agentList = other.agentList;
		
	}
	
	
	public void rebuilIndiviual(){
		
		agents = new HashMap<Integer, AgentMATP>();
		
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
		
		int k = 0;
		List<Integer> indexList = RandomUtil.shuffle(this.agentList);
		
		while(numAddedNodes < graph.getNumNodes()){
			
			Integer node = indexList.get(k);
			k = (++k) % indexList.size();
					
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
			
			//PAS: (secundario) Tem varias formas de inicializar o ciclo. Esta é uma delas (ok).
			// Veja que inicializar envolve: 1) de particionar, 2) formar os ciclos.
			// Seria bom ter varias formas de fazer cada um e parametrizar a escolha delas. 
			agents.put(n,   new AgentMATP(n, new InducedSubGraph(nodes, graph)));
			
		}

	} 

	

	
	public void tweak(){
		
		Integer agentNode = ListUtil.chooseAtRandom(this.agentList);
		AgentMATP agent = this.agents.get(agentNode);
		
		
		if(RandomUtil.chooseBoolean()){
			agent.addRandomNodeAndRebuildPath();
		}else{
			agent.removeRandomNodeAndRebuildPath();
		}
		
		
		agent.twoChangeImprove(10);
		
		
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
		SimpleIndividual[] newS = (SimpleIndividual[])GenericMATPIndividual.crossOver(i1, i2);
		 
		 int i = 0;
		 
		 while(true){
			 i++;
			 i1 = new SimpleIndividual(centerList, g);
			 
			 for(Integer j: i1.agentList){
				 if(i1.agents.get(j).getPath().isEmpty()){
					 System.out.println("aqui!!!!");
					 System.out.println(" "  + i + "  ");
					 return;
				 }
			 }
			 
			 
		 }
		 
	}



	@Override
	public GenericMATPIndividual copy() {
		return new SimpleIndividual(this);
	}


	@Override
	public void mutate() {
		this.tweak();
	}



}
