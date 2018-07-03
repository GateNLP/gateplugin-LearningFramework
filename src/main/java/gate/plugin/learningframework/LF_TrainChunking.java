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
import java.io.File;
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
import gate.plugin.learningframework.engines.AlgorithmKind;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.SeqEncoder;
import gate.plugin.learningframework.features.SeqEncoderEnum;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 */
@CreoleResource(
        name = "LF_TrainChunking",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_TrainChunking",
        comment = "Train a machine learning model for chunking")
public class LF_TrainChunking extends LearningFrameworkPRBase {

  private static final long serialVersionUID = 8365342794702016408L;

  private final Logger LOGGER = Logger.getLogger(LF_TrainChunking.class.getCanonicalName());

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

  private SeqEncoderEnum seqEncoderEnum = SeqEncoderEnum.BIO;
  private SeqEncoder seqEncoder;
  /**
   * The algorithm to create classification labels from chunks to use.
   * @param val One of the pre-defined enums
   */
  @RunTime
  @Optional
  @CreoleParameter(comment = "The sequence to classification algorithm to use.", defaultValue = "BIO" )
  public void setSeqEncoder(SeqEncoderEnum val) {
    seqEncoderEnum = val;
  }
  
  public SeqEncoderEnum getSeqEncoder() {
    return seqEncoderEnum;
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
  
  protected List<String> classAnnotationTypes;
  protected Set<String> classAnnotationTypesSet;
  @RunTime
  @CreoleParameter(comment = "Annotation types which indicate the class, at least one required.")
  public void setClassAnnotationTypes(List<String> classTypes) {
    this.classAnnotationTypes = classTypes;
  }
  public List<String> getClassAnnotationTypes() {
    return this.classAnnotationTypes;
  }
  

  private boolean haveSequenceTagger;

  private FeatureSpecification featureSpec = null;

  private File dataDirFile;

  private Engine engine;
  
  private CorpusRepresentation corpusRepresentation;
  
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
    AnnotationSet classAS = inputAS.get(classAnnotationTypesSet);
    // the nameFeatureName is always null for now!
    String nameFeatureName = null;
    // TODO: we should put the gate.LF.target feature on each instance here, using the exact same
    // way to find the class (BIO for each class etc) as in FeatureExtraction.
    // We need to do this here because  the FeatureExtraction code is used at training and 
    // application time while the copying must only be done at training time!
    
    if(haveSequenceTagger) {
      AnnotationSet sequenceAS = inputAS.get(getSequenceSpan());
      corpusRepresentation.add(instanceAS, sequenceAS, inputAS, classAS, null, TargetType.NOMINAL, "", nameFeatureName, seqEncoder);
    } else {
      corpusRepresentation.add(instanceAS, null, inputAS, classAS, null, TargetType.NOMINAL, "", nameFeatureName, seqEncoder);
    }
  }


  @Override
  public void controllerStarted(Controller controller) {

    if("file".equals(dataDirectory.getProtocol())) {
      dataDirFile = gate.util.Files.fileFromURL(dataDirectory);
    } else {
      throw new GateRuntimeException("Training is only possible if the dataDirectory URL is a file: URL");
    }
    if(getSeqEncoder().getEncoderClass() == null) {
      throw new GateRuntimeException("SeqEncoder class not yet implemented, please choose another one: "+getSeqEncoder());
    }
    
    try {
      System.err.println("Trying to create instance of "+getSeqEncoder().getEncoderClass());
      @SuppressWarnings("unchecked")
      Constructor<?> tmpc = getSeqEncoder().getEncoderClass().getDeclaredConstructor();
      seqEncoder = (SeqEncoder) tmpc.newInstance();
      seqEncoder.setOptions(getSeqEncoder().getOptions());
    } catch (IllegalAccessException | IllegalArgumentException | 
            InstantiationException | NoSuchMethodException | 
            SecurityException | InvocationTargetException ex) {
      throw new GateRuntimeException("Could not create SeqEncoder instance",ex);
    }
    
    // process the class annotation types: 
    if(getClassAnnotationTypes() == null) {
      setClassAnnotationTypes(new ArrayList<>());
    }
    if(getClassAnnotationTypes().isEmpty()) {
      throw new GateRuntimeException("Need at least one class annotation type!");
    }
    classAnnotationTypesSet = new HashSet<>();
    classAnnotationTypesSet.addAll(classAnnotationTypes);
    if(!dataDirFile.exists()) {
      throw new GateRuntimeException("Data directory not found: "+dataDirFile.getAbsolutePath());
    }

    if (getTrainingAlgorithm() == null) {
      throw new GateRuntimeException("LearningFramework: no training algorithm specified");
    }
    if (getTrainingAlgorithm().getAlgorithmKind() == AlgorithmKind.SEQUENCE_TAGGER) {
      if (getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter is required for sequence tagging algorithms");
      }
      haveSequenceTagger = true;
    } else {
      if (getSequenceSpan() != null && !getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter must not be specified with non-sequence tagging algorithm");
      }
      haveSequenceTagger = false;
    }
    
    if (getDuplicateId() == 0) {
      featureSpec = new FeatureSpecification(featureSpecURL);
      FeatureInfo fi = featureSpec.getFeatureInfo();
      fi.setGlobalScalingMethod(scaleFeatures);
      engine = Engine.create(trainingAlgorithm, getAlgorithmParameters(), fi, TargetType.NOMINAL, dataDirectory);
      corpusRepresentation = engine.getCorpusRepresentation();
      System.err.println("DEBUG: created the engine: " + engine + " with CR=" + engine.getCorpusRepresentation());
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
    // TODO: check for number of documents > 0!    
    if(t!=null) {
      System.err.println("An exception occurred during processing of documents, no training will be done");
      System.err.println("Exception was "+t.getClass()+": "+t.getMessage());
      return;
    }
    if(getSeenDocuments().get()==0) {
      throw new GateRuntimeException("No documents seen, cannot train");
    }
    if (getDuplicateId() == 0) {
      System.out.println("LearningFramework: Starting training engine " + engine);
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
      }
      // NOTE: some parts of the info instance should/could get updated inside the
      // engine as part of the trainModel() method (the EngineMB engines delegate
      // this into their own updateInfo() method). 
      engine.getInfo().nrTrainingInstances = corpusRepresentation.nrInstances();
      engine.getInfo().classAnnotationTypes = getClassAnnotationTypes();
      // engine.getInfo().classLabels = corpusRepresentation.classLabels();
      // Store some additional information in the info datastructure which will be saved with the model
      engine.getInfo().nrTrainingDocuments = getSeenDocuments().get();

      // TODO: what if we do sequence tagging by classification???
      engine.getInfo().targetFeature = "LF_class";
      engine.getInfo().trainingCorpusName = corpus.getName();
      engine.getInfo().classAnnotationTypes = getClassAnnotationTypes();

      if (seqEncoder != null) {
        engine.getInfo().seqEncoderClass = seqEncoder.getClass().getName();
        engine.getInfo().seqEncoderOptions = seqEncoder.getOptions().toString();
      }

      engine.trainModel(gate.util.Files.fileFromURL(dataDirectory),
              getInstanceType(),
              getAlgorithmParameters());
      LOGGER.info("LearningFramework: Training complete!");
      engine.saveEngine(dataDirFile);
    } // duplicateId == 0
  }


}
