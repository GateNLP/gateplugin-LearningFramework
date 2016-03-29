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
import gate.plugin.learningframework.data.CorpusRepresentation;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.data.CorpusRepresentationMalletSeq;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.TargetType;
import gate.util.Files;
import gate.util.GateRuntimeException;
import java.io.File;

/**
 *
 */
@CreoleResource(
        name = "LF_Export",
        helpURL = "https://github.com/GateNLP/gateplugin-LearningFramework/wiki/LF_Export",
        comment = "Export training instances in various formats for external training and analysis")
public class LF_Export extends LF_ExportBase {
  private static final long serialVersionUID = -420477191226830002L;
  

  private final Logger logger = Logger.getLogger(LF_Export.class.getCanonicalName());

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
  @CreoleParameter(comment = "If specified, export as classification or regression problem (currently required).")
  public void setTargetFeature(String classFeature) {
    this.targetFeature = classFeature;
  }

  public String getTargetFeature() {
    return this.targetFeature;
  }

  protected String classAnnotationType;
  @Optional
  @RunTime
  @CreoleParameter(comment = "Annotation type indicating the class for sequence tagging problems (not yet supported).")
  public void setClassAnnotationType(String classType) {
    this.classAnnotationType = classType;
  }

  public String getClassAnnotationType() {
    return this.classAnnotationType;
  }

  protected TargetType targetType;
  @RunTime
  @CreoleParameter(comment = "Target type: classification or regression problem?")
  public void setTargetType(TargetType val) { targetType = val; }
  public TargetType getTargetType() { return targetType; }
  
  
  // Depending on what the user wants, we use one of the two, so we avoid constant casting.
  private CorpusRepresentationMalletTarget corpusRepresentationClass = null;
  private CorpusRepresentationMalletSeq corpusRepresentationSeq = null;
  
  private FeatureSpecification featureSpec = null;

  private Engine engine = null;

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
  
  // ----------------------------------------------------------------------------
  
  private boolean haveSequenceProblem = false;  // true if a classAnnotationType is specified
  private boolean haveSequenceAlg    = false;  // tue if we export for MALLET_SEQ
  
  // TODO: 
  // Some export formats may need to directly write each document at execute time while
  // others first need the mallet corpus then do the export after the last document.
  // Make it easier to include either here!
  // Also: can we get most of what we need to do into the Exporter enum already??

  @Override
  public void execute(Document doc) {
    // extract the required annotation sets,
    AnnotationSet inputAS = doc.getAnnotations(getInputASName());
    AnnotationSet instanceAS = inputAS.get(getInstanceType());
    String nameFeatureName = null;
    if(haveSequenceAlg) {
      if(haveSequenceProblem) {
        corpusRepresentationSeq.add(instanceAS, inputAS.get(getSequenceSpan()), inputAS, inputAS.get(getClassAnnotationType()), null, targetType, nameFeatureName);
      } else {
        corpusRepresentationSeq.add(instanceAS, inputAS.get(getSequenceSpan()), inputAS, null, getTargetFeature(), targetType, nameFeatureName);        
      }
    } else {
      if(haveSequenceProblem) {
        corpusRepresentationClass.add(instanceAS, null, inputAS, inputAS.get(getClassAnnotationType()), null, targetType, nameFeatureName);
      } else {
        corpusRepresentationClass.add(instanceAS, null, inputAS, null, getTargetFeature(), targetType, nameFeatureName);
      }
    }
  }

  @Override
  public void afterLastDocument(Controller arg0, Throwable t) {
    File outDir = Files.fileFromURL(getDataDirectory());
    if(!haveSequenceAlg) { 
      corpusRepresentationClass.addScaling(getScaleFeatures());
      CorpusRepresentation.export(corpusRepresentationClass, exporter, outDir, getAlgorithmParameters());
    } else {
      CorpusRepresentation.export(corpusRepresentationSeq, exporter, outDir, getAlgorithmParameters());
    }
  }

  @Override
  protected void finishedNoDocument(Controller c, Throwable t) {
    logger.error("Processing finished, but got an error or no documents seen, cannot export!");
  }

  @Override
  protected void beforeFirstDocument(Controller controller) {
    
    if(getExporter() == Exporter.EXPORTER_MALLET_SEQ) {
      if(getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter is required for EXPORTER_MALLET_SEQ");
      } 
    } else {
      if(getSequenceSpan() != null && !getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter must not be specified unless EXPORTER_MALLET_SEQ is used");
      }
    }
    
    
    System.err.println("DEBUG: Before Documents.");
    
    // Read and parse the feature specification
    featureSpec = new FeatureSpecification(featureSpecURL);
    System.err.println("DEBUG Read the feature specification: "+featureSpec);

    if(getTargetFeature() != null && !getTargetFeature().isEmpty()) {
      // we want to export things as regression or classification problem, classAnnotationType must be empty
      haveSequenceProblem = false;
      if(getClassAnnotationType() != null && !getClassAnnotationType().isEmpty()) {
        throw new GateRuntimeException("Either targetFeature or classAnnotationType must be specified, not both");
      }
      if(getExporter() == Exporter.EXPORTER_MALLET_SEQ) {
        // this would have to create a MalletSeq representation and then find a way to export that!
        // corpusRepresentationSeq = new CorpusRepresentationMalletSeq(featureSpec.getFeatureInfo(), scaleFeatures);
        // System.err.println("DEBUG: created the corpusRepresentationMalletSeq: "+corpusRepresentationSeq);
      } else {
        corpusRepresentationClass = new CorpusRepresentationMalletTarget(featureSpec.getFeatureInfo(), scaleFeatures, targetType);
        System.err.println("DEBUG: created the corpusRepresentationMalletClass: "+corpusRepresentationClass);
      }
    } else if(getClassAnnotationType() != null && !getClassAnnotationType().isEmpty()) {
      haveSequenceProblem = true;
      if(getTargetFeature() != null && !getTargetFeature().isEmpty()) {
        throw new GateRuntimeException("Either targetFeature or classAnnotationType must be specified, not both");
      }
      if(getExporter() == Exporter.EXPORTER_MALLET_SEQ) {
        throw new GateRuntimeException("Exporting using MALLET_SEQ is not yet supported");
        // this would have to create a MalletSeq representation and then find a way to export that!
        // corpusRepresentationSeq = new CorpusRepresentationMalletSeq(featureSpec.getFeatureInfo(), scaleFeatures);
        // System.err.println("DEBUG: created the corpusRepresentationMalletSeq: "+corpusRepresentationSeq);
      } else {
        corpusRepresentationClass = new CorpusRepresentationMalletTarget(featureSpec.getFeatureInfo(), scaleFeatures,TargetType.NOMINAL);
        System.err.println("DEBUG: created the corpusRepresentationMalletClass: "+corpusRepresentationClass);        
      }
  }
    
    haveSequenceAlg = getSequenceSpan()!=null && !getSequenceSpan().isEmpty();
    
    
    System.err.println("DEBUG: setup of the export PR complete");
  }

}
