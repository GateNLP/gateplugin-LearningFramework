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

import cc.mallet.topics.ParallelTopicModel;
import gate.Annotation;
import gate.AnnotationSet;
import java.net.URL;


import gate.Controller;
import gate.Document;
import gate.Factory;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.plugin.learningframework.data.CorpusRepresentation;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.AlgorithmClustering;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.engines.EngineMBTopicsLDA;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@CreoleResource(
        name = "LF_TrainTopicModel",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_TrainTopicModel",
        comment = "Train a topic model")
public class LF_TrainTopicModel extends LearningFrameworkPRBase {

  private static final long serialVersionUID = 3354214456596583124L;

  // Not used at the moment
  //private transient final Logger logger = Logger.getLogger(LF_TrainTopicModel.class.getCanonicalName());

  protected URL dataDirectory;

  @RunTime
  @Optional
  @CreoleParameter(comment = "The directory where all data will be stored and read from, if not specified, uses current running directory")
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
  
  private boolean applyAfterTraining = true;
  
  @RunTime
  @CreoleParameter(comment = "After training the model, calculate and apply topic distributions to the training documents",
          defaultValue="false")
  public void setApplyAfterTraining(Boolean val) {
    applyAfterTraining = val;
  }

  public Boolean getApplyAfterTraining() {
    return applyAfterTraining;
  }

  private String featurePrefix;
  
  @RunTime
  @Optional
  @CreoleParameter(comment="Prefix of the feature names written to the instance annotations", 
          defaultValue="LDA_")
  public void setFeaturePrefix(String val) {
    featurePrefix = val;
  }
  
  public String getFeaturePrefix() {
    return featurePrefix;
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
    AnnotationSet tokenAS;
    if(getTokenAnnotationType()==null || getTokenAnnotationType().isEmpty()) {
      tokenAS = inputAS.get("Token");
    } else {
      tokenAS = inputAS.get(getTokenAnnotationType());
    }
    AnnotationSet instanceAS = null;
    if (getInstanceType()!=null && !getInstanceType().isEmpty()) {
      instanceAS = inputAS.get(getInstanceType());
      //System.err.println("DEBUG: doc="+doc.getName()+", instance anns: "+instanceAS.size());
    }
    corpusRepresentation.add(instanceAS, null, tokenAS, null, getTokenFeature(), TargetType.NONE, null, null, null);
  }
  
  @Override
  public void controllerStarted(Controller controller) {
    if(dataDirectory==null) {
      try {
        dataDirectory = new File(".").toURI().toURL();
      } catch (MalformedURLException ex) {
        throw new GateRuntimeException("Could not create URL for current dirctory", ex);
      }
    }
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
    // AlgorithmClustering alg = getTrainingAlgorithm();
    // System.err.println("DEBUG: Before Document.");
    // System.err.println("  Training algorithm engine class is " + alg.getEngineClass());
    // System.err.println("  Training algorithm algor class is " + alg.getTrainerClass());

    if (getDuplicateId() == 0) {
      engine = Engine.create(trainingAlgorithm, getAlgorithmParameters(), null, TargetType.NONE, dataDirectory);
      corpusRepresentation = engine.getCorpusRepresentation();
      // System.err.println("DEBUG: created the engine: " + engine);
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
      engine.saveEngine(dataDirFile);
      
      // Now, if apply to training set was specified, AND we used the Mallet algorithm AND we only have one duplicate
      // AND the number of we just processed agrees with the number of documents in the corpus (i.e. we do not 
      // have a GCP-like process-one-document-at-a-time situation)
      // calculate the topic distribution for each of the documents.
      // CAUTION: this assumes that the order of documents in the corpus still agrees with the 
      // order of documents in the mallet model.
      if (trainingAlgorithm == AlgorithmClustering.MalletLDA_CLUS_MR && getApplyAfterTraining()) {
        EngineMBTopicsLDA engine_mbt = (EngineMBTopicsLDA)engine;
        ParallelTopicModel tm = engine_mbt.getTopicModel();
        if(nDuplicates.get() == 1 && corpus.size() == getSeenDocuments().get()) {
          System.out.println("INFO: re-processing corpus for application...");
          // List<TopicAssignment> tass = tm.getData();
          int n = 0; // this is the running index of the instances as seen by Mallet
          for(int docNr=0; docNr < corpus.size(); docNr++) {
            boolean documentWasLoaded = corpus.isDocumentLoaded(docNr);
            Document doc = corpus.get(docNr);
            AnnotationSet inputAS = doc.getAnnotations(getInputASName());            
            AnnotationSet instanceAS;
            if (getInstanceType() != null && !getInstanceType().isEmpty()) {
              instanceAS = inputAS.get(getInstanceType());
            } else {     
              // if the instance annotation set has not been specified, we put a Document annotation
              // into the inputAS, unless we already have one or more
              instanceAS = inputAS.get("Document");
              if (instanceAS.isEmpty()) {
                gate.Utils.addAnn(inputAS, 0, doc.getContent().size(), "Document", Factory.newFeatureMap());
                instanceAS = inputAS.get("Document");
              }
            }
            for (Annotation instAnn : instanceAS) {
              double[] tdist = tm.getTopicProbabilities(n);
              List<Double> tdistlist = new ArrayList<>(tdist.length);
              int i = 0;
              int bestTopic = -1;
              double bestProb = -999.99;
              for (double val : tdist) {
                tdistlist.add(val);
                if (val > bestProb) {
                  bestTopic = i;
                  bestProb = val;
                }
                i++;
              }
              String pref = getFeaturePrefix();
              if(pref==null) {
                pref = "";
              }
              instAnn.getFeatures().put(pref+"TopicDist", tdistlist);
              // Also add a feature that gives the index and word list of the most likely topic
              instAnn.getFeatures().put(pref+"BestTopic", bestTopic);
              instAnn.getFeatures().put(pref+"BestTopicProb", bestProb);              
              n++;
            }
            if(!documentWasLoaded) {
              corpus.unloadDocument(doc);
              Factory.deleteResource(doc);
            }
          }
          System.out.println("INFO: re-processing corpus for application finished.");
        } else {
          System.err.println("ERROR: cannot apply after training, either more than one duplicate or corpus size mismatch");
        }
        
      }
    }
  }

}
