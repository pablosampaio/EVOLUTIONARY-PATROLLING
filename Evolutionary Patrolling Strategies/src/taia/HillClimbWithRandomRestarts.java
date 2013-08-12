package taia;

import java.io.IOException;

import yaps.graph_library.Graph;
import yaps.graph_library.GraphDataRepr;
import yaps.graph_library.GraphReader;
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
	
	public SimpleIndividual climbHillWithRandomRestarts(int numberOfIterations, int[] distributionOfTimeIntervals) {
		
		//S <- Some initial random candidate solution
		SimpleIndividual s = this.getIndividualBuilder().buildNewRandomIndividual();
		
		//We have successfully generated a initial random candidate solution
		
		SimpleIndividual best = s; //Best <- S
		
		
		double qualityOfBest = this.getMetricFacility().assessFitness(s);
		
	
		
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
			
			//S <- Some initial random candidate solution
			s = this.getIndividualBuilder().buildNewRandomIndividual();
			
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
		
		randomRestarts.getIndividualBuilder().setNumAgents(3);
		randomRestarts.getIndividualBuilder().setUpBuilder();
		
		SimpleIndividual finalSolution = randomRestarts.climbHillWithRandomRestarts(15, distribution);
		
		System.out.println("Random Restarts: Final configuration:\n"+finalSolution+"\n================================\n");

	}

}
