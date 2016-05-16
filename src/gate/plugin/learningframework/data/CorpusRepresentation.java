/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.data;

import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.Exporter;
import gate.plugin.learningframework.engines.Info;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.util.ArrayList;

/**
 * The base class of all classes that handle the representation of instances.
 * The LearningFramework uses the MalletSeq and Mallet representations whenever possible.
 * The other subclasses so far are only used to convert from Mallet representation for 
 * training, classification or export. 
 * @author Johann Petrak
 */
public abstract class CorpusRepresentation {
  protected FeatureInfo featureInfo;
  protected ScalingMethod scalingMethod;
  protected LFPipe pipe;
  
  /**
   * Returns whatever object the concrete representation uses to represent the instances.
   * In addition, each specific CorpusRepresentation subclass has a representation specific
   * method that returns the correct type of data, e.g. getRepresentationLibSVM or getRepresentationWeka.
   * @return 
   */
  public abstract Object getRepresentation();
  
  /**
   * Write the instances to one or more files.
   * If parms is null, the "default natural format" for that representation is used, otherwise
   * some other format that this representation supports is created, depending on the concrete
   * parameters given.
   * @param directory 
   */
  public abstract void export(File directory, String parms);
  
  public static void export(CorpusRepresentationMallet crm, Exporter action, File directory, String parms) {
    if(action == Exporter.EXPORTER_MALLET_CLASS) {
      crm.export(directory, parms);
    } else if(action == Exporter.EXPORTER_WEKA_CLASS) {
      System.err.println("Exporting for classification by Weka to "+directory);
      CorpusRepresentationWeka crw = new CorpusRepresentationWeka(crm);
      crw.export(directory, parms);
    } else if(action == Exporter.EXPORTER_WEKA_LIBSVM_CLASS) {
      System.err.println("Exporting for classification by Weka as LibSVM to "+directory);
      CorpusRepresentationWeka crw = new CorpusRepresentationWeka(crm);
      if(parms==null) parms = "";
      parms = "-f libsvm " + parms;
      crw.export(directory, parms);
    } else if(action == Exporter.EXPORTER_WEKA_REGRESSION) {
      System.err.println("Exporting for regression by Weka to "+directory);
      CorpusRepresentationWeka crw = new CorpusRepresentationWeka(crm);
      crw.export(directory, parms);
    } else if(action == Exporter.EXPORTER_LIBSVM_CLASS) {
      System.err.println("Exporting for classification as LibSVM to "+directory);
      CorpusRepresentationLibSVM crl = new CorpusRepresentationLibSVM(crm);
      crl.export(directory, parms);
    } else {
      // NOTE: if we start to get lots more representations and export formats, maybe
      // we should do this by reflection somehow ...
      throw new GateRuntimeException("Export method not yet implemented: "+action);
    }
    // In addition to the actual data file exported by the methods above,
    // always also export the pipe and a template info file!
    Info info = new Info();
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmClassification";
    info.algorithmName = "WEKA_CL_NAIVE_BAYES";
    info.classAnnotationType = "null";
    LFPipe lfpipe = (LFPipe)crm.getPipe();
    if(lfpipe.getTargetAlphabet()==null) {
      info.classLabels = null;
    } else {
      //info.classLabels = lfpipe.getTargetAlphabet().toArray(); 
      Object[] objs = lfpipe.getTargetAlphabet().toArray();
      info.nrTargetValues = objs.length;
      ArrayList<String> labels = new ArrayList<String>();
      for(Object obj : objs) { labels.add(obj.toString()); }
      info.classLabels = labels;
    }
    info.nrTrainingDimensions = lfpipe.getDataAlphabet().size();
    info.nrTrainingDocuments = 0;
    info.nrTrainingInstances = crm.getRepresentationMallet().size();
    info.targetFeature = "class";
    info.task = "CLASSIFIER";
    info.trainerClass = "weka.classifiers.bayes.NaiveBayes";
    info.trainingCorpusName = "";
    info.engineClass = "gate.plugin.learningframework.engines.EngineWeka";
    info.modelClass = " weka.classifiers.bayes.NaiveBayes";
    info.save(directory);
    // finally save the Mallet corpus representation
    crm.savePipe(directory);
  }
  
  
  
  /**
   * Remove all instances but leave other information intact.
   * This removes all the instances but retains information about the features/attributes 
   * and how instances should get transformed or scaled.
   */
  public abstract void clear();

  // TODO: it may be good in some situations, if we could import data from external sources
  // directly, but not sure about the details. This is not implemented at the moment at all  
  
  
  
}
