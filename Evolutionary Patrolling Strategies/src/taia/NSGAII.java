package taia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;

import taia.strategies.BreedStrategy;
import taia.strategies.CrossOver;
import taia.strategies.CrossOverType;
import taia.strategies.IndividualBuilder;
import taia.strategies.IndividualBuilderCenterChooseType;
import taia.strategies.IndividualBuilderPartitionType;
import taia.strategies.IndividualBuilderPathConstructorType;
import taia.strategies.Mutate;
import taia.strategies.MutateType;
import taia.strategies.ParetoFacility;
import taia.strategies.Selection;
import taia.strategies.SelectionType;
import taia.util.MetricFacility;
import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;
import yaps.metrics.Metric;

public class NSGAII {
	
	private ParetoFacility pareto = null;
	private MetricFacility mtf = new MetricFacility();
	private IndividualBuilder individualBuilder = null;
	private int archiveSize = 20;
	private int populationSize = 50;
	private BreedStrategy breed;
	

	public NSGAII(int archiveSize, int populationSize, ParetoFacility pareto, IndividualBuilder ind){
		this.individualBuilder = ind;
		this.pareto = pareto;
		this.archiveSize = archiveSize;
		this.populationSize = populationSize;
	}

	public HashSet<SimpleIndividual> doNSGAII(int time){
		
		HashSet<SimpleIndividual> P = new HashSet<SimpleIndividual>(this.populationSize);
		
		for(int i = 0; i < this.populationSize; i++){
			P.add(individualBuilder.buildNewIndividual());
		}
		
		HashSet<SimpleIndividual> best = null;
		ArrayList<HashSet<SimpleIndividual>> R;
		
		while(time-- > 0){
			
			for(SimpleIndividual p: P){
				this.mtf.assessComplexFitness(p);
			}			
			
			R = this.pareto.FrontRankAssignmentByNondominatingSort(P);
			
			best = R.get(0);
			
			HashSet<SimpleIndividual> A = new HashSet<SimpleIndividual>();
			
			for(int i = 0; i < R.size(); i++){
				HashSet<SimpleIndividual> Ri = R.get(i);
				
				if((Ri.size() + A.size()) > this.archiveSize){
					A.addAll(this.pareto.bestSparsitySelection(Ri, this.archiveSize - A.size()));
					break;
				}else{
					A.addAll(Ri);
				}
			}
			
			P = this.breed.verySimpleBreedStraegy(A);
			
			
		}
		
		
		return best;
	}
	
	public void setPareto(ParetoFacility pareto) {
		this.pareto = pareto;
	}

	public void setMetricFacility(MetricFacility mtf) {
		this.mtf = mtf;
	}

	public void setIndividualBuilder(IndividualBuilder individualBuilder) {
		this.individualBuilder = individualBuilder;
	}

	public void setArchiveSize(int archiveSize) {
		this.archiveSize = archiveSize;
	}

	public void setPopulationSize(int populationSize) {
		this.populationSize = populationSize;
	}

	public void setBreed(BreedStrategy breed) {
		this.breed = breed;
	}
	
	public static void main(String[] args) throws IOException {
		
		int arquiveSize = 3;
		int popSize = 10;
		
		
		Graph g = GraphReader.readAdjacencyList("./maps/island11", GraphDataRepr.LISTS);
		PreCalculedPathGraph pg = new PreCalculedPathGraph(g);
		
		MetricFacility metrics = new MetricFacility();
		metrics.addNewMetirc(Metric.MAXIMUM_INTERVAL);
		metrics.addNewMetirc(Metric.STD_DEV_OF_FREQUENCIES);
		metrics.setSimulationTime(1000);
		
		ParetoFacility pareto = new ParetoFacility(metrics);
		
		IndividualBuilder builder = new IndividualBuilder(pg);
		builder.setCenterType(IndividualBuilderCenterChooseType.RANDOM);
		builder.setPartitionType(IndividualBuilderPartitionType.RANDOM_START);
		builder.setPathBuilderType(IndividualBuilderPathConstructorType.NearestNeighborMethod);
		builder.setUpBuilder();

		
		Selection select = new Selection();
		select.setTypeForMany(SelectionType.PARETO_BINARY);
		select.setTypeForOne(SelectionType.PARETO_BINARY);
		select.setPareto(pareto);
		
		Mutate mutate = new Mutate();
		mutate.setMUtateType(MutateType.HALFADD_HALFSUB_REBUILD_IMPROVE);
		
		CrossOver cross = new CrossOver();
		cross.setType(CrossOverType.SIMPLE_RANDOM_CROSSOVER);
		
		BreedStrategy breed = new BreedStrategy(builder, popSize);
		breed.setSelection(select);
		breed.setCrossOver(cross);
		breed.setPareto(pareto);
		breed.setIndividualBuilder(builder);
		breed.setMutate(mutate);
		
		
		NSGAII nsga = new NSGAII(arquiveSize, popSize, pareto, builder);
		nsga.setBreed(breed);
		nsga.setMetricFacility(metrics);
		
		
		nsga.doNSGAII(20);
		
		
		
	}


}







































