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
import java.util.ArrayList;
import java.util.List;
import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.features.FeatureExtractionMalletSparse;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import org.apache.log4j.Logger;
import static gate.plugin.learningframework.data.CorpusRepresentationMalletTarget.extractIndependentFeaturesHelper;
import gate.plugin.learningframework.features.SeqEncoder;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import static gate.plugin.learningframework.LFUtils.newURL;
import gate.plugin.learningframework.mallet.LFAlphabet;
import gate.plugin.learningframework.mallet.LFInstanceList;

public class CorpusRepresentationMalletSeq extends CorpusRepresentationMallet {

  static final Logger LOGGER = Logger.getLogger("CorpusRepresentationMalletSeq");

  public CorpusRepresentationMalletSeq(FeatureInfo fi, ScalingMethod sm) {
    featureInfo = fi;
    scalingMethod = sm;

    Pipe innerPipe = new Noop(new LFAlphabet(), new LabelAlphabet());
    List<Pipe> pipes = new ArrayList<>();
    pipes.add(innerPipe);
    pipe = new LFPipe(pipes);
    pipe.setFeatureInfo(fi);
    instances = new LFInstanceList(pipe);
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
    this.instances = new LFInstanceList(pipe);
  }

  /**
   * Create a new instance based on the pipe stored in directory.
   *
   * @param directory directory with the saved model
   * @return corpus representation instance
   */
  public static CorpusRepresentationMalletSeq load(URL directory) {
    // load the pipe from a Java object serialization representation
    URL inFile = newURL(directory, "pipe.pipe");
    LFPipe lfpipe = null;
    try (InputStream bom = inFile.openStream();
         ObjectInputStream ois = new ObjectInputStream(bom)) {
      lfpipe = (LFPipe) ois.readObject();      
    } catch (Exception ex) { 
      throw new GateRuntimeException("Could not read pipe from "+inFile,ex);
    }
    CorpusRepresentationMalletSeq crms = new CorpusRepresentationMalletSeq(lfpipe);
    return crms;
  }


  /**
   * Add instances. 
   * 
   * The exact way of how the target is created to the instances depends on which
   * parameters are given and which are null. The parameter sequenceAS must always be non-null for
   * this corpus representation since this corpus representation is always used with sequence
   * tagging algorithms If the parameter classAS is non-null then instances for a sequence tagging
   * task are created, in that case targetFeatureName must be null. If targetFeatureName is non-null
   * then instances for a regression or classification problem are created (depending on targetType)
   * and classAS must be null. if the parameter nameFeatureName is non-null, then a Mallet instance
   * name is added from the source document and annotation.
   *
   * @param instancesAS instance annotation set
   * @param sequenceAS sequence annotation set
   * @param inputAS input annotation set
   * @param classAS class annotation set
   * @param targetFeatureName feature name of target
   * @param targetType type of target
   * @param instanceWeightFeature ignored, this is only relevant for classification/regression
   * @param nameFeatureName feature for instance name, not used at the moment
   * @param seqEncoder sequence encoder instance
   */
  @Override
  public void add(AnnotationSet instancesAS, AnnotationSet sequenceAS, AnnotationSet inputAS, AnnotationSet classAS, String targetFeatureName, TargetType targetType, String instanceWeightFeature, String nameFeatureName, SeqEncoder seqEncoder) {
    if (sequenceAS == null) {
      throw new GateRuntimeException("LF invalid call to CorpusRepresentationMallet.add: sequenceAS must not be null "
              + " for document " + inputAS.getDocument().getName());
    }
    for (Annotation sequenceAnnotation : sequenceAS.inDocumentOrder()) {
      Instance inst = getInstanceForSequence(instancesAS, sequenceAnnotation, inputAS, classAS, targetFeatureName, targetType, nameFeatureName, seqEncoder);
        instances.add(inst);
    }
  }

  @Override
  public void finishAdding() {  
    if(scalingMethod != ScalingMethod.NONE) {
      throw new GateRuntimeException("Scaling not allowed/not yet implemented for sequence tagging representation");
    }
  }
  
  /**
   * Get a single Instance for a sequence annotation. If the
   *
   * @param instancesAS instance annotation set
   * @param sequenceAnnotation sequence annotation set
   * @param inputAS input annotation set
   * @param classAS class annotation set
   * @param targetFeatureName name of target feature
   * @param targetType type of target
   * @param nameFeatureName name feature
   * @param seqEncoder sequence encoder instance
   * @return the Instance instance
   */
  public Instance getInstanceForSequence(
          AnnotationSet instancesAS,
          Annotation sequenceAnnotation,
          AnnotationSet inputAS,
          AnnotationSet classAS,
          String targetFeatureName,
          TargetType targetType,
          String nameFeatureName,
          SeqEncoder seqEncoder) {

    List<Annotation> instanceAnnotations = gate.Utils.getContainedAnnotations(instancesAS, sequenceAnnotation).inDocumentOrder();
    List<Instance> instanceList = new ArrayList<>(instanceAnnotations.size());
    for (Annotation instanceAnnotation : instanceAnnotations) {
      Instance inst = extractIndependentFeaturesHelper(instanceAnnotation, inputAS, featureInfo, pipe);
      if (targetType != TargetType.NONE) {
        if (classAS != null) {
          // extract the target as required for sequence tagging
          FeatureExtractionMalletSparse.extractClassForSeqTagging(inst, pipe.getTargetAlphabet(), classAS, instanceAnnotation, seqEncoder);
        } else if (targetType == TargetType.NOMINAL) {
          FeatureExtractionMalletSparse.extractClassTarget(inst, pipe.getTargetAlphabet(), targetFeatureName, instanceAnnotation, inputAS);
        } else if (targetType == TargetType.NUMERIC) {
          FeatureExtractionMalletSparse.extractNumericTarget(inst, targetFeatureName, instanceAnnotation, inputAS);
        }
      }
      if (!FeatureExtractionMalletSparse.ignoreInstanceWithMV(inst)) {
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
      FeatureExtractionMalletSparse.extractName(finalInst, sequenceAnnotation, inputAS.getDocument());
    }
    return finalInst;

  }

  @Override
  public int nrInstances() {
    if(instances == null) {
      return 0;
    } else {
      return instances.size();
    }
  }
  
  
}
