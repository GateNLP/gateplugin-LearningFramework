/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.features;

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
   * Create an instance with an empty list of attributes.
   */
  public FeatureInfo() {
    attributes = new ArrayList<FeatureSpecAttribute>();
  }

  /**
   * Create an FeatureInfo instance that is a deep copy of another one.
   */
  public FeatureInfo(FeatureInfo other) {
    this.growthStopped = other.growthStopped;
    attributes = new ArrayList<FeatureSpecAttribute>();
    for(FeatureSpecAttribute attr : other.getAttributes()) {
      attributes.add(attr.clone());
    }
  }
  
  public void stopGrowth() {
    // make sure that all alphabets we have stored with some of the attributes are
    // locked too!
    for(FeatureSpecAttribute attr : attributes) {
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

  protected List<FeatureSpecAttribute> attributes;


  public List<FeatureSpecAttribute> getAttributes() { return attributes; }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("FeatureInfo{growthStopped=");
    sb.append(growthStopped);
    sb.append(",attrs=");
    sb.append(attributes);
    sb.append("}");
    return sb.toString();
  }






}
