package org.ucr.ds.cd.core;

import java.util.List;

import soot.toolkits.graph.Block;

public class BB {

	int _id;
	String _headIns;
	String _tailIns;
	List <Integer> _listPreviousBBs;
	List <Integer> _listNextBBs;
	List <Integer> _listDirectorLines;
	String _methodName;
	String _className;
	Block _block;
	int _tailId;
	
	
	
}


