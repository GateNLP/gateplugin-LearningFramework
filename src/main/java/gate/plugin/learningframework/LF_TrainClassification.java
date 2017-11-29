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

import gate.Annotation;
import gate.AnnotationSet;
import java.net.URL;

import org.apache.log4j.Logger;

import gate.Controller;
import gate.Document;
import gate.FeatureMap;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.plugin.learningframework.data.CorpusRepresentation;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.AlgorithmClassification;
import gate.plugin.learningframework.engines.AlgorithmKind;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;
import java.io.File;

/**
 *
 */
@CreoleResource(
        name = "LF_TrainClassification",
        helpURL = "https://github.com/GateNLP/gateplugin-LearningFramework/wiki/LF_TrainClassification",
        comment = "Train a machine learning model for classification")
public class LF_TrainClassification extends LF_TrainBase {

  private static final long serialVersionUID = -420477191226830002L;

  private final Logger logger = Logger.getLogger(LF_TrainClassification.class.getCanonicalName());

  protected URL dataDirectory;

  @RunTime
  @CreoleParameter(comment = "The directory where all data will be stored and read from")
  public void setDataDirectory(URL output) {
    dataDirectory = output;
  }

  public URL getDataDirectory() {
    return this.dataDirectory;
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

  private CorpusRepresentation corpusRepresentation = null;
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
  
  private int nrDocuments;
  
  private File dataDirFile;

  @Override
  public Document process(Document doc) {
    if(isInterrupted()) {
      interrupted = false;
      throw new GateRuntimeException("Execution was requested to be interrupted");
    }
    nrDocuments += 1;
    // extract the required annotation sets,
    AnnotationSet inputAS = doc.getAnnotations(getInputASName());
    AnnotationSet instanceAS = inputAS.get(getInstanceType());
    // the sequenceAS must be specified for a sequence tagging algorithm and most not be specified
    // for a non-sequence tagging algorithm!
    AnnotationSet sequenceAS = null;
    if (getTrainingAlgorithm() == AlgorithmClassification.MALLET_SEQ_CRF) {
      // NOTE: we already have checked earlier, that in that case, the sequenceSpan parameter is 
      // given!
      sequenceAS = inputAS.get(getSequenceSpan());
    }

    // before we add the instances, put the LF internal class feature gate.LF.target on the instance
    // annotations!
    // This makes sure that for training, any attribute which makes use of the internal class feature
    // will get the correct value from the training set, even if the gate.LF.target feature has
    // been set differently before this PR is run.
    // While we are at it, we also check that the target feature actually has a non-null
    // value. We throw an exception if there is an instance without a target value!
    for(Annotation inst : instanceAS) {
      FeatureMap fm = inst.getFeatures();
      Object targetVal = fm.get(getTargetFeature());
      if(null==targetVal) {
        throw new GateRuntimeException("Target value is null in document "+document.getName()+" for instance "+inst);
      }
      fm.put("gate.LF.target",targetVal);
    }

    
    // the classAS is always null for the classification task!
    // the nameFeatureName is always null for now!
    
    String nameFeatureName = null;
    corpusRepresentation.add(instanceAS, sequenceAS, inputAS, null, getTargetFeature(), TargetType.NOMINAL, instanceWeightFeature, nameFeatureName, null);
    return doc;
  }

  @Override
  protected void beforeFirstDocument(Controller controller) {
    if("file".equals(dataDirectory.getProtocol()))
      dataDirFile = gate.util.Files.fileFromURL(dataDirectory);
    else
      throw new GateRuntimeException("Training is only possible if the dataDirectory URL is a file: URL");
    if(!dataDirFile.exists()) throw new GateRuntimeException("Data directory not found: "+dataDirFile.getAbsolutePath());

    if (getTrainingAlgorithm() == null) {
      throw new GateRuntimeException("LearningFramework: no training algorithm specified");
    }
    if (getTrainingAlgorithm().getAlgorithmKind() == AlgorithmKind.SEQUENCE_TAGGER) {
      if (getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter is required for sequence tagging algorithm");
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
    engine = Engine.createEngine(trainingAlgorithm, getAlgorithmParameters(), fi, TargetType.NOMINAL, dataDirectory);    
    corpusRepresentation = (CorpusRepresentation)engine.getCorpusRepresentation();
    System.err.println("DEBUG: created the engine: " + engine + " with CR="+corpusRepresentation);

    nrDocuments = 0;
    
    System.err.println("DEBUG: setup of the training PR complete");    
  }
  
  
  @Override
  public void afterLastDocument(Controller arg0, Throwable t) {
    if(t!=null) {
      System.err.println("An exception occurred during processing of documents, no training will be done");
      System.err.println("Exception was "+t.getClass()+": "+t.getMessage());
      return;
    }
    System.out.println("LearningFramework: Starting training engine " + engine);
    if(corpusRepresentation instanceof CorpusRepresentationMallet) {
      CorpusRepresentationMallet crm = (CorpusRepresentationMallet)corpusRepresentation;
      System.out.println("Training set classes: "
              + crm.getRepresentationMallet().getPipe().getTargetAlphabet().toString().replaceAll("\\n", " "));
      System.out.println("Training set size: " + crm.getRepresentationMallet().size());
      if (crm.getRepresentationMallet().getDataAlphabet().size() > 20) {
        System.out.println("LearningFramework: Attributes " + crm.getRepresentationMallet().getDataAlphabet().size());
      } else {
        System.out.println("LearningFramework: Attributes " + crm.getRepresentationMallet().getDataAlphabet().toString().replaceAll("\\n", " "));
      }
    }
    engine.getInfo().nrTrainingInstances = corpusRepresentation.nrInstances();
    
    // Store some additional information in the info datastructure which will be saved with the model
    engine.getInfo().nrTrainingDocuments = nrDocuments;
    engine.getInfo().targetFeature = getTargetFeature();
    engine.getInfo().trainingCorpusName = corpus.getName();
    
    engine.trainModel(gate.util.Files.fileFromURL(dataDirectory),
            getInstanceType(),
            getAlgorithmParameters());
    logger.info("LearningFramework: Training complete!");
    engine.saveEngine(dataDirFile);
  }

  @Override
  protected void finishedNoDocument(Controller c, Throwable t) {
    logger.error("Processing finished, but got an error, no documents seen, or the PR was disabled in the pipeline - cannot train!");
  }


}
