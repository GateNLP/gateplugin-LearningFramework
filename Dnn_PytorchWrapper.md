# Pytorch Wrapper

The PytorchWrapper is currently one of two wrappers for using "Deep Learning" / Neural Networks
with the Learning Framework. See [Using Neural Networks](UsingNeuralNetworks) for an overview.

The PytorchWrapper provides two algorithms:
* `PytorchWrapper_CL_DR`: a classification algorithm that processes individual instances. This creates a JSON training data file where each line represents the feature vector for an instance and the corresponding class label
* `PytorchWrapper_SEQ_DR`: a sequence tagging algorithm that processes whole sequences of instances (where each instances has to get assigned a label). This creates a JSON training data file where each line represents a sequence of feature vectors for each instance in the sequence and the corresponding sequence of labels for each instance in the sequence.

In both cases the default action taken by the wrapper for training is to try to
dynamically create a neural network that can process the features as specified in the feature specification
file to predict the class labels. Heuristics are used for how the network architecture is created and how
hyperparameters like number of embedding dimensions, number of hidden units etc are chosen.

The generated architecture and hyperparameters are shown to the user and logged. This can be used to
more easily adapt or implement the Pytorch neural network module specifically to the problem at hand.
The pytorch wrapper library also comes with a number of pre-defined special-purpose modules which can be
used as starting points for specific tasks (e.g. NER or POS tagging).

Since the process of adapting / replacing the network architecture and optimzing the hyperparameters of the
network, or choosing the correct regularization strategy or optimizer require a better understanding of which
data the network is using (with the other algorithms supported by the LearningFramework, e.g. LibSVM or the
Mallet algorithms, the format of the data is just an implementation detail that the user does not need to know
about) the data format an many other details are document in this pages and its subpages. Here is an overview
of the PytorchWrapper documentation:
* [Training](Dnn_PytorchWrapper_Training) - all the details about how to train a model, specify parameters, change
  the neural network architecture and hyperparameters, use and adapt a predefined special-purpose module from the
  library etc. (TO BE IMPROVED)
* [Application](Dnn_PytorchWrapper_Application) - how to use a trained model and apply it to new documents (TO BE DONE)
* [Examples](Dnn_PytorchWrapper_Examples) - some simple illustrative examples (TO BE DONE)
* [Tutorials](Dnn_PytorchWrapper_Tutorials) - tutorials to solve some NLP task using the PytorchWrapper backend (TO BE DONE)
