package taia.strategies;

import taia.SimpleIndividual;
import taia.util.ListUtil;
import yaps.util.RandomUtil;

public class Mutate {

	private MutateType type = MutateType.HALFADD_HALFSUB_REBUILD_IMPROVE;
	private int numberOfInprovesAfterChange = 5;
	
	
	public void setMUtateType(MutateType type){
		this.type = type;
	}
	
	private void halfAdd_halfSub_Rebuild_Improve(SimpleIndividual ind, Integer agent){
		
		if(RandomUtil.chooseBoolean()){
			ind.addRandomNodeAndRebuilOnAgent(agent);
		}else{
			ind.removeRandomNodeAndRebuildOnAgent(agent);
		}
		
		int i = numberOfInprovesAfterChange;
		
		while(i-- > 0){
			ind.improveAgentByTwoChange(agent);
		}
		
		
	}
	
	
	public void mutate(SimpleIndividual ind){
		switch(this.type){
		case ALWAYS_ADD_A_NODE_AND_REBUILD:
			break;
		case HALFADD_HALFSUB_REBUILD:
			break;
		case HALFADD_HALFSUB_REBUILD_IMPROVE:
			Integer n = ListUtil.chooseAtRandom(ind.getAgentsCentralNodesList());
			this.halfAdd_halfSub_Rebuild_Improve(ind, n);
			break;
		default:
			break;
		
		
		}
	}
	
	

}
