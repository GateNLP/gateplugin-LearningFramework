# Pytorch/Keras Wrapper Configuration file

The wrapper configuration file is a YAML (see https://yaml.org/) format file
that can be used to configure the details of how GATE invokes the wrapper scripts
for training and application.

The file name should be "FileJsonPyTorch.yaml" for the PyTorch wrapper and "FileJsonKeras.yaml" for the Keras wrapper
and the file should be placed into the directory that is configured as the "Data Directory".

Here is an example content of a configuration file:
```yaml
shellcmd: /bin/bash
shellparms: -x -v
PYTHON_BIN: /usr/bin/python3
```

Generally, variables which are all upper case correspond to environment variables while 
other variables are used elsewhere. 

The following variables are currently supported:
* shellcmd: the shell program to invoke to run the script that invokes the backend. 
* shellparms: the parameters to pass on to the shell program, only used if a shellcmd is specified
* `PYTHON_BIN`: the path of the Python 3 interpreter to use. This will be set as an environment variable
  for the invocation script and used by the invocation script in place of an expected "python" command on the
  binary path.
