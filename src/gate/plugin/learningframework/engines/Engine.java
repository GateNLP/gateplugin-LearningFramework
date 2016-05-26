/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.engines;

import cc.mallet.types.Alphabet;
import cc.mallet.types.InstanceList;
import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.GateClassification;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Base class for all engines.
 * This is the base class for all engines. It also provides the static factory method 
 * loadEngine(directory) which will return a subclass of the appropriate type, if possible.
 * @author Johann Petrak
 */
public abstract class Engine {
  
  public static final String FILENAME_MODEL = "lf.model";
  public static final String FILENAME_PIPE = "lf.pipe";
  
  private static Logger logger = Logger.getLogger(Engine.class);
  
  /**
   * The Mallet Corpus Representation associated with this engine. 
   */
  CorpusRepresentationMallet corpusRepresentationMallet;
  
  Algorithm algorithm;
  
  /**
   * A factory method to return the engine which is stored in the given directory.
   * All the filenames are fixed so only the directory name is needed.
   * This will first read the info file which contains information about the Engine class,
   * then construct the Engine instance and initialize it.
   * If there are parameters that will influence the initialization of the algorithm,
   * they will be used.
   * 
   * NOTE: this will also be used for creating an Engine for those situations
   * where an external model was trained. For this the user has to provide
   * an info.yaml file manually. This file must contain the Engine class,
   * all other entries can be missing.
   * 
   * @param directory
   * @return 
   */
  public static Engine loadEngine(File directory, String parms) {
    // 1) read the info file
    Info info = Info.load(directory);
    // extract the Engine class from the file and create an instance of the engine
    Engine eng;
    try { 
      //String classname = "gate.plugin.learningframework.engines.EngineMalletClass";
      //System.err.println("Trying literal >"+classname+"<");
      //eng = (Engine)info.getClass().getClassLoader().loadClass(classname).newInstance();
      //System.err.println("Trying fomr var: >"+info.engineClass+"<");      
      //eng = (Engine)info.getClass().getClassLoader().loadClass(info.engineClass.trim()).newInstance();
      eng = (Engine)Class.forName(info.engineClass).newInstance();
    } catch (Exception ex) {
      throw new GateRuntimeException("Error creating engine class when loading: "+info.engineClass,ex);
    }
    // store the info we have just obtained in the new engine instance
    eng.info = info;
    // now use the specific engine's loadModel method to complete the loading: each engine
    // knows best how to load its own kinds of models.
    // NOTE: loadModel also loads the Mallet corpus representation and initializes any non-Mallet
    // representation if necessary.
    eng.loadModel(directory, parms);
    eng.loadMalletCorpusRepresentation(directory);

    // we could stop growh right after loading, but that would interfere with engines which
    // allow updating, incremental learning etc. 
    // Instead we stop growth at the beginning of each classify method and re-enabled it 
    // at the end. 
    // eng.corpusRepresentationMallet.stopGrowth();
    
    Algorithm algorithm = null;
    if(info.algorithmClass != null && !info.algorithmClass.isEmpty()) {
      if(info.algorithmClass.equals(AlgorithmClassification.class.getName())) {
        algorithm = AlgorithmClassification.valueOf(info.algorithmName);      
      } else if(info.algorithmClass.equals(AlgorithmSequenceTagging.class.getName())) {
        algorithm = AlgorithmSequenceTagging.valueOf(info.algorithmName);
      } else if(info.algorithmClass.equals(AlgorithmRegression.class.getName())) {
        algorithm = AlgorithmRegression.valueOf(info.algorithmName);      
      } else {
        throw new GateRuntimeException("Not a known algorithm enumeration class "+info.algorithmClass);
      }
      if(algorithm.getTrainerClass()==null) {
        try {
          // NOTE: in case we do not know a trainer class and we also do not have one stored in 
          // the info file, do not create it - sometimes this is simply not necessary for 
          // classification!
          if(info.trainerClass!=null) {
            algorithm.setTrainerClass(Class.forName(info.trainerClass));
          }
        } catch (Exception ex) {
          throw new GateRuntimeException("Could not find the trainer class "+info.trainerClass);
        }
      }    
      eng.initializeAlgorithm(algorithm,parms);
      eng.algorithm = algorithm;
    } // if we have an algorithm class in the info file
    return eng;
  }
  
  protected abstract void loadMalletCorpusRepresentation(File directory);
  
  
  
  /**
   * Save an engine to a directory.
   * This saves the information about the engine and the training algorithm together with
   * a trained model.
   * It does not make sense to save an engine before all that information is present, a
   * GateRuntimeException is thrown if the engine is not in a state where it can be reasonably 
   * saved.
   * @param directory 
   */
  public void saveEngine(File directory) {
    // First save the info, but before that, update the info!
    info.modelClass = model.getClass().getName();
    info.save(directory);
    // Then delegate to the engine to save the model
    saveModel(directory);
    // finally save the Mallet corpus representation
    corpusRepresentationMallet.savePipe(directory);
  }
  
  
  /** 
   * A factory method to create a new instance of an engine with the given backend algorithm.
   * This works in two steps: first the instance of the engine is created, then that instance's
   * method for initializing the algorithm is called (initializeAlgorithm) with the given parameters.
   * However, some training algorithms cannot be instantiated until all the training data is
   * there (e.g. Mallet CRF) - for these, the initializeAlgorithm method does nothing and the
   * actual algorithm initialization happens when the train method is called. 
   * The engine also stores a reference to the Mallet corpus representation (and thus, the Pipe),
   * which enables the Engine to know about the fields and other meta-information.
   * @return 
   */
  public static Engine createEngine(Algorithm algorithm, String parms, CorpusRepresentationMallet crm) {
    Engine eng;
    try {
      System.err.println("CREATE ENGINE: trying to create for class "+algorithm.getEngineClass());
      eng = (Engine)algorithm.getEngineClass().newInstance();
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not create the Engine "+algorithm.getEngineClass(),ex);
    }
    eng.initializeAlgorithm(algorithm,parms);
    eng.corpusRepresentationMallet = crm;
    eng.info = new Info();
    // we have to prevent a NPE for those algorithms where the trainer class is not stored
    // in the Algorithm instance
    if(algorithm.getTrainerClass()!=null) {
      eng.info.trainerClass = algorithm.getTrainerClass().getName();
    }
    eng.info.engineClass = algorithm.getEngineClass().getName();
    eng.info.task = eng.getAlgorithmKind().toString();
    eng.info.algorithmClass = algorithm.getClass().getName();
    eng.info.algorithmName = algorithm.toString();
    eng.algorithm = algorithm;
    return eng;
  }
  
  
  
  /**
   * Load a stored model into the engine.
   * NOTE: this also loads and sets the Mallet corpus representation needed for the engine
   * and creates any internal corpus representation from the mallet representation. Since 
   * loading of a model is done for subsequent classification, the internal representaiton
   * may be one where the class attribute is deliberately left away.
   * @param directory
   * @param info 
   */
  protected abstract void loadModel(File directory, String parms);
  
  protected abstract void saveModel(File directory);
  
  
  /**
   * Train a model from the instances.
   * This always takes our own representation of instances (which is a Mallet InstanceList ATM).
   * The Engine instance should know best how to use or convert that representation to its own
   * format, using one of the CorpusRepresentationXXX classes.
   */
  public abstract void trainModel(String parms);
  
  public abstract EvaluationResult evaluate(String algorithmParameters,EvaluationMethod evaluationMethod,int numberOfFolds,double trainingFraction,int numberOfRepeats);
  
  protected void updateInfo() {
    info.nrTrainingInstances = corpusRepresentationMallet.getRepresentationMallet().size();
    info.nrTrainingDimensions = corpusRepresentationMallet.getRepresentationMallet().getDataAlphabet().size();    
    LFPipe pipe = (LFPipe)corpusRepresentationMallet.getPipe();
    Alphabet targetAlph = pipe.getTargetAlphabet();
    if(targetAlph == null) {
      info.nrTargetValues = 0;
    } else {
      info.nrTargetValues = targetAlph.size();
      //info.classLabels = 
      Object[] objs = targetAlph.toArray();
      ArrayList<String> labels = new ArrayList<String>();
      for(Object obj : objs) { labels.add(obj.toString()); }
      info.classLabels = labels;
    }
    
  }
  
  /**
   * Classify all instance annotations.
   * If the algorithm is a sequence tagger, the sequence annotations must be given, otherwise
   * they must not be given. 
   * @return 
   */
  public abstract List<GateClassification> classify(
          AnnotationSet instanceAS, AnnotationSet inputAS,
          AnnotationSet sequenceAS, String parms);
  
  public abstract void initializeAlgorithm(Algorithm algorithm, String parms);
  
  // fields shared by all subclasses of Engine:
  // The following fields are present in each subclass, but not inherited from this class 
  // but defined with the Engine-specific types.
  
  // TODO: not sure if this will work for all situations, we may have to distinguish between
  // trainer, applier, and model
  
  /**
   * The model is the result of the training step and is what is stored/loaded.
   * This will only have a value if an engine was loaded successfully or if training was 
   * completed successfully.
   */
  protected Object model;
  
  public Object getModel() { return model; }
  
  public AlgorithmKind getAlgorithmKind() { 
    // Most algorithms are classification algorithm, those which are not will return 
    // a different value. This does not tell about what task (classification, sequence tagging etc.)
    // the algorithm is used for, it says what the ability of the algorithm is, and hence what kind
    // of independent features or targets it needs to handle.
    return AlgorithmKind.CLASSIFIER; 
  }
  
  /**
   * The trainer is the instance of something that can be used to create a trained model.
   * This should be set right after the EngineXXX instance is created.
   */
  protected Object trainer;
  public Object getTrainer() { return trainer; }
  
  protected Info info;
  
  public Info getInfo() { return info; }
  
  public CorpusRepresentationMallet getCorpusRepresentationMallet() {
    return corpusRepresentationMallet;
  }
  
  public String toString() {
    return "Engine{"+getClass()+"/"+
            (algorithm==null ? "(null)" : algorithm.getClass())+
            ",alg="+trainer+",info="+info+
            ",model="+this.getModel()+",CR="+corpusRepresentationMallet+"}";
  }
  
}
