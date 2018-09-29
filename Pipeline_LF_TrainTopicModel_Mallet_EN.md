# Pipeline `LF_TrainTopicModel_Mallet_EN`

This is a "ready made" pipeline that illustrates a simple preparation of documents
for training a topic model and then runs the PR to train a Mallet topic model.

IMPORTANT: this pipeline expects that Token annotations as created by ANNIE, with 
POS tags in the feature "category" and the lemma/root in feature "root"
are already in the default annotation set.

Steps:
* Use a Groovy script to copy only those token annotations which are of kind "word"
  and which do not have a POS tag starting with "V" into the annotation set LDA
  as annotation type "TokenWord"
* Use a FeatureGazetteer to remove TokenWord annotations where a stop word matches the lemma
* Use a FeatureGazetteer to remove TokenWord annotations where a stop word matches the lower case string
* Use a JavaRegexpAnnotator to find strings in the document that look like something we do not 
  want to include for topic modelling (currently only finds URLs and email addresses)
* Remove TokenWord annotations contained within what has found in the previous step
* Run the LF PR for training a topic model, using the remaining TokenWord annotations as input
