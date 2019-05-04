echo "if you closed adb logcat press enter"
read input_close
rm -r sootOutput/$1
PLATFORDIRFORSLICER="/home/aalav003/Desktop/adt-bundle-linux-x86_64-20140702/sdk/platforms/"
SOURCESINK="SourcesAndSinks.txt"

PKGNAME=`aapt dump badging $1 | grep package:\ name | awk '{ print $2 }' | cut -f2 -d"="`
echo $PKGNAME


#javac -cp  ".:sootclasses-trunk-jar-with-dependencies.jar:soot-infoflow-android.jar;/usr/lib/jvm/java-8-oracle/jre/rt.jar" Instrumenter.java
#java -Xmx5g -cp  ".:sootclasses-trunk-jar-with-dependencies.jar:soot-infoflow-android.jar;/usr/lib/jvm/java-8-oracle/jre/rt.jar" Instrumenter $PKGNAME -w -allow-phantom-refs -process-multiple-dex -android-jars $PLATFORDIRFORSLICER -src-prec apk -output-format dex -process-dir  $1

javac -cp  ".:soot-trunk.jar:soot-infoflow.jar:soot-infoflow-android.jar;/usr/lib/jvm/java-8-oracle/jre/rt.jar" Instrumenter.java
java -Xmx5g -cp  ".:soot-trunk.jar:soot-infoflow.jar:soot-infoflow-android.jar;/usr/lib/jvm/java-8-oracle/jre/rt.jar" Instrumenter $PKGNAME -w -allow-phantom-refs -process-multiple-dex -android-jars $PLATFORDIRFORSLICER -src-prec apk -output-format dex -process-dir  $1

chmod 777 sootOutput/$1
echo signing sootOutput/$1 to sootOutput/$1_signed.apk
java -jar signapk.jar testkey.x509.pem testkey.pk8 sootOutput/$1 $1_signed.apk

chmod 777 $1_signed.apk


