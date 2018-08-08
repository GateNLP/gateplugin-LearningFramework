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

import cc.mallet.classify.Classifier;
import cc.mallet.topics.ParallelTopicModel;
import cc.mallet.topics.TopicInferencer;
import cc.mallet.topics.TopicModelDiagnostics;
import cc.mallet.types.Instance;
import gate.Annotation;
import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.ModelApplication;
import static gate.plugin.learningframework.engines.Engine.FILENAME_MODEL;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import static gate.plugin.learningframework.LFUtils.newURL;
import gate.plugin.learningframework.data.CorpusRepresentationMalletLDA;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 *
 * @author Johann Petrak
 */
public class EngineMBTopicsLDA extends EngineMBMallet {

  // not used for now
  // private static final Logger LOGGER = Logger.getLogger(EngineMBTopicsLDA.class);

  protected TopicModelDiagnostics tmd;
  protected ParallelTopicModel tm;
  
  public ParallelTopicModel getTopicModel() {
    return tm;
  }
  public TopicModelDiagnostics getTopicModelDiagnostics() {
    return tmd;
  }
  
  public EngineMBTopicsLDA() { }

  @Override
  public void trainModel(File dataDirectory, String instanceType, String parmString) {
    
    // TODO: at this point we could maybe remove low-frequency words.
    // This would be possible by adding a vocab/stats object to count each 
    // entry in the alphabet and then going through the instances, using
    // the FeatureSequence.prune method
    
    
    System.err.println("EngineMalletClass.trainModel: trainer="+trainer);
    System.err.println("EngineMalletClass.trainModel: CR="+corpusRepresentation);
    

    int displayInterval = 200;
    int numIterations = 100;
    // default values for those which can be set 
    int nrTopics = 10;
    double alpha = 1.0;
    double beta = 0.01;
    int showNrTopWords = 20;
    int showNrDocs = 5;
    int numThreads = Runtime.getRuntime().availableProcessors();
    if(numThreads<1) {
      numThreads = 1;
    }
    int seed = 0;
    Parms parmdef = new Parms(parmString,
                "t:topics:i",
                "p:procs:i",
                "w:words:i",
                "d:docs:i",
                "s:seed:i",
                "a:alpha:d",
                "b:beta:d"
    );
    nrTopics = (int) parmdef.getValueOrElse("topics", nrTopics);
    alpha = (double) parmdef.getValueOrElse("alpha", alpha);
    beta = (double) parmdef.getValueOrElse("beta", beta);
    showNrTopWords = (int) parmdef.getValueOrElse("words", showNrTopWords);
    showNrDocs = (int) parmdef.getValueOrElse("docs", showNrDocs);
    numThreads = (int) parmdef.getValueOrElse("procs", numThreads);
    seed = (int) parmdef.getValueOrElse("seed", seed);
    
    System.out.println("INFO: running Mallet LDA with parameters: topics="+nrTopics+
            ",alpha="+alpha+",beta="+beta+",words="+showNrTopWords+",procs="+numThreads+
            ",docs="+showNrDocs+",seed="+seed);
    
    tm = new ParallelTopicModel(nrTopics, alpha, beta);
    model= tm;    
    tm.setTopicDisplay(displayInterval, showNrTopWords);
    tm.setNumThreads(numThreads);
    tm.setNumIterations(numIterations);
    tm.setRandomSeed(seed);
    // For showing top documents see implementation of 
    // tm.printTopicDocuments(printwriter, showNrDocs);
    tm.addInstances(corpusRepresentation.getRepresentationMallet());
    try {
      tm.estimate();
    } catch (IOException ex) {
      throw new GateRuntimeException("Exception during training of model", ex);
    }    
    Object[][] topWords = tm.getTopWords(showNrTopWords);
    for(int i = 0; i<nrTopics; i++) {
      System.out.print("Topic-"+i+": ");
      for(int j = 0; j<showNrTopWords; j++) {
        System.out.print(topWords[i][j]);
        System.out.print(" ");
      }
      System.out.println();
}
    
    tmd = new TopicModelDiagnostics(tm, corpusRepresentation.getRepresentationMallet().getAlphabet().size());
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
    } catch (FileNotFoundException | UnsupportedEncodingException ex)  {
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
    try (InputStream is = modelFile.openStream();
         ObjectInputStream ois = new ObjectInputStream(is)) {
      ParallelTopicModel ptm = (ParallelTopicModel) ois.readObject();
      model=ptm;
    } catch (IOException | ClassNotFoundException ex) {
      throw new GateRuntimeException("Could not load Mallet model", ex);
    }
  }
  
  
  
  
  @Override
  public List<ModelApplication> applyModel(
          AnnotationSet instanceAS, AnnotationSet inputAS, AnnotationSet sequenceAS, String parms) {
    // NOTE: this generic method cannot be used for LDA since we need to know
    // the token feature. Instead we have an engine specific method (see below)
    // Also, we directly store the results in the instance annotations, instead of
    // returning a model application instance (which only works for classification/regression)
    throw new GateRuntimeException("Method applyModel cannot be used with EngineMBTopicsLDA, use applyTopicModel");
  }
  
  public void applyTopicModel(AnnotationSet instanceAS, AnnotationSet inputAS, 
          String tokenFeature, String parms) {
    CorpusRepresentationMalletLDA data = (CorpusRepresentationMalletLDA)corpusRepresentation;
    data.stopGrowth();

    int numIterations = 10;
    int burnIn = 10;
    int thinning = 0;
    int seed = 0;
    Parms parmdef = new Parms(parms,
                "i:iters:i",
                "B:burnin:i",
                "T:thinning:i",
                "s:seed:i"
    );
    numIterations = (int) parmdef.getValueOrElse("iters", numIterations);
    burnIn = (int) parmdef.getValueOrElse("burnin", burnIn);
    thinning = (int) parmdef.getValueOrElse("thinning", thinning);
    seed = (int) parmdef.getValueOrElse("seed", seed);


    ParallelTopicModel tm = (ParallelTopicModel)model;
    TopicInferencer ti = tm.getInferencer();
    tm.setRandomSeed(seed);
    
    for(Annotation instAnn : instanceAS.inDocumentOrder()) {
      // System.err.println("DEBUG: adding instance annotation "+instAnn);
      Instance inst = data.getInstanceFor(gate.Utils.start(instAnn), gate.Utils.end(instAnn), inputAS, tokenFeature);
      // System.err.println("DEBUG: Instance data is "+inst.getData());
      // System.err.println("DEBUG: got inferencer "+ti);
      // NOTE: see http://mallet.cs.umass.edu/api/cc/mallet/topics/TopicInferencer.html#getSampledDistribution(cc.mallet.types.Instance,%20int,%20int,%20int)
      double[] tdist = ti.getSampledDistribution(inst, numIterations, thinning, burnIn);
      List<Double> tdistlist = new ArrayList<>(tdist.length);
      int i = 0;
      int bestTopic = -1;
      double bestProb = -999.99;
      for(double val : tdist) {
        tdistlist.add(val);
        if(val > bestProb) {
          bestTopic = i;
          bestProb = val;
        }
        i++;
      }
      instAnn.getFeatures().put("LF_MBTopicsLDA_TopicDist", tdistlist);    
      // Also add a feature that gives the index and word list of the most likely topic
      instAnn.getFeatures().put("LF_MBTopicsLDA_MLTopic", bestTopic);
      instAnn.getFeatures().put("LF_MBTopicsLDA_MLTopicProb", bestProb);
      // TODO: to add the topic words we have to pre-calculate the tok k words for each topic
      // and assign the list for topic k here!
      // instAnn.getFeatures().put("LF_MBTopicsLDA_MLTopicWords", bestProb);            
    }
  }
  

  @Override
  public void initializeAlgorithm(Algorithm algorithm, String parms) {
    // NOTE: for LDA we do not do anything in here, everything happens in the train() method
  }

  @Override
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    throw new GateRuntimeException("Method evaluate not available for EngineMBTopicsLDA");
  }
  

  @Override
  protected void initWhenLoading(URL dir, String parms) {
    super.initWhenLoading(dir, parms);
    corpusRepresentation.stopGrowth();
  }
  
  
}
