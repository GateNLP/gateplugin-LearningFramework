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

import java.util.ArrayList;
import java.util.List;


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

  public static String featureName4Attribute(String type, String feature) {
    return type + TYPESEP + feature + NAMESEP + "A";
  }
  
  public static String featureName4Attribute(String name) {
    return name + NAMESEP + "A";
  }
  
  public static String featureName4AttributeList(String type, String feature, int elementnumber) {
    return type + TYPESEP + feature + NAMESEP + "L" + elementnumber;
  }
  public static String featureName4AttributeList(String name, int elementnumber) {
    return name + NAMESEP + "L" + elementnumber;
  }
  
  public static String featureName4Ngram(String type, String feature, int n) {
    return type + TYPESEP + feature + NAMESEP + "N" + n;
  }
  public static String featureName4Ngram(String name, int n) {
    return name + NAMESEP + "N" + n;
  }
  
  
  /**
   * Generate the feature name from its components.
   * 
   * For a sparse feature this is the prefix of the feature name and VALSEP
   * and the value itself would need to get appended as well.
   * 
   * @param attributeName the name from the attribute declaration in the feature specification file, can be null or empty
   * @param annType the name of the annotation type of the instance annotations, can be empty
   * @param featureName the name of the feature
   * @param attrKind the kind of the attribute, e.g. "A" or "N3" or "L-2"
   * @return 
   */
  public static String featureNamePrefix(String attributeName, String annType, String featureName, String attrKind) {
    String internalFeatureName;
    if (attributeName == null || attributeName.isEmpty()) {
      internalFeatureName = annType + TYPESEP + featureName + NAMESEP + attrKind;
    } else {
      internalFeatureName = attributeName + NAMESEP + attrKind;
    }
    return internalFeatureName;
  }
  
  // This method converts a list of FeatureSpecAttribute instances into
  // a list of feature names. Note that for the AttributeList instances
  // more than one feature name may get created.
  public static List<String> featureSpecAttributes2FeatureNames(List<FeatureSpecAttribute> attrs) {
    List<String> fnames = new ArrayList<>();    
    String fname = null;
    for(FeatureSpecAttribute attr : attrs) {
      String name = attr.name;
      String type = attr.annType;
      String feature = attr.feature;
      boolean haveName = (name!=null && !name.isEmpty());
      if(attr instanceof FeatureSpecNgram) {
        fname = haveName ? 
                featureName4Ngram(name, ((FeatureSpecNgram)attr).number) :
                featureName4Ngram(type, feature, ((FeatureSpecNgram)attr).number);
        fnames.add(fname);
      } else if(attr instanceof FeatureSpecAttributeList) {
        int from = ((FeatureSpecAttributeList)attr).from;
        int to = ((FeatureSpecAttributeList)attr).to;
        for(int i=from;i<=to;i++) {
          fname = haveName ? 
                  featureName4AttributeList(name, i) :
                  featureName4AttributeList(type, feature, i);
          fnames.add(fname);          
        }
      } else {
        fname = haveName ? 
                featureName4Attribute(name) :
                featureName4Attribute(type, feature);
        fnames.add(fname);        
      }
    }
    return fnames;
  }


}
