# Parameters for the PytorchWrapper 

This describes the main parameters that can be specified for training, exporting and application
in the GATE runtime parameter "algorithmParameters". Note that additional parameters may be 
usable for specific modules or wrapper implementations in the PytrochWrapper, these 
are documented together with the module/wrapper implementation (See TBD!!)

The parameters which can specified for training can also be specified when running the 
training command from the command line (see [PytorchWrapper_Info])

## Parameters for training

* --embs specifications: set/override the embeddings definitions for some nominal feature. This is a comma-separated list
  (no spaces!) of specifications where each specification is a colon-separated list of the form 
  "embid:embdims:embtrain:embminfreq:embfile". If an element if the list is not specified, the value specified in 
  the feature configuration file or the default value is used. An element is not specified if the value is the empty string
  (two colons following each other) or, at the end of the list, if no more elements are specified. The following elements
  can be specified (see also [Feature Specification](/FeatureSpecification):
  * embid: (required) the embeddings id which can be empty, in which case the default embedding id is implied
  * embdims: (numeric) the number of embedding dimensions to use or 0 to determine automatically. If a file is used,
    the specified dimensions must be equal to the dimensions of the embeddings in the file.
  * embtrain: one of "yes" (train/fine-tune), "no" leave unchanged, "mapping" (requires file, learn a mapping), 
    "onehot" (use onehot vector instead of embeddings)
  * embminfreq: (numeric) the minimum number of occurrences of a word in the training set, less frequent words get replaced
    by an OOV placeholder
  * embfile: (string) the path to an embedding file in text format
* --valsize: number of instances to split ofif the dataset for validation, if this is a float > 0.0 and < 1.0, the fraction of 
  instances to split off. This is ignored if --valfile is specified.
* --valeverybatches: validate on the validation set every that many batches
* --valeveryinstances: validate on the validation set every that many instances
* --valeveryepochs: validate on the validation set every that many epochs
* --repeveryinstances: report the training set loss/accuracy every that many instances
* --repeverybatches: report the training set loss/accuracy every that many batches
* --batchsize: size of a batch (default 20)
* --maxepochs: maximum number of epochs to run, depending on the stopping criterion used, this can be less
  (NOTE: currently the stopping criterion cannot be changed: training is terminated if the validation accuracy 
  does not increase for two validation cycles)
* --stopfile: the path to a file, if that file exists, training is terminated
* --module: the name of a module in the `modules` subdirectory of the wrapper library which is used instead of 
  the auto-generated module
* --wrapper: the name of a wrapper implementation in the wrapper library which is used instead of the default
  wrapper implementation
* --learningrate: overrides the default learning rate for the optimizer used
* --cuda: if false, CUDA is not used, if true, CUDA is used, if not specified, automatically determined from the 
  available hardware
* --seed: the random seed to set before training starts: this influences the randomness of the parameters the module
  gets initialized with and the selection of instances for the validation set. 
* --resume: this is only useful when running from the command line. If specified, an existing saved model is used
  to continue training instead of starting from scratch
* --notrain: no training is carried out. This can be used in GATE to avoid training and then run the training
  from the command line. This will still generate and show the model
* --nocreate: no training and also does not generate the model
* --valfile: specify a separate validation file instead of splitting off instances from the training file for validation
  (see below)

In order to use a separate validation file:
* Use `LF_Export` to export the corpus for validation into a separate data directory
* specify the relative path to the data file create (datadirectory/crvd.data.json) with the --valfile parameter
