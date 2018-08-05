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


import org.apache.log4j.Logger;

import gate.AnnotationSet;
import gate.Controller;
import gate.Document;
import gate.Factory;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.engines.EngineMBTopicsLDA;
import gate.util.GateRuntimeException;
import java.net.URL;

/**
 * <p>
 * Training, evaluation and application of ML in GATE.</p>
 */
@CreoleResource(name = "LF_ApplyTopicModel",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_ApplyTopicModel",
        comment = "Apply a trained topic model to document annotations")
public class LF_ApplyTopicModel extends LearningFrameworkPRBase {

  static final Logger LOGGER = Logger.getLogger(LF_ApplyTopicModel.class.getCanonicalName());
  private static final long serialVersionUID = 5851732674711579672L;

  protected URL dataDirectory;

  @RunTime
  @CreoleParameter(comment = "The directory where all data will be stored and read from")
  public void setDataDirectory(URL output) {
    dataDirectory = output;
  }

  public URL getDataDirectory() {
    return this.dataDirectory;
  }

  private String tokenAnnotationType = "Token";
  
  @RunTime
  @CreoleParameter(comment = "The annotation type representing the words/tokens to use",
          defaultValue = "Token")
  public void setTokenAnnotationType(String val) {
    tokenAnnotationType = val;
  }
  
  public String getTokenAnnotationType() {
    return tokenAnnotationType;
  }

  private String tokenFeature = "string";
  
  @RunTime
  @Optional
  @CreoleParameter(comment = "The feature containing the token string to use, if empty, use document content",
          defaultValue = "string")
  public void setTokenFeature(String val) {
    tokenFeature = val;
  }
  
  public String getTokenFeature() {
    return tokenFeature;
  }
  
  
////////////////////////////////////////////////////////////////////////////

  private transient Engine engine;

  private URL savedModelDirectoryURL;

  @Override
  public void process(Document doc) {
    if(isInterrupted()) {
      interrupted = false;
      throw new GateRuntimeException("Execution was requested to be interrupted");
    }
    // extract the required annotation sets,
    AnnotationSet inputAS = doc.getAnnotations(getInputASName());
    if(getTokenAnnotationType()==null || getTokenAnnotationType().isEmpty()) {
      inputAS = inputAS.get("Token");
    } else {
      inputAS = inputAS.get(getTokenAnnotationType());
    }
    AnnotationSet instanceAS = null;
    if (getInstanceType()!=null && !getInstanceType().isEmpty()) {
      instanceAS = inputAS.get(getInstanceType());
    } else {
      // if the instance annotation set has not been specified, we put a Document annotation
      // into the default set for now, unless we already have one or more.
      instanceAS = document.getAnnotations().get("Document");
      if(instanceAS.isEmpty()) {
        gate.Utils.addAnn(document.getAnnotations(), 0, doc.getContent().size(), "Document", Factory.newFeatureMap());
        instanceAS = document.getAnnotations().get("Document");
      } 
    }
    EngineMBTopicsLDA engineLDA = (EngineMBTopicsLDA)engine;
    engineLDA.applyTopicModel(
          instanceAS, inputAS,
          getTokenFeature(), getAlgorithmParameters());

  }


  @Override
  public void controllerStarted(Controller controller) {
    if (dataDirectory == null) {
      throw new GateRuntimeException("Parameter dataDirectory not set!");
    }
    if (savedModelDirectoryURL == null || !savedModelDirectoryURL.toExternalForm().equals(dataDirectory.toExternalForm())) {
      savedModelDirectoryURL = dataDirectory;
    }
    // Restore the Engine
    engine = Engine.load(savedModelDirectoryURL, getAlgorithmParameters());
    System.out.println("LF-Info: model loaded is now " + engine);

    if (engine.getModel() == null) {
      throw new GateRuntimeException("Do not have a model, something went wrong.");
      // System.err.println("WARNING: no internal model to apply, this is ok if an external model is used");
    } else {
      System.out.println("LearningFramework: Applying model "
              + engine.getModel().getClass() + " ...");
    }

  }


}
