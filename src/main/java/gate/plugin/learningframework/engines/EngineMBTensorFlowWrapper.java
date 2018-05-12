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
 * An engine that represents Python TensorFlow through en external process.
 * 
 * This requires that the user configures the location of where tensorflow-wrapper is installed.
 * This can be done by setting the environment variable TENSORFLOW_WRAPPPER_HOME, the Java property
 * gate.plugin.learningframework.tensorflowwrapper.home or by adding another yaml file "tensorflow.yaml" 
 * to the data directory which contains the setting tensorflowwrapper.home.
 * If the path starts with a slash
 * it is an absolute path, otherwise the path is resolved relative to the 
 * directory. 
 * 
 * 
 * @author Johann Petrak
 */
public class EngineMBTensorFlowWrapper extends EngineMBPythonNetworksBase {

  static class TensorFLowModel { }
  
  public EngineMBTensorFlowWrapper() {
    WRAPPER_NAME = "TensorFlowWrapper";
    ENV_WRAPPER_HOME = "TENSORFLOW_WRAPPER_HOME";
    PROP_WRAPPER_HOME = "gate.plugin.learningframework.tensorflowwrapper.home";
    YAML_FILE = "tensorflow.yaml";
    YAML_SETTING_WRAPPER_HOME = "tensorflowwrapper.home";
    SCRIPT_APPLY_BASENAME = "tensorflowWrapperApply";
    SCRIPT_TRAIN_BASENAME = "tensorflowWrapperTrain";
    SCRIPT_EVAL_BASENAME = "tensorflowWrapperEval";
    MODEL_BASENAME = "tensorflowmodel";
    MODEL_INSTANCE = new TensorFLowModel();
  }
  
 
  
}
