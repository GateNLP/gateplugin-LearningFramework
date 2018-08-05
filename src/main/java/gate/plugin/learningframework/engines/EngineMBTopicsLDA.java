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

package gate.plugin.learningframework.engines;

import cc.mallet.classify.Classification;
import cc.mallet.classify.Classifier;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicModelDiagnostics;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelVector;
import cc.mallet.types.Labeling;
import gate.Annotation;
import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.ModelApplication;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import static gate.plugin.learningframework.engines.Engine.FILENAME_MODEL;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import static gate.plugin.learningframework.LFUtils.newURL;
import gate.plugin.learningframework.data.CorpusRepresentationMalletLDA;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;

/**
 *
 * @author Johann Petrak
 */
public class EngineMBTopicsLDA extends EngineMBMallet {

  private static final Logger LOGGER = Logger.getLogger(EngineMBTopicsLDA.class);

  protected TopicModelDiagnostics tmd;
  
  public EngineMBTopicsLDA() { }

  @Override
  public void trainModel(File dataDirectory, String instanceType, String parms) {
    System.err.println("EngineMalletClass.trainModel: trainer="+trainer);
    System.err.println("EngineMalletClass.trainModel: CR="+corpusRepresentation);
    
    // TODO: get the most important parameters from the parms or use defaults
    int nrTopics = 10;
    double alpha = 1.0;
    double beta = 0.01;
    int displayInterval = 200;
    int displayTopicWords = 10;
    int numThreads = 4;
    int numIterations = 100;
    ParallelTopicModel tm = new ParallelTopicModel(nrTopics, alpha, beta);
    model= tm;    
    tm.setTopicDisplay(displayInterval, displayTopicWords);
    tm.setNumThreads(numThreads);
    tm.setNumIterations(numIterations);
    tm.addInstances(corpusRepresentation.getRepresentationMallet());
    try {
      tm.estimate();
    } catch (IOException ex) {
      throw new GateRuntimeException("Exception during training of model", ex);
    }    
    tmd = new TopicModelDiagnostics(tm, displayTopicWords);
    System.out.println("Topic Model Coherence: "+Arrays.toString(tmd.getCoherence().scores));
    updateInfo();
  }

  @Override
  protected void saveModel(File directory) {
    super.saveModel(directory);
    // in addition to saving the model, we also write out some additional files
    // generated from the diagnostics
    try (
            PrintWriter pw = new PrintWriter(new File(directory, "diagnostics.xml"), "UTF-8");
        )
    {
      pw.print(tmd.toXML());
    } catch (Exception ex)  {
      throw new GateRuntimeException("Exception when writing diagnostics.xml",ex);
    }
  }  
  
  
  @Override
  protected void loadAndSetCorpusRepresentation(URL directory) {
    if(corpusRepresentation==null) {
      corpusRepresentation = CorpusRepresentationMalletLDA.load(directory);
    }
  }
  

  @Override
  protected void loadModel(URL directory, String parms) {
    URL modelFile = newURL(directory, FILENAME_MODEL);
    Classifier classifier;
    ObjectInputStream ois;
    try (InputStream is = modelFile.openStream()) {
      ois = new ObjectInputStream(is);
      classifier = (Classifier) ois.readObject();
      model=classifier;
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not load Mallet model", ex);
    }
  }
  
  
  @Override
  public List<ModelApplication> applyModel(
          AnnotationSet instanceAS, AnnotationSet inputAS, AnnotationSet sequenceAS, String parms) {
    // TODO!!!!!!
    // NOTE: the crm should be of type CorpusRepresentationMalletClass for this to work!
    if(!(corpusRepresentation instanceof CorpusRepresentationMalletTarget)) {
      throw new GateRuntimeException("Cannot perform classification with data from "+corpusRepresentation.getClass());
    }
    CorpusRepresentationMalletTarget data = (CorpusRepresentationMalletTarget)corpusRepresentation;
    data.stopGrowth();
    List<ModelApplication> gcs = new ArrayList<>();
    LFPipe pipe = (LFPipe)data.getRepresentationMallet().getPipe();
    Classifier classifier = (Classifier)model;
    // iterate over the instance annotations and create mallet instances 
    for(Annotation instAnn : instanceAS.inDocumentOrder()) {
      Instance inst = data.extractIndependentFeatures(instAnn, inputAS);
      inst = pipe.instanceFrom(inst);
      Classification classification = classifier.classify(inst);
      Labeling labeling = classification.getLabeling();
      LabelVector labelvec = labeling.toLabelVector();
      List<String> classes = new ArrayList<>(labelvec.numLocations());
      List<Double> confidences = new ArrayList<>(labelvec.numLocations());
      for(int i=0; i<labelvec.numLocations(); i++) {
        classes.add(labelvec.getLabelAtRank(i).toString());
        confidences.add(labelvec.getValueAtRank(i));
      }      
      ModelApplication gc = new ModelApplication(instAnn, labeling.getBestLabel().toString(), 
              labeling.getBestValue(), classes, confidences);
      //System.err.println("ADDING GC "+gc);
      // now save the class in our special class feature on the instance as well
      instAnn.getFeatures().put("gate.LF.target",labeling.getBestLabel().toString());
      gcs.add(gc);
    }
    data.startGrowth();
    return gcs;
  }

  @Override
  public void initializeAlgorithm(Algorithm algorithm, String parms) {
    // NOTE: for LDA we do not do anything in here, everything happens in the train() method
  }

  @Override
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    throw new GateRuntimeException("Method evaluate not available for EngineMBTopicsLDA");
  }
  

}
