# LF_ApplyTopicModel Processing Resource

This model uses a trained LDA topic model to assugn topic distributions to documents/instances.
As for the training algorithm, a "document" for the topic model algorithm may be identical to a
GATE document but can also be just the text under an instance annotation, represented by the
token annotation type and optional feature.

* `algorithmParameters` (String, default: empty) - parameters to pass on to the LDA application algorithm. Note that this depends on the algorithm that has been used for training.
* `dataDirectory` (file URL, default: no default) where the model is stored and where to save any additional report files
* `inputASName` (String, default: empty = default annotation set) Annotation set containing any instance annotations and the token annotations
* `instanceType` (String, default: empty=use "Document") - if specified, the annotation type used to cover the text that corresponds to one document for the LDA algorithm, if not specified, use the whole GATE document
* `tokenAnnotationType` (String, default: Token) - annotation type used to identify the document words/tokens
* `tokenFeature` (String, default: string) - feature containing the token text for each token, if missing, use the cleaned text of the underlying document text

If the `instanceType`  parameter is empty, then the algorithm will check if the input annotation set contains any "Document" annotations and if yes, will use them. If there are no Document annotations, the algorithm will create one Document annotation that covers the whole GATE document and use that.

The following features are set in each of the "Document" or instanceType annotations:
* `LF_MBTopicsLDA_MLTopic`: integer, most prominent/likely topic for this document
* `LF_MBTopicsLDA_MLTopicProb`: float, the probability of the most likely topic for this document
* `LF_MBTopicsLDA_TopicDist`: a list of as many float values as there are topics, representing the probabilities for each of the topics in the document.


## AlgorithmParameters

### Algorithm `MalletLDA_CLUS_MR`

In addition to the annotations and features in the annotations created by the PR, the following
files are written to the data directory:

(TBD/NOT YET)

### Algorithm `GensimLDA_CLUS_DR`
