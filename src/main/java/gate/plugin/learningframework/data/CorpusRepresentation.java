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

import gate.AnnotationSet;
import gate.plugin.learningframework.features.SeqEncoder;
import gate.plugin.learningframework.features.TargetType;
import java.util.List;

/**
 * The base class of all classes that handle the representation of instances.
 * The LearningFramework uses the MalletSeq and Mallet representations whenever possible.
 * The other subclasses so far are only used to convert from Mallet representation for 
 * training, classification or export. 
 * @author Johann Petrak
 */
public abstract class CorpusRepresentation {
  //protected FeatureInfo featureInfo;
  //protected ScalingMethod scalingMethod;
  //protected LFPipe pipe;
  
  /**
   * Returns whatever object the concrete representation uses to represent the instances.
   * In addition, each specific CorpusRepresentation subclass has a representation specific
   * method that returns the correct type of data, e.g. getRepresentationLibSVM 
   * 
   * @return the instance representation object
   */
  public abstract Object getRepresentation();
  
  //public abstract InstanceList getRepresentationMallet();
  
  // NOTE: if the target type is NONE and the corpus representation is one to be used for clustering/LDA,
  // then the instanceAS is either an annotation covering the "document" or null, in which case the whole 
  // document is used. The inputAS is the set of token annotations for the whole document.
  public abstract void add(AnnotationSet instancesAS, AnnotationSet sequenceAS, AnnotationSet inputAS, AnnotationSet classAS, String targetFeatureName, TargetType targetType, String instanceWeightFeature, String nameFeatureName, SeqEncoder seqEncoder);
  
  public abstract void finishAdding();

  public abstract void startAdding();
  
  /**
   * Returns the number of training instances added to this CR so far.
   * 
   * 
   * @return number of instances added so far
   */
  public abstract int nrInstances();
  
  /**
   * Number of actual dimensions represented.
   * 
   * @return 
   */
  public abstract int nrDimensions();
  
  protected TargetType targetType;
  /**
   * Get the target type set for this corpus.
   * @return the target type
   */
  public TargetType getTargetType() {
    return targetType;
  }
  /**
   * Set the target type for the corpus representation.
   * Normally, this is automatically set when the corpus representation 
   * subclass is created and needs never to be changed.
   * @param val the target type 
   */
  public void setTargetType(TargetType val) {
    targetType = val;
  }
  
  /**
   * If we have labels, returns a list of strings, otherwise an empty list
   */
  public abstract List<String> getLabelList();
  
  
}
