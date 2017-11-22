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
package gate.plugin.learningframework.data;

import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.Label;
import gate.Annotation;
import gate.AnnotationSet;
import gate.plugin.learningframework.LFUtils;
import gate.plugin.learningframework.features.FeatureExtractionDense;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecAttribute;
import gate.plugin.learningframework.features.SeqEncoder;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import static gate.plugin.learningframework.data.CorpusRepresentationMalletTarget.extractIndependentFeaturesHelper;
import java.util.ArrayList;
import static gate.plugin.learningframework.features.FeatureExtractionBase.*;
import gate.plugin.learningframework.features.FeatureExtractionMalletSparse;
import gate.plugin.learningframework.stats.StatsForFeatures;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Common base class for non Mallet volatile representations.
 * 
 * This is for representations which are "volatile" i.e. whenever something is 
 * added it is not kept in memory. Data could get immediately written to a file
 * or database or immediately passed on to an online training algorithm.
 * <p>
 * This tries to handle both sequence and non-sequence corpora.
 * 
 * @author Johann Petrak
 */
public class CorpusRepresentationVolatileDense2JsonStream extends CorpusRepresentationVolatileBase {

  public static final String DATA_FILE_NAME = "crvd.data.json";
  public static final String META_FILE_NAME = "crvd.meta.json";
  
  Logger logger = org.apache.log4j.Logger.getLogger(CorpusRepresentationVolatileDense2JsonStream.class);

  private FileOutputStream outStream; 
  private File outDir;
  private FeatureInfo featureInfo; // the feature info from the feauture specification
  List<String> fnames;
  StatsForFeatures stats = new StatsForFeatures();
  
  private final Object LOCKING_OBJECT = new Object();
  
  // some statistics we update while writing the corpus to the file and those
  // get included in the metadata written as well
  private int linesWritten = 0;
  
  
  /**
   * The constructor needs to specify the file where to save the instances to.
   * Note: if several threads use this instance, they should all share the 
   * just this one instance, and their calls to the add method will automatically
   * get synchronized.
   * @param outFile
   */
  public CorpusRepresentationVolatileDense2JsonStream(File outDir, FeatureInfo featureInfo) {
    this.outDir = outDir;
    this.featureInfo = featureInfo;
    this.fnames = featureSpecAttributes2FeatureNames(featureInfo.getAttributes());
    File outFile = new File(outDir,DATA_FILE_NAME);
    try {
      outStream = new FileOutputStream(outFile);
    } catch (FileNotFoundException ex) {
      throw new GateRuntimeException("Cannot open output stream to "+outFile,ex);
    }
    // At this point, since this is a dense representation, we already know
    // all the features and can set up to gather on-the-fly statistics about
    // them as we collect instances, if we wanted. 
    
    saveMetadata();
    // TODO: write the initial metadata file!!!
    // Format should be JSON!
  }
  
  /**
   * Prevent the addition of new features or feature values when instances are added.
   */
  @Override
  public void stopGrowth() {
    // TODO: this may be useful for sparse volatile representations, not used yet
  }
  
  /**
   * Enable the addition of new features or feature values when instances are added.
   * After a CorpusRepresentationMallet instance is created, growth is enabled by default.
   */
  @Override
  public void startGrowth() {
    // TODO: this may be useful for sparse volatile representations, not used yet
  }
      
  /** 
   * Add instances from the document.
   * 
   * This adds any instances to the corpus, i.e. convert annotations first to the internal
   * dense instance representation, then converts the dense instances to JSON and 
   * writes them to the stream which must have been opened when this corpus representation
   * was created.
   * <p>
   * NOTE: this method is automatically synchronized and should be save to use from multiple threads
   * <p>
   * !!!TODO: explain which methods are used by this to convert to dense internal
   * instance representation and then to the final output format.
   * 
   * @param instancesAS
   * @param sequenceAS
   * @param inputAS
   * @param classAS
   * @param targetFeatureName
   * @param targetType
   * @param instanceWeightFeature
   * @param nameFeatureName
   * @param seqEncoder 
   */
  @Override
  public void add(
          AnnotationSet instancesAS, 
          AnnotationSet sequenceAS, 
          AnnotationSet inputAS, 
          AnnotationSet classAS, 
          String targetFeatureName, 
          TargetType targetType, 
          String instanceWeightFeature, 
          String nameFeatureName, 
          SeqEncoder seqEncoder) {
    // first of all, distinguish between processing for sequences and for non-sequences
    // if the sequenceAS parameter is non-null we process sequences of instances, otherwise we process plain instances
    String json = "";
    if(sequenceAS == null) {
      // processing plain instances
      // For each instance, do this:
      List<Annotation> instanceAnnotations = instancesAS.inDocumentOrder();
      
      for (Annotation instanceAnnotation : instanceAnnotations) {

        InstanceRepresentation inst =
                annotation2instance(instanceAnnotation,inputAS,classAS,
                        targetFeatureName,targetType,instanceWeightFeature,seqEncoder);
        // now that we have the internal instance representation, send it off
        // by first converting to a json string and then sending the string to the output
        // file
        json = internal2Json(inst);
        writeData(json);
      }
    } else {
      // processing sequences
      for (Annotation sequenceAnnotation : sequenceAS.inDocumentOrder()) {
        List<InstanceRepresentation>  insts4seq =
        instancesForSequence(instancesAS, sequenceAnnotation, inputAS, classAS, targetFeatureName, targetType, seqEncoder);
        json = internal2Json(insts4seq);
        writeData(json);
      }
        
      
    }
  }

  public void writeData(String json) {
    try {
      synchronized(LOCKING_OBJECT) {
        outStream.write(json.getBytes("UTF-8"));
        linesWritten += 1;
      }
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not write generated JSON",ex);
    }    
  }

  
  public List<InstanceRepresentation> instancesForSequence(
          AnnotationSet instancesAS, Annotation sequenceAnnotation,
          AnnotationSet inputAS, AnnotationSet classAS, 
          String targetFeatureName, TargetType targetType, SeqEncoder seqEncoder
          ) {

    // get all the instances from within the sequence in order
    List<Annotation> instanceAnnotations = gate.Utils.getContainedAnnotations(instancesAS, sequenceAnnotation).inDocumentOrder();
    List<InstanceRepresentation>  insts4seq = new ArrayList<>(instanceAnnotations.size());
    // for each instance annotation, get the instance representation and add it to the list
    for (Annotation instanceAnnotation : instanceAnnotations) {
      InstanceRepresentation inst =
                annotation2instance(instanceAnnotation,inputAS,classAS,
                        targetFeatureName,targetType,null,seqEncoder);
      insts4seq.add(inst);
    }
    
    return insts4seq;
  }
  
  
  public InstanceRepresentation annotation2instance(Annotation instanceAnnotation,
          AnnotationSet inputAS, AnnotationSet classAS,
          String targetFeatureName, TargetType targetType,
          String instanceWeightFeature, SeqEncoder seqEncoder) {
    // create a new dense instance representation
    InstanceRepresentation inst = new InstanceRepresentationDenseVolatile();
    // first extract the independent features and add them to the instance representation
    for (FeatureSpecAttribute attr : featureInfo.getAttributes()) {
      inst = FeatureExtractionDense.extractFeature(inst, attr, inputAS, instanceAnnotation);
    }
    // now add the apropriate target information to the instance, depending on if we
    // do sequence tagging, classification, or regression
    if (classAS != null) {
      // extract the target as required for sequence tagging
      inst = FeatureExtractionDense.extractClassForSeqTagging(inst, classAS, instanceAnnotation, seqEncoder);
    } else {
      if (targetType == TargetType.NOMINAL) {
        inst = FeatureExtractionDense.extractClassTarget(inst, targetFeatureName, instanceAnnotation, inputAS);
      } else if (targetType == TargetType.NUMERIC) {
        inst = FeatureExtractionDense.extractNumericTarget(inst, targetFeatureName, instanceAnnotation, inputAS);
      }
    }
    // Set the instance weight, if there is one
    if (instanceWeightFeature != null && !instanceWeightFeature.isEmpty()) {
      // If the instanceWeightFeature is not specified we do not set any weight, but if it is 
      // specified then we either try to convert the value to double or use 1.0.
      double score = LFUtils.anyToDoubleOrElse(instanceAnnotation.getFeatures().get(instanceWeightFeature), 1.0);
      inst.setInstanceWeight(score);
    }
    // end of code to refactor into method
    // **************************************************************
    return inst;
  }
  
  
  /**
   * Convert the instance to json. 
   * 
   * Note: this is influenced by the feature info set in the corpus representation!
   * @param inst
   * @return 
   */
  public String internal2Json(InstanceRepresentation inst) {
    // can this be shared between multiple threads?
    ObjectMapper mapper = new ObjectMapper();
    List<Object> values = internal2array(inst);
    // the final instance is a list containing the values and the target (for now)
    ArrayList<Object> theInstance = new ArrayList<Object>(2);
    theInstance.add(values);
    theInstance.add(inst.getTargetValue());
    // now convert this to a JSON String
    String json = "";
    try {
      json = mapper.writeValueAsString(theInstance);
    } catch (JsonProcessingException ex) {
      throw new GateRuntimeException("Could not convert instance to json",ex);
    }
    return json;
  }

  public String internal2Json(List<InstanceRepresentation> instseq) {
    // can this be shared between multiple threads?
    ObjectMapper mapper = new ObjectMapper();
    // the format is a two-element list, where the first element is 
    // the list of all lists of independent features and the second element
    // is a list of all targets    
    List<Object> indepList = new ArrayList<>();
    List<Object> targetList = new ArrayList<>();
    for(InstanceRepresentation inst : instseq) {
      List<Object> values = internal2array(inst);
      indepList.add(values);
      targetList.add(inst.getTargetValue());
    }
    List<Object> finalList = new ArrayList<>();
    finalList.add(indepList);
    finalList.add(targetList);
    // now convert this to a JSON String
    String json = "";
    try {
      json = mapper.writeValueAsString(finalList);
    } catch (JsonProcessingException ex) {
      throw new GateRuntimeException("Could not convert instance sequence to json",ex);
    }
    return json;
  }

  
  private List<Object> internal2array(InstanceRepresentation inst) {    
    ArrayList<Object> values = new ArrayList<>();    
    for(String fname : fnames) {
      values.add(inst.getFeature(fname));
    }
    return values;
  }
  
  
  
  /**
   * Finish adding data to the CR. This may close or finish any channel for
   * passing on the data to a file, database or other sink. 
   * 
   * @param scaleFeatures 
   */
  public void finish() {
    // TODO: write the metadata file (again)!!!
    try {
      saveMetadata();
      outStream.close();
    } catch (IOException ex) {
      throw new GateRuntimeException("Error closing output stream for corpus representation", ex);
    }
  }

  @Override
  public Object getRepresentation() {
    throw new UnsupportedOperationException("Not supported by this corpus representation"); 
  }
  
  public String json4metadata() {
    try  {
      ObjectMapper mapper = new ObjectMapper();
      Map<String,Object> metadata = new HashMap<>();
      metadata.put("featureInfo", featureInfo);   
      metadata.put("featureNames",fnames);
      metadata.put("linesWritten",linesWritten);
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd,HH:mm:ss");      
      metadata.put("savedOn",sdf.format(new Date()));
      String json = mapper.writeValueAsString(metadata);
      return json;
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not serialize metadata",ex);
    }
    
  } 
  
  
  /**
   * Save the current metadata.
   */
  public void saveMetadata() {    
    try (
            FileOutputStream fos = new FileOutputStream(new File(outDir, META_FILE_NAME));) {
      String json = json4metadata();
      synchronized (LOCKING_OBJECT) {
        fos.write(json.getBytes("UTF-8"));
      }
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not write metadata to file", ex);
    }
  }

  
}
