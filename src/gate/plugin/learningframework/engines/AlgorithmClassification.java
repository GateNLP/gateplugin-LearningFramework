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
public enum AlgorithmClassification implements Algorithm {
  // NOTE: not sure if the different LIBSVM algorithms should get a different entry here or 
  // if we want to use parameters for those.
  // Also consider supporting in addition this port: https://github.com/davidsoergel/jlibsvm/
  LIBSVM_CL(EngineLibSVM.class,libsvm.svm.class), 
  MALLET_CL_C45(EngineMalletClass.class,cc.mallet.classify.C45Trainer.class),
  MALLET_CL_DECISION_TREE(EngineMalletClass.class,cc.mallet.classify.DecisionTreeTrainer.class),
  MALLET_CL_MAX_ENT(EngineMalletClass.class,cc.mallet.classify.MaxEntTrainer.class),
  MALLET_CL_NAIVE_BAYES_EM(EngineMalletClass.class,cc.mallet.classify.NaiveBayesEMTrainer.class),
  MALLET_CL_NAIVE_BAYES(EngineMalletClass.class,cc.mallet.classify.NaiveBayes.class),
  MALLET_CL_WINNOW(EngineMalletClass.class,cc.mallet.classify.WinnowTrainer.class),
  MALLET_SEQ_CRF(EngineMalletSeq.class,null), // creating this training is too complex, no class specified
  MALLET_CL_SPECIFY_CLASS(EngineMalletClass.class,null),
  WEKA_CL_NAIVE_BAYES(EngineWeka.class,weka.classifiers.bayes.NaiveBayes.class),
  WEKA_CL_J48(EngineWeka.class,weka.classifiers.trees.J48.class),
  WEKA_CL_MULTILAYER_PERCEPTRON(EngineWeka.class,weka.classifiers.functions.MultilayerPerceptron.class),
  WEKA_CL_RANDOM_TREE(EngineWeka.class,weka.classifiers.trees.RandomTree.class),
  WEKA_CL_IBK(EngineWeka.class,weka.classifiers.lazy.IBk.class),
  WEKA_CL_LOGISTIC_REGRESSION(EngineWeka.class,weka.classifiers.functions.Logistic.class),
  WEKA_CL_RANDOM_FOREST(EngineWeka.class,weka.classifiers.trees.RandomForest.class),
  WEKA_CL_SPECIFY_CLASS(EngineWeka.class,null);
  private AlgorithmClassification() {
    
  }
  private AlgorithmClassification(Class engineClass, Class algorithmClass) {
    this.engineClass = engineClass;
    this.trainerClass = algorithmClass;
  }
  private Class engineClass;
  private Class trainerClass;
  @Override
  public Class getEngineClass() { return engineClass; }
  @Override
  public Class getTrainerClass() { return trainerClass; }

  @Override
  public void setTrainerClass(Class trainerClass) {
    this.trainerClass = trainerClass;
  }
}
