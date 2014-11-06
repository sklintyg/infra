#!/bin/sh

export INTYG_HOME=`pwd`/../..

cd $INTYG_HOME/common/build/tools
mvn clean install
if [ $? != 0 ]; then exit 1; fi

cd $INTYG_HOME/common/pom
mvn clean install
if [ $? != 0 ]; then exit 1; fi

cd $INTYG_HOME/common/support
mvn clean install
if [ $? != 0 ]; then exit 1; fi

cd $INTYG_HOME/common/web
mvn clean install
if [ $? != 0 ]; then exit 1; fi

cd $INTYG_HOME/common/util/logging-util
mvn clean install
if [ $? != 0 ]; then exit 1; fi

cd $INTYG_HOME/common/util/integration-util
mvn clean install
if [ $? != 0 ]; then exit 1; fi
