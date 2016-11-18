#!/bin/bash

source jars.sh

OUT_DIR=$PROJECT_HOME_DIR/out/production/risk
CLASS_PATH=$LIB_PATH:$LIBS_DIR/$JUNIT_JAR:$LIBS_DIR/$HAMCREST_JAR:$OUT_DIR
TEST_SRC_DIR=$PROJECT_HOME_DIR/test
TEST_OUT_DIR=$PROJECT_HOME_DIR/out/test/risk

TEST_SRC_FILES=`find $TEST_SRC_DIR -name "*java"`

mkdir -p $TEST_OUT_DIR

javac -cp $CLASS_PATH -d $TEST_OUT_DIR -sourcepath $TEST_SRC_DIR $TEST_SRC_FILES

CLASS_PATH=$CLASS_PATH:$TEST_OUT_DIR

TEST_NAMES=""

for t in $TEST_SRC_FILES;
do 
	t=${t#../test/}
	t=${t%.java}
 	t=${t//\//.}
	TEST_NAMES="$TEST_NAMES $t"
done

java -cp $CLASS_PATH org.junit.runner.JUnitCore $TEST_NAMES
