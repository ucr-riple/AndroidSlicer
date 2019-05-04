

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


import org.xmlpull.v1.XmlPullParserException;

import soot.*;
import soot.jimple.infoflow.android.SetupApplication;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.options.Options;
import soot.toolkits.graph.ExceptionalUnitGraph;
import soot.util.Chain;

//import oracle.jrockit.jfr.Options;

public class TestInside {

	//@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		SetupApplication app = new SetupApplication(Constants.PLATFORM_PATH, Constants.APK_PATH);
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

		Options.v().set_process_dir(Collections.singletonList(Constants.APK_PATH));
		

		Options.v().set_android_jars(Constants.PLATFORM_PATH);

		Options.v().set_whole_program(true);

		Options.v().set_allow_phantom_refs(true);

		Options.v().set_keep_line_number(true);
		Options.v().set_allow_phantom_refs(true);
		List <String> list = new ArrayList<>();
		list.add(Constants.APK_PATH);
		Options.v().set_process_dir(Collections.singletonList(Constants.APK_PATH));

		Options.v().set_output_format(Options.output_format_none);

		Options.v().setPhaseOption("cg.spark verbose:true", "on");
		Scene.v().loadNecessaryClasses();
		
		// SetupApplication app = new SetupApplication(Constants.PLATFORM_PATH, Constants.APK_PATH);
// 		try {
// 
// 		app.calculateSourcesSinksEntrypoints(Constants.SOURCE_SINK_PATH);
// 
// 		} catch (IOException e) {
// 
// 		// TODO Auto-generated catch block
// 
// 		e.printStackTrace();
// 
// 		} catch (XmlPullParserException e) {
// 
// 		// TODO Auto-generated catch block
// 
// 		e.printStackTrace();
// 
// 		}

//		
		
		SootMethod entryPoint = app.getEntryPointCreator().createDummyMain();

		Options.v().set_main_class(entryPoint.getSignature());

		Scene.v().setEntryPoints(Collections.singletonList(entryPoint));
		System.out.println(entryPoint.getActiveBody());

		PackManager.v().runPacks();

		System.out.println(Scene.v().getCallGraph().size());
		CallGraph cg = Scene.v().getCallGraph();
		//app.runInfoflow();
		

		
//		SootMethod sm = Scene.v().getMainMethod();
//		
//		ExceptionalUnitGraph eu = new ExceptionalUnitGraph(sm.getActiveBody());
//		
//		Body b = eu.getBody();
//		Chain <Unit> units = b.getUnits();
//		
//		Iterator<Unit > it = units.snapshotIterator();
//		
//		while(it.hasNext())
//		{
//			System.out.println("current: "+((Unit)it.next()).toString());
//			System.out.println("next :" + eu.getSuccsOf(it.next()));
//		}
//		cfg.checkUnit(cfg.getARandomMethod());
	}

}
