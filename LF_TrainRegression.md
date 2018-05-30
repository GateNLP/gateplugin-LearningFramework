# LF_TrainRegression Processing Resource

The regression training PR allows you to train a classifier suitable for learning a numerical target, for example learning to predict the star rating a reviewer assigns based on the text of their review. The PR has no init time parameters. Here are the runtime parameters:

* `algorithmParameters` - parameters influencing the algorithm, documented either in the library's own documentation or on this wiki
* `dataDirectory`- Where to save the model
* `featureSpecURL` - The xml file containing the feature specification
* `inputASName` - Input annotation set containing attributes/class
* `instanceType` - annotation type to use as instance
* `instanceWeightFeature` (String, no default, optional) the name of a feature in the instance annotation that contains the instance weight. If this is not specified, no instance weights are collected. If this is specified, then if the feature exists, its value is converted to an instance weight if possible, or an error occurs, if the the feature does not exist, the weight 1.0 is used. This is only relevant for training algorithms that can use instance weights.
* `scaleFeatures` - use a feature scaling method for preparation?
* `targetFeature` - which feature on the instance annotation indicates the class?
* `trainingAlgorithm` - which algorithm to use

Set your instance to the annotation type that you wish to classify, for example "Review". These annotations must already be present in the input annotation set. For learning to predict the star rating assigned by a reviewer based on the text of their review, a good starting point for features would be the words in the review, as in this example feature specification:

    <ML-CONFIG>

    <NGRAM>
    <NUMBER>1</NUMBER>
    <TYPE>Token</TYPE>
    <FEATURE>string</FEATURE>
    </NGRAM>

    </ML-CONFIG>
