#!/bin/bash

## Args we should get
modelbase="$1"
shift
metafile="$1"
shift
wrapperdir="$1"
shift

wrapperapply=$wrapperdir/apply.py

datadir=`dirname modelbase`
datadir=`cd $datadir; pwd -P`

versionpython="UNKNOWN"
wherepython=`which python`
if [[ "x$wherepython" != "x" ]]
then
  versionpython=`python -V |& cut -f 2 -d " " | cut -f 1 -d'.'`
fi
if [[ "$versionpython" == "3" ]]
then
  pythoncmd=$wherepython
else
  wherepython=`which python3`
  if [[ "x$wherepython" == "x" ]]
  then
    echo 'ERROR: could not find a python 3 interpreter, exiting'
    exit 1
  fi
fi

export PYTHONPATH="$wrapperdir/gate-lf-python-data:$wrapperdir/gate-lf-keras-json"


echo 'MODEL BASE NAME = ' $modelbase >&2
echo 'META FILE       = ' $metafile  >&2
echo 'DATA DIR        = ' $datadir   >&2
echo 'ADDITIONALPARMS = ' "$@"       >&2
echo 'WRAPPER SCRIPT  = ' $wrapperapply >&2
echo 'ADDITIONALPARMS = ' "$@"       >&2
echo 'PYTHON          = ' $wherepython >&2
echo 'PYTHONPATH      = ' $PYTHONPATH >&2
echo 'RUNNING         = ' ${wherepython} "${wrapperapply}" "${modelbase}" "${metafile}" "${datadir}" "$@"  >&2

${wherepython} "${wrapperapply}" "${modelbase}" "${metafile}" "${datadir}" "$@" 

