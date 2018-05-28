# How to evaluate trained models

TODO: update, include pointers to
* [Evaluate Classification PR](LF_EvaluateClassification)
* [Evaluate Regression PR](LF_EvaluateRegression)
* Describe how to evaluate Chunking using QA or the evaluation plugin (https://github.com/johann-petrak/gateplugin-Evaluation)


Evaluation is an essential part of machine learning work. It is vital to know how well your system performs, and this cannot be discerned from simply looking at a few documents. Performance statistics should be generated over a corpus large enough to give an indicator of the likely success of the approach in an application context.

Evaluation modes integrated in the PRs include both hold-out and cross-validation, and runtime parameters allow you to specify the number of folds for cross-validation, or the percentage to hold out for hold out evaluation. To perform evaluation, select the relevant mode.

Evaluation modes simply return basic classification accuracy. Note that classification accuracy alone may give a misleading impression of task success for highly imbalanced datasets, such as are common in NLP. For example, if you have five named entities in 500 "token" training instances, then the learner could easily set all instances to "false" and appear to have a success of 99%. A correct task evaluation would involve calculating precision and recall in finding these named entities. The integrated evaluation modes wouldn't therefore be suitable for publishing results about the success of the approach in finding named entities, or gaining a real feel for how successful your system is. Suggested use of evaluation modes is to facilitate parameter tuning in making your learner the best it can be. You would then need to perform a separate task evaluation.

GATE offers a range of more advanced evaluation functionality for determining actual task success. For example, you could separate your corpus into training and test sets, and having applied your learner to the test set, use [Corpus QA](https://gate.ac.uk/userguide/sec:eval:corpusqualityassurance) to investigate its performance.

In the case that you want to try several combinations of parameters, the following approach would be sensible: create several XGAPP files with different learner parameters (or use [Modular Pipelines](https://github.com/johann-petrak/gateplugin-ModularPipelines) with different configuration files), then use a script to run them each in turn in cross-validation or hold-out mode. Then evaluate performance of the best learner on your task using Corpus QA.

Alternatively you could export your data as an ARFF file and tune your parameters in Weka, which saves time repeatedly scraping features off of GATE documents. However you need to be careful in transferring your learnings back into GATE (is the algorithm integrated? All the parameters you used? The same kind of feature scaling?)
