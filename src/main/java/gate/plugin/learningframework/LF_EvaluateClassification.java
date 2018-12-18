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
import gate.plugin.learningframework.data.CorpusRepresentation;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.AlgorithmClassification;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.engines.EvaluationResult;
import gate.plugin.learningframework.engines.EvaluationResultClassification;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;

/**
 *
 */
@CreoleResource(
        name = "LF_EvaluateClassification",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_EvaluateClassification",
        comment = "Evaluate an algorithm and parameter settings for classification")
public class LF_EvaluateClassification extends LearningFrameworkPRBase {

  private static final long serialVersionUID = -3542195765685547360L;

  private final transient Logger logger = Logger.getLogger(LF_EvaluateClassification.class.getCanonicalName());

  /**
   * The configuration file.
   *
   */
  private java.net.URL featureSpecURL = null;

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

  private transient CorpusRepresentation corpusRepresentation = null;
  private transient FeatureSpecification featureSpec = null;

  private transient Engine engine = null;

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

  protected String instanceWeightFeature = "";
  @RunTime
  @Optional
  @CreoleParameter(comment = "The feature that constains the instance weight. If empty, no instance weights are used",
          defaultValue="")
  public void setInstanceWeightFeature(String val) {
    instanceWeightFeature = val;
  }
  public String getInstanceWeightFeature() { return instanceWeightFeature; }
    
  private URL dataDirURL;

  @Override
  public void process(Document doc) {
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
    if (getTrainingAlgorithm() == AlgorithmClassification.MalletCRF_SEQ_MR) {
      // NOTE: we already have checked earlier, that in that case, the sequenceSpan parameter is 
      // given!
      // NOTE: we do not actually support a sequence learner yet!
      sequenceAS = inputAS.get(getSequenceSpan());
    }
    // We allo a class annotation type to be specified: in this case, we will run the 
    // evaluation for the classification problem generated from the chunking problem.
    AnnotationSet classAS;
    String tfName;
    if(getClassAnnotationType() == null || getClassAnnotationType().isEmpty()) {
      tfName = getTargetFeature();
      classAS = null;
    } else {
      tfName = null;
      classAS = inputAS.get(getClassAnnotationType());
    }
    inputAS.get(getClassAnnotationType());
    String nameFeatureName = null;
    corpusRepresentation.add(instanceAS, sequenceAS, inputAS, classAS, tfName, TargetType.NOMINAL, instanceWeightFeature, nameFeatureName, null);
  }

  @Override
  public void controllerStarted(Controller controller) {
    if (getDuplicateId() == 0) {
      if (getTrainingAlgorithm() == null) {
        throw new GateRuntimeException("LearningFramework: no training algorithm specified");
      }
      if (getTrainingAlgorithm() == AlgorithmClassification.MalletCRF_SEQ_MR) {
        if (getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
          throw new GateRuntimeException("SequenceSpan parameter is required for " + getTrainingAlgorithm());
        }
      } else {
        if (getSequenceSpan() != null && !getSequenceSpan().isEmpty()) {
          throw new GateRuntimeException("SequenceSpan parameter must not be specified with non-sequence tagging algorithm");
        }
      }

      AlgorithmClassification alg = getTrainingAlgorithm();

      System.err.println("DEBUG: Before Document.");
      System.err.println("  Training algorithm engine class is " + alg.getEngineClass());
      System.err.println("  Training algorithm algor class is " + alg.getTrainerClass());

      // Read and parse the feature specification
      featureSpec = new FeatureSpecification(featureSpecURL);
      System.err.println("DEBUG Read the feature specification: " + featureSpec);

      // Create the engine from the Algorithm parameter
      FeatureInfo fi = featureSpec.getFeatureInfo();
      fi.setGlobalScalingMethod(scaleFeatures);
      engine = Engine.create(trainingAlgorithm, getAlgorithmParameters(), fi, TargetType.NOMINAL, dataDirURL);

      System.err.println("DEBUG: created the engine: " + engine);

      corpusRepresentation = engine.getCorpusRepresentation();
      System.err.println("DEBUG: created the corpusRepresentationMallet: " + corpusRepresentation);

      getSharedData().put("engine", engine);
      getSharedData().put("featureSpec", featureSpec);
      getSharedData().put("corpusRepresentation", corpusRepresentation);
    } else {
      // duplicateId > 0
      engine = (Engine) getSharedData().get("engine");
      featureSpec = (FeatureSpecification) getSharedData().get("featureSpec");
      corpusRepresentation = (CorpusRepresentation) getSharedData().get("corpusRepresentation");
    }
  }

  @Override
  public void controllerFinished(Controller arg0, Throwable t) {
    if (getDuplicateId() == 0) {
      System.out.println("LearningFramework: Starting evaluating engine " + engine);
      if (corpusRepresentation instanceof CorpusRepresentationMallet) {
        CorpusRepresentationMallet crm = (CorpusRepresentationMallet) corpusRepresentation;
        System.out.println("Training set classes: "
                + crm.getRepresentationMallet().getPipe().getTargetAlphabet().toString().replaceAll("\\n", " "));
        System.out.println("Training set size: " + crm.getRepresentationMallet().size());
        if (crm.getRepresentationMallet().getDataAlphabet().size() > 20) {
          System.out.println("LearningFramework: Attributes " + crm.getRepresentationMallet().getDataAlphabet().size());
        } else {
          System.out.println("LearningFramework: Attributes " + crm.getRepresentationMallet().getDataAlphabet().toString().replaceAll("\\n", " "));
        }
        //System.out.println("DEBUG: instances are "+corpusRepresentation.getRepresentationMallet());
      }

      EvaluationResult er = engine.evaluate(getAlgorithmParameters(), evaluationMethod, numberOfFolds, trainingFraction, numberOfRepeats);
      logger.info("LearningFramework: Evaluation complete!");
      logger.info(er);
      if (getCorpus() != null && er instanceof EvaluationResultClassification) {
        getCorpus().getFeatures().put("LearningFramework.accuracyEstimate", ((EvaluationResultClassification) er).accuracyEstimate);
      }
    }
  }


}
