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

import gate.AnnotationSet;
import java.net.URL;

import org.apache.log4j.Logger;

import gate.Controller;
import gate.Document;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.plugin.learningframework.data.CorpusRepresentation;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.AlgorithmClustering;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;
import java.io.File;

@CreoleResource(
        name = "LF_TrainTopicModel",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_TrainTopicModel",
        comment = "Train a topic model")
public class LF_TrainTopicModel extends LearningFrameworkPRBase {

  private static final long serialVersionUID = 3354214456596583124L;

  private transient final Logger logger = Logger.getLogger(LF_TrainTopicModel.class.getCanonicalName());

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
  
  private AlgorithmClustering trainingAlgorithm;

  @RunTime
  @Optional
  @CreoleParameter(comment = "The topic modeling algorithm to be used for training.")
  public void setTrainingAlgorithm(AlgorithmClustering algo) {
    this.trainingAlgorithm = algo;
  }

  public AlgorithmClustering getTrainingAlgorithm() {
    return this.trainingAlgorithm;
  }


  private transient CorpusRepresentation corpusRepresentation = null;

  private transient Engine engine = null;

  private File dataDirFile;

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
    }
    corpusRepresentation.add(instanceAS, null, inputAS, null, getTokenFeature(), TargetType.NONE, null, null, null);
  }
  
  @Override
  public void controllerStarted(Controller controller) {
    if("file".equals(dataDirectory.getProtocol())) {
      dataDirFile = gate.util.Files.fileFromURL(dataDirectory);
    } else {
      throw new GateRuntimeException("Training is only possible if the dataDirectory URL is a file: URL");
    }
    if(!dataDirFile.exists()) {
      throw new GateRuntimeException("Data directory not found: "+dataDirFile.getAbsolutePath());
    }

    
    if (getTrainingAlgorithm() == null) {
      throw new GateRuntimeException("LearningFramework: no training algorithm specified");
    }
    AlgorithmClustering alg = getTrainingAlgorithm();

    System.err.println("DEBUG: Before Document.");
    System.err.println("  Training algorithm engine class is " + alg.getEngineClass());
    System.err.println("  Training algorithm algor class is " + alg.getTrainerClass());

    if (getDuplicateId() == 0) {
      engine = Engine.create(trainingAlgorithm, getAlgorithmParameters(), null, TargetType.NONE, dataDirectory);
      corpusRepresentation = engine.getCorpusRepresentation();
      System.err.println("DEBUG: created the engine: " + engine);
      getSharedData().put("engine", engine);
      getSharedData().put("corpusRepresentation", corpusRepresentation);
    } else {
      // duplicateId > 0
      engine = (Engine) getSharedData().get("engine");
      corpusRepresentation = (CorpusRepresentation) getSharedData().get("corpusRepresentation");
    }
  }
  

  @Override
  public void controllerFinished(Controller arg0, Throwable t) {
    if(t!=null) {
      System.err.println("An exception occurred during processing of documents, no training will be done");
      System.err.println("Exception was "+t.getClass()+": "+t.getMessage());
      return;
    }
    if(getSeenDocuments().get()==0) {
      throw new GateRuntimeException("No documents seen, cannot train");
    }
    if (getDuplicateId() == 0) {
      System.out.println("LearningFramework: Starting training engine " + engine);
      if (corpusRepresentation instanceof CorpusRepresentationMallet) {
        CorpusRepresentationMallet crm = (CorpusRepresentationMallet) corpusRepresentation;
        System.out.println("Training set size: " + crm.getRepresentationMallet().size());
        if (crm.getRepresentationMallet().getDataAlphabet().size() > 20) {
          System.out.println("LearningFramework: Attributes " + crm.getRepresentationMallet().getDataAlphabet().size());
        } else {
          System.out.println("LearningFramework: Attributes " + crm.getRepresentationMallet().getDataAlphabet().toString().replaceAll("\\n", " "));
        }
      }

      engine.getInfo().nrTrainingInstances = corpusRepresentation.nrInstances();

      // Store some additional information in the info datastructure which will be saved with the model
      engine.getInfo().nrTrainingDocuments = getSeenDocuments().get();
      engine.getInfo().targetFeature = null;
      engine.getInfo().trainingCorpusName = corpus.getName();

      engine.trainModel(gate.util.Files.fileFromURL(dataDirectory),
              getInstanceType(),
              getAlgorithmParameters());
      logger.info("LearningFramework: Training complete!");
      engine.saveEngine(dataDirFile);
    }
  }

}
