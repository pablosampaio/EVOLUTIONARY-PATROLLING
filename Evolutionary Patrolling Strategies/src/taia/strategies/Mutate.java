package taia.strategies;

import taia.AgentMATP;
import taia.SimpleIndividual;
import taia.util.ListUtil;
import yaps.graph_library.PathBuilder;
import yaps.util.RandomUtil;

public class Mutate {

	private MutateType type = MutateType.HALFADD_HALFSUB_SMALLCHANGES;
	private int numberOfInprovesAfterChange = 5;
	private IndividualBuilderPathConstructorType rebuildType = IndividualBuilderPathConstructorType.NearestInsertionMethod;
	
	
	public Mutate(){}
	
	public Mutate(MutateType type){
		this.type = type;
	}
	
	public void setRebuildType( IndividualBuilderPathConstructorType rebuildType){
		this.rebuildType = rebuildType;
	}
	
	public void setMutateType(MutateType type){
		this.type = type;
	}
	
	public void setNumberOfImprovesTries(int num){
		this.numberOfInprovesAfterChange = num;
	}
	
	private void improve(SimpleIndividual ind, Integer agent){
		this.improve(ind, agent, this.numberOfInprovesAfterChange);
	}
	
	private void improve(SimpleIndividual ind, Integer agent, int numberImproves){
		while(numberImproves-- > 0){
			ind.improveAgentByTwoChange(agent);
		}
	}
	
	private void halfAdd_halfSub(SimpleIndividual ind, Integer agent){
		
		AgentMATP ag = ind.getAgentMATP(agent);
		
		switch(this.rebuildType){
		case NearestInsertionMethod:
			ag.setBuildMethod(PathBuilder.NearestInsertionMethod);
			break;
		case NearestNeighborMethod:
			ag.setBuildMethod(PathBuilder.NearestNeighborMethod);
			break;
		default:
			break;
		}
		
		
		if(RandomUtil.chooseBoolean()){
			ind.addRandomNodeAndRebuilOnAgent(agent);
		}else{
			ind.removeRandomNodeAndRebuildOnAgent(agent);
		}

	}
	
	private void halfAdd_halfSub_smallChanges(SimpleIndividual ind, Integer agent){
		
		if(RandomUtil.chooseBoolean()){
			ind.getAgentMATP(agent).addRandomNodeWithSmallChanges();
		}else{
			ind.getAgentMATP(agent).removeRandomNodeWithSmallChanges();
		}
		
	}
	
	public void mutate(SimpleIndividual ind){
		
		Integer n = ListUtil.chooseAtRandom(ind.getAgentsCentralNodesList());
		
		switch(this.type){
		case HALFADD_HALFSUB_SMALLCHANGES:
			
			this.halfAdd_halfSub_smallChanges(ind, n);
			
			break;
		case HALFADD_HALFSUB_SMALLCHANGES_IMPROVE:
			
			this.halfAdd_halfSub_smallChanges(ind, n);
			this.improve(ind, n);
			
			break;
		case HALFADD_HALFSUB_REBUILD:
			
			this.halfAdd_halfSub(ind, n);
			
			break;
		case HALFADD_HALFSUB_REBUILD_IMPROVE:
			
			
			this.halfAdd_halfSub(ind, n);
			this.improve(ind, n);
			
			break;
		default:
			this.type = MutateType.HALFADD_HALFSUB_SMALLCHANGES;
			this.mutate(ind);
			break;
		
		}
	}

	@Override
	public String toString() {
		return "Mutate [type=" + type + ", rebuildType=" + rebuildType + ", number of improves="
				+ numberOfInprovesAfterChange + "]";
	}


	
	

}
