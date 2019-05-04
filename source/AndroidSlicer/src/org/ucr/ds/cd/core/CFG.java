package org.ucr.ds.cd.core;

import java.io.File;
import java.io.IOException;

import org.xmlpull.v1.XmlPullParserException;
import org.apache.commons.io.FileUtils;
import org.ucr.ds.cd.utilities.*;

import soot.jimple.infoflow.android.SetupApplication;
import soot.toolkits.graph.*;
import soot.*;
import soot.tagkit.LineNumberTag;
import soot.options.Options;
import soot.jimple.JimpleBody;
import soot.util.*;
import java.util.*;
import soot.options.*;

public class CFG {
	
	public static Map <SootClass, List<ExceptionalBlockGraph>> mapCFG = new LinkedHashMap<SootClass, List<ExceptionalBlockGraph>>();
	
	private static Map <String, SootClass> mapClassName = new LinkedHashMap<String, SootClass>();
	private Map <String, List<SootMethod>> mapClassMethod = new LinkedHashMap<String, List<SootMethod>>();
	
	private static List <Unit> listDirectors = new ArrayList<Unit>();
	private static Map <Unit, Unit> mapUnitDirector = new LinkedHashMap<Unit, Unit>(); 
	private static List <Unit> listEndPoints = new ArrayList<Unit>();
	

	public CFG(String apkPath)
	{
		
		SetupApplication app = new SetupApplication(Constants.PLATFORM_PATH, apkPath);
		try {

		app.calculateSourcesSinksEntrypoints(Constants.SOURCE_SINK_PATH);

		} catch (IOException e) {

		// TODO Auto-generated catch block

		e.printStackTrace();

		} catch (XmlPullParserException e) {

		// TODO Auto-generated catch block

		e.printStackTrace();

		}

		soot.G.reset();

		Options.v().set_keep_line_number(true);

		Options.v().set_src_prec(Options.src_prec_apk);

		Options.v().set_process_dir(Collections.singletonList(apkPath));
		

		Options.v().set_android_jars(Constants.PLATFORM_PATH);

		Options.v().set_whole_program(true);

		Options.v().set_allow_phantom_refs(true);

		Options.v().set_keep_line_number(true);

		Options.v().set_output_format(Options.output_format_none);

		Options.v().setPhaseOption("cg.spark", "on");

		Scene.v().loadNecessaryClasses();
	}
	
	public static SootMethod getARandomMethod()
	{
		return Scene.v().getMainMethod();
	}
	
	public static void calculateDirectorRegions(SootMethod sm)
	{
		Chain <Unit> chain = sm.retrieveActiveBody().getUnits();
		
		for(Unit u: chain)
		{
			//whether there is a transition
			if(u.getUnitBoxes().size()>0)
			{
				listDirectors.add(u);
				for(UnitBox b: u.getUnitBoxes())
				{
					Unit unit = b.getUnit();
//					List <Type> types = sm.getParameterTypes();
//					for (Type p: types)
//					{
//						
//					}
					mapUnitDirector.put(unit, u);
				}
			}
		}
	}
	//check this method
	public static  void loadCFG()
	{
		Chain<SootClass> chain = Scene.v().getApplicationClasses();
		Iterator<SootClass> iterator = chain.snapshotIterator();
		while(iterator.hasNext())
		{
			SootClass sc = iterator.next();
			List <SootMethod> listMethods = sc.getMethods();
			List <ExceptionalBlockGraph> listEug = new ArrayList();
			for(SootMethod sm : listMethods)
			{			
				if(sm.getSource()!= null){
					listEug.add(new ExceptionalBlockGraph(sm.retrieveActiveBody()));
				}
			}

			mapCFG.put(sc,  listEug);
			
		}
	}
	//check this method
	public static void saveCFG() throws IOException
	{
		Set <SootClass> scs = mapCFG.keySet();
		
		for(SootClass sc:scs)
		{
			List <String> ll = new ArrayList<String>();
			String header = sc.getName();
			List <ExceptionalBlockGraph> l= mapCFG.get(sc);
			for(ExceptionalBlockGraph eb:l)
			{
				header += "," + eb.getBody().getMethod().getName();
				List <Block> listBlocks = eb.getBlocks();
				
				for(Block b: listBlocks)
				{
					
					header +=","+b.getHead().toString()+","+b.getHead().getJavaSourceStartLineNumber();
					header +=","+b.getTail().toString()+","+b.getTail().getJavaSourceStartLineNumber();
					
					List <Block> listPreds= b.getPreds();
					List <Block> listSucc= b.getSuccs();
					String predsLineNo= ",PRED_START";
					if(listPreds.size()>0)
					{
						for(Block predBlock : listPreds)
						{
							predsLineNo += ","+predBlock.getTail().toString()+","+predBlock.getTail().getJavaSourceStartLineNumber();
						}
						header +=predsLineNo +","+"PRED_END";
					}
					
					String succsLineNo= "";
					if(listSucc.size()>0)
					{
						for(Block succBlock : listSucc)
						{
							succsLineNo += ","+succBlock.getHead().toString()+","+succBlock.getHead().getJavaSourceStartLineNumber();
						}
						header +=succsLineNo;
					}
					
					ll.add(header);
				}
				FileUtils.writeLines(new File(sc.getName()+"_block.dat"),  ll, true);
				ll=new ArrayList<String>();
			}
			
			
			
		}
	}
	
	public static void checkUnit(SootMethod sm)
	{
		ExceptionalUnitGraph eu = new ExceptionalUnitGraph(sm.retrieveActiveBody());
		
		Body b = eu.getBody();
		Chain <Unit> units = b.getUnits();
		
		
		Iterator<Unit > it = units.snapshotIterator();
		
		while(it.hasNext())
		{
			System.out.println("current: "+((Unit)it.next()).toString());
			System.out.println("next :" + eu.getSuccsOf(it.next()));
		}
		
	}
	
	public static void getUnitFromLine(SootClass sc)
	{
		//first get sootmethod and class
		//then get it
		
		List <ExceptionalBlockGraph> listTemp = mapCFG.get(sc);
		for (ExceptionalBlockGraph eu: listTemp)
		{
			
		}
	}
	
	
}
