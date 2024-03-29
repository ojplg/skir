#!/bin/bash

if [ -z $JAVA_PATH_SEPARATOR ]; then
        PATH_SEPARATOR=":"
else
	PATH_SEPARATOR=$JAVA_PATH_SEPARATOR
fi
PROJECT_HOME_DIR=${PWD%/bin}
LIBS_DIR=$CYGWIN_PREFIX$PROJECT_HOME_DIR/lib

JETTY_VERSION=9.4.41.v20210516
JETTY_JARS=(jetty-http jetty-server jetty-util jetty-io jetty-continuation jetty-servlet jetty-security jetty-client)
JETTY_WEBSOCKET_JARS=(javax-websocket-server-impl javax-websocket-client-impl websocket-server websocket-common websocket-api websocket-servlet websocket-client)

SERVLET_JAR=servlet-api-3.1.jar
WEBSOCKET_JAR=javax.websocket-api-1.0.jar

LOG4J_VERSION=2.17.1
LOG4J_JARS=(log4j-core log4j-api)

JSON_JAR=json-simple-1.1.1.jar

JUNIT_VERSION=4.13.1
JUNIT_JAR=junit-$JUNIT_VERSION.jar

HAMCREST_VERSION=1.3
HAMCREST_JAR=hamcrest-core-$HAMCREST_VERSION.jar

JETLANG_VERSION=0.2.17
JETLANG_JAR=jetlang-$JETLANG_VERSION.jar

VELOCITY_VERSION=2.3
VELOCITY_JAR=velocity-engine-core-$VELOCITY_VERSION.jar

APACHE_COLLECTIONS_VERSION=3.2.2
APACHE_COLLECTIONS_JAR=commons-collections-$APACHE_COLLECTIONS_VERSION.jar

APACHE_LANG3_VERSION=3.12.0
APACHE_LANG3_JAR=commons-lang3-$APACHE_LANG3_VERSION

APACHE_CLI_VERSION=1.5.0
APACHE_CLI_JAR=commons-cli-$APACHE_CLI_VERSION

SLF4J_VERSION=1.7.32
SLF4J_JAR=slf4j-api-$SLF4J_VERSION.jar

LIB_PATH=$LIBS_DIR/$JSON_JAR
LIB_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$SERVLET_JAR
LIB_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$JETLANG_JAR
LIB_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$WEBSOCKET_JAR
LIB_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$VELOCITY_JAR
LIB_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$APACHE_COLLECTIONS_JAR
LIB_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$APACHE_LANG3_JAR.jar
LIB_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$APACHE_CLI_JAR.jar
LIB_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$SLF4J_JAR


for jar in ${JETTY_JARS[@]}; do
	LIB_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$jar-$JETTY_VERSION.jar
done

for jar in ${JETTY_WEBSOCKET_JARS[@]}; do
	LIB_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$jar-$JETTY_VERSION.jar
done

for jar in ${LOG4J_JARS[@]}; do
	LIB_PATH=$LIB_PATH$PATH_SEPARATOR$LIBS_DIR/$jar-$LOG4J_VERSION.jar
done
