package com.cqu.core;

import java.util.HashMap;

public class ResultCycle extends Result{
	
	public double[] totalCostInCycle;			//新增参数，用于记录每一个回合的totalCost，来描述动态变化
	public double nccc;
		
	public ResultCycle() {
		// TODO Auto-generated constructor stub
		super();
		this.totalCostInCycle = null;
	}
	
	public ResultCycle(Result rs)
	{
		super(rs);
		this.totalCostInCycle = ((ResultCycle)rs).totalCostInCycle;
		this.nccc=((ResultCycle)rs).nccc;
	}
	
	public void min(Result rs)
	{
		this.messageQuantity=Math.min(this.messageQuantity, rs.messageQuantity);
		this.lostRatio=Math.min(this.lostRatio, rs.lostRatio);
		this.totalTime=Math.min(this.totalTime, rs.totalTime);
		if(this.totalCost > rs.totalCost){
			this.totalCost = rs.totalCost;
			this.totalCostInCycle = ((ResultCycle)rs).totalCostInCycle;
		}
		this.nccc=Math.min(this.nccc, ((ResultCycle)rs).nccc);
	}
	
	public void max(Result rs)
	{
		this.messageQuantity=Math.max(this.messageQuantity, rs.messageQuantity);
		this.lostRatio=Math.max(this.lostRatio, rs.lostRatio);
		this.totalTime=Math.max(this.totalTime, rs.totalTime);
		if(this.totalCost <= rs.totalCost){
			this.totalCost = rs.totalCost;
			this.totalCostInCycle = ((ResultCycle)rs).totalCostInCycle;
		}
		this.nccc=Math.max(this.nccc, ((ResultCycle)rs).nccc);
	}
	
	public void add(Result rs, int validCount)
	{
		super.add(rs, validCount);
		if(totalCostInCycle == null){
			totalCostInCycle = new double[((ResultCycle)rs).totalCostInCycle.length];
			for(int i = 0; i < totalCostInCycle.length; i++)
				this.totalCostInCycle[i] = 0;
		}
		for(int i = 0; i < totalCostInCycle.length; i++)
			this.totalCostInCycle[i] += (((ResultCycle)rs).totalCostInCycle[i]/validCount);
		this.nccc+=((ResultCycle)rs).nccc/validCount;
	}
	
	public void minus(Result rs, int validCount)
	{
		super.minus(rs, validCount);
		if(totalCostInCycle == null){
			totalCostInCycle = new double[((ResultCycle)rs).totalCostInCycle.length];
			for(int i = 0; i < totalCostInCycle.length; i++)
				this.totalCostInCycle[i] = 0;
		}
		for(int i = 0; i < totalCostInCycle.length; i++)
			this.totalCostInCycle[i] -= (((ResultCycle)rs).totalCostInCycle[i]/validCount);
		this.nccc-=((ResultCycle)rs).nccc/validCount;
	}
}
