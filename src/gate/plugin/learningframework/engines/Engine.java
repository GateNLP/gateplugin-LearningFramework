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

import cc.mallet.types.Alphabet;
import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.ModelApplication;
import gate.plugin.learningframework.data.CorpusRepresentation;
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
  @Deprecated
  CorpusRepresentationMallet corpusRepresentationMallet;
  
  /**
   * The corpus representation to use with the concrete instance of the engine.
   */
  CorpusRepresentation corpusRepresentation;
  
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
   * NOTE: the details of how the Engine is initialised once the specific subclass has been 
   * created are implemented in the init() instance method. 
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
    eng.initAfterLoad(directory, parms);
    return eng;
  }
  
  /**
   * The details of how to initialise a specific instance of an Engine subclass from 
   * 
   * 
   * The Engine class implements a default implementation which can be overridden and called 
   * by subclasses. The default implementation does the following: 1) call the instance specific
   * loadModel method, 2) call the instance specific loadCorpusRepresentation method, 3) set the
   * Algorithm according to what is in the info.
   */
  protected void initAfterLoad(File directory, String parms) {
    // now use the specific engine's loadModel method to complete the loading: each engine
    // knows best how to load its own kinds of models.
    // NOTE: loadModel also loads the Mallet corpus representation and initializes any non-Mallet
    // representation if necessary.
    this.loadModel(directory, parms);
    this.loadMalletCorpusRepresentation(directory);
    //System.err.println("Loaded mallet corpus representation: "+eng.getCorpusRepresentationMallet());

    // we could stop growh right after loading, but that would interfere with engines which
    // allow updating, incremental learning etc. 
    // Instead we stop growth at the beginning of each applyModel method and re-enabled it 
    // at the end. 
    // eng.corpusRepresentationMallet.stopGrowth();
    
    Algorithm algorithm = null;
    if(info.algorithmClass != null && !info.algorithmClass.isEmpty()) {
      if(info.algorithmClass.equals(AlgorithmClassification.class.getName())) {
        algorithm = AlgorithmClassification.valueOf(info.algorithmName);      
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
      this.initializeAlgorithm(algorithm,parms);
      this.algorithm = algorithm;
    } // if we have an algorithm class in the info file
    
  }
  
  /**
   * Re-create the mallet corpus representation from the directory.
   * 
   * This was used previously since we always saved a mallet corpus representation.
   * Since we can now save other kinds of corpus representations, this is deprecated.
   * 
   * @param directory 
   */
  @Deprecated
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
    // NOTE: for external algorithms the model will be null
    // or a string at this point, if that is the case, we 
    // do not save the info file here, but expect saveModel to do this!
    if(info.modelClass==null) {
      // do nothing
    } else {
      info.modelClass = model.getClass().getName();
      info.save(directory);
    }
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
  public abstract void trainModel(File dataDirectory, String instanceType, String parms);
  
  public abstract EvaluationResult evaluate(String algorithmParameters,EvaluationMethod evaluationMethod,int numberOfFolds,double trainingFraction,int numberOfRepeats);
  
  protected void updateInfo() {
    //System.err.println("In updateInfo, model is "+model);
    if(model!=null) {
      info.modelClass = model.getClass().getName();
    }
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
  public abstract List<ModelApplication> applyModel(
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
  
  @Deprecated
  public CorpusRepresentationMallet getCorpusRepresentationMallet() {
    return corpusRepresentationMallet;
  }
  
  public CorpusRepresentation getCorpusRepresentation() {
    return corpusRepresentation;
  }
  
  public String toString() {
    return "Engine{"+getClass()+"/"+
            (algorithm==null ? "(null)" : algorithm.getClass())+
            ",alg="+trainer+",info="+info+
            ",model="+this.getModel()+",CR="+corpusRepresentationMallet+"}";
  }
  
  /**
   * Re-create and return the corpus representation that was stored for this engine.
   * 
   *
   * @param dir
   * @return 
   */
  protected CorpusRepresentation recreateCorpusRepresentation(File dir) {
    throw new RuntimeException("Method recreateCorpusRepresentation not yet implemented for "+this.getClass().getName());
  }
  
  
}
