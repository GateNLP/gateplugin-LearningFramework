#!/bin/bash

## Args we should get
modelbase="$1"
shift
metafile="$1"
shift
datadir="$1"
shift

echo 'MODEL BASE NAME = ' $modelbase >&2
echo 'META FILE       = ' $metafile  >&2
echo 'DATA DIR        = ' $datadir   >&2
echo 'ADDITIONALPARMS = ' "$@"       >&2
echo 'RUNNING         = ' python3 "${datadir}"/apply.py "${modelbase}" "${metafile}" "${datadir}" "$@"  >&2

python3 "${datadir}"/apply.py "${modelbase}" "${metafile}" "${datadir}" "$@" 
