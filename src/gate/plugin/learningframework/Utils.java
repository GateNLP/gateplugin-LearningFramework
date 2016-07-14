/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework;

/**
 *
 * @author Johann Petrak
 */
public class Utils {

  // NOTE: we use an AugmentableFeatureVector to represent the growing featureName vector inputAS we
  // build it.
  // The Mallet documentation is close to non-existing ATM, so here is what the methods we use do:
  // afv.add("x",val) adds val to whatever the current value for "x" is oder adds the featureName, if
  //   the Alphabet can grow. If the Alphabet cannot grow, the method does nothing.
  //   UPDATE: this does not work! if one tries to do that, the indices get messed up and
  //   the fv will throw an ArrayIndexOutOfBoundsException!!!!
  //   So we have always to explicitly check if the featureName is in the alphabet!!!
  //   UPDATE: Mallet uses assert for checking things like this, so if assertsions are not enable,
  //   no exception is thrown until it is too late!
  // afv.value("x") retrieves the value if "x" is in the vector, otherwise an exception is thrown,
  //   even if "x" is in the alphabet.
  // afv.contains("x") is true if the featureName vector contains a value for "x" (which implies it must
  //   be in the alphabet)
  // afv.getAlphabet().contains("x") is true if "x" is in the alphabet.
  /**
   * Convert the object to a double or, if it is null or not convertible, use the orElse value.
   *
   * @param any
   * @param orElse
   * @return
   */
  public static double anyToDoubleOrElse(Object any, double orElse) {
    if (any == null) {
      return orElse;
    }
    if (any instanceof Number) {
      return ((Number) any).doubleValue();
    } else if (any instanceof String) {
      Double tmp = null;
      try {
        tmp = Double.parseDouble((String) any);
      } catch (Exception ex) {
        // do not do anything, we just are happy to find tmp=null in this case
      }
      if (tmp == null) {
        return orElse;
      } else {
        return tmp;
      }
    } else {
      return orElse;
    }
  }

}
