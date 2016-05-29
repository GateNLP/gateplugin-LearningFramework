
package gate.plugin.learningframework.features;

import java.io.Serializable;

/**
 *
 * @author Johann Petrak
 */
public class FeatureSpecAttributeList extends FeatureSpecSimpleAttribute implements Serializable, Cloneable {

  private static final long serialVersionUID = -4627730393276173588L;

  public FeatureSpecAttributeList(String aname, String type, String feature, Datatype datatype, CodeAs codeas, MissingValueTreatment missingValueTreatment, String missingValueValue, String scalingMethod, String transformMethod, int from, int to, String withinType) {
    super(aname, type, feature, datatype, codeas, missingValueTreatment, missingValueValue, scalingMethod, transformMethod);
    this.from = from;
    this.to = to;
    this.withinType = withinType;
  }
  
  /**
   * Create an AttributeList instance from a SimpleAttribute plus the from and to values
   */
  public FeatureSpecAttributeList(FeatureSpecSimpleAttribute att, String withinType, int from, int to) {
    super(att.name, att.annType, att.feature, att.datatype, att.codeas, att.missingValueTreatment, 
            "dummy", "dummy", "dummy");
    this.from = from;
    this.to = to;
    this.withinType = withinType;
  }
  
  int from;
  int to;
  String withinType = null;
  
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
