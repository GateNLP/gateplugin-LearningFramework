/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.features;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Johann Petrak
 */
public abstract class Attribute implements Serializable, Cloneable {

  private static final long serialVersionUID = 651636894843439700L;

  public String annType;
  public String feature;
  public String name;
  
  public abstract void stopGrowth();
  public abstract void startGrowth();
  
  @Override
  public Attribute clone()  {
    try {
      return (Attribute) super.clone();
    } catch (CloneNotSupportedException ex) {
      throw new RuntimeException("Could not clone Attribute",ex);
    }
  }

}
