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

import gate.util.GateRuntimeException;
import java.io.Serializable;

/**
 *
 * @author Johann Petrak
 */
public abstract class FeatureSpecAttribute implements Serializable, Cloneable {

  private static final long serialVersionUID = 651636894843439700L;

  public String annType;
  public String feature;
  public String name;
  public int    featureId;  // a feature index, starting with 0
  public String missingValueValue = null;
  public Datatype datatype;
  public String listsep = null;
  
  public abstract void stopGrowth();
  public abstract void startGrowth();
  
  @Override
  public FeatureSpecAttribute clone()  {
    try {
      return (FeatureSpecAttribute) super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new RuntimeException("Could not clone Attribute",ex);
    }
  }
  
  /**
   * Returns the missing value as the proper data type for this attribute.
   * For example returns a String for nominal or a Float for numeric. 
   * @return 
   */
  public Object missingValue() {
    Object ret = null;
    switch (datatype) {
      case nominal:
        ret = missingValueValue;
        break;
      case bool:
        ret = Boolean.parseBoolean(missingValueValue);
        break;
      case numeric:
        ret = Double.parseDouble(missingValueValue);
        break;
      default:
        throw new GateRuntimeException("Unknown datatype: "+datatype);
    }      
    return ret;
  }
  

}