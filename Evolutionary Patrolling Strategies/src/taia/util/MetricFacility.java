package taia.util;

import java.util.ArrayList;

import taia.ClosedPathFacility;
import taia.SimpleIndividual;
import yaps.metrics.Metric;
import yaps.metrics.VisitsList;

public class MetricFacility {

	private ArrayList<Metric> metricList = null;
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

	//PAS: Ao inves de "complexFitness", chama de "multiObjectiveFitness", "multiFitness", algo assim... 
	//     (Rever nos demais lugares tb).
	public double[] assessComplexFitness(SimpleIndividual ind){

		double[] fitness = new double[this.metricList.size()];

		VisitsList v = generateVisitList(ind);

		int k = 0;
		for(Metric m: this.metricList){
			fitness[k] = m.calculate(v, ind.getGraph().getNumNodes(), 1, this.simulationTime);
			k++;
		}

		ind.setComplexMetricValue(fitness);

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
