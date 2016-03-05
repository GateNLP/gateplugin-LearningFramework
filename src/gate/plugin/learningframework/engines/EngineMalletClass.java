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
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelVector;
import cc.mallet.types.Labeling;
import gate.Annotation;
import gate.AnnotationSet;
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
        Parms ps = new Parms(parms, "m:maxDepth:i", "p:prune:b");
        int maxDepth = (int)ps.getValueOrElse("maxDepth", -1);
        boolean prune = (boolean)ps.getValueOrElse("prune",false);  
        trainer = new C45Trainer(maxDepth,prune);
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
  public Object evaluateHoldout(InstanceList instances, double portion, int repeats, String parms) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public Object evaluateXVal(InstanceList instances, int k, String parms) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
  

}
