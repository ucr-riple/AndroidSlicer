package org.ucr.ds.cd.core;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MethodResult {
		Map <String, InstructionUnits> mapInstUnits;
		boolean isEnd;
		String methodName;
		
		MethodResult(String methodName)
		{
			mapInstUnits = new LinkedHashMap();
			isEnd =true;
			this.methodName = methodName;
		}
		
		Map <String, InstructionUnits> getMap()
		{
			return mapInstUnits;
		}
		boolean getResult()
		{
			return isEnd;
		}
		
		void setResult(boolean result)
		{
			isEnd = result;
		}
		void addInsList(List <InstructionUnits> listIUs)
		{
			for(InstructionUnits iu: listIUs)
			{
				mapInstUnits.put(iu.getUnitId(), iu);
			}
			
		}
		void addIns(InstructionUnits iu)
		{
			if(!iu.getMethod().getName().equals(""))
			{
				if(mapInstUnits.get(iu.getUnitId())==null)
				{
					
					mapInstUnits.put(iu.getUnitId(), iu);
					
				}
				else
				{
					mapInstUnits.put(iu.getUnitId(), iu);
				}
			}
		}
		

}
