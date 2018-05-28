# Feature Specification File

The feature specification file is a file that describes how machine learning features should get constructed from the GATE document and the annotations present in a GATE document. "Machine learning features" are a different from the features in a GATE feature map. Roughly, the machine learning features are key/value pairs which are created in a certain way for each machine learning example/instance that should get added to the training set during training time or should get classified during application time. These key/value pairs can be created from the original document text, from annotation features from the feature map of the instance annotation, from features from the feature map of annotations that precede, follow or overlap with the instance annotation and various other ways. The feature specification file describes most of how this is done, while the instance annotation parameter in the PR describes which annotation is used to start with (see more about this below).

Machine learning features are sometimes also called "attributes" or "independent variables". This can be a bit confusing because annotation features are converted to machine learning features and depending on the type of value, one annotation feature can be converted and represented through several different attributes or variables for the machine learning algorithm. Some of the details of how this is done is included in the description of the feature specification settings below.

In addition to the examples in this page, example feature specification files for the two task types are included in the [tutorial](Tutorial).

## A rough overview of how machine learning features / attributes are created

At training time, the training PR processes document by document in the training corpus. For each document, the PR processes all "instance annotations", i.e. all annotations which are from the input annotation set and have the type specified as the instance annotation type.
The PR then gets the "target" value from the instance annotation. This is the value that the model we train should learn to predict. In addition, the PR extracts the machine learning features, these are the values for each training example from which the model should learn to predict the target. The machine learning features can come from:
* the feature map of the instance annotation
* feature map of annotations overlapping with the instance annotation
* the feature map from instance annotations preceding or following the current instance annotation or from annotations overlapping with those other instance annotations.

The original values for which machine learning features can be created can be numeric, boolean, strings, lists or maps. Each of these values gets converted into one or more machine learning features (attributes) and the way this is done can be influenced by the settings in the feature specification file.

At application time, the machine learning features are extracted in almost the same way, however, there is no target value to extract because this is what the trained model will predict from the extracted features.

Note that very often the quality of the trained model depends on which annotation features are available for the machine learning algorithm and how exactly they get converted into machine learning features.

## XML representation of the Feature Specification File

The Feature Specification file is an XML file that should be encoded in UTF-8. It can have any root element, but something like `<LearningFramework>` is recommended for clarity.

Nested within the root element there must be 1 or more attribute specification elements. An attribute specification is one of the elements described below and contains in turn nested elements that further describe each attribute specification. The case of the element names does not matter (e.g. `<ATTRIBUTE>` and `<attribute>` and `<aTTriButE>` all work equally)

### `<ATTRIBUTE>`

This describes a feature that is taken from the instance annotation or from an overlapping annotation of some specified type if the `<TYPE>` element is present. If the `<TYPE>` element is present and is identical to the instance annotation type specified as PR parameter, then the features are taken from the original instance annotation. If the `<TYPE>` element is present and different from the instance annotation type specified for the PR, then the first annotation of that type that is overlapping with the instance annotation is used. If there are several such annotations, the longest is chosen, if there are several of those, a random one is selected. If there is no overlapping annotation, no feature is created for that instance.

NOTE: usually the annotations overlapping with the instance annotation should be contained within the instance annotation or even be coextensive with the instance annotation and the user should make sure that all annotations are created in a way that is useful for the learning algorithm.


The `ATTRIBUTE` element can have the following nested elements (see below for a more detailed explanation of some of these):
* `TYPE` (optional) the annotation type of annotations to get the feature from; if missing or the type specified is identical to the instance annotation type, the instance annotation is used
* `FEATURE` (optional) the feature name, if missing, the presence of an annotation is indicated by a boolean feature.
* `DATATYPE` (required) the data type, one of `nominal`, `numeric`, `boolean`/`bool`. If `FEATURE` is missing, then the datatype must be `boolean` or can be missing to indicate `boolean`
* `NAME` the attribute name to use internally. The LF usually provides a useful default, but this can be used to make information about trained models (decision trees, feature weights etc) more readable
* `CODEAS` (optional) the way how the value of the attribute should get encoded. This is only relevant for non-numeric features and must be one of `one_of_k` or `number`. Non-nominal is always encoded as a number, the default for nominal is `one_of_k`.
* `MISSINGVALUETREATMENT` (optional) how to handle situations where the value of the attribute is null or there is no annotation from which to take the value. One of `keep`, `zero_value`, or `special_value`. The default for nominal values encoded as `one_of_k` is `keep`, for boolean values is `zero_value` (identical to false) and for all other data types is `special_value`.
* `<FEATURENAME4VALUE>` (optional): only allowed for datattype `nominal` and codeas `one_of_k`. The name of a feature which contains the value to assign to the attribute, instead of 1.0. This can be used e.g. to assign a TF*IDF score or some other score to each word. If the feature is not present or null, then no machine learning feature is created.
* `LISTSEP` (optional) a list separator string to use for nominal values which contain a string representing a list. In this case each element in the list is used as a separate indicator feature (a feature is generated for each element in the list and set to 1.0)
* `WITHIN` (optional): an annotation type that represents some kind of sequence within which the attribute should occur. If this is specified, then only annotations which are within the same `WITHIN` annotation as the instance annotation will be used. In addition, if the beginning of the instance annotation is the same as the beginning of the `WITHIN` annotation, a special `START` symbol will be added, and if the end of the the instance annotation is the same as the end of the `WITHIN` annotation a special `END` symbol will be added. This can help the learning algorithm to detect situations where a word occurs at the beginning or end of a sentence, for example. IMPORTANT: the `WITHIN` element should only be specified for one attribute or attributelist feature if there are several since it is probably redundant and not helpful if the stop symbol attribute is generated more than once. 

A value of `one_of_k` forthe `CODEAS` element means that for each possible value of the annotation feature, a separate attribute (machine learning feature) is created, and that feature is set to 1.0 if that value is present and 0.0 if the value is not present. If instead `number` is used, then every possible value is mapped to a different integer value. `one_of_k` coding is normally used to represent words or tokens or other nominal features derived from words.

Note that if the datatype is `nominal` and the coding is `one_of_k` then if the value of a feature is an array, an indicator feature for each element of the array is generated, if the value of a feature is a map than an indicator feature for each key/value combination in the map is generated.

If the datatype is `numeric` the LF makes an attempt to convert any scalar type to a number, e.g. Boolean values are converted as expected and a String is converted to a number, if possible (if not possible, 0.0 is used). If the value is an array of doubles or an Iterable, then each element is converted to its own feature and "spliced" into the final feature vector. This can be used to add values from dense vectors
like embeddings to the machine learning features.

Here is an example of an attribute specification, in which the value of the feature "string" of annotations of type "Token" is used:

    <ATTRIBUTE>
    <TYPE>Token</TYPE>
    <FEATURE>string</FEATURE>
    <DATATYPE>nominal</DATATYPE>
    </ATTRIBUTE>

### `<ATTRIBUTELIST>`

This represents a whole ordered list of features, one list for each instance annotation. The annotations of the specified type are ordered by offset. The one overlapping with the instance is element "0" in the attribute list, the one before is element "-1", the one after is element "1" and so on. The `<FROM>` and `<TO>` elements give the start and end element indices of how many annotations to use.

As with the `<ANNOTATION>` specification, if the `<TYPE>` element is missing, or identical to the
instance annotation type, then the instance annotation type
specified in the PR is used. Whichever type is used, the annotations of this type should not overlap among each other. The feature extraction code does not check if annotations are actually strictly forming a sequence in the document!


All settings from the `<ATTRIBUTE>` specification can be used plus:
* `<FROM>` the starting number of the element in the list of annotations to generate features from
* `<TO>` the ending number of the element in the list of annotations
* all the parameters from the `<ATTRIBUTE>` specification

The element numbers are zero based and increasing for annotations which start to the right of the
start offset of the instance annotation and -1 based and decreasing for annotations which start to
the left of the start offset of the instance annotation.

### `<NGRAM>`

This creates features for all annotations contained within the span of the instance annotation
and optionally combines sequences of successive N values into N-grams.

* `<TYPE>` must be present and describes the annotation that is expected to be contained
within the range of the instance annotation. For example, if the instance annotation is
"MovieTitle" and the type of the NGRAM specification is "Token", then some feature from
all the Token annotations contained in a MovieTitle will be combined into N-grams.
* `<NUMBER>` the N in N-gram, i.e. the number of values from features from successive
annotations to combine. For example if NUMBER is 2, and "MovieTitle" is
"Dr. Strangelove or: How I learned to Stop Worrying and Love the Bomb" then tokens
"How" and "I", "I" and "learned" and all other pairs will be used to combine the feature values.
If NUMBER is "1", unigrams are used. If this is not specified, "1" is used.
* `<FEATURE>` the feature to use to get the value for each element of an NGRAM.
* `<FEATURENAME4VALUE>` the name of a feature which contains the value to assign to each occurrence of a unigram. By default each individual unigram will get the value 1.0 assigned and the final feature for a unigram is the count of how often the unigram occurs within the instance. If this is specified, the value of that feature is used instead of 1.0 for each unigram. For ngrams where n is greater than 1, the value used for each ngram is the product of the value for each unigram before accumulation, and the final feature value is the sum of all ngram values for each of the ngrams that occur in the instance.

### NOTES

"Not creating a feature for an instance": machine learning features are created dynamically as they are encountered. For example, if an `<attribute>` specification specifies a type but the training set contains no annotation of that type that overlaps with any of the instance annotations, then the feature or features from that specification are never created and not in the training set. However if some instances contain an overlapping annotation and others do not, then a feature which is created for one instance is not created for a different instance. In that case, the feature is in the training set but will be treated as a "missing value" in which case what happens exactly depends on the missing value treatment specified for that attribute and on the learning algorithm (because some missing value treatments are not supported by some learning algorithms). The default is in most cases that a missing value is treated like the value zero (0.0).

## Machine Learning Feature Names

The internal feature names used for machine learning are generated from the annotation type, feature map feature name, attribute specification type, the details specific to an attributelist or ngram attribute and possibly the actual value encoded in a one-of-k fashion. They follow the following scheme:

* The first part is the "attribute name" which is either the annotation type combined with the feature map feature name and separated by "┆" or the value of the `<NAME>` element of the attribute specification. If the feature is taken from the instance the annotation type is empty. If no feature map feature is specified, the feature name is empty. Examples are "Mention┆string", "┆category", "Token┆", or "myCoolFeature1".
* The "attribute name" is followed by the separator character "╬"
* This is followed by the attribute specification type: "A" for `<ATTRIBUTE>`, "L" for `<ATTRIBUTELIST>` or "N" for `<NGRAM>`
* For an attributelist, this is followed by the element number, an integer, if negative it includes the minus character
* For an ngram, this is followed by the "n" of ngram, e.g. "2" for 2-grams.
* If this feature represents the one-of-k coding of a nominal value, then this is followed by "═" (this is a different Unicode character than the normal equal sign!) followed by the value. If the value is an ngram with n>1, then the parts of the ngrams are separated by "┋"
* If this attribute represents a special value like a START or STOP symbol, the structure of the attribute name is the same as for a `one-of-k` coded nominal value, but the value is the String that represents the special value, e.g."╔START╗".