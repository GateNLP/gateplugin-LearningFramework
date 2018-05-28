# LF_ApplyClassification Processing Resource

The classification application PR allows you to apply a classifier you already learned using the classification training PR. The PR has no init time parameters. Here are the runtime parameters:

* `algorithmParameters` Parameters to pass on to the application algorithm, if any. 
* `dataDirectory` The directory that was used to save the model during training. 
* `inputASName` The annotation set that contains the instance annotations and the annotations specified in the feature configuration file.
* `instanceType` The annotation type of the instance annotations. 
* `outputASName` The annotation set where the prediction will be placed. If this is the same as the input annotation set, then the existing instance annotations will be updated, otherwise new annotation will be created.
* `sequenceSpan` The annotation type for sequence spans if the classification algorithm is a sequence learning algorithm
* `serverUrl` (String, no default) if specified, will try to connect to the given URL and use the server there to get the predictions. See [[ServerForApplication]] for details.
* `targetFeature` Which feature to write the classification onto. Leave blank to put it in the feature that was learned at training time.

## AlgorithmParameters

If a `serverUrl` is specified and thus a HTTP server is used to carry out the classification, the following parameters are supported:
* `-d` or `-dense`: send the vectors in dense format, default is sparse format