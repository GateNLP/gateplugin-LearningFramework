/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.data;

import cc.mallet.types.Alphabet;
import cc.mallet.types.InstanceList;
import gate.AnnotationSet;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.log4j.Logger;

/**
 * Common base class for Mallet for classification and  Mallet for sequence tagging.
 * @author Johann Petrak
 */
public abstract class CorpusRepresentationMallet extends CorpusRepresentation {

  Logger logger = org.apache.log4j.Logger.getLogger(CorpusRepresentationMallet.class);

  protected InstanceList instances;

  public InstanceList getRepresentationMallet() { return instances; }
  
  public Object getRepresentation() { return instances; }
  
  public LFPipe getPipe() {
    if(instances == null) return null;
    if(instances.getPipe() == null) {
      return null;
    } else {
      return (LFPipe)instances.getPipe();
    }
  }
  
  /**
   * Prevent the addition of new features or feature values when instances are added.
   */
  public void stopGrowth() {
    LFPipe pipe = (LFPipe)instances.getPipe();
    pipe.getDataAlphabet().stopGrowth();
    Alphabet ta = pipe.getTargetAlphabet();
    if(ta != null) ta.stopGrowth();
    FeatureInfo fi = pipe.getFeatureInfo();
    fi.stopGrowth();
  }
  
  /**
   * Enable the addition of new features or feature values when instances are added.
   * After a CorpusRepresentationMallet instance is created, growth is enabled by default.
   */
  public void startGrowth() {
    LFPipe pipe = (LFPipe)instances.getPipe();
    pipe.getDataAlphabet().startGrowth();
    Alphabet ta = pipe.getTargetAlphabet();
    if(ta != null) ta.startGrowth();
    FeatureInfo fi = pipe.getFeatureInfo();
    fi.startGrowth();    
  }
    
  public void savePipe(File directory) {
    File outFile = new File(directory,"pipe.pipe");
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(new FileOutputStream(outFile));
      oos.writeObject(pipe);
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not save LFPipe for CorpusRepresentationMallet to "+outFile,ex);
    } finally {
      if(oos!=null) try {
        oos.close();
      } catch (IOException ex) {
        logger.error("Could not close stream after saving LFPipe to "+outFile, ex);
      }        
    }
  }
  
  public abstract void add(AnnotationSet instancesAS, AnnotationSet sequenceAS, AnnotationSet inputAS, AnnotationSet classAS, String targetFeatureName, TargetType targetType, String nameFeatureName);
  
  public abstract void addScaling(ScalingMethod scaleFeatures);
}
