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
import java.net.URL;

import org.apache.log4j.Logger;

import gate.Controller;
import gate.Document;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.engines.AlgorithmRegression;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;
import java.io.File;

/**
 *
 */
@CreoleResource(
        name = "LF_TrainRegression",
        helpURL = "https://github.com/GateNLP/gateplugin-LearningFramework/wiki/LF_TrainRegression",
        comment = "Train a machine learning model for regression")
public class LF_TrainRegression extends LF_TrainBase {

  private static final long serialVersionUID = -420477191226830002L;

  private final Logger logger = Logger.getLogger(LF_TrainRegression.class.getCanonicalName());

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

  private AlgorithmRegression trainingAlgorithm;

  @RunTime
  @Optional
  @CreoleParameter(comment = "The algorithm to be used for training.")
  public void setTrainingAlgorithm(AlgorithmRegression algo) {
    this.trainingAlgorithm = algo;
  }

  public AlgorithmRegression getTrainingAlgorithm() {
    return this.trainingAlgorithm;
  }

  protected String algorithmJavaClass;

  @RunTime
  @Optional
  @CreoleParameter(comment = "The Java class of the training algorithm to use, only used if SPECIFY_CLASS is selected")
  public void setAlgorithmJavaClass(String className) {
    algorithmJavaClass = className;
  }

  public String getAlgorithmJavaClass() {
    return algorithmJavaClass;
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

  protected String targetFeature;

  @RunTime
  @Optional
  @CreoleParameter(comment = "The feature containing the target value")
  public void setTargetFeature(String classFeature) {
    this.targetFeature = classFeature;
  }

  public String getTargetFeature() {
    return this.targetFeature;
  }

  private CorpusRepresentationMalletTarget corpusRepresentation = null;
  private FeatureSpecification featureSpec = null;

  private Engine engine = null;

  
  private int nrDocuments;
  
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
    // the classAS is always null for the regression task!
    // the sequenceAS is always null for the regression task!
    // the nameFeatureName is always null for now!
    String nameFeatureName = null;
    corpusRepresentation.add(instanceAS, null, inputAS, null, getTargetFeature(), TargetType.NUMERIC, nameFeatureName);
    return doc;
  }

  @Override
  public void afterLastDocument(Controller arg0, Throwable t) {
    System.out.println("LearningFramework: Starting training engine " + engine);
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
    engine.getInfo().targetFeature = getTargetFeature();
    engine.getInfo().trainingCorpusName = corpus.getName();
    
    engine.trainModel(gate.util.Files.fileFromURL(dataDirectory),
            getInstanceType(),
            getAlgorithmParameters());
    logger.info("LearningFramework: Training complete!");
    engine.saveEngine(dataDir);
  }

  @Override
  protected void finishedNoDocument(Controller c, Throwable t) {
    logger.error("Processing finished, but no documents seen, got an error, or the PR was disabled in the pipeline - cannot train!");
  }

  @Override
  protected void beforeFirstDocument(Controller controller) {
    dataDir = gate.util.Files.fileFromURL(dataDirectory);
    if(!dataDir.exists()) throw new GateRuntimeException("Data directory not found: "+dataDir.getAbsolutePath());

    if (getTrainingAlgorithm() == null) {
      throw new GateRuntimeException("LearningFramework: no training algorithm specified");
    }
    AlgorithmRegression alg = getTrainingAlgorithm();
    // if an algorithm is specified where the name ends in "SPECIFY_CLASS" use the 
    // algorithmJavaClass 
    if (getTrainingAlgorithm().toString().endsWith("SPECIFY_CLASS")) {
      if (getAlgorithmJavaClass() == null || getAlgorithmJavaClass().isEmpty()) {
        throw new GateRuntimeException("AlgorithmClass parameter must be specified when " + getTrainingAlgorithm() + " is chosen");
      }
      Class clazz = null;
      try {
        clazz = Class.forName(getAlgorithmJavaClass());
      } catch (ClassNotFoundException ex) {
        throw new GateRuntimeException("Could not load algorithm class: " + getAlgorithmJavaClass(), ex);
      }
      alg.setTrainerClass(clazz);
    }

    System.err.println("DEBUG: Before Document.");
    System.err.println("  Training algorithm engine class is " + alg.getEngineClass());
    System.err.println("  Training algorithm algor class is " + alg.getTrainerClass());

    // Read and parse the feature specification
    featureSpec = new FeatureSpecification(featureSpecURL);
    System.err.println("DEBUG Read the feature specification: " + featureSpec);

    // create the corpus representation for creating the training instances
    corpusRepresentation = new CorpusRepresentationMalletTarget(featureSpec.getFeatureInfo(), scaleFeatures, TargetType.NUMERIC);
    System.err.println("DEBUG: created the corpusRepresentationMallet: " + corpusRepresentation);

    // Create the engine from the Algorithm parameter
    engine = Engine.createEngine(trainingAlgorithm, getAlgorithmParameters(), corpusRepresentation);
    
    System.err.println("DEBUG: created the engine: " + engine);

    nrDocuments = 0;
    
    System.err.println("DEBUG: setup of the training PR complete");    
  }

}
