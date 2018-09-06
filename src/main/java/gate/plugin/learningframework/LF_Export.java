
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

import gate.plugin.learningframework.export.Exporter;
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
import gate.plugin.learningframework.engines.AlgorithmKind;
import gate.plugin.learningframework.export.CorpusExporter;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.SeqEncoder;
import gate.plugin.learningframework.features.SeqEncoderEnum;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.IOException;
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
        name = "LF_Export",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_Export",
        comment = "Export training instances in various formats for external training and analysis")
public class LF_Export extends LF_ExportBase {

  private static final long serialVersionUID = 606764899130852772L;

  private final Logger logger = Logger.getLogger(LF_Export.class.getCanonicalName());

  protected URL dataDirectory;
  { 
    try {
      dataDirectory = new File(".").getCanonicalFile().toURI().toURL();
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not create URL for current directory to use as a default for dataDirectory",ex);
    }
  }

  @RunTime
  @Optional
  @CreoleParameter(comment = "The directory where all data will be stored and read from (default is current dir of Java process)")
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


  protected ScalingMethod scaleFeatures = ScalingMethod.NONE;

  @RunTime
  @Optional
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
  @CreoleParameter(comment = "If specified, export as classification or regression problem")
  public void setTargetFeature(String classFeature) {
    this.targetFeature = classFeature;
  }

  public String getTargetFeature() {
    return this.targetFeature;
  }

  protected List<String> classAnnotationTypes;
  protected Set<String> classAnnotationTypesSet;
  @RunTime
  @Optional
  @CreoleParameter(comment = "If specified, annotation types which indicate the class for sequence tagging")
  public void setClassAnnotationTypes(List<String> classTypes) {
    this.classAnnotationTypes = classTypes;
  }
  public List<String> getClassAnnotationTypes() {
    return this.classAnnotationTypes;
  }
  

  protected TargetType targetType = TargetType.NOMINAL;
  @RunTime
  @Optional
  @CreoleParameter(comment = "Target type: classification or regression problem?", defaultValue="NOMINAL")
  public void setTargetType(TargetType val) { targetType = val; }
  public TargetType getTargetType() { return targetType; }
  
  
  private CorpusRepresentation corpusRepresentation = null;
  
  private FeatureSpecification featureSpec = null;

  protected String sequenceSpan;

  @RunTime
  @Optional
  @CreoleParameter(comment = "Sequence tagging export is not yet supported")
  public void setSequenceSpan(String seq) {
    this.sequenceSpan = seq;
  }

  public String getSequenceSpan() {
    return this.sequenceSpan;
  }
  
  
  @RunTime
  @CreoleParameter(comment = "Export format, some formats allow finer configuration via the algorithmParameters")
  public void setExporter(Exporter value) {
    this.exporter = value;
  }

  public Exporter getExporter() {
    return exporter;
  }

  private Exporter exporter;
  
  private SeqEncoderEnum seqEncoderEnum = SeqEncoderEnum.BIO;
  private SeqEncoder seqEncoder;
  /**
   * The sequence to classification algorithm to use.
   * @param val which sequence encoder to use
   */
  @RunTime
  @Optional
  @CreoleParameter(comment = "The sequence to classification algorithm to use." )
  public void setSeqEncoder(SeqEncoderEnum val) {
    seqEncoderEnum = val;
  }
  
  public SeqEncoderEnum getSeqEncoder() {
    return seqEncoderEnum;
  }
  
  
  
  // ----------------------------------------------------------------------------
  
  private boolean haveSequenceProblem = false; 
  private boolean haveSequenceAlg    = false;  
  private boolean haveClusteringProblem = false;
  private boolean haveClusteringAlg = false;
  private CorpusExporter corpusExporter = null;
  
  @Override
  public void process(Document doc) {
    // extract the required annotation sets,
    AnnotationSet inputAS = doc.getAnnotations(getInputASName());
    AnnotationSet instanceAS = inputAS.get(getInstanceType());
    String nameFeatureName = null;
    if(haveClusteringAlg) {
      // we should already have checked at starting time that we do not have a sequence problem!
      corpusRepresentation.add(instanceAS, null, inputAS, null, null, TargetType.NONE, instanceWeightFeature, nameFeatureName, seqEncoder);      
    } else if(haveSequenceAlg) {  // this implies we have a sequence annotation type
      if(haveSequenceProblem) {  // this implies we have a classAS
        AnnotationSet classAS = inputAS.get(classAnnotationTypesSet);
        corpusRepresentation.add(instanceAS, inputAS.get(getSequenceSpan()), inputAS, classAS, null, targetType, instanceWeightFeature, nameFeatureName, seqEncoder);
      } else {
        corpusRepresentation.add(instanceAS, inputAS.get(getSequenceSpan()), inputAS, null, getTargetFeature(), targetType, instanceWeightFeature, nameFeatureName, seqEncoder);        
      }
    } else {  // not a sequence algorithm, we do not have a sequence annotation type
      if(haveSequenceProblem) {
        AnnotationSet classAS = inputAS.get(classAnnotationTypesSet);
        corpusRepresentation.add(instanceAS, null, inputAS, classAS, null, targetType, instanceWeightFeature, nameFeatureName, seqEncoder);
      } else {
        corpusRepresentation.add(instanceAS, null, inputAS, null, getTargetFeature(), targetType, instanceWeightFeature, nameFeatureName, seqEncoder);
      }
    }
  }

  @Override
  public void controllerStarted(Controller controller) {

    if(getExporter() == null) {
      throw new GateRuntimeException("Exporter parameter is null");
    }
    
    // OK, we try to figure out what kind of problem we have and which algorithm
    // kind we export for. Essentially we have the following possible problems:
    // * regression: predict a numeric target
    //   If: target feature is given and tgargetType is numeric
    // * classification: just predict a nominal label
    //   If: target feature is given and targetType is nominal
    // * sequence tagging/chunking: predict a nominal BIO-like label and postprocess
    //   classAnnotationTypes is given, targetFeature is empty, targetType must be nominal
    
    // We have the following kinds of algorithms
    // * regressor: given feature vector, predict numeric target
    // * classifier:  given feature vector, predict nominal target
    // * sequence tagger: given list of feature vectors, predict list of nominal targets
    // * clusterer: given feature vector, find clusters
    
    // This shows which problem is compatible with which algorithm:
    // PROBLEM  /  ALGORITHM  / COMPATIBLE
    // regression / regressor / yes
    // regression / clusterer / yes, ignore target
    // regression / clsasification, sequence tagger / no
    // classification / classifier / yes 
    // classification / sequence tagger / yes
    // classification / clusterer / yes, but ignore target
    // sequence tagging / sequence tagger / yes
    // sequence tagging / classifier / yes
    // clustering / clusterer / yes
    // clustering / not clusterer / no
   
    if(getClassAnnotationTypes() == null) {
      setClassAnnotationTypes(new ArrayList<>());
    }
    
    if(!getClassAnnotationTypes().isEmpty()) {      
      classAnnotationTypesSet = new HashSet<>();
      classAnnotationTypesSet.addAll(classAnnotationTypes);
      // having span annotations means that we have a sequence problem
      haveSequenceProblem = true;
      // in this case, the target feature must not be set
      if(getTargetFeature() != null && !getTargetFeature().isEmpty()) {
        throw new GateRuntimeException("Either targetFeature or classAnnotationTypes must be specified, not both");
      }
    } else {
      // we do not have class annotations, so we must have the target feature
      if(getTargetFeature() == null || getTargetFeature().isEmpty()) {
        haveClusteringProblem = true;
      } else {
        haveClusteringProblem = false;  // could be classification or regression
      }
      // we do not have a sequence tagging problem
      haveSequenceProblem = false;      
    }
    
    seqEncoder = null;
    // seqEncoder is ignored if we do not have a sequence tagging problem
    if(haveSequenceProblem) {
      if(getSeqEncoder().getEncoderClass() == null) {
        throw new GateRuntimeException("SeqEncoder class not yet implemented, please choose another one: "+getSeqEncoder());
      }
      try {
        @SuppressWarnings("unchecked")
        Constructor<?> tmpc = getSeqEncoder().getEncoderClass().getDeclaredConstructor();
        seqEncoder = (SeqEncoder) tmpc.newInstance();
       seqEncoder.setOptions(getSeqEncoder().getOptions());
     } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
        throw new GateRuntimeException("Could not create SeqEncoder instance",ex);
      }
    } // haveSequenceProblem
    
    
    // Check what kind of algorithm the user has chosen and if compatible with 
    // the pronlem. 
    // Currently, the only non-compatible situation is using a regressor with 
    // a non-regression problem or having a regression problem without a regressor
    AlgorithmKind algkind = exporter.getAlgorithmKind();
    if(haveSequenceProblem && algkind == AlgorithmKind.REGRESSOR) {
      throw new GateRuntimeException("Cannot use a regressor for a sequence tagging problem");
    } 
    if(algkind == AlgorithmKind.REGRESSOR && !getTargetType().equals(TargetType.NUMERIC)) {
      throw new GateRuntimeException("Cannot use a regressor without a numeric target type");
    }
    if(algkind != AlgorithmKind.REGRESSOR && getTargetType().equals(TargetType.NUMERIC)) {
      throw new GateRuntimeException("Cannot use a numeric target type without a regressor");
    }
    if(algkind == AlgorithmKind.CLUSTERING && haveSequenceProblem) {
      throw new GateRuntimeException("Cannot use a clusterer with sequence problem");
    }
    
    // if we have an sequence tagging algorithm, we need a sequence, so the sequence
    // span type must be given, but it must not be given unless we have a sequence
    // tagging algorithm    
    if(getExporter().getAlgorithmKind() == AlgorithmKind.SEQUENCE_TAGGER) {
      if(getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter is required for Sequence exporter");
      } 
    } else {    
      if(getSequenceSpan() != null && !getSequenceSpan().isEmpty()) {
        // NOTE: we do not have a sequence exporter yet!!
        throw new GateRuntimeException("SequenceSpan parameter must not be specified unless Sequence exporter is used");
      }
    }
    haveSequenceAlg = (algkind == AlgorithmKind.SEQUENCE_TAGGER);


    if (getDuplicateId() == 0) {
      // Read and parse the feature specification
      featureSpec = new FeatureSpecification(featureSpecURL);
      //System.err.println("DEBUG Read the feature specification: " + featureSpec);
      FeatureInfo finfo = featureSpec.getFeatureInfo();
      finfo.setGlobalScalingMethod(scaleFeatures);

      // create the corpus exporter
      URL effectiveDataDirectory;
      if (dataDirectory == null || dataDirectory.toExternalForm().isEmpty()) {
        try {
          effectiveDataDirectory = new File(".").getCanonicalFile().toURI().toURL();
        } catch (IOException ex) {
          throw new GateRuntimeException("Cannot use current running directory", ex);
        }
      } else {
        effectiveDataDirectory = dataDirectory;
      }
      //System.err.println("DEBUG: using data directory:" + effectiveDataDirectory);
      corpusExporter = CorpusExporter.create(exporter, getAlgorithmParameters(), finfo,
              getInstanceType(), effectiveDataDirectory);

      corpusRepresentation = corpusExporter.getCorpusRepresentation();
      getSharedData().put("corpusEporter", corpusExporter);
      getSharedData().put("featureSpec", featureSpec);
      getSharedData().put("corpusRepresentation", corpusRepresentation);
      System.out.println("INFO: Sequence tagging problem: "+haveSequenceProblem);
      System.out.println("INFO: Sequence tagging algorithm: "+haveSequenceAlg);
      System.out.println("INFO: clustering problem: "+haveClusteringProblem);
      System.out.println("INFO: clustering algorithm: "+haveClusteringAlg);
      
    } else {
      // duplicateId > 0
      corpusExporter = (CorpusExporter) getSharedData().get("corpusExporter");
      featureSpec = (FeatureSpecification) getSharedData().get("featureSpec");
      corpusRepresentation = (CorpusRepresentation) getSharedData().get("corpusRepresentation");
    }
    
  }

  @Override
  public void controllerFinished(Controller arg0, Throwable t) {
    if (getDuplicateId() == 0) {
      corpusRepresentation.finishAdding();
      System.out.println("INFO: LF_Export exporting data to "+getDataDirectory());
      corpusExporter.export();
    }
  }


}
