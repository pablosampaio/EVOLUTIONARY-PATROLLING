package taia.util;

import taia.ClosedPathFacility;
import taia.SimpleIndividual;
import yaps.metrics.Metric;
import yaps.metrics.VisitsList;

public class MetricFacility {

	
	private Metric metric =  Metric.MAXIMUM_INTERVAL;
	private int simulationTime = 10000;
	
	public VisitsList generateVisitList(SimpleIndividual ind){
		
		VisitsList v = new VisitsList();

		for(Integer n: ind.getAgentsCentralNodesList()){
			v.addVisitList( ClosedPathFacility.fromClosedPathToVisitList(ind.getAgentMATP(n).getPath(), ind.getGraph().getAllPaths(), simulationTime, n));			
		}
		
		return v;
	}
	
	public double assessFitness( SimpleIndividual ind){
		double metricValue = metric.calculate( this.generateVisitList(ind), ind.getGraph().getNumNodes(), 1, simulationTime);
		ind.setMetricValue(metricValue);
		return metricValue;
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
	
	
}
