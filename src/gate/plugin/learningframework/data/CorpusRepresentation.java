

package gate.plugin.learningframework.data;

import cc.mallet.types.InstanceList;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.Exporter;
import gate.plugin.learningframework.engines.Info;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.mallet.LFPipe;
import java.io.File;
import java.util.ArrayList;

/**
 * The base class of all classes that handle the representation of instances.
 * The LearningFramework uses the MalletSeq and Mallet representations whenever possible.
 * The other subclasses so far are only used to convert from Mallet representation for 
 * training, classification or export. 
 * @author Johann Petrak
 */
public abstract class CorpusRepresentation {
  protected FeatureInfo featureInfo;
  protected ScalingMethod scalingMethod;
  protected LFPipe pipe;
  
  /**
   * Returns whatever object the concrete representation uses to represent the instances.
   * In addition, each specific CorpusRepresentation subclass has a representation specific
   * method that returns the correct type of data, e.g. getRepresentationLibSVM 
   * @return 
   */
  public abstract Object getRepresentation();
  
  public abstract InstanceList getRepresentationMallet();
  
  
  
  
  /**
   * Remove all instances but leave other information intact.
   * This removes all the instances but retains information about the features/attributes 
   * and how instances should get transformed or scaled.
   */
  public abstract void clear();
  
}
