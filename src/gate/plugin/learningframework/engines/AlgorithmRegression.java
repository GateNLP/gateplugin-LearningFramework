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
  LIBSVM_RG(EngineLibSVM.class,null),
  GENERIC_RG_SERVER(EngineServer.class,null),
  WEKA_RG_WRAPPER(EngineWekaWrapper.class,null),
  SKLEARN_RG_WRAPPER(EngineSklearnWrapper.class,null),
  TENSORFLOW_RG_WRAPPER(EngineTensorFlowWrapper.class,null),
  KERAS_RG_WRAPPER(EngineKerasWrapper.class,null);
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
