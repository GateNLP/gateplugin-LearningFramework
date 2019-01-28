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
import gate.plugin.learningframework.data.CorpusRepresentationMalletLDA;
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
 * Common base class for all Engines which use the Mallet Corpus Representation.
 * 
 * This is used to gather the code for all engines which use a CorpusRepresentationMallet
 * internally even if the algorithm wrapped by the engine is not a Mallet algorithm.
 * This is the case for other Java algorithms like LibSVM where the Mallet 
 * corpus representation is used to create a temporary representation which is 
 * then converted to the algorithm specific representation before training.
 * 
 * @author Johann Petrak
 */
public abstract class EngineMB extends Engine {
  
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
    LFPipe pipe = corpusRepresentation.getPipe();
    Alphabet targetAlph = pipe.getTargetAlphabet();
    if(targetAlph == null) {
      info.nrTargetValues = 0;
    } else {
      info.nrTargetValues = targetAlph.size();
      //info.classLabels = 
      Object[] objs = targetAlph.toArray();
      ArrayList<String> labels = new ArrayList<>();
      for(Object obj : objs) { labels.add(obj.toString()); }
      info.classLabels = labels;
    }
    
  }
  
  @Override
  protected void saveCorpusRepresentation(File directory) {
    corpusRepresentation.finishAdding();
    corpusRepresentation.savePipe(directory);
  }
  
  @Override
  protected void loadAndSetCorpusRepresentation(URL directory) {
    // TODO: Special case if the corpus representaiton is for clustering or we 
    // override in the Engine!!
    if(corpusRepresentation==null) {
      corpusRepresentation = CorpusRepresentationMalletTarget.load(directory);
    }
  }
  
  
  @Override
  protected void initWhenCreating(URL directory, Algorithm algorithm, 
          String parameters, FeatureInfo fi, TargetType tt) {    
    if(null == algorithm.getAlgorithmKind()) {
      throw new GateRuntimeException("Not a usable algorithm kind for now with Mallet based engines: "+algorithm);
    } else {
      switch (algorithm.getAlgorithmKind()) {
        case SEQUENCE_TAGGER:
          corpusRepresentation = new CorpusRepresentationMalletSeq(fi);
          break;
        case REGRESSOR:
        case CLASSIFIER:
          corpusRepresentation = new CorpusRepresentationMalletTarget(fi, tt);
          break;
        case CLUSTERING:
          corpusRepresentation = new CorpusRepresentationMalletLDA(fi);
          break;
        default:
          throw new GateRuntimeException("Not a usable algorithm kind for now with Mallet based engines: "+algorithm);
      }
    }
    this.featureInfo = fi;
    corpusRepresentation.startAdding();
  }
  
}
