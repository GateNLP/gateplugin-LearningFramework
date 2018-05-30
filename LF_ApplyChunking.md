# LF_ApplyChunking Processing Resource

The chunking application PR allows you to apply a chunking classifier you already learned using the chunking training PR. The PR has no init time parameters. Here are the runtime parameters:

* `algorithmParameters`:  At application time, this runtime parameter is a catch-all for various currently undocumented switches that don't warrant their own parameter. You can leave it blank.
* `confidenceThreshold` (double, default: empty, do not use): the minimum average (over all instance classifications)   
  confidence score threshold required for a chunk to get assigned (this has no effect if the algorithm does not produce a classification confidence
    score). If the minimum confidence is not reached, no chunk annotation is created.
* `dataDirectory`: the directory where the trained model is saved
* `inputASName`:  the input annotation set containing instance annotations and attribute annotations
* `instanceType`:  the annotation type to classify; probably Token or equivalent
* `outputASName`:  annotation set where the new chunk annotations are placed (blank/not specified means the default annotation set)
* `sequenceSpan`: for sequence classifiers only, the sequence span you gave at training time (or equivalent).
