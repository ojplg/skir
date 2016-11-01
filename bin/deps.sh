#!/bin/bash

# This script will download and install all the dependencies of the project

source jars.sh

TMP_DOWNLOAD_DIR=$PROJECT_HOME_DIR/.tmp_download_dir

JETTY_TARBALL=jetty-distribution-$JETTY_VERSION
JETTY_URL=http://repo1.maven.org/maven2/org/eclipse/jetty/jetty-distribution/$JETTY_VERSION/$JETTY_TARBALL

LOG4J_TARBALL=apache-log4j-$LOG4J_VERSION-bin
LOG4J_URL=http://apache.cs.utah.edu/logging/log4j/$LOG4J_VERSION/$LOG4J_TARBALL

JSON_URL=https://storage.googleapis.com/google-code-archive-downloads/v2/code.google.com/json-simple

function clean_and_create_tmp_dir {
	rm -rf $TMP_DOWNLOAD_DIR
	mkdir $TMP_DOWNLOAD_DIR
}

# Pass the URL and the base name of the tar ball
function download_and_untar {
        echo " curling" $1
	curl $1.tar.gz > $2.tar.gz
	echo " extracting" $2
	tar -xzf $2.tar.gz
}

# pass the base name of the tar ball and the name of the desired jar file
function copy_to_libs {
	echo " copying" $2
	cp $1/$2 $LIBS_DIR/
}

function get_log4j {
	echo "log4j ..."
	download_and_untar $LOG4J_URL $LOG4J_TARBALL
	for jar in ${LOG4J_JARS[@]}; do
		copy_to_libs $LOG4J_TARBALL $jar-$LOG4J_VERSION.jar
	done
}

function get_jetty {
	echo "jetty ..."
	download_and_untar $JETTY_URL $JETTY_TARBALL
	for jar in ${JETTY_JARS[@]}; do
		copy_to_libs $JETTY_TARBALL/lib $jar-$JETTY_VERSION.jar
	done
	copy_to_libs $JETTY_TARBALL/lib $SERVLET_JAR
}

function get_json {
	echo "json ..."
	curl $JSON_URL/$JSON_JAR > $JSON_JAR
	cp $JSON_JAR $LIBS_DIR/
}

echo "Downloading dependencies ..."

clean_and_create_tmp_dir
cd $TMP_DOWNLOAD_DIR

get_log4j
get_jetty
get_json

