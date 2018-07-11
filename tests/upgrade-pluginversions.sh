#!/bin/bash

tsvfile="$1"
if [[ "x$tsvfile" == "x" ]]
then
  echo Need one parameter, the name of the upgrade tsv file
  exit 1
fi

if [ "x${GATE_HOME}" == "x" ]
then
  echo Environment variable GATE_HOME not set
  exit 1
fi

if [[ -f "${GATE_HOME}/gate.classpath" ]]
then
  gatecp=`cat "${GATE_HOME}/gate.classpath"`
else
  if [[ -d "${GATE_HOME}/lib" ]]
  then
    gatecp="${GATE_HOME}/lib/"'*'
  else
    echo Could not find $GATE_HOME/gate.classpath nor $GATE_HOME/lib
    exit 1
  fi
fi


find . -name '*.gapp' -o -name '*.xgapp' | while read f
do
  echo "Updating file $f"
  java -classpath "$gatecp" gate.util.persistence.UpgradeXGAPP "$f" "$tsvfile"
done
