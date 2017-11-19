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
package gate.plugin.learningframework.features;

import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Utils;
import gate.plugin.learningframework.LFUtils;
import gate.plugin.learningframework.mallet.NominalTargetWithCosts;
import gate.util.GateRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

/**
 * Base class for the FeatureExtraction classes.
 * 
 * This mainly just defines common constants for now, the actual implementations
 * work a bit differently since the use different representations for the instances.
 * 
 * @author Johann Petrak
 */
public class FeatureExtractionBase {

  // Naming scheme for follows the following pattern:
  // start with either: ${annotationType}${TYPESEP}${featureName}
  // or:                                 ${TYPESEP}${featureName}
  // or:                ${specifiedAttributeName}
  // followed by:       ${NAMESEP}A or ${NAMESEP}L${number} or ${NAMESEP}N${N}
  // optionally followed, for sparse feature vectors only, and only
  // if one-of-k representation, by: ${VALSEP}${actualValue}
  // (for dense, the value is not part of the feature name but the value)
  // or optionally followed, for sparse feature vectors only, if a feature
  // has a list value, by: ${ELEMSEP${elemNumber}
  // (for dense, the list will be the actual value of this single feature)
  // Ngram values are represented as gram${NGRAMSEP}gram..  
  // 

  /**
   * Separates the type name inside the name from the feature name. The scheme is
   * typename¦featurename where typename can be empty.
   */
  public static final String TYPESEP = "┆";

  /**
   * Separates the name from any additional information. Additional information is kind of attribute
   * (Ngram, attributelist etc), things like the element number if it is an attributelist, or the
   * value for nominal one-of-k coded features.
   */
  public static final String NAMESEP = "╬";

  /**
   * Separates the remainder of the feature name from the part that indicates the value for nominal
   * one-of-k coded features.
   */
  public static final String VALSEP = "═";

  /**
   * Separates the grams in an n-gram with n>1.
   */
  public static final String NGRAMSEP = "┋";

  /**
   * Separates the name of the feature from the element number, if we get the value for a numeric
   * attribute which is a list or array of numeric values.
   */
  public static final String ELEMSEP = "┄";

  public static final String MVVALUE = "╔MV╗";

  public static final String SEQ_INSIDE = "I";
  public static final String SEQ_BEGINNING = "B";
  public static final String SEQ_OUTSIDE = "O";

  public static final String PROP_HAVE_MV = "haveMV";
  public static final String PROP_IGNORE_HAS_MV = "ignore-MV";
  
  public static final String START_SYMBOL = "╔START╗";
  public static final String STOP_SYMBOL = "╔STOP╗";

  protected static final boolean DEBUG_SEQUENCE_CLASS = true;


}
