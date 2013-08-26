package taia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import taia.strategies.CrossOver;
import taia.strategies.CrossOverType;
import taia.strategies.IndividualBuilder;
import taia.strategies.IndividualBuilderCenterChooseType;
import taia.strategies.IndividualBuilderPartitionType;
import taia.strategies.IndividualBuilderPathConstructorType;
import taia.strategies.Mutate;
import taia.strategies.MutateType;
import taia.strategies.Selection;
import taia.strategies.SelectionType;
import taia.util.MetricFacility;
import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;
import yaps.metrics.Metric;

public class Experiments {


	public static String[] graphsNames = {

		"./maps/island11",
		"./maps/complete15",
		"./maps/map_a.adj",
		"./maps/map_cicles_corridor.adj",
		"./maps/map_grid.adj",
		"./maps/map_islands.adj",

	};

	public static MutateType[] mutateTypes= {
		MutateType.HALFADD_HALFSUB_SMALLCHANGES,
		MutateType.HALFADD_HALFSUB_SMALLCHANGES_IMPROVE,
		MutateType.HALFADD_HALFSUB_REBUILD,
		MutateType.HALFADD_HALFSUB_REBUILD_IMPROVE,
	};

	public static IndividualBuilderPathConstructorType[] pathConstructorTypes = {
		IndividualBuilderPathConstructorType.NearestNeighborMethod,
		IndividualBuilderPathConstructorType.NearestInsertionMethod
	};

	public static IndividualBuilderCenterChooseType[] centerChooseType  = {
		IndividualBuilderCenterChooseType.RANDOM,
		IndividualBuilderCenterChooseType.APROXIMATED_MAXIMUM_DISTANCE
	};


	public static IndividualBuilderPartitionType[] partitionType = {
		IndividualBuilderPartitionType.FUNGAE_COLONY,
		IndividualBuilderPartitionType.RANDOM_START
	};

	public static CrossOverType[] crossOverTypes  = {
		CrossOverType.SIMPLE_RANDOM_CROSSOVER
	};


	public static SelectionType[] selectionTypes= {
		SelectionType.TOURNAMENT,
		SelectionType.BESTFITNESS
	};

	public static HashMap<String, PreCalculedPathGraph> generateGraphTable(){

		HashMap<String, PreCalculedPathGraph> pList = new  HashMap<String, PreCalculedPathGraph>();

		for(String name: graphsNames){

			try {
				Graph g = GraphReader.readAdjacencyList(name, GraphDataRepr.LISTS);

				pList.put(name, new PreCalculedPathGraph(g));

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		return pList;
	}



	public static List<Mutate> generateMutateList(int minNumberImproves, int maxNumberImproves){

		ArrayList<Mutate> mList = new ArrayList<Mutate>();

		for(MutateType t: mutateTypes){

			for(IndividualBuilderPathConstructorType pc: pathConstructorTypes){

				for(int i = minNumberImproves; i <= maxNumberImproves; i++){

					Mutate m = new Mutate();
					m.setMutateType(t);
					m.setNumberOfImprovesTries(i);
					m.setRebuildType(pc);
					mList.add(m);
				}

			}

		}
		return mList;
	}

	public static List<IndividualBuilder> generateIndividualBuilderList(PreCalculedPathGraph graph, int minNumberOfAgents, int maxNumberOfAgents){
		ArrayList<IndividualBuilder> bList = new ArrayList<IndividualBuilder>();

		for(IndividualBuilderPartitionType pt:partitionType){
			for(IndividualBuilderCenterChooseType ct:centerChooseType){
				for(IndividualBuilderPathConstructorType pht: pathConstructorTypes){
					for(int numAgents = minNumberOfAgents; numAgents <= maxNumberOfAgents; numAgents++){
						IndividualBuilder indB = new IndividualBuilder(graph, numAgents);
						indB.setConstructorParameters(ct, pt, pht);
						indB.setUpBuilder();
						bList.add(indB);
					}
				}
			}
		}

		return bList;
	}


	public static List<CrossOver> generateCrossOverList(){

		ArrayList<CrossOver> cList = new ArrayList<CrossOver>();

		for(CrossOverType ct: crossOverTypes){
			CrossOver c = new CrossOver();
			c.setType(ct);
			cList.add(c);
		}

		return cList;
	}


	public static List<Selection> generateSelectionList(int maxTournamentSize){
		
		ArrayList<Selection> sList = new ArrayList<Selection>();
		
		for(SelectionType manyt: selectionTypes){
			for(SelectionType onet: selectionTypes){
				
				if(manyt == SelectionType.TOURNAMENT || onet == SelectionType.TOURNAMENT){
				
					for(int i = 2; i < maxTournamentSize; i += 2){
					
						Selection s = new Selection();
						s.setTypeForMany(manyt);
						s.setTypeForOne(onet);
						s.setTournamentSize(i);
						sList.add(s);
						
					}
					
					
				}else{
					
					Selection s = new Selection();
					s.setTypeForMany(manyt);
					s.setTypeForOne(onet);
					sList.add(s);
				}
				

			}
		}
		
		return sList;
		
	}
	
	public static void executeHillClimb(HashMap<String, PreCalculedPathGraph> gtable, List<Mutate> mlist, HashMap<String, List<IndividualBuilder>> iblist, MetricFacility metric){
		executeHillClimb(gtable, mlist, iblist, metric, 10000);
	}


	public static void executeHillClimbWithRandomRestarts(HashMap<String, PreCalculedPathGraph> gtable, List<Mutate> mlist, HashMap<String, List<IndividualBuilder>> iblist, MetricFacility metric, int numberOfRestarts, int[] distributionOfTimeIntervals){
		for(String name: graphsNames){
			PreCalculedPathGraph g = gtable.get(name);
			for(Mutate m: mlist){
				for(IndividualBuilder ib: iblist.get(name)){

					System.out.println("New execution:");

					StringBuilder sb = new StringBuilder();
					sb.append("Graph name: " + name + "\n");
					sb.append(ib.toString() + "\n");
					sb.append(m.toString() + "\n");

					System.out.println(sb);

					HillClimbWithRandomRestarts h = new HillClimbWithRandomRestarts(g, metric, m, ib);

					SimpleIndividual s = h.climbHillWithRandomRestarts(numberOfRestarts, distributionOfTimeIntervals);

					System.out.println(s.toString());


				}
			}
		}
	}

	public static void executeHillClimb(HashMap<String, PreCalculedPathGraph> gtable, List<Mutate> mlist, HashMap<String, List<IndividualBuilder>> iblist, MetricFacility metric, int numberIterations){
		for(String name: graphsNames){
			PreCalculedPathGraph g = gtable.get(name);
			for(Mutate m: mlist){
				for(IndividualBuilder ib: iblist.get(name)){

					System.out.println("New execution:");

					StringBuilder sb = new StringBuilder();
					sb.append("Graph name: " + name + "\n");
					sb.append(ib.toString() + "\n");
					sb.append(m.toString() + "\n");

					System.out.println(sb);

					HillClimb h = new HillClimb(g, metric, m, ib);
					SimpleIndividual s = h.doHillClimb(numberIterations);

					System.out.println(s.toString());

				}
			}
		}
	}

	public static void executeSimulatedAnnealing(HashMap<String, PreCalculedPathGraph> gtable, List<Mutate> mlist, HashMap<String, List<IndividualBuilder>> iblist, MetricFacility metric, int numberIterations){
		for(String name: graphsNames){
			PreCalculedPathGraph g = gtable.get(name);
			for(Mutate m: mlist){
				for(IndividualBuilder ib: iblist.get(name)){

					System.out.println("New execution:");

					StringBuilder sb = new StringBuilder();
					sb.append("Graph name: " + name + "\n");
					sb.append(ib.toString() + "\n");
					sb.append(m.toString() + "\n");

					
					System.out.println(sb);
					SimulatedAnnealing h= new SimulatedAnnealing(g, metric, m, ib);
					
					
					for(double initialTemp = 20; initialTemp > 0.5; initialTemp = initialTemp * 0.8){
						for(double rate = 0.01; rate < (initialTemp/3) ; rate = rate * 2){

							
							
							StringBuilder sb2 = new StringBuilder();
							
							sb2.append("Initial temp = " + initialTemp);
							sb2.append(" Decay rate = " + rate + ".\n");
							
							System.out.println(sb2);
							
							SimpleIndividual s = h.simulatedAnnealing(numberIterations, initialTemp, rate);
							System.out.println(s.toString());
						}
					}
				}
			}
		}
	}
	
	public static void executeMuLambdaStrategy(HashMap<String, PreCalculedPathGraph> gtable, List<Mutate> mlist, HashMap<String, List<IndividualBuilder>> iblist, MetricFacility metric, int time, int muMax, int lambdaMax) {
		
		for(String name : graphsNames) {
			
			for(Mutate m : mlist) {
				
				for(IndividualBuilder ib : iblist.get(name)) {
					
					System.out.println("New execution:");
					
					StringBuilder sb = new StringBuilder();
					sb.append("Graph name: " + name + "\n");
					sb.append(ib.toString() + "\n");
					sb.append(m.toString() + "\n");

					
					System.out.println(sb);
					
					for(int mu = 1; mu <= muMax; mu+= 4) {
						
						for(int lambda = 1 +  mu; lambda <= lambdaMax; lambda+= 4) {
							
							MuLambdaStrategy mulambda = new MuLambdaStrategy(m, metric, ib, mu, lambda);
							
							System.out.println("Mu: "+mu+"; Lambda: "+mulambda.getLambda()+".");
							
							
							SimpleIndividual s = mulambda.doMuLambdaStrategy(time);
							System.out.println(s.toString());
							
						}	
					}	
				}	
			}	
		}
	}
	
	public static void executeMuPlusLambdaStrategy(HashMap<String, PreCalculedPathGraph> gtable, List<Mutate> mlist, HashMap<String, List<IndividualBuilder>> iblist, MetricFacility metric, int time, int muMax, int lambdaMax) {
		
		for(String name : graphsNames) {
			
			for(Mutate m : mlist) {
				
				for(IndividualBuilder ib : iblist.get(name)) {
					
					System.out.println("New execution:");
					
					StringBuilder sb = new StringBuilder();
					sb.append("Graph name: " + name + "\n");
					sb.append(ib.toString() + "\n");
					sb.append(m.toString() + "\n");

					
					System.out.println(sb);
					
					for(int mu = 1; mu <= muMax; mu+= 4) {
						
						for(int lambda = 1 +  mu; lambda <= lambdaMax; lambda+= 4) {
							
							MuPlusLambdaStrategy mulambda = new MuPlusLambdaStrategy(m, metric, ib, mu, lambda);
							
							System.out.println("Mu: "+mu+"; Lambda: "+mulambda.getLambda()+".");
							
							
							SimpleIndividual s = mulambda.doMuLambdaStrategy(time);
							System.out.println(s.toString());
							
						}	
					}	
				}	
			}	
		}
	}
	
	
	public static void executeGeneticAlgorithm(HashMap<String, PreCalculedPathGraph> gtable, List<Mutate> mlist, HashMap<String, List<IndividualBuilder>> iblist, List<Selection> slist, List<CrossOver> clist, MetricFacility metric, int maxPopSize, int numberOfIterations) {
		
		for(String name : graphsNames) {
			for(Mutate m : mlist) {
				for(IndividualBuilder ib : iblist.get(name)) {
					for(CrossOver c: clist){
						for(Selection s: slist){
							
							System.out.println("New execution:");
							
							StringBuilder sb = new StringBuilder();
							sb.append("Graph name: " + name + "\n");
							sb.append(ib.toString() + "\n");
							sb.append(m.toString() + "\n");
							sb.append(c.toString());
							sb.append(s.toString());
							
							System.out.println(sb);
							
							
							for(int pop = 4; pop < maxPopSize; pop *= 2){
								
								GeneticAlgorithm ge = new GeneticAlgorithm(m, metric, ib, c, s, pop);
								
								System.out.println("Population size: "+ ge.getPopulationSize() + ".");
								
								SimpleIndividual si = ge.doEvolvePopulation(numberOfIterations);
							
								System.out.println(si);
								
							}
						}
					}
				}	
			}	
		}
	}
	
	public static void executeGeneticAlgorithmWithEltism(HashMap<String, PreCalculedPathGraph> gtable, List<Mutate> mlist, HashMap<String, List<IndividualBuilder>> iblist, List<Selection> slist, List<CrossOver> clist, MetricFacility metric, int maxPopSize, int maxElistmSize, int numberOfIterations) {
		
		for(String name : graphsNames) {
			for(Mutate m : mlist) {
				for(IndividualBuilder ib : iblist.get(name)) {
					for(CrossOver c: clist){
						for(Selection s: slist){
							
							System.out.println("New execution:");
							
							StringBuilder sb = new StringBuilder();
							sb.append("Graph name: " + name + "\n");
							sb.append(ib.toString() + "\n");
							sb.append(m.toString() + "\n");
							sb.append(c.toString());
							sb.append(s.toString());
							
							System.out.println(sb);
														
							for(int pop = 4; pop < maxPopSize; pop *= 2){
								
								
								for(int el = 1; el < maxElistmSize; el += 2){
									
									GeneticAlgorithmWithElitism ge = new GeneticAlgorithmWithElitism(m, metric, ib, c, s, pop, el);
									
									System.out.println("Population size: "+ ge.getPopSize() + ", elitsm: " + el);
									
									SimpleIndividual si = ge.doEvolvePopulation(numberOfIterations);
								
									System.out.println(si);
								}
							}
						}
					}
				}	
			}	
		}
	}
	
	public static void main(String[] args) {


		List<Mutate> mlist = generateMutateList(1, 10);

		List<CrossOver> cList = generateCrossOverList();
		
		List<Selection> sList = generateSelectionList(10);
		
		HashMap<String, PreCalculedPathGraph> gTable = generateGraphTable();
		HashMap<String, List<IndividualBuilder>> builderTable = 
				new HashMap<String, List<IndividualBuilder>>();

		for(String name: graphsNames){
			builderTable.put(name, generateIndividualBuilderList(gTable.get(name), 2, 10));
		}


		MetricFacility metric = new MetricFacility();
		metric.setSimulationTime(1000);
		metric.setMetric(Metric.MAXIMUM_INTERVAL);

		executeHillClimb(gTable, mlist, builderTable, metric, 10);


		int[] distribution = new int[10];

		for(int i = 0; i < 10; i++) {
			distribution[i] = 10;
		}

		executeHillClimbWithRandomRestarts(gTable, mlist, builderTable, metric, 10, distribution);

		
		executeSimulatedAnnealing(gTable, mlist, builderTable, metric, 100);
	
		executeMuLambdaStrategy(gTable, mlist, builderTable, metric, 100, 20, 30);
		
		executeMuPlusLambdaStrategy(gTable, mlist, builderTable, metric, 100, 20, 30);
		
		
		executeGeneticAlgorithm(gTable, mlist, builderTable, sList, cList, metric, 30, 100);
		
		executeGeneticAlgorithmWithEltism(gTable, mlist, builderTable, sList, cList, metric, 30, 10, 100);
		
	}

}

