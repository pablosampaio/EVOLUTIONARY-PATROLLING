package taia;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import yaps.graph_library.InducedSubGraph;
import yaps.graph_library.Path;
import yaps.graph_library.algorithms.AllPairsShortestPaths;
import yaps.util.RandomUtil;

public class SimpleIndividual implements Comparable<SimpleIndividual>{

	protected HashMap<Integer, AgentMATP> agents;
	protected PreCalculedPathGraph graph;
	protected List<Integer> agentList;
	//private double metricValue;
	private double[] complexMetricValue;
	

	private SimpleIndividual(){}
	

	public static SimpleIndividual buildIndividualData(List<Integer> fixedNodeList, HashMap<Integer, AgentMATP> agents, PreCalculedPathGraph graph){
		
		SimpleIndividual i = new SimpleIndividual();
		
		i.agentList = fixedNodeList;
		i.graph = graph;
		i.agents = agents;
	
		return i;
		
	}
	
	
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

		//PAS: (Secundario) Representando as parti��es com um array? Pode ser invi�vel para
		//algoritmos de popula��o em grafos muito grandes...
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
			
			//Garante que nenhum indiv�duo � inicializado em um caminho de um �nico n�.
			//� bem raro, mas pode acontecer devido aos fatores aleat�rios
			//Gera bugs mais adiante.
			while (nodes.size() < 2){
				Path path = allp.getPath(n, RandomUtil.chooseInteger(0, graph.getNumNodes() - 1));
				
				for(Integer p: path){
					if(!nodes.contains(p)){
						nodes.add(p);
					}
				}
			}
			
			
			//PAS: (secundario) Tem varias formas de inicializar o ciclo. Esta � uma delas (ok).
			// Veja que inicializar envolve: 1) de particionar, 2) formar os ciclos.
			// Seria bom ter varias formas de fazer cada um e parametrizar a escolha delas. 
			agents.put(n,   new AgentMATP(n, new InducedSubGraph(nodes, graph)));
			
		}

	} 

	

/*	
	public void tweak(){
		
		Integer agentNode = ListUtil.chooseAtRandom(this.agentList);
		AgentMATP agent = this.agents.get(agentNode);
		
		
		if(RandomUtil.chooseBoolean()){
			agent.addRandomNodeAndRebuildPath();
		}else{
			agent.removeRandomNodeAndRebuildPath();
		}
		
		
		agent.twoChangeImprove(10);
		
		
	}*/
	
	public AgentMATP getAgentMATP(Integer agent){
		return this.agents.get(agent);
	} 

	public void setgetAgentMATP(Integer agent, AgentMATP rep){
		this.agents.put(agent, rep);
	}
	
	public void addRandomNodeAndRebuilOnAgent(Integer agent){
		this.agents.get(agent).addRandomNodeAndRebuildPath();
	}

	public void addRandomNodeWithSmallChangesOnAgent(Integer agent){
		this.agents.get(agent).addRandomNodeWithSmallChanges();
	}
	
	public void removeRandomNodeAndRebuildOnAgent(Integer agent){
		this.agents.get(agent).removeRandomNodeAndRebuildPath();
	}
	
	public void removeRandomNodeWithSmallChangesOnAgent(Integer agent){
		this.agents.get(agent).removeRandomNodeWithSmallChanges();
	}
	
	
	public void improveAgentByTwoChange(Integer agent){
		this.agents.get(agent).twoChangeImprove();
	}
	
	public List<Integer> getAgentsCentralNodesList(){
		return this.agentList;
	}
	
	
	public PreCalculedPathGraph getGraph() {
		return graph;
	}



	public void setGraph(PreCalculedPathGraph graph) {
		this.graph = graph;
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

	public SimpleIndividual copy() {
		return new SimpleIndividual(this);
	}

	public double getMetricValue() {
		if(this.complexMetricValue == null){
			this.complexMetricValue = new double[1];
			this.complexMetricValue[0] = 0;
		}
		
		return this.complexMetricValue[0];
	}

	@Override
	public String toString() {
		
		StringBuilder sb = new StringBuilder();
		
		sb.append("[");
		for(double d: this.getComplexMetricValue()){
			sb.append( " " + d + " ");
		}
		sb.append("]");
		
		return this.agents.toString() + "\nmetric: " + sb.toString() + "\n";
	}

	public void setMetricValue(double metricValue) {
		if(this.complexMetricValue == null){
			this.complexMetricValue = new double[1];
		}
		
		this.complexMetricValue[0] = metricValue;
	}


	@Override
	public int compareTo(SimpleIndividual o) {
		if(this.getMetricValue() < o.getMetricValue()){
			return -1;
		}else if(this.getMetricValue() == o.getMetricValue()){
			return 0;
		}
		return 1;
	}


	public double[] getComplexMetricValue() {
		return complexMetricValue;
	}


	public void setComplexMetricValue(double[] complexMetricValue) {
		this.complexMetricValue = complexMetricValue;
	}
	
}
