#!/bin/bash

## Args we should get
modelbase="$1"
shift
metafile="$1"
shift
wrapperdir="$1"
shift

wrapperapply=$wrapperdir/gate-lf-pytorch-json/apply.py

datadir=`dirname modelbase`
datadir=`cd $datadir; pwd -P`

if [[ -z "${PYTHON_BIN}" ]]
then
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
      echo 'ERROR: could not find a python 3 interpreter, exiting' >&2
      exit 1
    fi
  fi
else
  wherepython="${PYTHON_BIN}"
fi

export PYTHONPATH="$wrapperdir/gate-lf-python-data:$wrapperdir/gate-lf-pytorch-json"


#echo 'PYTHON_BIN      = ' ${PYTHON_BIN} >&2
#echo 'MODEL BASE NAME = ' $modelbase >&2
#echo 'META FILE       = ' $metafile  >&2
#echo 'DATA DIR        = ' $datadir   >&2
#echo 'ADDITIONALPARMS = ' "$@"       >&2
#echo 'WRAPPER SCRIPT  = ' $wrapperapply >&2
#echo 'ADDITIONALPARMS = ' "$@"       >&2
#echo 'PYTHON          = ' $wherepython >&2
#echo 'PYTHONPATH      = ' $PYTHONPATH >&2
#echo 'RUNNING         = ' ${wherepython} "${wrapperapply}" "${modelbase}" "$@"  >&2

if ${wherepython} "${wrapperapply}" "${modelbase}" "$@"  ; then
  echo 'PROCESSING OK ' $? >&2
  exit 0
else
  echo 'PROCESSING ERROR ' $? >&2
  exit 127
fi

