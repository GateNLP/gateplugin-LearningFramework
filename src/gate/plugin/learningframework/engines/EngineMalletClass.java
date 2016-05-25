/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.engines;

import cc.mallet.classify.C45Trainer;
import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.DecisionTreeTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.InstanceList.CrossValidationIterator;
import cc.mallet.types.LabelVector;
import cc.mallet.types.Labeling;
import gate.Annotation;
import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.GateClassification;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import static gate.plugin.learningframework.engines.Engine.FILENAME_MODEL;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;

/**
 *
 * @author Johann Petrak
 */
public class EngineMalletClass extends EngineMallet {

  private static Logger logger = Logger.getLogger(EngineMalletClass.class);

  public EngineMalletClass() { }

  @Override
  public void trainModel(String parms) {
    System.err.println("EngineMalletClass.trainModel: trainer="+trainer);
    System.err.println("EngineMalletClass.trainModel: CR="+corpusRepresentationMallet);
    
    model=((ClassifierTrainer) trainer).train(corpusRepresentationMallet.getRepresentationMallet());
    updateInfo();
  }

  @Override
  public List<GateClassification> classify(
          AnnotationSet instanceAS, AnnotationSet inputAS, AnnotationSet sequenceAS, String parms) {
    // NOTE: the crm should be of type CorpusRepresentationMalletClass for this to work!
    if(!(corpusRepresentationMallet instanceof CorpusRepresentationMalletTarget)) {
      throw new GateRuntimeException("Cannot perform classification with data from "+corpusRepresentationMallet.getClass());
    }
    CorpusRepresentationMalletTarget data = (CorpusRepresentationMalletTarget)corpusRepresentationMallet;
    data.stopGrowth();
    List<GateClassification> gcs = new ArrayList<GateClassification>();
    LFPipe pipe = (LFPipe)data.getRepresentationMallet().getPipe();
    Classifier classifier = (Classifier)model;
    // iterate over the instance annotations and create mallet instances 
    for(Annotation instAnn : instanceAS.inDocumentOrder()) {
      Instance inst = data.extractIndependentFeatures(instAnn, inputAS);
      inst = pipe.instanceFrom(inst);
      Classification classification = classifier.classify(inst);
      Labeling labeling = classification.getLabeling();
      LabelVector labelvec = labeling.toLabelVector();
      List<String> classes = new ArrayList<String>(labelvec.numLocations());
      List<Double> confidences = new ArrayList<Double>(labelvec.numLocations());
      for(int i=0; i<labelvec.numLocations(); i++) {
        classes.add(labelvec.getLabelAtRank(i).toString());
        confidences.add(labelvec.getValueAtRank(i));
      }      
      GateClassification gc = new GateClassification(instAnn, labeling.getBestLabel().toString(), 
              labeling.getBestValue(), classes, confidences);
      //System.err.println("ADDING GC "+gc);
      gcs.add(gc);
    }
    data.startGrowth();
    return gcs;
  }

  @Override
  public void initializeAlgorithm(Algorithm algorithm, String parms) {
    // if this is one of the algorithms were we need to deal with parameters in some way,
    // use the non-empty constructor, otherwise just instanciate the trainer class.
    // But only bother if we have a parameter at all
    if (parms == null || parms.trim().isEmpty()) {
      // no parameters, just instantiate the class
      Class trainerClass = algorithm.getTrainerClass();
      try {
        trainer = (ClassifierTrainer) trainerClass.newInstance();
      } catch (Exception ex) {
        throw new GateRuntimeException("Could not create trainer instance for " + trainerClass);
      }
    } else {      
      // there are parameters, so if it is one of the algorithms were we support setting
      // a parameter do this      
      if (algorithm.equals(AlgorithmClassification.MALLET_CL_C45)) {      
        Parms ps = new Parms(parms, "m:maxDepth:i", "p:prune:b","n:minNumInsts:i");
        int maxDepth = (int)ps.getValueOrElse("maxDepth", 0);
        int minNumInsts = (int)ps.getValueOrElse("minNumInsts", 2);
        boolean prune = (boolean)ps.getValueOrElse("prune",true);
        C45Trainer c45trainer = null;
        if(maxDepth > 0) {
          if(!prune) 
            c45trainer = new C45Trainer(maxDepth,false);
          else 
            c45trainer = new C45Trainer(maxDepth,true);          
        } else {
          c45trainer = new C45Trainer(prune);
        }
        c45trainer.setMinNumInsts(minNumInsts);
        trainer = c45trainer;
      } else if(algorithm.equals(AlgorithmClassification.MALLET_CL_DECISION_TREE)) {
        DecisionTreeTrainer dtTrainer = new DecisionTreeTrainer();
        Parms ps = new Parms(parms, "m:maxDepth:i", "i:minInfoGainSplit:d");
        int maxDepth = (int)ps.getValueOrElse("maxDepth", DecisionTreeTrainer.DEFAULT_MAX_DEPTH);
        double minIGS = (double)ps.getValueOrElse("minInfoGainSplit",DecisionTreeTrainer.DEFAULT_MIN_INFO_GAIN_SPLIT);  
        dtTrainer.setMaxDepth(maxDepth);
        dtTrainer.setMinInfoGainSplit(minIGS);
        trainer = dtTrainer;
      /* TODO!!!
      } else if(algorithm.equals(AlgorithmClassification.MALLET_CL_MAX_ENT)) {
      } else if(algorithm.equals(AlgorithmClassification.MALLET_CL_NAIVE_BAYES)) {
      } else if(algorithm.equals(AlgorithmClassification.MALLET_CL_NAIVE_BAYES_EM)) {
      } else if(algorithm.equals(AlgorithmClassification.MALLET_CL_WINNOW)) {
      */
      } else {
        // all other algorithms are still just instantiated from the class name, we ignore
        // the parameters
        logger.warn("Parameters ignored when creating Mallet trainer " + algorithm.getTrainerClass());
        Class trainerClass = algorithm.getTrainerClass();
        try {
          trainer = (ClassifierTrainer) trainerClass.newInstance();
        } catch (Exception ex) {
          throw new GateRuntimeException("Could not create trainer instance for " + trainerClass);
        }
      }
    }
  }


  @Override
  protected void loadMalletCorpusRepresentation(File directory) {
    corpusRepresentationMallet = CorpusRepresentationMalletTarget.load(directory);
  }
  
  @Override
  protected void loadModel(File directory, String parms) {
    File modelFile = new File(directory, FILENAME_MODEL);
    if (!modelFile.exists()) {
      throw new GateRuntimeException("Cannot load model file, does not exist: " + modelFile);
    }
    Classifier classifier;
    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(new FileInputStream(modelFile));
      classifier = (Classifier) ois.readObject();
      model=classifier;
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not load Mallet model", ex);
    } finally {
      if (ois != null) {
        try {
          ois.close();
        } catch (IOException ex) {
          logger.error("Could not close object input stream after loading model", ex);
        }
      }
    }
  }

  @Override
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    EvaluationResult ret = null;
    Parms parms = new Parms(algorithmParameters,"s:seed:i");
    int seed = (Integer)parms.getValueOrElse("seed", 1);
    if(evaluationMethod == EvaluationMethod.CROSSVALIDATION) {
      CrossValidationIterator cvi = corpusRepresentationMallet.getRepresentationMallet().crossValidationIterator(numberOfFolds, seed);
      if(algorithm instanceof AlgorithmClassification) {
        double sumOfAccs = 0.0;
        while(cvi.hasNext()) {
          InstanceList[] il = cvi.nextSplit();
          InstanceList trainSet = il[0];
          InstanceList testSet = il[1];
          Classifier cl = ((ClassifierTrainer) trainer).train(trainSet);
          sumOfAccs += cl.getAccuracy(testSet);
        }
        EvaluationResultClXval e = new EvaluationResultClXval();
        e.internalEvaluationResult = null;
        e.accuracyEstimate = sumOfAccs/numberOfFolds; 
        e.nrFolds = numberOfFolds;   
        ret = e;
      } else {
        throw new GateRuntimeException("Mallet evaluation: not available for regression!");
      }
    } else {
      if(algorithm instanceof AlgorithmClassification) {
        Random rnd = new Random(seed);
        double sumOfAccs = 0.0;
        for(int i = 0; i<numberOfRepeats; i++) {
          InstanceList[] sets = corpusRepresentationMallet.getRepresentationMallet().split(rnd,
				new double[]{trainingFraction, 1-trainingFraction});
          Classifier cl = ((ClassifierTrainer) trainer).train(sets[0]);
          sumOfAccs += cl.getAccuracy(sets[1]);
        }
        EvaluationResultClHO e = new EvaluationResultClHO();
        e.internalEvaluationResult = null;
        e.accuracyEstimate = sumOfAccs/numberOfRepeats;
        e.trainingFraction = trainingFraction;
        e.nrRepeats = numberOfRepeats;
        ret = e;
      } else {
        throw new GateRuntimeException("Mallet evaluation: not available for regression!");
      }      
    }
    return ret;
    
  }
  

}
