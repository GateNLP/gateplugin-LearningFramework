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

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Utils;
import gate.plugin.learningframework.data.InstanceRepresentation;
import gate.util.GateRuntimeException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Code for extracting features from a document based on a FeatureInfo into a dense representation. 
 * 
 * @author Johann Petrak
 */
public class FeatureExtractionDense extends FeatureExtractionBase {

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
  

  private static Logger LOGGER = Logger.getLogger(FeatureExtractionDense.class.getName());

  
  /**
   * Dispatch to the proper helper method for extracting a feature of that type.
   * @param inst
   * @param att
   * @param inputAS
   * @param instanceAnnotation 
   * @return  
   */
  public static InstanceRepresentation extractFeature(
          InstanceRepresentation inst,
          FeatureSpecAttribute att,
          AnnotationSet inputAS,
          Annotation instanceAnnotation) {
    if (att instanceof FeatureSpecAttributeList) {
      return extractFeatureHelper(inst, (FeatureSpecAttributeList) att, inputAS, instanceAnnotation);
    } else if (att instanceof FeatureSpecSimpleAttribute) {
      return extractFeatureHelper(inst, (FeatureSpecSimpleAttribute) att, inputAS, instanceAnnotation);
    } else if (att instanceof FeatureSpecNgram) {
      return extractFeatureHelper(inst, (FeatureSpecNgram) att, inputAS, instanceAnnotation);
    } else {
      throw new GateRuntimeException("Attempt to call extractFeature with type " + att.getClass());
    }
  }

  /**
   * Extract the instance feature for a simple attribute.
   *
   * This simply sets the value of the feature to the extracted value. Note that
   * for dense representations, we do not care at this point if the value is one
   * that has not been seen before: for training time, the vocabulary/alphabet is
   * calculated only after the whole training set has been created and for application
   * time, an unseen value will have to get treated specially by the model (e.g.
   * substituting a special OOV value, ignoring the feature or something else).
   * 
   * If the annotation types of the instance annotation and the annotation specified
   * for the attribute are the same, then the instance annotation is directly used, otherwise, if
   * there is a single overlapping annotation of the annType specified in the attribute, that one is
   * used. If there is no overlapping annotation, the feature gets set to a default empty/missing
   * value for the type (empty string, 0.0, fals, empty list etc.)
   * If there are several overlapping annotations, an exception is thrown, this should not
   * happen.
   *
   * If no featureName is specified, the value is a boolean indicating if an annotation is present.
   * 
   * NOTE: this does not support WITHIN (will get replaced by a separate PR)
   * This does not support and silently ignores one-of-k.
   * This does not support and silently ignores featureName4Value, instead that other feature should get included 
   * with a separate ATTRIBUTE declaration. 
   * This for now does not support and silently ignores missingvaluetreatment and 
   * will set the feature to type specific defaults.
   * This does support listsep and will always try to create a list of values if 
   * that declaration is present. 
   * If no listsep is provided and the value of an attribute is a list or array,
   * a list will be created as the value of the feature. It is the user's responsibility
   * to ensure that for this ALL instances have that feature actually set to a list
   * value or the backend is able to handle data where the value is sometimes a list
   * and sometimes not.
   * 
   * @param inst
   * @param att
   * @param inputASname
   * @param instanceAnnotation
   * @param doc
   */
  private static InstanceRepresentation extractFeatureHelper(
          InstanceRepresentation inst,
          FeatureSpecSimpleAttribute att,
          AnnotationSet inputAS,
          Annotation instanceAnnotation) {
    Document doc = inputAS.getDocument();
    /*Although the user needn't specify the annotation annType if it's the
     * same inputAS the instance, they may do so. It's intuitive that if they
     * do so, they mean to extract the featureName from the instance, not just
     * the first colocated same annType annotation. This matters in
     * disambiguation, where we have many colocated same annType annotations.
     * Fix it up front by wiping out annType if it's the same as the instance.
     */
    
    String annType = att.annType;
    String featureName = att.feature;
    Datatype dt = att.datatype;
    String listsep = att.listsep;
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
    
    // The source annotation is the actual annotation to use: it may be the instance annotation
    // if the type stored with the feature specification is empty or identical to the instance
    // annotation type, otherwise it is the annotation of the specified type covered by the 
    // instance annotation.
    Annotation sourceAnnotation = null;
    if (annType.isEmpty() || instanceAnnotation.getType().equals(annType)) {
      sourceAnnotation = instanceAnnotation;
      // annType = sourceAnnotation.getType();
      annType = "";
    } else {
      AnnotationSet overlappings = gate.Utils.getOverlappingAnnotations(inputAS, instanceAnnotation, annType);
      if (overlappings.size() > 1) {
        LOGGER.warn("More than one overlapping annotation of type " + annType + " for instance annotation at offset "
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
    inst = extractFeatureWorker(att, featureName(att,0), inst, sourceAnnotation, doc, annType, featureName, dt, listsep);
    return inst;
  }
  
  /**
   * Extract a attribute list feature for an instance.
   * 
   * This extracts all the features that correspond to a single attribute list feature 
   * specification for a single instance. 
   * 
   * Note that the annotation type used to identify instances specified as a PR parameter
   * can refer to the annotations described by the feature spec file (either because the type
   * is identical or because the feature spec file does not specify a type) or it is just used
   * to tell us where to take the actual annotations for this spec from. 
   * 
   * @param inst The instance representation to which the features should get added
   * @param al the feature specification
   * @param inputAS the annotation set that contains all relevant annotations
   * @param instanceAnnotation the actual instance annotation
   */
  private static InstanceRepresentation extractFeatureHelper(
          InstanceRepresentation inst,
          FeatureSpecAttributeList al,
          AnnotationSet inputAS,
          Annotation instanceAnnotation
  ) {

    Document doc = inputAS.getDocument();

    Datatype dt = al.datatype;        // feature data type
    String featureName = al.feature;  // feature name, if empty use the document text
    // type of a containing annotation, if specified, restrict everything to within the longest
    // annotation covering the instance annotation
    String withinType = al.withinType; 
    int from = al.from;  // list element from
    int to = al.to;      // list element to
    String listsep = al.listsep; 

    // If the type from the attribute specification is the same as the 
    // instance annotation type or if it is empty, we create the elements
    // from the instance annotations. Otherwise we use the specified type. 
    String annType = al.annType;
    // NOTE: annType4Getting is either:
    // * the one from the feature specification, if non-empty
    // * the one specified in the PR, if the feature specification type is empty
    // The anntype4Feature is always the one specified for the feature specification
    String annType4Feature = annType;  // the name to use in the data and model, possibly empty
    String annType4Getting = annType;  // the name to access the annotations in the document, never empty
    if (annType4Getting == null || annType4Getting.isEmpty()) {
      annType4Getting = instanceAnnotation.getType();  // make sure it is identical to the type of the inst ann
    }
    
    
    // 
    // The annotation -1 is one that ends before the beginning of annotation 0
    // Annotation -2 is one that ends before the beginning of annottion -1
    // Annotation +1 is one that starts after the end of annotation 0
    // Annotation +2 is one that starts after the end of annotation 1
    // Annotation 0 is the one which is either current instance (if we use 
    // instance annotations) or the longest one that overlaps with the current 
    // instance (similar to a Simple Attr).

    // First of all, get the annotation 0 and also get the set of the 
    // annotation types we are interested in.
    // If we have a "WITHIN" declaration, we immediately limit the 
    // set of interesting annotations to those within the containing annotation.
    
    // The sourceAnnotation is the annotation from which to actually take the the feature values
    // and its type is annType4Getting. This may be different from the instance annotation and 
    // instance annotation type if the instance annotation is just used to identify the span
    // where to take the sourceAnnotation from. 
    Annotation sourceAnnotation = null;
    // By default, the annotations for this list can come from within this range
    long rangeFrom = 0L;
    long rangeTo = doc.getContent().size();
    
    // the following are only set if we actually do have a within annotaiton and will remain at -1
    // otherwise. This is needed so we can quickly check if we need to set the START/STOP features
    // which are only set if there is a within annotation at all (to enable this for document start/end
    // a document annotation needs to be used as within annotation type).
    long withinFrom = -1L;
    long withinTo = -1L;
    AnnotationSet withinSet = inputAS;
    if (withinType != null) {
      // find out which within annotation covers our instance annotation
      // If there is none, the instance is not within a within annotation and we ignore it.
      // If there is more than one, we currently take an arbitrary one.
      AnnotationSet withins = gate.Utils.getCoveringAnnotations(inputAS, instanceAnnotation, withinType);
      if (withins.size() != 1) {
        LOGGER.warn("More than one covering WITHIN annotation for " + instanceAnnotation + " in document " + doc.getName());
      }
      if (withins.size() == 0) {
        LOGGER.warn("No covering WITHIN annotation for " + instanceAnnotation + " in document " + doc.getName());
        for (int i=from; i <= to; i++) {
          inst = inst.setFeature(featureName(al,i), al.missingValue());
        }
        return inst;
      }
      Annotation within = withins.iterator().next(); // get an arbitrary one
      rangeFrom = within.getStartNode().getOffset();
      rangeTo = within.getEndNode().getOffset();
      withinFrom = rangeFrom;
      withinTo = rangeTo;
      withinSet = gate.Utils.getContainedAnnotations(inputAS, within, annType4Getting);
    }
    if (annType.isEmpty() || instanceAnnotation.getType().equals(annType)) {
      // if the type specified for this attribute list is empty or equal to the type of the 
      // instance annotation, we directly use the instance annotation. In that case we also
      // use an empty feature name for the outputfile/model so that at application time we 
      // can match it with instance annotations which have a different type
      sourceAnnotation = instanceAnnotation;
      annType4Feature = "";
    } else {
      // the instance annotation is not the one we want to use, we need to find the actual source
      // annotation. 
      // If there is more than one overlapping source annotation, pick the last longest one when 
      // going through them in document order. 
      AnnotationSet overlappings = gate.Utils.getOverlappingAnnotations(inputAS, instanceAnnotation, annType4Getting);
      if (overlappings.size() > 1) {
        LOGGER.warn("More than one overlapping source annotation of type " + annType4Getting + " for instance annotation at offset "
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
        LOGGER.warn("No overlapping source annotation of type " + annType4Getting + " for instance annotation at offset "
                + gate.Utils.start(instanceAnnotation) + " in document " + doc.getName() + " instance ignored");
        for (int i=from; i <= to; i++) {
          inst = inst.setFeature(featureName(al,i), al.missingValue());
        }
        return inst;
      } else {
        // we have exactly one annotation, use that one
        sourceAnnotation = gate.Utils.getOnlyAnn(overlappings);
      }
    }
    
    // Now we have annotation [0]
    long start = sourceAnnotation.getStartNode().getOffset();
    long end = sourceAnnotation.getEndNode().getOffset();
    
    
    List<Annotation> annlistforward = withinSet.getContained(end, rangeTo).get(annType4Getting).inDocumentOrder();
    List<Annotation> annlistbackward = withinSet.getContained(rangeFrom, start).get(annType4Getting).inDocumentOrder();
    //System.err.println("rangeFrom=" + rangeFrom + ", rangeTo=" + rangeTo + ",START=" + start + ", END=" + end + ", forwardsize=" + annlistforward.size() + ", backwardsize=" + annlistbackward.size());
    // go through each of the members in the attribute list and get the annotation
    // then process each annotation just like a simple annotation, only that the name of 
    // featureName gets derived from this list attribute plus the location in the list.
    // TODO: this does not work if the annotations are overlapping!!!

    // TODO: this could also create n-grams of consecutive elements, based on an AttributeList
    // parameter N. Instead of creating the feature from a single source annotation, the 
    // features of N successive elements would get combined to form an ngram feature.
    // The identifier for such a feature would have to include the starting list element and N
    // e.g. "L"+i+"N"+n
    

    // First loop: go from index -1 to the smallest from index to the left
    int albsize = annlistbackward.size();
    for (int i = -1; i >= from; i--) {
      // -1 corresponds to element (size-1) in the list, 
      // -2 corresponds to element (size-2) in the list etc. 
      // in general we want element (size+i) if that is > 0
      if (albsize + i >= 0) {
        Annotation ann = annlistbackward.get(albsize + i);
        inst = extractFeatureWorker(al, featureName(al,i), inst, ann, doc, annType4Feature,
                featureName, dt, listsep);
      } else {
        // generate a missing value feature here
        inst = inst.setFeature(featureName(al,i), al.missingValue());
      }
    }
    // if we have index 0 in the range, process for that one
    if (from <= 0 && to >= 0) {
      inst = extractFeatureWorker(al, featureName(al,0), inst, sourceAnnotation, doc, annType4Feature,
              featureName, dt, listsep);
    }
    // do the ones to the right
    int alfsize = annlistforward.size();
    for (int i = 1; i <= to; i++) {
      // for i=1 we get element 0, in general we get element i-1
      // if i <= size
      if (i <= alfsize) {
        Annotation ann = annlistforward.get(i - 1);
        inst = extractFeatureWorker(al, featureName(al,i), inst, ann, doc, annType4Feature,
                featureName, dt, listsep);
      } else {
        // generate a missing value feature here
        inst = inst.setFeature(featureName(al,i), al.missingValue());
      }
    }
    return inst;
  } // extractFeature (AttributeList)
  
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
  private static InstanceRepresentation extractFeatureHelper(
          InstanceRepresentation inst,
          FeatureSpecNgram ng,
          AnnotationSet inputAS,
          Annotation instanceAnnotation
  ) {
    Document doc = inputAS.getDocument();
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
      // return an empty list, we alway need the feature to be there!
      inst = inst.setFeature(featureName(ng,0), new ArrayList<String>());
      return inst;
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


    // now create the ngrams inputAS follows: starting with the first element in strings, go
    // through all the elements up to the (size-n)ths and concatenate with the subsequent 
    // n stings using the pre-defined separator character.
    // Add all the ngrams into a list
    List<String> ngramlist = new ArrayList<>();
    // first check if our strings array is actually big enough so we can create at least one n-gram
    if (strings.size() < number) {
      // ok, no, for dense representations we return an empty list here
      inst = inst.setFeature(featureName(ng,0), ngramlist);
      return inst;
    }
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
            ngramlist.add(ngram);
    }
    inst = inst.setFeature(featureName(ng,0), ngramlist);
    //System.err.println("DEBUG: Vector after adding feature "+ng+" is now "+fv);
    return inst;
  } // extractFeature(NGram)


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
   * @param listsep
   * @param specialsymbol: if this is non-null, then an attribute is generated
   * for some special symbol (e.g. start/stop indicator). In this case, some other parameters 
   * are usually ignored.
   */
  private static InstanceRepresentation extractFeatureWorker(
          FeatureSpecAttribute attr,
          String fname,
          InstanceRepresentation inst,
          Annotation sourceAnnotation,
          Document doc,
          String annType,
          String featureName,
          Datatype dt,
          String listsep) {

    String name = attr.name;
    //String fname = featureNamePrefix(attr) + internalFeatureIndicator;
    if (featureName.isEmpty()) {
      // construct the featureName name and set to 1.0
      // however, only add the featureName if the featureName alphabet is allowed to grow.
      inst = inst.setFeature(fname, true);
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
        inst = inst.setHasMissing(true);
        inst = inst.setFeature(fname, attr.missingValue());
      } else {
        inst = inst.setHasMissing(false);
        // we almost always just set this to the original value 
        // so far the only exception is if we have a listsep, in which case we 
        // convert the string to a list of strings.
        // Otherwise, convert the value to either string, Double or Boolean,
        // depending on the type of the Attribute.
        if(attr.listsep != null && !attr.listsep.isEmpty()) {
          String[] els = valObj.toString().split(attr.listsep);
          inst = inst.setFeature(fname, Arrays.asList(els));
        } else {    
          Object finalValue = attr.toValue(valObj);
          //System.err.println("DEBUG: final value for "+valObj+" of type "+valObj.getClass()+" is "+finalValue);
          inst = inst.setFeature(fname, finalValue);
        }
      } // no missing value
    } // non-empty feature name
    return inst;
  } // extractFeatureWorker

  

  // *****************************************************************************
  // Extract the target stuff
  // *****************************************************************************
  public static InstanceRepresentation extractNumericTarget(InstanceRepresentation inst, String targetFeature, Annotation instanceAnnotation, AnnotationSet inputAS) {
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
    return inst.setTargetValue(value);
  }

  /**
   * Extract the class label for the given instance annotation. 
   * 
   * Only handles scalar targets so far. 
   * 
   * TODO: handle array of costs
   * 
   * @param inst
   * @param alphabet the label alphabet, must be of type LabelAlphabet
   * @param targetFeature
   * @param instanceAnnotation
   * @param doc
   */
  public static InstanceRepresentation extractClassTarget(InstanceRepresentation inst, String targetFeature, Annotation instanceAnnotation, AnnotationSet inputAS) {
    Document doc = inputAS.getDocument();
    Object obj = instanceAnnotation.getFeatures().get(targetFeature);
    // Brilliant, we have a missing target, WTF? Throw an exception
    if (obj == null) {
      throw new GateRuntimeException("No target value for feature " + targetFeature
              + " for instance at offset " + gate.Utils.start(instanceAnnotation) + " in document " + doc.getName());
    } else if (obj instanceof List) {
      throw new GateRuntimeException("Array of costs not yet implemented for dense instances");
    } else if (obj instanceof double[]) {
      throw new GateRuntimeException("Array of costs not yet implemented for dense instances");
    } else {
      // all other things are treated as a string 
      String value = obj.toString();
      inst = inst.setTargetValue(value);
    }
    return inst;
  }

  /**
   * Extract the class for an instance for sequence tagging.
   *
   * In the case of sequence tagging, we construct the class based on the instance's position
   * relative to the class annotation annType. If it occurs at the beginning of the class
   * annotation, it's a "beginning". In the middle or at the end, it's an "inside". Instances that
   * don't occur in the span of a class annotation are an "outside".
   *
   * @param alph the label alphabet to use, must be an instance of LabelAlphabet
   * @param instanceAnnotation, the instance annotation, e.g. "Token".
   */
  public static InstanceRepresentation extractClassForSeqTagging(InstanceRepresentation inst, AnnotationSet classAS, Annotation instanceAnnotation, SeqEncoder seqEncoder) {
    String target = "";
    Document doc = classAS.getDocument();
    AnnotationSet overlappingClassAnns = Utils.getOverlappingAnnotations(classAS, instanceAnnotation);
    // NOTE: previously we only allowed at most one class annotation, but now we are as flexible
    // as possible here: any number of class annotations of any number of types can overlap.
    // The class label for each instance is generated from the complete list of what overlaps,
    // e.g. beginning of T1, beginning of another T1, continuation of T2 and end of T3 
    // The class labels for such combinations only get generated if an overlap actually occurs,
    // so if we only ever see nicely separated annotations, then we will never see the combined labels.
    // Labels are dynamically generated as a string of pipe-separated type names, with the flag
    // (beginning=B, inside=I) appended, or class "O" if outside of all types. 
    // The ordering of types in the class label name must be consistent: TODO!!
    // NOTE: this should be one of several possible ways to do it, implemented in several
    // methods/classes and choosable through e.g. the "algorithmParameter" settings.
    // Then we could use approaches like BIO, BMEWO, BMEWO+ (see
    // https://lingpipe-blog.com/2009/10/14/coding-chunkers-as-taggers-io-bio-bmewo-and-bmewo/)
    // or the ones listed in http://cs229.stanford.edu/proj2005/KrishnanGanapathy-NamedEntityRecognition.pdf
    // Whenever we choose a strategy here, the strategy needs to get stored in the 
    // model info file and re-used at application time!
    // NOTE: need to see if the label alphabet growing setting is handled correctly!
    
    // if there is at least one overlapping class annotation
    if (overlappingClassAnns.size() > 0) {
      // convert the set of annotation types to a list of type|code names
      // this should eventually be parametrizable so we can choose one of several methods
      // ideally we implement this as a method of one of an instance of several Seq2Class 
      // subclasses. If it is an instance we could maybe also implement methods where we
      // need to remember something about the last instance for which we did it!
      target = seqEncoder.seqAnns2ClassLabel(overlappingClassAnns, instanceAnnotation);
    } else {
      //No overlapping mentions so it's an outside
      target = seqEncoder.CODE_OUTSIDE;
    }
    // if debugging is enabled, we put the 
    // the target class on the instance annotation
    if (DEBUG_SEQUENCE_CLASS) {
      instanceAnnotation.getFeatures().put("LF_sequenceClass", target);
    }
    inst = inst.setTargetValue(target);
    return inst;
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
    // these features are created by the LF and will not be present in the list of specifications.
    // We create a new specification on the fly for those.
    if(mlFeatureName.endsWith(START_SYMBOL) || mlFeatureName.endsWith(STOP_SYMBOL)) {
      // TODO!!!
      // return a newly constructed dummy feature spec attribute
      // NOTE: for now we handle the case that this method does not find anything and returns
      // null if we get a START/STOP indicator feature directly where this method is called!
    }
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

}
