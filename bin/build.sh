#!/bin/bash

source jars.sh

#echo $LIB_PATH

OUT_DIR=$PROJECT_HOME_DIR/target/classes
SRC_DIR=$PROJECT_HOME_DIR/src/main/java
RESOURCE_DIR=$PROJECT_HOME_DIR/src/main/resources

mkdir -p $OUT_DIR

javac -proc:none -cp $LIB_PATH -d $CYGWIN_PREFIX$OUT_DIR -sourcepath $CYGWIN_PREFIX$SRC_DIR $CYGWIN_PREFIX$SRC_DIR/ojplg/skir/play/Skir.java

cp -r $RESOURCE_DIR/* $OUT_DIR
