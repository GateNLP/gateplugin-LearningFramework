@ECHO OFF

SET ROOTDIR=%WRAPPER_HOME%
SET model=%1
shift

: create var with remaining arguments
set r=%1
:loop
shift
if [%1]==[] goto done
set r=%r% %1
goto loop
:done

if [%PYTHON_BIN%]==[] goto nopython
%PYTHON_BIN% %ROOTDIR%\FileJsonPyTorch\gate-lf-pytorch-json\apply.py %model% %r%
goto exit
:nopython
python %ROOTDIR%\FileJsonPyTorch\gate-lf-pytorch-json\apply.py %model% %r%
:exit
