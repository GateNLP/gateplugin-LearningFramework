/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.engines;


/**
 *
 * @author johann
 */
public enum AlgorithmRegression implements Algorithm {
  LIBSVM_RG(EngineLibSVM.class,null);
  private AlgorithmRegression() {
    
  }
  private AlgorithmRegression(Class engineClass, Class algorithmClass) {
    this.engineClass = engineClass;
    this.trainerClass = algorithmClass;
  }
  private Class engineClass;
  private Class trainerClass;
  @Override
  public Class getEngineClass() { return engineClass; }

  @Override
  public Class getTrainerClass() {
    return trainerClass;
  }

  @Override
  public void setTrainerClass(Class trainerClass) {
    this.trainerClass = trainerClass;
  }
}
