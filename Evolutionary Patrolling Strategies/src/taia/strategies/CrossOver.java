package taia.strategies;

import taia.AgentMATP;
import taia.SimpleIndividual;
import taia.util.ListUtil;

public class CrossOver {

	private CrossOverType type = CrossOverType.SIMPLE_RANDOM_CROSSOVER;
	
	
	
	
	private SimpleIndividual[] simpleRandomCrossOver(SimpleIndividual i1, SimpleIndividual i2, Integer agent){
		
		
		SimpleIndividual[] out = new SimpleIndividual[2];
		
		AgentMATP ag1 = i1.getAgentMATP(agent);
		AgentMATP ag2 = i2.getAgentMATP(agent);
		
		out[0] = i1.copy();
		out[1] = i2.copy();
		
		out[0].setgetAgentMATP(agent, ag2);
		out[1].setgetAgentMATP(agent, ag1);
		
		
		return out;
		
	}
	
	public SimpleIndividual[] crossOver(SimpleIndividual i1, SimpleIndividual i2){
		switch(this.type){
		case SIMPLE_RANDOM_CROSSOVER:
			Integer n = ListUtil.chooseAtRandom(i1.getAgentsCentralNodesList());
			return simpleRandomCrossOver(i1, i2, n);
		default:
			break;
		
		}
		return null;
	}

	public void setType(CrossOverType type) {
		this.type = type;
	}
	
	

}
