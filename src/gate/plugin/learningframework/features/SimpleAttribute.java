/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.features;

import cc.mallet.types.Alphabet;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johann Petrak
 */
public class SimpleAttribute extends Attribute implements Serializable, Cloneable {

  private static final long serialVersionUID = -2346560362547132478L;

  public SimpleAttribute(String aname, String type, String feature, Datatype datatype, CodeAs codeas, MissingValueTreatment missingValueTreatment, String missingValueValue, String scalingMethod, String transformMethod) {
    this.name = aname;
    this.annType = type;
    this.feature = feature;
    this.datatype = datatype;
    this.codeas = codeas;
    this.missingValueTreatment = missingValueTreatment;
    if (datatype == Datatype.nominal && codeas == CodeAs.number) {
      alphabet = new Alphabet();
    }
  }
  public CodeAs codeas = CodeAs.one_of_k;
  public Datatype datatype;
  public MissingValueTreatment missingValueTreatment = MissingValueTreatment.zero_value;
  public Alphabet alphabet;

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
  public SimpleAttribute clone() {
      return (SimpleAttribute) super.clone();
  }
  

}
