/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.engines;

/**
 * All algorithms implement this interface.
 * @author johann
 */
public interface Algorithm {
  public Class getTrainerClass();
  public Class getEngineClass();
  // For those algorithms called SOMETHING_SPECIFY_CLASS, the trainer class is initially null,
  // but we use this method to set it to whatever class the user actually specifies
  public void setTrainerClass(Class trainerClass);
}
