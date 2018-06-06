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

import java.util.HashMap;
import java.util.Map;

/**
 * Representation of a dense-representation, non-lasting learning instance.
 * 
 * This is for when we create a dense representation of a learning instance
 * which will then immediately get converted or written, so a memory efficient
 * representation is not required. 
 * <p>
 * This representation just wraps a HashMap for storing all features and target
 * properties.
 * <p>
 * NOTE: this does not support removing features for now!
 * 
 * @author Johann Petrak 
 */
public class InstanceRepresentationDenseVolatile implements InstanceRepresentation {
  protected Map<String,Object> map = new HashMap<>();
  protected int numFeatures = 0;

  @Override
  public InstanceRepresentation setFeature(String name, Object value) {
    if(!map.containsKey(name)) {
      numFeatures += 1;
    }
    map.put(name, value);
    return this;
  }
  
  @Override
  public int numFeatures() {
    return numFeatures;
  }

  @Override
  public Object getFeature(String name) {
      return map.get(name);
  }

  @Override
  public InstanceRepresentation setTargetValue(Object value) {
    map.put(TARGET_VALUE, value);
    return this;
  }
  
  @Override 
  public Object getTargetValue() {
    return map.get(TARGET_VALUE);
  }

  @Override
  public InstanceRepresentation setTargetCosts(Object value) {
    map.put(TARGET_COSTS, value);
    return this;
  }

  @Override
  public InstanceRepresentation setInstanceWeight(double weight) {
    map.put(INSTANCE_WEIGHT,weight);
    return this;
  }

  @Override
  public double getInstanceWeight() {
    return (double)map.get(INSTANCE_WEIGHT);
  }

  @Override
  public boolean hasFeature(String name) {
    return map.containsKey(name);
  }

  @Override
  public boolean hasTarget() {
    return map.containsKey(TARGET_VALUE);
  }

  @Override
  public InstanceRepresentation setHasMissing(boolean flag) {
    map.put(HASMISSINGVALUE_FLAG, flag);
    return this;
  }

  @Override
  public boolean hasMissing() {
    return map.containsKey(HASMISSINGVALUE_FLAG);
  }
  
  @Override
  public String toString() {
    return "{InstanceRepresentationDenseVolatile: "+map.toString()+"}";
  }

}
