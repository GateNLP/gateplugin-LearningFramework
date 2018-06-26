
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
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.SeqEncoder;
import gate.plugin.learningframework.features.SeqEncoderEnum;
import gate.plugin.learningframework.features.TargetType;
import gate.util.Files;
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
  private CorpusExporter corpusExporter = null;
  
  @Override
  public void process(Document doc) {
    // extract the required annotation sets,
    AnnotationSet inputAS = doc.getAnnotations(getInputASName());
    AnnotationSet instanceAS = inputAS.get(getInstanceType());
    String nameFeatureName = null;
    if(haveSequenceAlg) {
      if(haveSequenceProblem) {
        AnnotationSet classAS = inputAS.get(classAnnotationTypesSet);
        corpusRepresentation.add(instanceAS, inputAS.get(getSequenceSpan()), inputAS, classAS, null, targetType, instanceWeightFeature, nameFeatureName, seqEncoder);
      } else {
        corpusRepresentation.add(instanceAS, inputAS.get(getSequenceSpan()), inputAS, null, getTargetFeature(), targetType, instanceWeightFeature, nameFeatureName, seqEncoder);        
      }
    } else {
      if(haveSequenceProblem) {
        AnnotationSet classAS = inputAS.get(classAnnotationTypesSet);
        corpusRepresentation.add(instanceAS, null, inputAS, classAS, null, targetType, instanceWeightFeature, nameFeatureName, seqEncoder);
      } else {
        corpusRepresentation.add(instanceAS, null, inputAS, null, getTargetFeature(), targetType, instanceWeightFeature, nameFeatureName, seqEncoder);
      }
    }
  }

  @Override
  protected void beforeFirstDocument(Controller controller) {

    if(getExporter() == null) {
      throw new GateRuntimeException("Exporter parameter is null");
    }
    
    System.err.println("DEBUG: Before Documents.");
    if(getSeqEncoder().getEncoderClass() == null) {
      throw new GateRuntimeException("SeqEncoder class not yet implemented, please choose another one: "+getSeqEncoder());
    }
    
    // Read and parse the feature specification
    featureSpec = new FeatureSpecification(featureSpecURL);
    System.err.println("DEBUG Read the feature specification: "+featureSpec);

    try {
      @SuppressWarnings("unchecked")
      Constructor<?> tmpc = getSeqEncoder().getEncoderClass().getDeclaredConstructor();
      seqEncoder = (SeqEncoder) tmpc.newInstance();
      seqEncoder.setOptions(getSeqEncoder().getOptions());
    } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
      throw new GateRuntimeException("Could not create SeqEncoder instance",ex);
    }
    
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
        throw new GateRuntimeException("One of targetFeature or classAnnotationTypes must be specified");
      }
      // we do not have a sequence tagging problem
      haveSequenceProblem = false;      
    }
    
    AlgorithmKind algkind = exporter.getAlgorithmKind();
    // now check if the problem is compatible with the algorithm kind:
    // here are all combinations and if they are compatible:
    // seq prob / SEQU:  yes
    // seq prob / REGR:  NO
    // seq prob / CLASS: yes
    // no seq   / SEQU:  NO
    // no seq   / REGR:  yes
    // no seq   / CLASS: yes
    if(haveSequenceProblem && algkind == AlgorithmKind.REGRESSOR) {
      throw new GateRuntimeException("Cannot use a regressor for a sequence tagging problem");
    } else if(!haveSequenceProblem && algkind == AlgorithmKind.SEQUENCE_TAGGER) {
      throw new GateRuntimeException("Cannot use a sequence tagger if it is not a sequence tagging problem");
    }

    // Check if we have a sequence span if the algorithm is a Sequence Tagger
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

    // create the corpus exporter
    URL effectiveDataDirectory;
    if(dataDirectory==null || dataDirectory.toExternalForm().isEmpty()) {
      try {
        effectiveDataDirectory = new File(".").getCanonicalFile().toURI().toURL();
      } catch(Exception ex) {
        throw new GateRuntimeException("Cannot use current running directory", ex);
      }
    } else {
      effectiveDataDirectory = dataDirectory;
    }
    System.err.println("DEBUG: using data directory:"+effectiveDataDirectory);
    corpusExporter = CorpusExporter.create(exporter, getAlgorithmParameters(), featureSpec.getFeatureInfo(), 
            getInstanceType(), effectiveDataDirectory);
    
    corpusRepresentation = corpusExporter.getCorpusRepresentation();

    
    System.err.println("DEBUG: setup of the export PR complete");
  }

  @Override
  public void afterLastDocument(Controller arg0, Throwable t) {
    
    corpusRepresentation.finishAdding();
    corpusExporter.export();
  }

  @Override
  protected void finishedNoDocument(Controller c, Throwable t) {
    logger.error("Processing finished, but got an error or no documents seen, cannot export!");
  }


  
  private Exception GateRuntimeException(String exporter_parameter_is_null) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

}
