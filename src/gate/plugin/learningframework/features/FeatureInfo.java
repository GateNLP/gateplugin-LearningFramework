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

package gate.plugin.learningframework.features;

import gate.plugin.learningframework.ScalingMethod;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * This contains the information from the parsed FeatureSpecification plus additional information
 * accumulated during the extraction of a corpus. The additional information is stuff like mappings
 * between the attribute names from the feature specification and the actual names used in the
 * Mallet feature vector, or the mapping between nominal values coded as numeric and the number used
 * to represent the value. Like alphabets, the information in this object can be locked using the
 * stopGrowth() method.
 *
 * @author Johann Petrak
 */
public class FeatureInfo implements Serializable {

  private static final long serialVersionUID = 1;
  protected boolean growthStopped = false;

  /**
   * Create an instance with an empty list of featureSpecs.
   */
  public FeatureInfo() {
    featureSpecs = new ArrayList<FeatureSpecAttribute>();
  }

  /**
   * Create an FeatureInfo instance that is a deep copy of another one.
   */
  public FeatureInfo(FeatureInfo other) {
    this.growthStopped = other.growthStopped;
    featureSpecs = new ArrayList<FeatureSpecAttribute>();
    for(FeatureSpecAttribute attr : other.getAttributes()) {
      featureSpecs.add(attr.clone());
    }
  }
  
  public void stopGrowth() {
    // make sure that all alphabets we have stored with some of the featureSpecs are
    // locked too!
    for(FeatureSpecAttribute attr : featureSpecs) {
      attr.stopGrowth();
    }
    growthStopped = true;
  }

  public void startGrowth() {
    growthStopped = false;
  }

  public boolean growthStopped() {
    return growthStopped;
  }

  protected List<FeatureSpecAttribute> featureSpecs;


  protected ScalingMethod globalScalingMethod = ScalingMethod.NONE;
  
  public void setGlobalScalingMethod(ScalingMethod sm) {
    globalScalingMethod = sm;
  }
  
  public ScalingMethod getGlobalScalingMethod() {
    return globalScalingMethod;
  }
  
  public List<FeatureSpecAttribute> getAttributes() { return featureSpecs; }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("FeatureInfo{growthStopped=");
    sb.append(growthStopped);
    sb.append(",attrs=");
    sb.append(featureSpecs);
    sb.append("}");
    return sb.toString();
  }






}
