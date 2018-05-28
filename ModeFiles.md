# Saved Files

When you train a model, it is saved on disk in the location you provide, and can be used in future application models by indicating this location.

The saved model includes the following:

* The serialized model itself;
* The feature file used to create the training data. This is saved so that the application time data can be created with the appropriate features to match the model;
* The serialized machinery that uses the feature file to convert the data into a set of instances for the classifier (the pipe containing the alphabet, in Mallet terms, ensuring that features have the same order, and instructions for string handling).
* An information file that provides information that the application time PR needs to understand what the model is. It's also human-readable and informative.
