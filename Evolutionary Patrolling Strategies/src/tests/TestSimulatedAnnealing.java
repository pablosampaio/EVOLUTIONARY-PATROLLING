package tests;

import java.io.IOException;
import java.util.ArrayList;

import taia.PreCalculedPathGraph;

import yaps.graph_library.Graph;
import yaps.graph_library.GraphReader;
import yaps.graph_library.InducedSubGraph;
import yaps.graph_library.Path;
import yaps.graph_library.PathBuilder;
import yaps.metrics.IntervalMetricsReport;
import yaps.util.RandomUtil;

public class TestSimulatedAnnealing extends TestHillClimb {
	
	private static final int simulationTime = 10000;
	
	private static double temperature = 0.5;
	
	private static final double temperatureDecreaseRate = 0.1;

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		PreCalculedPathGraph g = new PreCalculedPathGraph(GraphReader.readAdjacencyList("./maps/island11"));
		
		ArrayList<Integer> nodes = new ArrayList<Integer>();

		for(int k = 0; k < g.getNumNodes(); k++){
			nodes.add(k);
		}
		
		InducedSubGraph sg = new InducedSubGraph(nodes, g);

		Path s = PathBuilder.nearestInsertionMethod(sg);
		
		Path best = s;

		System.out.println("Cost:  " + quality(g, best));

		for(int i = 0; i < 20000; i++){

			Path r = PathBuilder.twoChange(
					RandomUtil.chooseInteger(0, s.size() - 3), 
					RandomUtil.chooseInteger(0, s.size() - 3), 
					s,  sg);

			if(
					(quality(g, r) > quality(g, s) ||
					( ((double)(RandomUtil.chooseInteger(0, 100)) / 100) < Math.pow(Math.E, (quality(g, s) - quality(g, s)))/temperature)) 
					){
				s = r;
			}
			
			temperature -= temperatureDecreaseRate;
			
			if(quality(g, s) > quality(g, best)) {
				best = s;
			}
			
			if(temperature <= 0.0) {
				break;
			}
		}

		System.out.println("Cost:  " + quality(g, best));

	}
	
	
	public static double quality(Graph g, Path p) {
		
		return (new IntervalMetricsReport(g.getNumEdges(), 1, simulationTime, getVisitTimes(p, g, simulationTime))).getAverageInterval();
		
	}

}
