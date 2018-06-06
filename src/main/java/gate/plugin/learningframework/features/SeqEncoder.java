/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.features;

import gate.Annotation;
import gate.Document;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Class for implementing methods to convert sequences to classes and back
 * @author Johann Petrak 
 */
public abstract class SeqEncoder {
  public static final String CODESEP = "|";
  public static final String CODESEP_PATTERN = Pattern.quote(CODESEP);
  public static final String TYPESEP = ",";
  public static final String TYPESEP_PATTERN = Pattern.quote(TYPESEP);
  public static final String CODE_OUTSIDE = "O";
  public static final String CODE_BEGIN = "B";
  public static final String CODE_INSIDE = "I";
  public static final String CODE_END = "E";
  public static final String CODE_SINGLE = "S"; // = begin and end
  private Map<String,String> options = new HashMap<>();
  public abstract String seqAnns2ClassLabel(Collection<Annotation> seqAnns, Annotation instAnn, Document curDoc);
  public void setOptions(Map<String,String> options) {
    if(options != null) this.options.putAll(options);
  }

  /**
   * Return options.
   * 
   * TODO: this still needs to get implemented.
   * 
   * @return Option settings.
   */
  public Map<String,String> getOptions() { return options; }
  // TODO: not sure yet what the best way is to implement the conversion back from
  // class labels to annotations. This probably needs to map the full sequence
  // of class labels to a set of annotations?
}
