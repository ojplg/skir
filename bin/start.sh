#!/bin/bash

source jars.sh

echo "Starting ... "

CLASS_PATH=$CYGWIN_PREFIX$PROJECT_HOME_DIR/target/classes$PATH_SEPARATOR$LIB_PATH

echo $CLASS_PATH

cd .. 
java -cp $CLASS_PATH ojplg.skir.play.Skir $@
