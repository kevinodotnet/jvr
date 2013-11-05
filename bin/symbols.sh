#!/bin/bash

# This file is used to simplify the management of importing
# symbol names and values from the Dialogic API to the Java
# package.
#
# Put any new symbols into a file (one per line, no whitespace)
# then call this script with the filename as the only argument.
#
# The script will output two files:
#
# $OUTDIR/new.java: Java member variables for the jvr.java file
# $OUTDIR/new.cpp : PUT_* lines for the jvr.cpp file.

# echo "Need to add CHAR support now"; exit;

BASE_DIR=`dirname $0`
BASE_DIR="$BASE_DIR/.."

INPUTFILE=$1
if test "$INPUTFILE" == ""; then
	echo "No input file provided"
	exit;
fi

OUTDIR=$2
if ! test -e $OUTDIR; then
	echo "Output directory $OUTDIR does not exist.";
	exit 1;
fi

# Get a list of the symbols that have already been imported and add them
# to the list of new symbols
# cat $BASE_DIR/src/jni/jvr.cpp | grep PUT_ | grep -v "#define" | cut -d\" -f2 >> $INPUTFILE

# Remove duplicates
sort -u $INPUTFILE > $INPUTFILE.new
mv $INPUTFILE.new $INPUTFILE

# Output the symbols to the two files we require
JAVA_OUT=$OUTDIR/new.java
CPP_OUT=$OUTDIR/new.cpp
rm $JAVA_OUT $CPP_OUT 2>/dev/null

echo "	////////////////////////////////////////////" >> $CPP_OUT
echo "	// This section managed by bin/symbols.sh   " >> $CPP_OUT
echo "  // DO NOT EDIT MANUALLY                     " >> $CPP_OUT
echo "	////////////////////////////////////////////" >> $CPP_OUT
echo "	////////////////////////////////////////////" >> $JAVA_OUT
echo "	// This section managed by bin/symbols.sh   " >> $JAVA_OUT
echo "  // DO NOT EDIT MANUALLY                     " >> $JAVA_OUT
echo "	////////////////////////////////////////////" >> $JAVA_OUT

for i in `cat $INPUTFILE`; do
	echo "	PUT_INT(\"$i\",$i);" >> $CPP_OUT
	echo "	public static int $i;" >> $JAVA_OUT
done

echo "	////////////////////////////////////////////" >> $CPP_OUT
echo "	// END OF bin/symbols.sh managed sectionh   " >> $CPP_OUT
echo "	////////////////////////////////////////////" >> $CPP_OUT
echo "	////////////////////////////////////////////" >> $JAVA_OUT
echo "	// END OF bin/symbols.sh managed sectionh   " >> $JAVA_OUT
echo "	////////////////////////////////////////////" >> $JAVA_OUT
echo "" >> $CPP_OUT
echo "" >> $JAVA_OUT
