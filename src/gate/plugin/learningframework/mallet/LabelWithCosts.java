/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.mallet;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author Johann Petrak
 */
public class LabelWithCosts implements Serializable {

  private static final long serialVersionUID = 2552102403617791653L;
  
  Integer idx = null;
  String label = null;
  double[] costs = null;
  public LabelWithCosts(List<Double> costs) {
    this.costs = new double[costs.size()];
    for(int i=0;i<costs.size();i++) this.costs[i] = costs.get(i);
    storeLabel();
  }
  public LabelWithCosts(double[] costs) {
    this.costs = costs;
    storeLabel();
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
    final LabelWithCosts other = (LabelWithCosts) obj;
    if (!Objects.equals(this.label, other.label)) {
      return false;
    }
    return true;
  }
  
  
  
}
