/*
 * Copyright (c) 1995-2015, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * Copyright 2015 South London and Maudsley NHS Trust and King's College London
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 */
package gate.plugin.learningframework;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;

import gate.AnnotationSet;
import gate.Controller;
import gate.Document;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.plugin.learningframework.engines.AlgorithmKind;
import gate.plugin.learningframework.engines.Engine;
import gate.util.GateRuntimeException;
import java.net.URL;

/**
 * <p>
 * Training, evaluation and application of ML in GATE.</p>
 */
@CreoleResource(name = "LF_ApplyClassification",
        helpURL = "https://github.com/GateNLP/gateplugin-LearningFramework/wiki/LF_ApplyClassification",
        comment = "Apply a trained classification model to documents")
public class LF_ApplyClassification extends LearningFrameworkPRBase {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  static final Logger logger = Logger.getLogger(LF_ApplyClassification.class.getCanonicalName());

  protected URL dataDirectory;

  @RunTime
  @CreoleParameter(comment = "The directory where all data will be stored and read from")
  public void setDataDirectory(URL output) {
    dataDirectory = output;
  }

  public URL getDataDirectory() {
    return this.dataDirectory;
  }

  
  
  protected String outputASName;

  @RunTime
  @Optional
  @CreoleParameter(comment="If not empty, place new annotations in the output set instead of updating the existing annotations", 
          defaultValue = "LearningFramework")
  public void setOutputASName(String oasn) {
    this.outputASName = oasn;
  }

  public String getOutputASName() {
    return this.outputASName;
  }

  /**
   * The confidence threshold for applying an annotation. In the case of NER, the confidence
   * threshold is applied to the average for the entire entity.
   *
   */
  private Double confidenceThreshold;

  @RunTime
  @CreoleParameter(defaultValue = "0.0", comment = "The minimum "
          + "confidence/probability for including "
          + "an annotation at application time.")
  public void setConfidenceThreshold(Double confidenceThreshold) {
    this.confidenceThreshold = confidenceThreshold;
  }

  public Double getConfidenceThreshold() {
    return this.confidenceThreshold;
  }

  protected String targetFeature;

  @RunTime
  @Optional
  @CreoleParameter(comment = "Name of class feature to add to the original "
          + "instance annotations. Default is the name that was used for training.",
          defaultValue = "")
  public void setTargetFeature(String name) {
    targetFeature = name;
  }

  public String getTargetFeature() {
    return targetFeature;
  }
  
  String sequenceSpan;
  
  @RunTime
  @Optional
  @CreoleParameter(comment = "For sequence learners, an annotation type "
          + "defining a meaningful sequence span. Ignored by non-sequence "
          + "learners. ")
  public void setSequenceSpan(String seq) {
    sequenceSpan = seq;
  }

  public String getSequenceSpan() {
    return sequenceSpan;
  }
  
  

////////////////////////////////////////////////////////////////////////////

  private Engine engine;

  private File savedModelDirectoryFile;

  // this is either what the user specifies as the PR parameter, or what we have stored 
  // with the saved model.
  private String targetFeatureToUse; 

  @Override
  public Document process(Document doc) {
    if(isInterrupted()) {
      interrupted = false;
      throw new GateRuntimeException("Execution was requested to be interrupted");
    }
    // extract the required annotation sets,
    AnnotationSet inputAS = doc.getAnnotations(getInputASName());
    AnnotationSet instanceAS = inputAS.get(getInstanceType());
    // the classAS must be null for classification
    // the sequenceAS must be specified for a sequence tagging algorithm and most not be specified
    // for a non-sequence tagging algorithm!
    AnnotationSet sequenceAS = null;
    if(engine.getAlgorithmKind() == AlgorithmKind.SEQUENCE_TAGGER) {
      // NOTE: we already have checked earlier, that in that case, the sequenceSpan parameter is 
      // given!
      sequenceAS = doc.getAnnotations(getSequenceSpan());
    }

    List<GateClassification> gcs = engine.classify(
          instanceAS, inputAS,
          sequenceAS, getAlgorithmParameters());
    
    AnnotationSet outputAS = null;
    if(getOutputASName()!=null || !getOutputASName().isEmpty()) {
      outputAS = doc.getAnnotations(getOutputASName());
    }

    GateClassification.applyClassification(doc, gcs, targetFeatureToUse, outputAS, null);    
    return doc;
  }


  @Override
  public void afterLastDocument(Controller arg0, Throwable throwable) {
    // No need to do anything, empty implementation!
  }

  public void finishedNoDocument(Controller arg0, Throwable throwable) {
    // no need to do anything
  }

  @Override
  protected void beforeFirstDocument(Controller controller) {

    // JP: this was moved from the dataDirectory setter to avoid problems
    // but we should really make sure that the learning is reloaded only 
    // if the URL has changed since the last time (if ever) it was loaded.
    savedModelDirectoryFile = gate.util.Files.fileFromURL(dataDirectory);

    // Restore the Engine
    engine = Engine.loadEngine(savedModelDirectoryFile, getAlgorithmParameters());
    System.out.println("LF-Info: model loaded is now "+engine);

    if (engine.getModel() == null) {
      throw new GateRuntimeException("Do not have a model, something went wrong.");
    } else {
      System.out.println("LearningFramework: Applying model "
              + engine.getModel().getClass() + " ...");
    }
    
    if(engine.getAlgorithmKind() == AlgorithmKind.SEQUENCE_TAGGER) {
      if(getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("sequenceSpan parameter must not be empty when a sequence tagging algorithm is used for classification");
      }
    }
    
    if(getTargetFeature()==null || getTargetFeature().isEmpty()) {
      // try to get the target feature from the model instead
      String targetFeatureFromModel = engine.getInfo().targetFeature;
      if(targetFeatureFromModel == null || targetFeatureFromModel.isEmpty()) {
        throw new GateRuntimeException("Not targetFeature parameter specified and none available from the model info file either.");
      }
      targetFeatureToUse = targetFeatureFromModel;
      System.err.println("Using target feature name from model: "+targetFeatureToUse);
    } else {
      targetFeatureToUse = getTargetFeature();
      System.err.println("Using target feature name from PR parameter: "+targetFeatureToUse);
    }
  }

}
