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
  WEKA_RG_ADDITIVE_REGRESSION(EngineWeka.class,weka.classifiers.meta.AdditiveRegression.class),
  WEKA_RG_LINEAR_REGRESSION(EngineWeka.class,weka.classifiers.functions.LinearRegression.class),
  WEKA_RG_REPTree(EngineWeka.class,weka.classifiers.trees.REPTree.class),
  WEKA_RG_SMOReg(EngineWeka.class,weka.classifiers.functions.SMOreg.class),
  WEKA_RG_MULTILAYER_PERCEPTRON(EngineWeka.class,weka.classifiers.functions.MultilayerPerceptron.class),
  WEKA_RG_GAUSSIAN_PROCESSES(EngineWeka.class,weka.classifiers.functions.GaussianProcesses.class),
  WEKA_RG_SPECIFY_CLASS(EngineWeka.class,null);
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
