#!/bin/bash

## script for running a scala REPL that has all the necessary libraries
## on the class path.


if [ "${GATE_HOME}" == "" ]
then
  echo Environment variable GATE_HOME not set
  exit 1
fi

PRG="$0"
CURDIR="`pwd`"
# need this for relative symlinks
while [ -h "$PRG" ] ; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`"/$link"
  fi
done
SCRIPTDIR=`dirname "$PRG"`
SCRIPTDIR=`cd "$SCRIPTDIR"; pwd -P`
ROOTDIR=`cd "$SCRIPTDIR"/..; pwd -P`

## Find scala: if it is on the path, use that one, otherwise use SCALA_HOME
## or fail.
SCALAPGM=`which scala`
if [ "$SCALAPGM" == "" ] 
then
  if [ "$SCALA_HOME" == "" ]
  then
    echo scala not in path and SCALA_HOME not set
    exit 1
  else
    SCALADIR="$SCALA_HOME"
  fi
else
  SCALABIN=`dirname "$SCALAPGM"`
  SCALADIR=`cd "$SCALABIN"/..; pwd -P`
fi

${SCALADIR}/bin/scala -cp $ROOTDIR/LearningFramework.jar:$ROOTDIR/lib/'*':${GATE_HOME}/bin/gate.jar:${GATE_HOME}/lib/'*'  -i $ROOTDIR/bin/scalainit.scala 
