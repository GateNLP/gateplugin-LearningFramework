![GATE](https://gate.ac.uk/plugins/gau-0.1/images/logo-gate.png)

# GATE Learning Framework Plugin

[NOTE: the documentation is still work in progress! This documentation also usually reflects the very latest development version so the latest released version or the version available in the GATE plugin manager may be slightly different.]

The Learning Framework is GATE's most recent machine learning plugin. It's still under active development, but stable enough to use. However future versions may introduce changes which may not be backwards compatible (meaning that pipelines may only work with the older versin or saved models may not be compatible between versions)

It offers a wider variety of more up to date ML algorithms than earlier machine learning plugins, currently the following is supported natively (directly integrated in the plugin code):
* most [Mallet]( http://mallet.cs.umass.edu/) classification algorithms
* Mallet's CRF implementation
* [LibSVM](https://www.csie.ntu.edu.tw/~cjlin/libsvm/), using the java implementation of the original LibSVM code.

In addition, a growing number of machine learning libraries and tools are integrated using __wrappers__: this means that wrapper software (which needs to be downloaded and installed separately) runs the algorithms for training and application and the LearningFramework communicates with the wrapper software from a different process. This solution is used for two reasons: 1) the license of the machine learning library or tool is not compatible with the license of the LearningFramework (e.g. Weka) and 2) the machine learning tool is written in a different language, e.g. Python (e.g. Keras, SciKit-Learn).

The following libraries and tools are available in the LearningFramework through a wrapper:
* [Weka](http://www.cs.waikato.ac.nz/ml/weka/) through the [weka-wrapper](https://github.com/GateNLP/weka-wrapper), see  [Using Weka](UsingWeka)
* [SciKit-Learn](http://scikit-learn.org/stable/) through the [sklearn-wrapper](https://github.com/GateNLP/sklearn-wrapper), see [Using SciKit Learn](UsingSklearn)
* [CostSensitiveClassification](http://albahnsen.com/CostSensitiveClassification/index.html) through the [sklearn-wrapper](https://github.com/GateNLP/sklearn-wrapper), see [Using CostCla](UsingCostCla)
* [Keras](https://keras.io/) through the [keras-wrapper](https://github.com/GateNLP/keras-wrapper), see [Using Keras](UsingKeras)

Finally, the application of a trained model can also be performed via the use of a HTTP model application server. The LearningFramework supports a very simple HTTP protocol for sending feature vectors to the server in JSON format, getting back the model predictions and applying them to the document that is being processed. See [[ServerForApplication]]

The Learning Framework implements different task modes:
* Classification, which simply assigns a class to each instance annotation. For example, each sentence might be classified as having positive or negative sentiment, each word may get assigned a part-of-speech tag, or a document may be classified as being relevant to some topic or not. With classification, the parts of text are known in advance and assigned one out of several possible class labels.
* Sequence tagging, also called Chunking, which finds mentions, such as locations or persons, within the text, i.e. the relevant parts of text are not known in advanced but the task is to find them.
* Regression, which assigns a numerical target, and might be used to rank disambiguation candidates, for example. This is similar to classification in that the relevant parts of text (sentences, words, ...) are known in advance, but instead of a nominal class label, a numeric value is assigned to those parts.
* Exporting of training data in various formats, including ARFF, CSV, TSV, MatrixMarket
* Evaluation (only for some algorithms)

These are provided in separate processing resources (PRs), with separate PRs for training and application and evaluation plugins for classification and regression.
Get started [here](GettingStarted)!

### Feature Overview

* Supports classification, regression, sequence tagging
* Supports learning algorithms from: LibSVM, Mallet, Weka (using a wrapper software) and Scikit-Learn (using a wrapper software)
* Supports various ways of handling missing values
* Supports coding of nominal values as one-of-k or as "value number"
* Supports instance weights
* Supports per-instance classification cost vectors instead of the class label for classification for per-instance cost aware algorithms (however none working yet)
* Supports limiting attribute lists to only those annotations which are within another containing annotation
* Supports using pre-calculated scores for one-of-k coded nominal values, e.g. pre-calculated TF\*IDF scores for terms or ngrams (for n-grams with n>1 the final score is calculated as the product of the individual pre-calculcated gram scores)
* Supports multi-valued annotation features for one-of-k coded nominal attributes: for example if the annotation feature is a List<String>, a dimension / feature is created for each element in the list
* Supports multi-valied annotation features for numeric attributes: in this case the elements (which must be doubles or must be convertible to doubles) are "spliced" into the final feature vector (e.g. for making use of pre-calculated word embeddings).


### Processing Resources:
* [LF_TrainClassification](LF_TrainClassification) train a classification model
* [LF_ApplyClassification](LF_ApplyClassification) apply a trained classification model
* [LF_TrainRegression](LF_TrainRegression) train a regression model
* [LF_ApplyRegression](LF_ApplyRegression) apply a trained regression model
* [LF_TrainChunking](LF_TrainChunking) train a model for sequence tagging / chunking
* [LF_ApplyChunking](LF_ApplyChunking) apply a trined model for sequence tagging / chunking
* [LF_Export](LF_Export) export a training set to an external file
* [LF_EvaluateClassification](LF_EvaluateClassification) estimate classification accuracy
* [LF_EvaluateRegression](LF_EvaluateRegression) estimate regression quality

### Other important documentation pages:
* [UsingWeka](UsingWeka) all about how to use Weka with the LearningFramework plugin.
* [UsingSklearn](UsingSklearn) all about how to use SciKit Learn with the LearningFramework plugin.
* [UsingCostCla](UsingCostCla) all about how to use CostCla (https://github.com/albahnsen/CostSensitiveClassification) with the LearningFramework plugin
* [UsingKeras](UsingKeras) all about how to use Keras (https://keras.io/, a Deep Learning framework built on top of
[TensorFlow](https://www.tensorflow.org/) and [Theano](http://deeplearning.net/software/theano/) with the LearningFramework plugin
* [FeatureSpecification](FeatureSpecification) all about the feature specification file and what it can contain as well as how machine learning features
  are created from the original document annotations
* [AlgorithmParameters](AlgorithmParameters) some general notes about algorithm parameters. Most parameters are documented with the wiki page about the PR where they can be used
* [VectorValues](VectorValues) how to use pre-caluclated dense vectors like embeddings and other vector-valued features
* [SavedFiles](SavedFiles) the files that get saved as a result of training or exporting
* [ServerForApplication](ServerForApplication) describes the interaction with a HTTP server for carrying out the application of trained models
* [API](API) how to use the LearningFramework classes from Java/Scala/Groovy code and examples of how to use it with the [GATE Java Plugin](https://github.com/johann-petrak/gateplugin-Java)
* [FAQs](FAQs)
* [ImplementationNotes](ImplementationNotes) Development notes and notes on how things are implemented in the LearningFramework
