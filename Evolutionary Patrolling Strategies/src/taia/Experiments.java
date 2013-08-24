package taia;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import taia.strategies.MutateType;
import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;

public class Experiments {
	
	
	public static String[] graphsNames = {
		
		"./maps/island11",
		"./maps/complete15",
		"./maps/map_a.adj",
		"./maps/map_cicles_corridor.adj",
		"./maps/map_grid.adj",
		"./maps/map_islands.adj",
		
	};
	
	public static MutateType[] mutateTypes= {
		
		
	};
	
	public static List<PreCalculedPathGraph> generateGraphList(){
		
		ArrayList<PreCalculedPathGraph> pList = new ArrayList<PreCalculedPathGraph>();
		
		for(String name: graphsNames){
			
			try {
				Graph g = GraphReader.readAdjacencyList(name, GraphDataRepr.LISTS);
				
				pList.add(new PreCalculedPathGraph(g));
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		}
		
		
		return pList;
	}
	
	
	
	public static void main(String[] args) {
		
		
		List<PreCalculedPathGraph> graphsList = generateGraphList();
		
		
		for(PreCalculedPathGraph graph: graphsList){
			System.out.println(graph);
		}
		
		
	}

}
