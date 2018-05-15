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


package gate.plugin.learningframework.mallet;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Represent a cost vector in a way that Mallet can use as a classification label.
 * 
 * This can be created from a List of Double or double[] of per-instance costs, 
 * in which case the "label" used will be the String representation of the class index
 * with the minimum cost. 
 * If the instance is used directly as a target, then instead of the index, the 
 * nominal clas label can be stored directly as well.
 * 
 * NOTE: this is not related to or implements the interface from the Mallet
 * Label class. It can be stored as the value of a LabelAlphabet but 
 * the purpose of this class is essentially to be a replacement of String
 * which can also carry around a cost vector which is added information 
 * that does not influence the "target semanticness" of the value: two instances
 * of this class compare or have hash values based only on the target string,
 * not the cost vector.
 * 
 * @author Johann Petrak
 */
public class NominalTargetWithCosts implements Serializable {

  private static final long serialVersionUID = 2552102403617791653L;
  
  private Integer idx = null;
  private String label = null;
  private double[] costs = null;
  public NominalTargetWithCosts(List<Double> costs) {
    this.costs = new double[costs.size()];
    for(int i=0;i<costs.size();i++) this.costs[i] = costs.get(i);
    storeLabel();
  }
  public NominalTargetWithCosts(String l, List<Double> costs) {
    this(costs);
    label = l;
  }
  public NominalTargetWithCosts(double[] costs) {
    this.costs = costs;
    storeLabel();
  }
  public NominalTargetWithCosts(String l, double[] costs) {
    this(costs);
    label = l;
  }
  private void storeLabel() {
      int firstMin = -1;
      double minCost = Double.POSITIVE_INFINITY;
      for(int i=0; i<costs.length; i++) {
        if(costs[i]<minCost) {
          firstMin = i;
          minCost = costs[i];
        }
      } // for
      idx = firstMin;
      label = ""+firstMin;
  } // storeLabel
  
  public double[] getCosts() {
    return costs;
  }
  
  public Integer getClassIndex() {
    return idx;
  }
  public String getClassLabel() {
    return label;
  }
  
  // We simply convert this to the string representation of label which makes 
  // it easy to use with classifiers which do not know about costs e.g.
  // all the built in mallet classifiers.
  // This also makes it easy to convert the label back into an index even if
  // we do not have the full interface of LabelWithCosts imported.
  @Override
  public String toString() {
    return label;
    /*
    StringBuilder sb = new StringBuilder();
    sb.append("LabelWithCosts{");
    sb.append(idx);
    sb.append(",[");
    for(int i=0;i<costs.length;i++) {
      if(i>0) sb.append(",");
      sb.append(costs[i]);
    }
    sb.append("]");
    return sb.toString();
    */
  }

  @Override
  public int hashCode() {
    int hash = 3;
    hash = 79 * hash + Objects.hashCode(this.label);
    return hash;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    final NominalTargetWithCosts other = (NominalTargetWithCosts) obj;
    if (!Objects.equals(this.label, other.label)) {
      return false;
    }
    return true;
  }
  
  
  
}
