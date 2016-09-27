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
 * An engine that represents Python Scikit Learn through en external process.
 * 
 * This requires that the user configures the location of where sklearn-wrapper is installed.
 * This can be done by setting the environment variable SKLEARN_WRAPPPER_HOME, the Java property
 * gate.plugin.learningframework.sklearnwrapper.home or by adding another yaml file "sklearn.yaml" 
 * to the data directory which contains the setting sklearnwrapper.home.
 * If the path starts with a slash
 * it is an absolute path, otherwise the path is resolved relative to the 
 * directory. 
 * 
 * 
 * @author Johann Petrak
 */
public class EngineSklearnWrapper extends EngineSklearnBase {

  public EngineSklearnWrapper() {    
    WRAPPER_NAME = "SklearnWrapper";
    ENV_WRAPPER_HOME = "SKLEARN_WRAPPER_HOME";
    PROP_WRAPPER_HOME = "gate.plugin.learningframework.sklearnwrapper.home";
    YAML_FILE = "sklearn.yaml";
    YAML_SETTING_WRAPPER_HOME = "sklearnwrapper.home";
    SCRIPT_APPLY_BASENAME = "sklearnWrapperApply";
    SCRIPT_TRAIN_BASENAME = "sklearnWrapperTrain";
    SCRIPT_EVAL_BASENAME = "sklearnWrapperEval";
    MODEL_BASENAME = "sklmodel";
    MODEL_INSTANCE = new SklearnModel();
  }
  
  static class SklearnModel { }
  
}
