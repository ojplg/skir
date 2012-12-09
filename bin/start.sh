#!/bin/sh

echo "Starting ... "

PROJECT_HOME=../

DEPS_DIR=~/java_stuff
JETTY_DIR=$DEPS_DIR/jetty-distribution-7.6.3.v20120416/lib
JETTY_DEPS=$JETTY_DIR/jetty-http-7.6.3.v20120416.jar
JETTY_DEPS=$JETTY_DEPS:$JETTY_DIR/jetty-server-7.6.3.v20120416.jar
JETTY_DEPS=$JETTY_DEPS:$JETTY_DIR/jetty-util-7.6.3.v20120416.jar
JETTY_DEPS=$JETTY_DEPS:$JETTY_DIR/servlet-api-2.5.jar
JETTY_DEPS=$JETTY_DEPS:$JETTY_DIR/jetty-io-7.6.3.v20120416.jar
JETTY_DEPS=$JETTY_DEPS:$JETTY_DIR/jetty-continuation-7.6.3.v20120416.jar
JETTY_DEPS=$JETTY_DEPS:$JETTY_DIR/jetty-websocket-7.6.3.v20120416.jar

DEPS=$JETTY_DEPS

CLASS_PATH=$PROJECT_HOME/out/production/risk:$DEPS

echo $CLASS_PATH

java -cp $CLASS_PATH play.Risk
