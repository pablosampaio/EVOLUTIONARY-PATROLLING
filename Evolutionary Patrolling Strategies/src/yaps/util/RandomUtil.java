package yaps.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class RandomUtil {
	
	private static long SEED = 1613; 
	private static Random rand = new Random(SEED); 
	
	/**
	 * Randomly chooses an integer from the inclusive range "from .. to".  
	 */
	public static int chooseInteger(int from, int to) {
		return from + rand.nextInt(to-from+1);
	}

	/**
	 * Escolhe uma das posicoes do array aleatoriamente, com probabilidades 
	 * (para cada posi√ß√£o) proporcionais aos pesos delas. 
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
		
		return weights.length - 1; //nunca devera acontecer!
	}
	
	
	/**
	 * Escolhe um double entre zero e 1(exclusivo).
	 */
	public static double getUniformDouble(){
		return rand.nextDouble();
	}
	
	/**
	 * Escolhe um boolean entre zero e 1(exclusivo).
	 * 
	 */
	//PAS: Que nome eh esse?
	// Acho que ele poderia ter uma vers„o com um par‚metro p da probabilidade
	// de dar true. Vao precisar em alguns algoritmos
	public static boolean getHeadTailTrow(){
		return rand.nextBoolean();
	}
	
	//PAS: Um algoritmo especializado (que eu j· tinha 
	//implementado aqui, mas no tinha enviado). 
	// Fisher-Yattes algorithm
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

}
