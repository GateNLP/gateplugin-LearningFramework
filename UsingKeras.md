# Using Keras

IMPORTANT NOTE: Using Weka is currently only possible on Linux. For OS X or Windows, the steps below may work if some form of linux compatibility is available (e.g. using Cygwin on Windows), but no instructions for how to do this are provided yet.

Keras is a separate software implemented in Python and in order to get used from the LearningFramework,
a wrapper software, keras-wrapper needs to be installed (see below).

Keras can currently only be used for classification and regression, sequence tagging is not supported yet.

## Installation of the keras-wrapper

TBD

## Using the keras-wrapper

TBD

Miscellaneous Notes:
* to disable/enable GPU usage with Theano, use the Theano config flags, e.g. `export THEANO_FLAGS='device=cpu'`, see http://deeplearning.net/software/theano/library/config.html
* to configure the back-end, use `export KERAS_BACKEND=tensorflow`, see https://keras.io/backend/