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

import cc.mallet.types.InstanceList;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.mallet.LFPipe;

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
   * @return 
   */
  public abstract Object getRepresentation();
  
  //public abstract InstanceList getRepresentationMallet();
  
  
}
