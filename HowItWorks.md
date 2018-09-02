# How it Works

This page gives a short and rather general overview over how tha LearningFramework is designed, how it works
under the hood and how it can best be used.

More detailled notes about implementation details and technical details are
in the [Developer Wiki](https://github.com/GateNLP/gateplugin-LearningFramework/wiki)

## Relevant terms and concepts

* Instance: a representation of an individual item that is getting processed by the ML algorithm: for
  supervised learning, during training, each instance is associated with features and a target value.
  The ML algorithm tries to learn how to map features to the target value. During application, only the
  features are known and the ML algorithm "predicts" the target value.
* Target: the value that is associated with each instance, a nominal value for classification, or
  a numeric value for regression. This value is only known for a limited number of instances (the training
  instances for training, and evaluation instances for evaluating what the ML algorithm has learned)
  but the trained ML model should be able to "predict" the target as well as possible for new instances which
  do not have the target assigned.
* Features: something that can always be determined for the instance (unlike the target which we want to predict)
  Features are often the words or lemmata that occur in or around an instance, sometimes the n-grams
  or information that could be derived for words in earlier processing stages, e.g. if a word is part of
  a gazetteer lookup, the POS tag of a word etc.
* Supervised learning: the ML algorithm tries to learn a mapping from a set of features to a target value
* Unsupervised learning: the ML algorithm looks at instances only and tries to find something interesting out
  about them, e.g. how to group them (clustering) or how each instance belongs to one or more topics
  (topic modelling)
* Classification: a supervised learning task where given input features for an instance, a class label should get
  assigned to the instance
* Regression: a supervised learning task where given a set of input features for an instance, a numeric value should get
  assigned to the instance
* Sequence tagging/chunking: a supervised learning task. Given a sequence of instances, find interesting sub-sequences. For example,
  given the sequence of words in a document, find where a mention of a person starts, continues and ends or
  find where a noun phrase starts, continues and ends.
  Sequence tagging/chunking is internally represented as a classification task: instances are the elements
  (e.g. words) of a sequence (e.g. document or sentence) and the beginning, continuation, and ending of
  a subsequence (e.g. mention of a person) is internally indicated by tags.
* Topic modelling: an unsupervised learning task. Given a large number of instances (e.g. documents), find
  "topics" (distributions of words) and assign a distribution of those topics to each instance.
* Sequence tagging algorithm: most supervised classification algorithms take the input features for a single
  instance and the target for the instance for learning. However, sequence tagging algorithms always take
  a whole sequence of labelled instances for learning. These algorithms try to make use of information
  about how the target of some instance may depend on the target of the preceding instance.

Note:
* A sequence tagging problem can be solved with a sequence tagging algorithm where the algorithm
  processes whole sequences of instances (e.g. Sentences of Tokens) to find the best corresponding
  sequence of targets (e.g. labels indicating where a person mention starts, continues or ends)
  However, a sequence tagging problem can also be solved by a classification algorithm which processes
  each instance separately.
* A classification problem can be solved with a classification algorithm: each instance gets processed
  separately and the algorithm tries to learn a mapping between the features of the instance and the
  class label of the instance. However, a classification problem can often also be solved with a
  sequence tagging algorithm where the algorithm processes a whole sequence of instances and tries
  to learn a mapping to the corresponding sequence of labels. In this case, a sequence has to be
  used. For example, assigning a POS tag to each token is a classification problem that can be handled
  by a classification algorithm for each token separately (potentially using features from the context around
  the token) but it can also be handled by a sequence tagging algorithm for a whole sentence, where
  the algorithm processed all the instances (tokens) of a sentence and tries to find a mapping to
  all the POS-tags for each token in the sentence.

Examples:
* POS tagging: an instance is a token. Features are properties of the token (is it upper-case, which prefix,
    suffix does it have) or of surrounding tokens. The target is the POS tag assigned to the token (e.g. NN)
* Spam detection: an instance is a document representing an email. Features could be the words that occur
    in the document, the kind of links that occur in the document. The target is a flag for each document
    that indicates if it is spam or not spam. Spam detection is a special case of:
* Document classification: an instance is a document. Features could be the words that occur, bigrams that
    occur, number of sentences, occurrence of specific named entities etc. The target is the document class.
* Sentence classification: an instance is a sentence. Features could be the words / some of the words that
    occur in the sentence, bigrams that occur, features derived from a dependency parse etc. Target could
    be if the sentence expresses a positive or negative sentiment.

Here is how some of the concepts/terms above relate to GATE concepts:
* Instance: an instance is represented by an annotation. For example, for POS tagging, this would be a Token
  annotation, for document classification, it would be an annotation that spans the whole GATE document
* Features: for machine learning, a "feature" is any value that can be associated with the instance. This is
  different from the meaning of "feature" in relation to the feature map of a GATE annotation, although the
  value of a feature for machine learning will often come from a feature in a feature map. The ML features
  can come from the feature map of the instance annotation, but also from annotation features of annotations
  which are contained within an instance annotation, or from instance annotations that come before or after the
  current instance annotation. This can be configured using the [Feature Specification File](FeatureSpecification)
* Target: the target value for classification and regression comes from an annotation feature of
  the instance annotation. However for sequence tagging, the sub-sequences are identified by annotation types
  (e.g. annotations of type "Person" for subsequences of tokens which are mentions of persons) and the
  LearningFramework automatically converts the spans of those annotations into the labels for each instance
  internally.

## Learning algorithms included in the LearningFramework

The LearningFramework provides a range of algorithms, usually several different algorithms
for a learning taks. Currently the following groups of algorithms are available:

* Mallet-based: these are algorithms from the [Mallet](http://mallet.cs.umass.edu/) machine learning library, implemented
  in Java and directly included in the plugin.
* LibSVM-based: this is based on the Java implementation of the [LibSVM](https://www.csie.ntu.edu.tw/~cjlin/libsvm/)
  library and directly included in the plugin.
* Sklearn-based: this is based on the Python library [Scikit-Learn](http://scikit-learn.org/stable/) and can be
  used through a wrapper. In order to use the algorithms, Python, the Scikit-Learn library, the wrapper
  must be installed in addition to the plugin
* Weka-based: this is based on the Java software [Weka](https://www.cs.waikato.ac.nz/ml/weka/) and can be used
  through a wrapper. The software is NOT directly included in the plugin for licensing reasons.
  In order to use these algorithms, weka and the weka-specific wrapper must be installed in addition to the plugin.
* PytorchWrapper (DR): this is based on the Python library [Pytorch](https://pytorch.org/) and can be used through a wrapper.
  In order to use this, Python and the Pytorch library must be installed (the wrapper is included in the plugin and
  does not have to be installed separately)
* KerasWrapper (DR): this is based on the Python library [Keras](https://keras.io/) and can be used through a wrapper.
  In order to use this, Python and the Keras library must be installed (the wrapper is included in the plugin and
  does not have to be installed separately)
* KerasWrapper (MR): this is a limited wrapper for Keras which is integrated in a different way from KerasWrapper (DR)
  and uses a different instance representation (see below). This wrapper is likely to get removed in the future.

## Representation of instances

For training, the LearningFramework processes each document and creates an internal
representation of each instance from the features extracted from the document. Which internal
representation is used depends on the learning algorithm used.

Currently the following representations are implemented:
* Mallet Representation (MR): this represents each instance as a sparse numeric vector and uses
  the sparse vector representation of the Mallet library for this. All instances are kept in memory,
  so the size of a training corpus is limited by the amount of memory available to keep all
  the sparse vector representations of the instances in the corpus in memory.
  Sequence tagging problems are represented as sequences of sparse feature vectors, in memory.
  All algorithm that have a name in GATE that ends in `_MR` use this representation. These are:
  * all Mallet-based Algorithms
  * LibSVM-based Algorithms (Note: the Mallet-representation is converted to a LibSVM specific representation
    before training is carried out, so the size of the corpus is limited by fitting both representations at
    some point into memory)
  * Sklearn-based Algorithms (Note: for training the Mallet-representation is exported to a different sparse representation
    in a file which is subsequently used by Sklearn)
  * Weka-based Algorithms (Note: for training the Mallet-representation is exported to sparse ARFF format in a file which
    is subsequently used by Weka)
  * The `KerasWrapper_CL_MR`, an old Keras wrapper which may get removed in the future: for training this
    writes the Mallet-representatin to a file which is then used by the wrapper.
* Dense JSON Representation (DR): All algorithms that have a name in GATE that ends in `_DR` use this representation.
  Currently these are the two Neural-Network wrappers, PytorchWrapper and KerasWrapper.
  This representation writes instances to a file using a JSON-based, dense representation. This representation
  still represents nominal values in their original string form (e.g. token strings). Because the instances
  get written to a file, processing and converting is slower, but there is no memory restriction and corpora
  of arbitrary size can be processed as long as there is enough free disk-space
