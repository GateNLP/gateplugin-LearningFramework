# LF_ApplyRegression Processing Resource

The regression application PR allows you to apply a regression model you already learned using the regression training PR. The PR has no init time parameters. Here are the runtime parameters:

* `algorithmParameters` — At application time, this runtime parameter is a catch-all for various currently undocumented switches that don't warrant their own parameter.
* `dataDirectory` — Where is the model that you want to use saved on disk?
* `inputASName` — Input annotation set containing attributes
* `instanceType` — Annotation type to classify
* `outputASName` — Where to put the new classifications. Leave blank to put them on the instance.
* `serverUrl` (String, no default) if specified, will try to connect to the given URL and use the server there to get the predictions. See [[ServerForApplication]] for details.
* `targetFeature` — Which feature to write the classification onto. Leave blank to put it in the feature that was learned at training time.

## AlgorithmParameters

If a `serverUrl` is specified and thus a HTTP server is used to carry out the classification, the following parameters are supported:
* `-d` or `-dense`: send the vectors in dense format, default is sparse format