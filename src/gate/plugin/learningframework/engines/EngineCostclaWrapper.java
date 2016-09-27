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
 * @author Johann Petrak
 */
public class EngineCostclaWrapper extends EngineSklearnBase {

  public EngineCostclaWrapper() {
    WRAPPER_NAME = "SklearnWrapper";
    ENV_WRAPPER_HOME = "SKLEARN_WRAPPER_HOME";
    PROP_WRAPPER_HOME = "gate.plugin.learningframework.sklearnwrapper.home";
    YAML_FILE = "sklearn.yaml";
    YAML_SETTING_WRAPPER_HOME = "sklearnwrapper.home";
    SCRIPT_APPLY_BASENAME = "costclaWrapperApply";
    SCRIPT_TRAIN_BASENAME = "costclaWrapperTrain";
    SCRIPT_EVAL_BASENAME = "costclaWrapperEval";
    MODEL_BASENAME = "costclamodel";
    MODEL_INSTANCE = new CostclaModel();
  }
  
  static class CostclaModel { }

}
