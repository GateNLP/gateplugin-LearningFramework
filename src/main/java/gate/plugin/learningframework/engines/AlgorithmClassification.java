/*
 * Copyright (c) 2015-2016 The University Of Sheffield.
 *
 * This file is part of gateplugin-LearningFramework 
 * (see https://github.com/GateNLP/gateplugin-LearningFramework).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
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
  MALLET_CL_BALANCED_WINNOW(EngineMBMalletClass.class,cc.mallet.classify.BalancedWinnowTrainer.class),
  MALLET_CL_C45(EngineMBMalletClass.class,cc.mallet.classify.C45Trainer.class),
  MALLET_CL_DECISION_TREE(EngineMBMalletClass.class,cc.mallet.classify.DecisionTreeTrainer.class),
  MALLET_CL_MAX_ENT(EngineMBMalletClass.class,cc.mallet.classify.MaxEntTrainer.class),
  MALLET_CL_NAIVE_BAYES_EM(EngineMBMalletClass.class,cc.mallet.classify.NaiveBayesEMTrainer.class),
  MALLET_CL_NAIVE_BAYES(EngineMBMalletClass.class,cc.mallet.classify.NaiveBayesTrainer.class),
  MALLET_CL_WINNOW(EngineMBMalletClass.class,cc.mallet.classify.WinnowTrainer.class),
  MALLET_SEQ_CRF(EngineMBMalletSeq.class,null,AlgorithmKind.SEQUENCE_TAGGER), // ByLabelLikelihood or ByThreadedLabelLikelihood
  MALLET_SEQ_CRF_SG(EngineMBMalletSeq.class,null,AlgorithmKind.SEQUENCE_TAGGER), // Stochastic gradient
  MALLET_SEQ_CRF_VG(EngineMBMalletSeq.class,null,AlgorithmKind.SEQUENCE_TAGGER), // Value gradient
  // The following requires specification of an array of Optimizable.ByGradientValue
  // instances which need to be initialized with Instances 
  // We only add this after figuring out exactly how it needs to get set up!
  // MALLET_SEQ_CRF_VGS(EngineMalletSeq.class,null), // ByValueGradients  
  MALLET_SEQ_MEMM(EngineMBMalletSeq.class,null,AlgorithmKind.SEQUENCE_TAGGER),
  //GENERIC_CL_SERVER(EngineServer.class,null),
  WEKA_CL_WRAPPER(EngineMBWekaWrapper.class,null),
  SKLEARN_CL_WRAPPER(EngineMBSklearnWrapper.class,null),
  //TENSORFLOW_CL_WRAPPER(EngineTensorFlowWrapper.class,null),
  KERAS_CL_WRAPPER(EngineKerasWrapper.class,null),
  COSTCLA_CL_WRAPPER(EngineMBCostclaWrapper.class,null);
  private AlgorithmClassification() {
    
  }
  private AlgorithmClassification(Class engineClass, Class algorithmClass) {
    this.engineClass = engineClass;
    this.trainerClass = algorithmClass;
    this.algorithmKind = AlgorithmKind.CLASSIFIER;
  }
  private AlgorithmClassification(Class engineClass, Class algorithmClass, AlgorithmKind ak) {
    this.engineClass = engineClass;
    this.trainerClass = algorithmClass;
    this.algorithmKind = ak;
  }
  private Class engineClass;
  private Class trainerClass;
  private AlgorithmKind algorithmKind;
  @Override
  public Class getEngineClass() { return engineClass; }
  @Override
  public Class getTrainerClass() { return trainerClass; }
  @Override 
  public AlgorithmKind getAlgorithmKind() { return algorithmKind; }

  @Override
  public void setTrainerClass(Class trainerClass) {
    this.trainerClass = trainerClass;
  }
}
