package com.cqu.core;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cqu.adopt.AdoptAgent;
import com.cqu.agiledpop.AgileDPOPAgent;
import com.cqu.bfsdpop.BFSDPOPAgent;
import com.cqu.dpop.DPOPAgent;
import com.cqu.hybriddpop.HybridDPOP;
import com.cqu.hybriddpop.LabelPhaseHybridDPOP;
import com.cqu.hybridmbdpop.HybridMBDPOP;
import com.cqu.hybridmbdpop.LabelPhase;
import com.cqu.hybridmbdpop.LabelPhaseHybridMBDPOP;
import com.cqu.maxsum.MaxSumADVPAgent;
import com.cqu.parser.Problem;
import com.cqu.util.CollectionUtil;

public class AgentManager {
	
	public static final String[] AGENT_TYPES=new String[]{"DPOP", "BFSDPOP", "HybridDPOP", "HybridMBDPOP", "AgileDPOP", "ADOPT", "BNBADOPT", "ADOPT_K", "BDADOPT",
		"SynAdopt", "SynAdopt2", "DSA_A", "DSA_B", "DSA_C", "DSA_D", "DSA_E", "MGM", "MGM2", "ALSDSA", "ALSMGM", "ALSMGM2", "ALS_DSA", "ALS_H1_DSA", "ALS_H2_DSA", 
		"ALSLMUSDSA4", "ALSMLUDSA", "ALSDSAMGM", "ALSDSAMGMEVO", "ALSDSALUC", "MAXSUM", "MAXSUM_ADVP", "ACO", "ACO_tree", "ACO_bf", "ACO_phase", "ACO_line", "ACO_final"};
	
	private Map<Integer, Agent> agents;
	private int treeHeight=0;
	
	public AgentManager(Problem problem, String agentType) {
		// TODO Auto-generated constructor stub
		LabelPhase labelPhase=null;
		if(agentType.equals("HybridMBDPOP"))
		{
			labelPhase=new LabelPhaseHybridMBDPOP(problem);
			labelPhase.label();
		}else if(agentType.equals("HybridDPOP"))
		{
			labelPhase=new LabelPhaseHybridDPOP(problem);
			labelPhase.label();
		}
		agents=new HashMap<Integer, Agent>();
		for(Integer agentId : problem.agentNames.keySet())
		{
			Agent agent=null;
			if(agentType.equals("DPOP"))
			{
				agent=new DPOPAgent(agentId, problem.agentNames.get(agentId), problem.agentLevels.get(agentId), 
						problem.domains.get(problem.agentDomains.get(agentId)));
			}else if(agentType.equals("HybridMBDPOP"))
			{
				agent=new HybridMBDPOP(agentId, problem.agentNames.get(agentId), problem.agentLevels.get(agentId), 
						problem.domains.get(problem.agentDomains.get(agentId)), labelPhase.getIsSearchingPolicyAgents().get(agentId), 
						labelPhase.getIsNeighborSearchingPolicyAgents().get(agentId), labelPhase.getContext());
			}else if(agentType.equals("HybridDPOP"))
			{
				agent=new HybridDPOP(agentId, problem.agentNames.get(agentId), problem.agentLevels.get(agentId), 
						problem.domains.get(problem.agentDomains.get(agentId)), labelPhase.getIsSearchingPolicyAgents().get(agentId), 
						labelPhase.getIsNeighborSearchingPolicyAgents().get(agentId), labelPhase.getContext());
			}else if(agentType.equals("AgileDPOP"))
			{
				agent=new AgileDPOPAgent(agentId, problem.agentNames.get(agentId), problem.agentLevels.get(agentId), 
						problem.domains.get(problem.agentDomains.get(agentId)));
			}else if(agentType.equals("BFSDPOP"))
			{
				agent=new BFSDPOPAgent(agentId, problem.agentNames.get(agentId), problem.agentLevels.get(agentId), 
						problem.domains.get(problem.agentDomains.get(agentId)), problem.crossConstraintAllocation.get(agentId));
			}else if (agentType.equals("MAXSUM_ADVP")) {
				agent=new MaxSumADVPAgent(agentId, problem.agentNames.get(agentId), problem.agentLevels.get(agentId), problem.domains.get(problem.agentDomains.get(agentId)));
			}else
			{
				agent=new AdoptAgent(agentId, problem.agentNames.get(agentId), problem.agentLevels.get(agentId), 
						problem.domains.get(problem.agentDomains.get(agentId)));
			}
			Map<Integer, int[]> neighbourDomains=new HashMap<Integer, int[]>();
			Map<Integer, int[][]> constraintCosts=new HashMap<Integer, int[][]>();
			int[] neighbourAgentIds=problem.neighbourAgents.get(agentId);
			Map<Integer, Integer> neighbourLevels=new HashMap<Integer, Integer>();
			for(int i=0;i<neighbourAgentIds.length;i++)
			{
				neighbourDomains.put(neighbourAgentIds[i], problem.domains.get(problem.agentDomains.get(neighbourAgentIds[i])));
				neighbourLevels.put(neighbourAgentIds[i], problem.agentLevels.get(neighbourAgentIds[i]));
			}
			String[] neighbourAgentCostNames=problem.agentConstraintCosts.get(agentId);
			for(int i=0;i<neighbourAgentCostNames.length;i++)
			{
				if(agentId < neighbourAgentIds[i]){
					constraintCosts.put(neighbourAgentIds[i], 
							CollectionUtil.toTwoDimension(problem.costs.get(neighbourAgentCostNames[i]), 
									problem.domains.get(problem.agentDomains.get(agentId)).length, 
									problem.domains.get(problem.agentDomains.get(neighbourAgentIds[i])).length));
				}else{
					int[][] temp = CollectionUtil.toTwoDimension(problem.costs.get(neighbourAgentCostNames[i]), 
							problem.domains.get(problem.agentDomains.get(neighbourAgentIds[i])).length, 
							problem.domains.get(problem.agentDomains.get(agentId)).length);
					
					constraintCosts.put(neighbourAgentIds[i], CollectionUtil.reverse(temp));
				}
				
			}
			
			agent.setNeibours(problem.neighbourAgents.get(agentId), problem.parentAgents.get(agentId), 
					problem.childAgents.get(agentId), problem.allParentAgents.get(agentId), 
					problem.allChildrenAgents.get(agentId), neighbourDomains, constraintCosts, neighbourLevels);
			
			agents.put(agent.getId(), agent);
			
			//get tree height
			if(treeHeight<problem.agentLevels.get(agentId))
			{
				treeHeight=problem.agentLevels.get(agentId);
			}
			
			/*{
				String str="-----------"+agent.name+"-----------\n";
				str+="Parent: "+agent.parent+"\n";
				str+="Children: "+CollectionUtil.arrayToString(agent.children)+"\n";
				str+="AllParents: "+CollectionUtil.arrayToString(agent.allParents)+"\n";
				str+="AllChildren: "+CollectionUtil.arrayToString(agent.allChildren)+"\n";
				FileUtil.writeStringAppend(str, "dfsTree.txt");
			}*/
		}
		treeHeight++;
	}
	
	public int getTreeHeight()
	{
		return this.treeHeight;
	}
	
	public Map<Integer, Integer> getAgentValues()
	{
		Map<Integer, Integer> agentValues=new HashMap<Integer, Integer>();
		for(Agent agent : agents.values())
		{
			agentValues.put(agent.getId(), agent.getValue());
		}
		return agentValues;
	}
	
	public Agent getAgent(int agentId)
	{
		if(agents.containsKey(agentId))
		{
			return agents.get(agentId);
		}else
		{
			return null;
		}
	}
	
	public int getAgentCount()
	{
		return this.agents.size();
	}
	
	public void startAgents(MessageMailer msgMailer)
	{
		for(Agent agent : agents.values())
		{
			agent.setMessageMailer(msgMailer);
			agent.startProcess();
		}
	}
	
	public void stopAgents()
	{
		for(Agent agent : agents.values())
		{
			agent.stopRunning();
		}
	}
	
	public Object printResults(List<Map<String, Object>> results)
	{
		if(results.size()>0)
		{
			Agent agent=null;
			for(Integer agentId : this.agents.keySet())
			{
				agent=this.agents.get(agentId);
				break;
			}
			return agent.printResults(results);
		}
		return null;
	}
	
	public String easyMessageContent(Message msg)
	{
		Agent senderAgent=this.getAgent(msg.getIdSender());
		Agent receiverAgent=this.getAgent(msg.getIdReceiver());
		return senderAgent.easyMessageContent(msg, senderAgent, receiverAgent);
	}
}
