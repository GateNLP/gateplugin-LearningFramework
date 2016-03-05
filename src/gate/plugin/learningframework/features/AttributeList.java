/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.features;

import java.io.Serializable;

/**
 *
 * @author Johann Petrak
 */
public class AttributeList extends SimpleAttribute implements Serializable, Cloneable {

  private static final long serialVersionUID = -4627730393276173588L;

  public AttributeList(String aname, String type, String feature, Datatype datatype, CodeAs codeas, MissingValueTreatment missingValueTreatment, String missingValueValue, String scalingMethod, String transformMethod, int from, int to) {
    super(aname, type, feature, datatype, codeas, missingValueTreatment, missingValueValue, scalingMethod, transformMethod);
    this.from = from;
    this.to = to;
  }
  
  /**
   * Create an AttributeList instance from a SimpleAttribute plus the from and to values
   */
  public AttributeList(SimpleAttribute att, int from, int to) {
    super(att.name, att.annType, att.feature, att.datatype, att.codeas, att.missingValueTreatment, 
            "dummy", "dummy", "dummy");
    this.from = from;
    this.to = to;
  }
  
  int from;
  int to;
  
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
            ",from="+from+
            ",to="+to;
  }
  
  @Override
  public AttributeList clone() {
    return (AttributeList) super.clone();
  }
  
  
}
