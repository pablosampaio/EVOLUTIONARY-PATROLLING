package taia.util;

import java.util.ArrayList;
import java.util.List;

import yaps.util.RandomUtil;

public class ListUtil {

	//
	public static <T> T chooseAtRandom(List<T> list){
		return list.get(RandomUtil.chooseInteger(0, list.size() - 1));
	}


	/**
	 * N�o INCLUSIVO!!!!!!!
	 * @param start
	 * @param end
	 * @param increment
	 * @return
	 */
	public static List<Integer> createIndexList(int start, int end, int increment){

		ArrayList<Integer> list = new ArrayList<Integer>();

		for(int i = start; i < end; i+=increment){
			list.add(i);

		}


		return list;
	}



	public static <T> List<T> randomChoose(int numOfItens, List<T> list){

		int listSize = list.size();

		if(numOfItens > listSize || numOfItens < 0){
			throw new IllegalArgumentException("Numero de itens incompativel.");
		}

		if(numOfItens == listSize){
			return new ArrayList<T>(list);
		}


		List<T> choose = new ArrayList<T>();

		while(choose.size() < numOfItens ){

			T ob = list.get(RandomUtil.chooseInteger(0, listSize - 1));

			if(!choose.contains(ob)){
				choose.add(ob);
			}

		}


		return choose;

	}



	public static <T> List<T> selectionSort(List<T> a, List<Double> values) {
		
		List<T> b = new ArrayList<T>(a);
		List<Double> meansure = new ArrayList<Double>(values);
		int choose;
		
		for(int i = 0; i < b.size(); i++){
			choose = i;
			for(int j = i + 1; j < a.size(); j++){
				if(meansure.get(j) < meansure.get(choose)){
					choose = j;
				}
			}
			
			T temp = b.get(i);
			b.set(i, b.get(choose));
			b.set(choose, temp);
			
			Double v = meansure.get(i);
			meansure.set(i, meansure.get(choose));
			meansure.set(choose, v);
			
		}
				
		return b;	
	}

	public static <T> List<T> selectionSort(List<T> a,  double[] values) {
		
		List<T> b = new ArrayList<T>(a);
		double[]  meansure = new  double[values.length];
		int choose;
		
		for(int i = 0; i < values.length; i++){
			meansure[i] = values[i];
		}
		
		for(int i = 0; i < b.size(); i++){
			choose = i;
			for(int j = i + 1; j < a.size(); j++){
				if(meansure[j] < meansure[choose]){
					choose = j;
				}
			}
			
			T temp = b.get(i);
			b.set(i, b.get(choose));
			b.set(choose, temp);
			
			Double v = meansure[i];
			meansure[i] = meansure[choose];
			meansure[choose] = v;
			
		}
				
		return b;	
	}
	
	


}
