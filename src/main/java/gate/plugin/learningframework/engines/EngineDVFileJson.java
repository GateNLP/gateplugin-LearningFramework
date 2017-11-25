/*
 *  Copyright (c) The University of Sheffield.
 *
 *  This file is free software, licensed under the 
 *  GNU Library General Public License, Version 2.1, June 1991.
 *  See the file LICENSE.txt that comes with this software.
 *
 */
package gate.plugin.learningframework.engines;

import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.ModelApplication;
import gate.plugin.learningframework.data.CorpusRepresentation;
import gate.plugin.learningframework.data.CorpusRepresentationVolatileDense2JsonStream;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.util.Files;
import java.io.File;
import java.net.URL;
import java.util.List;

/**
 * Common base class for all Engines which are dense, volatile and write JSON to a file.
 * 
 * 
 * @author Johann Petrak
 */
public abstract class EngineDVFileJson extends EngineDV {
  
  // For this engine, this will always be a CorpusRepresentationVolatileDense2JsonStream
  protected CorpusRepresentationVolatileDense2JsonStream corpusRepresentation;
  
  // just as a reminder, we need to implement the following methods:
  @Override
  public CorpusRepresentation getCorpusRepresentation() { return corpusRepresentation; }
  
    
  @Override
  protected void initWhenCreating(URL directory, Algorithm algorithm, String parms, FeatureInfo featureInfo, TargetType targetType) {
    File outDir = Files.fileFromURL(directory);
    corpusRepresentation = new CorpusRepresentationVolatileDense2JsonStream(outDir, featureInfo);
  }

  @Override
  protected void loadAndSetCorpusRepresentation(URL directory) {
    File outDir = Files.fileFromURL(directory);
    // TODO: !!!!!!
    // We need to load the feature info from the directory as well, this should be 
    // in its own serialized file for this kind of engine.
    
    // TODO!!!!!!!!!!!!!!!!!!!!!!!!
    FeatureInfo featureInfo = null;
    corpusRepresentation = new CorpusRepresentationVolatileDense2JsonStream(outDir, featureInfo);
  }

  @Override
  protected void loadModel(URL directory, String parms) {
    // TODO
    
    // TODO: how is this different from loadAndSetCorpusRepresentation, with regard to 
    // timing and preconditions?
  }

  @Override
  protected void saveCorpusRepresentation(File directory) {
    // TODO
  }

  @Override
  protected void saveModel(File directory) {
    // TODO
  }

  @Override
  public void trainModel(File dataDirectory, String instanceType, String parms) {
    // TODO:
  }

  @Override
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    throw new UnsupportedOperationException("Not supported (yet?)"); 
  }

  @Override
  public List<ModelApplication> applyModel(AnnotationSet instanceAS, AnnotationSet inputAS, AnnotationSet sequenceAS, String parms) {
    // TODO 
    return null;
  }

  @Override
  public void initializeAlgorithm(Algorithm algorithm, String parms) {
    // TODO
  }

  
  protected void updateInfo() {
    // TODO:    
  }
  
}