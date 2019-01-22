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

In this tutorial we look at an example that is similar to the last example:
movie reviews and whether the review is positive or negative.

The original data for this is taken from the "sentence polarity dataset v1.0"
dataset available from the Cornell University 
 [Movie Review Data](http://www.cs.cornell.edu/people/pabo/movie-review-data/)
download page. This dataset has been widely used by researchers for experimenting with 
various text classification approaches. 

## The dataset

The dataset contains 5331 sentences or text snippets known to represent 
a positive movie review and 5331 sentences/snippets known to represent 
a negative review. 

We will use a version of this corpus converted to GATE format for this tutorial.
The corpus is part of a zip file that provides everything that is needed to 
follow along with this tutorial. 


