#!/bin/bash

PROJECT_HOME_DIR=..
LIBS_DIR=$PROJECT_HOME_DIR/lib

#JETTY_VERSION=7.6.21.v20160908
JETTY_VERSION=9.3.14.v20161028
JETTY_JARS=(jetty-http jetty-server jetty-util jetty-io jetty-continuation jetty-servlet jetty-security)
JETTY_WEBSOCKET_JARS=(javax-websocket-server-impl javax-websocket-client-impl websocket-server websocket-common websocket-api websocket-servlet websocket-client)

SERVLET_JAR=servlet-api-3.1.jar
WEBSOCKET_JAR=javax.websocket-api-1.0.jar

LOG4J_VERSION=2.7
LOG4J_JARS=(log4j-core log4j-api)

JSON_JAR=json-simple-1.1.1.jar

JUNIT_VERSION=4.12
JUNIT_JAR=junit-$JUNIT_VERSION.jar

HAMCREST_VERSION=1.3
HAMCREST_JAR=hamcrest-core-$HAMCREST_VERSION.jar

JETLANG_VERSION=0.2.17
JETLANG_JAR=jetlang-$JETLANG_VERSION.jar

VELOCITY_VERSION=1.7
VELOCITY_JAR=velocity-$VELOCITY_VERSION.jar

LIB_PATH=$LIBS_DIR/$JSON_JAR:$LIBS_DIR/$SERVLET_JAR:$LIBS_DIR/$JETLANG_JAR:$LIBS_DIR/$WEBSOCKET_JAR

for jar in ${JETTY_JARS[@]}; do
	LIB_PATH=$LIB_PATH:$LIBS_DIR/$jar-$JETTY_VERSION.jar
done

for jar in ${JETTY_WEBSOCKET_JARS[@]}; do
	LIB_PATH=$LIB_PATH:$LIBS_DIR/$jar-$JETTY_VERSION.jar
done

for jar in ${LOG4J_JARS[@]}; do
	LIB_PATH=$LIB_PATH:$LIBS_DIR/$jar-$LOG4J_VERSION.jar
done
