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
import gate.lib.interaction.process.ProcessBase;
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
  
  // Wrapper name: this is set by the actual implementing engine class and 
  // will influence the file name of scripts, config files etc specific to that
  // wrapper.
  protected String WRAPPER_NAME;
  
  protected ProcessBase process;
  
  
  // For this engine, this will always be a CorpusRepresentationVolatileDense2JsonStream
  protected CorpusRepresentationVolatileDense2JsonStream corpusRepresentation;
  
  @Override
  public CorpusRepresentation getCorpusRepresentation() { 
    return corpusRepresentation; 
  }
  
    
  @Override
  protected void initWhenCreating(URL directory, Algorithm algorithm, String parms, FeatureInfo featureInfo, TargetType targetType) {
    File outDir = Files.fileFromURL(directory);
    corpusRepresentation = new CorpusRepresentationVolatileDense2JsonStream(outDir, featureInfo);
    corpusRepresentation.startAdding();
    Utils4Engines.copyWrapper(WRAPPER_NAME,directory);
  }

  @Override
  protected void loadAndSetCorpusRepresentation(URL directory) {
    // this does not actually need to load anything but the featureInfo ... 
    // this is needed to convert our instance data to JSON, which is then sent
    // off to the script or server which is responsible to use any other saved
    // model info (the model itself, scaling info, vocab info, embeddings etc)
    File outDir = Files.fileFromURL(directory);
    featureInfo = FeatureInfo.load(directory);
    corpusRepresentation = new CorpusRepresentationVolatileDense2JsonStream(outDir, featureInfo);
  }

  @Override
  protected void loadModel(URL directory, String parms) {
    // This should all get handled by the script, so nothing really needed here so far
  }

  @Override
  protected void saveCorpusRepresentation(File directory) {
    // all we need to do is close the corpus (which will also make it save the metadata)
    corpusRepresentation.finishAdding();
  }

  @Override
  protected void saveModel(File directory) {
    // this is all handled by the script we are running for training, nothing 
    // needed in here.
  }

  @Override
  public void trainModel(File dataDirectory, String instanceType, String parms) {    
    // we also need to save the updated info file
    info.engineClass = this.getClass().getName();
    info.save(dataDirectory);    
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