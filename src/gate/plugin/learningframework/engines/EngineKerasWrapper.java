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
