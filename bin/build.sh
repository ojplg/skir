#!/bin/bash

source jars.sh

#echo $LIB_PATH

OUT_DIR=$PROJECT_HOME_DIR/target/classes
SRC_DIR=$PROJECT_HOME_DIR/src/main/java
RESOURCE_DIR=$PROJECT_HOME_DIR/resource

mkdir -p $OUT_DIR

javac -cp $LIB_PATH -d $OUT_DIR -sourcepath $SRC_DIR $SRC_DIR/ojplg/skir/play/Skir.java

cp -r $RESOURCE_DIR/* $OUT_DIR
cp $SRC_DIR/log4j2.xml $OUT_DIR
