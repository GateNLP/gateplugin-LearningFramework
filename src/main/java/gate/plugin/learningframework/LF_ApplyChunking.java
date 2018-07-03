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
import gate.plugin.learningframework.features.SeqEncoder;
import gate.util.GateRuntimeException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;

/**
 * <p>
 * Training, evaluation and application of ML in GATE.</p>
 */
@CreoleResource(name = "LF_ApplyChunking",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_ApplyChunking",
        comment = "Apply a trained chunking model to documents")
public class LF_ApplyChunking extends LearningFrameworkPRBase {


  static final Logger LOGGER = Logger.getLogger(LF_ApplyClassification.class.getCanonicalName());
  private static final long serialVersionUID = -5955373184542660323L;

  protected URL dataDirectory;

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
  @CreoleParameter(defaultValue = "LearningFramework")
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
          + "an annotation at application time. In the case of NER, the confidence "
          + "threshold is applied to the average for the entire entity. If empty, ignore.")
  public void setConfidenceThreshold(Double confidenceThreshold) {
    this.confidenceThreshold = confidenceThreshold;
  }

  public Double getConfidenceThreshold() {
    return this.confidenceThreshold;
  }


  protected String sequenceSpan;
  
  @RunTime
  @Optional
  @CreoleParameter(comment = "For sequence learners, an annotation type "
          + "defining a meaningful sequence span. Ignored by non-sequence "
          + "learners. Needs to be in the input AS.")
  public void setSequenceSpan(String seq) {
    sequenceSpan = seq;
  }

  public String getSequenceSpan() {
    return sequenceSpan;
  }
  
  private SeqEncoder seqEncoder;
  

////////////////////////////////////////////////////////////////////////////

  private Engine engine;

  private URL dataDir;


  @Override
  public void process(Document doc) {
    if(isInterrupted()) {
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
    if(engine.getAlgorithm().getAlgorithmKind()==AlgorithmKind.SEQUENCE_TAGGER) {
      // NOTE: we already have checked earlier, that in that case, the sequenceSpan parameter is 
      // given!
      sequenceAS = inputAS.get(getSequenceSpan());
    }
    //System.err.println("instanceAS.size="+instanceAS.size()+", inputAS.size="+inputAS.size()+"sequenceAS.size="+
    //        sequenceAS.size());
    List<ModelApplication> gcs = engine.applyModel(
          instanceAS, inputAS,
          sequenceAS, getAlgorithmParameters());

    AnnotationSet tmpAS = doc.getAnnotations("LF_SEQ_TMP");
    // since we specify the output annotation set tmpAS, new instance annotations are created there
    String featureName = engine.getInfo().targetFeature;    
    ModelApplication.applyClassification(doc, gcs, Globals.outputClassFeature, tmpAS, null);
    AnnotationSet tmpInstanceAS = tmpAS.get(getInstanceType());
    AnnotationSet outputAS = doc.getAnnotations(getOutputASName());
    String classAnnotationType = engine.getInfo().classAnnotationType;
    
    ModelApplication.addSurroundingAnnotations(tmpAS, tmpInstanceAS, outputAS, classAnnotationType, getConfidenceThreshold(), seqEncoder);
  }

  @Override
  public void controllerStarted(Controller controller) {

    // Shared data gets initialized only once for the initial instance,
    // non-shared data gets initialized for each duplicate separately
    if(getDuplicateId()==0) {
      if(dataDir==null || !dataDir.toExternalForm().equals(dataDirectory.toExternalForm())) {
       dataDir = dataDirectory;
       // Restore the Engine
       engine = gate.plugin.learningframework.engines.Engine.load(dataDir, getAlgorithmParameters());
       System.out.println("LF-Info: model loaded is now: "+engine);
     } else {
       System.out.println("LF-Info: re-using already loaded model: "+engine);
     }    
     // TODO: store the reference to the engine in the shared data map
     getSharedData().put("engine", engine);
    } else {
      engine = (Engine)getSharedData().get("engine");
    }

    String secn = engine.getInfo().seqEncoderClass;
    String seco = engine.getInfo().seqEncoderOptions;
    
    try {
      @SuppressWarnings("unchecked")
      Constructor<?> tmpc = Class.forName(secn).getDeclaredConstructor();
      seqEncoder = (SeqEncoder) tmpc.newInstance();
    } catch (ClassNotFoundException | IllegalAccessException | 
            IllegalArgumentException | InstantiationException | 
            NoSuchMethodException | SecurityException | InvocationTargetException ex) {
      throw new GateRuntimeException("Could not create SeqEncoder instance",ex);
    }
    
    if (engine.getModel() == null) {
        // This is really only an error if we do not have some kind of wrapped algorithm
        // where the model is handled externally.
        // For now, we just show a warning.
        // throw new GateRuntimeException("Do not have a model, something went wrong.");
        System.err.println("WARNING: no internal model to apply, this is ok if an external model is used");
        //throw new GateRuntimeException("Do not have a model, something went wrong.");
    } else {
      System.out.println("LearningFramework: Applying model "
              + engine.getModel().getClass() + " ...");
    }
    
    if(engine.getAlgorithm().getAlgorithmKind() == AlgorithmKind.SEQUENCE_TAGGER) {
      if(getSequenceSpan() == null || getSequenceSpan().isEmpty()) {
        throw new GateRuntimeException("sequenceSpan parameter must not be empty when a sequence tagging algorithm is used for classification");
      }
    }
  }
  

}
