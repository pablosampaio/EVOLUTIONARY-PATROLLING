package taia.strategies;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

import taia.SimpleIndividual;
import taia.util.ListUtil;
import taia.util.MetricFacility;
import yaps.metrics.Metric;
import yaps.util.RandomUtil;

public class ParetoFacility {

	private MetricFacility metrics;
	//private PaletoSparsitySelectionType selectionType = PaletoSparsitySelectionType.RANDOM;
	

	public HashSet<SimpleIndividual> selectRamdom(int numSelected, HashSet<SimpleIndividual> P){
		return new HashSet<SimpleIndividual>(ListUtil.randomChoose(numSelected, new ArrayList<SimpleIndividual>(P))); 		
	} 
	
	public ParetoFacility(MetricFacility metrics){
		this.metrics = metrics;
	}
		
	public boolean paretoDominates(SimpleIndividual i1, SimpleIndividual i2){
		
		boolean a = false;
		
		double[] A = i1.getMultiObjectiveMetricValues();
		double[] B = i2.getMultiObjectiveMetricValues();
		
		
		for(int i = 0; i < A.length; i++){
			
			if(A[i] < B[i]){
				a = true;
			}else if(B[i] < A[i]){
				return false;	
			}	

		}		
		return a;
	}
	
	
	public Hashtable<SimpleIndividual, Double> multiObjectiveSparsityAssignment(List<SimpleIndividual> F){
		
		List<Metric> metricList = metrics.getMetricList();
		
		Hashtable<SimpleIndividual, Double> sparsity = new Hashtable<SimpleIndividual, Double>(F.size());
 		
		for(SimpleIndividual f: F){
			sparsity.put(f, 0.);
		}
		
		double[] meansureArray = new double[F.size()];
		
		for(int metricIndex = 0; metricIndex < metricList.size(); metricIndex++){
			

			for(int i = 0; i < F.size(); i++){
				meansureArray[i] = F.get(i).getMultiObjectiveMetricValues()[metricIndex];
			}
			
			
			List<SimpleIndividual> Fsorted = ListUtil.selectionSort(F, meansureArray);
			
			sparsity.put(Fsorted.get(0), Double.POSITIVE_INFINITY);
			sparsity.put(Fsorted.get(F.size() - 1), Double.POSITIVE_INFINITY);
			
			
			for(int j = 1; j < F.size() - 1; j++){
				double f = sparsity.get(Fsorted.get(j));
				double obj_prev = Fsorted.get(j - 1).getMultiObjectiveMetricValues()[metricIndex],
						obj_post = Fsorted.get(j + 1).getMultiObjectiveMetricValues()[metricIndex];
				
				f += ( (obj_post - obj_prev)/metrics.getMetricRange(metricIndex) ); //PAS: lembrar: sem implementar o range, ainda nao esta 100% correto
				
				sparsity.put(Fsorted.get(j),f);
				
			}
			
		}
		
		
		return sparsity;
	}

	public SimpleIndividual paretoDominationBinaryTournament(SimpleIndividual[] P){
		
		SimpleIndividual i1 = P[RandomUtil.chooseInteger(0, P.length - 1)]; 
		
		SimpleIndividual i2 = P[RandomUtil.chooseInteger(0, P.length - 1)];
		
		if(paretoDominates(i1, i2)){
			return i1;
		}else if(paretoDominates(i2, i1)){
			return i2;
		}
		
		if(RandomUtil.chooseBoolean()){
			return i1;
		}
		
		return i2;
	}
	
	public SimpleIndividual sparsitySelectionTournament(SimpleIndividual[] F, int tournamentSize){
		
		List<SimpleIndividual> Flist = new ArrayList<SimpleIndividual>(F.length);
		
		for(SimpleIndividual f : F){
			Flist.add(f);
		}
		
		Hashtable<SimpleIndividual, Double> fsparsity = this.multiObjectiveSparsityAssignment(Flist);
		
		
		SimpleIndividual best = ListUtil.chooseAtRandom(Flist);
		
		while(tournamentSize-- > 1){
			
			SimpleIndividual next = ListUtil.chooseAtRandom(Flist);
			
			if(fsparsity.get(best) < fsparsity.get(next)){
				best = next;
			}
			
		}
		
		return best;
	}
	
	public SimpleIndividual[] sparsitySelectionTournament(SimpleIndividual[] F, int tournamentSize, int selectionSize){
		
		
		HashSet<SimpleIndividual> bestSet = new HashSet<SimpleIndividual>(selectionSize);
		
		List<SimpleIndividual> Flist = new ArrayList<SimpleIndividual>(F.length);
		
		for(SimpleIndividual f : F){
			Flist.add(f);
		}
		
		Hashtable<SimpleIndividual, Double> fsparsity = this.multiObjectiveSparsityAssignment(Flist);
		
		for(int k = 0; k < selectionSize;){
			
			SimpleIndividual best = ListUtil.chooseAtRandom(Flist);
			
			while(tournamentSize-- > 1){
				
				SimpleIndividual next = ListUtil.chooseAtRandom(Flist);
				
				if(fsparsity.get(best) < fsparsity.get(next)){
					best = next;
				}
				
			}
		
			if(!bestSet.contains(best)){
				bestSet.add(best);
				k++;		
			}
		}
		
		return bestSet.toArray(new SimpleIndividual[0]);
	}
	
	public SimpleIndividual bestSparsitySelection(SimpleIndividual[] F){
		return this.bestSparsitySelection(F, 1)[0];
	}
	
	
	public HashSet<SimpleIndividual> bestSparsitySelection(HashSet<SimpleIndividual>  F, int truncateSize){
		
		List<SimpleIndividual> Flist = new ArrayList<SimpleIndividual>(F.size());
		
		for(SimpleIndividual f : F){
			Flist.add(f);
		}
		
		Hashtable<SimpleIndividual, Double> fsparsity = this.multiObjectiveSparsityAssignment(Flist);
		
		Flist.clear();
		ArrayList<Double> sparsityRank = new ArrayList<Double>();
		
		for(SimpleIndividual f: fsparsity.keySet()){
			Flist.add(f);
			sparsityRank.add(fsparsity.get(f));
		}
		
		Flist = ListUtil.selectionSort(Flist, sparsityRank);
		
		return new HashSet<SimpleIndividual>( Flist.subList(0, truncateSize) );
	}
	
	public SimpleIndividual[] bestSparsitySelection(SimpleIndividual[] F, int truncateSize){
		
		List<SimpleIndividual> Flist = new ArrayList<SimpleIndividual>(F.length);
		
		for(SimpleIndividual f : F){
			Flist.add(f);
		}
		
		Hashtable<SimpleIndividual, Double> fsparsity = this.multiObjectiveSparsityAssignment(Flist);
		
		Flist.clear();
		ArrayList<Double> sparsityRank = new ArrayList<Double>();
		
		for(SimpleIndividual f: fsparsity.keySet()){
			Flist.add(f);
			sparsityRank.add(fsparsity.get(f));
		}
		
		Flist = ListUtil.selectionSort(Flist, sparsityRank);
		
		return Flist.subList(0, truncateSize).toArray(new SimpleIndividual[0]);
	}

	public HashSet<SimpleIndividual> computParetoNonDominatedFront(HashSet<SimpleIndividual> P){
		
		HashSet<SimpleIndividual> Front = new HashSet<SimpleIndividual>();
		
		for(SimpleIndividual p: P){
		
			boolean addNew = true;
			
			
			LinkedList<SimpleIndividual> excluded = new LinkedList<SimpleIndividual>();
			
			for(SimpleIndividual f: Front){
								
				if(paretoDominates(f, p)){
					addNew = false;
					break;
				}else if(paretoDominates(p, f)){
					excluded.add(f);
				}	
			}
			
			if(excluded.size() > 0){
				Front.removeAll(excluded);
			}
			
			if(addNew){
				Front.add(p);
			}
			
		}
		
		return Front;
	}
	
	
	public ArrayList<HashSet<SimpleIndividual>> FrontRankAssignmentByNondominatingSort(HashSet<SimpleIndividual> P){
		
		HashSet<SimpleIndividual> populationSet = new HashSet<SimpleIndividual>(P);
		ArrayList<HashSet<SimpleIndividual>> rankedFront = new ArrayList<HashSet<SimpleIndividual>>();
		
		
		HashSet<SimpleIndividual> frontSet;
		
		while(!populationSet.isEmpty()){
			frontSet = computParetoNonDominatedFront(populationSet);
			populationSet.removeAll(frontSet);
			rankedFront.add(frontSet);
		}
		
		return rankedFront;
	}
	
}
