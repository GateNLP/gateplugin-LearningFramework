/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.tests;

import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.Instance;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;
import java.io.File;
import java.net.MalformedURLException;

/**
 *
 * @author Johann Petrak
 */
public class Utils {
  // For the comparison of doubles, we use an epsilon of approximately
  // 1.7E-15 which is 1.0 (the maximum expected number) divided through the value of the maximum 
  // mantissa of double (64 bit), but with 3 bits taken away, i.e. 52-3 bits for the mantissa,
  // i.e. 2^49 
  public static final double EPS = 1.7763568394002505e-15;
  public static final double EPS4 = 1e-4;
  
  // create a string with 1000 blanks which we will use as document content for many documents
  // dynamically created in the tests
  public static final String STR1000 = new String(new char[1000]).replace("\0", " ");
  
  public static Document newDocument() throws ResourceInstantiationException {
    return Factory.newDocument(STR1000);
  }
  
  /**
   * Add an annotation to the set with the given name and return the set.
   */  
  public static Annotation addAnn(Document doc, String setName, int from, int to, String type, FeatureMap fm) {
    AnnotationSet set = doc.getAnnotations(setName);
    int id = gate.Utils.addAnn(set, from, to, type, fm);
    return set.get(id);
  }
  
  public static Instance newInstance() {
    return new Instance(new AugmentableFeatureVector(new Alphabet()),null,null,null);    
  }
  
  public static Document loadDocument(File file) throws MalformedURLException, ResourceInstantiationException {
    FeatureMap parms = Factory.newFeatureMap();
    parms.put("sourceUrl", file.toURI().toURL());
    Document doc = (Document)Factory.createResource("gate.corpora.DocumentImpl", parms);
    return doc;
  }

}
