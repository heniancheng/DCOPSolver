package com.cqu.sa;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cqu.core.Infinity;
import com.cqu.core.Message;
import com.cqu.core.ResultCycle;
import com.cqu.cyclequeue.AgentCycle;
import com.cqu.dsa.DsaA_Agent;
import com.cqu.main.Debugger;
import com.cqu.mgm.MgmAgent;
import com.cqu.settings.Settings;

public class SAAgent extends AgentCycle{
	
	public final static int TYPE_VALUE_MESSAGE=1;
	private static int cycleCountEnd;
	private final static double T=800;
	private final static double Tmin=20;
	private final static double r=0.5;
	private double t = T;
	
	public final static String KEY_LOCALCOST="KEY_LOCALCOST";
	public final static String KEY_NCCC="KEY_NCCC";
	
	private int nccc = 0;
	private int selectValueIndex;
	private int receivedQuantity;
	private int cycleCount;
	private int neighboursQuantity;	
	private int neighboursGain[];
	private HashMap<Integer, Integer> neighboursValueIndex;			//<neighbour 鐨� Index, neighbourValue 鐨�  Index>
	
	public SAAgent(int id, String name, int level, int[] domain) {
		super(id, name, level, domain);
		// TODO Auto-generated constructor stub
	}
	
	protected void initRun() {
		super.initRun();
		
		cycleCountEnd = Settings.settings.getCycleCountEnd();
		
		localCost=2147483647;
		valueIndex=(int)(Math.random()*(domain.length));
		selectValueIndex=0;
		receivedQuantity=0;
		cycleCount=0;
		neighboursValueIndex=new HashMap<Integer, Integer>();
		neighboursQuantity=neighbours.length;
		neighboursGain=new int[neighboursQuantity];
		for(int i=0; i<neighbours.length; i++)
			neighboursValueIndex.put((Integer)i, (Integer)0);
		sendValueMessages();
	}
	
	private void sendValueMessages(){
		for(int neighbourIndex=0; neighbourIndex<neighboursQuantity; neighbourIndex++){
			Message msg=new Message(this.id, neighbours[neighbourIndex], DsaA_Agent.TYPE_VALUE_MESSAGE, valueIndex);
			this.sendMessage(msg);
		}
	}
	
	protected void disposeMessage(Message msg){
		if (receivedQuantity==0)
			cycleCount++;
		receivedQuantity=(receivedQuantity+1)%neighboursQuantity;
		
		int senderIndex=0;
		int senderId=msg.getIdSender();
		
		for (int neighbourindex=0; neighbourindex<neighbours.length;neighbourindex++){
			if(senderId == neighbours[neighbourindex]){
				senderIndex = neighbourindex;
				break;
			}
		}
		
		neighboursValueIndex.put((Integer)senderIndex, (Integer)msg.getValue());
		
        if (receivedQuantity == 0){
        	if (t >Tmin){
        		
                localCost = localCost();
				
				int saved_localCost = 0;
				for (int neighbourindex=1;neighbourindex<neighboursQuantity;neighbourindex++){
//					if(this.id < neighbours[neighbourindex])      
						saved_localCost+=constraintCosts.get(neighbours[neighbourindex])[selectValueIndex][neighboursValueIndex.get(neighbourindex)];		
//					else
//						saved_localCost+=constraintCosts.get(neighbours[neighbourindex])[neighboursValueIndex.get(neighbourindex)][selectValueIndex];	
				}
				
				int randomValueIndex = (int)(Math.random()*(domain.length));
				int newLocalCost = 0;
				
				for (int neighbourindex=1;neighbourindex<neighboursQuantity;neighbourindex++){
//					if(this.id < neighbours[neighbourindex])      
						newLocalCost+=constraintCosts.get(neighbours[neighbourindex])[randomValueIndex][neighboursValueIndex.get(neighbourindex)];		
//					else
//						newLocalCost+=constraintCosts.get(neighbours[neighbourindex])[neighboursValueIndex.get(neighbourindex)][randomValueIndex];	
				}
				
				if (newLocalCost < localCost){
					selectValueIndex = randomValueIndex;
					valueIndex = selectValueIndex;
				}else if (newLocalCost > localCost){
					if(Math.random() < Math.exp(-(newLocalCost - localCost)/t)){
						selectValueIndex = randomValueIndex;
						valueIndex = selectValueIndex;
					}
				}
				
				t=t*r;
				sendValueMessages();	
				
        	}else{
        		stopRunning();
        	}
        }

	}
	
	private int localCost(){
		int localCostTemp=0;
		for(int i=0; i<neighboursQuantity; i++){
//			if(this.id < neighbours[i])
				localCostTemp+=constraintCosts.get(neighbours[i])[valueIndex][neighboursValueIndex.get(i)];		
//			else
//				localCostTemp+=constraintCosts.get(neighbours[i])[neighboursValueIndex.get(i)][valueIndex];	
		}
		return localCostTemp;
	}
	
	protected void runFinished(){
		super.runFinished();
		
		HashMap<String, Object> result=new HashMap<String, Object>();
		result.put(KEY_ID, this.id);
		result.put(KEY_NAME, this.name);
		result.put(KEY_VALUE, this.domain[valueIndex]);
		result.put(KEY_LOCALCOST, this.localCost);
		result.put(KEY_NCCC, this.nccc);
		
		this.msgMailer.setResult(result);
		System.out.println("Agent "+this.name+" stopped!");
	}
	
	@Override
	public Object printResults(List<Map<String, Object>> results) {
		// TODO Auto-generated method stub
		double totalCost=0;
		int ncccTemp = 0;
		for(Map<String, Object> result : results){
			
			int id_=(Integer)result.get(KEY_ID);
			String name_=(String)result.get(KEY_NAME);
			int value_=(Integer)result.get(KEY_VALUE);
			
			if(ncccTemp < (Integer)result.get(KEY_NCCC))
				ncccTemp = (Integer)result.get(KEY_NCCC);
			totalCost+=((double)((Integer)result.get(KEY_LOCALCOST)))/2;
			
			String displayStr="Agent "+name_+": id="+id_+" value="+value_;
			System.out.println(displayStr);
		}
		
		System.out.println("totalCost: "+Infinity.infinityEasy((int)totalCost)+
				" nccc: "+Infinity.infinityEasy((int)ncccTemp));
		
		ResultCycle ret=new ResultCycle();
		ret.nccc=(int)ncccTemp;
		ret.totalCost=(int)totalCost;
		return ret;
	}

	private void increaseNccc(){
		nccc++;
	}
	
	@Override
	public String easyMessageContent(Message msg, AgentCycle sender, AgentCycle receiver) {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
		return "from "+sender.getName()+" to "+receiver.getName()+" type "+MgmAgent.messageContent(msg);
	}
	
	public static String messageContent(Message msg){
		switch (msg.getType()) {
		case MgmAgent.TYPE_VALUE_MESSAGE :
			int val=(Integer) msg.getValue();
			int valueIndex=val;
			return "value["+valueIndex+"]";
		case MgmAgent.TYPE_GAIN_MESSAGE :
			int gainValue=(Integer) msg.getValue();
			return "gain["+gainValue+"]";
		default:
			return "unknown";
		}
	}
	
	@Override
	protected void messageLost(Message msg) {
		// TODO 鑷姩鐢熸垚鐨勬柟娉曞瓨鏍�
		if(Debugger.debugOn==true)
		{
			System.out.println(Thread.currentThread().getName()+": message lost in agent "+
					this.name+" "+this.msgMailer.easyMessageContent(msg));
		}
	}
}
