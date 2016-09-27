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

import cc.mallet.types.Alphabet;
import java.io.Serializable;

/**
 *
 * @author Johann Petrak
 */
public class FeatureSpecSimpleAttribute extends FeatureSpecAttribute implements Serializable, Cloneable {

  private static final long serialVersionUID = -2346560362547132478L;

  public FeatureSpecSimpleAttribute(
          String aname, 
          String type, 
          String feature, 
          Datatype datatype, 
          CodeAs codeas, 
          MissingValueTreatment missingValueTreatment, 
          String missingValueValue, 
          String scalingMethod, 
          String transformMethod,
          String listsep,
          String featureName4Value) {
    this.name = aname;
    this.annType = type;
    this.feature = feature;
    this.datatype = datatype;
    this.codeas = codeas;
    this.missingValueTreatment = missingValueTreatment;
    if (datatype == Datatype.nominal && codeas == CodeAs.number) {
      alphabet = new Alphabet();
    }
    this.listsep = listsep;
    this.featureName4Value = featureName4Value;
  }
  public CodeAs codeas = CodeAs.one_of_k;
  public Datatype datatype;
  public MissingValueTreatment missingValueTreatment = MissingValueTreatment.zero_value;
  public Alphabet alphabet;
  public String listsep;
  public String featureName4Value;

  @Override
  public void stopGrowth() {
    if(alphabet!=null) { alphabet.stopGrowth(); }
  }

  @Override
  public void startGrowth() {
    if(alphabet!=null) { alphabet.startGrowth(); }
  }
  
  @Override
  public String toString() {
    return "SimpleAttribute(name="+name+
            ",type="+annType+
            ",feature="+feature+
            ",datatype="+datatype+
            ",missingvaluetreatment="+missingValueTreatment+
            ",codeas="+codeas;
  }
  
  @Override
  public FeatureSpecSimpleAttribute clone() {
      return (FeatureSpecSimpleAttribute) super.clone();
  }
  

}
