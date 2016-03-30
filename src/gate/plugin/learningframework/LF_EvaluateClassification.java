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
import gate.plugin.learningframework.engines.AlgorithmClassification;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.engines.EvaluationResult;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;
import java.io.File;

/**
 *
 */
@CreoleResource(
        name = "LF_EvaluateClassification",
        helpURL = "https://github.com/GateNLP/gateplugin-LearningFramework/wiki/LF_EvaluateClassification",
        comment = "Train a machine learning model for classification")
public class LF_EvaluateClassification extends LF_TrainBase {

  private static final long serialVersionUID = -420477134626830002L;

  private final Logger logger = Logger.getLogger(LF_EvaluateClassification.class.getCanonicalName());

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

  private AlgorithmClassification trainingAlgorithm;

  @RunTime
  @Optional
  @CreoleParameter(comment = "The algorithm to be used for training the classifier")
  public void setTrainingAlgorithm(AlgorithmClassification algo) {
    this.trainingAlgorithm = algo;
  }

  public AlgorithmClassification getTrainingAlgorithm() {
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
  @CreoleParameter(comment = "The feature containing the class label")
  public void setTargetFeature(String classFeature) {
    this.targetFeature = classFeature;
  }

  public String getTargetFeature() {
    return this.targetFeature;
  }

  private CorpusRepresentationMalletTarget corpusRepresentation = null;
  private FeatureSpecification featureSpec = null;

  private Engine engine = null;

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
  
  
  protected EvaluationMethod evaluationMethod = EvaluationMethod.CROSSVALIDATION;
  @RunTime
  @Optional
  @CreoleParameter(comment = "Evaluation Method, not all algorithms may support all methods", defaultValue = "CROSSVALIDATION")
  public void setEvaluationMethod(EvaluationMethod val) {
    evaluationMethod = val;
  }
  
  public EvaluationMethod getEvaluationMethod() {
    return evaluationMethod;
  }
  
  
  protected int numberOfFolds = 10;
  @RunTime
  @Optional
  @CreoleParameter(comment = "Number of folds for the cross validation", defaultValue = "10")
  public void setNumberOfFolds(Integer val) {
    if(val < 2) {
      throw new GateRuntimeException("numberOfFolds must be > 1");
    }
    numberOfFolds = val;
  }
  
  public Integer getNumberOfFolds() {    
    return numberOfFolds;
  }
  
  protected double trainingFraction = 0.6667;
  @RunTime
  @Optional
  @CreoleParameter(comment = "Fraction of instances to use for training, > 0.0 and < 1.0", defaultValue = "0.6667")
  public void setTrainingFraction(Double val) {
    if(val <= 0.0 || val >= 1.0) {
      throw new GateRuntimeException("trainingFraction must be > 0.0 and < 1.0");
    }
    trainingFraction = val;
  }
  
  public Double getTrainingFraction() {
    return trainingFraction;
  }
  
  
  protected int numberOfRepeats = 1;
  @RunTime
  @Optional
  @CreoleParameter(comment = "Number of times to perform holdout evaluation to get an average", defaultValue = "1")
  public void setNumberOfRepeats(Integer val) {
    numberOfRepeats = val;
  }
  
  public Integer getNumberOfRepeats() {    
    return numberOfRepeats;
  }
  
  
  protected String classAnnotationType;

  @RunTime
  @Optional
  @CreoleParameter(comment = "Annotation type containing/indicating the class.")
  public void setClassAnnotationType(String classType) {
    this.classAnnotationType = classType;
  }

  public String getClassAnnotationType() {
    return this.classAnnotationType;
  }


  
  
  
  
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
    // the classAS 
    // the sequenceAS must be specified for a sequence tagging algorithm and most not be specified
    // for a non-sequence tagging algorithm!
    AnnotationSet sequenceAS = null;
    if (getTrainingAlgorithm() == AlgorithmClassification.MALLET_SEQ_CRF) {
      // NOTE: we already have checked earlier, that in that case, the sequenceSpan parameter is 
      // given!
      // NOTE: we do not actually support a sequence learner yet!
      sequenceAS = inputAS.get(getSequenceSpan());
    }
    // We allo a class annotation type to be specified: in this case, we will run the 
    // evaluation for the classification problem generated from the chunking problem.
    AnnotationSet classAS = null;
    String tfName = null;
    if(getClassAnnotationType() == null || getClassAnnotationType().isEmpty()) {
      tfName = getTargetFeature();
      classAS = null;
    } else {
      tfName = null;
      classAS = inputAS.get(getClassAnnotationType());
    }
    inputAS.get(getClassAnnotationType());
    String nameFeatureName = null;
    corpusRepresentation.add(instanceAS, sequenceAS, inputAS, classAS, tfName, TargetType.NOMINAL, nameFeatureName);
    nrDocuments++;
    return doc;
  }

  @Override
  public void afterLastDocument(Controller arg0, Throwable t) {
    System.out.println("LearningFramework: Starting evaluating engine " + engine);
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
    
    EvaluationResult er = engine.evaluate(getAlgorithmParameters(),evaluationMethod,numberOfFolds,trainingFraction,numberOfRepeats);
    logger.info("LearningFramework: Evaluation complete!");
    logger.info(er);
  }

  @Override
  protected void finishedNoDocument(Controller c, Throwable t) {
    logger.error("Processing finished, but no documents seen, cannot train!");
  }

  @Override
  protected void beforeFirstDocument(Controller controller) {
    if (getTrainingAlgorithm() == null) {
      throw new GateRuntimeException("LearningFramework: no training algorithm specified");
    }
    if (getTrainingAlgorithm() == AlgorithmClassification.MALLET_SEQ_CRF) {
      if (getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter is required for MALLET_SEQ_CRF");
      }
    } else {
      if (getSequenceSpan() != null && !getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter must not be specified with non-sequence tagging algorithm");
      }
    }

    AlgorithmClassification alg = getTrainingAlgorithm();
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
    corpusRepresentation = new CorpusRepresentationMalletTarget(featureSpec.getFeatureInfo(), scaleFeatures, TargetType.NOMINAL);
    System.err.println("DEBUG: created the corpusRepresentationMallet: " + corpusRepresentation);

    // Create the engine from the Algorithm parameter
    engine = Engine.createEngine(trainingAlgorithm, getAlgorithmParameters(), corpusRepresentation);
    
    System.err.println("DEBUG: created the engine: " + engine);

    nrDocuments = 0;
    
    System.err.println("DEBUG: setup of the training PR complete");    
  }

}
