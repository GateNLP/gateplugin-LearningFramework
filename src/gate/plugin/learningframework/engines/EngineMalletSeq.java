/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.engines;

import cc.mallet.fst.CRF;
import cc.mallet.fst.CRFOptimizableByLabelLikelihood;
import cc.mallet.fst.CRFTrainerByLabelLikelihood;
import cc.mallet.fst.SumLatticeDefault;
import cc.mallet.fst.Transducer;
import cc.mallet.fst.TransducerTrainer;
import cc.mallet.fst.ViterbiWriter;
import cc.mallet.optimize.Optimizable;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import gate.Annotation;
import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.GateClassification;
import gate.plugin.learningframework.data.CorpusRepresentationMalletSeq;
import static gate.plugin.learningframework.engines.Engine.FILENAME_MODEL;
import gate.plugin.learningframework.features.TargetType;
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
public class EngineMalletSeq extends EngineMallet {

  private static Logger logger = Logger.getLogger(EngineMalletSeq.class);
  
  @Override
  public void initializeAlgorithm(Algorithm algorithm, String parms) {
    // DOES NOTHINIG?
  }

  public AlgorithmKind getAlgorithmKind() { 
    return AlgorithmKind.SEQUENCE_TAGGER; 
  }
 

  @Override
  public void trainModel(File DataDirectory, String instanceType, String options) {
    InstanceList trainingData = corpusRepresentationMallet.getRepresentationMallet();
    CRF crf = trainModel(trainingData,options);
    model = crf;
    updateInfo();
  }
  
  public CRF trainModel(InstanceList trainingData, String options) {
    
    // NOTE: Training of the CRF is very flexible in Mallet and not everything is clear to me
    // yet. Unfortunately, there is practically no documentation available.
    // There is some useful example code around:
    // http://mallet.cs.umass.edu/fst.php - the only real documentation available
    // src/cc/mallet/examples/TrainCRF.java -  very basic example
    // src/cc/mallet/fst/SimpleTagger.java - more detailled: especially also shows multithreaded training!
    //   how to use this: http://mallet.cs.umass.edu/sequences.php
    

    // the algorithm name is stored in info.
    // NOTE: the name can come from an algorithm selected for classification OR an algorithm
    // selected for actual sequence tagging. This is why we check the literal name here
    // instead of something derived from the Algorithm enum class.
    System.err.println("DEBUG: our algorithm name is "+info.algorithmName);
    if(info.algorithmName.equals("MALLET_SEQ_CRF")) {
      
      CRF crf = new CRF(trainingData.getPipe(), null);
      
      Parms parms = new Parms(options,"S:states:s","o:orders:s","of:ofully:b","as:addstart:B");
      
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
          String ordersparm = (String)parms.getValueOrElse("orders", "1");
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
          } else if(ordersparm.equals("21")) {
            orders = new int[]{2};
          } else {
            throw new GateRuntimeException("Invalid value for parameter orders: "+ordersparm);
          }
          boolean ofully = (Boolean)parms.getValueOrElse("ofully", false);
          crf.addOrderNStates(trainingData, orders, null, null, null, null, ofully);
        default:
          throw new GateRuntimeException("Unknown value for parameter states: "+states);
      }
      boolean addStart = (boolean) parms.getValueOrElse("addstart", true);
      if(addStart) crf.addStartState();
      
      //crf.setWeightsDimensionDensely();
      
      // initialize model's weights
      crf.setWeightsDimensionAsIn(trainingData, false);
      
      //  CRFOptimizableBy* objects (terms in the objective function)
      // objective 1: label likelihood objective
      CRFOptimizableByLabelLikelihood optLabel
              = new CRFOptimizableByLabelLikelihood(crf, trainingData);
      
      // CRF trainer
      Optimizable.ByGradientValue[] opts
              = new Optimizable.ByGradientValue[]{optLabel};
      // by default, use L-BFGS as the optimizer
      // CRFTrainerByValueGradients crfTrainer = new CRFTrainerByValueGradients(crf, opts);
      
      CRFTrainerByLabelLikelihood crft = new CRFTrainerByLabelLikelihood(crf);
      
      //CRFTrainerByStochasticGradient crft = new CRFTrainerByStochasticGradient(crf, trainingData);

      //crft.setUseSparseWeights(true);
      //crft.setUseSomeUnsupportedTrick(true);
      
      // all setup done, train until convergence
      //crfTrainer.setMaxResets(0);
      
      // TODO: if we want to output the viterbi paths:
        ViterbiWriter viterbiWriter = new ViterbiWriter(
          "LF_debug", // output file prefix
          new InstanceList[] { trainingData },
          new String[] { "train" }) {
        @Override
        public boolean precondition (TransducerTrainer tt) {
          return tt.getIteration() % Integer.MAX_VALUE == 0;
        }
      };
      crft.addEvaluator(viterbiWriter);      
      crft.train(trainingData, Integer.MAX_VALUE);
      return crf;
    } else {
      // For now, there is no other algorithm!
      throw new GateRuntimeException("EngineMalletSeq: only MALLET_SEQ_CRF supported so far!");
    }
  }

  @Override
  public List<GateClassification> classify(
          AnnotationSet instanceAS, AnnotationSet inputAS, AnnotationSet sequenceAS, String parms) {
    // stop growth
    CorpusRepresentationMalletSeq data = (CorpusRepresentationMalletSeq)corpusRepresentationMallet;
    data.stopGrowth();
    
    List<GateClassification> gcs = new ArrayList<GateClassification>();

    Transducer crf = (Transducer)model;
    
    for(Annotation sequenceAnn : sequenceAS) {
      int sequenceSpanId = sequenceAnn.getId();
      Instance inst = data.getInstanceForSequence( 
              instanceAS, sequenceAnn, inputAS, null, null, TargetType.NONE, null);

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
          GateClassification gc = new GateClassification(
                  instanceAnn, bestLabel, bestProb, sequenceSpanId);

          gcs.add(gc);
        }
      }
    }
    data.startGrowth();
    return gcs;
  }


  @Override
  protected void loadMalletCorpusRepresentation(File directory) {
    corpusRepresentationMallet = CorpusRepresentationMalletSeq.load(directory);
  }
  
  @Override
  protected void loadModel(File directory, String parms) {
    File modelFile = new File(directory, FILENAME_MODEL);
    if (!modelFile.exists()) {
      throw new GateRuntimeException("Cannot load model file, does not exist: " + modelFile);
    }
    Transducer classifier;
    ObjectInputStream ois = null;
    try {
      ois = new ObjectInputStream(new FileInputStream(modelFile));
      classifier = (CRF) ois.readObject();
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
  // NOTE: this evaluates only the classification problem generated from the original chunking problem,
  // so as for classification, we get accuracy estimates, not precision/recall/F-measure.
  // We do not have anything in the LearningFramework for doing F-measure evaluation, this has to 
  // be done outside of the LF in some kind of wrapper or script that invokes the proper LF methods.
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    EvaluationResult ret = null;
    Parms parms = new Parms(algorithmParameters,"s:seed:i");
    int seed = (Integer)parms.getValueOrElse("seed", 1);
    if(evaluationMethod == EvaluationMethod.CROSSVALIDATION) {
      InstanceList.CrossValidationIterator cvi = corpusRepresentationMallet.getRepresentationMallet().crossValidationIterator(numberOfFolds, seed);
      if(algorithm instanceof AlgorithmClassification) {
        double sumOfAccs = 0.0;
        while(cvi.hasNext()) {
          InstanceList[] il = cvi.nextSplit();
          InstanceList trainSet = il[0];
          InstanceList testSet = il[1];
          CRF crf = trainModel(trainSet, algorithmParameters);
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
          InstanceList[] sets = corpusRepresentationMallet.getRepresentationMallet().split(rnd,
				new double[]{trainingFraction, 1-trainingFraction});
          CRF crf = trainModel(sets[0], algorithmParameters);
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
