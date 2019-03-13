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

import gate.util.GateRuntimeException;
import java.io.File;
import java.io.StringReader;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Parse a feature specification and create an initial FeatureInfo object. 
 *
 * @author Johann Petrak
 */
public class FeatureSpecification {

  //private static Logger LOGGER = Logger.getLogger(FeatureSpecification.class.getName());


  private org.jdom.Document jdomDocConf = null;

  private URL url;
  
  // for error checking we remember all mappings from embedding ids to 
  // each of the things that can be specified about embeddings:
  // file, dims, train
  private Map<String,String> embeddingId2file = new HashMap<>();
  private Map<String,Integer> embeddingId2dims = new HashMap<>();
  private Map<String,Integer> embeddingId2minfreq = new HashMap<>();
  private Map<String,String> embeddingId2train = new HashMap<>();

  /**
   * Constructor from URL
   * @param configFileURL URL of feature config XML file
   */
  public FeatureSpecification(URL configFileURL) {
    url = configFileURL;

    SAXBuilder saxBuilder = new SAXBuilder(false);
    try {
      try {
        jdomDocConf = saxBuilder.build(configFileURL);
        parseConfigXml();
      } catch (JDOMException jde) {
        throw new GateRuntimeException(jde);
      }
    } catch (java.io.IOException ex) {
      throw new GateRuntimeException("Error parsing config file URL " + url, ex);
    }
  }

  /**
   * Constructor from String
   * @param configString XML string
   */
  public FeatureSpecification(String configString) {
    SAXBuilder saxBuilder = new SAXBuilder(false);
    try {
      try {
        jdomDocConf = saxBuilder.build(new StringReader(configString));
        parseConfigXml();
      } catch (JDOMException jde) {
        throw new GateRuntimeException(jde);
      }
    } catch (java.io.IOException ex) {
      throw new GateRuntimeException("Error parsing config file String:\n" + configString, ex);
    }
  }

  /**
   * Constructor from File
   * @param configFile File for feature config XML file
   */
  public FeatureSpecification(File configFile) {
    SAXBuilder saxBuilder = new SAXBuilder(false);
    try {
      try {
        this.jdomDocConf = saxBuilder.build(configFile);
        parseConfigXml();
      } catch (JDOMException jde) {
        throw new GateRuntimeException(jde);
      }
    } catch (java.io.IOException ex) {
      throw new GateRuntimeException("Error parsing config file " + configFile, ex);
    }
  }

  private void parseConfigXml() {

    // TODO: process children in order, then dispatch how to parse based on type.
    // Then, parsing ATTRIBUTE and ATTRIBUTELIST is nearly identical except that 
    // we parse the range in addition for ATTRIBUTELIST.
    // Make an else part where we document how we might add additional stuff...
    Element rootElement = jdomDocConf.getRootElement();

    @SuppressWarnings("unchecked")
    List<Element> elements = rootElement.getChildren();
    
    int n = 0;
    for (Element element : elements) {
      n++;
      String elementName = element.getName().toLowerCase();
      if (elementName.equals("attribute")) {
        featureInfo.add(parseSimpleAttribute(element, n));
      } else if (elementName.equals("attributelist")) {
        FeatureSpecSimpleAttribute att = parseSimpleAttribute(element, n);
        int from = Integer.parseInt(element.getChildText("FROM"));
        int to = Integer.parseInt(element.getChildText("TO"));
        String withinType = getChildTextOrElse(element, "WITHIN", null); 
        featureInfo.add(new FeatureSpecAttributeList(att, withinType, from, to));
      } else if (elementName.equals("ngram")) {
        featureInfo.add(parseNgramAttribute(element, n));
      } else {
        throw new GateRuntimeException("Not a recognized element name for the LearningFramework config file: " + elementName);
      }
    }
    // go through all the feature specifications and make sure the 
    // embedding settings are set to whatever we have stored for the id.
    // If after this some values are still blank, it is the responsibility
    // of the backend code to find the appropriate default values since 
    // different backends or algorithms could work better with different 
    // defaults
    for (FeatureSpecAttribute fs : featureInfo.featureSpecs) {
      if(fs.datatype == Datatype.nominal) {
        if(fs.emb_file.isEmpty()) {
          String tmp_emb_file = embeddingId2file.get(fs.emb_id);
          if(tmp_emb_file != null) {
            fs.emb_file = tmp_emb_file;
          }
        }
        if(fs.emb_dims == 0) {
          Integer tmp_emb_dims = embeddingId2dims.get(fs.emb_id);
          if(tmp_emb_dims != null) {
            fs.emb_dims = tmp_emb_dims;
          }
        }
        if(fs.emb_train.isEmpty()) {
          String tmp_emb_train = embeddingId2train.get(fs.emb_id);
          if(tmp_emb_train != null) {
            fs.emb_train = tmp_emb_train;
          }
        }
        if(fs.emb_minfreq == 0) {
          Integer tmp_emb_minfreq = embeddingId2minfreq.get(fs.emb_id);
          if(tmp_emb_minfreq != null) {
            fs.emb_minfreq = tmp_emb_minfreq;
          } else {
            fs.emb_minfreq = 1;
          }
        }
      }
    }
  } // parseConfigXml

  private FeatureSpecAttribute parseAndAddEmbeddingInfo(Element element, int i, FeatureSpecAttribute spec) {
    // expects any FeatureSpec object and will add embedding info to it, if present
    // This using the info already stored in the instance members to check for 
    // contradictions in the specification
    
    // the element is the parent, so lets first get the embedding child, if any
    Element emb = getChildOrNull(element, "EMBEDDINGS");
    if(emb==null) {
      return spec;  // nothing there, nothing to do
    }
    // get all the possible settings for the embedding
    String emb_id = getChildTextOrElse(emb, "ID", "");
    String emb_file = getChildTextOrElse(emb, "FILE", "");
    String emb_dims_str = getChildTextOrElse(emb, "DIMS", "");
    String emb_minfreq_str = getChildTextOrElse(emb, "MINFREQ", "");
    String emb_train = getChildTextOrElse(emb,"TRAIN","");
    // only if any of the file,dim, or train things are set to non-empty, 
    // we need to bother
    if(!emb_file.isEmpty()) {
      // TODO: check the file exists already here!!
      String have_file = embeddingId2file.get(emb_id);
      if(have_file == null) {
        embeddingId2file.put(emb_id, emb_file);
        spec.emb_file = emb_file;
      } else if(!emb_file.equals(have_file)) {
        throw new GateRuntimeException("EMBEDDING setting file to "+emb_file+" for attribute "+i+" contradicts "+
                have_file+" set previously");
      } else {
        spec.emb_file = emb_file;
      }
    } else {  // the file spec is empty:
      // if the file spec has been set for this id earlier, use that,
      // otherwise the default value in the specification object is unchanged
      // (empty string)
      String have_file = embeddingId2file.get(emb_id);
      if(have_file != null) {
        spec.emb_file = have_file;
      }
    }
    if(!emb_train.isEmpty()) {
      if(!emb_train.equals("yes") && !emb_train.equals("no") && !emb_train.equals("mapping") && 
         !emb_train.equals("onehot")) {
        throw new GateRuntimeException("EMBEDDING TRAIN setting must be one of yes, no, onehot or mapping for attribute"+i);
      }
      String have_train = embeddingId2train.get(emb_id);      
      if(have_train == null) {
        embeddingId2train.put(emb_id, emb_train);
        spec.emb_train = emb_train;
      } else if(!emb_train.equals(have_train)) {
        throw new GateRuntimeException("EMBEDDING setting train to "+emb_train+" for attribute "+i+" contradicts "+
                have_train+" set previously");
      } else {
        spec.emb_train = emb_train;        
      }
    } else {
      String have_train = embeddingId2train.get(emb_id);
      if(have_train != null) {
        spec.emb_train = have_train;
      }
    }
    if(!emb_dims_str.isEmpty()) {
      Integer have_dims = embeddingId2dims.get(emb_id);
      Integer emb_dims = Integer.parseInt(emb_dims_str);
      if(have_dims == null) {
        embeddingId2dims.put(emb_id, emb_dims);
        spec.emb_dims = emb_dims;
      } else if(!emb_dims.equals(have_dims)) {
        throw new GateRuntimeException("EMBEDDING setting dims to "+emb_dims+" for attribute "+i+" contradicts "+
                have_dims+" set previously");
      } else {
        spec.emb_dims = emb_dims;        
      }
    } else {
      Integer have_dims = embeddingId2dims.get(emb_id);
      if(have_dims != null) {
        spec.emb_dims = have_dims;
      }
    }
    if(!emb_minfreq_str.isEmpty()) {
      Integer have_minfreq = embeddingId2minfreq.get(emb_id);
      Integer emb_minfreq = Integer.parseInt(emb_minfreq_str);
      if(have_minfreq == null) {
        embeddingId2minfreq.put(emb_id, emb_minfreq);
        spec.emb_minfreq = emb_minfreq;
      } else if(!emb_minfreq.equals(have_minfreq)) {
        throw new GateRuntimeException("EMBEDDING setting minfreq to "+emb_minfreq+" for attribute "+i+" contradicts "+
                have_minfreq+" set previously");
      } else {
        spec.emb_minfreq = emb_minfreq;        
      }
    } else {
      Integer have_minfreq = embeddingId2minfreq.get(emb_id);
      if(have_minfreq != null) {
        spec.emb_minfreq = have_minfreq;
      }
    }
    spec.emb_id = emb_id;
    return spec;
  }

  
  private FeatureSpecSimpleAttribute parseSimpleAttribute(Element attributeElement, int i) {
    String aname = getChildTextOrElse(attributeElement, "NAME", "").trim();
    String feat = getChildTextOrElse(attributeElement, "FEATURE", "").trim();
    String dtstr = getChildTextOrElse(attributeElement, "DATATYPE", null);    
    if (!feat.isEmpty() && dtstr == null) {
      throw new GateRuntimeException("DATATYPE not specified for ATTRIBUTE " + i);
    }
    if(feat.isEmpty()) {
      if(dtstr == null) {
        dtstr = "bool";
      } else if(!dtstr.equals("bool") && !dtstr.equals("boolean")) {
        throw new GateRuntimeException("DATATYPE must be bool or not specified if no feature given in ATTRIBUTE "+i);
      }
    }
    if(dtstr.equals("boolean")) {
      dtstr = "bool"; // allow both but internally we use bool to avoid keyword clash.
    }
    Datatype dt = Datatype.valueOf(dtstr);
    // TODO: this should be named ANNOTATIONTYPE or ANNTYPE to avoid confusion
    // with the datatype
    String atype = getChildTextOrElse(attributeElement, "TYPE", "");
    // if empty we use the instance annotation type, whatever that is
    //if (atype.isEmpty()) {
    //  System.err.println("Warning: TYPE in ATTRIBUTE "+i+" is empty, using instance annotation type");
    //}
    String codeasstr = getChildTextOrElse(attributeElement, "CODEAS", "").toLowerCase();
    if (!codeasstr.isEmpty() && !codeasstr.equals("one_of_k") && !codeasstr.equals("number")) {
      throw new GateRuntimeException("CODEAS for ATTRIBUTE " + i + " specified but not one_of_k or number but " + codeasstr);
    }
    // codeas currently only makes sense and is used for nominal, so complain if it is specified
    // for other datatypes
    if(!codeasstr.isEmpty() && (dt != Datatype.nominal) ) {
      throw new GateRuntimeException("CODEAS can only be used with DATATYPE nominal for ATTRIBUTE "+i);
    }
    // for non-nominal, we always really use number
    if(codeasstr.isEmpty() && dt != Datatype.nominal) {
      codeasstr = "number";
    }
    // for nominal the default when not specified is on_of_k
    if(codeasstr.isEmpty() && dt == Datatype.nominal) {
      codeasstr = "one_of_k";
    }
    
    CodeAs codeas = CodeAs.valueOf(codeasstr);
    // the default for missingvaluetreatment is special_value for numeric and 
    // number-coded nominal, but for one-of-k coded values, we use "zero_value"
    // because this is usually how the absence of such values is coded!
    String missingValueTreatmentStr;
    String featureName4Value = "";
    if(dt==Datatype.nominal && codeas==CodeAs.one_of_k) {
      missingValueTreatmentStr = getChildTextOrElse(attributeElement, "MISSINGVALUETREATMENT", "keep");
      featureName4Value = getChildTextOrElse(attributeElement,"FEATURENAME4VALUE","");
    } else if (dt==Datatype.bool) {
      missingValueTreatmentStr = getChildTextOrElse(attributeElement, "MISSINGVALUETREATMENT", "zero_value");
    } else {
      missingValueTreatmentStr = getChildTextOrElse(attributeElement, "MISSINGVALUETREATMENT", "special_value");
    }
    MissingValueTreatment mvt = MissingValueTreatment.valueOf(missingValueTreatmentStr);
    // If the datatype is not anything other than nominal, we also allow the 
    // setting "listsep" for automatical list splitting    
    String listsep = getChildTextOrElse(attributeElement, "LISTSEP", "");
    if(!listsep.isEmpty()) {
      if(dt!=Datatype.nominal) {
        throw new GateRuntimeException("LISTSEP only allowed if datatype is nominal");
      }      
    }
    String withinType = getChildTextOrElse(attributeElement, "WITHIN", null); 
    String defaultMissingValue = "";
    if(dt == Datatype.bool) {
      defaultMissingValue = "false";
    } else if(dt == Datatype.numeric) {
      defaultMissingValue = "0.0";
    }
    String missingValueValue = getChildTextOrElse(attributeElement, "MISSINGVALUE", defaultMissingValue);
    
    // TODO: not implemented yet, but we should add this!!
    String scalingMethod = "";
    String transformMethod = "";
    FeatureSpecSimpleAttribute att = new FeatureSpecSimpleAttribute(
            aname,
            atype,
            feat,
            dt,
            codeas,
            mvt,
            missingValueValue,
            scalingMethod,
            transformMethod,
            withinType,
            listsep,
            featureName4Value
    );
    // now if this is a nominal attribute, add any embedding block
    if(dt == Datatype.nominal) {
      att = (FeatureSpecSimpleAttribute)parseAndAddEmbeddingInfo(attributeElement, i, att);
    } else {
      if(getChildOrNull(attributeElement, "EMBEDDINGS") != null) {
        throw new GateRuntimeException("EMBEDDINGS not allowed for non-NOMINAL ATTRIBUTE "+i);
      }
    }
    return att;
  }

  private FeatureSpecAttribute parseNgramAttribute(Element ngramElement, int i) {
    String aname = getChildTextOrElse(ngramElement,"NAME","").trim();
    String annType = getChildTextOrElse(ngramElement,"TYPE","").trim();
    if (annType.isEmpty()) {
      throw new GateRuntimeException("TYPE in NGRAM " + i + " must not be missing or empty");
    }
    String numberString = getChildTextOrElse(ngramElement,"NUMBER","1").trim();
    String featureName4Value = getChildTextOrElse(ngramElement,"FEATURENAME4VALUE","");
    String maxlen = getChildTextOrElse(ngramElement,"MAXLEN","0");
    String shorten = getChildTextOrElse(ngramElement,"SHORTEN","").toLowerCase();
    
    String feature = getChildTextOrElse(ngramElement,"FEATURE","").trim();
    if (feature.isEmpty()) {
      throw new GateRuntimeException("FEATURE in NGRAM " + i + " must not be missing or empty");
    }
    if (!(shorten.equals("") || shorten.equals("left") || shorten.equals("left") || 
            shorten.equals("both") || shorten.equals("middle"))) {
      throw new GateRuntimeException("SHORTEN must be missing, empty or one of right, left, middle, both");
    }
    FeatureSpecNgram ng = new FeatureSpecNgram(
            aname,
            Integer.parseInt(numberString),
            annType,
            feature,
            featureName4Value
    );
    ng.maxlen = Integer.parseInt(maxlen);
    ng.shorten = shorten;
    ng = (FeatureSpecNgram)parseAndAddEmbeddingInfo(ngramElement, i, ng);
    return ng;
  }

  private FeatureInfo featureInfo = new FeatureInfo();
  
  /**
   * Return the FeatureInfo object for this specification.
   * 
   * This will always return a new deep copy of the FeatureInfo that corresponds
   * to the information inf the FeatureSepcification. 
   * 
   * @return FeatureInfo instance
   */
  public FeatureInfo getFeatureInfo() {
    return new FeatureInfo(featureInfo); // this returns a cloned copy of the original
  }

  //// HELPER METHODS
  /**
   * Return the text of a single child element or a default value. This checks that there is at most
   * one child of this annType and throws and exception if there are more than one. If there is no
   * child of this name, then the value elseVal is returned. NOTE: the value returned is trimmed if
   * it is a string, but case is preserved.
   * NOTE: this tries both the all-uppercase and the all-lowercase variant of the given name.
   */
  private static String getChildTextOrElse(Element parent, String name, String elseVal) {
    @SuppressWarnings("unchecked")
    List<Element> children = parent.getChildren(name);
    if (children.size() > 1) {
      throw new GateRuntimeException("Element " + parent.getName() + " has more than one nested " + name + " element");
    }
    if(children.isEmpty()) {
      return elseVal;
    }
    String tmp = parent.getChildTextTrim(name.toUpperCase());
    if(tmp == null) {
      tmp = parent.getChildText(name.toLowerCase());
    }
    if (tmp == null) {
      return elseVal;
    } else {
      return tmp;
    }
  }
  
  private static Element getChildOrNull(Element parent, String name) {
    @SuppressWarnings("unchecked")
    List<Element> children = parent.getChildren(name);
    if (children.size() > 1) {
      throw new GateRuntimeException("Element " + parent.getName() + " has more than one nested " + name + " element");
    } else if (children.isEmpty()) {
      return null;
    } else {
      return children.get(0);
    }
   
  }
  
  @Override
  public String toString() {
    // The only difference between this and the feature info is that we also
    // know about the embedding mapping, so just print the feature info
    return featureInfo.toString();
  }
  
  

}
