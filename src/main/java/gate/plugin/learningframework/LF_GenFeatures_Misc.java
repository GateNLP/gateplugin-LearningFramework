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
package gate.plugin.learningframework;

import gate.Annotation;
import gate.AnnotationSet;

import org.apache.log4j.Logger;

import gate.Controller;
import gate.Document;
import gate.FeatureMap;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.util.GateRuntimeException;

/**
 *
 */
@CreoleResource(
        name = "LF_GenFeatures_Misc",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_GenFeatures_Misc",
        comment = "Generate various kinds of features")
public class LF_GenFeatures_Misc extends AbstractDocumentProcessor {

  private static final long serialVersionUID = -4854556257508853640L;

  private final Logger logger = Logger.getLogger(LF_GenFeatures_Misc.class.getCanonicalName());

  protected Boolean genWordShape = false;  
  @RunTime
  @CreoleParameter(
          comment = "Generate word shape",
          defaultValue = "false")
  public void setGenWordShape(Boolean val) {
    genWordShape = val;
  }
  public Boolean getGenWordShape() {
    return genWordShape;
  }
  protected Boolean genWordShapeShort = true;  
  @RunTime
  @CreoleParameter(
          comment = "Generate short word shape",
          defaultValue = "true")
  public void setGenWordShapeShort(Boolean val) {
    genWordShapeShort = val;
  }
  public Boolean getGenWordShapeShort() {
    return genWordShapeShort;
  }

  

  protected String inputASName;

  @RunTime
  @Optional
  @CreoleParameter
  public void setInputASName(String iasn) {
    this.inputASName = iasn;
  }

  public String getInputASName() {
    return this.inputASName;
  }

  protected String instanceType;

  @RunTime
  @CreoleParameter(defaultValue = "Token", comment = "The annotation type to "
          + "be treated as instance.")
  public void setInstanceType(String inst) {
    this.instanceType = inst;
  }

  public String getInstanceType() {
    return this.instanceType;
  }

  protected String stringFeature = "";
  @RunTime
  @Optional
  @CreoleParameter(defaultValue = "", 
          comment = "Where to take the word string from, empty means underlying document string"
  )
  public void setStringFeature(String val) {
    stringFeature = val;
  }
  public String getStringFeature() {
    return stringFeature;
  }
  
  
  
  @Override
  public Document process(Document doc) {
    if(isInterrupted()) {
      interrupted = false;
      throw new GateRuntimeException("Execution was requested to be interrupted");
    }
    // extract the required annotation sets,
    AnnotationSet inputAS = doc.getAnnotations(getInputASName());
    AnnotationSet instanceAS = inputAS.get(getInstanceType());
    for(Annotation ann : instanceAS) {
      FeatureMap fm = ann.getFeatures();
      String string;
      if(getStringFeature().isEmpty()) {
        string = gate.Utils.stringFor(document, ann);
      } else {
        string = (String)fm.get(getStringFeature());
      }
      if(string == null) {
        string = "";
      }
      if(getGenWordShape()) {
        char[] arr = string.toCharArray();
        char[] out = new char[arr.length];
        for(int i = 0; i<arr.length; i++) {
          char c = arr[i];
          if(Character.isLetter(c)) {
            if(Character.isUpperCase(c)) {
              out[i]='A';
            } else {
              out[i]='a';
            }
          }
          else if(Character.isDigit(c)) {
            out[i]='9';
          } else {
            out[i] = c;
          }
        }
        String shape = new String(out);
        fm.put("wordShape",shape);
      } // if genWordShape
      if(getGenWordShapeShort()) {
        char last = (char)0;
        int len = 0;
        char[] arr = string.toCharArray();
        char[] out = new char[arr.length];
        char tmp;
        for(int i = 0; i<arr.length; i++) {
          char c = arr[i];
          if(Character.isLetter(c)) {
            if(Character.isUpperCase(c)) {
              tmp='A';
            } else {
              tmp='a';
            }
          }
          else if(Character.isDigit(c)) {
            tmp='9';
          } else {
            tmp = c;
          }
          if(tmp!=last) {
            out[len++] = tmp;
            last = tmp;
          }
        }
        String shape = new String(out, 0, len);
        fm.put("wordShapeShort",shape);
      } // if genWordShapeShort
      
    }
    return doc;
  }

  
  @Override
  public void afterLastDocument(Controller arg0, Throwable t) {
  }

  @Override
  protected void finishedNoDocument(Controller c, Throwable t) {
    logger.error("Processing finished, but got an error, no documents seen, or the PR was disabled in the pipeline - cannot train!");
  }

  @Override
  protected void beforeFirstDocument(Controller controller) {
    if(!getGenWordShape() && !getGenWordShapeShort()) {
      throw new GateRuntimeException("Should generate something!");
    }
  }

}
