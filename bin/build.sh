#!/bin/bash

source jars.sh

#echo $LIB_PATH

OUT_DIR=$PROJECT_HOME_DIR/out/production/risk
SRC_DIR=$PROJECT_HOME_DIR/src
mkdir -p $OUT_DIR

javac -cp $LIB_PATH -d $OUT_DIR -sourcepath $SRC_DIR $SRC_DIR/play/Risk.java

