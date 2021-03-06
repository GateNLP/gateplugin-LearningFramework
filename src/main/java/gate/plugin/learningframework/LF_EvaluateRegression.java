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
import gate.plugin.learningframework.engines.AlgorithmRegression;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.engines.EvaluationResult;
import gate.plugin.learningframework.engines.EvaluationResultRegression;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;

/**
 *
 */
@CreoleResource(
        name = "LF_EvaluateRegression",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_EvaluateRegression",
        comment = "Evaluate an algorithm and parameter settings for regression")
public class LF_EvaluateRegression extends LearningFrameworkPRBase {

  private static final long serialVersionUID = -4216855026883354L;

  private final Logger logger = Logger.getLogger(LF_EvaluateRegression.class.getCanonicalName());

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
  @CreoleParameter(comment = "The algorithm to be used for training the classifier")
  public void setTrainingAlgorithm(AlgorithmRegression algo) {
    this.trainingAlgorithm = algo;
  }

  public AlgorithmRegression getTrainingAlgorithm() {
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
  @CreoleParameter(comment = "The feature containing the target label")
  public void setTargetFeature(String targetFeature) {
    this.targetFeature = targetFeature;
  }

  public String getTargetFeature() {
    return this.targetFeature;
  }

  private CorpusRepresentation corpusRepresentation = null;
  private FeatureSpecification featureSpec = null;

  private Engine engine = null;

  protected String sequenceSpan;

  
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
    AnnotationSet sequenceAS = null;
    AnnotationSet classAS = null;
    String tfName = getTargetFeature();
    String nameFeatureName = null;
    corpusRepresentation.add(instanceAS, sequenceAS, inputAS, classAS, tfName, TargetType.NUMERIC, instanceWeightFeature, nameFeatureName, null);
  }

  @Override
  public void controllerStarted(Controller controller) {
    if (getDuplicateId() == 0) {
      if (getTrainingAlgorithm() == null) {
        throw new GateRuntimeException("LF_EvaluateRegression: no training algorithm specified");
      }
      if (getTargetFeature() == null || getTargetFeature().isEmpty()) {
        throw new GateRuntimeException("LF_EvaluateRegression: no target feature specified");
      }

      // Read and parse the feature specification
      featureSpec = new FeatureSpecification(featureSpecURL);

      FeatureInfo fi = featureSpec.getFeatureInfo();
      fi.setGlobalScalingMethod(scaleFeatures);

      // Create the engine from the Algorithm parameter
      engine = Engine.create(trainingAlgorithm, getAlgorithmParameters(), fi, TargetType.NUMERIC, dataDirURL);
      System.out.println(engine.toFormattedString());

      //System.err.println("DEBUG: created the engine: " + engine);
      corpusRepresentation = engine.getCorpusRepresentation();
      //System.err.println("DEBUG: created the corpusRepresentationMallet: " + corpusRepresentation);
      getSharedData().put("engine", engine);
      getSharedData().put("featureSpec", featureSpec);
      getSharedData().put("corpusRepresentation", corpusRepresentation);

    } else {
      // duplicateId > 0
      engine = (Engine) getSharedData().get("engine");
      System.out.println(engine.toFormattedString());          
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
      if (getCorpus() != null && er instanceof EvaluationResultRegression) {
        getCorpus().getFeatures().put("LearningFramework.rmse", ((EvaluationResultRegression) er).rmse);
      }
    }
  }



}
