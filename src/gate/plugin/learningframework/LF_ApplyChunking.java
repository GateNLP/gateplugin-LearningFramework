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
@CreoleResource(name = "LF_ApplyChunking",
        helpURL = "https://github.com/GateNLP/gateplugin-LearningFramework/wiki/LF_ApplyChunking",
        comment = "Apply a trained chunking model to documents")
public class LF_ApplyChunking extends LearningFrameworkPRBase {

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
  @CreoleParameter(defaultValue = "LearningFramework")
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
          + "an annotation at application time. In the case of NER, the confidence "
          + "threshold is applied to the average for the entire entity.")
  public void setConfidenceThreshold(Double confidenceThreshold) {
    this.confidenceThreshold = confidenceThreshold;
  }

  public Double getConfidenceThreshold() {
    return this.confidenceThreshold;
  }


  String sequenceSpan;
  
  @RunTime
  @Optional
  @CreoleParameter(comment = "For sequence learners, an annotation type "
          + "defining a meaningful sequence span. Ignored by non-sequence "
          + "learners. Needs to be in the input AS.")
  public void setSequenceSpan(String seq) {
    sequenceSpan = seq;
  }

  public String getSequenceSpan() {
    return sequenceSpan;
  }
  
  

////////////////////////////////////////////////////////////////////////////

  private Engine engine;

  private File dataDir;


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
    if(engine.getAlgorithmKind()==AlgorithmKind.SEQUENCE_TAGGER) {
      // NOTE: we already have checked earlier, that in that case, the sequenceSpan parameter is 
      // given!
      sequenceAS = inputAS.get(getSequenceSpan());
    }
    //System.err.println("instanceAS.size="+instanceAS.size()+", inputAS.size="+inputAS.size()+"sequenceAS.size="+
    //        sequenceAS.size());
    List<GateClassification> gcs = engine.classify(
          instanceAS, inputAS,
          sequenceAS, getAlgorithmParameters());

    System.err.println("Got gcs="+gcs);
    AnnotationSet tmpAS = doc.getAnnotations("LF_SEQ_TMP");
    // since we specify the output annotation set tmpAS, new instance annotations are created there
    String featureName = engine.getInfo().targetFeature;    
    GateClassification.applyClassification(doc, gcs, Globals.outputClassFeature, tmpAS, null);
    // TODO: tmpAS only contains the instances we have just created, so we can probably get
    // read of the tmpInstanceAS parameter alltogether?
    AnnotationSet tmpInstanceAS = tmpAS.get(getInstanceType());
    AnnotationSet outputAS = doc.getAnnotations(getOutputASName());
    // TODO: maybe make confidence threshold more flexible for sequence annotations?
    String classAnnotationType = engine.getInfo().classAnnotationType;
    
    GateClassification.addSurroundingAnnotations(tmpAS, tmpInstanceAS, outputAS, classAnnotationType, getConfidenceThreshold());
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
    dataDir = gate.util.Files.fileFromURL(dataDirectory);

    // Restore the Engine
    engine = gate.plugin.learningframework.engines.Engine.loadEngine(dataDir, getAlgorithmParameters());
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
  }

}
