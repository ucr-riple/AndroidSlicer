//the purpose of this class is to add logging functionality to the apk, so that we can get what we require
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

//import MethodInstrumenter.ThreadRunner;
import soot.Body;
import soot.BodyTransformer;
import soot.Local;
import soot.PackManager;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Transform;
import soot.Unit;
import soot.jimple.AbstractStmtSwitch;
import soot.jimple.AssignStmt;
import soot.jimple.DefinitionStmt;
import soot.jimple.IfStmt;
import soot.jimple.InvokeExpr;
import soot.jimple.InvokeStmt;
import soot.jimple.Jimple;
import soot.jimple.Stmt;
import soot.jimple.LookupSwitchStmt;
import soot.jimple.StringConstant;
import soot.jimple.SwitchStmt;
import soot.options.Options;

public class Instrumenter {
	
	static long time =5*60*1000;
	static String pkgName = "";
	public static Object mLock = new Object();
	public static boolean isComplete = false;
	static enum TYPE {DIRECTOR, HEAD, TAIL, DEF, INST};
	
	static List <String> tailsList = new ArrayList<String>();
	static List <String> headsList = new ArrayList<String>();
	static List <String> directorList = new ArrayList<String>();
	static List <Integer> defList = new ArrayList<Integer>();
	static SootClass cls ;
	static SootMethod  mtd ;
	static boolean isCallback = false;
	static List <Unit> identityStmts = new ArrayList();
	static class ThreadRunner extends Thread
	{
		public void run()
		{
			while (time <= 30*60*1000)
			{
				System.out.println("Waiting: at observer. Time = " + time );
				System.out.println("sade");
				synchronized (mLock) {
					if(!isComplete)
					{
						try {
							Thread.sleep(5*60*1000);
						}catch(Exception ex)
						{
							
						}
					}
					
				}
				time+=5*60*1000;
			}
			System.out.println("Observer: Cannot wait 10 minutes for a single app. Exiting.");
			System.exit(0);
			
		}
	}
	
	static void initialize(String pkgName2)
	{
		Options.v().set_src_prec(Options.src_prec_apk);
		pkgName = pkgName2.replace("'", "");
		if(pkgName.contains("/"))
		{
			String[] pkgNameArray = pkgName.split("/");
			pkgName = pkgNameArray[pkgNameArray.length-1];
		}
		System.out.println ("pkg: "+pkgName);
		Scene.v().addBasicClass("java.io.PrintStream",SootClass.SIGNATURES);
		Scene.v().addBasicClass("java.lang.System",SootClass.SIGNATURES);		

	}
	
	static void addLog(TYPE type, Unit u, SootClass sc, SootMethod sm, Body b)
	{
		String header = u.getJavaSourceStartLineNumber() +","+ sc.getName() + "," + sm.getName();
		String tag = "SLC: ";
		String typeStr = "";
		
		switch(type)
		{
			case DIRECTOR:
				typeStr = "__director__";
				break;
			case HEAD:
				typeStr = "__head__";
				break;
			case TAIL:
				typeStr = "__tail__";
				break;
			case INST:
				typeStr = "__inst__";
				break;
			default:
				break;
		}
		String toAdd = tag+"," + header + "," + typeStr+"," + u.toString();
		
		addPrint(toAdd, u, b);
	}
	
	static String getPayload(TYPE type, Unit u, SootClass sc, SootMethod sm, Body b)
	{
		String header = u.getJavaSourceStartLineNumber() +"ZZZ"+ sc.getName() + "ZZZ" + sm.getName();
		String tag = "SLICING: ";
		String typeStr = "";
		
		switch(type)
		{
			case DIRECTOR:
				typeStr = "__director__";
				break;
			case HEAD:
				typeStr = "__head__";
				break;
			case TAIL:
				typeStr = "__tail__";
				break;
			case INST:
				typeStr = "__inst__";
				break;
			default:
				break;
		}
		return tag+"ZZZ" + header + "ZZZ" + typeStr+"ZZZ" + u.toString();
	}
	
	static void addPrint(String str, Unit u, Body b)
	{
		
		Local tmpRef = addTmpRef(b);
		Local tmpString = addTmpString(b);
		
		final PatchingChain<Unit> units = b.getUnits();
		units.insertBefore(Jimple.v().newAssignStmt(
				tmpRef, Jimple.v().newStaticFieldRef(
						Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())), u);
		
		units.insertBefore(Jimple.v().newAssignStmt(tmpString,
				StringConstant.v(str)), u);
		
		SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
		units.insertBefore(Jimple.v().newInvokeStmt(
				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)), u);
	}
	
	static void runMethodTransformationPack()
	{
		
		PackManager.v().getPack("jtp").add(new Transform("jtp.myInstrumenter", new BodyTransformer() {
			
			@Override
			protected void internalTransform(final Body b, String phaseName, @SuppressWarnings("rawtypes") Map options) {
				
				// Uncomment the below line and its corresponding "}" to narrow down the scope of 
				//instrumented classes to your interested classes by putting the name of classes that 
				//you want or don't want to instrument.
				//if(!(b.getMethod().getDeclaringClass().getName().contains("com.squareup.picasso"))) {
				SootClass sc = b.getMethod().getDeclaringClass();
				SootMethod sm = b.getMethod();				
				final PatchingChain<Unit> units = b.getUnits();
				
				for(Iterator iter = units.snapshotIterator(); iter.hasNext();) {
		            final Unit u = (Unit) iter.next();
		            
		            cls = sc;
		            mtd = sm;
		            if(b.getMethod().getName().startsWith("on"))
		            {
		            	isCallback = true;
		            }
		            
		            u.apply(new AbstractStmtSwitch() {		
						
		            	public void caseInvokeStmt(InvokeStmt stmt) {

		            		Local tmpRef = addTmpRef(b);
							Local tmpString = addTmpString(b);
							String payload = getPayload(TYPE.INST, u, cls, mtd, b);
							InvokeExpr iex = stmt.getInvokeExpr();
		            		if(isCallback)
		            		{
		            			payload = "CALLBACK_SLC: " + payload;
		            			isCallback = false;
		            		}
				            units.insertBefore(Jimple.v().newAssignStmt(
				    				tmpRef, Jimple.v().newStaticFieldRef(
				    						Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())), u);
	
				    		units.insertBefore(Jimple.v().newAssignStmt(tmpString, 
				    				StringConstant.v(payload)), u);
				    		
				    		SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
				    		units.insertBefore(Jimple.v().newInvokeStmt(
				    				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)), u);
				    		b.validate();
						}
		            	
		            	
		            	public void caseAssignStmt(AssignStmt stmt)
						{
							Local tmpRef = addTmpRef(b);
							Local tmpString = addTmpString(b);
							String payload = getPayload(TYPE.INST, u, cls, mtd, b);
							if(isCallback)
		            		{
		            			payload = "CALLBACK_SLC: " + payload;
		            			isCallback = false;
		            		}
							units.insertBefore(Jimple.v().newAssignStmt(
				    				tmpRef, Jimple.v().newStaticFieldRef(
				    						Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())), u);
				    		
				    		units.insertBefore(Jimple.v().newAssignStmt(tmpString,
				    				StringConstant.v(payload)), u);
				    		
				    		SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
				    		units.insertBefore(Jimple.v().newInvokeStmt(
				    				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)), u);
				    		b.validate();
			            }
			            	
						
						public void caseDefinitionStmt(DefinitionStmt stmt)
						{
							Local tmpRef = addTmpRef(b);
							Local tmpString = addTmpString(b);
							String payload = getPayload(TYPE.INST, u, cls, mtd, b);
							if(isCallback)
		            		{
		            			payload = "CALLBACK_SLC: " + payload;
		            			isCallback = false;
		            		}
							units.insertBefore(Jimple.v().newAssignStmt(
				    				tmpRef, Jimple.v().newStaticFieldRef(
				    						Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())), u);
	
				    		units.insertBefore(Jimple.v().newAssignStmt(tmpString,
				    				StringConstant.v(payload)), u);
				    		
				    		SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
				    		units.insertBefore(Jimple.v().newInvokeStmt(
				    				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)), u);
				    		b.validate();
						}
						
						public void caseLookupSwitchStmt(LookupSwitchStmt stmt)
						{
							Local tmpRef = addTmpRef(b);
							Local tmpString = addTmpString(b);
							String payload = getPayload(TYPE.INST, u, cls, mtd, b);
							if(isCallback)
		            		{
		            			payload = "CALLBACK_SLC: " + payload;
		            			isCallback = false;
		            		}
							units.insertBefore(Jimple.v().newAssignStmt(
				    				tmpRef, Jimple.v().newStaticFieldRef(
				    						Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())), u);
	
				    		units.insertBefore(Jimple.v().newAssignStmt(tmpString,
				    				StringConstant.v(payload)), u);
				    		
				    		SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
				    		units.insertBefore(Jimple.v().newInvokeStmt(
				    				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)), u);
				    		b.validate();
						}
						public void caseSwitchStmt(SwitchStmt stmt)
						{
							Local tmpRef = addTmpRef(b);
							Local tmpString = addTmpString(b);
							String payload = getPayload(TYPE.INST, u, cls, mtd, b);
							if(isCallback)
		            		{
		            			payload = "CALLBACK_SLC: " + payload;
		            			isCallback = false;
		            		}
							units.insertBefore(Jimple.v().newAssignStmt(
				    				tmpRef, Jimple.v().newStaticFieldRef(
				    						Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())), u);
	
				    		units.insertBefore(Jimple.v().newAssignStmt(tmpString,
				    				StringConstant.v(payload)), u);
				    		
				    		SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
				    		units.insertBefore(Jimple.v().newInvokeStmt(
				    				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)), u);
				    		b.validate();
						}
						public void caseIfStmt(IfStmt stmt)
						{
							Local tmpRef = addTmpRef(b);
							Local tmpString = addTmpString(b);
							String payload = getPayload(TYPE.INST, u, cls, mtd, b);
							if(isCallback)
		            		{
		            			payload = "CALLBACK_SLC: " + payload;
		            			isCallback = false;
		            		}
							units.insertBefore(Jimple.v().newAssignStmt(
				    				tmpRef, Jimple.v().newStaticFieldRef(
				    						Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())), u);
	
				    		units.insertBefore(Jimple.v().newAssignStmt(tmpString,
				    				StringConstant.v(payload)), u);
				    		
				    		SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
				    		units.insertBefore(Jimple.v().newInvokeStmt(
				    				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)), u);
				    		b.validate();
						}
						
						public void caseStmt(Stmt stmt)
						{
							Local tmpRef = addTmpRef(b);
							Local tmpString = addTmpString(b);
							String payload = getPayload(TYPE.INST, u, cls, mtd, b);
							if(isCallback)
		            		{
		            			payload = "CALLBACK_SLC: " + payload;
		            			isCallback = false;
		            		}
							units.insertBefore(Jimple.v().newAssignStmt(
				    				tmpRef, Jimple.v().newStaticFieldRef(
				    						Scene.v().getField("<java.lang.System: java.io.PrintStream out>").makeRef())), u);
				    		
				    		units.insertBefore(Jimple.v().newAssignStmt(tmpString,
				    				StringConstant.v(payload)), u);
				    		
				    		SootMethod toCall = Scene.v().getSootClass("java.io.PrintStream").getMethod("void println(java.lang.String)");
				    		units.insertBefore(Jimple.v().newInvokeStmt(
				    				Jimple.v().newVirtualInvokeExpr(tmpRef, toCall.makeRef(), tmpString)), u);
				    		b.validate();
						}	
						
					});

		        }
			//}//if uuid
				
			}
		}));
		
	}
	
	private static Local addClassRef(Body body)
	{
		Local tmpRef = Jimple.v().newLocal("classRef", RefType.v("java.lang.Class"));
		body.getLocals().add(tmpRef);
		return tmpRef;
	}

	private static Local addStringRef(Body body)
	{
		Local tmpStrRef = Jimple.v().newLocal("stringRef", RefType.v("java.lang.String"));
		body.getLocals().add(tmpStrRef);
		return tmpStrRef;
	}

	private static Local addTmpRef(Body body)
	{
		Local tmpRef = Jimple.v().newLocal("tmpRef", RefType.v("java.io.PrintStream"));
		body.getLocals().add(tmpRef);
		return tmpRef;
	}

	private static Local addTmpString(Body body)
	{
		Local tmpString = Jimple.v().newLocal("tmpString", RefType.v("java.lang.String"));
		body.getLocals().add(tmpString);
		return tmpString;
	}
	
	public static void main (String args[])
	{
		initialize(args[0]);
		
		runMethodTransformationPack();
		int argLen = args.length-1;
		String newArgs[] = new String [argLen];

		for (int ii =1; ii < argLen + 1; ii++)
		{
			newArgs[ii-1]=args[ii];
		}

		soot.Main.main(newArgs);
		System.exit(0);
	}

}











