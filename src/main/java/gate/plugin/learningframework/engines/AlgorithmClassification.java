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
  CostclaWrapper_CL_MR(EngineMBCostclaWrapper.class,null),
  KerasWrapper_CL_DR(EngineDVFileJsonKeras.class,null),
  KerasWrapper_CL_MR(EngineKerasWrapper.class,null),
  KerasWrapper_SEQ_DR(EngineDVFileJsonKeras.class,null),
  LibSVM_CL_MR(EngineLibSVM.class,libsvm.svm.class), 
  MalletBalancedWinnow_CL_MR(EngineMBMalletClass.class,cc.mallet.classify.BalancedWinnowTrainer.class),
  MalletC45_CL_MR(EngineMBMalletClass.class,cc.mallet.classify.C45Trainer.class),
  MalletCRF_SEQ_MR(EngineMBMalletSeq.class,null,AlgorithmKind.SEQUENCE_TAGGER), // ByLabelLikelihood or ByThreadedLabelLikelihood
  MalletCRFSG_SEQ_MR(EngineMBMalletSeq.class,null,AlgorithmKind.SEQUENCE_TAGGER), // Stochastic gradient
  MalletCRFVG_SEQ_MR(EngineMBMalletSeq.class,null,AlgorithmKind.SEQUENCE_TAGGER), // Value gradient
  MalletDecisionTree_CL_MR(EngineMBMalletClass.class,cc.mallet.classify.DecisionTreeTrainer.class),
  MalletMEMM_SEQ_MR(EngineMBMalletSeq.class,null,AlgorithmKind.SEQUENCE_TAGGER),
  MalletMexEnt_CL_MR(EngineMBMalletClass.class,cc.mallet.classify.MaxEntTrainer.class),
  MalletNaiveBayesEM_CL_MR(EngineMBMalletClass.class,cc.mallet.classify.NaiveBayesEMTrainer.class),
  MalletNaiveBayes_CL_MR(EngineMBMalletClass.class,cc.mallet.classify.NaiveBayesTrainer.class),
  MalletWinnow_CL_MR(EngineMBMalletClass.class,cc.mallet.classify.WinnowTrainer.class),
  PytorchWrapper_CL_DR(EngineDVFileJsonPyTorch.class,null),
  PytorchWrapper_SEQ_DR(EngineDVFileJsonPyTorch.class,null,AlgorithmKind.SEQUENCE_TAGGER),
  // The following requires specification of an array of Optimizable.ByGradientValue
  // instances which need to be initialized with Instances 
  // We only add this after figuring out exactly how it needs to get set up!
  // MalletCRFVGS_SEQ_MR(EngineMalletSeq.class,null), // ByValueGradients  
  //GenericServer_CL_MR(EngineServer.class,null),
  SklearnWrapper_CL_MR(EngineMBSklearnWrapper.class,null),
  WekaWrapper_CL_MR(EngineMBWekaWrapper.class,null),
  //TensorflowWrapper_CL_MR(EngineTensorFlowWrapper.class,null),
  ;
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
