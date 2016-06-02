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
  MALLET_SEQ_CRF(EngineMalletSeq.class,null), // ByLabelLikelihood or ByThreadedLabelLikelihood
  MALLET_SEQ_CRF_SG(EngineMalletSeq.class,null), // Stochastic gradient
  MALLET_SEQ_CRF_VG(EngineMalletSeq.class,null), // Value gradient
  // The following requires specification of an array of Optimizable.ByGradientValue
  // instances which need to be initialized with Instances 
  // We only add this after figuring out exactly how it needs to get set up!
  // MALLET_SEQ_CRF_VGS(EngineMalletSeq.class,null), // ByValueGradients  
  MALLET_SEQ_MEMM(EngineMalletSeq.class,null),
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
