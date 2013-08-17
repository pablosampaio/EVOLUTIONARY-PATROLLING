package taia.strategies;

import java.util.ArrayList;
import java.util.HashSet;

import taia.SimpleIndividual;
import taia.util.ListUtil;
import yaps.util.RandomUtil;

public class ParetoFacility {

	
	private PaletoSparsitySelectionType selectionType = PaletoSparsitySelectionType.RANDOM;
	

	public static HashSet<SimpleIndividual> selectRamdom(int numSelected, HashSet<SimpleIndividual> P){
		return new HashSet<SimpleIndividual>(ListUtil.randomChoose(numSelected, new ArrayList<SimpleIndividual>(P))); 		
	} 
	
	
	public HashSet<SimpleIndividual> selectManyBySparsity(int numSelected, HashSet<SimpleIndividual> P){
		
		switch(this.selectionType){
		case RANDOM:
			return selectRamdom(numSelected, P);
		default:
			break;
		
		}
		
		
		return null;
	}
	
	
	public static boolean paretoDominates(SimpleIndividual i1, SimpleIndividual i2){
		
		boolean a = false;
		
		double[] A = i1.getComplexMetricValue();
		double[] B = i2.getComplexMetricValue();
		
		
		for(int i = 0; i < A.length; i++){
			
			if(A[i] < B[i]){
				a = true;
			}else if(B[i] < A[i]){
				return false;	
			}	

		}		
		return a;
	}
	

	public static SimpleIndividual paretoDominationBinaryTournament(SimpleIndividual[] P){
		
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
	
	public static HashSet<SimpleIndividual> computParetoNonDominatedFront(HashSet<SimpleIndividual> P){
		
		HashSet<SimpleIndividual> Front = new HashSet<SimpleIndividual>();
		
		for(SimpleIndividual p: P){
		
			boolean addNew = true;
			
			for(SimpleIndividual f: Front){
								
				if(paretoDominates(f, p)){
					addNew = false;
					break;
				}else if(paretoDominates(p, f)){
					Front.remove(f);
				}	
			}
			
			if(addNew){
				Front.add(p);
			}
			
		}
		
		return Front;
	}
	
	
	public static ArrayList<HashSet<SimpleIndividual>> FrontRankAssignmentByNondominatingSort(HashSet<SimpleIndividual> P){
		
		HashSet<SimpleIndividual> populationSet = new HashSet<SimpleIndividual>(P);
		ArrayList<HashSet<SimpleIndividual>> rankedFront = new ArrayList<HashSet<SimpleIndividual>>();
		
		
		HashSet<SimpleIndividual> frontSet;
		
		while(!populationSet.isEmpty()){
			frontSet = computParetoNonDominatedFront(populationSet);
			populationSet.removeAll(frontSet);
			rankedFront.add(frontSet);
		}
		
		return null;
	}


	public PaletoSparsitySelectionType getSelectionType() {
		return selectionType;
	}


	public void setSelectionType(PaletoSparsitySelectionType selectionType) {
		this.selectionType = selectionType;
	}
	
	
	
	
	
	
}
















