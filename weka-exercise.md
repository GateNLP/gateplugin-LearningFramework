# Using GATE's Learning Framework in Conjunction with Weka

## Introduction

In this task you will learn how to use Weka to complement the machine learning functionality available in GATE. Weka offers the advantages of faster training times and additional technologies such as feature selection. It can allow you to rapidly discern the level of performance achievable on your task, and the best algorithms and parameters. Then you can bring this result back into GATE (or an approximation of it).
In this exercise, we'll begin by using the Export PR in the Learning Framework to export the training data from the classification exercise. Then we'll load the data into Weka. We'll try various algorithms and parameters. We'll also try some feature selection and scaling, and see how that affects our result. Then we'll set up GATE to use the Weka wrapper, and replicate our best result within GATE.
You will need:
* The latest version of [Weka](http://www.cs.waikato.ac.nz/ml/weka/downloading.html) installed on your computer
* Course materials from the classification exercise
* The Learning Framework plugin
* The Learning Framework [Weka wrapper](https://github.com/GateNLP/weka-wrapper)
 
## Exporting Data from GATE

* Start up GATE Developer and load the corpora from the classification exercise
* Create the application as you did for the classification exercise; start with ANNIE but remove the last two PRs, and add the annotation set transfer PR to copy up the Key annotations. (You might need to consult the slides from the classification exercise to get this right.)
* Create a Learning Framework Export PR, and add this to the end of the application. Set the parameters.
    * algorithmParameters can be left blank
    * classAnnotationType is for chunking data. This is a classification problem so it should be left blank
make a data directory to store your output data in, and set dataDirectory to this
exporter should be EXPORTER_ARFF_CLASS
    * featureSpecURL is the same feature file you used in the classification exercise
set inputASName to the annotation set where you have your features and class annotations
    * instanceType is the annotation type of your instance
    * instanceWeightFeature is an advanced feature that you can ignore for now
    * scaleFeatures will not be used for this
    * sequenceSpan should be left blank, as we are not exporting sequence training data in this exercise
    * targetFeature is the name of the feature that contains your classes (the feature on the instance annotations)
    * targetType indicates whether your class is nominal or numeric. 
* Run the application over the training data.
* Take a look in the folder you specified for the data to be put in. "data.arff" contains your output data. Have a look at this. Does it look how you would expect? It begins with a feature list, which is an expanded version of the feature specification you gave. Then the instances are given, one per line.
 
[[images/Screenshot-GATEDeveloperLFExportParams.png]]
 
## Importing Data into Weka

* Start up Weka and select "Explorer"
* Use "Open File" to load in the data.arff file you just created
* Spend a few minutes looking around the Weka interface

## Experimenting with Classification in Weka

* Trying algorithms and parameters
    * In the "Classify" tab, click on "Choose". You can see there are a lot of algorithms that you can try! Not all of them are integrated in GATE, but for now, feel free to explore. What is the best result you can get?
    * To set algorithm parameters, click on the parameters. A window will pop up allowing you to change them.
    * Use 10 fold cross-validation. Percentage split is also fine but will give a slightly depressed, and slightly less accurate result. Using the training set gives an inflated result but can be useful in some circumstances. We don't have a separate test set so we can't test on that.
    * Unlike in the GATE GUI, you can click on "Stop" if it is taking too long!
    * Do you notice how much faster that was than running experiments within GATE? Why do you think that is?
    * Make sure to keep a record of what you tried, and the result you obtained.
* Experimenting with LibSVM
    * Experiment with LibSVM in the context of Weka. To do this, you'll have to tell Weka where to find the libsvm.jar library, which is included in the Learning Framework in the lib directory. [This page](https://weka.wikispaces.com/LibSVM) explains how.
    * LibSVM has many parameters. Experiment with changing the values in the following:
        * cost--this is the penalty the algorithm attaches to misclassification at training time
        * kernelType--different kernel types allow different shapes of separators to be drawn in separating the classes
        * degree--this is the degree of the kernel. Higher numbers allow for more flexibility in looking for a good separator between classes. Note that the degree parameter is only used in some kernel types, not all
        * normalize--this will scale the features to make them commensurate. Commensurate features can work better for certain algorithms, in the case that the magnitude of the feature is not an indicator of its importance in classifying the points
* Feature selection
    * We have many features, but most of them probably aren't useful for solving our classification problem. We can use heuristics to find out which are the best features, and only use those. This will improve training times and possibly give a better result, since the algorithms won't be distracted by red herrings.
    * Feature selection is slow, so we will use a heuristic approach in this exercise; CfsSubsetEval, best first (the default). This will take a few minutes to run! Might be a good time for a coffee break?
    * How many features were found to be useful?
    * Now we need to use just these features, not the others. This is going to involve a lot of clicking! Make a note of the selected features and return to the "Preprocess" tab. You can select these features by clicking in the box next to them on the left (a tick appears). To remove the other features, first click "Invert", above the feature list, then "Remove", below.
    * Now return to the "Classify" tab and see how the result you get now compares to the result you got earlier.
    * You can try different ways of selecting features. For example, you could use cross-validation to get a more reliable list.
 
## Bringing what you Learned back into GATE

* In order to get a Learning Framework model that encapsulates what we learned while using Weka, we need to approximate it and train within GATE. Start by getting the Weka wrapper and carefully following the instructions [here](https://github.com/GateNLP/weka-wrapper)
* If the algorithm you want to use is available within GATE, then you can use it. Several of the more interesting Weka algorithms are available if you use the Weka Wrapper. LibSVM is also available.
* To approximate the feature selection you decided on, you will need to get GATE to make those features. If certain keywords were found to be of value, you can use a gazetteer to find those. If you find that for example parts of speech are generally useful but words aren't you can alter your feature file to include parts of speech but exclude words.
* Algorithm parameters should be supported, and can be specified via the algorithmParameters runtime parameter.
"Normalize" (feature scaling) in LibSVM isn't available in GATE, but there is a form of feature scaling that is a reasonable approximation.
* You will have to look at the [LibSVM documentation](https://www.csie.ntu.edu.tw/~cjlin/libsvm/) to find matches for the Weka parameters, as they have different names in Weka.
* Now you can evaluate a model in GATE and see if the result is approximately as you expect. If it's different, can you figure out why?

