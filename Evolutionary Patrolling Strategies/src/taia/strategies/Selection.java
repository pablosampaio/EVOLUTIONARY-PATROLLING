package taia.strategies;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import taia.SimpleIndividual;
import taia.util.ListUtil;
import yaps.util.RandomUtil;

/* PAS: Faltou o principal: tournment (com um parâmetro k).
 * Pensar: criar como subclasses?
 */
public class Selection {

	private SelectionType typeForOne = SelectionType.TOURNAMENT;
	private SelectionType  typeForMany = SelectionType.BESTFITNESS;
	private int tournamentSize = 4;
	
	





	public SelectionType getTypeForOne() {
		return typeForOne;
	}





	public void setTypeForOne(SelectionType typeForOne) {
		this.typeForOne = typeForOne;
	}





	public SelectionType getTypeForMany() {
		return typeForMany;
	}





	public void setTypeForMany(SelectionType typeForMany) {
		this.typeForMany = typeForMany;
	}





	public int getTournamentSize() {
		return tournamentSize;
	}





	public void setTournamentSize(int tournamentSize) {
		this.tournamentSize = tournamentSize;
	}




	public SimpleIndividual[] selectManyFromMany(SimpleIndividual[] population, int selectionSize){
		
		SimpleIndividual[] selection = new SimpleIndividual[selectionSize];
		
		switch (typeForMany) {
		case TOURNAMENT:
			for(int i = 0; i < selectionSize; i++){
				selection[i] = randomFitnessBasedTournamentSelection(population, this.tournamentSize);
			}
			
			return selection;
		case BESTFITNESS:
			
			//VT&DM Yes, you can do better.....
			ArrayList<SimpleIndividual> t = new ArrayList<SimpleIndividual>( population.length );
			
			for(SimpleIndividual p: population){
				t.add(p);
			}
			
			Collections.sort(t);
			
			SimpleIndividual[] h = new SimpleIndividual[0];
			
			return t.subList(0, selectionSize).toArray(h);
		
		default:
			break;
		
		}
		
		return null;
		
	}

	
	public SimpleIndividual selectOneFromMany(SimpleIndividual[] population){
		
		switch (typeForOne) {
		case TOURNAMENT:
			return randomFitnessBasedTournamentSelection(population, this.tournamentSize);
		case BESTFITNESS:
			return bestFitnessSelection(population);
		default:
			break;
		
		}
		
		return null;
		
	}
	
	
	
	//FIXME implement selection!!!!!!!!!!!!!!!
	public static SimpleIndividual bestFitnessSelection(SimpleIndividual[] P){
		return P[0];
	}

	public static SimpleIndividual FitnessProportionateSelection(SimpleIndividual[] individuals, double[] props){
		return individuals[RandomUtil.chooseProportionally(props)];
	}
	
	public static SimpleIndividual FitnessProportionateSelection(SimpleIndividual[] individuals){

		double[] props = new double[individuals.length];

		for(int i = 0; i < individuals.length; i++){
			props[i] = individuals[i].getMetricValue();
		}

		return FitnessProportionateSelection(individuals, props);
		
	}


	public static SimpleIndividual[] muIndividualsFitnessProportionateSelection(SimpleIndividual[] individualsList, int mu){

		double[] props = new double[individualsList.length];
		SimpleIndividual[] selection = new SimpleIndividual[mu];

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
	
	public static SimpleIndividual[] muIndividualsBestFitnessSelection(SimpleIndividual[] P, int mu){
		
		SimpleIndividual[] Q = new SimpleIndividual[mu];
		
	
		for(int i = 0; i < P.length; i++){
			
			if( (Q[mu - 1] == null) || (Q[mu - 1].getMetricValue() > P[i].getMetricValue())){
				insertOnQ(P[i], Q, 0);
			}
			
		}
		
		return Q;
		
	}

	private static void insertOnQ(SimpleIndividual pi, SimpleIndividual[] Q, int i){
		if(i >= Q.length){
			return;
		}

		if(Q[i] == null){
			Q[i] = pi;
			return;
		}

		if(pi.getMetricValue() < Q[i].getMetricValue()){
			SimpleIndividual pj = Q[i];
			Q[i] = pi;
			insertOnQ(pj, Q, i + 1);
			return;
		}

		insertOnQ(pi, Q, i + 1);
		return;

	}

	//PAS: Deveria selecionar k distintos (sem repetição).
	public static SimpleIndividual randomFitnessBasedTournamentSelection(SimpleIndividual[] P, int tournamentSize){
		
		
		List<Integer> tournamentOrder = ListUtil.createIndexList(0, P.length, 1);
		RandomUtil.shuffleInline(tournamentOrder);
		
		SimpleIndividual best = P[tournamentOrder.get(0)];
		SimpleIndividual next;
		
		for(int i = 1; i < tournamentSize; i++){
			next =  P[tournamentOrder.get(i)];
			if(next.getMetricValue() < best.getMetricValue()){
				best = next;
			}
		}
			
		return best;
	}

	

	

}
