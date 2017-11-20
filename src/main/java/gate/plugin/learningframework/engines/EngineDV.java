/*
 *  Copyright (c) The University of Sheffield.
 *
 *  This file is free software, licensed under the 
 *  GNU Library General Public License, Version 2.1, June 1991.
 *  See the file LICENSE.txt that comes with this software.
 *
 */
package gate.plugin.learningframework.engines;

import cc.mallet.types.Alphabet;
import gate.plugin.learningframework.data.CorpusRepresentation;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.data.CorpusRepresentationMalletSeq;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;

/**
 * Common base class for all Engines which use the Dense Volatile representation
 * 
 * 
 * @author Johann Petrak
 */
public abstract class EngineDV extends Engine {
  
  // TODO FROM HERE!!!
  
  protected CorpusRepresentationMallet corpusRepresentation; 

  @Override
  public CorpusRepresentation getCorpusRepresentation() {
    return corpusRepresentation;
  }
  
  protected void updateInfo() {
    //System.err.println("In updateInfo, model is "+model);
    if(model!=null) {
      info.modelClass = model.getClass().getName();
    }
    info.nrTrainingInstances = corpusRepresentation.getRepresentationMallet().size();
    info.nrTrainingDimensions = corpusRepresentation.getRepresentationMallet().getDataAlphabet().size();    
    LFPipe pipe = (LFPipe)corpusRepresentation.getPipe();
    Alphabet targetAlph = pipe.getTargetAlphabet();
    if(targetAlph == null) {
      info.nrTargetValues = 0;
    } else {
      info.nrTargetValues = targetAlph.size();
      //info.classLabels = 
      Object[] objs = targetAlph.toArray();
      ArrayList<String> labels = new ArrayList<String>();
      for(Object obj : objs) { labels.add(obj.toString()); }
      info.classLabels = labels;
    }
    
  }
  
  @Override
  protected void saveCorpusRepresentation(File directory) {
    corpusRepresentation.savePipe(directory);
  }
  
  @Override
  protected void loadAndSetCorpusRepresentation(URL directory) {
    if(corpusRepresentation==null)
      corpusRepresentation = CorpusRepresentationMalletTarget.load(directory);
  }
  
  
  @Override
  protected void initWhenCreating(URL directory, Algorithm algorithm, String parameters, FeatureInfo fi, TargetType tt) {    
    if(algorithm.getAlgorithmKind() == AlgorithmKind.SEQUENCE_TAGGER) {
      corpusRepresentation = new CorpusRepresentationMalletSeq(fi, fi.getGlobalScalingMethod());
    } else if(algorithm.getAlgorithmKind() == AlgorithmKind.REGRESSOR || 
              algorithm.getAlgorithmKind() == AlgorithmKind.CLASSIFIER) {
      corpusRepresentation = new CorpusRepresentationMalletTarget(fi, fi.getGlobalScalingMethod(), tt);
    } else {
      throw new GateRuntimeException("Not a usable algorithm kind for now with Mallet based engines: "+algorithm);
    }
  }
  
}
