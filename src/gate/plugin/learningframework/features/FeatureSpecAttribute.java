

package gate.plugin.learningframework.features;

import java.io.Serializable;

/**
 *
 * @author Johann Petrak
 */
public abstract class FeatureSpecAttribute implements Serializable, Cloneable {

  private static final long serialVersionUID = 651636894843439700L;

  public String annType;
  public String feature;
  public String name;
  
  public abstract void stopGrowth();
  public abstract void startGrowth();
  
  @Override
  public FeatureSpecAttribute clone()  {
    try {
      return (FeatureSpecAttribute) super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new RuntimeException("Could not clone Attribute",ex);
    }
  }

}
