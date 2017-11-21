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
public class FeatureSpecAttributeList extends FeatureSpecSimpleAttribute implements Serializable, Cloneable {

  private static final long serialVersionUID = -4627730393276173588L;

  public FeatureSpecAttributeList(
          String aname, 
          String type, 
          String feature, 
          Datatype datatype, 
          CodeAs codeas, 
          MissingValueTreatment missingValueTreatment, 
          String missingValueValue, 
          String scalingMethod, 
          String transformMethod, 
          int from, int to, 
          String withinType,
          String listsep,
          String featureName4Value) {
    super(aname, type, feature, datatype, codeas, missingValueTreatment, 
            missingValueValue, scalingMethod, transformMethod, withinType, listsep, featureName4Value);
    this.from = from;
    this.to = to;
  }
  
  /**
   * Create an AttributeList instance from a SimpleAttribute plus the from and to values
   */
  public FeatureSpecAttributeList(FeatureSpecSimpleAttribute att, String withinType, int from, int to) {
    /*
          String aname, 
          String type, 
          String feature, 
          Datatype datatype, 
          CodeAs codeas, 
          MissingValueTreatment missingValueTreatment, 
          String missingValueValue, 
          String scalingMethod, 
          String transformMethod,
          String withinType,
          String listsep,
          String featureName4Value
    */
    super(att.name, att.annType, att.feature, 
            att.datatype, att.codeas, 
            att.missingValueTreatment, 
            att.missingValueValue, "", "", withinType, att.listsep, att.featureName4Value);
    this.from = from;
    this.to = to;
  }
  
  public int from;
  public int to;
  
  // NOTE: this inherits the alphabet from SimpleAttribute: even though this object represents a 
  // whole set of features, the alphabet gets shared by all of them!

  
  @Override
  public String toString() {
    return "AttributeList(name="+name+
            ",type="+annType+
            ",feature="+feature+
            ",datatype="+datatype+
            ",missingvaluetreatment="+missingValueTreatment+
            ",codeas="+codeas+
            ",within="+withinType+
            ",from="+from+
            ",to="+to;
  }
  
  @Override
  public FeatureSpecAttributeList clone() {
    return (FeatureSpecAttributeList) super.clone();
  }
  
  
}
