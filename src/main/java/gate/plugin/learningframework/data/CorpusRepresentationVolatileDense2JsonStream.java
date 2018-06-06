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
import java.util.ArrayList;
import static gate.plugin.learningframework.features.FeatureExtractionBase.*;
import gate.plugin.learningframework.stats.Stats;
import gate.plugin.learningframework.stats.StatsForFeatures;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

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

  private Logger LOGGER = org.apache.log4j.Logger.getLogger(CorpusRepresentationVolatileDense2JsonStream.class);

  private FileOutputStream outStream;
  private File outDir;
  private File outDataFile;
  private File outMetaFile;
  private FeatureInfo featureInfo; // the feature info from the feauture specification
  private List<String> fnames;
  private StatsForFeatures stats = new StatsForFeatures();
  private SummaryStatistics seqLenStats = new SummaryStatistics(); // if we have sequences, stats about the lengths

  public List<String> getTargetLabels() {
    Stats statsForTarget = stats.getStatistics(StatsForFeatures.KEY_FOR_TARGET);
    if (statsForTarget != null && statsForTarget.isString()) {
      return statsForTarget.stringValues();
    } else {
      return new ArrayList<>();
    }
  }

  public int getNrFeatures() {
    return fnames.size();
  }

  // The following flag is either unset (null) or indicates that the corpus representation
  // has received sequence representations. Once a sequence or non-sequence has been added,
  // any attempt to add the other type will lead to an exception.
  protected Boolean isSequence = null;

  public File getDataFile() {
    return outDataFile;
  }

  public File getMetaFile() {
    if (outMetaFile == null) {
      outMetaFile = new File(outDir, META_FILE_NAME);
    }
    return outMetaFile;
  }

  private final Object LOCKING_OBJECT = new Object();

  // some statistics we update while writing the corpus to the file and those
  // get included in the metadata written as well
  private int linesWritten = 0;

  @Override
  public int nrInstances() {
    return linesWritten;
  }

  /**
   * The constructor needs to specify the file where to save the instances to.
   * 
   * Note: if several threads use this instance, they should all share the just
   * this one instance, and their calls to the add method will automatically get
   * synchronized.
   *
   * @param outDir directory where to save the instances
   * @param featureInfo FeatureInfo instance
   */
  public CorpusRepresentationVolatileDense2JsonStream(File outDir, FeatureInfo featureInfo) {
    this.outDir = outDir;
    this.featureInfo = featureInfo;
    this.fnames = featureSpecAttributes2FeatureNames(featureInfo.getAttributes());
    // NOTE: the actual opening of the output file only happens when we initialise 
    // 
  }

  /**
   * Prevent the addition of new features or feature values when instances are
   * added.
   */
  @Override
  public void stopGrowth() {
    // TODO: this may be useful for sparse volatile representations, not used yet
  }

  /**
   * Enable the addition of new features or feature values when instances are
   * added. After a CorpusRepresentationMallet instance is created, growth is
   * enabled by default.
   */
  @Override
  public void startGrowth() {
    // TODO: this may be useful for sparse volatile representations, not used yet
  }

  /**
   * Add instances from the document.
   *
   * This adds any instances to the corpus, i.e. convert annotations first to
   * the internal dense instance representation, then converts the dense
   * instances to JSON and writes them to the stream which must have been opened
   * when this corpus representation was created.
   * <p>
   * NOTE: this method is automatically synchronized and should be save to use
   * from multiple threads
   * <p>
   * !!!TODO: explain which methods are used by this to convert to dense
   * internal instance representation and then to the final output format.
   *
   * @param instancesAS instance annotation set
   * @param sequenceAS sequence annotation set
   * @param inputAS input annotation set
   * @param classAS class annotation set
   * @param targetFeatureName target feature name
   * @param targetType type of target
   * @param instanceWeightFeature weight feature name
   * @param nameFeatureName name feature name
   * @param seqEncoder sequence encoder instance
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
    String json;
    if (sequenceAS == null) {
      if (isSequence == null) {
        isSequence = false;
      } else if (isSequence) {
        throw new GateRuntimeException("Trying to add non-sequence after sequence has already been added");
      }
      // processing plain instances
      // For each instance, do this:
      List<Annotation> instanceAnnotations = instancesAS.inDocumentOrder();

      for (Annotation instanceAnnotation : instanceAnnotations) {

        InstanceRepresentation inst
                = labeledAnnotation2Instance(instanceAnnotation, inputAS, classAS,
                        targetFeatureName, targetType, instanceWeightFeature, seqEncoder);
        // now that we have the internal instance representation, send it off
        // by first converting to a json string and then sending the string to the output
        // file
        json = internal2Json(inst,false);
        writeData(json);
      }
    } else {
      if (isSequence == null) {
        isSequence = true;
      } else if (!isSequence) {
        throw new GateRuntimeException("Trying to add sequence after non-sequence has already been added");
      }
      // processing sequences
      for (Annotation sequenceAnnotation : sequenceAS.inDocumentOrder()) {
        List<InstanceRepresentation> insts4seq
                = instancesForSequence(instancesAS, sequenceAnnotation, inputAS, classAS, targetFeatureName, targetType, seqEncoder);
        seqLenStats.addValue(insts4seq.size());
        json = internal2Json(insts4seq,false);
        writeData(json);
      }

    }
  }

  public void writeData(String json) {
    try {
      synchronized (LOCKING_OBJECT) {
        outStream.write(json.getBytes("UTF-8"));
        outStream.write("\n".getBytes("UTF-8"));
        linesWritten += 1;
      }
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not write generated JSON", ex);
    }
  }

  public List<InstanceRepresentation> instancesForSequence(
          AnnotationSet instancesAS, Annotation sequenceAnnotation,
          AnnotationSet inputAS, AnnotationSet classAS,
          String targetFeatureName, TargetType targetType, SeqEncoder seqEncoder
  ) {

    // get all the instances from within the sequence in order
    List<Annotation> instanceAnnotations = gate.Utils.getContainedAnnotations(instancesAS, sequenceAnnotation).inDocumentOrder();
    List<InstanceRepresentation> insts4seq = new ArrayList<>(instanceAnnotations.size());
    // for each instance annotation, get the instance representation and add it to the list
    for (Annotation instanceAnnotation : instanceAnnotations) {
      InstanceRepresentation inst
              = labeledAnnotation2Instance(instanceAnnotation, inputAS, classAS,
                      targetFeatureName, targetType, null, seqEncoder);
      insts4seq.add(inst);
    }

    return insts4seq;
  }

  public List<InstanceRepresentation> unlabeledInstancesForSequence(
          AnnotationSet instancesAS, Annotation sequenceAnnotation,
          AnnotationSet inputAS
  ) {

    // get all the instances from within the sequence in order
    List<Annotation> instanceAnnotations = gate.Utils.getContainedAnnotations(instancesAS, sequenceAnnotation).inDocumentOrder();
    List<InstanceRepresentation> insts4seq = new ArrayList<>(instanceAnnotations.size());
    // for each instance annotation, get the instance representation and add it to the list
    for (Annotation instanceAnnotation : instanceAnnotations) {
      InstanceRepresentation inst
              = unlabeledAnnotation2Instance(instanceAnnotation, inputAS, null);
      insts4seq.add(inst);
    }

    return insts4seq;
  }

  /**
   * Convert a labeled instance annotation to an instance representation.
   * 
   * @param instanceAnnotation instance annotation
   * @param inputAS input annotation set
   * @param classAS class annotation set
   * @param targetFeatureName name of target feature
   * @param targetType type of target
   * @param instanceWeightFeature instance weight feature, currently unused
   * @param seqEncoder sequence encoder instance
   * @return InstanceRepresentation
   */
  public InstanceRepresentation labeledAnnotation2Instance(Annotation instanceAnnotation,
          AnnotationSet inputAS, AnnotationSet classAS,
          String targetFeatureName, TargetType targetType,
          String instanceWeightFeature, SeqEncoder seqEncoder) {
    // create a new dense instance representation
    InstanceRepresentation inst = unlabeledAnnotation2Instance(
            instanceAnnotation, inputAS, instanceWeightFeature);
    // add the stats for all the features
    // TODO: maybe this is too slow and eventually we need to just limit this to 
    // adding stats for any list-like features (so the consumer of the data can
    // decide beforehand how to represent those lists).
    addToStatsForFeatures(inst);

    // now add the apropriate target information to the instance, depending on if we
    // do sequence tagging, classification, or regression
    if (classAS != null) {
      // extract the target as required for sequence tagging
      inst = FeatureExtractionDense.extractClassForSeqTagging(inst, classAS, instanceAnnotation, seqEncoder);
      // ok, for this we should have a nominal target value
      stats.addValue(StatsForFeatures.KEY_FOR_TARGET, inst.getTargetValue());
    } else {
      if (targetType == TargetType.NOMINAL) {
        inst = FeatureExtractionDense.extractClassTarget(inst, targetFeatureName, instanceAnnotation, inputAS);
        stats.addValue(StatsForFeatures.KEY_FOR_TARGET, inst.getTargetValue());
      } else if (targetType == TargetType.NUMERIC) {
        inst = FeatureExtractionDense.extractNumericTarget(inst, targetFeatureName, instanceAnnotation, inputAS);
        stats.addValue(StatsForFeatures.KEY_FOR_TARGET, inst.getTargetValue());
      }
    }
    return inst;
  }

  /**
   * Convert an unlabeled instance annotation to an InstanceRepresentation.
   * 
   * @param instanceAnnotation instance annotation 
   * @param inputAS input annotation set
   * @param instanceWeightFeature instance weight feature, currently unused
   * @return InstanceRepresentation
   */
  public InstanceRepresentation unlabeledAnnotation2Instance(Annotation instanceAnnotation,
          AnnotationSet inputAS,
          String instanceWeightFeature) {
    // create a new dense instance representation
    InstanceRepresentation inst = new InstanceRepresentationDenseVolatile();
    // first extract the independent features and add them to the instance representation
    for (FeatureSpecAttribute attr : featureInfo.getAttributes()) {
      inst = FeatureExtractionDense.extractFeature(inst, attr, inputAS, instanceAnnotation);
    }
    if (instanceWeightFeature != null && !instanceWeightFeature.isEmpty()) {
      // If the instanceWeightFeature is not specified we do not set any weight, but if it is 
      // specified then we either try to convert the value to double or use 1.0.
      double score = LFUtils.anyToDoubleOrElse(instanceAnnotation.getFeatures().get(instanceWeightFeature), 1.0);
      inst.setInstanceWeight(score);
    }
    return inst;
  }

  /**
   * Update the feature stastistics from the instance.
   * 
   * @param inst instance from which to update
   */
  public void addToStatsForFeatures(InstanceRepresentation inst) {
    // System.err.println("DEBUG: addToStatsForFeatures for "+inst);
    for (String fname : fnames) {
      stats.addValue(fname, inst.getFeature(fname));
    }
  }

  /**
   * Convert the instance to json.
   *
   * Note: this is influenced by the feature info set in the corpus
   * representation!
   *
   * @param inst instance to convert
   * @param noTarget - if true, does not include  the target(s) and does not use outermost list for 
   * indep / target pair
   * @return JSON string
   */
  public String internal2Json(InstanceRepresentation inst, boolean noTarget) {
    // can this be shared between multiple threads?
    ObjectMapper mapper = new ObjectMapper();
    List<Object> values = internal2array(inst);
    // the final instance is a list containing the values and the target (for now)
    // unless noTarget has been given
    if(noTarget) {
      try {
        return mapper.writeValueAsString(values);
      } catch (JsonProcessingException ex) {
        throw new GateRuntimeException("Could not convert instance to json", ex);
      }      
    } else {
      ArrayList<Object> theInstance = new ArrayList<>(2);
      theInstance.add(values);
      theInstance.add(inst.getTargetValue());
      // now convert this to a JSON String
      try {
        return mapper.writeValueAsString(theInstance);
      } catch (JsonProcessingException ex) {
        throw new GateRuntimeException("Could not convert instance to json", ex);
      }
    }
  }

  /**
   * Convert a list of instances to json.
   * 
   * @param instseq list of instance representations
   * @param noTarget whether or not to include the target
   * @return JSON string
   */
  public String internal2Json(List<InstanceRepresentation> instseq, boolean noTarget) {
    // can this be shared between multiple threads?
    ObjectMapper mapper = new ObjectMapper();
    // the format is a two-element list, where the first element is 
    // the list of all lists of independent features and the second element
    // is a list of all targets    
    List<Object> indepList = new ArrayList<>();
    List<Object> targetList = new ArrayList<>();
    for (InstanceRepresentation inst : instseq) {
      List<Object> values = internal2array(inst);
      indepList.add(values);
      if(!noTarget) {
        targetList.add(inst.getTargetValue());
      }
    }
    if(noTarget) {
      try {
        return mapper.writeValueAsString(indepList);
      } catch (JsonProcessingException ex) {
        throw new GateRuntimeException("Could not convert instance sequence to json", ex);
      }      
    } else {
      List<Object> finalList = new ArrayList<>();
      finalList.add(indepList);
      finalList.add(targetList);
      // now convert this to a JSON String
      try {
        return mapper.writeValueAsString(finalList);
      } catch (JsonProcessingException ex) {
        throw new GateRuntimeException("Could not convert instance sequence to json", ex);
      }
    }
  }

  private List<Object> internal2array(InstanceRepresentation inst) {
    ArrayList<Object> values = new ArrayList<>();
    for (String fname : fnames) {
      values.add(inst.getFeature(fname));
    }
    return values;
  }

  /**
   * Switch on adding for the corpus representation.
   * 
   * 
   */
  @Override
  public void startAdding() {
    File outFile = new File(outDir, DATA_FILE_NAME);
    this.outDataFile = outFile;
    try {
      outStream = new FileOutputStream(outFile);
    } catch (FileNotFoundException ex) {
      throw new GateRuntimeException("Cannot open output stream to " + outFile, ex);
    }
    // Save the initial metadata, this will get overridden with the final metadata
    // when finishAdding() is called. The idea is to allow a client to 
    // check for incomplete writing of the training set.
    saveMetadata();
  }

  /**
   * Finish adding data to the CR. 
   * 
   * This may close or finish any channel for
   * passing on the data to a file, database or other sink.
   *
   */
  @Override
  public void finishAdding() {
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

  /**
   * Write the complete json representation for the metadata to the writer.
   * 
   * @param writer where to write the JSON to
   */
  public void json4metadata(Writer writer) {
    System.err.println("DEBUG: writing the metadata file!!");
    try {
      ObjectMapper mapper = new ObjectMapper();
      // NOTE: the default mapper constructor uses a factory which 
      // auto-closes the stream or writer, this could be used to prevent it
      // but only with a newer version of jackson than in the GATE lib.
      // For now disabled.
      // This is needed to add a final newline to the meta-data file written
      // so that it has one line and not zero, technically
      // mapper.disable(JsonGenerator.Feature.AUTO_CLOSE_TARGET);
      Map<String, Object> metadata = new HashMap<>();
      metadata.put("featureInfo", featureInfo);
      metadata.put("featureNames", fnames);
      metadata.put("linesWritten", linesWritten);
      // outDataFile may be null if somebody calls this before the startAdding()
      // method
      metadata.put("dataFile", (outDataFile==null ? "" : outDataFile.getAbsolutePath()));
      metadata.put("isSequence", isSequence);
      metadata.put("features", featureSpecAttributes2FeatureInfos(featureInfo.getAttributes()));
      if (isSequence != null && isSequence) {
        metadata.put("sequLengths.mean", seqLenStats.getMean());
        metadata.put("sequLengths.min", seqLenStats.getMin());
        metadata.put("sequLengths.max", seqLenStats.getMax());
        metadata.put("sequLengths.variance", seqLenStats.getVariance());
      }

      Map<String, Stats.StatsObject> featureStats = new HashMap<>();
      for (String fname : fnames) {
        Stats s = stats.getStatistics(fname);
        //System.err.println("!!!!Getting feature stats for "+fname+": "+s);
        if (s != null) { // when we store the initial metadata, none of these will exist yet
          featureStats.put(fname, s.getStatsObject());
        }
      }
      metadata.put("featureStats", featureStats);
      Stats targetStats = stats.getStatistics(StatsForFeatures.KEY_FOR_TARGET);
      // note: we also write the metadata before we see training intances, in this
      // case the targetStats object will be null!
      if (targetStats != null) {
        metadata.put("targetStats", targetStats.getStatsObject());
      }
      SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd,HH:mm:ss");
      metadata.put("savedOn", sdf.format(new Date()));
      mapper.writeValue(writer, metadata);
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not serialize metadata", ex);
    }

  }

  /**
   * Save the current metadata.
   * 
   */
  public void saveMetadata() {
    // System.err.println("DEBUG: SAVING METADATA");
    outMetaFile = new File(outDir, META_FILE_NAME);
    try (
            FileOutputStream fos = new FileOutputStream(outMetaFile);
            OutputStreamWriter osw = new OutputStreamWriter(fos, "UTF-8");
            ) {
      synchronized (LOCKING_OBJECT) {
        json4metadata(osw);
        // TODO: can only be done once we can use a more recent Jackson library, 
        // see the json4metadata method!
        // osw.append("\n");
      }
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not write metadata to file", ex);
    }
  }

}
