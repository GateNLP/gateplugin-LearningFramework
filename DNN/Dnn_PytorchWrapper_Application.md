# Using a trained PytorchWrapper Model

At application time, when you use `LF_ApplyClassification` or `LF_ApplyChunking` the following happens:
* If it does not already exist, the LearningFramework copies the python software needed into your data directory into a subdirectory with the name `FileJsonPytorch`
* the PR starts the application program in the software directory and establishes a connection (through a pipe)
* the PR processes all the documents in the corpus and passes all the instances in the documents to the
  application program. For each instance it receives the prediction and applies it to the document.
