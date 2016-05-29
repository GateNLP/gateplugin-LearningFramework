/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.data;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import gate.plugin.learningframework.features.CodeAs;
import gate.plugin.learningframework.features.Datatype;
import gate.plugin.learningframework.features.FeatureExtraction;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecAttribute;
import gate.plugin.learningframework.features.FeatureSpecAttributeList;
import gate.plugin.learningframework.features.FeatureSpecNgram;
import gate.plugin.learningframework.features.FeatureSpecSimpleAttribute;
import gate.plugin.learningframework.mallet.LFPipe;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Describes all Attributes/features of a dataset.
 * This describes the attributes of a corpus representation at
 * the time the instance is created. 
 * @author Johann Petraj
 */
public class Attributes implements Iterable<Attribute> {
  /**
   * Generate the attributes object from the information in the pipe.
   * The pipe should be a LFPipe, but we also try to come up with something
   * if it is an ordinary pipe. 
   * @param pipe 
   */
  public Attributes(Pipe pipe, String instanceType) {
    // first create the attributes (independent vars)    
    Alphabet dataAlphabet = pipe.getDataAlphabet();
    // if we can, also represent the pipe as LFPipe
    LFPipe lfPipe = null;
    FeatureInfo featureInfo = null;
    if(pipe instanceof LFPipe) {
      lfPipe = (LFPipe)pipe;
      featureInfo = lfPipe.getFeatureInfo();
    }
    // the alphabet we use if we have a boolean variable
    Alphabet booleanAlph = new Alphabet();
    booleanAlph.lookupIndex("false");
    booleanAlph.lookupIndex("true");    
    for(int i =0; i<dataAlphabet.size(); i++) {
      String malletFeatureName = (String) dataAlphabet.lookupObject(i);
      // create an attribute with default settings for datatype, code and 
      // alphabet, if we got more information about it we will override later
      Attribute attr = new Attribute(
              malletFeatureName, i, Datatype.numeric, null, null, null);
      // add it
      attributes.add(attr);
      name2index.put(malletFeatureName, i);
      // If we have a LFPipe, also get some additional info about the type, values etc.
      // NOTE that the default type for features that indicate the presence of
      // strings, ngrams etc. (which we assume when nothing else is declared)
      // is numeric, so that instead of 0/1 we can have counts or tf/idf or 
      // other scores. So only if there is an explicity declaration of a different
      // type, we will change the default values.
      if(featureInfo != null) {
        FeatureSpecAttribute fsAttr = 
                FeatureExtraction.lookupAttributeForFeatureName(
                  featureInfo.getAttributes(),
                  malletFeatureName,
                  instanceType);
        if(fsAttr instanceof FeatureSpecAttributeList) {
          FeatureSpecAttributeList fsAttrList = (FeatureSpecAttributeList)fsAttr;
          attr.codeAs = fsAttrList.codeas;
          attr.mvTreatment = fsAttrList.missingValueTreatment;
          attr.datatype = fsAttrList.datatype;
          if(fsAttrList.datatype == Datatype.bool) {
            attr.alphabet = booleanAlph;
          } else if(fsAttrList.datatype == Datatype.nominal) {
            if(fsAttrList.codeas == CodeAs.number)
              attr.alphabet = fsAttrList.alphabet;
          } 
        } else if(fsAttr instanceof FeatureSpecSimpleAttribute) {
          FeatureSpecSimpleAttribute fsAttrSimple = (FeatureSpecSimpleAttribute)fsAttr;
          attr.codeAs = fsAttrSimple.codeas;
          attr.mvTreatment = fsAttrSimple.missingValueTreatment;
          attr.datatype = fsAttrSimple.datatype;
          if(fsAttrSimple.datatype == Datatype.bool) {
            attr.alphabet = booleanAlph;
          } else if(fsAttrSimple.datatype == Datatype.nominal) {
            if(fsAttrSimple.codeas == CodeAs.number)
              attr.alphabet = fsAttrSimple.alphabet;
          }           
        } else if(fsAttr instanceof FeatureSpecNgram) {
          // nothing to do here
        } else if(fsAttr==null) {
          throw new RuntimeException("FeatureSpecification is null for feature "+
                  i+", name="+malletFeatureName+ 
                  "\nFeatureSpecification is "+featureInfo);
        } else {
          throw new RuntimeException(
                  "Impossible: found odd FeatureSpecAttribute type "+fsAttr.getClass());
        }
      }
    }
    Alphabet targetAlphabet = pipe.getTargetAlphabet();
    // if the target alphabet exists, we assume a nominal target
    // The target index is the next index after the last independent attribute
    // index. This is convenient for Weka.
    targetAttribute = new Attribute("target", attributes.size(), Datatype.numeric, null, null, null);
    if(targetAlphabet != null) {
      targetAttribute.alphabet = targetAlphabet;
      targetAttribute.datatype = Datatype.nominal;
    }
  }
  /**
   * Get the independent Attribute object for the attribute with that name.
   * If no such attribute exists, return null. The target attribute cannot
   * be retrieved that way, use getTargetAttribute() instead.
   * @param name 
   */
  public Attribute getAttribute(String name) {
    Integer idx = name2index.get(name);
    if(idx==null) return null;
    return attributes.get(idx);
  }
  /**
   * Return the independent attribute with the given index.
   * The target attribute cannot be retrieved that way, use getTargetAttribute()
   * instead.
   * @param index
   * @return 
   */
  public Attribute getAttribute(int index) {
    if(index>=attributes.size()) {
      throw new RuntimeException("Attribute "+index+" does not exist, only have "+attributes.size());
    }
    return attributes.get(index);
  }
  public int nAttributes() {
    return attributes.size();
  }
  protected Map<String,Integer> name2index = new HashMap<String,Integer>();
  protected List<Attribute> attributes = new ArrayList<Attribute>();

  /**
   * Get the target Attribute.
   * NOTE: the target attribute has index one larger than the highest
   * index of the independent attributes, but this index cannot be used
   * to retrieve it! 
   * @return 
   */
  public Attribute getTargetAttribute() {
    return targetAttribute;
  }
  
  protected Attribute targetAttribute;
  
  @Override
  public Iterator<Attribute> iterator() {
    return new AttributeIterator();
  }
  
  public class AttributeIterator implements Iterator<Attribute> {

    /* the index which would get returned next */
    private int currentIndex = 0;
    @Override
    public boolean hasNext() {
      return(attributes.size()>currentIndex);
    }

    @Override
    public Attribute next() {
      return attributes.get(currentIndex++);      
    }
  }
}
