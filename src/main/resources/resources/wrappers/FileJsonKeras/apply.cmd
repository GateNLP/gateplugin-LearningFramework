@ECHO OFF

if "x%WRAPPER_HOME%"=="x" (
  set WRAPPER_HOME=%~dp0
)
SET model=%1
shift

: create var with remaining arguments
set r=%1
:loop
shift
if "x%1"=="x" goto done
set r=%r% %1
goto loop
:done

if "x%PYTHON_BIN%"=="x" (
  set PYTHON_BIN="%HOMEDRIVE%""%HOMEPATH%"\Miniconda3\python.exe
)
%PYTHON_BIN% %WRAPPER_HOME%\gate-lf-keras-json\apply.py %model% %r%

