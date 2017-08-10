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
import java.io.File;
import java.net.URL;

import org.apache.log4j.Logger;

import gate.Controller;
import gate.Document;
import gate.FeatureMap;
import gate.Utils;
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
    // TODO: we should put the gate.LF.target feature on each instance here, using the exact same
    // way to find the class (BIO for each class etc) as in FeatureExtraction.
    // We need to do this here because  the FeatureExtraction code is used at training and 
    // application time while the copying must only be done at training time!
    
    if(haveSequenceTagger) {
      AnnotationSet sequenceAS = inputAS.get(getSequenceSpan());
      corpusRepresentation.add(instanceAS, sequenceAS, inputAS, classAS, null, TargetType.NOMINAL, "", nameFeatureName);
    } else {
      corpusRepresentation.add(instanceAS, null, inputAS, classAS, null, TargetType.NOMINAL, "", nameFeatureName);
    }
    nrDocuments++;
    return doc;
  }

  @Override
  public void afterLastDocument(Controller arg0, Throwable t) {
    if(t!=null) {
      System.err.println("An exception occurred during processing of documents, no training will be done");
      System.err.println("Exception was "+t.getClass()+": "+t.getMessage());
      return;
    }
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

    corpusRepresentation.finish();

    // Store some additional information in the info datastructure which will be saved with the model
    engine.getInfo().nrTrainingDocuments = nrDocuments;
    engine.getInfo().nrTrainingInstances = corpusRepresentation.getRepresentationMallet().size();
    
    // TODO: what if we do sequence tagging by classification???
    engine.getInfo().targetFeature = "LF_class";
    engine.getInfo().trainingCorpusName = corpus.getName();
    engine.getInfo().classAnnotationType = getClassAnnotationType();
    
    engine.trainModel(gate.util.Files.fileFromURL(dataDirectory),
            getInstanceType(),
            getAlgorithmParameters());
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
    if (getTrainingAlgorithm().toString().contains("MALLET_SEQ_")) {
      if (getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter is required for MALLET_SEQ_*");
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
