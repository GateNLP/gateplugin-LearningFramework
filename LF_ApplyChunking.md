# LF_ApplyChunking Processing Resource

The chunking application PR allows you to apply a chunking classifier you already learned using the chunking training PR. The PR has no init time parameters. Here are the runtime parameters:

* algorithmParameters—At application time, this runtime parameter is a catch-all for various currently undocumented switches that don't warrant their own parameter. You can leave it blank.
* confidenceThreshold—Although it's a better idea to tune your model appropriately, in some cases you might find it helpful to tweak performance by applying a threshold on the confidence, for example to make your model more or less conservative. The utility of this depends on the quality of the confidence score assigned by the algorithm you chose at training time.
* dataDirectory—Where is the model that you want to use saved on disk?
* inputASName—Input annotation set containing attributes
* instanceType—Annotation type to classify; probably Token or equivalent
* outputASName—Where to put the new classifications. Leave blank to put them on the instance.
* sequenceSpan—For sequence classifiers only--the sequence span you gave at training time (or equivalent).

