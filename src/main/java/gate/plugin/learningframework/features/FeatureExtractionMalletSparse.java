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
 * NOTE: currently this is mostly static methods, we should probably move to a usage pattern where 
 * we use an instance. Then, the instance can get configured, debugging enabled etc. 
 * 
 * @author Johann Petrak
 */
public class FeatureExtractionMalletSparse extends FeatureExtractionBase {

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
  // NOTE: the various separater characters are all unicode characters taken
  // from the "Box Drawing" Unicode block as these are extremely unlikely
  // to be used either as part of annotation type or feature names or to 
  // occur inside ngrams of the text. No attempt is made to escape or otherwise
  // handle these characters IF they indeed occur in these places.
  

  private static final Logger LOGGER = Logger.getLogger(FeatureExtractionMalletSparse.class.getName());

  /**
   * TODO
   * @param inst TODO
   * @param att TODO
   * @param inputAS TODO
   * @param instanceAnnotation TODO
   */
  public static void extractFeature(
          Instance inst,
          FeatureSpecAttribute att,
          AnnotationSet inputAS,
          Annotation instanceAnnotation) {
    if (att instanceof FeatureSpecAttributeList) {
      extractFeatureHelper(inst, (FeatureSpecAttributeList) att, inputAS, instanceAnnotation);
    } else if (att instanceof FeatureSpecSimpleAttribute) {
      extractFeatureHelper(inst, (FeatureSpecSimpleAttribute) att, inputAS, instanceAnnotation);
    } else if (att instanceof FeatureSpecNgram) {
      extractFeatureHelper(inst, (FeatureSpecNgram) att, inputAS, instanceAnnotation);
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
   * NOTE: if a within type is specified for this attribute, then no features are created 
   * if the instance annotation or the source annotation is not covered by an within annotation.
   * 
   *
   * @param inst TODO
   * @param att TODO
   * @param inputASname TODO
   * @param instanceAnnotation TODO
   * @param doc TODO
   */
  private static void extractFeatureHelper(
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
     * Fix it up front by wiping out annType if it's the same as the instance.
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
    String withinType = att.withinType;
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


    long rangeFrom = 0;
    long rangeTo = doc.getContent().size();
    long withinFrom = -1;
    long withinTo = -1;
    AnnotationSet withinSet = inputAS;
    if (withinType != null) {
      AnnotationSet withins = gate.Utils.getCoveringAnnotations(inputAS, instanceAnnotation, withinType);
      if (withins.size() != 1) {
        LOGGER.warn("More than one covering WITHIN annotation for " + instanceAnnotation + " in document " + doc.getName());
      }
      if (withins.size() == 0) {
        LOGGER.warn("No covering WITHIN annotation for " + instanceAnnotation + " in document " + doc.getName());
        return; // Skip this instance!
      }
      Annotation within = withins.iterator().next(); // get an arbitrary one
      rangeFrom = within.getStartNode().getOffset();
      rangeTo = within.getEndNode().getOffset();
      withinFrom = rangeFrom;
      withinTo = rangeTo;
      withinSet = gate.Utils.getContainedAnnotations(inputAS, within);
    }
    
    // The source annotation is the actual annotation to use: it may be the instance annotation
    // if the type stored with the feature specification is empty or identical to the instance
    // annotation type, otherwise it is the annotation of the specified type overlapping with the 
    // instance annotation.
    Annotation sourceAnnotation = null;
    if (annType.isEmpty() || instanceAnnotation.getType().equals(annType)) {
      sourceAnnotation = instanceAnnotation;
      // annType = sourceAnnotation.getType();
      
      // TODO: this was originally included here, but not sure why?
      // removing for now ...
      //annType = "";
    } else {
      AnnotationSet overlappings = gate.Utils.getOverlappingAnnotations(withinSet, instanceAnnotation, annType);
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
    // NOTE: there should be no way of how a featureName we encounter now is already in the featureName
    // vector, so we do not even check, we simply add the featureName.
    // How we add the featureName depends on the datatype, on the codeas setting if it is nominal,
    // and also on how we treat missing values.
    extractFeatureWorker(att.name, "A", inst, sourceAnnotation, doc, annType, featureName, featureName4Value, alphabet, dt, mvt, codeas, listsep,null);
    // now if the source annotation was not null (missing) and the source annotation started with
    // and/or ended with the within annotation, we also add special START/STOP features 
    if(sourceAnnotation != null && withinFrom == gate.Utils.start(sourceAnnotation)) {
      extractFeatureWorker(att.name, "A", inst, sourceAnnotation, doc, annType, featureName, featureName4Value, alphabet, Datatype.nominal, MissingValueTreatment.zero_value, CodeAs.one_of_k, listsep,START_SYMBOL);
    }
    if(sourceAnnotation != null && withinTo == gate.Utils.end(sourceAnnotation)) {
      extractFeatureWorker(att.name, "A", inst, sourceAnnotation, doc, annType, featureName, featureName4Value, alphabet, Datatype.nominal, MissingValueTreatment.zero_value, CodeAs.one_of_k, listsep,STOP_SYMBOL);
    }
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
  private static void extractFeatureHelper(
          Instance inst,
          FeatureSpecAttributeList al,
          AnnotationSet inputAS,
          Annotation instanceAnnotation
  ) {


    Document doc = inputAS.getDocument();
    AugmentableFeatureVector fv = (AugmentableFeatureVector) inst.getData();

    Datatype dt = al.datatype;        // feature data type
    String featureName = al.feature;  // feature name, if empty use the document text
    // type of a containing annotation, if specified, restrict everything to within the longest
    // annotation covering the instance annotation
    String withinType = al.withinType; 
    int from = al.from;  // list element from
    int to = al.to;      // list element to
    Alphabet alphabet = al.alphabet;
    MissingValueTreatment mvt = al.missingValueTreatment;
    CodeAs codeas = al.codeas;
    String listsep = al.listsep; 
    String featureName4Value = al.featureName4Value;

    // If the type from the attribute specification is the same as the 
    // instance annotation type or if it is empty, we create the elements
    // from the instance annotations. Otherwise we use the specified type. 
    String annType = al.annType;
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
        return; // Skip this instance!
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
      // instance annotation, we directly use the instance annotation. 
      sourceAnnotation = instanceAnnotation;
      // annType4Feature = "";
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
        return;
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
        extractFeatureWorker(al.name, "L" + i, inst, ann, doc, annType4Feature,
                featureName, featureName4Value, alphabet, dt, mvt, codeas, listsep,null);
        // if we have the leftmost annotation and the offset of the annotation is equals to
        // the start of the within annotation or the document start, then also set the start
        // feature for this instance
        if(i==from && gate.Utils.start(ann)==withinFrom) {
          extractFeatureWorker(al.name,"L"+i,inst,ann,doc,annType4Feature,
                  null, null, alphabet, Datatype.nominal,
                  MissingValueTreatment.zero_value, CodeAs.one_of_k, "",START_SYMBOL);
        }
      } else {
        break;
      }
    }
    // if we have index 0 in the range, process for that one
    if (from <= 0 && to >= 0) {
      extractFeatureWorker(al.name, "L" + 0, inst, sourceAnnotation, doc, annType4Feature,
              featureName, featureName4Value, alphabet, dt, mvt, codeas, listsep,null);
      if(gate.Utils.start(sourceAnnotation)==withinFrom) {
          extractFeatureWorker(al.name,"L0",inst,sourceAnnotation,doc,annType4Feature,
                  null, null, alphabet, Datatype.nominal, 
                  MissingValueTreatment.zero_value, CodeAs.one_of_k, "",START_SYMBOL);
        }
      if(gate.Utils.end(sourceAnnotation)==withinTo) {
          extractFeatureWorker(al.name,"L0",inst,sourceAnnotation,doc,annType4Feature,
                  null, null, alphabet, Datatype.nominal, 
                  MissingValueTreatment.zero_value, CodeAs.one_of_k, "",STOP_SYMBOL);
        }
    }
    // do the ones to the right
    int alfsize = annlistforward.size();
    for (int i = 1; i <= to; i++) {
      // for i=1 we get element 0, in general we get element i-1
      // if i <= size
      if (i <= alfsize) {
        Annotation ann = annlistforward.get(i - 1);
        extractFeatureWorker(al.name, "L" + i, inst, ann, doc, annType4Feature,
                featureName, featureName4Value, alphabet, dt, mvt, codeas, listsep,null);
        if(i==to && gate.Utils.end(ann)==withinTo) {
          extractFeatureWorker(al.name,"L"+i,inst,ann,doc,annType4Feature,
                  null, null, alphabet, Datatype.nominal, 
                  MissingValueTreatment.zero_value, CodeAs.one_of_k, "",STOP_SYMBOL);
        }
      } else {
        break;
      }
    }
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
  private static void extractFeatureHelper(
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
    
    // TODO: this was in the code but was unusade, make sure we do not miss anything!
    // (Note: same line was also copy-pasted into the dense feature extractor)
    // String[] gram = new String[number];
    List<Annotation> al = Utils.getContainedAnnotations(inputAS, instanceAnnotation, annType).inDocumentOrder();
    // If we have less annotations than our n for n-gram, there is certainly nothing to do, 
    // leave the featureName vector untouched.
    if (al.size() < number) {
      return;
    }
    // this will hold the actual token strings to use for creating the n-grams
    List<String> strings = new ArrayList<>();
    // this will hold the score to use for each string we extract, but only of the
    // featureName4Value was specified and exists.
    List<Double> scores = new ArrayList<>();

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
   * @param doc TODO
   * @param annType the annotation type as defined in the attribute specification. This can be empty
   * if the original instance annotation is used.
   * @param featureName the feature name as defined in the attribute specification.
   * @param alphabet TODO
   * @param dt TODO
   * @param mvt TODO
   * @param codeas TODO
   * @param listsep TODO
   * @param specialsymbol: if this is non-null, then an attribute is generated
   * for some special symbol (e.g. start/stop indicator). In this case, some other parameters 
   * are usually ignored.
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
          String listsep,
          String specialSymbol) {

    AugmentableFeatureVector fv = (AugmentableFeatureVector) inst.getData();
    // create the default feature name prefix: this is either "A"+NAMESEP+type+NAMESEP+featureName
    // or just the name give in the attribute
    String internalFeatureNamePrefix;
    if (name.isEmpty()) {
      internalFeatureNamePrefix = annType + TYPESEP + featureName + NAMESEP + internalFeatureIndicator;
    } else {
      internalFeatureNamePrefix = name + NAMESEP + internalFeatureIndicator;
    }
    // If we have a special symbol, then the boolean special symbol indicator attribute is generated
    if (specialSymbol != null && !specialSymbol.isEmpty()) {
      String fname = internalFeatureNamePrefix;
      setInFeatureVector(fv, internalFeatureNamePrefix + VALSEP + specialSymbol, 1.0);    
    // inputAS a boolean. No matter what the datatype is, this is always indicated by setting the
    // featureName to 1.0 (while for all instances, where the annotation is missing, the value will
    // implicitly be set to 0.0). 
    // System.err.println("DEBUG: for fname="+featureName+",dt="+dt);
    } else if (featureName == null || featureName.isEmpty()) {
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
                // If the value we get for the score is not present or null, we suppress creating the attribute
                Object value = sourceAnnotation.getFeatures().get(featureName4Value);
                if(value != null) {
                  double score = gate.plugin.learningframework.LFUtils.anyToDoubleOrElse(value, 1.0);
                  setInFeatureVector(fv, internalFeatureNamePrefix + VALSEP + val, score);
                }
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
              LOGGER.warn("Cannot parse String " + valObj + " as a number, using 0.0 for annotation of type "
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
              LOGGER.warn("Cannot parse String " + valObj + " as a boolean, using 0.0 for annotation of type "
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

  // *****************************************************************************
  // Extract the target stuff
  // *****************************************************************************

  /**
   * TODO
   * @param inst TODO
   * @param targetFeature TODO
   * @param instanceAnnotation TODO
   * @param inputAS TODO
   */
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
   * @param inst the instance
   * @param alphabet the label alphabet, must be of type LabelAlphabet
   * @param targetFeature the target feature
   * @param instanceAnnotation the instance annotation
   * @param inputAS TODO
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
      @SuppressWarnings("unchecked")
      NominalTargetWithCosts lwc = new NominalTargetWithCosts((List<Double>) obj);
      inst.setTarget(labelalphabet.lookupLabel(lwc));
    } else if (obj instanceof double[]) {
      @SuppressWarnings("unchecked")
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
   * @param inst TODO
   * @param alph the label alphabet to use, must be an instance of LabelAlphabet
   * @param classAS TODO
   * @param instanceAnnotation, the instance annotation, e.g. "Token".
   * @param seqEncoder TODO
   */
  public static void extractClassForSeqTagging(Instance inst, Alphabet alph, AnnotationSet classAS, Annotation instanceAnnotation, SeqEncoder seqEncoder) {
    String target = "";
    Document doc = classAS.getDocument();
    if (!(alph instanceof LabelAlphabet)) {
      throw new GateRuntimeException("LF extractClassForSeqTagging: the alphabet must be of type LabelAlphabet"
              + " for instance annotation at offset " + gate.Utils.start(instanceAnnotation)
              + " in document " + doc.getName());
    }
    LabelAlphabet labelalph = (LabelAlphabet) alph;
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
    // we now have the target label as a string, now set the target of the instance to 
    // to the actual label
    // NOTE: the target alphabet for such an instance MUST be a LabelAlphabet!
    inst.setTarget(labelalph.lookupLabel(target));
  }

  /**
   * TODO
   * @param inst TODO
   * @return TODO
   */
  public static boolean ignoreInstanceWithMV(Instance inst) {
    Object val = inst.getProperty(PROP_IGNORE_HAS_MV);
    if (val == null) {
      return false;
    }
    return ((Boolean) inst.getProperty(PROP_IGNORE_HAS_MV));
  }

  /**
   * TODO
   * @param inst TODO 
   * @return TODO
   */
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
   * @param inst TODO
   * @param instanceAnnotation TODO
   * @param doc TODO
   */
  public static void extractName(Instance inst, Annotation instanceAnnotation, Document doc) {
    String value = doc.getName() + ":" + gate.Utils.start(instanceAnnotation) + ":"
            + gate.Utils.end(instanceAnnotation);
    inst.setName(value);
  }

  /**
   * Return the attribute name part of a ML feature.
   *
   * @param mlFeature the feature name
   * @return the attribute name
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
   * @param attributes the list of feature attributes
   * @param mlFeatureName TODO
   * @param instanceType TODO
   * @return the attribute
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
