package taia.strategies;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import taia.PreCalculedPathGraph;
import taia.util.ListUtil;
import yaps.graph_library.Path;
import yaps.graph_library.algorithms.AllPairsShortestPaths;
import yaps.util.RandomUtil;

public class GraphEquipartition {

	private static int MAX_NUM_INTERATIONS = 100;


	private static double sumDistancies(List<Integer> nis, AllPairsShortestPaths p){

		double distance = 0;


		for(int i = 0; i < nis.size() - 1; i++){
			for(int j = i + 1; j < nis.size(); j++){
				distance += p.getDistance(nis.get(i), nis.get(j));
			}
		}

		return distance;
	}

	@SuppressWarnings("unused")
	private static double sumDistancies(Integer[] nis, AllPairsShortestPaths p){

		double distance = 0;

		for(int i = 0; i < nis.length - 1; i++){
			for(int j = i + 1; j < nis.length; j++){
				distance += p.getDistance(nis[i], nis[j]);
			}
		}

		return distance;

	}
	/**
	 * Tries to build a array of numOfNodes nodes whose indices are approximately maximum distanced.
	 * 
	 * @param numOfNodes
	 * @param g
	 * @return
	 */
	public static List<Integer> maximumDistance(int numOfNodes, PreCalculedPathGraph g){
		return maximumDistance(numOfNodes, g, MAX_NUM_INTERATIONS);
	}

	/**
	 * Tries to build a array of numOfNodes nodes whose indices are approximately maximum distanced.
	 * 
	 * @param numOfNodes
	 * @param g
	 * @param numOfIterations
	 * @return
	 */
	public static List<Integer> maximumDistance(int numOfNodes, PreCalculedPathGraph g, int numOfIterations){

		AllPairsShortestPaths allPaths = g.getAllPaths();

		List<Integer> nodeArray = ListUtil.randomChoose(numOfNodes, g.getNodesList());

		while(numOfIterations-- > 0){

			int i = RandomUtil.chooseInteger(0, nodeArray.size() - 1);

			double dMin = sumDistancies(nodeArray, allPaths);

			Integer n = nodeArray.get(i), nBest;

			nBest = n;

			List<Integer> neigbors = g.getSuccessors(n);

			for(int j = 0; j < neigbors.size(); j++){


				Integer nj = neigbors.get(j);

				boolean contains = false;

				for(int k = 0; k < nodeArray.size(); k++){
					if(nodeArray.get(k) == nj){
						contains = true;
						break;
					}
				}

				if(contains){
					continue;
				}


				nodeArray.set(i, nj);

				double d = sumDistancies(nodeArray, allPaths);

				nodeArray.set(i, n);

				if(d > dMin){
					dMin = d;
					nBest = nj;
				}

			}

			nodeArray.set(i, nBest);

		}


		return nodeArray;
	}

	private static boolean includeNext(Integer cntr, Integer[] cntrList, List<Integer> ngborList){

		while(!ngborList.isEmpty()){

			Integer n = ngborList.remove(0);

			if(cntrList[n].equals(-1)){
				cntrList[n] = cntr;
				return true;
			}else{
				continue;
			}

		}

		return false;
	}


	public static HashMap<Integer, HashSet<Integer>> fungalColonyPartition(List<Integer> centers, PreCalculedPathGraph g, boolean addPath){

		Integer[] nodeColony = new Integer[g.getNumNodes()]; 

		for(Integer n: g.getNodesList()){
			if(centers.contains(n)){
				nodeColony[n] = n;
			}else{
				nodeColony[n] = -1;
			}
		}

		HashMap<Integer, HashSet<Integer>> cntrColony = new
				HashMap<Integer, HashSet<Integer>>();

		HashMap<Integer, List<Integer>> cntrNeigbrListMap = new
				HashMap<Integer, List<Integer>>();

		for(Integer c: centers){
			cntrNeigbrListMap.put(c, g.getOrderedDistanceList(c));
			cntrColony.put(c, new HashSet<Integer>());
		}

		boolean loop = true;

		while(loop){

			loop = false;

			for(Integer center: centers){
				List<Integer> cList = cntrNeigbrListMap.get(center);

				if(includeNext(center, nodeColony, cList)){
					loop = true;
					continue;
				}

				loop = loop || false;

			}
		}

		if(addPath){
			AllPairsShortestPaths allp = g.getAllPaths();

			for(Integer n: g.getNodesList()){

				Path p = allp.getPath(nodeColony[n], n);
				
				cntrColony.get(nodeColony[n]).addAll(p);
				
			}

		}else{

			for(Integer n: g.getNodesList()){
				cntrColony.get(nodeColony[n]).add(n);
			}

		}

		
		List<Integer> graphNodes = g.getNodesList();

		for(Integer c: centers){
			while(cntrColony.get(c).size() < 2){
				Integer destination = ListUtil.chooseAtRandom(graphNodes);
				Path path = g.getAllPaths().getPath(c, destination);
				cntrColony.get(c).addAll(path);
			}
		}
		
		return cntrColony;

	}


	
	public static HashMap<Integer, HashSet<Integer>>  naiveRandomEquipartition(List<Integer> centers, PreCalculedPathGraph g){

		HashMap<Integer, HashSet<Integer> > partition = new HashMap<Integer, HashSet<Integer>>(centers.size());
		HashSet<Integer> addedNodes = new HashSet<Integer>(g.getNumNodes());

		for(Integer c: centers){
			addedNodes.add(c);
			partition.put(c, new HashSet<Integer>());
			partition.get(c).add(c);
		}


		int k = 0;
		List<Integer> indexList = RandomUtil.shuffle(centers);
		List<Integer> graphNodes = g.getNodesList();

		while(addedNodes.size() < g.getNumNodes()){

			Integer source = indexList.get(k);
			k = (++k) % indexList.size();

			Integer destination = ListUtil.chooseAtRandom(graphNodes);

			while(source.equals(destination)){
				destination = ListUtil.chooseAtRandom(graphNodes);
			}

			Path path = g.getAllPaths().getPath(source, destination);

			addedNodes.addAll(path);
			partition.get(source).addAll(path);

		}

		for(Integer c: centers){
			while(partition.get(c).size() < 2){
				Integer destination = ListUtil.chooseAtRandom(graphNodes);
				Path path = g.getAllPaths().getPath(c, destination);
				partition.get(c).addAll(path);
			}
		}

		return partition; 

	}

	
	
} 






