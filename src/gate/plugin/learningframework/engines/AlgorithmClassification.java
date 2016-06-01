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
  MALLET_CL_BALANCED_WINNOW(EngineMalletClass.class,cc.mallet.classify.BalancedWinnowTrainer.class),
  MALLET_CL_C45(EngineMalletClass.class,cc.mallet.classify.C45Trainer.class),
  MALLET_CL_DECISION_TREE(EngineMalletClass.class,cc.mallet.classify.DecisionTreeTrainer.class),
  MALLET_CL_MAX_ENT(EngineMalletClass.class,cc.mallet.classify.MaxEntTrainer.class),
  MALLET_CL_NAIVE_BAYES_EM(EngineMalletClass.class,cc.mallet.classify.NaiveBayesEMTrainer.class),
  MALLET_CL_NAIVE_BAYES(EngineMalletClass.class,cc.mallet.classify.NaiveBayes.class),
  MALLET_CL_WINNOW(EngineMalletClass.class,cc.mallet.classify.WinnowTrainer.class),
  // TODO: this is not implemented yet since we need to be able to delay the 
  // instantiation of the CRF trainer to actual training time!!
  //MALLET_SEQ_CRF(EngineMalletSeq.class,null), // creating this training is too complex, no class specified
  WEKA_CL_WRAPPER(EngineWekaExternal.class,null);
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
