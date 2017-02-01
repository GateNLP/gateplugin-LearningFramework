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
 * Code for extracting features from a document based on a FeatureInfo. For now this is
 * intrinsically tied to Mallet: our own internal way of representing features, instances, alphabets
 * etc. uses in a large part the Mallet approach and the result of extracting features from the
 * document is a Mallet instance. Should we ever want to support other representations, this would
 * need to get refactored to an Interface or base class, with different implementations for
 * different internal representations.
 *
 * @author Johann Petrak
 */
public class FeatureExtraction {

  // We have to make sure that no two feature names that come from different attribute specifications
  // can be identical, and also that the different feature names that can come from the same attribute
  // specification for NGRAM and ATTRIBUTELIST are different from each other and those from other specs.
  // Also, the feature name should still be as short as possible, readable and contain as few
  // special characters as possible. 
  // UPDATE (2016-05-23): decided to change the naming scheme again to make it easier
  // to map between Mallet feature names and the original attributes.
  // Also, the instance annotation type is not getting included in the mallet feature
  // name any more so that a model trained on one annotation type can be applied to
  // another.
  // Description of the aims and how the mapping should work:
  // - each attribute in the feature specification maps to one or more mallet features
  // - each attribute in the feature specification is identified by either a "NAME" specification
  //   in the attribute element (which must be unique across all attribute entries), or 
  //   by the combination of annotation type and feature name specified in the attribute element.
  // - we call the NAME or the annotation type/feature name "attribute name".
  // - for each attribute name, there can be several mallet features for ngrams or attribute lists,
  //   and in case of one-of-k coded nominal features, for each possible value
  // New convention:
  // - the mallet name starts with the attribute name, followed by the NAMESEP character
  // - the mallet name can have one of three formats:
  //   = annotatioType TYPESEP featureName : if an anntation type is specified in the attribtue specification
  //     This means the feature will be taken from an annotation  with that type
  //     that overlaps with the instance annotation
  //   = TYPESEP featureName  : if not annotation type is specified in the attribute specification
  //     This means the feature will be take directly from the instance annotation
  //   = specifiedName : this overrides either of the two previous names if the NAME element was specified
  //     for an attribute specification.
  // - the character after the NAMESEP character indicates the type of the attribute specification,
  //   A for ATTRIBUTE, L for ATTRIBUTELIST and N for NGRAM
  // - If the type is ATTRIBUTE and the feature is a nominal one-of-k feature, then the feature name is
  //   followed by VALSEP followed by the value itself
  // - If the type is Attributelist then the "L" is followed by the element number in the attribute
  //   list, e.g. "-2", "0" or "3".
  //   If the feature is a nominal one-of-k feature then the feature name is followed by VALSEP followed
  //   by the value itself
  // - if the type is NGRAM, then the "N" is followed by the number (e.g. 2 for 2-grams). This is then
  //   followed by VALSEP and the ngram itself, with each gram separated by the others using NGRAMSEP
  // 
  // 
  // This is the old convention: 
  // Here is what we use for now:
  // If a NAME is specified in the attribute definition, then that name is used as the 
  // first part of the feature name prefix, optionally followed by #[i] where [i] is then
  // number of the attribute list element, e.g. "#-1". This means that an attribute name should
  // not contain numbers. 
  // If a NAME is not specified, then the feature name prefix is constructed in the following way
  // instead:
  // it starts with a "feature indicator" which is "A" for attribute, N[k] for
  // an ngram, A[i] for the ith entry in an attributelist and M[i]N[k] for an attribute list
  // for ngrams with n>1 (future!)
  // The feature indicator is followed by the NAMESEP character, then followed by the annotation
  // type, followed by NAMESEP and followed by the feature name. For a boolean feature
  // which indicates the presence of an annotation, the featuer name is empty.
  // This is either the whole feature name or it is followed by VALSEP and followed by the 
  // actual nominal value, if the feature is for a nominal value coded as one-of-k.
  // The value for an ngram is all the individual grams, concatenated witth NGRAMSEP.
  // NOTE: the various separater characters are all unicode characters taken
  // from the "Box Drawing" Unicode block as these are extremely unlikely
  // to be used either as part of annotation type or feature names or to 
  // occur inside ngrams of the text. No attempt is made to escape or otherwise
  // handle these characters IF they indeed occur in these places.
  /**
   * Separates the grams in an n-gram with n>1.
   */
  private static final String NGRAMSEP = "┋";

  /**
   * Separates the name from any additional information. Additional information is kind of attribute
   * (Ngram, attributelist etc), things like the element number if it is an attributelist, or the
   * value for nominal one-of-k coded features.
   */
  private static final String NAMESEP = "╬";

  /**
   * Separates the type name inside the name from the feature name. The scheme is
   * typename¦featurename where typename can be empty.
   */
  private static final String TYPESEP = "┆";

  /**
   * Separates the remainder of the feature name from the part that indicates the value for nominal
   * one-of-k coded features.
   */
  private static final String VALSEP = "═";

  /**
   * Separates the name of the feature from the element number, if we get the value for a numeric
   * attribute which is a list or array of numeric values.
   */
  private static final String ELEMSEP = "┄";

  private static final String MVVALUE = "╔MV╗";

  public static final String SEQ_INSIDE = "I";
  public static final String SEQ_BEGINNING = "B";
  public static final String SEQ_OUTSIDE = "O";

  public static final String PROP_HAVE_MV = "haveMV";
  public static final String PROP_IGNORE_HAS_MV = "ignore-MV";

  private static Logger logger = Logger.getLogger(FeatureExtraction.class.getName());

  public static void extractFeature(
          Instance inst,
          FeatureSpecAttribute att,
          AnnotationSet inputAS,
          Annotation instanceAnnotation) {
    if (att instanceof FeatureSpecAttributeList) {
      extractFeature(inst, (FeatureSpecAttributeList) att, inputAS, instanceAnnotation);
    } else if (att instanceof FeatureSpecSimpleAttribute) {
      extractFeature(inst, (FeatureSpecSimpleAttribute) att, inputAS, instanceAnnotation);
    } else if (att instanceof FeatureSpecNgram) {
      extractFeature(inst, (FeatureSpecNgram) att, inputAS, instanceAnnotation);
    } else {
      throw new GateRuntimeException("Attempt to call extractFeature with type " + att.getClass());
    }
  }

  /**
   * Extract the instance features for a simple attribute.
   *
   * This adds the internal features to the inst object and also updates any alphabets, if they
   * allow growth. If the annotation types of the instance annotation and the annotation specified
   * for the attribute are the same, then the instance annotation is directly used, otherwise, if
   * there is a single overlapping annotation of the annType specified in the attribute, that one is
   * used. If there is no overlapping annotation, nothing is extracted for that instance and
   * implicitly, all features are set to 0.0 (TODO: this should probably get treated inputAS if all
   * features were missing so that for each featureName, the proper missing value treatment can be
   * applied!) If there are several overlapping annotations, an exception is thrown, this should not
   * happen.
   *
   * If no featureName is specified, we give an indicator of the presence, artificial featureName
   * name for binary featureName.
   *
   *
   * @param inst
   * @param att
   * @param inputASname
   * @param instanceAnnotation
   * @param doc
   */
  private static void extractFeature(
          Instance inst,
          FeatureSpecSimpleAttribute att,
          AnnotationSet inputAS,
          Annotation instanceAnnotation) {
    Document doc = inputAS.getDocument();
    /*Although the user needn't specify the annotation annType if it's the
     * same inputAS the instance, they may do so. It's intuitive that if they
     * do so, they mean to extract the featureName from the instance, not just
     * the first colocated same annType annotation. This matters in
     * disambiguation, where we have many colocated same annType annotations.
     * Fix it up front by wiping out annType if it's the same inputAS the instance.
     */
    AugmentableFeatureVector fv = (AugmentableFeatureVector) inst.getData();
    String annType = att.annType;
    String featureName = att.feature;
    MissingValueTreatment mvt = att.missingValueTreatment;
    CodeAs codeas = att.codeas;
    Datatype dt = att.datatype;
    Alphabet alphabet = att.alphabet;
    String listsep = att.listsep;
    String featureName4Value = att.featureName4Value;
    // first of all get the annotation from where we want to construct the annotation.
    // If annType is the same type as the annType of the instance annotation, use the 
    // instance annotation directly. Otherwise, use an annotation of type annType that overlaps
    // with the instance annotation.
    // TODO: what do if there are several such overlapping annotations?
    // For now, we log a warning and use the longest!
    // Throwing an exception could be too harsh since there may be cases 
    // where this could occur, e.g. when the instance is a token and the 
    // type for which we construct features is Person. Normally, one would 
    // expect each person to be made up of several tokens, but it could e.g.
    // happen that Marie-Luise is annotated as a single token but Marie and 
    // Luise end up being annotated as separate Person annotations.

    Annotation sourceAnnotation = null;
    if (annType.isEmpty() || instanceAnnotation.getType().equals(annType)) {
      sourceAnnotation = instanceAnnotation;
      // annType = sourceAnnotation.getType();
      annType = "";
    } else {
      AnnotationSet overlappings = gate.Utils.getOverlappingAnnotations(inputAS, instanceAnnotation, annType);
      if (overlappings.size() > 1) {
        logger.warn("More than one overlapping annotation of type " + annType + " for instance annotation at offset "
                + gate.Utils.start(instanceAnnotation) + " in document " + doc.getName());
        // find the last longest (try to make this deterministic, there is 
        // still a small chance of non-determinism if there are more than one
        // overlapping annotations of the same length in the last position 
        // where a longest annotation occurs.
        int maxSize = 0;
        for (Annotation ann : overlappings.inDocumentOrder()) {
          if (gate.Utils.length(ann) > maxSize) {
            maxSize = gate.Utils.length(ann);
            sourceAnnotation = ann;
          }
        }
      } else if (overlappings.size() == 0) {
        // there is no overlappign annotation so we have to pass on null 
        // and treat this as a missing value
        sourceAnnotation = null;
      } else {
        // we have exactly one annotation, use that one
        sourceAnnotation = gate.Utils.getOnlyAnn(overlappings);
      }
    }
    // NOTE: there should be no way of how a featureName we encounter now is already in the featureName
    // vector, so we do not even check, we simply add the featureName.
    // How we add the featureName depends on the datatype, on the codeas setting if it is nominal,
    // and also on how we treat missing values.
    extractFeatureWorker(att.name, "A", inst, sourceAnnotation, doc, annType, featureName, featureName4Value, alphabet, dt, mvt, codeas, listsep);
  }

  /**
   * Do the actual hard work of extracting a feature and adding it to the Mallet feature vector.
   * This is used to do the actual extraction for simple attributes and attribute lists.
   *
   * @param name the name of the feature as defined in the attribute specification or an empty
   * string
   * @param internalFeatureIndicator the part of the feature name that indicates the type of
   * attribute specification, e.g. "A" for attribute or something like "L-3" for attribute list
   * @param inst the mallet instance
   * @param sourceAnnotation the annotation from which to extract the feature value
   * @param doc
   * @param annType the annotation type as defined in the attribute specification. This can be empty
   * if the original instance annotation is used.
   * @param featureName the feature name as defined in the attribute specification.
   * @param alphabet
   * @param dt
   * @param mvt
   * @param codeas
   */
  private static void extractFeatureWorker(
          String name,
          String internalFeatureIndicator,
          Instance inst,
          Annotation sourceAnnotation,
          Document doc,
          String annType,
          String featureName,
          String featureName4Value,
          Alphabet alphabet,
          Datatype dt,
          MissingValueTreatment mvt,
          CodeAs codeas,
          String listsep) {

    AugmentableFeatureVector fv = (AugmentableFeatureVector) inst.getData();
    // create the default feature name prefix: this is either "A"+NAMESEP+type+NAMESEP+featureName
    // or just the name give in the attribute
    String internalFeatureNamePrefix;
    if (name.isEmpty()) {
      internalFeatureNamePrefix = annType + TYPESEP + featureName + NAMESEP + internalFeatureIndicator;
    } else {
      internalFeatureNamePrefix = name + NAMESEP + internalFeatureIndicator;
    }
    // if the featureName name is empty, then all we want is indicate the presence of the annotation
    // inputAS a boolean. No matter what the datatype is, this is always indicated by setting the
    // featureName to 1.0 (while for all instances, where the annotation is missing, the value will
    // implicitly be set to 0.0). 
    // System.err.println("DEBUG: for fname="+featureName+",dt="+dt);
    if (featureName == null || featureName.isEmpty()) {
      // construct the featureName name and set to 1.0
      // however, only add the featureName if the featureName alphabet is allowed to grow.
      String fname = internalFeatureNamePrefix;
      setInFeatureVector(fv, fname, 1.0);
    } else {
      // First get the value inputAS an Object, if there is no value, we have an Object that is null
      // If the sourceAnnotation is null, we already did not find the source at all,
      // so the value is always missing.
      Object valObj = null;
      if (sourceAnnotation != null) {
        valObj = sourceAnnotation.getFeatures().get(featureName);
      }
      // no matter what the datatype is, a null is always a missing value, so we set the 
      // property that indicates the existence of a missing valuein the instance right here
      if (valObj == null) {
        inst.setProperty(PROP_HAVE_MV, true);
      } else {
        inst.setProperty(PROP_HAVE_MV, false);
      }
      // initialize the PROP_IGNORE_HAS_MV property to be false, if we have a MV which
      // causes the instance to get ignored we set it to true below
      inst.setProperty(PROP_IGNORE_HAS_MV, false);
      // if the datatype is nominal, we have to first check what the codeas setting is.
      if (dt == Datatype.nominal) {
        if (codeas == CodeAs.one_of_k) {
          if (valObj != null) {
            // it is not a missing value

            // if the value is an Iterable, create one feature for each element
            if (valObj instanceof Iterable) {
              Iterable iterable = (Iterable) valObj;
              for (Object obj : iterable) {
                String val = obj.toString();
                setInFeatureVector(fv, internalFeatureNamePrefix + VALSEP + val, 1.0);
              }
            } else if (valObj instanceof Map) {
              Map map = (Map) valObj;
              for (Object key : map.keySet()) {
                Object mapval = map.get(key);
                String val = key.toString() + "=" + mapval.toString();
                setInFeatureVector(fv, internalFeatureNamePrefix + VALSEP + val, 1.0);
              }
            } else if (valObj instanceof Object[]) {
              for (Object obj : ((Object[]) valObj)) {
                setInFeatureVector(fv, internalFeatureNamePrefix + VALSEP + obj.toString(), 1.0);
              }
            } else if (valObj instanceof int[]) {
              for (int intval : ((int[]) valObj)) {
                setInFeatureVector(fv, internalFeatureNamePrefix + VALSEP + intval, 1.0);
              }
              // TODO: other array types??
            } else {
              String val = valObj.toString();

              if (listsep != null && !listsep.isEmpty()) {
                // we need to have a look if the value needs the get split into elements
                String[] vals = val.split(listsep, -1);
                for (String v : vals) {
                  // NOTE: we automatically remove any empty elements here
                  if (!v.trim().isEmpty()) {
                    setInFeatureVector(fv, internalFeatureNamePrefix + VALSEP + v.trim(), 1.0);
                  }
                }
              } else // just take the value as is.
              // Only in this case we allow for optionally getting the score from a different
              // feature of the same annotation we got the value from. 
              if (featureName4Value.isEmpty()) {
                setInFeatureVector(fv, internalFeatureNamePrefix + VALSEP + val, 1.0);
              } else {
                // NOTE: sourceAnnotation should always ne non-null here since valObj is non-null
                double score = gate.plugin.learningframework.LFUtils.anyToDoubleOrElse(sourceAnnotation.getFeatures().get(featureName4Value), 1.0);
                setInFeatureVector(fv, internalFeatureNamePrefix + VALSEP + val, score);
              }
            }
          } else {
            // we have a missing value, check the missing value treatment for what to do now
            switch (mvt) {
              case ignore_instance:
                inst.setProperty(PROP_IGNORE_HAS_MV, true);
                break;
              case keep:  // this represents the MV by not setting any indicator featureName, so nothing to do
                //System.out.println("DEBUG: oneofk, mv, keep");
                break;
              case zero_value: // we treat this identical to keep: no feature set
                break;
              case special_value: // we use the predefined special value
                setInFeatureVector(fv, internalFeatureNamePrefix + VALSEP + MVVALUE, 1.0);
                break;
              default:
                throw new NotImplementedException("MV-Handling");
            }
          }
        } else if (codeas == CodeAs.number) { // end of CodeAs.one_of_k
          if (valObj != null) {
            // For this representation, we need to maintain a dictionary that maps values to 
            // numbers! This is also done using an Alphabet, and if a value is not in the alphabet,
            // then if the alphabet is allowed to grow, we simply get the new value. But if the 
            // alphabet is not allowed to grow (at application time), finding a new value is an 
            // error and causes an exception
            // The alphabet for an number-coded attribute is stored in the SimpleAttribute object.
            // before we handle the value, we need to convert it to a string
            String val = valObj.toString();
            if (alphabet.contains(val)) {
              // add the featureName, using the value we have stored for it, but only if the featureName
              // itself can be added
              setInFeatureVector(fv, internalFeatureNamePrefix, alphabet.lookupIndex(val));
            } else // we have not seen this value: if the alphabet is allowed to grow add it and
            // then try to add the featureName, otherwise, do nothing
            if (!alphabet.growthStopped()) {
              // the lookupIndex method automatically adds the value if it is not there yet
              setInFeatureVector(fv, internalFeatureNamePrefix, alphabet.lookupIndex(val));
            } else {
              //System.out.println("DEBUG: number, growStopped");
            }
          } else {
            // we have a nominal value that should get coded numeric but it is a missing value
            switch (mvt) {
              case ignore_instance:
                inst.setProperty(PROP_IGNORE_HAS_MV, true);
                //System.out.println("DEBUG: other, mv, setProp");
                break;
              case keep:
                setInFeatureVector(fv, internalFeatureNamePrefix, Double.NaN);
                break;
              case zero_value: // use the "special_value" 
                setInFeatureVector(fv, internalFeatureNamePrefix, 0.0);
                String val = MVVALUE;
                if (alphabet.contains(val)) {
                  setInFeatureVector(fv, internalFeatureNamePrefix, alphabet.lookupIndex(MVVALUE));
                } else if (!alphabet.growthStopped()) {
                  setInFeatureVector(fv, internalFeatureNamePrefix, alphabet.lookupIndex(MVVALUE));
                } else {
                  //System.out.println("DEBUG: number, growStopped");
                }
                break;
              case special_value: // we use the special value -1.0 which should get handled by Mallet somehow
                setInFeatureVector(fv, internalFeatureNamePrefix, -1.0);
                break;
              default:
                throw new NotImplementedException("MV-Handling");
            }
          } // if valObj != null .. else
        } else {  // codeas setting for the nominal attribute
          throw new NotImplementedException("CodeAs method not implemented");
        }
      } else if (dt == Datatype.numeric) {
        if (valObj != null) {
          // just add the value, if possible and if the object can be interpreted inputAS a number
          double val = 0.0;
          if (valObj instanceof Number) {
            val = ((Number) valObj).doubleValue();
            setInFeatureVector(fv, internalFeatureNamePrefix, val);
          } else if (valObj instanceof Boolean) {
            if ((Boolean) valObj) {
              val = 1.0;
            } else {
              val = 0.0;
            }
            setInFeatureVector(fv, internalFeatureNamePrefix, val);
          } else if (valObj instanceof double[]) {
            // create one feature for each entry in the array
            int i = 0;
            for (double el : ((double[]) valObj)) {
              setInFeatureVector(fv, internalFeatureNamePrefix + ELEMSEP + i, el);
              i++;
            }
          } else if (valObj instanceof Iterable) {
            int i = 0;
            for (Object el : (Iterable) valObj) {
              val = LFUtils.anyToDoubleOrElse(el, 0.0);
              if (val != 0.0) {
                setInFeatureVector(fv, internalFeatureNamePrefix + ELEMSEP + i, LFUtils.anyToDoubleOrElse(el, val));
              }
              i++;
            }
          } else {
            // try to convert the string to a number. If that fails, just use 0.0 but log a warning
            try {
              val = Double.parseDouble(valObj.toString());
            } catch (Exception ex) {
              val = 0.0;
              logger.warn("Cannot parse String " + valObj + " as a number, using 0.0 for annotation of type "
                      + sourceAnnotation.getType()
                      + // take it from the annotation, annType can be empty!
                      " at offset " + gate.Utils.start(sourceAnnotation) + " in document " + doc.getName());
            }
            setInFeatureVector(fv, internalFeatureNamePrefix, val);
          }
          //System.err.println("DEBUG: for fname="+featureName+",dt="+dt+", valObj="+valObj+", fv="+fv.numLocations());
        } else {
          // we have a numeric missing value!
          switch (mvt) {
            case ignore_instance:
              inst.setProperty(PROP_IGNORE_HAS_MV, true);
              //System.out.println("DEBUG: numeric, mv, setProp");
              break;
            case keep:  // for this kind of codeas, we use the value NaN
              setInFeatureVector(fv, internalFeatureNamePrefix, Double.NaN);
              break;
            case zero_value: // use the first value, does not make much sense really, but ...
              // TODO: document that this combination should be avoided, probably
              setInFeatureVector(fv, internalFeatureNamePrefix, 0.0);
              break;
            case special_value: // we use the special value -1.0 which should get handled by Mallet somehow
              setInFeatureVector(fv, internalFeatureNamePrefix, -1.0);
              break;
            default:
              throw new NotImplementedException("MV-Handling");
          }
          //System.err.println("DEBUG: for fname="+featureName+",dt="+dt+", valObj="+valObj+", fv="+fv.numLocations());
        }
      } else if (dt == Datatype.bool) {
        if (valObj != null) {
          double val = 0.0;
          if (valObj instanceof Boolean) {
            if ((Boolean) valObj) {
              val = 1.0;
            }
          } else if (valObj instanceof Number) {
            if (((Number) valObj).doubleValue() != 0) {
              val = 1.0;
            }
          } else {
            try {
              boolean tmp = Boolean.parseBoolean(valObj.toString());
              if (tmp) {
                val = 1.0;
              }
            } catch (Exception ex) {
              // value is already 0.0
              logger.warn("Cannot parse String " + valObj + " as a boolean, using 0.0 for annotation of type "
                      + sourceAnnotation.getType()
                      + // take it from the annotation, annType can be empty!
                      " at offset " + gate.Utils.start(sourceAnnotation) + " in document " + doc.getName());
            }
          }
          setInFeatureVector(fv, internalFeatureNamePrefix, val);
        } else {
          // we have a missing boolean value
          switch (mvt) {
            case ignore_instance:
              //System.out.println("DEBUG: boolean, mv, setProp");
              inst.setProperty(PROP_IGNORE_HAS_MV, true);
              break;
            case keep:  // for this kind of codeas, we use the value NaN
              setInFeatureVector(fv, internalFeatureNamePrefix, Double.NaN);
              break;
            case zero_value: // Use zero which will make false identical to missing
              // and work well with sparse vectors
              setInFeatureVector(fv, internalFeatureNamePrefix, 0.0);
              break;
            case special_value: // we use the special value -1.0 which should get handled by Mallet somehow
              setInFeatureVector(fv, internalFeatureNamePrefix, 0.5);
              break;
            default:
              throw new NotImplementedException("MV-Handling");
          }

        }
      } else {
        throw new NotImplementedException("Datatype!");
      }

    }
    //System.out.println("DEBUG: worker, fv is now"+fv.numLocations());
  } // extractFeature (SimpleAttribute)

  /*
   *
   */
  // TODO: NOTE: this currently returns a single string which represents all N-grams 
  // If there are at least n annotations inputAS speficied by the Ngam TYPE contained in the span of 
  // the instance annotation, then those annotations are arranged in document order and
  // - starting with the second index, up to the last
  // - 
  // CHECK: if we get the same ngram multiple times, we should have a count!!! e.g. unigram "fred" three
  // times we should have 3.0
  // TODO: check what to do if the contained annotations are not in non-overlapping order: should we
  // create an ngram if the second annotations starts before the end of the first or even at the same 
  // offset inputAS the first? If that is the case, what should the order of the annotations then be?
  // NOTE: if the featureName is missing, i.e. it is null or the empty string, then the whole annotation gets ignored
  private static void extractFeature(
          Instance inst,
          FeatureSpecNgram ng,
          AnnotationSet inputAS,
          Annotation instanceAnnotation
  ) {
    Document doc = inputAS.getDocument();
    AugmentableFeatureVector fv = (AugmentableFeatureVector) inst.getData();
    int number = ng.number;
    String annType = ng.annType;
    String featureName = ng.feature;
    String featureName4Value = ng.featureName4Value;
    // TODO: this we rely on the ngram only having allowed field values, e.g. annType
    // has to be non-null and non-empty and number has to be > 0.
    // If featureName is null, then for ngrams, the string comes from the covered document
    String[] gram = new String[number];
    List<Annotation> al = Utils.getContainedAnnotations(inputAS, instanceAnnotation, annType).inDocumentOrder();
    // If we have less annotations than our n for n-gram, there is certainly nothing to do, 
    // leave the featureName vector untouched.
    if (al.size() < number) {
      return;
    }
    // this will hold the actual token strings to use for creating the n-grams
    List<String> strings = new ArrayList<String>();
    // this will hold the score to use for each string we extract, but only of the
    // featureName4Value was specified and exists.
    List<Double> scores = new ArrayList<Double>();

    for (Annotation ann : al) {
      // for ngrams we either have a featureName name 
      if (featureName != null) {
        // NOTE: if the featureName is not a string, we convert it to string
        Object obj = ann.getFeatures().get(featureName);
        // if there is no value at all, then the annotation is ignored
        if (obj != null) {
          String tmp = obj.toString().trim();
          // if the resulting string is empty, it is also ignored 
          if (!tmp.isEmpty()) {
            strings.add(tmp);
            double score = 1.0;
            if (!featureName4Value.isEmpty()) {
              score = gate.plugin.learningframework.LFUtils.anyToDoubleOrElse(ann.getFeatures().get(featureName4Value), 1.0);
            }
            scores.add(score);
          }
        }
      } else {
        // if the featureName is null, we get the string from the cleaned document text
        String tmp = gate.Utils.cleanStringFor(doc, ann).trim();
        if (!tmp.isEmpty()) {
          strings.add(tmp);
          double score = 1.0;
          if (!featureName4Value.isEmpty()) {
            score = gate.plugin.learningframework.LFUtils.anyToDoubleOrElse(ann.getFeatures().get(featureName4Value), 1.0);
          }
          scores.add(score);
        }
      }
    } // for Annotation ann : al
    // Now construct the actual ngrams and add them to the augmentable featureName vector. 
    // In the process, check first if such a featureName is already there, and if yes, just 
    // increment the value.
    // To avoid overhead, we only create the ngrams on the fly// Now construct the actual ngrams and add them to the augmentable feature vector. 
    // In the process, check first if such a feature is already there, and if yes, just 
    // increment the value.
    // To avoid overhead, we only create the ngrams on the fly

    // first check if our strings array is actually big enough so we can create at least one n-gram
    if (strings.size() < number) {
      return;
    }

    // now create the ngrams inputAS follows: starting with the first element in strings, go
    // through all the elements up to the (size-n)ths and concatenate with the subsequent 
    // n stings using the pre-defined separator character.
    StringBuilder sb = new StringBuilder();
    for (int i = 0; i < (strings.size() - number + 1); i++) {
      sb.setLength(0);
      // NOTE: the score for an ngram is calculated by multiplying the scores
      // for each part of the ngram. If there is no featureName4Value, then all those
      // scores are 1.0 at this point as well, so the final score is also 1.0.
      // If we have a featureName4Value and it is a 1-gram, then we get the value of that
      // feature. If it is an n-gram with n>1, then we get the product of all the scores
      // of each gram. 
      double score = 1.0;
      for (int j = 0; j < number; j++) {
        if (j != 0) {
          sb.append(NGRAMSEP);
        }
        sb.append(strings.get(i + j));
        score = score * scores.get(i + j);
      }
      String ngram = sb.toString();
      // we have got our ngram now, count it, but only add if we are allowed to!
      String prefix;
      if (ng.name.isEmpty()) {
        prefix = annType + TYPESEP + featureName;
      } else {
        prefix = ng.name;
      }
      prefix = prefix + NAMESEP + "N" + number;
      // NOTE: for now, we always add to any existing value of the feature vector we 
      // may already have. That way, if some ngram occurs multiple times, we use the 
      // sum its scores (and the score either is just 1.0 or whatever we got from using
      // the featureName4Value value).
      accumulateInFeatureVector(fv, prefix + VALSEP + ngram, score);
      // NOTE: previously, we only accumulated if there was no weight feature, otherwise
      // the weight was directly used without accumulation
      //if (featureName4Value.isEmpty()) {
      //  accumulateInFeatureVector(fv, prefix + VALSEP + ngram, score);
      //} else {
      //  setInFeatureVector(fv, prefix + VALSEP + ngram, score);
      //}
    }
    //System.err.println("DEBUG: Vector after adding feature "+ng+" is now "+fv);
  } // extractFeature(NGram)

  private static void extractFeature(
          Instance inst,
          FeatureSpecAttributeList al,
          AnnotationSet inputAS,
          Annotation instanceAnnotation
  ) {

    // If the type from the attribute specification is the same as the 
    // instance annotation type or if it is empty, we create the elements
    // from the instance annotations. Otherwise we use the specified type. 
    // 
    // The annotation -1 is one that ends before the beginning of annotation 0
    // Annotation -2 is one that ends before the beginning of annottion -1
    // Annotation +1 is one that starts after the end of annotation 0
    // Annotation +2 is one that starts after the end of annotation 1
    // Annotation 0 is the one which is either current instance (if we use 
    // instance annotations) or the longest one that overlaps with the current 
    // instance (similar to a Simple Attr).
    Document doc = inputAS.getDocument();
    AugmentableFeatureVector fv = (AugmentableFeatureVector) inst.getData();

    Datatype dt = al.datatype;
    String annType = al.annType;
    String annType4Feature = annType;
    String annType4Getting = annType;
    if (annType4Getting == null || annType4Getting.isEmpty()) {
      annType4Getting = instanceAnnotation.getType();
    }
    String featureName = al.feature;
    String withinType = al.withinType;
    int from = al.from;
    int to = al.to;
    Alphabet alphabet = al.alphabet;
    MissingValueTreatment mvt = al.missingValueTreatment;
    CodeAs codeas = al.codeas;
    String listsep = al.listsep;
    String featureName4Value = al.featureName4Value;

    // First of all, get the annotation 0 and also get the set of the 
    // annotation types we are interested in.
    // If we have a "WITHIN" declaration, we immediately limit the 
    // set of interesting annotations to those within the containing annotation.
    Annotation sourceAnnotation = null;
    long rangeFrom = 0L;
    long rangeTo = doc.getContent().size();
    AnnotationSet withinSet = inputAS;
    if (withinType != null) {
      AnnotationSet withins = gate.Utils.getCoveringAnnotations(inputAS, instanceAnnotation, withinType);
      if (withins.size() != 1) {
        logger.warn("More than one covering WITHIN annotation for " + instanceAnnotation + " in document " + doc.getName());
      }
      if (withins.size() == 0) {
        logger.warn("No covering WITHIN annotation for " + instanceAnnotation + " in document " + doc.getName());
        return; // Skip this instance!
      }
      Annotation within = withins.iterator().next(); // get an arbitrary one
      rangeFrom = within.getStartNode().getOffset();
      rangeTo = within.getEndNode().getOffset();
      withinSet = gate.Utils.getContainedAnnotations(inputAS, within, annType4Getting);
    }
    if (annType.isEmpty() || instanceAnnotation.getType().equals(annType)) {
      sourceAnnotation = instanceAnnotation;
      // TODO: if we have within, get set of instance annotations within within.
      // If not even the original annotation is within, do nothing but log 
      // a warning
      annType4Feature = "";
    } else {
      AnnotationSet overlappings = gate.Utils.getOverlappingAnnotations(inputAS, instanceAnnotation, annType4Getting);
      if (overlappings.size() > 1) {
        logger.warn("More than one overlapping annotation of type " + annType4Getting + " for instance annotation at offset "
                + gate.Utils.start(instanceAnnotation) + " in document " + doc.getName());
        // find the last longest (try to make this deterministic, there is 
        // still a small chance of non-determinism if there are more than one
        // overlapping annotations of the same length in the last position 
        // where a longest annotation occurs.
        int maxSize = 0;
        for (Annotation ann : overlappings.inDocumentOrder()) {
          if (gate.Utils.length(ann) > maxSize) {
            maxSize = gate.Utils.length(ann);
            sourceAnnotation = ann;
          }
        }
      } else if (overlappings.size() == 0) {
        // there is no overlappign annotation 
        // For lists we do not treat this as a missing value and instead 
        // just do not create anything for this instance annotation
        // TODO: log this
        return;
      } else {
        // we have exactly one annotation, use that one
        sourceAnnotation = gate.Utils.getOnlyAnn(overlappings);
      }
    }

    long start = sourceAnnotation.getStartNode().getOffset();
    long end = sourceAnnotation.getEndNode().getOffset();
    List<Annotation> annlistforward = inputAS.getContained(end, rangeTo).get(annType4Getting).inDocumentOrder();
    List<Annotation> annlistbackward = inputAS.getContained(rangeFrom, start).get(annType4Getting).inDocumentOrder();
    System.err.println("rangeFrom=" + rangeFrom + ", rangeTo=" + rangeTo + ",START=" + start + ", END=" + end + ", forwardsize=" + annlistforward.size() + ", backwardsize=" + annlistbackward.size());
    // go through each of the members in the attribute list and get the annotation
    // then process each annotation just like a simple annotation, only that the name of 
    // featureName gets derived from this list attribute plus the location in the list.
    // TODO: this does not work if the annotations are overlapping!!!

    // First loop: go from index -1 to the smallest from index to the left
    int albsize = annlistbackward.size();
    for (int i = -1; i >= from; i--) {
      // -1 corresponds to element (size-1) in the list, 
      // -2 corresponds to element (size-2) in the list etc. 
      // in general we want element (size+i) if that is > 0
      if (albsize + i >= 0) {
        Annotation ann = annlistbackward.get(albsize + i);
        extractFeatureWorker(al.name, "L" + i, inst, ann, doc, annType4Feature,
                featureName, featureName4Value, alphabet, dt, mvt, codeas, listsep);
      } else {
        break;
      }
    }
    // if we have index 0 in the range, process for that one
    if (from <= 0 && to >= 0) {
      extractFeatureWorker(al.name, "L" + 0, inst, sourceAnnotation, doc, annType4Feature,
              featureName, featureName4Value, alphabet, dt, mvt, codeas, listsep);
    }
    // do the ones to the right
    int alfsize = annlistforward.size();
    for (int i = 1; i <= to; i++) {
      // for i=1 we get element 0, in general we get element i-1
      // if i <= size
      if (i <= alfsize) {
        Annotation ann = annlistforward.get(i - 1);
        extractFeatureWorker(al.name, "L" + i, inst, ann, doc, annType4Feature,
                featureName, featureName4Value, alphabet, dt, mvt, codeas, listsep);
      } else {
        break;
      }
    }
  } // extractFeature (AttributeList)

  // *****************************************************************************
  // Extract the target stuff
  // *****************************************************************************
  public static void extractNumericTarget(Instance inst, String targetFeature, Annotation instanceAnnotation, AnnotationSet inputAS) {
    Document doc = inputAS.getDocument();
    Object obj = instanceAnnotation.getFeatures().get(targetFeature);
    // Brilliant, we have a missing target, WTF? Throw an exception
    if (obj == null) {
      throw new GateRuntimeException("No target value for feature " + targetFeature
              + " for instance at offset " + gate.Utils.start(instanceAnnotation) + " in document " + doc.getName());
    }
    double value = Double.NaN;
    if (obj instanceof Number) {
      value = ((Number) obj).doubleValue();
    } else {
      String asString = obj.toString();
      try {
        value = Double.parseDouble(asString);
      } catch (Exception ex) {
        throw new GateRuntimeException("Could not convert target value to a double for feature " + targetFeature
                + " for instance at offset " + gate.Utils.start(instanceAnnotation) + " in document " + doc.getName());
      }
    }
    inst.setTarget(value);
  }

  /**
   * Extract the class label for the given instance annotation. This gets used when the task
   * performed is classification (using either a classification algorithm or a sequence tagging
   * algorithm). In both cases, the class label is fetched from the instance annotation as the value
   * of the targetFeature.
   *
   * Mallet relies on the target alphabet having as many entries as there are classes. To support
   * per instance cost distributions, we store the distribution as a LabelWithCosts instance. This
   * instance is used as the Entry for a mallet Label and as long as the minimum cost class is the
   * same, the hashcodes and equals function will consider them to be identical.
   *
   * @param inst
   * @param alphabet the label alphabet, must be of type LabelAlphabet
   * @param targetFeature
   * @param instanceAnnotation
   * @param doc
   */
  public static void extractClassTarget(Instance inst, Alphabet alphabet, String targetFeature, Annotation instanceAnnotation, AnnotationSet inputAS) {
    LabelAlphabet labelalphabet = (LabelAlphabet) alphabet;
    Document doc = inputAS.getDocument();
    Object obj = instanceAnnotation.getFeatures().get(targetFeature);
    // Brilliant, we have a missing target, WTF? Throw an exception
    if (obj == null) {
      throw new GateRuntimeException("No target value for feature " + targetFeature
              + " for instance at offset " + gate.Utils.start(instanceAnnotation) + " in document " + doc.getName());
    } else if (obj instanceof List) {
      NominalTargetWithCosts lwc = new NominalTargetWithCosts((List<Double>) obj);
      inst.setTarget(labelalphabet.lookupLabel(lwc));
    } else if (obj instanceof double[]) {
      NominalTargetWithCosts lwc = new NominalTargetWithCosts((double[]) obj);
      inst.setTarget(labelalphabet.lookupLabel(lwc));
    } else {
      // all other things are treated as a string 
      String value = obj.toString();
      inst.setTarget(labelalphabet.lookupLabel(value));
    }
  }

  /**
   * Extract the class for an instance for sequence tagging.
   *
   * In the case of sequence tagging, we construct the class based on the instance's position
   * relative to the class annotation annType. If it occurs at the beginning of the class
   * annotation, it's a "beginning". In the middle or at the end, it's an "inside". Instances that
   * don't occur in the span of a class annotation are an "outside".
   *
   * This directly sets the target of the instance to a Label object that corresponds to one of the
   * three classes. In the case of NER classes, the target alphabet is always a labelalphabet and
   * pre-filled with all possible class labels when this method is invoked, so it does not matter if
   * the growth of the alphabet is stopped or not.
   *
   * @param inst The instance where the target should be set
   * @param classType The annotation name of the annotation that represents the class, e.g. "Person"
   * (this is required for the sequence tagging task!)
   * @param alph the label alphabet to use, must be an instance of LabelAlphabet
   * @param inputASname, the annotation set name of the set which contains the class annotations
   * @param instanceAnnotation, the instance annotation, e.g. "Token".
   * @param doc the document which is currently being processed
   */
  public static void extractClassForSeqTagging(Instance inst, Alphabet alph, AnnotationSet classAS, Annotation instanceAnnotation) {
    String target = "";
    Document doc = classAS.getDocument();
    if (!(alph instanceof LabelAlphabet)) {
      throw new GateRuntimeException("LF extractClassForSeqTagging: the alphabet must be of type LabelAlphabet"
              + " for instance annotation at offset " + gate.Utils.start(instanceAnnotation)
              + " in document " + doc.getName());
    }
    LabelAlphabet labelalph = (LabelAlphabet) alph;
    AnnotationSet overlappingClassAnns = Utils.getOverlappingAnnotations(classAS, instanceAnnotation);
    // Note: each instance annotation should only overlap with at most one class annotation.
    // Like with overlapping annotations from the feature specification, we log a warning and 
    // pick the longest here
    if (overlappingClassAnns.size() > 0) {
      Annotation classAnn = null;
      if (overlappingClassAnns.size() > 1) {
        logger.warn("More than one class annotation for instance at offset "
                + gate.Utils.start(instanceAnnotation) + " in document " + doc.getName());
        // find the longest
        int maxSize = 0;
        for (Annotation ann : overlappingClassAnns.inDocumentOrder()) {
          if (gate.Utils.length(ann) > maxSize) {
            maxSize = gate.Utils.length(ann);
            classAnn = ann;
          }
        }
      } else {
        classAnn = gate.Utils.getOnlyAnn(overlappingClassAnns);
      }
      // NOTE: this does allow situations where an instance annotation starts with the class
      // annotation and goes beyond the end of the class annotation or where it starts within
      // a class annotation and goes beyond the end. This is weird, but still probably the best
      // way to handle this. 
      if (classAnn.getStartNode().getOffset().equals(instanceAnnotation.getStartNode().getOffset())) {
        target = SEQ_BEGINNING;
      } else {
        target = SEQ_INSIDE;
      }
    } else {
      //No overlapping mentions so it's an outside
      target = SEQ_OUTSIDE;
    }
    // we now have the target label as a string, now set the target of the instance to 
    // to the actual label
    // NOTE: the target alphabet for such an instance MUST be a LabelAlphabet!
    inst.setTarget(labelalph.lookupLabel(target));
  }

  public static boolean ignoreInstanceWithMV(Instance inst) {
    Object val = inst.getProperty(PROP_IGNORE_HAS_MV);
    if (val == null) {
      return false;
    }
    return ((Boolean) inst.getProperty(PROP_IGNORE_HAS_MV));
  }

  public static boolean instanceHasMV(Instance inst) {
    Object val = inst.getProperty(PROP_HAVE_MV);
    if (val == null) {
      return false;
    }
    return ((Boolean) inst.getProperty(PROP_HAVE_MV));
  }

  /**
   * Extract the exact location of the instance for use as an instance name. The name string is made
   * up of the document name plus the start and end offsets of the instance annotation.
   */
  public static void extractName(Instance inst, Annotation instanceAnnotation, Document doc) {
    String value = doc.getName() + ":" + gate.Utils.start(instanceAnnotation) + ":"
            + gate.Utils.end(instanceAnnotation);
    inst.setName(value);
  }

  /**
   * Return the attribute name part of a ML feature.
   *
   * @param attributes
   * @param mlFeature
   * @return
   */
  public static String attrName4MlFeature(String mlFeature) {
    // first get the attribute name part
    String parts[] = mlFeature.split(NAMESEP, -1);
    if (parts.length < 2) {
      throw new RuntimeException("Odd ML feature name, does not contain a NAMESEP: " + mlFeature);
    }
    return parts[0];
  }

  /**
   * Try and find the attribute that corresponds to the ML featureName.
   *
   * This requries the instance annotation type because the way how the ML feature is generated
   * depends on the instance annotation type.
   *
   * @param attributes
   * @param featureName
   * @return
   */
  public static FeatureSpecAttribute lookupAttributeForFeatureName(List<FeatureSpecAttribute> attributes,
          String mlFeatureName, String instanceType) {
    FeatureSpecAttribute ret = null;
    String attrName = attrName4MlFeature(mlFeatureName);
    // now if the attrName contains a TYPESEP, we have to find the attribute by
    // type and feature name, otherwise we simply find it by name
    if (attrName.contains(TYPESEP)) {
      String parts[] = attrName.split(TYPESEP, -1);
      if (parts.length != 2) {
        throw new RuntimeException("ODD ATTRIBUTE NAME"); // should never happen
      }
      String featType = parts[0];
      String featName = parts[1];
      for (FeatureSpecAttribute attr : attributes) {
        if (featName.equals(attr.feature)) {
          // the types should also match but we have to consider that the 
          // type in the attribute specification can be empty in which case we 
          // have a match if the type from the mlFeature is also empty or matches
          // the instanceType. The same is true vice-versa: if the type from the
          // mlFeature is empty than the type from the attr spec must be empty
          // or match the instanceType
          if (featType.equals(attr.annType)) {
            ret = attr;
            break;
          } else if (featType.isEmpty()) {
            if (attr.annType.isEmpty() || attr.annType.equals(instanceType)) {
              ret = attr;
              break;
            }
          } else if (attr.annType.isEmpty()) {
            if (featType.isEmpty() || featType.equals(instanceType)) {
              ret = attr;
              break;
            }
          }
        }
      }
    } else {
      for (FeatureSpecAttribute attr : attributes) {
        if (attr.name != null && attr.name.equals(attrName)) {
          ret = attr;
          break;
        }
      }
    }
    return ret;
  }

  ///=======================================
  /// HELPER AND UTILITY METHODS
  ///=======================================
  /**
   * Set a feature in the feature vector, to the given value.
   * However, if growth is stopped, do not set the feature if the key is not known.
   *
   * This method assumes that the key for this feature vector is only set once, if it 
   * is set another time for the same feature vector, any old value is overridden!
   * 
   * @param fv
   * @param key
   * @param val
   */
  private static void setInFeatureVector(AugmentableFeatureVector fv, Object key, double val) {
    Alphabet a = fv.getAlphabet();
    if (!a.contains(key) && a.growthStopped()) {
      //System.err.println("DEBUG: GROWTH STOPPED! key="+key+",a="+a);
      return;
    }
    if(fv.contains(key)) {
      System.err.println("LF DEBUG: setting/overriding a value where there is already one! key="+key);      
      fv.setValue(a.lookupIndex(key), val);
    } else {
      fv.add(key, val);
    }
  }

  private static void accumulateInFeatureVector(AugmentableFeatureVector fv, Object key, double val) {
    Alphabet a = fv.getAlphabet();
    if (!a.contains(key) && a.growthStopped()) {
      return;
    }
    fv.add(key,val);
    // Instead of the previous statement the following was used for debugging:
    //if(fv.contains(key)) {
    //  fv.add(key,val);
      //System.err.println("DEBUG accumulate: adding to existing: key="+key+" index="+a.lookupIndex(key)+" loc="+fv.location(a.lookupIndex(key)));
    //} else {
    //  fv.add(key,val);
      //System.err.println("DEBUG accumulate: creating new: key="+key+" index="+a.lookupIndex(key)+" loc="+fv.location(a.lookupIndex(key)));
    //}
  }

}
