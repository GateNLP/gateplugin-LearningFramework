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
public enum AlgorithmClustering implements Algorithm {
  //GensimWrapper_CLUS_DR(EngineDVFileJsonGensim.class,null),
  GensimWrapper_CLUS_DR(null,null),
  MalletLDA_CLUS_MR(EngineMBTopicsLDA.class,null)  
  ;
  private AlgorithmClustering() {
    
  }
  private AlgorithmClustering(Class<?> engineClass, Class<?> algorithmClass) {
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
