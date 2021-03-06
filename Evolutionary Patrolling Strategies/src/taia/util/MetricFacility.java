package taia.util;

import java.util.ArrayList;

import taia.SimpleIndividual;
import yaps.graph_library.Path;
import yaps.graph_library.algorithms.AllPairsShortestPaths;
import yaps.metrics.Metric;
import yaps.metrics.VisitsList;

public class MetricFacility {

	private ArrayList<Metric> metricList = null;
	private Metric metric =  Metric.MAXIMUM_INTERVAL; //PAS: Sugest�o: remover este atributo e considerar a m�trica 
	                                                  //     do indice 0 da lista de metricas como a metrica dos casos mono-objetivo.
	private int simulationTime = 10000;

	public static VisitsList fromClosedPathToVisitList(Path p, AllPairsShortestPaths allPaths, int time, int agent){

		VisitsList v = new VisitsList();

		int lastTime = 1;
		v.addVisit(lastTime, p.getFirst());

		for(int i = 1; i < time; i++){
			int lNode = p.get( (i -1) % (p.size()-1) );
			int cNode = p.get( (i) % (p.size()-1)  );
			lastTime += allPaths.getDistance(lNode, cNode);
			
			v.addVisit(lastTime, cNode, agent);
			//v.addVisit(lastTime, cNode);
		}

		return v;
	}
	
	public static VisitsList fromClosedPathToVisitList(Path p, AllPairsShortestPaths allPaths, int time){
		return fromClosedPathToVisitList(p, allPaths, time, -1);
	}
	
	public VisitsList generateVisitList(SimpleIndividual ind){

		VisitsList v = new VisitsList();

		for(Integer n: ind.getAgentsCentralNodesList()){
			v.addVisitList( fromClosedPathToVisitList(ind.getAgentMATP(n).getPath(), ind.getGraph().getAllPaths(), simulationTime, n));			
		}

		return v;
	}

	public double assessFitness( SimpleIndividual ind){
		//PAS: Nao recalcular se ja tiver sido calculado (isso pode ajudar a melhorar o tempo dos algoritmos mais "steady-state")
		double metricValue = metric.calculate( this.generateVisitList(ind), ind.getGraph().getNumNodes(), 1, simulationTime);
		ind.setMetricValue(metricValue);
		return metricValue;
	}
	
	public double[] assessMultiObjectveFitness(SimpleIndividual ind){

		double[] fitness = new double[this.metricList.size()];

		VisitsList v = generateVisitList(ind);

		int k = 0;
		for(Metric m: this.metricList){
			fitness[k] = m.calculate(v, ind.getGraph().getNumNodes(), 1, this.simulationTime);
			k++;
		}

		ind.setMultiObjectiveMetricValues(fitness);

		return fitness;
	}


	public Metric getMetric() {
		return metric;
	}

	public void setMetric(Metric metric) {
		this.metric = metric;
	}

	public int getSimulationTime() {
		return simulationTime;
	}

	public void setSimulationTime(int simulationTime) {
		this.simulationTime = simulationTime;
	}


	public void addNewMetirc(Metric m){

		if(this.metricList == null){
			this.metricList = new ArrayList<Metric>();
		}

		this.metricList.add(m);

	}

	//FIXME Implementar calculo do range!!!
	public double getMetricRange(int metricIndex){
		return 1.0;
	}

	public ArrayList<Metric> getMetricList() {
		return metricList;
	}
	

}
