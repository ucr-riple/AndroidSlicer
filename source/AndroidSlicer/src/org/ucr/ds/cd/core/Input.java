package org.ucr.ds.cd.core;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Input {
	
	public Map <String, InstructionUnits> mapKeyUnits =new LinkedHashMap<String, InstructionUnits>();
	public Map <Integer, String> mapNoKey =new LinkedHashMap<Integer, String>();
	public Map <String, Integer> mapKeyNo =new LinkedHashMap<String, Integer>();
	public List <String> unitIds = new ArrayList();

}
