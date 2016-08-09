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
public class EngineTensorFlowWrapper extends EnginePythonNetworksBase {

  static class TensorFLowModel { }
  
  public EngineTensorFlowWrapper() {
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
