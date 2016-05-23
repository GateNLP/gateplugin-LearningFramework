/*
 * FeatureSpecification.java
 *  
 * Copyright (c) 1995-2015, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * Copyright 2015 South London and Maudsley NHS Trust and King's College London
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 *
 * Genevieve Gorrell, 9 Jan 2015
 */
package gate.plugin.learningframework.features;

import gate.util.GateRuntimeException;
import java.io.File;
import java.io.StringReader;

import java.net.URL;
import java.util.List;
import org.apache.log4j.Logger;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

/**
 * Parse a feature specification and create an initial FeatureInfo object. 
 *
 * @author Johann Petrak
 */
public class FeatureSpecification {

  private static Logger logger = Logger.getLogger(FeatureSpecification.class.getName());


  private org.jdom.Document jdomDocConf = null;

  private URL url;

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

    List<Element> elements = rootElement.getChildren();

    // In this method, we directly modify the attributes list from the featureinfo we have 
    // stored. 
    List<FeatureSpecAttribute> attributes = featureInfo.getAttributes();
    
    int n = 0;
    for (Element element : elements) {
      n++;
      String elementName = element.getName().toLowerCase();
      if (elementName.equals("attribute")) {
        attributes.add(parseSimpleAttribute(element, n));
      } else if (elementName.equals("attributelist")) {
        FeatureSpecSimpleAttribute att = parseSimpleAttribute(element, n);
        int from = Integer.parseInt(element.getChildText("FROM"));
        int to = Integer.parseInt(element.getChildText("TO"));
        attributes.add(new FeatureSpecAttributeList(att, from, to));
      } else if (elementName.equals("ngram")) {
        attributes.add(parseNgramAttribute(element, n));
      } else {
        throw new GateRuntimeException("Not a recognized element name for the LearningFramework config file: " + elementName);
      }
    }
  } // parseConfigXml

  private FeatureSpecSimpleAttribute parseSimpleAttribute(Element attributeElement, int i) {
    String aname = getChildTextOrElse(attributeElement, "NAME", "").trim();
    String feat = getChildTextOrElse(attributeElement, "FEATURE", "").trim();
    String dtstr = getChildTextOrElse(attributeElement, "DATATYPE", null);    
    if (!feat.isEmpty() && dtstr == null) {
      throw new GateRuntimeException("DATATYPE not specified for ATTRIBUTE " + i);
    }
    if(feat.isEmpty()) {
      if(dtstr == null) dtstr = "bool";
      else if(!dtstr.equals("bool") && !dtstr.equals("boolean")) {
        throw new GateRuntimeException("DATATYPE must be bool or not specified if no feature given in ATTRIBUTE "+i);
      }
    }
    if(dtstr.equals("boolean")) dtstr = "bool"; // allow both but internally we use bool to avoid keyword clash.
    Datatype dt = Datatype.valueOf(dtstr);
    // TODO: this should be named ANNOTATIONTYPE or ANNTYPE to avoid confusion
    // with the datatype
    String atype = getChildTextOrElse(attributeElement, "TYPE", "");
    // must not be empty
    if (atype.isEmpty()) {
      //throw new GateRuntimeException("TYPE in ATTRIBUTE " + i + " must not be missing or empty");
      System.err.println("Warning: TYPE in ATTRIBUTE "+i+" is empty, using instance annotation type");
    }
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
    String missingValueTreatmentStr = getChildTextOrElse(attributeElement, "MISSINGVALUETREATMENT", "special_value");
    MissingValueTreatment mvt = MissingValueTreatment.valueOf(missingValueTreatmentStr);
    // TODO: not implemented yet, but we should add this!!
    String scalingMethod = "";
    String transformMethod = "";
    String missingValueValue = ""; // if MVs should get replaced with a constant value, that value as a String
    FeatureSpecSimpleAttribute att = new FeatureSpecSimpleAttribute(
            aname,
            atype,
            feat,
            dt,
            codeas,
            mvt,
            missingValueValue,
            scalingMethod,
            transformMethod
    );
    return att;
  }

  private FeatureSpecAttribute parseNgramAttribute(Element ngramElement, int i) {
    String aname = getChildTextOrElse(ngramElement,"NAME","").trim();
    String annType = getChildTextOrElse(ngramElement,"TYPE","").trim();
    String numberString = getChildTextOrElse(ngramElement,"NUMBER","1").trim();
    if (annType.isEmpty()) {
      throw new GateRuntimeException("TYPE in NGRAM " + i + " must not be missing or empty");
    }
    
    String feature = getChildTextOrElse(ngramElement,"FEATURE","").trim();
    if (feature.isEmpty()) {
      throw new GateRuntimeException("FEATURE in NGRAM " + i + " must not be missing or empty");
    }
    FeatureSpecNgram ng = new FeatureSpecNgram(
            aname,
            Integer.parseInt(numberString),
            annType,
            feature
    );
    return ng;
  }

  private FeatureInfo featureInfo = new FeatureInfo();
  
  /**
   * Return the FeatureInfo object for this specification.
   * This will always return a new deep copy of the FeatureInfo that corresponds
   * to the information inf the FeatureSepcification. 
   * 
   * @return 
   */
  public FeatureInfo getFeatureInfo() {
    return new FeatureInfo(featureInfo); // this returns a cloned copy of the original
  }

  //// HELPER METHODS
  /**
   * Return the text of a single child element or a default value. This checks that there is at most
   * one child of this annType and throws and exception if there are more than one. If there is no
   * child of this name, then the value elseVal is returned. NOTE: the value returned is trimmed if
   * it is a string, but case is preserved
   */
  private static String getChildTextOrElse(Element parent, String name, String elseVal) {
    List<Element> children = parent.getChildren(name);
    if (children.size() > 1) {
      throw new GateRuntimeException("Element " + parent.getName() + " has more than one nested " + name + " element");
    }
    String tmp = parent.getChildTextTrim(name);
    if (tmp == null) {
      return elseVal;
    } else {
      return tmp;
    }
  }

}
