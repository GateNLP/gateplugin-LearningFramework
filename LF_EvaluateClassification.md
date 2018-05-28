# LF_EvaluateClassification Processing Resource

The classification evaluation PR allows you to obtain a fast accuracy score for a classification problem. This is a helpful place to start in tuning your choice of algorithm, features and parameters. However for a kappa statistic, you will need to create separate training and test corpora, and train and apply using training and application PRs, then use [Corpus QA](https://gate.ac.uk/userguide/sec:eval:corpusqualityassurance). The model isn't saved when you use the evaluation PR. This PR has no init time parameters. Here are the runtime parameters:

* algorithmJavaClass—advanced—allows user to specify an algorithm on the basis of its Java class name
* algorithmParameters—parameters influencing the algorithm, documented either in the library's own documentation or on this wiki
* evaluationMethod—Cross-validation or hold-out?
* featureSpecURL—The xml file containing the feature specification
* inputASName—Input annotation set containing attributes/class
* instanceType—Annotation type to use as instance
* numberOfFolds—For cross-validation, how many folds to split the data into? Ten is common. Higher numbers give a better result, but the evaluation takes longer to run.
* numberOfRepeats—For hold-out evaluation, the evaluation can be repeated across randomized splits and averaged for a more accurate result.
* scaleFeatures—Use a feature scaling method?
* sequenceSpan—For sequence classifiers only--gives the span across which to learn a sequence; for example a sentence is a meaningful sequence of words. More relevant to chunking problems.
* targetFeature—which feature on the instance annotation indicates the class?
* trainingAlgorithm—Which algorithm to use
* trainingFraction—For hold-out evaluation; in splitting off a training portion, what fraction should it be?

For an example feature specification, see the classification training PR documentation.