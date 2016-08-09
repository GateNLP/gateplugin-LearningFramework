/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
