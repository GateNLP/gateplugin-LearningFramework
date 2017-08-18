/*
 * Copyright (c) 2015-2016 The University Of Sheffield.
 *
 * This file is part of gateplugin-LearningFramework 
 * (see https://github.com/GateNLP/gateplugin-LearningFramework).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package gate.plugin.learningframework.engines;

import cc.mallet.classify.BalancedWinnowTrainer;
import cc.mallet.classify.C45Trainer;
import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.classify.ClassifierTrainer;
import cc.mallet.classify.DecisionTreeTrainer;
import cc.mallet.classify.MaxEntTrainer;
import cc.mallet.classify.NaiveBayesEMTrainer;
import cc.mallet.classify.NaiveBayesTrainer;
import cc.mallet.classify.WinnowTrainer;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.InstanceList.CrossValidationIterator;
import cc.mallet.types.LabelVector;
import cc.mallet.types.Labeling;
import gate.Annotation;
import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.ModelApplication;
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
public class EngineMBMalletClass extends EngineMBMallet {

  private static Logger logger = Logger.getLogger(EngineMBMalletClass.class);

  public EngineMBMalletClass() { }

  @Override
  public void trainModel(File dataDirectory, String instanceType, String parms) {
    System.err.println("EngineMalletClass.trainModel: trainer="+trainer);
    System.err.println("EngineMalletClass.trainModel: CR="+corpusRepresentation);
    
    model=((ClassifierTrainer) trainer).train(corpusRepresentation.getRepresentationMallet());
    updateInfo();
  }

  @Override
  public List<ModelApplication> applyModel(
          AnnotationSet instanceAS, AnnotationSet inputAS, AnnotationSet sequenceAS, String parms) {
    // NOTE: the crm should be of type CorpusRepresentationMalletClass for this to work!
    if(!(corpusRepresentation instanceof CorpusRepresentationMalletTarget)) {
      throw new GateRuntimeException("Cannot perform classification with data from "+corpusRepresentation.getClass());
    }
    CorpusRepresentationMalletTarget data = (CorpusRepresentationMalletTarget)corpusRepresentation;
    data.stopGrowth();
    List<ModelApplication> gcs = new ArrayList<ModelApplication>();
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
      ModelApplication gc = new ModelApplication(instAnn, labeling.getBestLabel().toString(), 
              labeling.getBestValue(), classes, confidences);
      //System.err.println("ADDING GC "+gc);
      // now save the class in our special class feature on the instance as well
      instAnn.getFeatures().put("gate.LF.target",labeling.getBestLabel().toString());
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
        Parms ps = new Parms(parms, "m:maxDepth:i", "p:prune:B","n:minNumInsts:i");
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
      } else if(algorithm.equals(AlgorithmClassification.MALLET_CL_MAX_ENT)) {
        MaxEntTrainer tr = new MaxEntTrainer();
        Parms ps = new Parms(parms, "v:gaussianPriorVariance:d",
                "l:l1Weight:d", "i:numIterations:i");
        // TODO: the default values cannot be take from MaxEntTrainer because
        // they are not public there
        double gaussianPriorVariance = (double)ps.getValueOrElse("gaussianPriorVariance", 1.0);
        tr.setGaussianPriorVariance(gaussianPriorVariance);
        double l1Weight = (double)ps.getValueOrElse("l1Weight", 0.0);
        tr.setL1Weight(l1Weight);
        int iters = (int)ps.getValueOrElse("numIterations", Integer.MAX_VALUE);
        tr.setNumIterations(iters);
        trainer = tr;
      // NOTE: for AdaBoost, use this method recursively to first initialize
      // the trainer with the base traner. The parameters should contain
      // something like -A ALGNAME -N numRounds -a -b ... 
      // where ALGNAME is an AlgorithmClassification constant and N is the
      // numRounds parameter for AdaBoost[M2] and all the other parameters 
      // are for the base algorithm initialization
      } else if(algorithm.equals(AlgorithmClassification.MALLET_CL_BALANCED_WINNOW)) {
        Parms ps = new Parms(parms, "e:epsilon:d",
                "d:delta:d", "i:maxIterations:i", "c:coolingRate:d");
        double epsilon = (double)ps.getValueOrElse("epsilon", BalancedWinnowTrainer.DEFAULT_EPSILON);
        double delta = (double)ps.getValueOrElse("delta", BalancedWinnowTrainer.DEFAULT_DELTA);
        int iters = (int)ps.getValueOrElse("int", BalancedWinnowTrainer.DEFAULT_MAX_ITERATIONS);
        double cr = (double)ps.getValueOrElse("coolingRate", BalancedWinnowTrainer.DEFAULT_COOLING_RATE);
        trainer = new BalancedWinnowTrainer(epsilon,delta,iters,cr);
      } else if(algorithm.equals(AlgorithmClassification.MALLET_CL_NAIVE_BAYES)) {
        // This one does not have any parameters!
        trainer = new NaiveBayesTrainer();        
      } else if(algorithm.equals(AlgorithmClassification.MALLET_CL_NAIVE_BAYES_EM)) {
        // No parameters!
        trainer = new NaiveBayesEMTrainer();
      } else if(algorithm.equals(AlgorithmClassification.MALLET_CL_WINNOW)) {
        Parms ps = new Parms(parms, "a:alpha:d",
                "b:beta:d", "n:nfact:d");
        double alpha = (double)ps.getValueOrElse("alpha", 2.0);
        double beta = (double)ps.getValueOrElse("beta", 2.0);
        double nfact = (double)ps.getValueOrElse("nfact", 0.5);
        
        WinnowTrainer trainer = new WinnowTrainer(alpha, beta, nfact);
      } else {
        // all other algorithms are still just instantiated from the class name, we ignore
        // the parameters
        logger.warn("IMPORTANT: parameters ignored when creating Mallet trainer " + algorithm.getTrainerClass());
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
      CrossValidationIterator cvi = corpusRepresentation.getRepresentationMallet().crossValidationIterator(numberOfFolds, seed);
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
          InstanceList[] sets = corpusRepresentation.getRepresentationMallet().split(rnd,
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
