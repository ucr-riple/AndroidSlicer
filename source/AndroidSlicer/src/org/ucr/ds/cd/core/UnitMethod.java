package org.ucr.ds.cd.core;

import soot.SootMethod;
import soot.Unit;

public class UnitMethod {
	
	private Unit u;
	private SootMethod sm;
	private int lineNo;
	
	public Unit getUnit(){
		return u;
	}
	
	public int getLineNo()
	{
		return lineNo;
	}
	
	public void setLineNo(int n)
	{
		lineNo = n; 
	}
	
	public SootMethod getMethod(){
		return sm;
	}
	
	public void setUnit( Unit unit){
		this.u=unit;
	}
	
	public void setMethod(SootMethod method){
		this.sm=method;
	}
}
