PLATFORDIRFORSLICER="/home/aalav003/Desktop/adt-bundle-linux-x86_64-20140702/sdk/platforms/"
SOURCESINK="SourcesAndSinks.txt"

echo "please enter the line number for slicing criterion"
read line_no

java -jar AndroidSlicer.jar r $1 "$1.logcat.txt" $line_no $PLATFORDIRFORSLICER $SOURCESINK
