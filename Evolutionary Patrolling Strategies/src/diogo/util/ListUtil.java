package diogo.util;

import java.util.ArrayList;
import java.util.List;

import yaps.util.RandomUtil;

public class ListUtil {

	//
	public static <T> T chooseAtRandom(List<T> list){
		return list.get(RandomUtil.chooseInteger(0, list.size() - 1));
	}
	
	
	/**
	 * Não INCLUSIVO!!!!!!!
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
	
	
}
