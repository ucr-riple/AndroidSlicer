package org.ucr.ds.cd.utilities;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.ucr.ds.cd.core.Input;
import org.ucr.ds.cd.core.InstructionUnits;
import org.ucr.ds.cd.core.Traces;

import soot.Body;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.util.Chain;

public class Parser {
	//tested
	public static List <Traces>  readFile(String fileName) throws IOException
	{
		List <String> traces = FileUtils.readLines(new File(fileName), Charset.defaultCharset());
		List <Traces> listTraces = new ArrayList();
		
		for(String line : traces)
		{
			String []tokens = line.split("ZZZ");
			if(tokens.length != 6) continue;
			if(tokens.length == Constants.TOKEN_LENGTH)
			{
				Traces tr = new Traces();
				tr._lineNo = Integer.parseInt(tokens[1]);
				tr._class = tokens[2];
				tr._method = tokens[3];
				tr._type = tokens[4];
				tr._ins = tokens[5];
				listTraces.add(tr);
			}
			else
				continue;
		}
		
		return listTraces;
		
	}
	public static Input transformInput(HashMap<Integer, ArrayList<InstructionUnits>> mapUnits) throws IOException
	{
		//HashMap<Integer, ArrayList<InstructionUnits>> mapUnits = getUnitMethodByLineNumberMethodNameClassName();
		Input input = new Input();
		int i=0;
		for(Integer key : mapUnits.keySet())
		{
			List <InstructionUnits> units = mapUnits.get(key);
			for(InstructionUnits iu: units)
			{
				input.mapKeyNo.put(iu.getUnitId(), i);
				input.mapKeyUnits.put(iu.getUnitId(), iu);
				input.mapNoKey.put(i, iu.getUnitId());
				i++;
			}
			
			
		}
		return input;
	}
	
//	private static HashMap<Integer, ArrayList<InstructionUnits>> getUnitMethodByLineNumberMethodNameClassName() throws IOException {
//		
//		HashMap<String, HashMap<String, ArrayList<String>>> hashresult = doFilePreparation(readFile(Constants.TRACE_FILE_NAME));
//		HashMap<Integer, ArrayList<InstructionUnits>> finlaMapResult = new HashMap<Integer, ArrayList<InstructionUnits>>();
//		for (String name: hashresult.keySet()){
//
//	        String key =name.toString();
//	        HashMap<String, ArrayList<String>> value = hashresult.get(name); 
//	        for (String name2: value.keySet()){
//	        	String key2 =name2.toString();
//	        	String value2 = value.get(key2).toString();
//	        	System.out.println(key + " " + key2 + " " +value2);  
//	        }
//		}
//		
//		for (String keyclass : hashresult.keySet()) {
//			Chain<SootClass> chain = Scene.v().getApplicationClasses();
//			Iterator<SootClass> iterator = chain.snapshotIterator();	
//			while (iterator.hasNext()) {
//				SootClass sootClassTemp = iterator.next();
//				if(sootClassTemp.getName().equals(keyclass))
//				{
//					HashMap<String, ArrayList<String>> tempMethodMap =hashresult.get(keyclass);
//					for (String keymethod : tempMethodMap.keySet()) {
//						List<SootMethod> methods = sootClassTemp.getMethods();
//						for (SootMethod sootMethodTemp : methods) {
//							if(keymethod.equals(sootMethodTemp.getName()))
//							{
//								ArrayList<String> arrayToSearch = tempMethodMap.get(keymethod);
//								for (String s : arrayToSearch)
//			    		  		{
//									String[] splited = s.split(","); //[0]=lineNumber [1]=Position in original file 
//			    		  			int lineNumber= Integer.parseInt(splited[0]);
//			    		  			int positionInOriginalFile= Integer.parseInt(splited[1]);
//			    		  			Body sootBody = sootMethodTemp.retrieveActiveBody();
//			    		  			BlockGraph blockGraph = new ExceptionalBlockGraph(sootBody);
//			    		  			for (Block block : blockGraph.getBlocks()) 
//			    		  			{					  
//			  						  for(Iterator<Unit> iter = block.iterator(); iter.hasNext();)
//			  						  {
//			  							Unit unit = iter.next(); 
//			  							if(unit.getJavaSourceStartLineNumber() == lineNumber)
//			  							{
//			  								InstructionUnits um = new InstructionUnits(sootMethodTemp);
//			  								um.setUnit(unit);
//			  								um.setMethod(sootMethodTemp);
//			  								um.setLineNo(lineNumber);
//			  								if(!finlaMapResult.containsKey(positionInOriginalFile))
//			  								{
//			  									ArrayList<InstructionUnits> tempumarray = new ArrayList<InstructionUnits>();
//			  									tempumarray.add(um);
//			  									finlaMapResult.put(positionInOriginalFile, tempumarray);
//			  								}
//			  								else{
//			  									ArrayList<InstructionUnits> tempumarray = finlaMapResult.get(positionInOriginalFile);
//			  									tempumarray.add(um);
//			  									finlaMapResult.put(positionInOriginalFile, tempumarray);
//			  								}		  									  								
//			  							}
//			  						  }
//			    		  			}  
//			    		  		}
//							}
//						}
//					}
//			    	
//				}
//			}
//		}
//		
//		      	
//		return finlaMapResult;
//		   
//	}
	
	public static HashMap<String, HashMap<String, ArrayList<String>>> doFilePreparation(List<Traces> traces) throws IOException {
		
				HashMap<String, HashMap<String, ArrayList<String>>> mapResult = new HashMap<String, HashMap<String, ArrayList<String>>>();
				
				String myLine = null;
		
				int counterForPosition=1;
				for(Traces t : traces)
				{
					//String[] array = myLine.split(",");
					int linenumber = t._lineNo;
					String methodName = t._method;
					String className = t._class;
					if(!mapResult.containsKey(className)) {
						ArrayList<String> tempArray = new ArrayList<String>();
						tempArray.add(linenumber + "," + counterForPosition);
						HashMap<String, ArrayList<String>> tempMethodMap = new HashMap<String, ArrayList<String>>();
						tempMethodMap.put(methodName, tempArray);
						mapResult.put(className , tempMethodMap);
					}
					else{
						HashMap<String, ArrayList<String>> tempMap =mapResult.get(className);
						if(!tempMap.containsKey(methodName))
						{
							ArrayList<String> tempArray = new ArrayList<String>();
							tempArray.add(linenumber + "," + counterForPosition);
							tempMap.put(methodName,tempArray);
							mapResult.put(className , tempMap);
							
						}
						else{
							ArrayList<String> tempArray =tempMap.get(methodName);
							tempArray.add(linenumber+","+counterForPosition);
							tempMap.put(methodName,tempArray);
							mapResult.put(className , tempMap);
						}
					}
				counterForPosition++;
				}
				return mapResult;
			}

}
