package org.ucr.ds.cd.core;

import soot.SootMethod;
import soot.Unit;

public class InstructionUnits {
	
	private Unit u;
	private SootMethod sm;
	private int lineNo = -1;
	//private int sootUnitPosition;
	private String sootUnitId;
	
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
	
	public InstructionUnits ()
	{
		sootUnitId = "";
	}
	
	public void setSootUnitId()
	{
		sootUnitId = ""+lineNo+"ZZZ"+sm.getName()+":"+sm.getDeclaringClass().getName()+":"+u.toString();
	}
	
	public InstructionUnits (SootMethod sm)
	{
		sootUnitId = ""+lineNo+"ZZZ"+sm.getName()+":"+sm.getDeclaringClass().getName()+":"+u.toString();
	}
	
	public void setUnitId(String id)
	{
		sootUnitId= id;
	}
	
	public String getUnitId()
	{
		return sootUnitId;
	}
	
}
