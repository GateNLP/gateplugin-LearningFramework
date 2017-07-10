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
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.data.CorpusRepresentationMalletSeq;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.SeqEncoder;
import gate.plugin.learningframework.features.SeqEncoderEnum;
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
  @CreoleParameter(comment = "Annotation type indicating the class for sequence tagging problems.")
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
  
  private SeqEncoderEnum seqEncoderEnum = SeqEncoderEnum.BIO;
  private SeqEncoder seqEncoder;
  /**
   * The sequence to classification algorithm to use.
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
  
  private boolean haveSequenceProblem = false;  // true if a classAnnotationType is specified
  private boolean haveSequenceAlg    = false;  // tue if we export for MALLET_SEQ
  
  // TODO: 
  // Some export formats may need to directly write each document at execute time while
  // others first need the mallet corpus then do the export after the last document.
  // Make it easier to include either here!
  // Also: can we get most of what we need to do into the Exporter enum already??

  @Override
  public Document process(Document doc) {
    // extract the required annotation sets,
    AnnotationSet inputAS = doc.getAnnotations(getInputASName());
    AnnotationSet instanceAS = inputAS.get(getInstanceType());
    String nameFeatureName = null;
    if(haveSequenceAlg) {
      if(haveSequenceProblem) {
        corpusRepresentationSeq.add(instanceAS, inputAS.get(getSequenceSpan()), inputAS, inputAS.get(getClassAnnotationType()), null, targetType, instanceWeightFeature, nameFeatureName, seqEncoder);
      } else {
        corpusRepresentationSeq.add(instanceAS, inputAS.get(getSequenceSpan()), inputAS, null, getTargetFeature(), targetType, instanceWeightFeature, nameFeatureName, seqEncoder);        
      }
    } else {
      if(haveSequenceProblem) {
        corpusRepresentationClass.add(instanceAS, null, inputAS, inputAS.get(getClassAnnotationType()), null, targetType, instanceWeightFeature, nameFeatureName, seqEncoder);
      } else {
        corpusRepresentationClass.add(instanceAS, null, inputAS, null, getTargetFeature(), targetType, instanceWeightFeature, nameFeatureName, seqEncoder);
      }
    }
    return doc;
  }

  @Override
  public void afterLastDocument(Controller arg0, Throwable t) {
    File outDir = Files.fileFromURL(getDataDirectory());
    
    if(!haveSequenceAlg) { 
      corpusRepresentationClass.finish();
      Exporter.export(corpusRepresentationClass, exporter, outDir, getInstanceType(), getAlgorithmParameters());
    } else {
      corpusRepresentationSeq.finish();
      Exporter.export(corpusRepresentationSeq, exporter, outDir, getInstanceType(), getAlgorithmParameters());
    }
  }

  @Override
  protected void finishedNoDocument(Controller c, Throwable t) {
    logger.error("Processing finished, but got an error or no documents seen, cannot export!");
  }

  @Override
  protected void beforeFirstDocument(Controller controller) {

    System.err.println("DEBUG: Before Documents.");
    if(getSeqEncoder().getEncoderClass() == null) {
      throw new GateRuntimeException("SeqEncoder class not yet implemented, please choose another one: "+getSeqEncoder());
    }
    
    try {
      seqEncoder = (SeqEncoder) getSeqEncoder().getEncoderClass().newInstance();
      seqEncoder.setOptions(getSeqEncoder().getOptions());
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not create SeqEncoder instance",ex);
    }
    
    
    /*
    if(getExporter() == Exporter.EXPORTER_MALLET_SEQ) {
      if(getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("SequenceSpan parameter is required for EXPORTER_MALLET_SEQ");
      } 
    } else {
    */
      if(getSequenceSpan() != null && !getSequenceSpan().isEmpty()) {
        // NOTE: we do not have a sequence exporter yet!!
        throw new GateRuntimeException("SequenceSpan parameter must not be specified unless Sequence exporter is used");
      }
    //}
    
    
    
    // Read and parse the feature specification
    featureSpec = new FeatureSpecification(featureSpecURL);
    System.err.println("DEBUG Read the feature specification: "+featureSpec);

    if(getTargetFeature() != null && !getTargetFeature().isEmpty()) {
      // we want to export things as regression or classification problem, classAnnotationType must be empty
      haveSequenceProblem = false;
      if(getClassAnnotationType() != null && !getClassAnnotationType().isEmpty()) {
        throw new GateRuntimeException("Either targetFeature or classAnnotationType must be specified, not both");
      }
      /*
      if(getExporter() == Exporter.EXPORTER_MALLET_SEQ) {
        // this would have to create a MalletSeq representation and then find a way to export that!
        // corpusRepresentationSeq = new CorpusRepresentationMalletSeq(featureSpec.getFeatureInfo(), scaleFeatures);
        // System.err.println("DEBUG: created the corpusRepresentationMalletSeq: "+corpusRepresentationSeq);
      } else {
      */
        corpusRepresentationClass = new CorpusRepresentationMalletTarget(featureSpec.getFeatureInfo(), scaleFeatures, targetType);
        System.err.println("DEBUG: created the corpusRepresentationMalletClass: "+corpusRepresentationClass);
      //}
    } else if(getClassAnnotationType() != null && !getClassAnnotationType().isEmpty()) {
      haveSequenceProblem = true;
      if(getTargetFeature() != null && !getTargetFeature().isEmpty()) {
        throw new GateRuntimeException("Either targetFeature or classAnnotationType must be specified, not both");
      }
      /*
      if(getExporter() == Exporter.EXPORTER_MALLET_SEQ) {
        throw new GateRuntimeException("Exporting using MALLET_SEQ is not yet supported");
        // this would have to create a MalletSeq representation and then find a way to export that!
        // corpusRepresentationSeq = new CorpusRepresentationMalletSeq(featureSpec.getFeatureInfo(), scaleFeatures);
        // System.err.println("DEBUG: created the corpusRepresentationMalletSeq: "+corpusRepresentationSeq);
      } else {
      */
        corpusRepresentationClass = new CorpusRepresentationMalletTarget(featureSpec.getFeatureInfo(), scaleFeatures,TargetType.NOMINAL);
        System.err.println("DEBUG: created the corpusRepresentationMalletClass: "+corpusRepresentationClass);        
      //}
  }
    
    haveSequenceAlg = false; //(getExporter() == Exporter.EXPORTER_MALLET_SEQ);
    
    
    System.err.println("DEBUG: setup of the export PR complete");
  }

}
