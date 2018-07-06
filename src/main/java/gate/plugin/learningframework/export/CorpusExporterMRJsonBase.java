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
package gate.plugin.learningframework.export;

import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;
import gate.plugin.learningframework.data.Attribute;
import gate.plugin.learningframework.data.Attributes;
import gate.plugin.learningframework.features.CodeAs;
import gate.plugin.learningframework.features.Datatype;
import gate.plugin.learningframework.mallet.LFAlphabet;
import gate.plugin.learningframework.mallet.NominalTargetWithCosts;
import gate.util.GateRuntimeException;

/**
 * Base class for the JSON exporter classes.
 *
 * See the subclasses for a description of the format. This class is mainly
 * there to contain shared (static) methods.
 *
 * @author Johann Petrak
 */
public abstract class CorpusExporterMRJsonBase extends CorpusExporterMR {

  // This returns the string representation of the feature vector

  /**
   * Return String representation of the feature vector.
   * @param fv feature vector
   * @param nrFeatures number of features
   * @param attrs attributes
   * @param asString value should be represented as a quoted string
   * @return the string representation
   */
  public static String featureVector2String(FeatureVector fv, int nrFeatures, Attributes attrs, boolean asString) {
    StringBuilder sb = new StringBuilder();
    // TODO: can we use some JSON library instead?
    sb.append("[");
    boolean first = true;
    for (int j = 0; j < nrFeatures; j++) {
      double value = fv.value(j);
      Attribute attr = attrs.getAttribute(j);
      if (first) {
        first = false;
      } else {
        sb.append(", ");
      }
      if (asString && (attr.datatype == Datatype.nominal && attr.codeAs == CodeAs.number)) {
        LFAlphabet attralph = attr.alphabet;
        int attrvals = attralph.size();
        String str = "";   // the default value is used if we have -1, which means the feature was missing
        if ((int) value >= attrvals) {
          System.err.println("ERROR: value not in alphabet for attr " + attr + ": " + value);
        } else if (((int) value) == -1) {
          // use empty string
        } else {
          try {
            str = (String) attr.alphabet.lookupObject((int) value);
          } catch (Exception ex) {
            System.err.println("Could not get object for value: " + value);
            System.err.println("Feature index is " + j);
            System.err.println("Attribute is " + attr);
            System.err.println("Alphabet is " + attralph);
            System.err.println("Alphabet size is " + attrvals);
            throw new GateRuntimeException("Could not get object for value: " + value, ex);
          }
        }
        sb.append("\"");
        sb.append(escape4Json(str));
        sb.append("\"");
      } else {
        // TODO: depending on MV processing!!
        if (Double.isNaN(value)) {
          sb.append(0.0);
        } else {
          // if we 
          sb.append(value);
        }
      }
    }
    sb.append("]");
    return sb.toString();
  }

  /**
   * Convert a target to String representation.
   * @param target the target
   * @param targetAlphabet target alphabet
   * @param asString value should get represented as quoted string
   * @return string representation
   */
  public static String target2String(Object target, LabelAlphabet targetAlphabet, boolean asString) {
    StringBuilder sb = new StringBuilder();
    if (targetAlphabet != null) {
      Label tl;
      if (target instanceof Label) {
        tl = (Label) target;
      } else {
        tl = targetAlphabet.lookupLabel(target);
      }
      Object entry = tl.getEntry();
      if (entry instanceof String) {
        if (asString) {
          sb.append("\"");
          sb.append(escape4Json((String) entry));
          sb.append("\"");
        } else {
          sb.append(targetAlphabet.lookupIndex(entry));
        }
      } else if (entry instanceof NominalTargetWithCosts) {
        throw new GateRuntimeException("Cost vectors not yet implemented");
      }
    } else {
      if (target instanceof Double) {
        sb.append(target);
      } else {
        throw new GateRuntimeException("No target Alphabet and odd target class: " + target.getClass());
      }
    }
    return sb.toString();
  } // export

  public static String escape4Json(String str) {
    return str.replaceAll("([\\\\\"])", "\\\\$1");
  }

}
