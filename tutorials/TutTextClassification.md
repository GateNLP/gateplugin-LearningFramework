# Tutorial on Text Classification

**NOTE: this tutorial is currently under construction!!**

The aim of this tutorial is to help with the following:
* Understand what the text classification task is
* Apply different machine learning algorithms to the problem:
  Logistic Regression, Support Vector Machines
* Understand how to evaluate the quality of a learned model
* Understand how choosing the features and hyperparameters influences
  the quality of a learned model
* Apply neural network approaches to the problem
* Understand the influence of hyperparameters, network architecture and
  optimization strategy when using a neural network


## Preparation

For this tutorial, please download and prepare the following
* !!!TODO: download the following ZIP-file [zipfile](TODO)
* extract the files to a working directory on your computer
* the directory contains the necessary documents and prepared
  pipelines for the various steps in the tutorial
* If you follow the steps in this tutorial you will create your own pipelines from scratch, but you can always use or compare with the prepared pipelines which are already there

## The Text Classification task

Whenever we have spans of text and the task is to assign one of several
possible classes to that span of text, we call that a
*text classification task*.

Some examples of this task are:
* Topic classification: each span of text should get assigned to one of several
  possible topics. A span of text for that task is often a whole document or a
  paragraph.
* Sentiment classification: each span of text, usually a sentence, should get
  assigned to one of several sentiments, often just "positive" or "negative"
* POS tagging: each word should get assigned a "Part of Speec" tag, e.g.
  "noun", "adverb" etc. For this task, the span of text is almost always
  a single word or token. This task is also special in that the sequence of
  the POS tags within a sentence follows certain rules. There is a
  separate tutorial for that task: [POS Tagging Tutorial](TutPOSTagging).
* Product reviews: for each span of text that represents a product review
  assign one of a view quality decisions ("positive" vs. "negative", or e.g.
  "0star" to "5star")

In GATE terms, what we want to accomplish is that an annotation that
covers the text (e.g. a document or a sentence) gets a feature that contains the *class label*. For example, if our task is sentiment classification of sentences, then there should be an annotation for each sentence (e.g."Sentence") and the feature map for the annotation should contain a feature where the value reflects the sentiment, e.g. a feature called "class" that can have the value "positive" or "negative".

One way to achieve this which you may already know is to create an application where we try to match words with a gazetteer and use JAPE rules to eventually assign the class label.

But if we want to use machine learning, our approach will be different: we will have to start with a corpus, that already contains a large number of texts and their corresponding class labels and we will *train* a model that will learn how to assign labels to new texts.
We will also need texts with their known labels in order to find out how well the trained model actually works.

In this tutorial we look at an example that is similar to the product review example: short movie review snippets of text and whether the review is positive or negative.

The original data for this is taken from the "sentence polarity dataset v1.0"
dataset available from the Cornell University
 [Movie Review Data](http://www.cs.cornell.edu/people/pabo/movie-review-data/)
download page. This dataset has been widely used by researchers for experimenting with
various text classification approaches.

## The dataset

The dataset contains 5331 sentences or text snippets known to represent
a positive movie review and 5331 sentences/snippets known to represent
a negative review.

We will need to use this dataset for two different purposes:
* For training a model that learns how to assign the class label
* For evaluating how well the model works

!!! IMPORTANT: evaluating a machine learning model is one of the
most important steps when doing machine learning. If this is not
done properly, we may get a wrong impression of how well our trained
model may perform on new, unseen data. To get a good estimate for
this, we need to actually use unseen data for the evaluation, so we
need to split the data we have in a part that is used for training and
in a different part that is used only for evaluation.

If we would use the training data for evaluation, we could easily
overestimate the performance of the algorithm, because an algorithm
could always just memorize what it has seen in the training data
which would give it the best possible performance on that data, but
would fail for any new data.

In this tutorial you will see that in order to train a good model,
one often has to try a number of different approaches, evaluate them
and then compare how well each approach does. When we do this very often, we start to slowly "adapt" our model to the data we have
kept aside for evaluation. We may improve our results on that
data, but the model we end up with may be too specific to the
evaluation data now and not perform so well on the actual unseen
data. For this reason, a common strategy is to actually keep aside
two different sets of evaluation data:
* development set: this is used for repeated evaluation in order to figure out which algorithms and algorithm parameters to use
* test set: this is used only once or very very rarely to get a final estimate of how well the model may perform on actual unseen data

In other words, we should divide our labelled/annotated corpus
into three portions:
* training set: used for training a model
* development set: used for evaluating models during experimentation
* test set: used for evaluating one or only a few final models which are the result of the experimentation

When we divide the original corpus, we should:
* divide the instances as randomly as possible
* but make sure that the distribution of the labels is the same in all parts

In our case there are an identical number of positive and negative sentences in the original dataset. Each sentence was converted into its own GATE document and then the whole corpus was divided into 3 corpora:
* train: 8530 sentences (4265 positive and negative each)
* dev: 1066 sentences (533 pos/neg each)
* test: 1066 sentences (533 pos/neg each)


## Inspect the prepared corpora

For this tutorial, all the sentences from the original corpus have
been converted to GATE format (one document per snippet/sentence),
randomly split into sets for train/dev/test and imported into
a serial datastore.

Open the datastore by starting GATE and choosing
`Datastore` - `Open Datastore` - `SerialDatastore` - then navigate
to the extracted directory `gate-lf-tutorial-textclassification1` and choose the subdirectory `corpus`.

Double click the `corpora` datastore then double click each of the
three corpora `dev`, `test`, and `train` under `GATE Serial Corpus` to  put them under the `Language Resources` tree in the GUI.

Double click the `train` corpus then double click a few of the documents which get displayed to inspect them and see what they look like:
* click the `Annotation Sets` and `Annotation List` buttons
* There are three sets: the default set, the Key set and the "Original markups" set, only the "Key" set contains annotations, in each document, there is a single `Sentence` annotation in the Key set.
* the `Sentence` annotation in the Key annotation set covers the
entire span of the document (which is just a sentence or snipped in any case). The annotation contains features from the conversion
processin in the feature map, we can ignore those.
In addition, there is the important feature: `class` which can have
either the value `neg` or `pos`. This is what we want to learn.
* Also look at documents in the `dev` and `test` corpus, they
should contain exactly the same kind of documents and annotations.

## Create Tokens

Before we can do anything we need at least to get token annotations.
For this we will simply use ANNIE which will create the Token,
SpaceToken and other annotations for us and will already create
some features in the feature maps of the annotations as well, e.g.
the POS tag ("category") of each token.

However, the standard ANNIE pipeline also creates Sentence annotations
and we already have those in the Key set, so we have to modify the
ANNIE pipeline. To do this perform the following steps (the result of these steps is in your directory in the `prepared-annie.xgapp` pipeline):
* Load the default ANNIE pipeline (`Applications` - `Ready made applications` - `ANNIE` - `ANNIE`)
* Double click the pipeline to show the processing resources (PRs)
* Instead of running the Sentence Splitter (step 4) we want to copy the Sentence annotation from the Key set
* Click the Plugin-Manager and load the `Tools` plugin (which contains the Annotation Set Transfer PR)
* Right click on `Processing Resources` - `New` and choose `Annotation Set Transfer`
* In the ANNIE pipeline editor click on the Annotation Set Transfer PR in the left pane, then click on the ANNIE Sentence Splitter in the right pane then click the "move to right" button between the two panes (">>") to insert the Annotation Set Transfer PR
* In the `Processing Resources` view right click the `ANNIE Sentence Splitter PR` and choose "close". This removes the Sentence Splitter from the pipeline as well.
* Click on the Annotation Set Transfer PR and  change the run time parameters:
  * change `copyAnnotations` to `true`
  * set `inputASName` to Key

Now save the modified pipeline into your directory as `annie.xgapp`

Run the pipeline on a single document from one of the corpora and
check that it works as intended: there should be Token, SpaceToken and Sentence annotations in the default annotation set now.
Run the pipeline again on the same document: the same annotations should be there and annotations should not accumulate -- everything gets reset correctly!

When everything works fine, run the pipeline on all three corpora (train, dev, test).
