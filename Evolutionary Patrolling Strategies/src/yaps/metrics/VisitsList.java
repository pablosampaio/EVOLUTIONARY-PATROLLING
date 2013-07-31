package yaps.metrics;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


/**
 * This class is a list of visits in a simulation. It is used to calculate metrics of 
 * the simulation (to measure how "efficiently" the nodes were visited). 
 * <br><br>
 * The visits must be inserted ordered by time.
 * 
 * @author Pablo A. Sampaio
 */
public class VisitsList {
	private List<Visit> visitList;
	private long lastTime;
		
	public VisitsList() {
		this.visitList = new LinkedList<Visit>();
		this.lastTime = 0;
	}

	//PAS: Arrumações de estilo. Vou fazer isso só nas classes do simulador, ok? ;)
	public VisitsList(List<Visit> visits) {
		this.visitList = new ArrayList<Visit>(visits.size());
		this.lastTime = 0;
		for(Visit v: visits){
			this.addVisit(v);
		}
	}
	
	public void addVisit(Visit visit) {
		if (visit.time < this.lastTime) {
			this.orderedAddVisit(visit);
		}
		visitList.add(visit);
		this.lastTime = visit.time;
	}
	
	//PAS: Solução do bug. 
	// Além disso, o agent passou para o último parâmetro porque é  
	// a informação menos importante.
	public void addVisit(int time, int node, int agent) {
		addVisit(new Visit(time, node, agent));
	}

	public void addVisit(int time, int node) {
		//Diogo
		//Correct behavior on insert.
		//PAS: Ok, vc corrigiu a mesma coisa neste método, só faltava o de cima...
		addVisit(new Visit(time, node, -1));
	}

	public int getNumVisits() {
		return visitList.size();
	}
	
	public Visit getVisit(int index) {
		return visitList.get(index);
	}

	public VisitsList filterByAgent(int agent) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : visitList) {
			if (visit.agent == agent) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}

	public VisitsList filterByVertex(int node) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : visitList) {
			if (visit.node == node) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}
	
	// inclusive limits (closed interval)
	public VisitsList filterByTime(int from, int to) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : visitList) {
			if (visit.time >= from && visit.time <= to) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}

	// parameter 'from' is an inclusive limit (closed interval)
	public VisitsList filterByTime(int from) {
		List<Visit> filteredVisits = new LinkedList<Visit>();
		
		for (Visit visit : visitList) {
			if (visit.time >= from) {
				filteredVisits.add(visit);
			}
		}
		
		return new VisitsList(filteredVisits);
	}

	//PAS: Arrumações de estilo. Removi código inatingível (a
	//menos que seja usado um tempo negativo). Na verdade, este
	//método nunca é chamado no projeto atual, mas vou manter...
	private void orderedAddVisit(Visit v){	
		int left = 0;
		int right = this.visitList.size();
		int midle = (left + right)/2;
		
		Visit u = this.visitList.get(midle);
		
		while (left < right - 1) {
			if (v.time > u.time) {
				left = midle;
			} else {
				right = midle;
			}
			
			midle = (left + right)/2;
			u = this.visitList.get(midle);
		}
		
		if (v.time > u.time) {
			this.visitList.add(midle + 1, v);
		} else {
			this.visitList.add(midle, v);
		}		
	}

	@Override
	public String toString() {		
		if(this.visitList.size() < 20){
			return "VisitsList : lastTime=" + lastTime + " visitList=" + visitList;
		}		
		return "VisitsList : lastTime=" + lastTime + " visitListSize=" + this.visitList.size();
	}

	public void addVisitList(VisitsList other) {
		List<Visit> newList = new ArrayList<Visit>(this.visitList.size() + other.visitList.size());
		Visit u, v;
		int i = 0, j = 0;
		
		while (i < this.visitList.size() && j < other.visitList.size()){
			u = this.visitList.get(i);
			v = other.visitList.get(j);
			
			if (u.time < v.time) {
				newList.add(u);
				i++;
			} else {
				newList.add(v);
				j++;
			}			
		}
		
		while (j < other.visitList.size()){
			newList.add(other.visitList.get(j));
			j++;
		}		
		while (i < this.visitList.size()){
			newList.add(this.visitList.get(i));
			i++;
		}

		this.lastTime = (this.lastTime > other.lastTime ? this.lastTime : other.lastTime);
		this.visitList = newList;		
	}
	
}
