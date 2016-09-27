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
 * An engine that represents Python Keras through en external process.
 * 
 * This requires that the user configures the location of where keras-wrapper is installed.
 * This can be done by setting the environment variable KERAS_WRAPPPER_HOME, the Java property
 * gate.plugin.learningframework.keraswrapper.home or by adding another yaml file "keras.yaml" 
 * to the data directory which contains the setting keraswrapper.home.
 * If the path starts with a slash
 * it is an absolute path, otherwise the path is resolved relative to the 
 * directory. 
 * 
 * 
 * @author Johann Petrak
 */
public class EngineKerasWrapper extends EnginePythonNetworksBase {

  static class KerasModel { }
  
  public EngineKerasWrapper() {
    WRAPPER_NAME = "KerasWrapper";
    ENV_WRAPPER_HOME = "KERAS_WRAPPER_HOME";
    PROP_WRAPPER_HOME = "gate.plugin.learningframework.keraswrapper.home";
    YAML_FILE = "keras.yaml";
    YAML_SETTING_WRAPPER_HOME = "keraswrapper.home";
    SCRIPT_APPLY_BASENAME = "kerasWrapperApply";
    SCRIPT_TRAIN_BASENAME = "kerasWrapperTrain";
    SCRIPT_EVAL_BASENAME = "kerasWrapperEval";
    MODEL_BASENAME = "kerasmodel";
    MODEL_INSTANCE = new KerasModel();
  }
  
 
  
}
