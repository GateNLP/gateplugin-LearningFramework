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

package gate.plugin.learningframework;

/**
 *
 * @author Johann Petrak
 */
public class LFUtils {

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
