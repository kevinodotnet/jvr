#!/bin/bash

############################################################
# Configuration 
############################################################

# Set this to the location of your Java installation.
if test "$JAVA_HOME" == ""; then
	JAVA_HOME=/usr/local/java
fi

############################################################
# You should not need to change anything below this line.
############################################################

# Change to the JVR home directory.
BASE_DIR=`dirname $0`
cd $BASE_DIR;
cd ..

# JVR required only one JAR file.  When you write your own
# application you will need to add your own JAR files as well.
CLASSPATH=lib/jvr.jar

# The 'java.library.path' paramater tells the JVM what
# directories to look in for shared libraries.  In this case
# we specify the "lib" directory so that the JVM can find
# jvr.dll
JAVA_ARGS=-Djava.library.path=lib

# The classpath argument.
JAVA_ARGS="$JAVA_ARGS -cp $CLASSPATH"

# For linkage problems. Ug.
export LD_PRELOAD=/home/kevino/code/jvr/lib/libjvr.so

# Invoke the console
$JAVA_HOME/bin/java $JAVA_ARGS $*
