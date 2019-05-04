package org.ucr.ds.cd.core;

import com.sun.corba.se.impl.protocol.ServantCacheLocalCRDBase;

import soot.Unit;

public class Instruction {
	private String methodString;
	private String classString;
	private int line;
	private Unit unit;
	
	public Instruction()
	{
		setMethodString("");
		setClassString("");
		setLine(-1);
		setUnitString(null);
	}

	String getMethodString() {
		return methodString;
	}

	void setMethodString(String methodString) {
		this.methodString = methodString;
	}

	String getClassString() {
		return classString;
	}

	void setClassString(String classString) {
		this.classString = classString;
	}

	int getLine() {
		return line;
	}

	void setLine(int line) {
		this.line = line;
	}

	Unit getUnitString() {
		return unit;
	}

	void setUnitString(Unit unit) {
		this.unit = unit;
	}
}
