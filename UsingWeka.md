# Using Weka 

IMPORTANT NOTE: Using Weka is currently only possible on Linux. For OS X or Windows, the steps below may work if some form of linux compatibility is available (e.g. using Cygwin on Windows), but no instructions for how to do this are provided yet.

Weka (http://www.cs.waikato.ac.nz/ml/weka/) is a collection of Machine Learning and other tools, implemented in Java. However Weka cannot be directly integrated in the LearningFramework plugin because its license (GPL) is not compatible with the license of other projects used by the plugin, and with the license of the plugin itself. 

Instead, the use of Weka is possible by running everything related to Weka externally, in a separate process (and separate VM): 
* For training, the training data can be exported using the [[LF_Export]] PR and choosing the EXPORTER_ARFF_CLASS or EXPORTER_ARFF_REGRESSION exporters. Then, a model can separately be trained using Weka.
* Alternately, the [[LF_TrainClassification]] or [[LF_TrainRegression]] PRs can be used to automatically export the training data as an ARFF file, and use the weka-wrapper (see below) to externally train a model. When training is done this way, the `algorithmParameters` parameter must be used to choose the Weka algorithm (see below)
* For application, the [[LF_ApplyClassification]] or [[LF_ApplyRegression]] PRs use the weka-wrapper (see below) to start a separate program which accepts data from the PR, classifies it and sends the classification back the the PR.

## Choosing the WEKA algorithm for training

If the [[LF_TrainClassification]] or [[LF_TrainRegression]] PR is used to train a WEKA model from the LearningFramework, then the PR parameters should be set as for other learning algorithms. In addition
the `algorithmParameters` parameter must be set:
* the first entry in the field must be the full class name (including package name) of the WEKA algorithm to use, e.g. `weka.classifiers.bayes.NaiveBayes`
* Anything added to the `algorithmParameters` field after the full class name and a space is added as actual parameters to the training command of WEKA. The possible options that can be used here are documented in the WEKA documentation, and also in the WEKA API documentation, e.g. http://weka.sourceforge.net/doc.dev/weka/classifiers/bayes/NaiveBayes.html shows that the option "-K" can be specified to use a kernel density estimator.

## weka-wrapper

The weka-wrapper (https://github.com/GateNLP/weka-wrapper) software is necessary to apply a model or to automatically train a model from inside the LearningFramework.

The following steps are needed to prepare the weka-wrapper for use with the LearningFramework:
* Install weka-wrapper
* Make sure the weka-wrapper commands work on your system
* Tell the LearningFramework how to run the weka-wrapper commands

### Installing weka-wrapper

* Get the latest release of the weka-wrapper: https://github.com/GateNLP/weka-wrapper/releases (Download the file called `weka-wrapper-x.y.zip`)
* unzip the zip file somewhere where it will be accessible from the LearningFramework later
* this creates a directory `weka-wrapper`

### Making sure the weka-wrapper commands work

From within the directory `weka-wrapper`, run the command `./bin/wekaWrapperApply.sh`.
If this shows an error message about missing parameters, weka-wrapper should be ready to use. 

### Telling the LearningFramework how to run weka-wrapper commands

The LearningFramework needs to be able to run the weka-wrapper commands `wekaWrapperApply.sh` and `wekaWrapperTrain.sh` in order to use Weka properly. For this the LearningFramework needs to know the 
location of where weka-wrapper is installed, i.e. the path of the directory (called weka-wrapper by default) which was created when the zip file was extracted during installation. This can be done by setting one
of the following to the full path to that directory:
* the environment variable `WEKA_WRAPPER_HOME`
* the java property `gate.plugin.learningframework.wekawrapper.home`
* the setting `wekawrapper.home` in a file `weka.yaml` in the data directory used

The setting in `weka.yaml` takes precedence over the java property which takes precedence of the environment variable. 

If any of these are set to a relative path, then the LearningFramework will try to interpret that as relative to the data directory used. 