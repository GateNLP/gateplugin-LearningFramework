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

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFOptimizableByLabelLikelihood;
import cc.mallet.fst.CRFTrainerByLabelLikelihood;
import cc.mallet.fst.CRFTrainerByStochasticGradient;
import cc.mallet.fst.CRFTrainerByThreadedLabelLikelihood;
import cc.mallet.fst.CRFTrainerByValueGradients;
import cc.mallet.fst.MEMM;
import cc.mallet.fst.MEMMTrainer;
import cc.mallet.fst.SumLatticeDefault;
import cc.mallet.fst.Transducer;
import cc.mallet.fst.TransducerTrainer;
import cc.mallet.fst.ViterbiWriter;
import cc.mallet.optimize.Optimizable;
import cc.mallet.optimize.OptimizationException;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import gate.Annotation;
import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.ModelApplication;
import gate.plugin.learningframework.data.CorpusRepresentationMalletSeq;
import static gate.plugin.learningframework.engines.Engine.FILENAME_MODEL;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.apache.log4j.Logger;
import static gate.plugin.learningframework.LFUtils.newURL;

/**
 *
 * @author Johann Petrak
 */
public class EngineMBMalletSeq extends EngineMBMallet {

  private static Logger logger = Logger.getLogger(EngineMBMalletSeq.class);
  
  @Override
  public void initializeAlgorithm(Algorithm algorithm, String parms) {
    // DOES NOTHINIG?
  }
 

  @Override
  public void trainModel(File DataDirectory, String instanceType, String options) {
    InstanceList trainingData = corpusRepresentation.getRepresentationMallet();
    Transducer td = trainModel(trainingData,options);
    model = td;
    updateInfo();
  }
  
  private static TransducerTrainer createTrainer(InstanceList trainingData, Info info, String options) {
    TransducerTrainer transtrainer = null;

    // NOTE: Training of the CRF is very flexible in Mallet and not everything is clear to me
    // yet. Unfortunately, there is practically no documentation available.
    // There is some useful example code around:
    // http://mallet.cs.umass.edu/fst.php - the only real documentation available
    // src/cc/mallet/examples/TrainCRF.java -  very basic example
    // src/cc/mallet/fst/SimpleTagger.java - more detailled: especially also shows multithreaded training!
    //   how to use this: http://mallet.cs.umass.edu/sequences.php
    
    // NOTE: the name can come from an algorithm selected for classification OR an algorithm
    // selected for actual sequence tagging. This is why we check the literal name here
    // instead of something derived from the Algorithm enum class.
    
    // NOTE on supported trainers: we only support trainers here which are not
    // too complex to set up and which can be used with the normal succession of
    // how training works in the LF.
    // Mallet also supports a lot of additional things, e.g. regularization 
    // on unlabeled data, but this cannot be used here. 
    // 
    String alg = info.algorithmName;
    System.err.println("DEBUG: our algorithm name is "+alg);
    if(alg.startsWith("MALLET_SEQ_CRF")) {
      
      CRF crf = new CRF(trainingData.getPipe(), null);
      
      Parms parms = new Parms(options,
              "S:states:s",
              "o:orders:s",
              "f:ofully:b",
              "a:addstart:b",
              "v:logViterbiPaths:i",
              "t:threads:i",
              "sg:stochasticGradient:b",
              "wdd:weightDimDensely:b",
              "usw:useSparseWeights:b",
              "ssut:setSomeUnsupportedTrick:b");
      
      String states = (String)parms.getValueOrElse("states", "fully-connected");
      switch (states) {
        case "fully-connected":
          crf.addFullyConnectedStatesForLabels();
          break;
        case "as-in":
          crf.addStatesForLabelsConnectedAsIn(trainingData);
          break;
        case "fully-threequarter":
          crf.addFullyConnectedStatesForThreeQuarterLabels(trainingData);
          break;
        case "half":
          crf.addStatesForHalfLabelsConnectedAsIn(trainingData);
          break;
        case "order-n":
          int[] orders = new int[]{1};
          String ordersparm = (String)parms.getValueOrElse("orders", "0:1");
          if(ordersparm.equals("1")) {
            orders = new int[]{1};
          } else if(ordersparm.equals("0:1")) {
            orders = new int[]{0,1};
          } else if(ordersparm.equals("0:1:2")) {
            orders = new int[]{0,1,2};
          } else if(ordersparm.equals("0")) {
            orders = new int[]{0};
          } else if(ordersparm.equals("1:2")) {
            orders = new int[]{1,2};
          } else if(ordersparm.equals("2")) {
            orders = new int[]{2};
          } else {
            throw new GateRuntimeException("Invalid value for parameter orders: "+ordersparm);
          }
          boolean ofully = (Boolean)parms.getValueOrElse("ofully", false);
          crf.addOrderNStates(trainingData, orders, null, null, null, null, ofully);
          break;
        default:
          throw new GateRuntimeException("Unknown value for parameter states: "+states);
      }
      boolean addStart = (boolean) parms.getValueOrElse("addstart", true);
      if(addStart) crf.addStartState();
      
      boolean wdd = (boolean) parms.getValueOrElse("weightDimDensely", false);
      if(wdd) crf.setWeightsDimensionDensely();
      
      // initialize model's weights
      // TODO: make this conditional on a parm, how does this relate to 
      // weightDimDensely?? 
      // !!! This should probably be the same parameter!!!
      // TODO: second parm should depend on the unsupported trick option!
      crf.setWeightsDimensionAsIn(trainingData, false);
      
      // now depending on which trainer we want we need to do slightly different
      // things
      if(alg.equals("MALLET_SEQ_CRF")) { // By[Thread]LabelLikelihood
        // if threads parameter is specified and >0, we use ByThreadLabelLikelihood
        int threads = (int) parms.getValueOrElse("threads", 0);
        boolean usw = (boolean) parms.getValueOrElse("useSparseWeights", false);
        boolean ssut = (boolean) parms.getValueOrElse("setSomeUnsupportedTrick", false);
        if(threads<=0) {      
          CRFTrainerByLabelLikelihood tr = new CRFTrainerByLabelLikelihood(crf);
          if(usw) tr.setUseSparseWeights(true);
          if(ssut) tr.setUseSomeUnsupportedTrick(true);
          transtrainer = tr;
        } else {
          CRFTrainerByThreadedLabelLikelihood tr = 
                  new CRFTrainerByThreadedLabelLikelihood(crf, threads);
          if(usw) tr.setUseSparseWeights(true);
          if(ssut) tr.setUseSomeUnsupportedTrick(true);
          transtrainer = tr;
        }        
      } else if(alg.equals("MALLET_SEQ_CRF_SG")) {
        // TODO: instead of all trainingData, use sample?
        // TODO: allow to use training rate instead of trainingData?
        CRFTrainerByStochasticGradient crft = 
                new CRFTrainerByStochasticGradient(crf, trainingData);
        
      } else if(alg.equals("MALLET_SEQ_CRF_VG")) {
        //  CRFOptimizableBy* objects (terms in the objective function)
        // objective 1: label likelihood objective
        CRFOptimizableByLabelLikelihood optLabel
                = new CRFOptimizableByLabelLikelihood(crf, trainingData);      
        Optimizable.ByGradientValue[] opts
               = new Optimizable.ByGradientValue[]{optLabel};
        // by default, use L-BFGS as the optimizer
        CRFTrainerByValueGradients crfTrainer = new CRFTrainerByValueGradients(crf, opts);
        crfTrainer.setMaxResets(0);
        transtrainer = crfTrainer;
      } else {
        throw new GateRuntimeException("Not yet supported: "+alg);
      }
      
      
      // TODO: if we want to output the viterbi paths:
      int logVit = (int) parms.getValueOrElse("logViterbiPaths", 0);
      if(logVit==0) logVit=Integer.MAX_VALUE;
      final int lv = logVit;
      ViterbiWriter viterbiWriter = new ViterbiWriter(
          "LF_debug", // output file prefix
          new InstanceList[] { trainingData },
          new String[] { "train" }) {
        @Override
        public boolean precondition (TransducerTrainer tt) {
          return tt.getIteration() % lv == 0;
        }
      };
      transtrainer.addEvaluator(viterbiWriter);   
    } else if(alg.equals("MALLET_SEQ_MEMM")) {
      // TODO: 
      MEMM memm = new MEMM(trainingData.getDataAlphabet(),trainingData.getTargetAlphabet());
      // check what this would do:
      //memm.addOrderNStates(trainingData, new int[]{1}, new boolean[]{false}, "START", null, null, false);
      
      memm.addFullyConnectedStatesForLabels();
      // optional:
      //memm.addStartState();
      // second parameter: unsupported trick
      memm.setWeightsDimensionAsIn(trainingData, false);
      transtrainer = new MEMMTrainer(memm);      
    } else {
      // Nothing else supported!
      throw new GateRuntimeException("EngineMalletSeq: unknown/unsupported algorithm: "+alg);
    }
    
    return transtrainer;
  }
  
  @Override
  protected void loadAndSetCorpusRepresentation(URL directory) {
    if(corpusRepresentation==null)
      corpusRepresentation = CorpusRepresentationMalletSeq.load(directory);
  }
  
  
  private Transducer trainModel(InstanceList trainingData, String options) {

    TransducerTrainer trainer = createTrainer(trainingData, info, options);
    Parms parms = new Parms(options,"i:iterations:i","V:verbose:b");
    boolean verbose = (boolean)parms.getValueOrElse("verbose", false);
    int iters = (int) parms.getValueOrElse("iterations", 0);
    if(iters==0) iters = Integer.MAX_VALUE;
    try {
      trainer.train(trainingData, iters);
    } catch(OptimizationException ex) {
      System.err.println("Encountered an OptimizationException during training (CONTINUING!): "+ex.getMessage());
      ex.printStackTrace(System.err);
      System.err.println("We ignore this exception and try to use the model so far ...");
    }
    if(verbose) 
      trainer.getTransducer().print();
    Transducer td = trainer.getTransducer();
    return td;
  }

  @Override
  public List<ModelApplication> applyModel(
          AnnotationSet instanceAS, AnnotationSet inputAS, AnnotationSet sequenceAS, 
          String parms) {
    // stop growth
    CorpusRepresentationMalletSeq data = (CorpusRepresentationMalletSeq)corpusRepresentation;
    data.stopGrowth();
    
    List<ModelApplication> gcs = new ArrayList<ModelApplication>();

    Transducer crf = (Transducer)model;
    
    for(Annotation sequenceAnn : sequenceAS) {
      int sequenceSpanId = sequenceAnn.getId();
      Instance inst = data.getInstanceForSequence( 
              instanceAS, sequenceAnn, inputAS, null, null, TargetType.NONE, null, null);

      //Always put the instance through the same pipe used for training.
      inst = crf.getInputPipe().instanceFrom(inst);

      SumLatticeDefault sl = new SumLatticeDefault(crf,
              (FeatureVectorSequence) inst.getData());

      List<Annotation> instanceAnnotations = gate.Utils.getContainedAnnotations(
              instanceAS, sequenceAnn).inDocumentOrder();

      //Sanity check that we're mapping the probs back onto the right anns.
      //This being wrong might follow from errors reading in the data to mallet inst.
      if (instanceAnnotations.size() != ((FeatureVectorSequence) inst.getData()).size()) {
        logger.warn("LearningFramework: CRF output length: "
                + ((FeatureVectorSequence) inst.getData()).size()
                + ", GATE instances: " + instanceAnnotations.size()
                + ". Can't assign.");
      } else {
        int i = 0;
        for (Annotation instanceAnn : instanceAnnotations) {
          i++;

          String bestLabel = null;
          double bestProb = 0.0;

          //For each label option ..
          
          // NOTE: for CRF we had this code:
          //for (int j = 0; j < crf.getOutputAlphabet().size(); j++) {
          //  String label = crf.getOutputAlphabet().lookupObject(j).toString();
          // but for Transducer we do not have the getOutputAlphabet method so we use
          // model.getInputPipe().getTargetAlphabet() instead (this seems to be what 
          // is used inside CRF anyway.)
          for (int j = 0; j < crf.getInputPipe().getTargetAlphabet().size(); j++) {
            String label = crf.getInputPipe().getTargetAlphabet().lookupObject(j).toString();

            //Get the probability of being in state j at position i+1
            //Note that the plus one is because the labels are on the
            //transitions. Positions are between transitions.
            double marg = sl.getGammaProbability(i, crf.getState(j));
            if (marg > bestProb) {
              bestLabel = label;
              bestProb = marg;
            }
          }
          ModelApplication gc = new ModelApplication(
                  instanceAnn, bestLabel, bestProb, sequenceSpanId);

          gcs.add(gc);
        }
      }
    }
    data.startGrowth();
    return gcs;
  }


  
  @Override
  protected void loadModel(URL directory, String parms) {
    URL modelFile = newURL(directory, FILENAME_MODEL);
    Transducer classifier;
    try (InputStream is = modelFile.openStream();
         ObjectInputStream ois = new ObjectInputStream(is)) {
      classifier = (CRF) ois.readObject();
      model=classifier;
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not load Mallet model", ex);
    }
  }

  @Override
  // NOTE: this evaluates only the classification problem generated from the original chunking problem,
  // so as for classification, we get accuracy estimates, not precision/recall/F-measure.
  // We do not have anything in the LearningFramework for doing F-measure evaluation, this has to 
  // be done outside of the LF in some kind of wrapper or script that invokes the proper LF methods.
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    EvaluationResult ret = null;
    Parms parms = new Parms(algorithmParameters,"s:seed:i");
    int seed = (Integer)parms.getValueOrElse("seed", 1);
    if(evaluationMethod == EvaluationMethod.CROSSVALIDATION) {
      InstanceList.CrossValidationIterator cvi = corpusRepresentation.getRepresentationMallet().crossValidationIterator(numberOfFolds, seed);
      if(algorithm instanceof AlgorithmClassification) {
        double sumOfAccs = 0.0;
        while(cvi.hasNext()) {
          InstanceList[] il = cvi.nextSplit();
          InstanceList trainSet = il[0];
          InstanceList testSet = il[1];
          Transducer crf = trainModel(trainSet, algorithmParameters);
          sumOfAccs += crf.averageTokenAccuracy(testSet);
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
          Transducer crf = trainModel(sets[0], algorithmParameters);
          sumOfAccs += crf.averageTokenAccuracy(sets[1]);
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
