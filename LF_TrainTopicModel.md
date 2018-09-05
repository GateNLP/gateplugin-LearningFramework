# LF_TrainTopicModel Processing Resource

This PR can be used to train an LDA topic model from a training corpus. For this algorithm the `instanceType` parameter specifies the annotation that identifies what should be a "document" as far as LDA is concerned. If the parameter is left empty, then the whole document is used, but if there are more than one such annotations in a GATE document, the LDA algorithm will treat the text covered by each of the instance annotations as individual documents. The text is identified by the annotations of type `tokenAnnotaitonType` and comes either from the `tokenFeature`  specified or the underlying cleaned document text, if `tokenFeature` is left empty.

## Parameters

The algorithm has no init parameters and the following run-time parameters:

* `algorithmParameters` (String, default: none) - algorithm specific parameters, see below
* `applyAfterTraining` (Boolean, default: false) - if this is true, and if the algorithm is `MalletLDA_CLUS_MR` and the PR is the only duplicate and the corpus contains all training documents, then the model will get applied to the original documents/instances in the training set.
* `dataDirectory`- (file URL, default: none) Where to save the model, required
* `inputASName` - (String, default: empty meaning the default annotation set) Input annotation set containing the instance and token annotations
* `instanceType` (String, default: empty, meaning the whole document) - annotation type to use as "instance", i.e. as document. If not specified, the whole document is used
* `tokenAnnotationType` (String, default: Token) - annotation type used to identify the document words/tokens
* `tokenFeature` (String, default: string) - feature containing the token text for each token, if missing, use the cleaned text of the underlying document text
* `trainingAlgorithm` - which algorithm to use (see below)

## Algorithms

### Algorithm `MalletLDA_CLUS_MR`

This algorithm is part of Mallet and directly included with the plugin. It uses an
in-memory representaiton of the training data, so it is limited by the amount of
available RAM.

(NOTE: this algorithm appears to only support symmetric dirichlet priors, but still have to check!)

The following parameters can be specified in the `algorithmParameters` field:
* -t/-topics (integer, default: 10) - the number of topics to use
* -T/-stopics (integer, default: 10) - maximum number of topics to show per document in the topTopicsPerDoc.txt file
* -p/-procs (integer, default: depends on computer) - the number of threads to use for parallel training
* -w/-words (integer, default: 20) - the number of most probably topic words to show for each topic
* -d/-docs (integer, default: 5) - the number of most prominent training documents to show for each topic
* -s (integer, default: 0 which uses the clock) - random seed to use for the gibbs sampler
* -i/-iters (integer, default: 1000) - number of iterations to run, should be larger than burnin (-B/-burn)
* -B/-burn (integer, default: 200) - number of burn-in iterations
* -S/-show (integer, default: 200) - number of iterations after which to show topics
* -a (float, default: 1.0) - alpha prior of the underlying symmetrich dirichlet (so each topic initially gets assined the 1/numbertopics of this value). Higher values allow for more topics per document.
* -b (float, default: 0.01) - beta parameter for topic-word smoothing. Higher values allow for more corpus words per topic.
* -M/-mcmi (integer, default: 0) - if larger than 0, the number of iterations to do topic maximization by iterated conditional modes, do not do this if 0. 
* -o/-opti (integer, default: 50) - number of iterations between reestimating dirichlet hyperparameters, if 0, never estimate.
* -D/-diags (boolean, default: false) - run the topic model diagnostics and save the diagnostics.xml file. This can take a long time and use up a large amount of memory.

If the `applyAfterTraining` parameter is `true` and all conditions for application to run are met,
then after training the model, the topic distributions are applied to each document. This is done by
adding features to the instance annotations as specified throught the `instanceType` parameter or
by using instead any "Document" annotation in the default set, or if none is found, adding one that spans
the whole GATE document. The following features are added:
* `LF_MBTopicsLDA_MLTopic_train`: integer, most prominent/likely topic for this document
* `LF_MBTopicsLDA_MLTopicProb_train`: float, the probability of the most likely topic for this document
* `LF_MBTopicsLDA_TopicDist_train`: a list of as many float values as there are topics, representing the probabilities for each of the topics in the document.
Note that the values of these features can differ from what would be the result of
applying the model using the `LF_ApplyTopicModel` PR, because the latter is the result of a new
iteration of Gibbs sampling. 



In addition to the annotations and features in the annotations created by the PR, the following
files are written to the data directory:
* `diagnostics.xml`: an XML file that contains for each topic a number of per-topic statistics (coherence, document entropy etc) plus a ranked list of words for that topic with per-word statistics (probability, document frequency, coherence etc.)



### Algorithm `GensimWrapper_CLUS_DR`

**NOTE: NOT YET IMPLEMENTED!!**

This is a wrapper around the LDA implementation in the Python Gensim package und uses an out-of-memory
representation of the training data, so it can scale to very large corpora.
