package org.ucr.ds.cd.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.ucr.ds.cd.utilities.Constants;
import org.ucr.ds.cd.utilities.Parser;
import org.ucr.ds.utilities.DexUtilities;
import org.xmlpull.v1.XmlPullParserException;

import soot.Body;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AssignStmt;
import soot.jimple.Constant;
import soot.jimple.IdentityStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.VirtualInvokeExpr;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.BlockGraph;
import soot.toolkits.graph.ExceptionalBlockGraph;
import soot.util.Chain;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.toolkits.scalar.SimpleLocalDefs;



public class Slicer {
	
	//method+class
	Map <String, List<InstructionUnits>> mapMethodInst = new LinkedHashMap();
	//unitmethodclass
	Map <String, InstructionUnits> mapUnits = new LinkedHashMap();
	//invokations
	Map <String, InstructionUnits> mapInvokations = new LinkedHashMap();
	//results
	Map <String, InstructionUnits> resultMap = new LinkedHashMap<String, InstructionUnits>();

	public static String SourcesSinks;
	
	public static String platformPath;

	Input loadInputfromTraces(List <Traces> tr)
	{
		int len = tr.size();
		System.out.println("length:"+len);
		
		Map <String, Map<Integer, String>> mapTrace = new LinkedHashMap();
		int i=0;
		int jj=0;
		for (Traces t: tr)
		{
//			if(i==853)
//			{
//				System.out.println("gotcha");
//			}
			if(!mapTrace.containsKey(t._method+t._class))
			{
				Map <Integer, String> temp = new LinkedHashMap<Integer, String>();
				temp.put(i, t._ins);
				mapTrace.put(t._method+t._class, temp);
				jj++;
			}
			else
			{
				Map <Integer, String> temp = mapTrace.get(t._method+t._class);
				temp.put(i, t._ins);
				mapTrace.put(t._method+t._class, temp);
				jj++;
			}
			i++;
		}
		
		System.out.println("Processed:"+jj);
		InstructionUnits []ins = new InstructionUnits[len];
		List <InstructionUnits> listUnis = new ArrayList<InstructionUnits>();
		Chain<SootClass> chain = Scene.v().getApplicationClasses();
		Iterator<SootClass> iterator = chain.snapshotIterator();	
		i=0;
		while(iterator.hasNext())
		{
			SootClass sc = iterator.next();
			List<SootMethod> methods = sc.getMethods();
			for(SootMethod mt:methods)
			{
				String key= mt.getName()+sc.getName();
				if(mt.getName().equals("getSimilarStems"))
				{
					PatchingChain<Unit> units1 = mt.getActiveBody().getUnits();
					Map <String, Unit> unitString1 = new LinkedHashMap<String, Unit>();
					for(Unit u1: units1)
					{
						System.out.println("::::::::::::::::::::::::::::::"+u1.toString()+"::::::::::::::::");
					}
				}
				try{
					if(mt.getActiveBody()==null)continue;
				}catch(Exception ex)
				{
					System.out.println("No body:"+mt.getName());
					continue;
				}
				PatchingChain<Unit> units = mt.getActiveBody().getUnits();
				Map <String, Unit> unitString = new LinkedHashMap<String, Unit>();
				for(Unit u: units)
				{
					unitString.put(u.toString(), u);
				}
				if(!mapTrace.keySet().contains(key))continue;
				Map <Integer, String> temp = mapTrace.get(key);
				//System.out.println("Entering:"+mt.getName());
				for(Integer key1: temp.keySet())
				{
					
					
					if(unitString.keySet().contains(temp.get(key1)))
					{
						Unit unit = unitString.get(temp.get(key1));
						i++;
						InstructionUnits iu = new InstructionUnits();
						iu.setMethod(mt);
						iu.setUnit(unit);
						iu.setLineNo(key1);
						iu.setSootUnitId();
						try{
							//System.out.println("This is filled up! "+ins[key1].getUnitId());
						}catch(Exception ex)
						{
							
						}
						ins[key1] = iu;
						listUnis.add(iu);
					}
				}
				
			}
			
		}
		System.out.println("i:"+i +"size:"+listUnis.size());
		//Map <String, InstructionUnits> mapIns = new LinkedHashMap<>();
		Input input = new Input();
		i=0;
		
		while(i<ins.length)
		{
			//mapIns.put(ins[i].getUnitId(), ins[i]);
			if(ins[i]!=null){
				
				input.mapKeyNo.put(ins[i].getUnitId(),  ins[i].getLineNo());
				input.mapKeyUnits.put(ins[i].getUnitId(), ins[i]);
				input.mapNoKey.put(ins[i].getLineNo(), ins[i].getUnitId());	
			}
			
			i++;
		}
		
		
		
		//for(map)
		
		return input;
		
	}

	void printList(List <String> list, String outFile) throws IOException
	{
		System.out.println("Saving:" + list.size());
		FileUtils.writeLines(new File(outFile), list);
	}
	public static void main(String args[])
	{
		if(args[0].equals("h"))
		{
			System.out.println("slicer t apk trace_file output_processed_trace_file platformdir SourcesSinks" );
			System.out.println("slicer r apk trace_file position_in_processed_trace_file platformdir SourcesSinks" );
			System.exit(0);
		}
		try {
			boolean justTrace = false;
			String option = args[0];
			int posToSlice = -1;
			String fileToParse = "";
			String pathApk = "";
			String outFile = "";
			platformPath = args[4];
			SourcesSinks = args[5];
			if(option.equals("t"))
			{
				justTrace = true;
				pathApk = args[1];
				fileToParse = args[2];
				outFile = args[3];
			}
			else
			{
				pathApk = args[1];
				fileToParse = args[2];
				posToSlice=Integer.parseInt(args[3]);
				
			}
			List <Traces>trs =  Parser.readFile(fileToParse);
			//HashMap<String, HashMap<String, ArrayList<String>>> map =Parser.doFilePreparation(trs);
			
			Slicer slicer = new Slicer(pathApk, platformPath);
			//HashMap<Integer, ArrayList<InstructionUnits>> mp2 = slicer.getUnitMethodByLineNumberMethodNameClassName(map);
			//Input input =Parser.transformInput(mp2);
			Input input = slicer.loadInputfromTraces(trs);
			List <String> callbackExecuted = new ArrayList<String>();
			if(justTrace)
			{
				System.out.println("Printing trace...");
				List <String> listTOPrint = new ArrayList<String>();
				Iterator entries = input.mapKeyNo.entrySet().iterator();
				while (entries.hasNext()) {
				  Entry thisEntry = (Entry) entries.next();
				  String key = (String)thisEntry.getKey();
				  Integer value = (Integer)thisEntry.getValue();
				  if(input.mapKeyUnits.get(key).getMethod().getName().startsWith("on"))
				  {
					  callbackExecuted.add(key);
					  callbackExecuted.add("\n");
				  }
				  listTOPrint.add(key);
				  // ...
				}
				
				slicer.printList(listTOPrint, outFile);
				slicer.printList(callbackExecuted, pathApk+".callbacks.txt");
				//FileUtils.writeLines(new File(outFile), input.mapKeyNo.keySet());
				System.out.println("Printing Complete.");
				System.exit(0);
				
			}
			
			System.out.println("size of the trace after loading:"+input.mapKeyNo.keySet().size());
			System.out.println("Testing for input:"+ input.mapNoKey.get(posToSlice));
			List <MethodResult> results = slicer.loadPDG(input , input.mapNoKey.get(posToSlice), posToSlice);

			System.out.println("Printing static program dependence from point of interest:");
			List <String> staticPrint = new ArrayList<String>();
			List <String> dynamicPrint = new ArrayList<String>();
			for(MethodResult me: results)
			{
				Map <String, InstructionUnits> mapp = me.getMap();
				
				for(String key: mapp.keySet())
				{
					InstructionUnits insUnit = mapp.get(key);
					
						System.out.println(key);
						staticPrint.add(key);
					
					
				}
			}
			System.out.println("Printing dynamic dependence:");
			if(slicer.resultMap.size()>0)
			{
				for(String key: slicer.resultMap.keySet())
				{
					dynamicPrint.add(slicer.resultMap.get(key).getUnitId());
				}
			}
			
			slicer.printList(staticPrint, pathApk.trim()+"_static.dat");
			slicer.printList(dynamicPrint, pathApk.trim()+"_dynamic.dat");
			
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public Slicer(String apkPath, String platFormDir)
	{
		SetupApplication app = new SetupApplication(platFormDir, apkPath);
		try {

		app.calculateSourcesSinksEntrypoints(SourcesSinks);

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
		
		Options.v().set_android_jars(platformPath);

		Options.v().set_whole_program(true);

		Options.v().set_allow_phantom_refs(true);

		Options.v().set_keep_line_number(true);

		Options.v().set_output_format(Options.output_format_none);

		Options.v().setPhaseOption("cg.spark", "on");

		Scene.v().loadNecessaryClasses();
		SootMethod entryPoint = app.getEntryPointCreator().createDummyMain();
		 
		Options.v().set_main_class(entryPoint.getSignature());
		
		Scene.v().setEntryPoints(Collections.singletonList(entryPoint));
		
		System.out.println("............"+entryPoint.getActiveBody());
		
		PackManager.v().runPacks();
	}


	
	public List <String> loadCallbacks() throws IOException
	{
		return FileUtils.readLines(new File(Constants.CALLBACK_FILE_NAME), Charset.defaultCharset());
	}
	
	public void loadApis()
	{
		
	}
	
	Input loadTraces()
	{
		return null;
	}
	
	private void initializer()
	{
		
	}
	
	List <InstructionUnits> getInstructionUnits(List<Unit> listUnits, SootMethod sootMethod)
	{
		List <InstructionUnits> listIUs = new ArrayList<InstructionUnits>();
		for(Unit u:listUnits)
		{
			InstructionUnits iu = new InstructionUnits();
			iu.setMethod(sootMethod);
			iu.setUnit(u);
			iu.setLineNo(-1);
			iu.setSootUnitId();
			listIUs.add(iu);
		}
		return listIUs;
	}
	MethodResult getMethodResult(InstructionUnits iu)
	{
		if(iu.getUnit() instanceof InvokeStmt)
		{
			int j=0;
		}
		MethodResult mer = new MethodResult(iu.getMethod().getName());
		mer.addIns(iu);
		
		List <InstructionUnits> listUnits = new ArrayList<InstructionUnits>();
		ExceptionalUnitGraph ex = new ExceptionalUnitGraph(iu.getMethod().getActiveBody());
		SimpleLocalDefs simpleLocalDefs = new SimpleLocalDefs(ex);
		List <Unit> units = new ArrayList<Unit>();
		
		List<ValueBox> useBoxes;
		useBoxes = iu.getUnit().getUseBoxes();
		List <ValueBox> tempUseBoxes =new ArrayList<ValueBox>();
		for(ValueBox vb:useBoxes)
		{
			tempUseBoxes.add(vb);
		}
		
		//now for each usebox
		while(!tempUseBoxes.isEmpty())
		{
			ValueBox vb = tempUseBoxes.remove(0);
			if(vb.getValue() instanceof Local)
			{
				Local local = (Local)vb.getValue();
				if(local instanceof Constant)
				{
					continue;
				}
				if(iu.getMethod().getActiveBody().getParameterLocals().contains(local))
				{
					mer.setResult(false);
				}
				List <Unit> defUnits = simpleLocalDefs.getDefsOf(local);
				for(Unit u: defUnits)
				{
					if(!units.contains(u))
					{
						units.add(u);
						for(ValueBox innerVb:u.getUseBoxes())
						{
							tempUseBoxes.add(innerVb);
						}
					}
					
				}
			}
		}
		
		
		// Handle Intent PutExtrra
		List <ValueBox> intentDefBoxes =new ArrayList<ValueBox>();
		for(Unit uu : units)
		{
			if(uu.toString().contains("= new android.content.Intent"))
			{
				List<ValueBox> defBoxes;
				defBoxes = uu.getDefBoxes();
				for(ValueBox vb:defBoxes)
				{
					intentDefBoxes.add(vb);
				}
			}		
		}	
		

		List <ValueBox> realIntentBoxes =new ArrayList<ValueBox>();
		for(ValueBox vv: intentDefBoxes)
		{
			Local intentLocal = (Local)vv.getValue();
			for(Unit uu : iu.getMethod().getActiveBody().getUnits())
			{
				if(uu.toString().contains("virtualinvoke") && uu.toString().contains("putExtra") && uu.toString().contains(intentLocal.toString()+".") && uu instanceof InvokeStmt)
				{								
					List<ValueBox> useBoxes2;
					useBoxes2 = uu.getUseBoxes();
					for(ValueBox vb:useBoxes2)
					{
						realIntentBoxes.add(vb);
					}
					units.add(uu);
				}
				if(uu.toString().contains("specialinvoke") && uu.toString().contains("android.content.Intent") && uu.toString().contains(intentLocal.toString()+".") && uu instanceof InvokeStmt)
				{
					List<ValueBox> useBoxes2;
					useBoxes2 = uu.getUseBoxes();
					for(ValueBox vb:useBoxes2)
					{
						realIntentBoxes.add(vb);
					}
					units.add(uu);
				}
				
			}
		}	
		
		while(!realIntentBoxes.isEmpty())
		{
			ValueBox vb = realIntentBoxes.remove(0);
			if(vb.getValue() instanceof Local)
			{
				Local local = (Local)vb.getValue();
				if(local instanceof Constant)
				{
					continue;
				}
				if(iu.getMethod().getActiveBody().getParameterLocals().contains(local))
				{
					mer.setResult(false);
				}
				
				List <Unit> defUnits = simpleLocalDefs.getDefsOf(local);
				for(Unit u: defUnits)
				{
					if(!units.contains(u))
					{
						units.add(u);
						for(ValueBox innerVb:u.getUseBoxes())
						{
							realIntentBoxes.add(innerVb);
						}
					}
					
				}
			}
		}		
		
		mer.addInsList(getInstructionUnits(units, iu.getMethod()));
		
		
		return mer;
	}
	
	void buildSlice(MethodResult mer, Map<String, InstructionUnits> dynamicSlice)
	{
		Map <String, InstructionUnits> staticSlice =new LinkedHashMap<String, InstructionUnits>();
		for(String key:mer.getMap().keySet())
		{
			staticSlice.put(key, mer.getMap().get(key));
		}
				
		for(String key: dynamicSlice.keySet())
		{
			try{
				if(staticSlice.containsKey("-1ZZZ"+key.split("ZZZ")[1]))
				{
					resultMap.put(key,  dynamicSlice.get(key));
				}
			}
			catch(ArrayIndexOutOfBoundsException ax)
			{
				System.out.println("Array:"+ key);
			}
			
		}
		
	}
	
	Map<String, InstructionUnits> getChunk(int pos, Input input )
	{
		InstructionUnits iu = input.mapKeyUnits.get(input.mapNoKey.get(pos));
		String currentMethod =  iu.getMethod().getName();
		Map <String, InstructionUnits> chunk = new LinkedHashMap<String, InstructionUnits>();
		int p = pos-1;
		while(p>=0)
		{
			iu = input.mapKeyUnits.get(input.mapNoKey.get(p));
			if(iu==null)
			{
				p--;
				continue;
			}
			if (p>4055 && p<4061)
			{
				iu = input.mapKeyUnits.get(input.mapNoKey.get(p));
			}
			
			if(iu!=null && iu.getMethod().getName().equals(currentMethod))
			{
				chunk.put(iu.getUnitId(), iu);
			}
			else
				break;
			p--;
		}
		return chunk;
	}
	
	List <MethodResult> loadPDG(Input input, String unitId, int pp)
	{
		//Input input =loadTraces();
		List <MethodResult> listMer = new ArrayList<MethodResult>();
		InstructionUnits iu = input.mapKeyUnits.get(unitId);
		int position = input.mapKeyNo.get(unitId);
		MethodResult mer = getMethodResult(iu);
		for(String keyString : mer.getMap().keySet())
		{
			System.out.println(mer.getMap().get(keyString));
		}
		String currentMethod = iu.getMethod().getName();
		String lastMethod = iu.getMethod().getName();
		listMer.add(mer);
		if(mer.getResult()==true)
		{
			buildSlice(mer, getChunk(position, input));			
			return listMer;
		}
		else
		{
			buildSlice(mer, getChunk(position, input));
			listMer.add(mer);
			position --;
			while(position >=0)
			{
				if(position==1683)
				{
					int kkr = 0;
				}
				String nextKey = input.mapNoKey.get(position);
				InstructionUnits nextUnit = input.mapKeyUnits.get(nextKey);
				if(nextUnit ==null || nextUnit.getUnit()==null)
				{
					position--;
					continue;
				}
				Unit unit = nextUnit.getUnit();
				String invokedMethod="";
				if(unit instanceof AssignStmt)
				{
					AssignStmt assighStmt = (AssignStmt)unit;
					Value v =assighStmt.getRightOp();
					
					if(v instanceof VirtualInvokeExpr) 
					{
						invokedMethod = ((VirtualInvokeExpr)v).getMethod().getName();
					}
					if(v instanceof StaticInvokeExpr) 
					{
						invokedMethod = ((StaticInvokeExpr)v).getMethod().getName();
					}
					
					else if(v instanceof SpecialInvokeExpr) 
					{
						invokedMethod = ((SpecialInvokeExpr)v).getMethod().getName();
					}
					
					else if(v instanceof InvokeExpr) 
					{
						invokedMethod = ((InvokeExpr)v).getMethod().getName();
					}
				}
				
				if(unit instanceof InvokeStmt || unit instanceof VirtualInvokeExpr || unit instanceof SpecialInvokeExpr || unit instanceof StaticInvokeExpr|| unit instanceof InvokeExpr )
				{
					currentMethod = nextUnit.getMethod().getName();
					
					if(unit instanceof InvokeStmt) 
					{
						InvokeExpr iex = ((InvokeStmt)unit).getInvokeExpr();
						invokedMethod = iex.getMethod().getName();
					}
					
					if(unit instanceof VirtualInvokeExpr) 
					{
						invokedMethod = ((VirtualInvokeExpr)unit).getMethod().getName();
					}
					
					if(unit instanceof StaticInvokeExpr) 
					{
						invokedMethod = ((VirtualInvokeExpr)unit).getMethod().getName();
					}
					
					else if(unit instanceof SpecialInvokeExpr) 
					{
						invokedMethod = ((SpecialInvokeExpr)unit).getMethod().getName();
					}
					
					else if(unit instanceof InvokeExpr) 
					{
						invokedMethod = ((SpecialInvokeExpr)unit).getMethod().getName();
					}
				}

				if(!invokedMethod.equals(""))
				{

					if(invokedMethod.equals(lastMethod))
					{
						MethodResult mer2 = getMethodResult(nextUnit);
						if(getMethodResult(nextUnit).getResult()!=true)
						{
							buildSlice(mer2, getChunk(position, input));
							listMer.add(mer2);
						}
						else
						{
							buildSlice(mer2, getChunk(position, input));
							listMer.add(mer2);
							return listMer;
						}
					}
				}
				lastMethod=currentMethod;
				position--;
			}
			
		}
		System.out.println("Done");
		return listMer;
	}

}
