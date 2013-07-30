package tests;

import java.io.IOException;
import java.util.ArrayList;

import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;
import yaps.graph_library.InducedSubGraph;
import yaps.graph_library.Path;
import yaps.graph_library.PathBuilder;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.util.RandomUtil;

public class TestHillClimb {


	private static final int simulationTime = 10000;



	public static void main(String[] args) throws IOException {

		Graph g = GraphReader.readAdjacencyList("./maps/island11");

		ArrayList<Integer> nodes = new ArrayList<Integer>();

		for(int k = 0; k < g.getNumNodes(); k++){
			nodes.add(k);
		}


		InducedSubGraph sg = new InducedSubGraph(nodes, g);

		Path p = PathBuilder.nearestInsertionMethod(sg);

		VisitsList v = getVisitTimes(p, g, simulationTime);
		IntervalMetricsReport intervalReport = new IntervalMetricsReport(g.getNumEdges(), 1, simulationTime, v);

		System.out.println("Cost:  " + intervalReport.getAverageInterval());

		for(int i = 0; i < 20000; i++){

			Path p2 = PathBuilder.twoChange(
					RandomUtil.chooseInteger(0, p.size() - 2), 
					RandomUtil.chooseInteger(0, p.size() - 2), 
					p,  sg);

			VisitsList v2 = getVisitTimes(p2, g, simulationTime);
			IntervalMetricsReport intervalReport2 = new IntervalMetricsReport(g.getNumNodes(), 1, simulationTime, v2);

			if(intervalReport.getAverageInterval() > intervalReport2.getAverageInterval()){
				p = p2;
				intervalReport = intervalReport2;
				System.out.println("Cost:  " + intervalReport.getAverageInterval());

			}
		}

		System.out.println("Cost:  " + intervalReport.getAverageInterval());
		
	}

	public static VisitsList getVisitTimes(Path p, Graph g, int time){

		VisitsList v = new VisitsList();

		g.changeRepresentation(GraphDataRepr.LISTS);
		int lastTime = 1;
		v.addVisit(lastTime, p.getFirst());

		for(int i = 1; i < time; i++){
			int lNode = p.get( (i -1) % (p.size()-1) );
			int cNode = p.get( (i) % (p.size()-1)  );
			lastTime += g.getLength(lNode, cNode);
			v.addVisit(lastTime, cNode);
		}

		return v;
	}
}
