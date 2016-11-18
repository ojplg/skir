#!/bin/bash

source jars.sh

echo "Starting ... "

CLASS_PATH=$PROJECT_HOME_DIR/out/production/risk:$LIB_PATH

echo $CLASS_PATH

java -cp $CLASS_PATH ojplg.skir.play.Risk
