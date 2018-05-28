# Algorithm Parameters

New algorithms are frequently included in the Learning Framework, and we try to support their parameters. This section is a work in progress, but we give some information about the parameters available for you to use.

## LibSVM

LibSVM can be used as a standard classifier in NER or classification mode. Probabilities are included by default, and are "real" probabilities. However, the resulting model is a little slower than if probabilities aren't calculated. If you are happy to use classifications without information about the probability of that classification being correct, and need your learner to be faster, you can turn off probability generation using "-b" as described below.

The full range of parameters are supported, which can be specified in the "algorithmParameters" field as a space-separated flagged sequence, as described in the LibSVM documentation:

&nbsp;&nbsp;-s svm_type : set type of SVM (default 0)<br>
&nbsp;&nbsp;&nbsp;&nbsp;_0 -- C-SVC_<br>
&nbsp;&nbsp;&nbsp;&nbsp;_1 -- nu-SVC_<br>
&nbsp;&nbsp;&nbsp;&nbsp;_2 -- one-class SVM_<br>
&nbsp;&nbsp;&nbsp;&nbsp;_3 -- epsilon-SVR_<br>
&nbsp;&nbsp;&nbsp;&nbsp;_4 -- nu-SVR_<br>
&nbsp;&nbsp;-t kernel_type : set type of kernel function (default 2)<br>
&nbsp;&nbsp;&nbsp;&nbsp;_0 -- linear: u'*v_<br>
&nbsp;&nbsp;&nbsp;&nbsp;_1 -- polynomial: (gamma*u'*v + coef0)^degree_<br>
&nbsp;&nbsp;&nbsp;&nbsp;_2 -- radial basis function: exp(-gamma*|u-v|^2)_<br>
&nbsp;&nbsp;&nbsp;&nbsp;_3 -- sigmoid: tanh(gamma*u'*v + coef0)_<br>
&nbsp;&nbsp;-d degree : set degree in kernel function (default 3)<br>
&nbsp;&nbsp;-g gamma : set gamma in kernel function (default 1/num_features)<br>
&nbsp;&nbsp;-r coef0 : set coef0 in kernel function (default 0)<br>
&nbsp;&nbsp;-c cost : set the parameter C of C-SVC, epsilon-SVR, and nu-SVR (default 1)<br>
&nbsp;&nbsp;-n nu : set the parameter nu of nu-SVC, one-class SVM, and nu-SVR (default 0.5)<br>
&nbsp;&nbsp;-p epsilon : set the epsilon in loss function of epsilon-SVR (default 0.1)<br>
&nbsp;&nbsp;-m cachesize : set cache memory size in MB (default 100)<br>
&nbsp;&nbsp;-e epsilon : set tolerance of termination criterion (default 0.001)<br>
&nbsp;&nbsp;-h shrinking: whether to use the shrinking heuristics, 0 or 1 (default 1)<br>
&nbsp;&nbsp;-b probability_estimates: whether to train a SVC or SVR model for probability estimates, 0 or 1 (default 0)<br>
&nbsp;&nbsp;-wi weight: set the parameter C of class i to weight*C, for C-SVC (default 1)<br>

### More on weights

Set weights for example like so: "-w0 1 -w1 2.5 -w2 2.5 -w3 4". Assign them assuming classes are in alphabetical/natural order. Weight assignments will be fed back to the logger so you can check them. If you assign the wrong number of weights for your classes, all the weights will be rejected. It's your responsibility to know how many classes you have in your training corpus.

# Mallet CRF

No parameters are used with Mallet CRF. You will need to set the sequenceSpan to something sensible; one learning instance will be generated for each sequence annotation. For example, if you are doing NER and are using "token" as instance, then you might give a sequence span of "sentence", because tokens fall into meaningful patterns within sentences, but not so much across sentence boundaries. Mallet CRF will then learn the material in sentence-long chunks. You need to have sentence annotations prepared on your document, as well as the tokens.

A sequence classifier is an appropriate choice in any context where your instances tend to fall into predictable sequences. It's good for named entity detection, for example, because named entities are often predictable sequences, such as descriptions of symptoms in clinical applications, and the contexts in which they appear are also often meaningful sequences. However, it wouldn't be so good for document classification because documents don't tend to form a meaningful sequence.

# Mallet Classification Algorithms

A number of Mallet classification algorithms are integrated, and the following parameters are available. In each case, parameters are given space separated and unflagged in the order specified:

### Balanced Winnow
     
&nbsp;epsilon (double, default 0.5)<br>
&nbsp;delta (double, default 0.1)<br>
&nbsp;max iterations (int, default 30)<br>
&nbsp;cooling rate (double, default 0.5)<br>

### C45:

&nbsp;max depth (int)

### Decision Tree:

&nbsp;max depth (int)

### Max Ent GE Range, Max Ent GE, Max Ent PR

These all take an array of constraints. This isn't currently supported.

### Max Ent:

&nbsp;gaussian prior (double, a parameter to avoid overtraining. 1.0 is the default value.)<br>
&nbsp;max iterations (int. I have coded this in but it is possible that Mallet still doesn't use it.)<br>

### MC Max Ent:

The following configurations only are supported:

&nbsp;gaussianPriorVariance (double, a parameter to avoid overtraining)

OR

&nbsp;gaussianPriorVariance (double)<br>
&nbsp;useMultiConditionalTraining (boolean)<br>
     
OR

&nbsp;hyperbolicPriorSlope (double)<br>
&nbsp;hyperbolicPriorSharpness (double)<br>

OR no arguments.

### Naive Bayes, Naive Bayes EM

These don't take any parameters.

### Winnow:

&nbsp;a (double)<br>
&nbsp;b (double)<br>
&nbsp;nfact (double, optional)<br>
