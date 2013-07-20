package yaps.util;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * List created to allow slicing of lists in a lazy fashion. 
 * 
 * It is specially suited for creating concatenations (returned as common java list) 
 * using slices of a list, assuming that this original list won't change during the
 * process. 
 *  
 * @author Pablo A. Sampaio
 */
public class ListView<E> extends AbstractList<E> {
	private int start;
	private int viewSize;
	private List<E> originalList;
	
	private boolean revert;
	
	//obs: endIndex is inclusive!
	public ListView(List<E> list, int startIndex, int endIndex) {
		this.originalList = list;
		this.start = startIndex;

		if (startIndex < 0 || startIndex >= list.size()) {
			throw new IndexOutOfBoundsException("index:" + startIndex + " size:" + list.size());
		}
		if (endIndex < 0 || endIndex >= list.size()) {
			throw new IndexOutOfBoundsException("index:" + endIndex + " size:" + list.size());
		}
		
		if (endIndex >= startIndex) {
			this.revert = false;
			this.viewSize = endIndex - startIndex + 1;
		} else {
			this.revert = true;
			this.viewSize = startIndex - endIndex + 1;
		}		
	}

	public ListView(List<E> list, int startIndex) {
		this(list, startIndex, list.size()-1);
	}

	public ListView(List<E> list, int startIndex, boolean invert) {
		this(list, startIndex, invert? 0 : list.size()-1);
	}

	@Override
	public E get(int index) {
		if (index >= this.viewSize) {
			throw new IndexOutOfBoundsException("index:" + index + " size:" + this.viewSize);
		}		
		int adjustedIndex = this.revert? (this.start - index) : (this.start + index); 
		return this.originalList.get(adjustedIndex);
	}

	@Override
	public int size() {
		return this.viewSize;
	}
	
	public List<E> createConcatenation(List<E> list) {
		ArrayList<E> concatenation = new ArrayList<E>(this.size() + list.size());
		concatenation.addAll(this);
		concatenation.addAll(list);
		return concatenation;
	}

	
	public static void main(String[] args) {
		LinkedList<Integer> theList = new LinkedList<Integer>();
		
		theList.add(1); theList.add(3);
		theList.add(5); theList.add(7);
		theList.add(9); theList.add(11);
		theList.add(13); theList.add(15);

		System.out.println("LISTA ORIGINAL: " + theList + ", tamanho " + theList.size());
		
		ListView<Integer> view1 = new ListView<Integer>(theList, 0, 2);  //indices 0 a 2
		ListView<Integer> view2 = new ListView<Integer>(theList, 4, 4);  //indice 4 apenas
		ListView<Integer> view3 = new ListView<Integer>(theList, 4);     //do indice 4 em diante
		
		System.out.println("VIEW_1: " + view1 + ", tamanho " + view1.size());
		System.out.println("VIEW_2: " + view2 + ", tamanho " + view2.size());
		System.out.println("VIEW_3: " + view3 + ", tamanho " + view3.size());
		System.out.println();
		
		System.out.println("VIEW_1 + VIEW_3: " + view1.createConcatenation(view3));
		System.out.println("VIEW_2 + VIEW_1: " + view2.createConcatenation(view1));
		System.out.println();
		
		ListView<Integer> view4 = new ListView<Integer>(theList, 4, true);  //do indice 4 para tras
		ListView<Integer> view5 = new ListView<Integer>(theList, 4, 0);     //do indice 4 para tras
		ListView<Integer> view6 = new ListView<Integer>(theList, 6, 4);     //indices 6, 5 e 4 (nesta ordem)
		
		System.out.println("VIEW_4: " + view4 + ", tamanho " + view4.size());
		System.out.println("VIEW_5: " + view5 + ", tamanho " + view5.size());
		System.out.println("VIEW_6: " + view6 + ", tamanho " + view6.size());
		System.out.println();
		
		System.out.println("VIEW_3 + VIEW_6: " + view3.createConcatenation(view6));
	}

}
