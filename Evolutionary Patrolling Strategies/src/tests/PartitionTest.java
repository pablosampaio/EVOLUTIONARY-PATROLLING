package tests;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import taia.PreCalculedPathGraph;
import taia.strategies.GraphEquipartition;
import yaps.graph_library.GraphReader;
import yaps.graph_library.InducedSubGraph;
import yaps.graph_library.Path;
import yaps.graph_library.PathBuilder;

public class PartitionTest {

	public static void main(String[] args) throws IOException {
		PreCalculedPathGraph g = new PreCalculedPathGraph(GraphReader.readAdjacencyList("./maps/island11"));
			
		List<Integer> centerList = GraphEquipartition.maximumDistance(4, g);

		HashMap<Integer, HashSet<Integer>> map = GraphEquipartition.fungalColonyPartition(centerList, g, true);
		
		HashMap<Integer, InducedSubGraph> graphMap = 
				new HashMap<Integer, InducedSubGraph>();
		
		HashMap<Integer, Path> pathMap = new HashMap<Integer, Path>();
		
		for(Integer c: centerList){
			graphMap.put(c, new InducedSubGraph(new ArrayList<Integer>(map.get(c)), g));
			pathMap.put(c, PathBuilder.nearestInsertionMethod(graphMap.get(c)));
		}
		
		//int j =0;
		
	}

}
