# Using a trained PytorchWrapper Model

At application time, when you use `LF_ApplyClassification` or `LF_ApplyChunking` the following happens:
* If it does not already exist, the LearningFramework copies the python software needed into your data directory into a subdirectory with the name `FileJsonPytorch`
* the PR starts the application program in the software directory and establishes a connection (through a pipe)
* the PR processes all the documents in the corpus and passes all the instances in the documents to the
  application program.
* The application program passes the prediction back to the PR which then uses the PR to set the feature
  or prepare the chunking

  
