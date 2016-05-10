/*
 * CorpusWriterMalletSeq.java
 *  
 * Copyright (c) 1995-2015, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * Copyright 2015 South London and Maudsley NHS Trust and King's College London
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 *
 * Genevieve Gorrell, 9 Jan 2015
 */
package gate.plugin.learningframework.data;

import gate.Annotation;
import gate.AnnotationSet;
import java.util.ArrayList;
import java.util.List;
import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.features.FeatureExtraction;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import org.apache.log4j.Logger;
import static gate.plugin.learningframework.data.CorpusRepresentationMalletTarget.extractIndependentFeaturesHelper;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

public class CorpusRepresentationMalletSeq extends CorpusRepresentationMallet {

  static final Logger logger = Logger.getLogger("CorpusRepresentationMalletSeq");

  public CorpusRepresentationMalletSeq(FeatureInfo fi, ScalingMethod sm) {
    featureInfo = fi;
    scalingMethod = sm;

    Pipe innerPipe = new Noop(new Alphabet(), new LabelAlphabet());
    List<Pipe> pipes = new ArrayList<Pipe>();
    pipes.add(innerPipe);
    pipe = new LFPipe(pipes);
    pipe.setFeatureInfo(fi);
    instances = new InstanceList(pipe);
  }

  /**
   * Non-public constructor for use when creating from a serialized pipe.
   *
   * @param fi
   */
  CorpusRepresentationMalletSeq(LFPipe pipe) {
    this.pipe = pipe;
    this.featureInfo = pipe.getFeatureInfo();
    this.scalingMethod = null;
    this.instances = new InstanceList(pipe);
  }

  /**
   * Create a new instance based on the pipe stored in directory.
   *
   * @param directory
   * @return
   */
  public static CorpusRepresentationMalletSeq load(File directory) {
    // load the pipe
    File inFile = new File(directory, "pipe.pipe");
    ObjectInputStream ois = null;
    LFPipe lfpipe = null;
    try {
      ois = new ObjectInputStream(new FileInputStream(inFile));
      lfpipe = (LFPipe) ois.readObject();
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not read pipe from " + inFile, ex);
    } finally {
      try {
        if (ois != null) {
          ois.close();
        }
      } catch (IOException ex) {
        logger.error("Error closing stream after loading pipe " + inFile, ex);
      }
    }
    CorpusRepresentationMalletSeq crms = new CorpusRepresentationMalletSeq(lfpipe);
    return crms;
  }

  public void clear() {
    LFPipe pipe = (LFPipe) instances.getPipe();
    instances = new InstanceList(pipe);
  }

  /**
   * Add instances. The exact way of how the target is created to the instances depends on which
   * parameters are given and which are null. The parameter sequenceAS must always be non-null for
   * this corpus representation since this corpus representation is always used with sequence
   * tagging algorithms If the parameter classAS is non-null then instances for a sequence tagging
   * task are created, in that case targetFeatureName must be null. If targetFeatureName is non-null
   * then instances for a regression or classification problem are created (depending on targetType)
   * and classAS must be null. if the parameter nameFeatureName is non-null, then a Mallet instance
   * name is added from the source document and annotation.
   *
   * @param instancesAS
   * @param inputAS
   * @param nameFeatureName
   */
  public void addOld(AnnotationSet instancesAS, AnnotationSet sequenceAS, AnnotationSet inputAS, AnnotationSet classAS, String targetFeatureName, TargetType targetType, String nameFeatureName) {
    if (sequenceAS == null) {
      throw new GateRuntimeException("LF invalid call to CorpusRepresentationMallet.add: sequenceAS must not be null "
              + " for document " + inputAS.getDocument().getName());
    }
    // This was the old approach:
    // First iterate through all the sequence annotations, then for each sequence annotation, get
    // the instance annotations in order. For each of these instance annotations, create a Mallet
    // Instance and record them all in an array, then create a featuresequence and a labelsequence
    // from the array of instances and create a final Mallet instance with the featuresequence
    // as data and the labelsequence as target
    for (Annotation sequenceAnnotation : sequenceAS.inDocumentOrder()) {
      List<Instance> instanceList = new ArrayList<Instance>(sequenceAS.size());
      List<Annotation> instanceAnnotations = gate.Utils.getContainedAnnotations(instancesAS, sequenceAnnotation).inDocumentOrder();
      for (Annotation instanceAnnotation : instanceAnnotations) {
        Instance inst = extractIndependentFeaturesHelper(instanceAnnotation, inputAS, featureInfo, pipe);
        if (classAS != null) {
          // extract the target as required for sequence tagging
          FeatureExtraction.extractClassForSeqTagging(inst, pipe.getTargetAlphabet(), classAS, instanceAnnotation);
        } else if (targetType == TargetType.NOMINAL) {
          FeatureExtraction.extractClassTarget(inst, pipe.getTargetAlphabet(), targetFeatureName, instanceAnnotation, inputAS);
        } else if (targetType == TargetType.NUMERIC) {
          FeatureExtraction.extractNumericTarget(inst, targetFeatureName, instanceAnnotation, inputAS);
        }
        if (!FeatureExtraction.ignoreInstanceWithMV(inst)) {
          instanceList.add(inst);
        }
      }
      // create a feature sequence from all the feature vectors in each of the instances in instanceList
      // create a label index sequence from all the labels of the instances in instance list
      // However, we do all this only if there is at least one instance in the first place
      if (instanceList.size() > 0) {
        FeatureVector[] vectors = new FeatureVector[instanceList.size()];
        int[] labelidxs = new int[instanceList.size()];
        for (int i = 0; i < vectors.length; i++) {
          vectors[i] = (FeatureVector) instanceList.get(i).getData();
        }
        FeatureVectorSequence fvseq = new FeatureVectorSequence(vectors);
        for (int i = 0; i < labelidxs.length; i++) {
          labelidxs[i] = ((Label) instanceList.get(i).getTarget()).getIndex();
        }
        FeatureSequence fseq = new FeatureSequence(pipe.getTargetAlphabet(), labelidxs);
        // create the final instance, if a name feature is given also add the name
        Instance finalInst = new Instance(fvseq, fseq, null, null);
        if (nameFeatureName != null) {
          FeatureExtraction.extractName(finalInst, sequenceAnnotation, inputAS.getDocument());
        }
        // add the instance to the instances 

        instances.add(finalInst);

      }
    }

  }

  /**
   * Add instances. The exact way of how the target is created to the instances depends on which
   * parameters are given and which are null. The parameter sequenceAS must always be non-null for
   * this corpus representation since this corpus representation is always used with sequence
   * tagging algorithms If the parameter classAS is non-null then instances for a sequence tagging
   * task are created, in that case targetFeatureName must be null. If targetFeatureName is non-null
   * then instances for a regression or classification problem are created (depending on targetType)
   * and classAS must be null. if the parameter nameFeatureName is non-null, then a Mallet instance
   * name is added from the source document and annotation.
   *
   * @param instancesAS
   * @param inputAS
   * @param nameFeatureName
   */
  @Override
  public void add(AnnotationSet instancesAS, AnnotationSet sequenceAS, AnnotationSet inputAS, AnnotationSet classAS, String targetFeatureName, TargetType targetType, String nameFeatureName) {
    if (sequenceAS == null) {
      throw new GateRuntimeException("LF invalid call to CorpusRepresentationMallet.add: sequenceAS must not be null "
              + " for document " + inputAS.getDocument().getName());
    }
    for (Annotation sequenceAnnotation : sequenceAS.inDocumentOrder()) {
      Instance inst = getInstanceForSequence(instancesAS, sequenceAnnotation, inputAS, classAS, targetFeatureName, targetType, nameFeatureName);
        instances.add(inst);
    }
  }

  @Override
  public void addScaling(ScalingMethod scaleFeatures) {  
    if(scaleFeatures != ScalingMethod.NONE)
      throw new GateRuntimeException("Scaling not allowed/not yet implemented for sequence tagging representation");
  }
  
  /**
   * Get a single Instance for a sequence annotation. If the
   *
   * @param instancesAS
   * @param sequenceAnnotation
   * @param inputAS
   * @param classAS
   * @param targetFeatureName
   * @param targetType
   * @param nameFeatureName
   * @return
   */
  public Instance getInstanceForSequence(
          AnnotationSet instancesAS,
          Annotation sequenceAnnotation,
          AnnotationSet inputAS,
          AnnotationSet classAS,
          String targetFeatureName,
          TargetType targetType,
          String nameFeatureName) {

    List<Annotation> instanceAnnotations = gate.Utils.getContainedAnnotations(instancesAS, sequenceAnnotation).inDocumentOrder();
    List<Instance> instanceList = new ArrayList<Instance>(instanceAnnotations.size());
    for (Annotation instanceAnnotation : instanceAnnotations) {
      Instance inst = extractIndependentFeaturesHelper(instanceAnnotation, inputAS, featureInfo, pipe);
      if (targetType != TargetType.NONE) {
        if (classAS != null) {
          // extract the target as required for sequence tagging
          FeatureExtraction.extractClassForSeqTagging(inst, pipe.getTargetAlphabet(), classAS, instanceAnnotation);
        } else if (targetType == TargetType.NOMINAL) {
          FeatureExtraction.extractClassTarget(inst, pipe.getTargetAlphabet(), targetFeatureName, instanceAnnotation, inputAS);
        } else if (targetType == TargetType.NUMERIC) {
          FeatureExtraction.extractNumericTarget(inst, targetFeatureName, instanceAnnotation, inputAS);
        }
      }
      if (!FeatureExtraction.ignoreInstanceWithMV(inst)) {
        instanceList.add(inst);
      }
    }
    FeatureVector[] vectors = new FeatureVector[instanceList.size()];
    for (int i = 0; i < vectors.length; i++) {
      vectors[i] = (FeatureVector) instanceList.get(i).getData();
    }
    FeatureVectorSequence fvseq = new FeatureVectorSequence(vectors);
    FeatureSequence fseq = null;
    if (targetType != TargetType.NONE) {
      int[] labelidxs = new int[instanceList.size()];
      for (int i = 0; i < labelidxs.length; i++) {
        labelidxs[i] = ((Label) instanceList.get(i).getTarget()).getIndex();
      }
      fseq = new FeatureSequence(pipe.getTargetAlphabet(), labelidxs);
    }
    // create the final instance, if a name feature is given also add the name
    Instance finalInst = new Instance(fvseq, fseq, null, null);
    if (nameFeatureName != null) {
      FeatureExtraction.extractName(finalInst, sequenceAnnotation, inputAS.getDocument());
    }
    return finalInst;

  }

  @Override
  public void export(File directory, String parms) {
    instances.save(new File(directory, "data.malletseq.ser"));
  }
}
