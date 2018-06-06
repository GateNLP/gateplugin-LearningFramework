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
import org.apache.log4j.Logger;

/**
 * Common base class for non Mallet volatile representations.
 * 
 * This is for representations which are "volatile" i.e. whenever something is 
 * added it is not kept in memory. Data could get immediately written to a file
 * or database or immediately passed on to an online training algorithm
 * 
 * @author Johann Petrak
 */
public abstract class CorpusRepresentationVolatileBase extends CorpusRepresentation {

  private Logger LOGGER = org.apache.log4j.Logger.getLogger(CorpusRepresentationVolatileBase.class);

  
  
  /**
   * Prevent the addition of new features or feature values when instances are added.
   */
  public void stopGrowth() {
    // TODO: this may be useful for sparse volatile representations, not used yet
  }
  
  /**
   * Enable the addition of new features or feature values when instances are added.
   * After a CorpusRepresentationMallet instance is created, growth is enabled by default.
   */
  public void startGrowth() {
    // TODO: this may be useful for sparse volatile representations, not used yet
  }
      
  @Override
  public abstract void add(AnnotationSet instancesAS, AnnotationSet sequenceAS, AnnotationSet inputAS, AnnotationSet classAS, String targetFeatureName, TargetType targetType, String instanceWeightFeature, String nameFeatureName, SeqEncoder seqEncoder);
  
  /**
   * Finish adding data to the CR. This may close or finish any channel for
   * passing on the data to a file, database or other sink. 
   * 
   */
  @Override
  public abstract void finishAdding();
  
  
}
