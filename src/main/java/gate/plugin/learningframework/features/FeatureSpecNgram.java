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

import java.io.Serializable;

/**
 *
 * @author Johann Petrak
 */
public class FeatureSpecNgram extends FeatureSpecAttribute implements Serializable, Cloneable {

  public FeatureSpecNgram(String aname, int number, String type, String feature, String featureName4Value) {
    this.name = aname;
    this.number = number;
    this.annType = type;
    this.feature = feature;
    this.featureName4Value = featureName4Value;
    this.datatype = Datatype.nominal;
    this.missingValueValue = "";
    featureCode = "N";
  }
  public int number = -1;
  public String featureName4Value = "";

  @Override
  public void stopGrowth() {
    /// we do not have any alphabets in an Ngram attribute, do nothing
  }

  @Override
  public void startGrowth() {
    /// we do not have any alphabets, do nothing
  }

  @Override
  public String toString() {
    return "NgramAttribute(name="+name+
            ",type="+annType+
            ",feature="+feature+
            ",featureName4Value="+featureName4Value+
            ",number="+number;
  }
  
  @Override
  public FeatureSpecNgram clone() {
      return (FeatureSpecNgram) super.clone();
  }
  
}
