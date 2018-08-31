#!/bin/bash

## Args we should get
metafile="$1"
shift
modelbase="$1"
shift

if [[ "x$modelbase" == "x" ]]
then
  echo 'Error: two parameters required: metafile and modelprefix' >&2
  exit 1
fi
datadir=`dirname $metafile`
datadir=`cd $datadir; pwd -P`

wrapperdir=$datadir/FileJsonPyTorch
wrappertrain=$wrapperdir/gate-lf-pytorch-json/train.py

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

export PYTHONPATH="$wrapperdir/gate-lf-python-data:$wrapperdir/gate-lf-pytorch-json"

echo 'MODEL BASE NAME = ' $modelbase >&2
echo 'META FILE       = ' $metafile  >&2
echo 'DATA DIR        = ' $datadir   >&2
echo 'WRAPPER SCRIPT  = ' $wrappertrain >&2
echo 'ADDITIONALPARMS = ' "$@"       >&2
echo 'PYTHON          = ' $wherepython >&2
echo 'PYTHONPATH      = ' $PYTHONPATH >&2
echo 'RUNNING         = ' ${wherepython} "${wrappertrain}" "${metafile}" "${modelbase}" "$@"  >&2

${wherepython} "${wrappertrain}" "${metafile}" "${modelbase}"  "$@" 

