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

import java.util.List;

import org.apache.log4j.Logger;

import gate.AnnotationSet;
import gate.Controller;
import gate.Document;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.plugin.learningframework.engines.AlgorithmKind;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.engines.EngineMBServer;
import gate.util.GateRuntimeException;
import java.net.URL;

/**
 * <p>
 * Training, evaluation and application of ML in GATE.</p>
 */
@CreoleResource(name = "LF_ApplyClassification",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_ApplyClassification",
        comment = "Apply a trained classification model to documents")
public class LF_ApplyClassification extends LearningFrameworkPRBase {

  static final Logger LOGGER = Logger.getLogger(LF_ApplyClassification.class.getCanonicalName());
  private static final long serialVersionUID = -754439854542759988L;

  protected URL dataDirectory;
  protected URL oldDataDirectory;

  @RunTime
  @CreoleParameter(comment = "The directory where all data will be stored and read from")
  public void setDataDirectory(URL output) {
    dataDirectory = output;
  }

  public URL getDataDirectory() {
    return this.dataDirectory;
  }

  protected String outputASName;

  @RunTime
  @Optional
  @CreoleParameter(comment = "If identical to the input AS, existing annotations are updated",
          defaultValue = "LearningFramework")
  public void setOutputASName(String oasn) {
    this.outputASName = oasn;
  }

  public String getOutputASName() {
    return this.outputASName;
  }

  /**
   * The confidence threshold for applying an annotation. In the case of NER, the confidence
   * threshold is applied to the average for the entire entity.
   *
   */
  private Double confidenceThreshold;

  @RunTime
  @Optional
  @CreoleParameter(comment = "The minimum "
          + "confidence/probability for including "
          + "an annotation at application time. If empty, ignore.")
  public void setConfidenceThreshold(Double confidenceThreshold) {
    this.confidenceThreshold = confidenceThreshold;
  }

  public Double getConfidenceThreshold() {
    return this.confidenceThreshold;
  }
  
  protected String targetFeature;

  @RunTime
  @Optional
  @CreoleParameter(comment = "Name of class feature to add to the original "
          + "instance annotations. Default is the name that was used for training.",
          defaultValue = "")
  public void setTargetFeature(String name) {
    targetFeature = name;
  }

  public String getTargetFeature() {
    return targetFeature;
  }

  private String sequenceSpan;

  @RunTime
  @Optional
  @CreoleParameter(comment = "For sequence learners, an annotation type "
          + "defining a meaningful sequence span. Ignored by non-sequence "
          + "learners. ")
  public void setSequenceSpan(String seq) {
    sequenceSpan = seq;
  }

  public String getSequenceSpan() {
    return sequenceSpan;
  }

  protected String serverUrl;

  @RunTime
  @Optional
  @CreoleParameter(comment = "Classify from a server instead of a stored model")
  public void setServerUrl(String url) {
    serverUrl = url;
  }

  public String getServerUrl() {
    return serverUrl;
  }

////////////////////////////////////////////////////////////////////////////
  private Engine engine;

  // this is either what the user specifies as the PR parameter, or what we have stored 
  // with the saved model.
  private String targetFeatureToUse;

  @Override
  public void process(Document doc) {
    if (isInterrupted()) {
      interrupted = false;
      throw new GateRuntimeException("Execution was requested to be interrupted");
    }
    // extract the required annotation sets,
    AnnotationSet inputAS = doc.getAnnotations(getInputASName());
    AnnotationSet instanceAS = inputAS.get(getInstanceType());
    // the classAS must be null for classification
    // the sequenceAS must be specified for a sequence tagging algorithm and most not be specified
    // for a non-sequence tagging algorithm!
    AnnotationSet sequenceAS = null;
    if (engine.getAlgorithm().getAlgorithmKind() == AlgorithmKind.SEQUENCE_TAGGER) {
      // NOTE: we already have checked earlier, that in that case, the sequenceSpan parameter is 
      // given!
      sequenceAS = inputAS.get(getSequenceSpan());
    }

    //System.out.println("LF_ApplyClassification debug: instanceAS="+instanceAS.size()+", inputAS="+inputAS.size()+
    //  ", sequenceAS="+sequenceAS);
    //if(sequenceAS == null) {
    //  System.err.println("DEBUG: classifying doc "+doc.getName()+" instanceAS:"+instanceAS.size()+", inputAS:"+inputAS.size());
    //} else {
    //  System.err.println("DEBUG: classifying doc "+doc.getName()+" instanceAS:"+instanceAS.size()+", inputAS:"+inputAS.size()+", sequenceAS:"+sequenceAS.size());      
    //}
    List<ModelApplication> gcs = engine.applyModel(
            instanceAS, inputAS,
            sequenceAS, getAlgorithmParameters());

    // If the outputSet is the same as the inputSet, we do not create new 
    // annotations
    // So if they are both null or both the same non-null value we leave the outputAS null, otherwise we 
    // set it to something (in the case of null, the default set).
    AnnotationSet outputAS = null;
    if (getOutputASName() == null && getInputASName() == null
            || getOutputASName() != null && getInputASName() != null && getOutputASName().equals(getInputASName())) {
    } else {
      outputAS = doc.getAnnotations(getOutputASName());
    }

    ModelApplication.applyClassification(doc, gcs, targetFeatureToUse, outputAS, getConfidenceThreshold());
  }

  @Override
  public void controllerStarted(Controller controller) {

    // If a server URL is specified, use the server engine. In that case the 
    // data directory only needs to contain an info file, but most of the information
    // in the info file is ignored. 
    // For now, only the target feature is used if it is not specified as a runtime parameter.
    // For now, the server does not support sequence taggers, so the sequence annotation must
    // be empty 
    if (serverUrl != null && !serverUrl.isEmpty()) {
      if (getSequenceSpan() != null && !getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("Sequence span not supported for server");
      }
      engine = new EngineMBServer(dataDirectory, serverUrl);
    } else {

      // if the engine is still null, or the dataDirectory has changed since 
      // we last loaded the engine, or the algorithmParameters were changed,
      // reload the engine.
      if (engine == null || !dataDirectory.toString().equals(oldDataDirectory.toString()) || getAlgorithmParametersIsChanged()) {
        oldDataDirectory = dataDirectory;
        engine = Engine.load(dataDirectory, getAlgorithmParameters());
      }
      System.out.println("LF-Info: loaded model is " + engine);

      if (engine.getModel() == null) {
        // This is really only an error if we do not have some kind of wrapped algorithm
        // where the model is handled externally.
        // For now, we just show a warning.
        // throw new GateRuntimeException("Do not have a model, something went wrong.");
        System.err.println("WARNING: no internal model to apply, this is ok if an external model is used");
      } else {
        System.out.println("LearningFramework: Applying model "
                + engine.getModel().getClass() + " ...");
      }

      if (engine.getAlgorithm().getAlgorithmKind() == AlgorithmKind.SEQUENCE_TAGGER) {
        if (getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
          throw new GateRuntimeException("sequenceSpan parameter must not be empty when a sequence tagging algorithm is used for classification");
        }
      }
    }

    if (getTargetFeature() == null || getTargetFeature().isEmpty()) {
      // try to get the target feature from the model instead
      String targetFeatureFromModel = engine.getInfo().targetFeature;
      if (targetFeatureFromModel == null || targetFeatureFromModel.isEmpty()) {
        throw new GateRuntimeException("Not targetFeature parameter specified and none available from the model info file either.");
      }
      targetFeatureToUse = targetFeatureFromModel;
      LOGGER.warn("Using target feature name from model: " + targetFeatureToUse);
    } else {
      targetFeatureToUse = getTargetFeature();
      LOGGER.warn("Using target feature name from PR parameter: " + targetFeatureToUse);
    }
    LOGGER.debug("Parameter confidenceThreshold not given, not using confidence threshold");
  }


}
