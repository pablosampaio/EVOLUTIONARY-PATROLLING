package taia;

import taia.individual.GenericMATPIndividual;
import yaps.util.RandomUtil;

public class Selections {


	public static GenericMATPIndividual FitnessProportionateSelection(GenericMATPIndividual[] individuals, double[] props){
		return individuals[RandomUtil.chooseProportionally(props)];
	}
	
	public static GenericMATPIndividual FitnessProportionateSelection(GenericMATPIndividual[] individuals){

		double[] props = new double[individuals.length];

		for(int i = 0; i < individuals.length; i++){
			props[i] = individuals[i].getMetricValue();
		}

		return FitnessProportionateSelection(individuals, props);
		
	}


	public static GenericMATPIndividual[] muIndividualsFitnessProportionateSelection(GenericMATPIndividual[] individualsList, int mu){

		double[] props = new double[individualsList.length];
		GenericMATPIndividual[] selection = new GenericMATPIndividual[mu];

		for(int i = 0; i < individualsList.length; i++){
			props[i] = individualsList[i].getMetricValue();
		}


		while(mu-- > 0){

			int i = RandomUtil.chooseProportionally(props);
			props[i] = 0;
			selection[mu - 1] = selection[i];

		}


		return selection;

	}
	
	public static GenericMATPIndividual[] muIndividualsBestFitnessSelection(GenericMATPIndividual[] P, int mu){
		
		GenericMATPIndividual[] Q = new GenericMATPIndividual[mu];
		
	
		for(int i = 0; i < P.length; i++){
			
			if( (Q[mu - 1] == null) || (Q[mu - 1].getMetricValue() > P[i].getMetricValue())){
				insertOnQ(P[i], Q, 0);
			}
			
		}
		
		return Q;
		
	}

	private static void insertOnQ(GenericMATPIndividual pi, GenericMATPIndividual[] Q, int i){
		if(i >= Q.length){
			return;
		}

		if(Q[i] == null){
			Q[i] = pi;
			return;
		}

		if(pi.getMetricValue() < Q[i].getMetricValue()){
			GenericMATPIndividual pj = Q[i];
			Q[i] = pi;
			insertOnQ(pj, Q, i + 1);
			return;
		}

		insertOnQ(pi, Q, i + 1);
		return;

	}

	
	public static GenericMATPIndividual randomFitnessBaseadTournamentSelection(GenericMATPIndividual[] P, int tournamentSize){
		
		GenericMATPIndividual best = P[RandomUtil.chooseInteger(0, P.length - 1)];
		GenericMATPIndividual next;
		for(int i = 1; i < tournamentSize; i++){
			next =  P[RandomUtil.chooseInteger(0, P.length - 1)];
			if(next.getMetricValue() < best.getMetricValue()){
				best = next;
			}
		}
		
	
		return best;
	}


}
