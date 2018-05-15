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
package gate.plugin.learningframework.stats;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple lightweight wrapper class for maintaining stats about many features.
 * 
 * This gathers statistics about many features, mapping feature names to 
 * feature statistics. The kind of statistic gathered depends on the type
 * of value passed in for each data point. 
 * <p>
 * Currently statistics are calculated like this, depending on the type
 * of value passed in:
 * <ul>
 * <li>Numeric: over the double representation of the value itself
 * <li>Boolean: over the 0/1 representation of false/true
 * <li>String: For now, no statistics are generated for this
 * <li>List/Array: over the size of the list or array
 * </ul>
 * 
 * @author Johann Petrak 
 */
public class StatsForFeatures {
  private Map<String,Stats> feature2stats = new HashMap<>();
  private final Object lockingObject = new Object();
  
  public static final String KEY_FOR_TARGET = "╳TARGET╳";
  
  /**
   * TODO 
   * @param featureName TODO
   * @param value TODO
   */
  public void addValue(String featureName, Object value) {
    synchronized(lockingObject) {
      Stats stats;
      if(feature2stats.containsKey(featureName)) {
        stats = feature2stats.get(featureName);
      } else {
        stats = new Stats(value);
        feature2stats.put(featureName, stats);
      }
      stats.addValue(value);
    } // synchronized
  } // addValue(...)
  
  /**
   * TODO
   * @param featureName TODO 
   * @return TODO
   */
  public Stats getStatistics(String featureName) {
    synchronized(lockingObject) {
      return feature2stats.get(featureName);
    }
  }
}
