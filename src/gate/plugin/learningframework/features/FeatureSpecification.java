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
    // if empty we use the instance annotation type, whatever that is
    if (atype.isEmpty()) {
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
    // the default for missingvaluetreatment is special_value for numeric and 
    // number-coded nominal, but for one-of-k coded values, we use "zero_value"
    // because this is usually how the absence of such values is coded!
    String missingValueTreatmentStr = "";
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
    if(dt == Datatype.bool) defaultMissingValue = "false";
    else if(dt == Datatype.numeric) defaultMissingValue = "0.0";
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
    return att;
  }

  private FeatureSpecAttribute parseNgramAttribute(Element ngramElement, int i) {
    String aname = getChildTextOrElse(ngramElement,"NAME","").trim();
    String annType = getChildTextOrElse(ngramElement,"TYPE","").trim();
    String numberString = getChildTextOrElse(ngramElement,"NUMBER","1").trim();
    String featureName4Value = getChildTextOrElse(ngramElement,"FEATURENAME4VALUE","");
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
            feature,
            featureName4Value
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
   * it is a string, but case is preserved.
   * NOTE: this tries both the all-uppercase and the all-lowercase variant of the given name.
   */
  private static String getChildTextOrElse(Element parent, String name, String elseVal) {
    List<Element> children = parent.getChildren(name);
    if (children.size() > 1) {
      throw new GateRuntimeException("Element " + parent.getName() + " has more than one nested " + name + " element");
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

}
