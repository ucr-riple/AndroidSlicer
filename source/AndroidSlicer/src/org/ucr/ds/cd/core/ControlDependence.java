package org.ucr.ds.cd.core;

import java.util.LinkedHashMap;
import java.util.Map;

public class ControlDependence {
	
	//immediate static post dominator
	
	static Map <String, String> IMMIDIATE_STATIC_POST_DOMNATOR = new LinkedHashMap<String, String>();
	static class ControlDependanceStack
	{
		Instruction directorRegion;
		Instruction IPD;
	}
	//immediate dynamic post dominator
	//director of a region
	//immediate static post dominator of it

}
