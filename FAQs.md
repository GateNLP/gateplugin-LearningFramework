# Frequently Asked Questions

## I've been using the Batch Learning PR and am thinking of switching to the Learning Framework.

The Learning Framework is similar in style to the Batch Learning PR, and offers many similar features, but also differs in some ways. If you are considering switching from using the Batch Learning PR, there are several ways in which you will find the new PR different, and will need to change your application:
- Relation extraction is not explicitly supported, so in order to do this using the Learning Framework, you'd have to do more work to piece together the task from separate classification steps;
- The Batch Learning PR allowed a combined NER/classification task in which entities are not only located but also classified. In the Learning Framework, you'll have to do this as separate tasks, either by learning each type as a separate NER task (recommended) or by doing NER then classifying your entities.
- Training and application are now split into separate PRs. You'll need to pick the right one for the task you are doing.
- The configuration file is similar in style but different in some ways. Many settings are now runtime parameters, so you will need to include these in your app. Aside from that, some of the feature specifications will require small edits. Aside from relations features, everything is included, but in some cases, differently specified;
- If you want to use PAUM, the Batch Learning PR remains the only implementation;
- Evaluation is differently implemented in the Learning Framework PR. Classification accuracy is provided, facilitating parameter tuning, using both cross-validation and hold-out methods, but task evaluation is left to other GATE tools to provide, for example the Corpus QA.

## My learner is slow/isn't working very well/isn't working at all/etc. ..?

You may have set your parameters up incorrectly, for example not having class and feature annotations in the same set, and indicating that set correctly to the PR. Use the Export PR to export to ARFF and have a look at the training set it generates. Looking at the ARFF can often reveal if the feature set you are generating isn't what you intended, as well as deepening your understanding of what you are doing.

You may have learned a poor classifier. SVM in particular may perform badly unless you tune parameters such as cost. You may have insufficient training data or inadequate features. Would you be able to do this task given those features and those training examples? You may find it easier and faster to experiment in Weka, by uploading your exported ARFF. If you use algorithms in Weka that are also included in the Learning Framework PR, then the results you obtain about the best parameter settings should transfer back into GATE for that same algorithm. Feature selection can be valuable.
