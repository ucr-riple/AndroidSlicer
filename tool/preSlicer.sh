PLATFORDIRFORSLICER="/home/aalav003/Desktop/adt-bundle-linux-x86_64-20140702/sdk/platforms/"
SOURCESINK="SourcesAndSinks.txt"

java -jar preSlicer.jar t $1 "$1.logcat.txt" "$1.logcat.processed.txt" $PLATFORDIRFORSLICER $SOURCESINK
