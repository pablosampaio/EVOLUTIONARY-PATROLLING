package yaps.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class RandomUtil {
	
	private static long SEED = 1613; 
	private static Random rand = new Random(SEED);
	
	
	public static void setNewSeed(long seed){
		SEED = seed;
		rand = new Random(SEED);
	}
	
	/**
	 * Randomly chooses an integer from the inclusive range "from .. to".  
	 */
	public static int chooseInteger(int from, int to) {
		return from + rand.nextInt(to-from+1);
	}

	/**
	 * Randomly chooses an index of the array, with probabilities
	 * proportional to the weights assigned for each index. 
	 */
	public static int chooseProportionally(double[] weights) {
		double sum = 0.0d;
		for (int i = 0; i < weights.length; i++) {
			sum += weights[i];
		}
		
		double choice = sum * rand.nextDouble(); //choice is in interval [0;sum)
		
		double partialSum = 0.0d;
		for (int i = 0; i < weights.length; i++) {
			partialSum += weights[i];
			if (choice <= partialSum) {
				return i;
			}
		}
		
		return weights.length - 1; //nunca deveria acontecer!
	}

	/**
	 * Generates a random uniform double in interval [0, 1).
	 */
	public static double chooseDouble() {
		return rand.nextDouble();
	}

	/**
	 * Generates a random uniform double in interval [min, max).
	 */
	public static double chooseDouble(double min, double max) {
		return min + rand.nextDouble()*(max-min);
	}
	
	public static boolean chooseBoolean() {
		return rand.nextBoolean();
	}
	
	public static boolean chooseBoolean(double probTrue) {
		return rand.nextDouble() <= probTrue;
	}

	//PAS: mudar isso para ListUtil ou trazer metodos aleatórios de lá para cá?
	/**
	 * Randomly shuffles (mixes) a list, returning a new one.
	 * Fisher-Yattes algorithm. 
	 */
	public static <T> List<T> shuffle(List<T> l){
		List<T> list = new ArrayList<T>(l);
		int j;
		T temp;
		for (int i = list.size()-1; i >= 1; i --) {
			j = rand.nextInt(i+1); //[0, i]
			temp = list.get(i);
			list.set(i, list.get(j));
			list.set(j, temp);
		}
		return list;
	}
	
	/**
	 * Randomly shuffles (mixes) a list, changing it directly.
	 * Fisher-Yattes algorithm. 
	 */
	public static <T> void shuffleInline(List<T> list) {
		int j;
		T temp;
		for (int i = list.size()-1; i >= 1; i --) {
			j = rand.nextInt(i+1); //[0, i]
			temp = list.get(i);
			list.set(i, list.get(j));
			list.set(j, temp);
		}
	}

}
