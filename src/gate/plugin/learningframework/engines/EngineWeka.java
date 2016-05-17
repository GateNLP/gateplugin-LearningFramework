/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.engines;

import cc.mallet.types.Instance;
import gate.Annotation;
import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.GateClassification;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.data.CorpusRepresentationWeka;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.evaluation.Evaluation;
import weka.core.Instances;
import cc.mallet.types.FeatureVector;

/**
 *
 * @author Johann Petrak
 */
public class EngineWeka extends Engine {
  
  
  // TODO: this needs to get adapted to the new more general evaluate method!
  public Evaluation evaluateHoldout(double portion, int repeats, String parms) {
    // Get the parameters 
    // -s/-seed <int> : seed, default 0
    // -S/-nostratify : switch off stratification if we evaluate classification
    Parms opts = new Parms(parms,"s:seed:i","S:nostratify:b");
    int seed = (int)opts.getValueOrElse("seed", 0);
    if(algorithm instanceof AlgorithmRegression) {
       weka.core.Instances all = new CorpusRepresentationWeka(corpusRepresentationMallet).getRepresentationWeka();
       Random rand = new Random(seed); 
       all.randomize(rand);
       if(repeats!=1) {
         throw new GateRuntimeException("Only repeats == 1 supported yet");
       }
       int trainSize = (int)Math.round(all.numInstances()*portion);
       int testSize = all.numInstances()-trainSize;
       Instances train = new Instances(all,0,trainSize);
       Instances test = new Instances(all,trainSize,testSize);
       Classifier classifier = (Classifier)trainer;
      try {
        classifier.buildClassifier(train);
      } catch (Exception ex) {
        throw new GateRuntimeException("Error during training of Weka classifier",ex);
      }
      Evaluation eval = null;
      try {
        eval = new Evaluation(train);
      } catch (Exception ex) {
        throw new GateRuntimeException("Could not create Evaluation object",ex);
      }
      try {
        eval.evaluateModel(classifier, test);
      } catch (Exception ex) {
        throw new GateRuntimeException("Error evaluating the classifier",ex);
      }
      System.out.println("Evaluation result:\n"+eval);
      return eval;
      
    } else {
      // must be classification algorithm then!
       weka.core.Instances all = new CorpusRepresentationWeka(corpusRepresentationMallet).getRepresentationWeka();
       boolean noStratify = (boolean)opts.getValueOrElse("nostratify", false);
       Random rand = new Random(seed); 
       all.randomize(rand);
       boolean stratified = !noStratify;
       // TODO: not sure if/how we can do stratification for holdout evaluation
       // TODO: there must be a better way to do the splitting too!
       // TODO: if there is no better way to split, maybe do out outside for
       // TODO: how to implement repeats?
       if(repeats!=1) {
         throw new GateRuntimeException("Only repeats == 1 supported yet");
       }
       // both regression and classification?
       int trainSize = (int)Math.round(all.numInstances()*portion);
       int testSize = all.numInstances()-trainSize;
       Instances train = new Instances(all,0,trainSize);
       Instances test = new Instances(all,trainSize,testSize);
       Classifier classifier = (Classifier)trainer;
      try {
        classifier.buildClassifier(train);
      } catch (Exception ex) {
        throw new GateRuntimeException("Error during training of Weka classifier",ex);
      }
      Evaluation eval = null;
      try {
        eval = new Evaluation(train);
      } catch (Exception ex) {
        throw new GateRuntimeException("Could not create Evaluation object",ex);
      }
      try {
        eval.evaluateModel(classifier, test);
      } catch (Exception ex) {
        throw new GateRuntimeException("Error evaluating the classifier",ex);
      }
      System.out.println("Evaluation result:\n"+eval);
      return eval;
    }
  }

  // TODO: this needs to get adapted to the new more general evaluate method!
  public Evaluation evaluateXVal(int k, String parms) {
    Parms opts = new Parms(parms,"s:seed:i","S:nostratify:b");
    int seed = (int)opts.getValueOrElse("seed", 0);
    boolean noStratify = (boolean)opts.getValueOrElse("nostratify", false);
    Random rand = new Random(seed); 
    Instances all = new CorpusRepresentationWeka(corpusRepresentationMallet).getRepresentationWeka();
    Evaluation eval = null;
    try {
      eval = new Evaluation(all);
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not create evaluation object",ex);
    }
    Classifier classifier = (Classifier)trainer;
    try {
      eval.crossValidateModel(classifier, all, k, rand);
    } catch (Exception ex) {
      throw new GateRuntimeException("Error running cross validation",ex);
    }
    System.out.println("Crossvaliation evaluation result:\n"+eval);
    return eval;
  }

  @Override
  protected void loadModel(File directory, String parms) {
    // when this is called, info should already be set
    // we create the instance of the training algorithm from the infor and the instance of
    // the actual trained classifier from de-serialization of the file
    File modelFile = new File(directory, FILENAME_MODEL);
    try {
        ObjectInputStream ois
                = new ObjectInputStream(new FileInputStream(modelFile));
        model = (Classifier) ois.readObject();
        System.out.println("Loaded Weka model " + model.getClass());
        ois.close();
    } catch (Exception e) {
        throw new GateRuntimeException("Could not load Weka model file "+modelFile,e);
    }    
    try {
      trainer = Class.forName(info.trainerClass).newInstance();
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not create Weka trainer instance for "+info.trainerClass,ex);
    }
  }

  @Override
  public void trainModel(String parms) {
    // TODO: process parameters!
    if(trainer == null) {
      throw new GateRuntimeException("Cannot train Weka model, not trainer initialized");
    }
    Classifier alg = (Classifier)trainer;
    // convert the Mallet representation to Weka instances
    CorpusRepresentationWeka crw = new CorpusRepresentationWeka(corpusRepresentationMallet);
    
    try {
      alg.buildClassifier(crw.getRepresentationWeka());
      // set the trained model to the algorithm, in Weka they are identical
      model = alg;
    } catch (Exception ex) {
      throw new GateRuntimeException("Error during training of Weka algorithm "+alg.getClass(),ex);
    }
    updateInfo();
  }
  
  CorpusRepresentationWeka crWeka;
  
  /**
   * Return the Weka dataset stored with this engine.
   * This exists mainly so that the engine nows the features, the dataset
   * itself is empty.
   * @return 
   */
  public CorpusRepresentationWeka getCorpusRepresentationWeka() {
    return crWeka;
  }
  
  @Override
  protected void loadMalletCorpusRepresentation(File directory) {
    corpusRepresentationMallet = CorpusRepresentationMalletTarget.load(directory);
    crWeka = new CorpusRepresentationWeka(corpusRepresentationMallet);
  }
  

  @Override
  public List<GateClassification> classify(
          AnnotationSet instanceAS, AnnotationSet inputAS, AnnotationSet sequenceAS, String parms) {
    
    Instances instances = crWeka.getRepresentationWeka();
    CorpusRepresentationMalletTarget data = (CorpusRepresentationMalletTarget)corpusRepresentationMallet;
    data.stopGrowth();
    List<GateClassification> gcs = new ArrayList<GateClassification>();
    LFPipe pipe = (LFPipe)data.getRepresentationMallet().getPipe();
    //System.out.println("FeatureInfo="+pipe.getFeatureInfo());
    Classifier wekaClassifier = (Classifier)model;
    //System.out.println("DataAlphabet="+data.getRepresentationMallet().getDataAlphabet());
    // iterate over the instance annotations and create mallet instances 
    for(Annotation instAnn : instanceAS.inDocumentOrder()) {
      Instance inst = data.extractIndependentFeatures(instAnn, inputAS);
      
      //FeatureVector fv = (FeatureVector)inst.getData();      
      //System.out.println("Mallet instance, fv: "+fv.toString(true)+", len="+fv.numLocations());
      inst = pipe.instanceFrom(inst);
      
      //FeatureVector fv = (FeatureVector)inst.getData();
      //System.out.println("Mallet instance, fv: "+fv.toString(true)+", len="+fv.numLocations());
      
      // Convert to weka Instance
      weka.core.Instance wekaInstance = CorpusRepresentationWeka.wekaInstanceFromMalletInstance(instances, inst);
      
      //System.out.println("Classifying instance: "+wekaInstance.toStringNoWeight());
      
      // classify with the weka classifier or predict the numeric value: if the mallet pipe does have
      // a target alphabet we assume classification, otherwise we assume regression
      GateClassification gc = null;
      if(pipe.getTargetAlphabet() == null) {
        // regression
        
        // DEBUG: for regression, the wekaInstance must have a numeric class attribute
        //if(!wekaInstance.classAttribute().isNumeric()) {
        //  System.err.println("Class attribute not numeric for Regression: "+wekaInstance.classAttribute());
        //}
        double result=Double.NaN;
        try {
          result = wekaClassifier.classifyInstance(wekaInstance);
        } catch (Exception ex) {
          // Hmm, for now we just log the error and continue, not sure if we should stop here!
          ex.printStackTrace(System.err);
          Logger.getLogger(EngineWeka.class.getName()).log(Level.SEVERE, null, ex);
        }
        //gc = new GateClassification(instAnn, (result==Double.NaN ? null : String.valueOf(result)), 1.0);
        gc = new GateClassification(instAnn, result);
      } else {
        // classification

        // DEBUG: for classification, the wekaInstance must have a nominal class attribute
        //if(!wekaInstance.classAttribute().isNominal()) {
        //  System.err.println("Class attribute not nominal for classification: "+wekaInstance.classAttribute());
        //}


        // Weka AbstractClassifier already handles the situation correctly when 
        // distributionForInstance is not implemented by the classifier: in that case
        // is calls classifyInstance and returns an array of size numClasses where
        // the entry of the target class is set to 1.0 except when the classification is a missing
        // value, then all class probabilities will be 0.0
        // If distributionForInstance is implemented for the algorithm, we should get
        // the probabilities or all zeros for missing class from the algorithm.
        double[] predictionDistribution = new double[0];
        try {
          //System.err.println("classifying instance "+wekaInstance.toString());
          predictionDistribution = wekaClassifier.distributionForInstance(wekaInstance);
        } catch (Exception ex) {
          throw new RuntimeException("Weka classifier error in document "+instanceAS.getDocument().getName(),ex);
        }
        // This is classification, we should always get a distribution list > 1
        if (predictionDistribution.length < 2) {
          throw new RuntimeException("Classifier returned less than 2 probabilities: " + predictionDistribution.length
                  + "for instance" + wekaInstance);
        }
        double bestprob = 0.0;
        int bestlabel = 0;
        /*
        System.err.print("DEBUG: got classes from pipe: ");
    		Object[] cls = pipe.getTargetAlphabet().toArray();
        boolean first = true;
        for(Object cl : cls) {
          if(first) { first = false; } else { System.err.print(", "); }
          System.err.print(">"+cl+"<");
        }
        System.err.println();
         */
        List<String> classList = new ArrayList<String>();
        List<Double> confidenceList = new ArrayList<Double>();
        for (int i = 0; i < predictionDistribution.length; i++) {
          int thislabel = i;
          double thisprob = predictionDistribution[i];
          String labelstr = (String) pipe.getTargetAlphabet().lookupObject(thislabel);
          classList.add(labelstr);
          confidenceList.add(thisprob);
          if (thisprob > bestprob) {
            bestlabel = thislabel;
            bestprob = thisprob;
          }
        } // end for i < predictionDistribution.length

        String cl
                = (String) pipe.getTargetAlphabet().lookupObject(bestlabel);

        gc = new GateClassification(
                instAnn, cl, bestprob, classList, confidenceList);
      }
      gcs.add(gc);
    }
    data.startGrowth();
    return gcs;
  }

  @Override
  public void initializeAlgorithm(Algorithm algorithm, String parms) {
    Class trainerClass = algorithm.getTrainerClass();
    //System.err.println("LF DEBUG: trying to initialize trainer class "+trainerClass);
    try {
      trainer = trainerClass.newInstance();
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not create Weka trining algorithm for class "+trainerClass);
    }
  }

  @Override
  public void saveModel(File directory) {
    File outFile = new File(directory,FILENAME_MODEL);
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(new FileOutputStream(outFile));
      oos.writeObject(model);
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not save Weka model to "+outFile,ex);
    } finally {
      if(oos!=null) try {
        oos.close();
      } catch (IOException ex) {
        // ignore
      }
    }
  }

  @Override
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    EvaluationResult ret = null;
    if(evaluationMethod == EvaluationMethod.CROSSVALIDATION) {
      Evaluation eval = evaluateXVal(numberOfFolds, algorithmParameters);
      if(algorithm instanceof AlgorithmClassification) {
        EvaluationResultClXval e = new EvaluationResultClXval();
        e.internalEvaluationResult = eval;
        e.accuracyEstimate = 1.0 - eval.errorRate();
        e.nrFolds = numberOfFolds;   
        ret = e;
      } else {
        EvaluationResultRgXval e = new EvaluationResultRgXval();
        e.internalEvaluationResult = eval;
        e.rmse = eval.rootMeanSquaredError();
        e.nrFolds = numberOfFolds;   
        ret = e;        
      }
    } else {
      // TODO: check if classification or regression!!
      Evaluation eval = evaluateHoldout(trainingFraction, numberOfRepeats, algorithmParameters);
      if(algorithm instanceof AlgorithmClassification) {
        EvaluationResultClHO e = new EvaluationResultClHO();
        e.internalEvaluationResult = eval;
        e.accuracyEstimate = 1.0 - eval.errorRate();
        e.trainingFraction = trainingFraction;
        e.nrRepeats = numberOfRepeats;
        ret = e;
      } else {
        EvaluationResultRgHO e = new EvaluationResultRgHO();
        e.internalEvaluationResult = eval;
        e.rmse = eval.rootMeanSquaredError();
        e.trainingFraction = trainingFraction;
        e.nrRepeats = numberOfRepeats;
        ret = e;        
      }      
    }
    return ret;
  }

  
  
  
}
