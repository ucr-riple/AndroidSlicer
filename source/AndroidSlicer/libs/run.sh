# PKGNAME=`aapt dump badging $1 | grep package:\ name | awk '{ print $2 }' | cut -f2 -d"="`
# #PLATFORMDIR=d:/sup_tools/Android/android-sdk/platforms
# SOOTDIR=C:/tanzir_work/apptasker/Appstract/code/AndroidInstrumentation/src
# TOOLDIR=C:/tanzir_work/apptasker/Appstract/code/instrumentation

javac -cp ".:xmlpull_1_0_5.jar:axml-2.0.jar:AXMLPrinter2.jar:soot-infoflow.jar:soot-infoflow-android.jar:junit.jar:org.hamcrest.core_1.3.0.jar:soot-trunk.jar:sootclasses.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_67.jdk/Contents/Home/jre/lib/rt.jar" TestInside.java
#java -Xmx5g -cp  ".:axml-2.0.jar:AXMLPrinter2.jar:soot-infoflow.jar:soot-infoflow-android.jar:junit.jar:org.hamcrest.core_1.3.0.jar:slf4j-api-1.7.5.jar:slf4j-simple-1.7.5.jar:soot.jar:sootclasses.jar:jasminclasses.jar:c:/Program Files/Java/jdk1.7.0_79/jre/lib/rt.jar" CallGraphGen -w -allow-phantom-refs -android-jars $PLATFORMDIR -src-prec apk -output-format dex -process-dir   $1
java -Xmx5g -cp  ".:xmlpull_1_0_5.jar:axml-2.0.jar:AXMLPrinter2.jar:soot-infoflow.jar:soot-infoflow-android.jar:junit.jar:org.hamcrest.core_1.3.0.jar:soot-trunk.jar:sootclasses.jar:/Library/Java/JavaVirtualMachines/jdk1.7.0_67.jdk/Contents/Home/jre/lib/rt.jar" TestInside
