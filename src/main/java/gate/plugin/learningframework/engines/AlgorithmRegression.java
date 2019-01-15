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
public enum AlgorithmRegression implements Algorithm {
  // KerasWrapper_RG_DR(EngineDVFileJsonKeras.class,null),
  // KerasWrapper_RG_MR(EngineKerasWrapper.class,null),
  LibSVM_RG_MR(EngineLibSVM.class,null),
  //GenericServer_RG_MR(EngineServer.class,null),
  PytorchWrapper_RG_DR(EngineDVFileJsonPyTorch.class,null),
  SklearnWrapper_RG_MR(EngineMBSklearnWrapper.class,null),
  WekaWrapper_RG_MR(EngineMBWekaWrapper.class,null),
  //TensorflowWrapper_RG_MR(EngineTensorFlowWrapper.class,null),
  ;
  private AlgorithmRegression() {
    
  }
  private AlgorithmRegression(Class<?> engineClass, Class<?> algorithmClass) {
    this.engineClass = engineClass;
    this.trainerClass = algorithmClass;
    this.algorithmKind = AlgorithmKind.REGRESSOR;
  }
  private Class<?> engineClass;
  private Class<?> trainerClass;
  private AlgorithmKind algorithmKind;
  @Override
  public Class<?> getEngineClass() { return engineClass; }
  @Override
  public Class<?> getTrainerClass() { return trainerClass; }
  @Override 
  public AlgorithmKind getAlgorithmKind() { return algorithmKind; }

  @Override
  public void setTrainerClass(Class<?> trainerClass) {
    this.trainerClass = trainerClass;
  }
}
