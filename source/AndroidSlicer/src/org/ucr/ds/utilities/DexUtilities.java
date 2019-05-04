package org.ucr.ds.utilities;

import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.util.Chain;

public class DexUtilities {
	
	//
	public static Unit getUnitByLineNumber(int lineNumber , String className, Chain<SootClass> chain) 
	{
		Unit result=null;

		//Chain<SootClass> chain = Scene.v().getApplicationClasses();
		Iterator<SootClass> iterator = chain.snapshotIterator();	
		while (iterator.hasNext()) {
		      SootClass sootClassTemp = iterator.next();	
		      if(sootClassTemp.getName().contains(className))
		      {
		    	  List<SootMethod> methods = sootClassTemp.getMethods();  
		    	  for (SootMethod sootMethodTemp : methods) {
		    		  if(sootMethodTemp.getSource()!= null){
		    			  Body sootBody = sootMethodTemp.retrieveActiveBody();
		    			  BlockGraph blockGraph = new ExceptionalBlockGraph(sootBody);
		    			  for (Block block : blockGraph.getBlocks()) 
						  {					  
							  for(Iterator<Unit> iter = block.iterator(); iter.hasNext();)
							  {
								  Unit unit = iter.next();
								  if(unit.getJavaSourceStartLineNumber() == lineNumber)
									  result=unit;							  						  
							  }
						  }
		    			  
		    		  }
		    	  }
		      }
			}
		
		return result;
	}

}
