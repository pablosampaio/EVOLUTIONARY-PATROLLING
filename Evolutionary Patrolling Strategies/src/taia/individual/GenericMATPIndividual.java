package taia.individual;

import java.util.HashMap;
import java.util.List;

import taia.AgentMATP;
import taia.ClosedPathFacility;
import taia.PreCalculedPathGraph;
import yaps.metrics.Metric;
import yaps.metrics.VisitsList;
import yaps.util.RandomUtil;

public abstract class GenericMATPIndividual {

	protected HashMap<Integer, AgentMATP> agents;
	protected PreCalculedPathGraph graph;
	protected List<Integer> agentList;
	private double metricValue;
	
	
	public abstract void rebuilIndiviual();
	
	public abstract GenericMATPIndividual copy();
	
	/**
	 * Soft version of mutate()
	 */
	public abstract void tweak();
	
	/**
	 * Hard version of tweak()
	 */
	public abstract void mutate();
	
	
	public VisitsList generateVisitList(int simulationTime){
		
		VisitsList v = new VisitsList();

		for(Integer n: agentList){
			v.addVisitList( ClosedPathFacility.fromClosedPathToVisitList(this.agents.get(n).getPath(), this.graph.getAllPaths(), simulationTime, n));			
		}
		
		return v;
	}
	
	public double assessFitness(Metric metric, int simulationTime){
		metricValue = metric.calculate( this.generateVisitList(simulationTime), graph.getNumNodes(), 1, simulationTime);
		return metricValue;
	}
	
	public GenericMATPIndividual tweakCopy(){
		GenericMATPIndividual i = this.copy();
		
		while(this.equals(i)){
			i.tweak();
						
		}
		
		return i;
	}

	
	public static GenericMATPIndividual[] fixedCrossOver(GenericMATPIndividual i1, GenericMATPIndividual i2, Integer agent){
		
		GenericMATPIndividual[] out = new GenericMATPIndividual[2];
		
		AgentMATP ag1 = i1.agents.get(agent);
		AgentMATP ag2 = i2.agents.get(agent);
		
		out[0] = i1.copy();
		out[1] = i2.copy();
		
		out[1].agents.put(agent, ag1);
		out[0].agents.put(agent, ag2);
		
		return out;
		
	}
	
	
	public static GenericMATPIndividual[] crossOver(GenericMATPIndividual i1, GenericMATPIndividual i2){
		Integer n = RandomUtil.chooseInteger(0, i1.graph.getNumNodes() - 1 );
		return fixedCrossOver(i1, i2, n);
	}
	
	public static GenericMATPIndividual[] massiveCrossOver(GenericMATPIndividual i1, GenericMATPIndividual i2, List<Integer> agentList){
		
		GenericMATPIndividual[] out = new GenericMATPIndividual[agentList.size()];
		GenericMATPIndividual[] aux;
		int cnt = 0;
		
		for(Integer n: agentList){
			aux = fixedCrossOver(i1, i2, n);
			out[cnt++] = aux[0];
			out[cnt++] = aux[1];
		}
		
		return out;
		
	}
	
	public double getMetricValue() {
		return metricValue;
	}

	
	@Override
	public String toString() {
		return this.agents.toString() + (metricValue == 0? "": "\nmetric: " + this.metricValue);
	}

}
