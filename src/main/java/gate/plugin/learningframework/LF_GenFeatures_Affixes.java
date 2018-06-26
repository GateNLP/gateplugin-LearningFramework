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

import org.jpetrak.gate8.api.plugins.AbstractDocumentProcessor;
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
import java.util.Locale;

/**
 *
 */
@CreoleResource(
        name = "LF_GenFeatures_Affixes",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_GenFeatures_Affixes",
        comment = "Generate features: prefixes and suffixes")
public class LF_GenFeatures_Affixes extends AbstractDocumentProcessor {

  private static final long serialVersionUID = 7672148279436444950L;

  private final Logger logger = Logger.getLogger(LF_GenFeatures_Affixes.class.getCanonicalName());

  protected Boolean genPrefixes = false;  
  @RunTime
  @CreoleParameter(
          comment = "If prefixes should get generated",
          defaultValue = "false")
  public void setGenPrefixes(Boolean val) {
    genPrefixes = val;
  }
  public Boolean getGenPrefixes() {
    return genPrefixes;
  }

  protected String prefixFeatureName = "pref";
  @RunTime
  @CreoleParameter(
          comment = "The name pattern to use for the feature names for prefixes",
          defaultValue = "pref"
  )
  public void setPrefixFeatureName(String val) {
    prefixFeatureName = val;
  }
  public String getPrefixFeatureName() {
    return prefixFeatureName;
  }
  
  protected Integer minPrefixLength = 2;
  @RunTime
  @CreoleParameter(
          comment = "Minimum number of characters of a prefix to get generated",
          defaultValue = "2"
  )
  public void setMinPrefixLength(Integer val) {
    minPrefixLength = val;
  }
  public Integer getMinPrefixLength() {
    return minPrefixLength;
  }
  
  protected Integer maxPrefixLength = 4;
  @RunTime
  @CreoleParameter(
          comment = "Maximum number of characters of a prefix to get generated",
          defaultValue = "4"
  )
  public void setMaxPrefixLength(Integer val) {
    maxPrefixLength = val;
  }
  public Integer getMaxPrefixLength() {
    return maxPrefixLength;
  }
  
  protected Integer minNonPrefixLength = 2;
  @RunTime
  @CreoleParameter(
          comment = "Minimum number of non prefix characters that must remain for a prefix to get generated",
          defaultValue = "2"
  )
  public void setMinNonPrefixLength(Integer val) {
    minNonPrefixLength = val;
  }
  public Integer getMinNonPrefixLength() { 
    return minNonPrefixLength;
  }
    
  

  protected Boolean genSuffixes = false;  
  @RunTime
  @CreoleParameter(
          comment = "If suffixes should get generated",
          defaultValue = "false")
  public void setGenSuffixes(Boolean val) {
    genSuffixes = val;
  }
  public Boolean getGenSuffixes() {
    return genSuffixes;
  }
  
  protected String suffixFeatureName = "suf";
  @RunTime
  @CreoleParameter(
          comment = "The name pattern to use for the feature names for suffixes",
          defaultValue = "suf"
  )
  public void setSuffixFeatureName(String val) {
    suffixFeatureName = val;
  }
  public String getSuffixFeatureName() {
    return suffixFeatureName;
  }
  
  protected Integer minSuffixLength = 2;
  @RunTime
  @CreoleParameter(
          comment = "Minimum number of characters of a suffix to get generated",
          defaultValue = "2"
  )
  public void setMinSuffixLength(Integer val) {
    minSuffixLength = val;
  }
  public Integer getMinSuffixLength() {
    return minSuffixLength;
  }
  
  protected Integer maxSuffixLength = 4;
  @RunTime
  @CreoleParameter(
          comment = "Maximum number of characters of a suffix to get generated",
          defaultValue = "4"
  )
  public void setMaxSuffixLength(Integer val) {
    maxSuffixLength = val;
  }
  public Integer getMaxSuffixLength() {
    return maxSuffixLength;
  }
  
  protected Integer minNonSuffixLength = 2;
  @RunTime
  @CreoleParameter(
          comment = "Minimum number of non suffix characters that must remain for a suffix to get generated",
          defaultValue = "2"
  )
  public void setMinNonSuffixLength(Integer val) {
    minNonSuffixLength = val;
  }
  public Integer getMinNonSuffixLength() { 
    return minNonSuffixLength;
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
  
  protected Boolean mapToUpper = false;
  @RunTime
  @CreoleParameter(
          defaultValue = "false",
          comment = "Fals: Use original string, true: first map to all upper case"
          )
  public void setMapToUpper(Boolean val) {
    mapToUpper = val;
  }
  public Boolean getMapToUpper() {
    return mapToUpper;
  }
  
  protected String mappingLanguage = "en";
  protected Locale mappingLocale = Locale.ENGLISH;
  @RunTime
  @Optional
  @CreoleParameter(
          comment = "Language to use for mapping to upper case, if empty, use system setting",
          defaultValue = "en"
  )
  public void setMappingLanguage(String val) {
    mappingLanguage = val;
    mappingLocale = new Locale(val);
  }
  public String getMappingLanguage() {
    return mappingLanguage;
  }
  
  @Override
  public void process(Document doc) {
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
      // NOTE: the upper-case normalization can change the length of the string
      // but we are deferring this to later, assuming that the length will only
      // increase, not decrease, hoping for a little performance improvement.
      
      // only do the prefix processing thing if the word is long enough
      if(getGenPrefixes() && (string.length() >= getMinPrefixLength()+getMinNonPrefixLength())) {
        // forst convert to the uppercased mapping if necessary
        if(getMapToUpper()) {
          string = string.toUpperCase(mappingLocale);
        }
        int max = string.length() - getMinNonPrefixLength();
        for(int i = getMinPrefixLength(); i <= max; i++) {
          fm.put(getPrefixFeatureName()+i, string.substring(0,i));
        }
      } // if long enough for prefix
      // only do the suffix processing thing if the word is long enough
      if(getGenSuffixes() && (string.length() >= getMinSuffixLength()+getMinNonSuffixLength())) {
        if(getMapToUpper()) {
          string = string.toUpperCase(mappingLocale);
        }
        int len = string.length();
        int max = len - getMinNonSuffixLength();
        for(int i = getMinSuffixLength(); i <= max; i++) {
          fm.put(getSuffixFeatureName()+i, string.substring(len-i));
        }        
      } // if long enough for suffix
    }
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
    if(!getGenPrefixes() && !getGenSuffixes()) {
      throw new GateRuntimeException("Should generate something!");
    }
  }

}
