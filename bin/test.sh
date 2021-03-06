#!/bin/bash

source jars.sh

OUT_DIR=$PROJECT_HOME_DIR/target/classes
CLASS_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$JUNIT_JAR$PATH_SEPARATOR$LIBS_DIR/$HAMCREST_JAR$PATH_SEPARATOR$OUT_DIR
TEST_SRC_DIR=$PROJECT_HOME_DIR/src/test/java
TEST_OUT_DIR=$PROJECT_HOME_DIR/target/test-classes

TEST_SRC_FILES=`find $TEST_SRC_DIR -name "*Test*.java"`
TEST_RESOURCES_DIR=$PROJECT_HOME_DIR/src/test/resources/

mkdir -p $TEST_OUT_DIR/src/test/resources

javac -proc:none -cp $CLASS_PATH -d $TEST_OUT_DIR -sourcepath $TEST_SRC_DIR $TEST_SRC_FILES

cp -r $TEST_RESOURCES_DIR $TEST_OUT_DIR
TEST_RESOURCES_DIR=$TEST_OUT_DIR/resources

CLASS_PATH=$TEST_RESOURCES_DIR:$TEST_OUT_DIR:$CLASS_PATH

TEST_NAMES=""

for t in $TEST_SRC_FILES;
do
	t=${t#$PROJECT_HOME_DIR/src/test/java/}
	t=${t%.java}
 	t=${t//\//.}
	TEST_NAMES="$TEST_NAMES $t"
done

cd ..
java -cp $CLASS_PATH org.junit.runner.JUnitCore $TEST_NAMES
