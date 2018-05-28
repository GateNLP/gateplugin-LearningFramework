# Using SciKit-Learn

IMPORTANT NOTE: Using SciKit-Learn is currently only possible on Linux. For OS X or Windows, the steps below may work if some form of linux compatibility is available (e.g. using Cygwin on Windows), but no instructions for how to do this are provided yet.

SciKit-Learn (http://scikit-learn.org/stable/) is a collection of Machine Learning and other tools, implemented in Python. For this reason SciKit-Learn cannot be directly integrated in the LearningFramework plugin.

Instead, the use of SciKit-Learn is possible by running the training and application programs externally, in a separate process: 
* For training, the training data can be exported using the [[LF_Export]] PR and choosing the EXPORTER_MATRIXMARKET2_CLASS or EXPORTER_MATRIXMARKET2_REGRESSION exporters. Then, a model can separately be trained using SciKit-Learn (see below for how to import the MatrixMarket files into Python for use with SciKit-Learn).
* Alternately, the [[LF_TrainClassification]] or [[LF_TrainRegression]] PRs can be used to automatically export the training data as two MatrixMarket files, and use the sklearn-wrapper (see below) to externally train a model
* For application, the [[LF_ApplyClassification]] or [[LF_ApplyRegression]] PRs use the sklearn-wrapper (see below) to start a separate program which accepts data from the PR, classifies it and sends the classification back the the PR.

## sklearn-wrapper

The sklearn-wrapper (https://github.com/GateNLP/sklearn-wrapper) software is necessary to apply a model or to automatically train a model from inside the LearningFramework.

The following steps are needed to prepare the sklearn-wrapper for use with the LearningFramework:
* Install sklearn-wrapper
* Make sure the sklearn-wrapper commands work on your system
* Tell the LearningFramework how to run the sklearn-wrapper commands

### Installing weka-wrapper

* Get the latest release of the sklearn-wrapper: https://github.com/GateNLP/sklearn-wrapper/releases (Download the file called `sklearn-wrapper-x.y.zip`) or clone the github repository
* if the zip file was downloaded, unzip the zip file somewhere where it will be accessible from the LearningFramework later
* this creates a directory `sklearn-wrapper`. A good location for this directory is parallel to the gateplugin-LearningFramework directory but any location can be configured.
* See also the instructions in the sklearn-wrapper README file.

### Making sure the sklearn-wrapper commands work

From within the directory `sklearn-wrapper`, run the command:

`./bin/sklearnWrapperApply.sh .`

If this shows the error message "ERROR: No model path", sklearn-wrapper should be ready to use. 

### Telling the LearningFramework how to run sklearn-wrapper commands

The LearningFramework needs to be able to run the sklearn-wrapper commands `sklearnWrapperApply.sh` and `sklearnWrapperTrain.sh` in order to use SciKit-Learn properly. For this the LearningFramework needs to know the 
location of where sklearn-wrapper is installed, i.e. the path of the directory (called sklearn-wrapper by default) which was created when the zip file was extracted during installation or where the git repository was cloned. This can be done by setting one of the following to the full path to that directory:
* the environment variable `SKLEARN_WRAPPER_HOME`
* the java property `gate.plugin.learningframework.sklearnwrapper.home`
* the setting `sklearnwrapper.home` in a file `sklearn.yaml` in the data directory used

The setting in `sklearn.yaml` takes precedence over the java property which takes precedence of the environment variable. 

If any of these are set to a relative path, then the LearningFramework will try to interpret that as relative to the data directory used. 

You should also set the environment variable `SKLEARN_WRAPPER_PYTHON` to indicate the command to run python.

## Using Exported Files for Training

When the training data is exported using the [[LF_Export]] with the EXPORTER_MATRIXMARKET2_CLASS or EXPORTER_MATRIXMARKET2_REGRESSION exporters, two files are created in the data directory:
* deps.mtx contains the targets for all instances
* indeps.mtx contains the attributes / independet variables for all instances

Both files are stored in MatrixMarket Coordinate Format (see http://math.nist.gov/MatrixMarket/formats.html)

To import these files for use with SciKit-Learn and train a model the following Python code can be used:
```python
import scipy.io as sio
## depfile = the path of the exported file deps.mtx
## indepfile = the path of the exported file indeps.mtx
deps = sio.mmread(depfile)
indeps = sio.mmread(indepfile)
## model = some learning algorithm e.g. sklearn.svm.SVC()
## sklearn can use the imported sparse matrix directly for the independent variables
## but needs the targets in a different shape and format
targets = deps.toarray().reshape(deps.shape[0],)
model.fit(indeps,targets)
## now store the trained model for later use ...
```