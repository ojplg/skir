#!/bin/bash

source jars.sh

echo "Starting ... "

CLASS_PATH=$PROJECT_HOME_DIR/target/classes$PATH_SEPARATOR$LIB_PATH

echo $CLASS_PATH

java -cp $CLASS_PATH ojplg.skir.play.Skir $@
