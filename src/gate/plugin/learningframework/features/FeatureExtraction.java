package gate.plugin.learningframework.features;

import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Utils;
import gate.util.GateRuntimeException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;


/**
 * Code for extracting features from a document based on a FeatureInfo.
 * For now this is intrinsically tied to Mallet: our own internal way of representing
 * features, instances, alphabets etc. uses in a large part the Mallet approach and 
 * the result of extracting features from the document is a Mallet instance.
 * Should we ever want to support other representations, this would need to get refactored
 * to an Interface or base class, with different implementations for different internal 
 * representations. 
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
   * Separates the name from any additional information.
   * Additional information is kind of attribute (Ngram, attributelist etc), 
   * things like the element number if it is an attributelist, or the value
   * for nominal one-of-k coded features. 
   */
  private static final String NAMESEP = "╬";
  
  /**
   * Separates the type name inside the name from the feature name. 
   * The scheme is typename¦featurename where typename can be empty.
   */
  private static final String TYPESEP = "┆";
  
  /**
   * Separates the remainder of the feature name from the part that indicates
   * the value for nominal one-of-k coded features.
   */
  private static final String VALSEP = "═";
  
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
    if(att instanceof FeatureSpecAttributeList) extractFeature(inst,(FeatureSpecAttributeList)att,inputAS,instanceAnnotation);
    else if(att instanceof FeatureSpecSimpleAttribute) extractFeature(inst,(FeatureSpecSimpleAttribute)att,inputAS,instanceAnnotation);
    else if(att instanceof FeatureSpecNgram) extractFeature(inst,(FeatureSpecNgram)att,inputAS,instanceAnnotation);
    else {
      throw new GateRuntimeException("Attempt to call extractFeature with type "+att.getClass());
    }
  }
  
  
  /**
   * Extract the instance features for a simple attribute.
   * 
   * This adds the internal features to the inst object and also updates any 
 alphabets, if they allow growth.
 If the annotation types of the instance annotation and the annotation specified for the attribute
 are the same, then the instance annotation is directly used, otherwise, if there is a single
 overlapping annotation of the annType specified in the attribute, that one is used.
 If there is no overlapping annotation, nothing is extracted for that instance and implicitly,
 all features are set to 0.0 (TODO: this should probably get treated inputAS if all features were
 missing so that for each featureName, the proper missing value treatment can be applied!)
 If there are several overlapping annotations, an exception is thrown, this should not happen.
 
 If no featureName is specified, we give an indicator of the presence, artificial featureName name 
 for binary featureName. 
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
      if(overlappings.size() > 1) {
        logger.warn("More than one overlapping annotation of type "+annType+" for instance annotation at offset "+
                gate.Utils.start(instanceAnnotation)+" in document "+doc.getName());
        // find the last longest (try to make this deterministic, there is 
        // still a small chance of non-determinism if there are more than one
        // overlapping annotations of the same length in the last position 
        // where a longest annotation occurs.
        int maxSize = 0;
        for(Annotation ann : overlappings.inDocumentOrder()) {
          if(gate.Utils.length(ann)>maxSize) {
            maxSize = gate.Utils.length(ann);
            sourceAnnotation = ann;
          }
        }
      } else if(overlappings.size() == 0) {
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
    extractFeatureWorker(att.name,"A",inst,sourceAnnotation,doc,annType,featureName,alphabet,dt,mvt,codeas);
  }
    
  
  /**
   * Do the actual hard work of extracting a feature and adding it to the Mallet
   * feature vector. This is used to do the actual extraction for simple 
   * attributes and attribute lists.
   * 
   * @param name the name of the feature as defined in the attribute specification or an empty string
   * @param internalFeatureIndicator the part of the feature name that indicates the type of attribute
   * specification, e.g. "A" for attribute or something like "L-3" for attribute list
   * @param inst the mallet instance
   * @param sourceAnnotation the annotation from which to extract the feature value
   * @param doc 
   * @param annType the annotation type as defined in the attribute specification. This can be 
   * empty if the original instance annotation is used.
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
          Alphabet alphabet,
          Datatype dt,
          MissingValueTreatment mvt,
          CodeAs codeas)  {
    
    AugmentableFeatureVector fv = (AugmentableFeatureVector)inst.getData();
    // create the default feature name prefix: this is either "A"+NAMESEP+type+NAMESEP+featureName
    // or just the name give in the attribute
    String internalFeatureNamePrefix;
    if(name.isEmpty()) {
      internalFeatureNamePrefix = annType+TYPESEP+featureName+NAMESEP+internalFeatureIndicator;
    } else {
      internalFeatureNamePrefix = name+NAMESEP+internalFeatureIndicator;
    }
    // if the featureName name is empty, then all we want is indicate the presence of the annotation
    // inputAS a boolean. No matter what the datatype is, this is always indicated by setting the
    // featureName to 1.0 (while for all instances, where the annotation is missing, the value will
    // implicitly be set to 0.0). 
    // System.err.println("DEBUG: for fname="+featureName+",dt="+dt);
    if(featureName==null||featureName.isEmpty()) {
      // construct the featureName name and set to 1.0
      // however, only add the featureName if the featureName alphabet is allowed to grow.
      String fname = internalFeatureNamePrefix;
      addToFeatureVector(fv, fname, 1.0);
    } else {    
      // First get the value inputAS an Object, if there is no value, we have an Object that is null
      // If the sourceAnnotation is null, we already did not find the source at all,
      // so the value is always missing.
      Object valObj = null;
      if(sourceAnnotation != null) {
        valObj = sourceAnnotation.getFeatures().get(featureName);
      } 
      // no matter what the datatype is, a null is always a missing value, so we set the 
      // property that indicates the existence of a missing valuein the instance right here
      if(valObj == null) {
        inst.setProperty(PROP_HAVE_MV, true);
      } else {
        inst.setProperty(PROP_HAVE_MV,false);
      }
      // initialize the PROP_IGNORE_HAS_MV property to be false, if we have a MV which
      // causes the instance to get ignored we set it to true below
      inst.setProperty(PROP_IGNORE_HAS_MV,false);
      // if the datatype is nominal, we have to first check what the codeas setting is.
      if(dt==Datatype.nominal) {
        if(codeas==CodeAs.one_of_k) {
          if(valObj != null) {
            // it is not a missing value
            String val = valObj.toString();
            // TODO: do we have to escape the featureName name in some way here?
            // TODO: if we want to store a count rather than 1.0, we would need to make use
            // of a pre-calculated feature vector here which should contain the count for 
            // this feature over all instances in the document (or whatever the counting strategy is)
            // For this we would have to modify this and the calling method to also take 
            // an optional feature vector and use it if it is non-null
            addToFeatureVector(fv, internalFeatureNamePrefix+VALSEP+val, 1.0);
          } else {
            // we have a missing value, check the missing value treatment for what to do now
            switch(mvt) {
              case ignore_instance: 
                inst.setProperty(PROP_IGNORE_HAS_MV,true);
                break;
              case keep:  // this represents the MV by not setting any indicator featureName, so nothing to do
                //System.out.println("DEBUG: oneofk, mv, keep");
                break;
              case zero_value: // we treat this identical to keep: no feature set
                break;
              case special_value: // we use the predefined special value
                addToFeatureVector(fv,internalFeatureNamePrefix+VALSEP+MVVALUE,1.0);
                break; 
              default:
                throw new NotImplementedException("MV-Handling");
            }                      
          }
        } else if(codeas==CodeAs.number) {
          if(valObj!=null) {
            // For this representation, we need to maintain a dictionary that maps values to 
            // numbers! This is also done using an Alphabet, and if a value is not in the alphabet,
            // then if the alphabet is allowed to grow, we simply get the new value. But if the 
            // alphabet is not allowed to grow (at application time), finding a new value is an 
            // error and causes an exception
            // The alphabet for an number-coded attribute is stored in the SimpleAttribute object.
            // before we handle the value, we need to convert it to a string
            String val = valObj.toString();
            if(alphabet.contains(val)) {
              // add the featureName, using the value we have stored for it, but only if the featureName
              // itself can be added
              addToFeatureVector(fv, internalFeatureNamePrefix, alphabet.lookupIndex(val));
            } else {
              // we have not seen this value: if the alphabet is allowed to grow add it and
              // then try to add the featureName, otherwise, do nothing
              if(!alphabet.growthStopped()) {
                // the lookupIndex method automatically adds the value if it is not there yet
                addToFeatureVector(fv, internalFeatureNamePrefix, alphabet.lookupIndex(val));
              } else {
                //System.out.println("DEBUG: number, growStopped");
              }
            }
          } else {
            // we have a nominal value that should get coded numeric but it is a missing value
            switch(mvt) {
              case ignore_instance: 
                inst.setProperty(PROP_IGNORE_HAS_MV, true);
                //System.out.println("DEBUG: other, mv, setProp");
                break;
              case keep:  
                addToFeatureVector(fv,internalFeatureNamePrefix, Double.NaN );
                break;
              case zero_value: // use the "special_value" 
                addToFeatureVector(fv,internalFeatureNamePrefix, 0.0 );
                String val = MVVALUE;
                if(alphabet.contains(val)) {
                  addToFeatureVector(fv, internalFeatureNamePrefix, alphabet.lookupIndex(MVVALUE));
                } else {
                  if(!alphabet.growthStopped()) {
                    addToFeatureVector(fv, internalFeatureNamePrefix, alphabet.lookupIndex(MVVALUE));
                  } else {
                    //System.out.println("DEBUG: number, growStopped");
                  }
                }
                break;
              case special_value: // we use the special value -1.0 which should get handled by Mallet somehow
                addToFeatureVector(fv,internalFeatureNamePrefix,-1.0);
                break; 
              default:
                throw new NotImplementedException("MV-Handling");
            }                                  
          } // if valObj != null .. else
        } else {  // codeas setting for the nominal attribute
          throw new NotImplementedException("CodeAs method not implemented");
        }
      } else if(dt == Datatype.numeric) {
        if(valObj != null) {
          // just add the value, if possible and if the object can be interpreted inputAS a number
          double val = 0.0;
          if(valObj instanceof Number) {
            val = ((Number)valObj).doubleValue();
          } else if(valObj instanceof Boolean) {
            if((Boolean)valObj)  { val = 1.0; } else {val = 0.0;}
          } else {
            // try to convert the string to a number. If that fails, just use 0.0 but log a warning
            try {
              val = Double.parseDouble(valObj.toString());
            } catch (Exception ex) {
              val = 0.0;
              logger.warn("Cannot parse String "+valObj+" as a number, using 0.0 for annotation of type "+
                      sourceAnnotation.getType()+  // take it from the annotation, annType can be empty!
                      " at offset "+gate.Utils.start(sourceAnnotation)+" in document "+doc.getName());
            }
          }        
          addToFeatureVector(fv,internalFeatureNamePrefix,val);          
          //System.err.println("DEBUG: for fname="+featureName+",dt="+dt+", valObj="+valObj+", fv="+fv.numLocations());
        } else {
            // we have a numeric missing value!
            switch(mvt) {
              case ignore_instance: 
                inst.setProperty(PROP_IGNORE_HAS_MV, true);
                //System.out.println("DEBUG: numeric, mv, setProp");
                break;
              case keep:  // for this kind of codeas, we use the value NaN
                addToFeatureVector(fv,internalFeatureNamePrefix, Double.NaN );
                break;
              case zero_value: // use the first value, does not make much sense really, but ...
                // TODO: document that this combination should be avoided, probably
                addToFeatureVector(fv,internalFeatureNamePrefix, 0.0 );
                break;
              case special_value: // we use the special value -1.0 which should get handled by Mallet somehow
                addToFeatureVector(fv,internalFeatureNamePrefix,-1.0);
                break; 
              default:
                throw new NotImplementedException("MV-Handling");
            }                                  
          //System.err.println("DEBUG: for fname="+featureName+",dt="+dt+", valObj="+valObj+", fv="+fv.numLocations());
        }
      } else if(dt == Datatype.bool) {
        if(valObj != null) {
          double val = 0.0;
          if(valObj instanceof Boolean) {
            if((Boolean)valObj) val = 1.0;
          } else if(valObj instanceof Number) {
            if(((Number)valObj).doubleValue() != 0) val = 1.0;
          } else {
            try {
              boolean tmp = Boolean.parseBoolean(valObj.toString());
              if(tmp) val = 1.0;
            } catch (Exception ex) {
              // value is already 0.0
              logger.warn("Cannot parse String "+valObj+" as a boolean, using 0.0 for annotation of type "+
                      sourceAnnotation.getType()+  // take it from the annotation, annType can be empty!
                      " at offset "+gate.Utils.start(sourceAnnotation)+" in document "+doc.getName());              
            }
          }
          addToFeatureVector(fv,internalFeatureNamePrefix,val);
        } else {
            // we have a missing boolean value
            switch(mvt) {
              case ignore_instance: 
                //System.out.println("DEBUG: boolean, mv, setProp");
                inst.setProperty(PROP_IGNORE_HAS_MV, true);
                break;
              case keep:  // for this kind of codeas, we use the value NaN
                addToFeatureVector(fv,internalFeatureNamePrefix, Double.NaN );
                break;
              case zero_value: // use the first value, does not make much sense really, but ...
                // TODO: document that this combination should be avoided, probably
                addToFeatureVector(fv,internalFeatureNamePrefix, 0.0 );
                break;
              case special_value: // we use the special value -1.0 which should get handled by Mallet somehow
                addToFeatureVector(fv,internalFeatureNamePrefix,0.5);
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
    // TODO: this we rely on the ngram only having allowed field values, e.g. annType
    // has to be non-null and non-empty and number has to be > 0.
    // If featureName is null, then for ngrams, the string comes from the covered document
      String[] gram = new String[number];
      List<Annotation> al = Utils.getContainedAnnotations(inputAS, instanceAnnotation, annType).inDocumentOrder();
      // If we have less annotations than our n for n-gram, there is certainly nothing to do, 
      // leave the featureName vector untouched.
      if (al.size() < number) return;
        // this will hold the actual token strings to use for creating the n-grams
        List<String> strings = new ArrayList<String>();
        for(Annotation ann : al) {
          // for ngrams we either have a featureName name 
          if(featureName != null) {
            // NOTE: if the featureName is not a string, we convert it to string
            Object obj = ann.getFeatures().get(featureName);
            // if there is no value at all, then the annotation is ignored
            if(obj!=null) {
              String tmp =obj.toString().trim();
              // if the resulting string is empty, it is also ignored 
              if(!tmp.isEmpty()) {
                strings.add(tmp);
              }
            }
          } else {
            // if the featureName is null, we get the string from the cleaned document text
            String tmp = gate.Utils.cleanStringFor(doc, ann).trim();
            if(!tmp.isEmpty()) {
              strings.add(tmp);
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
        if(strings.size()<number) return;
        
        // now create the ngrams inputAS follows: starting with the first element in strings, go
        // through all the elements up to the (size-n)ths and concatenate with the subsequent 
        // n stings using the pre-defined separator character.
        
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < (strings.size()-number+1); i++ ) {
          sb.setLength(0);
          for(int j = 0; j < number; j++) {
            if(j!=0) sb.append(NGRAMSEP);
            sb.append(strings.get(i+j));
          }
          String ngram = sb.toString();
          // we have got our ngram now, count it, but only add if we are allowed to!
          String prefix;
          if(ng.name.isEmpty()) {
            prefix = annType + TYPESEP + featureName;
          } else {
            prefix = ng.name;
          }
          prefix = prefix + NAMESEP + "N" + number;
          // NOTE: if the key is already in the feature vector, then 
          // this will increment the current count by one!
          // This means that the number of times the ngram is contained within the 
          // instance annotation is counted!
          // TODO: in order to use the number of times the ngram occurs in the sequence or 
          // in the document instead, we would need to e.g. this:
          // for each Ng attribute, we would first need to collect a feature vector over all
          // instances in the document (or sequence), then when each individual instance is processed, look
          // up the value we got there and use it to set (rather than add) it to the per-instance
          // feature vector here.
          // So this method would get the "global feature vector"  as an additional parameter
          // which would be used that way if it is non-null
          addToFeatureVector(fv, prefix+VALSEP+ngram, 1.0);
        }
  } // extractFeature(NGram)
  
  
  private static void extractFeature(
          Instance inst, 
          FeatureSpecAttributeList al, 
          AnnotationSet inputAS, 
          Annotation instanceAnnotation
          ) {

    // TODO: what if the annotation type from the attribute specification is 
    // the same as for the instance annotation or if it is empty.
    Document doc = inputAS.getDocument();
    AugmentableFeatureVector fv = (AugmentableFeatureVector) inst.getData();

    Datatype dt = al.datatype;
    String annType = al.annType;
    String featureName = al.feature;
    int from = al.from;
    int to = al.to;
    Alphabet alphabet = al.alphabet;
    MissingValueTreatment mvt = al.missingValueTreatment;
    CodeAs codeas = al.codeas;
    if(annType.isEmpty() || instanceAnnotation.getType().equals(annType)) {
      annType = instanceAnnotation.getType();
      annType = "";
    }
    long centre = instanceAnnotation.getStartNode().getOffset();
    List<Annotation> annlistforward = inputAS.get(annType, centre, doc.getContent().size()).inDocumentOrder();
    List<Annotation> annlistbackward = inputAS.get(annType, 0L, centre).inDocumentOrder();
    // go through each of the members in the attribute list and get the annotation
    // then process each annotation just like a simple annotation, only that the name of 
    // featureName gets derived from this list attribute plus the location in the list.
    for (int i = from; i <= to; i++) {
      Annotation ann = null;
      if (i < 0) {
        if (-i <= annlistbackward.size()) {
          ann = annlistbackward.get(annlistbackward.size() + i);
        }
      } else if (i < annlistforward.size()) {
          ann = annlistforward.get(i);
          // make compiler happy for now
          //textToReturn = textToReturn + separator + annType + ":" + featureName + ":r" + i + ":" + extractFeature(annType, featureName, datatype, inputASname, ann, doc);
      }
      // If the list annotation exists at all, process it just like a 
      // simple attribute. 
      // NOTE: if the list annotation does not exist we do not treat the corresponding
      // feature values as missing for now!!
      if(ann != null) {        
        extractFeatureWorker(al.name,"L"+i,inst,ann,doc,annType,featureName,alphabet,dt,mvt,codeas);    
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
    if(obj == null) {
      throw new GateRuntimeException("No target value for feature "+targetFeature+
              " for instance at offset "+gate.Utils.start(instanceAnnotation)+" in document "+doc.getName());
    }
    double value = Double.NaN;
    if(obj instanceof Number) {
      value = ((Number)obj).doubleValue();
    } else {
      String asString = obj.toString();
      try {
        value = Double.parseDouble(asString);
      } catch(Exception ex) {
        throw new GateRuntimeException("Could not convert target value to a double for feature "+targetFeature+
                " for instance at offset "+gate.Utils.start(instanceAnnotation)+" in document "+doc.getName());
      }
    }
    inst.setTarget(value);
  }
  
  /**
   * Extract the class label for the given instance annotation.
   * This gets used when the task performed is classification (using either a classification
   * algorithm or a sequence tagging algorithm). In both cases, the class label is fetched
   * from the instance annotation as the value of the targetFeature. 
   * @param inst
   * @param alphabet the label alphabet, must be of type LabelAlphabet
   * @param targetFeature
   * @param instanceAnnotation
   * @param doc 
   */
  public static void extractClassTarget(Instance inst, Alphabet alphabet, String targetFeature, Annotation instanceAnnotation, AnnotationSet inputAS) {
    LabelAlphabet labelalphabet = (LabelAlphabet)alphabet;
    Document doc = inputAS.getDocument();
    Object obj = instanceAnnotation.getFeatures().get(targetFeature);    
    // Brilliant, we have a missing target, WTF? Throw an exception
    if(obj == null) {
      throw new GateRuntimeException("No target value for feature "+targetFeature+
              " for instance at offset "+gate.Utils.start(instanceAnnotation)+" in document "+doc.getName());
    }
    String value = obj.toString();
    inst.setTarget(labelalphabet.lookupLabel(value));
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
   * three classes. In the case of NER classes, the target alphabet is always a labelalphabet
   * and pre-filled with all possible class labels when this method is invoked, so it does not
   * matter if the growth of the alphabet is stopped or not. 
   *
   * @param inst The instance where the target should be set
   * @param classType The annotation name of the annotation that represents the class, e.g.
   * "Person" (this is required for the sequence tagging task!)
   * @param alph the label alphabet to use, must be an instance of LabelAlphabet
   * @param inputASname, the annotation set name of the set which contains the class annotations
   * @param instanceAnnotation, the instance annotation, e.g. "Token".
   * @param doc the document which is currently being processed
   */
  public static void extractClassForSeqTagging(Instance inst, Alphabet alph, AnnotationSet classAS, Annotation instanceAnnotation) {
      String target = "";
      Document doc = classAS.getDocument();
      if(!(alph instanceof LabelAlphabet)) {
        throw new GateRuntimeException("LF extractClassForSeqTagging: the alphabet must be of type LabelAlphabet"+
                " for instance annotation at offset "+gate.Utils.start(instanceAnnotation)+
                " in document "+doc.getName());
      }
      LabelAlphabet labelalph = (LabelAlphabet)alph;
      AnnotationSet overlappingClassAnns = Utils.getOverlappingAnnotations(classAS, instanceAnnotation);
      // Note: each instance annotation should only overlap with at most one class annotation.
      // Like with overlapping annotations from the feature specification, we log a warning and 
      // pick the longest here
      if (overlappingClassAnns.size() > 0) {
        Annotation classAnn = null;
        if(overlappingClassAnns.size() > 1) {
          logger.warn("More than one class annotation for instance at offset "+
                  gate.Utils.start(instanceAnnotation)+" in document "+doc.getName());
          // find the longest
          int maxSize = 0;
          for(Annotation ann : overlappingClassAnns.inDocumentOrder()) {
            if(gate.Utils.length(ann)>maxSize) {
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
    if(val == null) return false;
    return((Boolean)inst.getProperty(PROP_IGNORE_HAS_MV));
  }
  
  public static boolean instanceHasMV(Instance inst) {
    Object val = inst.getProperty(PROP_HAVE_MV);
    if(val == null) return false;
    return((Boolean)inst.getProperty(PROP_HAVE_MV));
  }
  
  
  /**
   * Extract the exact location of the instance for use as an instance name.
   * The name string is made up of the document name plus the start and end offsets of the instance 
   * annotation.
   */
  public static void extractName(Instance inst, Annotation instanceAnnotation, Document doc) {
    String value = doc.getName() + ":" + gate.Utils.start(instanceAnnotation) + ":" +
            gate.Utils.end(instanceAnnotation);
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
    String parts[] = mlFeature.split(NAMESEP,-1);
    if(parts.length < 2) {
      throw new RuntimeException("Odd ML feature name, does not contain a NAMESEP: "+mlFeature);
    }
    return parts[0];
  }
  
  
  
  /**
   * Try and find the attribute that corresponds to the ML featureName.
   * 
   * This requries the instance annotation type because the way how the 
   * ML feature is generated depends on the instance annotation type. 
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
    if(attrName.contains(TYPESEP)) {
      String parts[] = attrName.split(TYPESEP,-1);
      if(parts.length != 2) throw new RuntimeException("ODD ATTRIBUTE NAME"); // should never happen
      String featType = parts[0];
      String featName = parts[1];
      for(FeatureSpecAttribute attr : attributes) {
        if(featName.equals(attr.feature)) {
          // the types should also match but we have to consider that the 
          // type in the attribute specification can be empty in which case we 
          // have a match if the type from the mlFeature is also empty or matches
          // the instanceType. The same is true vice-versa: if the type from the
          // mlFeature is empty than the type from the attr spec must be empty
          // or match the instanceType
          if(featType.equals(attr.annType)) {
            ret = attr;
            break;
          } else if(featType.isEmpty()) {
            if(attr.annType.isEmpty() || attr.annType.equals(instanceType)) {
              ret = attr;
              break;
            }             
          } else if(attr.annType.isEmpty()) {
            if(featType.isEmpty() || featType.equals(instanceType)) {
              ret = attr;
              break;
            }
          }
        }
      }
    } else {      
      for(FeatureSpecAttribute attr : attributes) {
        if(attr.name != null && attr.name.equals(attrName)) {
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
   * Same inputAS the method, but makes sure a non-growable Alphabet is considered.
   * @param fv
   * @param key
   * @param val 
   */
  private static void addToFeatureVector(AugmentableFeatureVector fv, Object key, double val) {
    Alphabet a = fv.getAlphabet();
    if(!a.contains(key) && a.growthStopped()) {
      //System.err.println("DEBUG: GROWTH STOPPED! key="+key+",a="+a);
      return;
    }
    fv.add(key,val);
  }
        
  
        
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
        
  

}
