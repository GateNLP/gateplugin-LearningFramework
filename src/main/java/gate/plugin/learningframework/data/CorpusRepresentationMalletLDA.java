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
package gate.plugin.learningframework.data;

import gate.Annotation;
import gate.AnnotationSet;
import java.util.ArrayList;
import java.util.List;
import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.TokenSequence;
import gate.Document;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import org.apache.log4j.Logger;
import gate.plugin.learningframework.features.SeqEncoder;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.URL;
import static gate.plugin.learningframework.LFUtils.newURL;
import gate.plugin.learningframework.mallet.LFAlphabet;
import gate.plugin.learningframework.mallet.LFInstanceList;
import java.io.IOException;
import java.util.Arrays;
import java.util.logging.Level;

public class CorpusRepresentationMalletLDA extends CorpusRepresentationMallet {

  static final Logger LOGGER = Logger.getLogger("CorpusRepresentationMalletLDA");

  public CorpusRepresentationMalletLDA(FeatureInfo fi, ScalingMethod sm) {
    featureInfo = fi;  // always null
    scalingMethod = ScalingMethod.NONE;

    // TODO: we really do not need any of this, figure out if we can simplify,
    // but keeping this should not really do any harm!
    Pipe innerPipe = new Noop(new LFAlphabet(), null);
    List<Pipe> pipes = new ArrayList<>();
    pipes.add(innerPipe);
    pipe = new LFPipe(pipes);
    pipe.setFeatureInfo(fi);
    instances = new LFInstanceList(pipe);
  }

  /**
   * Non-public constructor for use when creating from a serialized pipe.
   *
   * @param fi
   */
  CorpusRepresentationMalletLDA(LFPipe pipe) {
    this.pipe = pipe;
    this.featureInfo = pipe.getFeatureInfo();
    this.scalingMethod = ScalingMethod.NONE;
    this.instances = new LFInstanceList(pipe);
  }

  /**
   * Create a new instance based on the pipe stored in directory.
   *
   * @param directory directory with the saved model
   * @return corpus representation instance
   */
  public static CorpusRepresentationMalletLDA load(URL directory) {
    // load the pipe from a Java object serialization representation
    URL inFile = newURL(directory, "pipe.pipe");
    LFPipe lfpipe = null;
    try (InputStream bom = inFile.openStream();
         ObjectInputStream ois = new ObjectInputStream(bom)) {
      lfpipe = (LFPipe) ois.readObject();      
    } catch (ClassNotFoundException | IOException ex) { 
      throw new GateRuntimeException("Could not read pipe from "+inFile,ex);
    }
    CorpusRepresentationMalletLDA crms = new CorpusRepresentationMalletLDA(lfpipe);
    return crms;
  }


  /**
   * Add instances. 
   * 
   * For LDA, the instanceAS is either null or a set of annotations where each 
   * annotation represents a "document". The inputAS is the set containing all
   * token annotations for the GATE document. 
   * 
   * @param instancesAS instance/document annotation set or null
   * @param sequenceAS not used
   * @param inputAS set of token annotations
   * @param classAS not sued
   * @param tokenFeatureName the feature to use for the token string or empty to use the document text
   * @param targetType not used, expected to be Target.NONE
   * @param instanceWeightFeature ignored
   * @param nameFeatureName not used
   * @param seqEncoder not used
   */
  @Override
  public void add(AnnotationSet instancesAS, AnnotationSet sequenceAS, AnnotationSet inputAS, AnnotationSet classAS, String tokenFeatureName, TargetType targetType, String instanceWeightFeature, String nameFeatureName, SeqEncoder seqEncoder) {
    if (instancesAS == null) {
      // create one mallet instance for the whole document
      Document doc = inputAS.getDocument();
      instances.add(getInstanceFor(0L, doc.getContent().size(),inputAS, tokenFeatureName));
    } else {
      // create one mallet instance for each instance annotation
      for (Annotation instanceAnnotation : instancesAS.inDocumentOrder()) {
        instances.add(getInstanceFor(gate.Utils.start(instanceAnnotation),gate.Utils.end(instanceAnnotation),
                inputAS, tokenFeatureName));
      }
    }
  }

  @Override
  public void finishAdding() {  
    if(scalingMethod != ScalingMethod.NONE) {
      throw new GateRuntimeException("Scaling not allowed/not yet implemented for sequence tagging representation");
    }
  }
  
  /**
   * Get a Mallet FeatureSequence Instance for the tokens in the span.
   * The span is what is covered by the original instance annotation.
   * @param from start offset
   * @param to end offset 
   * @param inputAS  annotation set containing the token-like annotations
   * @param tokenFeatureName feature in the token-like annotations to use or empty for document text
   * @return  mallet instance containing a feature sequence 
   */
  public Instance getInstanceFor(
          long from,
          long to,
          AnnotationSet inputAS,
          String tokenFeatureName) {

    
    if(tokenFeatureName == null) {
      tokenFeatureName = "";
    }
    Document doc = inputAS.getDocument();
    List<Annotation> tokenAnnotations = inputAS.get(from, to).inDocumentOrder();
    List<String> tokenList = new ArrayList<>();
    String str;
    for(Annotation tokenAnnotation : tokenAnnotations) {
      if(tokenFeatureName.isEmpty()) {
        str = gate.Utils.cleanStringFor(doc, tokenAnnotation);
      } else {
        str = (String)tokenAnnotation.getFeatures().get(tokenFeatureName);
      }
      if(str != null && !str.isEmpty()) {
        tokenList.add(str);
      }
    }
    TokenSequence tokenSeq = new TokenSequence(tokenList.toArray());
    //System.err.println("DEBUG: tokensequence="+tokenSeq);
    //System.err.println("DEBUG: alphabet growStopped()="+instances.getAlphabet().growthStopped());
    
    
    // NOTE: the following will create a feature sequence that contains -1 entries
    // for tokens which are not in the alphabet, if alphabet growth has been stopped
    // FeatureSequence featSeq = tokenSeq.toFeatureSequence(instances.getAlphabet());
    
    // Instead we create the FeatureSequence ourselves
    FeatureSequence featSeq = new FeatureSequence(instances.getAlphabet(), tokenSeq.size());
    Alphabet alph = instances.getAlphabet();
    for(int i=0; i<tokenSeq.size(); i++) {
      int idx = alph.lookupIndex(tokenSeq.get(i).getText());
      if(idx > -1) {
        featSeq.add(idx);
      }
    }
    /*
    System.err.println("DEBUG: fseq size="+featSeq.size());
    System.err.println("DEBUG: fseq length="+featSeq.getLength());
    System.err.println("DEBUG: fseq feats="+Arrays.toString(featSeq.getFeatures()));
    System.err.println("DEBUG: fseq feats="+Arrays.toString(featSeq.getFeatures()));
    System.err.println("DEBUG: fseq featIndexSequence="+Arrays.toString(featSeq.toFeatureIndexSequence()));
    */
    
    return new Instance(featSeq, null, null, null);

  }

  @Override
  public int nrInstances() {
    if(instances == null) {
      return 0;
    } else {
      return instances.size();
    }
  }
  
  
}
