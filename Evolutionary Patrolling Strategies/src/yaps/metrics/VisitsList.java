package yaps.metrics;

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

	public VisitsList(List<Visit> visits) {
		
		//Diogo
		//Check test for empty lists.
		//========================================================//
		this.visitList = new LinkedList<Visit>();
		this.lastTime = 0;	
		
		if(visits.isEmpty()){
			return;
		}
		
		for(Visit v: visits){
			this.addVisit(v);
		}
		
		//========================================================//
		
		//this.visitList = new ArrayList<Visit>(visits);	
		//this.lastTime = visits.get(visits.size() - 1).time;
	}
	

	
	
	public void addVisit(Visit visit) {
		if (visit.time < this.lastTime) {
			//Diogo
			//Ordered insert rather than throws a error.
			//===================================================//
			//Ordered insert!
			this.orderedAddVisit(visit);
			return;
			//====================================================//
			//throw new IllegalArgumentException("Visit inserted in wrong order!");
		}
		visitList.add(visit);
		this.lastTime = visit.time;
	}
	
	public void addVisit(int time, int agent, int node) {
		addVisit(new Visit(time, agent, node));
	}

	
	public void addVisit(int time, int node) {
		//visitList.add(new Visit(time, -1, node));
		
		//Diogo
		//Correct behavior on insert.
		//==========================================================
		addVisit(new Visit(time, node, -1));
		//=========================================================
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

	
	//Diogo
	//ordered insert
	//============================================================//    	            		
	private void orderedAddVisit(Visit v){
		
		if(visitList.size() == 0){
			this.visitList.add(v);
			this.lastTime = v.time;
			return;
		}
		
		//this.lastTime = (this.lastTime > v.time ? this.lastTime : v.time);
		
		
		int left = 0;
		int right = this.visitList.size();
		int midle = (left + right)/2;
		
		Visit u = this.visitList.get(midle);
		
		while(left < right - 1){
			if(v.time > u.time){
				left = midle;
			}else{
				right = midle;
			}
			
			midle = (left + right)/2;
			u = this.visitList.get(midle);
		}
		
		if(v.time > u.time){
			this.visitList.add(midle + 1, v);
		}else{
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
	//=======================================================//
	
	//Diogo
	//add other list to the visit list
	//===========================================================================//
	public void addVisitList(List<Visit> vList) {
		this.addVisitList(new VisitsList(vList));
	}
	//=============================================================================//
	
	//Diogo
	//add other list to the visit list
	//===========================================================================//
	public void addVisitList(VisitsList other) {

		this.lastTime = (this.lastTime > other.lastTime ? this.lastTime : other.lastTime);
		
		int i = 0, j = 0, newSize = this.visitList.size() + other.visitList.size();
		Visit u, v;
		
		LinkedList<Visit> newList = new LinkedList<Visit>();
		
		while((i + j) < newSize){
			
			if(i == this.visitList.size()){
				while(j < other.visitList.size()){
					newList.add(other.visitList.get(j));
					j++;
				}
				
				
				break;
			}
			
			if(j == other.visitList.size()){
				while(i < this.visitList.size()){
					newList.add(this.visitList.get(j));
					i++;
				}
				break;
			}			
			
			u = this.visitList.get(i);
			v = other.visitList.get(j);
			
			if(u.time < v.time){
				newList.add(u);
				i++;
			}else{
				newList.add(v);
				j++;
			}
			
			
		}
		
		this.visitList = newList;
		
		
		
	}
	//=============================================================================//
	
}
