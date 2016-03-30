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

import gate.AnnotationSet;
import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;

import gate.Controller;
import gate.Document;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.data.CorpusRepresentationMalletSeq;
import gate.plugin.learningframework.engines.AlgorithmClassification;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;

/**
 *
 */
@CreoleResource(
        name = "LF_TrainChunking",
        helpURL = "https://github.com/GateNLP/gateplugin-LearningFramework/wiki/LF_TrainChunking",
        comment = "Train a machine learning model for chunking")
public class LF_TrainChunking extends LF_TrainBase {

  /**
   *
   */
  private static final long serialVersionUID = 1L;

  private Logger logger = Logger.getLogger(LF_TrainChunking.class.getCanonicalName());

  protected URL dataDirectory;

  @RunTime
  @CreoleParameter(comment = "The directory where all data will be stored and read from")
  public void setDataDirectory(URL output) {
    dataDirectory = output;
  }

  public URL getDataDirectory() {
    return this.dataDirectory;
  }

  
  /**
   * The configuration file.
   *
   */
  private java.net.URL featureSpecURL;

  @RunTime
  @CreoleParameter(comment = "The feature specification file.")
  public void setFeatureSpecURL(URL featureSpecURL) {
    this.featureSpecURL = featureSpecURL;
  }

  public URL getFeatureSpecURL() {
    return featureSpecURL;
  }

  /**
   * The implementation to be used, such as Mallet.
   *
   */
  private AlgorithmClassification trainingAlgorithm;

  @RunTime
  @Optional
  @CreoleParameter(comment = "The algorithm to be used for training.")
  public void setTrainingAlgorithm(AlgorithmClassification algo) {
    this.trainingAlgorithm = algo;
  }

  public AlgorithmClassification getTrainingAlgorithm() {
    return this.trainingAlgorithm;
  }

  protected ScalingMethod scaleFeatures = ScalingMethod.NONE;

  @RunTime
  @CreoleParameter(defaultValue = "NONE", comment = "If and how to scale features. ")
  public void setScaleFeatures(ScalingMethod sf) {
    scaleFeatures = sf;
  }

  public ScalingMethod getScaleFeatures() {
    return scaleFeatures;
  }

  protected String sequenceSpan;

  @RunTime
  @Optional
  @CreoleParameter(comment = "For sequence learners, an annotation type "
          + "defining a meaningful sequence span. Ignored by non-sequence "
          + "learners.")
  public void setSequenceSpan(String seq) {
    this.sequenceSpan = seq;
  }

  public String getSequenceSpan() {
    return this.sequenceSpan;
  }
  
  protected String classAnnotationType;

  @RunTime
  @CreoleParameter(comment = "Annotation type containing/indicating the class.")
  public void setClassAnnotationType(String classType) {
    this.classAnnotationType = classType;
  }

  public String getClassAnnotationType() {
    return this.classAnnotationType;
  }


  private boolean haveSequenceTagger;

  private FeatureSpecification featureSpec = null;

  private File dataDir;

  private Engine engine;
  
  private int nrDocuments;
  
  private CorpusRepresentationMallet corpusRepresentation;
  
  @Override
  public Document process(Document doc) {
    if(isInterrupted()) {
      interrupted = false;
      throw new GateRuntimeException("Execution was requested to be interrupted");
    }
    // extract the required annotation sets,
    AnnotationSet inputAS = doc.getAnnotations(getInputASName());
    AnnotationSet instanceAS = inputAS.get(getInstanceType());
    // the classAS 
    AnnotationSet classAS = inputAS.get(getClassAnnotationType());
    // the nameFeatureName is always null for now!
    String nameFeatureName = null;
    if(haveSequenceTagger) {
      AnnotationSet sequenceAS = inputAS.get(getSequenceSpan());
      corpusRepresentation.add(instanceAS, sequenceAS, inputAS, classAS, null, TargetType.NOMINAL, nameFeatureName);
    } else {
      corpusRepresentation.add(instanceAS, null, inputAS, classAS, null, TargetType.NOMINAL, nameFeatureName);
    }
    nrDocuments++;
    return doc;
  }

  @Override
  public void afterLastDocument(Controller arg0, Throwable t) {
    System.out.println("LearningFramework: Starting training engine " + engine);
    System.out.println("Training set classes: "
            + corpusRepresentation.getRepresentationMallet().getPipe().getTargetAlphabet().toString().replaceAll("\\n", " "));
    System.out.println("Training set size: " + corpusRepresentation.getRepresentationMallet().size());
    if (corpusRepresentation.getRepresentationMallet().getDataAlphabet().size() > 20) {
      System.out.println("LearningFramework: Attributes " + corpusRepresentation.getRepresentationMallet().getDataAlphabet().size());
    } else {
      System.out.println("LearningFramework: Attributes " + corpusRepresentation.getRepresentationMallet().getDataAlphabet().toString().replaceAll("\\n", " "));
    }
      //System.out.println("DEBUG: instances are "+corpusRepresentation.getRepresentationMallet());

    corpusRepresentation.addScaling(getScaleFeatures());

    // Store some additional information in the info datastructure which will be saved with the model
    engine.getInfo().nrTrainingDocuments = nrDocuments;
    engine.getInfo().nrTrainingInstances = corpusRepresentation.getRepresentationMallet().size();
    
    // TODO: what if we do sequence tagging by classification???
    engine.getInfo().targetFeature = "LF_class";
    engine.getInfo().trainingCorpusName = corpus.getName();
    engine.getInfo().classAnnotationType = getClassAnnotationType();
    
    engine.trainModel(getAlgorithmParameters());
    logger.info("LearningFramework: Training complete!");
    engine.saveEngine(dataDir);
  }

  @Override
  protected void finishedNoDocument(Controller c, Throwable t) {
    logger.error("Processing finished, but got an error, no documents seen, or the PR was disabled in the pipeline - cannot train!");
  }

  @Override
  protected void beforeFirstDocument(Controller controller) {
    featureSpec = new FeatureSpecification(featureSpecURL);
    dataDir = gate.util.Files.fileFromURL(dataDirectory);
    if(!dataDir.exists()) throw new GateRuntimeException("Data directory not found: "+dataDir.getAbsolutePath());

    if(getClassAnnotationType()==null || getClassAnnotationType().isEmpty()) {
      throw new GateRuntimeException("classAnnotationType must be specified for sequence tagging!");
    }
    if (getTrainingAlgorithm() == null) {
      throw new GateRuntimeException("LearningFramework: no training algorithm specified");
    }
    if (getTrainingAlgorithm() == AlgorithmClassification.MALLET_SEQ_CRF) {
      if (getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter is required for MALLET_SEQ_CRF");
      }
      haveSequenceTagger = true;
    } else {
      if (getSequenceSpan() != null && !getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter must not be specified with non-sequence tagging algorithm");
      }
      haveSequenceTagger = false;
    }
    
    // we need to choose our representation based on if we have a classification algorithm or 
    // a sequence tagger
    if(haveSequenceTagger) {
      corpusRepresentation = new CorpusRepresentationMalletSeq(featureSpec.getFeatureInfo(), scaleFeatures);
    } else {
      corpusRepresentation = new CorpusRepresentationMalletTarget(featureSpec.getFeatureInfo(),scaleFeatures, TargetType.NOMINAL);      
    }
    engine = Engine.createEngine(trainingAlgorithm, getAlgorithmParameters(), corpusRepresentation);
    System.err.println("DEBUG: created the engine: " + engine);  
    nrDocuments = 0;
  }

}
