package taia;

import yaps.graph_library.Graph;
import yaps.graph_library.Path;
import yaps.metrics.VisitsList;

//PAS: (Secundario:) Uma classe só para uma função? Repensar...
//Veja o que fiz com a classe PathFunctions - coloquei tudo em Path e apaguei a classe.
public class ClosedPathFacility {

	
	public static VisitsList fromClosedPathToVisitList(Path p, Graph g, int time, int agent){

		VisitsList v = new VisitsList();

		int lastTime = 1;
		v.addVisit(lastTime, p.getFirst());

		for(int i = 1; i < time; i++){
			int lNode = p.get( (i -1) % (p.size()-1) );
			int cNode = p.get( (i) % (p.size()-1)  );
			lastTime += g.getLength(lNode, cNode);
			
			v.addVisit(lastTime, cNode, agent);
			//v.addVisit(lastTime, cNode);
		}

		return v;
	}
	
	public static VisitsList fromClosedPathToVisitList(Path p, Graph g, int time){
		return fromClosedPathToVisitList(p, g, time, -1);
	}


}
