# Pytorch Backend

If you choose one of the algorithms starting with `PytorchWrapper_` the LearningFramework
uses a Python-based neural network and the PyTorch package to create and train a 
neural network model on your data.

NOTE: in order to use this, the [Python environment needs to get set up first](Preparation)

At training time, when you use `LF_TrainClassification` or `LF_TrainChunking` the following
happens:
* the LearningFramework copies the python software needed into your data directory into a subdirectory with the name `FileJsonPytorch`
* IMPORTANT: this software directory is NEVER overridden by the LearningFramework once it is there! This is done to ensure that the user can modify the neural network implementations any way needed for the 
learning project and so that the same version of the software is always used for the task. 
In order to deliberately use a newer version, the directory has to get deleted manually!
* the PR converts the training instances found in the documents of your corpus into a data file 
  (`crvd.data.json`) and a meta-file (`crvd.meta.json`) which are stored in the data directory.
* Some additional files are created which describe the learning problem for use by the application PR.
* Once all documents have been processed, the PR checks if there is configuration file `FileJsonPyTorch.yaml` which [can be used to configure the wrapper](WrapperConfig).
* The python-based backend for creating and training a neural network is started by the PR. Any parameters
  specified in the the PR's `algorithmParameters` field are passed to this program
* The neural network is trained on the data until some kind of conversion or termination criteria 
  occurs. The best network model is stored to a group of files (starting with `FileJsonPytorch.model`)
   in the data directory.
* NOTE: since training a neural network can be a long process it is possible to defer this step 
  to be done later from the command line by specifying the paramter "--notrain" in the `algorithmParameters` field of the PR.

At application time, when you use `LF_ApplyClassification` or `LF_ApplyChunking` the following happens:
* If it does not already exist, the LearningFramework copies the python software needed into your data directory into a subdirectory with the name `FileJsonPytorch`
* the PR starts the application program in the software directory and establishes a connection (through a pipe)
* the PR processes all the documents in the corpus and passes all the instances in the documents to the 
  application program. For each instance it receives the prediction and applies it to the document.


## `PytorchWrapper_CL_DR` and `PytorchWrapper_SEQ_DR` parameters


