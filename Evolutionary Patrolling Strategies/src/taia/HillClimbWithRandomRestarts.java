package taia;

import java.io.IOException;
import java.util.ArrayList;

import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;
import yaps.metrics.IntervalMetricsReport;
import yaps.metrics.VisitsList;
import yaps.util.RandomUtil;

public class HillClimbWithRandomRestarts extends HillClimb {
	
	//private int[] distributionOfTimeIntervals;

	public HillClimbWithRandomRestarts(Graph g) {
		
		super(g);
		//this.distributionOfTimeIntervals = null;
		
	}
	
	public HillClimbWithRandomRestarts(Graph g, int simulationTime) {
		
		super(g, simulationTime);
		//this.distributionOfTimeIntervals = null;
		
	}

	public HillClimbWithRandomRestarts(String graphFileName, int simulationTime)
			throws IOException {
		
		super(graphFileName, simulationTime);
		//this.distributionOfTimeIntervals = null;
		
	}

	public HillClimbWithRandomRestarts(String graphFileName)
			throws IOException {
		
		super(graphFileName);
		//this.distributionOfTimeIntervals = null;
		
	}

	/*public int[] getDistributionOfTimeIntervals() {
		
		if(this.distributionOfTimeIntervals == null){
			this.distributionOfTimeIntervals = new int[0];
		}
		return this.distributionOfTimeIntervals;
		
	}

	public void setDistributionOfTimeIntervals(int[] distributionOfTimeIntervals) {
		
		this.distributionOfTimeIntervals = distributionOfTimeIntervals;
		
	}*/
	
	public SimpleIndividual climbHillWithRandomRestarts(int numberOfAgents, int numberOfIterations, int[] distributionOfTimeIntervals) {
		
		//S <- Some initial random candidate solution
		
		ArrayList<Integer> agentList = new ArrayList<>();
		
		for(int i = 0; i < numberOfAgents; i++) {
			
			int agent = RandomUtil.chooseInteger(0, getNumOFNodes() -1);
			
			while(agentList.contains(agent)){
				agent = RandomUtil.chooseInteger(0, getNumOFNodes() -1);
			}
			
			agentList.add(agent);
			
		}
		
		SimpleIndividual s = new SimpleIndividual(agentList, getGraph());
		
		//We have successfully generated a initial random candidate solution
		
		SimpleIndividual best = s; //Best <- S
		
		VisitsList vs = best.generateVisitList(this.getSimulationTime());
		
		double qualityOfBest;
		
		IntervalMetricsReport intervalReport = new IntervalMetricsReport(getNumOFNodes(), 1, getSimulationTime(), vs);
		
		qualityOfBest = intervalReport.getAverageInterval();
		
		System.out.println("RandomRestarts: Initial configuration:  \n" + best+"\n");
		
		System.out.println("RandomRestarts: Initial metric value:  " + qualityOfBest+"\n");
		
		while(numberOfIterations-- > 0) {
			
			int time = distributionOfTimeIntervals[RandomUtil.chooseInteger(0, distributionOfTimeIntervals.length-1)];
			
			System.out.println("==========================================================\nHill Climb");
			
			s = this.doHillClimb(s, time);
			
			double qualityOfS = this.getBestMetric(); //Attention!!! "Best Metric" from the hill climb!!
			//Do not misunderstand!
			
			System.out.println("==========================================================");
			
			if(qualityOfS < qualityOfBest) {
				best = s;
				qualityOfBest = qualityOfS;
				
				System.out.println("RandomRestarts: new best metric value:  " + qualityOfBest+"\n");
				
			}
			
			//S <- Some random candidate solution
			
			agentList = new ArrayList<>();
			
			for(int i = 0; i < numberOfAgents; i++) {
				
				int agent = RandomUtil.chooseInteger(0, getNumOFNodes() -1);
				
				while(agentList.contains(agent)){
					agent = RandomUtil.chooseInteger(0, getNumOFNodes() -1);
				}
				
				agentList.add(agent);
				
			}
			
			s = new SimpleIndividual(agentList, getGraph());
			
			//We have successfully generated a new random solution candidate
			
		}
		
		return best;
		
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		
		Graph g = GraphReader.readAdjacencyList("./maps/island11");
		
		g.changeRepresentation(GraphDataRepr.LISTS);
		
		HillClimbWithRandomRestarts randomRestarts = new HillClimbWithRandomRestarts(g, 1000);
		
		int[] distribution = new int[10];
		
		for(int i = 0; i < 10; i++) {
			distribution[i] = 1000;
		}
		
		SimpleIndividual finalSolution = randomRestarts.climbHillWithRandomRestarts(3, 10, distribution);
		
		System.out.println("Random Restarts: Final configuration:\n"+finalSolution+"\n================================\n");

	}

}
