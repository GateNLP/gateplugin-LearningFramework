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

import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.ModelApplication;
import gate.plugin.learningframework.data.CorpusRepresentation;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.util.GateRuntimeException;
import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * Base class for all engines.
 * This is the base class for all engines. It also provides the static factory methods 
 create and load for creating and re-loading engines.
 * @author Johann Petrak
 */
public abstract class Engine {
  
  public static final String FILENAME_MODEL = "lf.model";
  public static final String FILENAME_PIPE = "lf.pipe";
  
  private static Logger LOGGER = Logger.getLogger(Engine.class);
  
  // =============================================================
  // FIELDS shared by all subclasses and their setters/getters 
  // =============================================================
  
  // TODO: decide which fields should get declared here and which only in Engine subclasses
  // and the accessor method only declared here ...
  
  /**
   * The corpus representation to use with the concrete instance of the engine.
   */
  //CorpusRepresentation corpusRepresentation;
  
  protected Info info;
  
  protected FeatureInfo featureInfo;

  /**
   * Return the info instance for this Engine. 
   * 
   * @return Info instance
   */
  public Info getInfo() { return info; }
  
  public FeatureInfo getFeatureInfo() { return featureInfo; }
  
  /**
   * Get the corpus representation for the engine. This will return the specific subclass of 
   * CorpusRepresentation used for the specific subclass of the Engine. Engine subclasses 
   * define their own field for storing the CorpusRepresentation.
   * 
   * @return  CorpusRepresentation instance
   */
  public abstract CorpusRepresentation getCorpusRepresentation();
  
  // TODO: maybe only declare and store type-specific in each group of Engine subclasses?
  protected Algorithm algorithm;
  public Algorithm getAlgorithm() {
    return algorithm;
  }

  /**
   * The model is the result of the training step and is what is stored/loaded.
   * This will only have a value if an engine was loaded successfully or if training was 
   * completed successfully.
   * <p>
   * TODO: maybe we can make this engine specific and only provide the declaration of an 
   * abstract getModel method here?
   * 
   */
  protected Object model;
  
  public Object getModel() { return model; }
  
  
  /**
   * The trainer is the instance of something that can be used to create a trained model.
   * This should be set right after the EngineXXX instance is created. 
   */
  protected Object trainer;
  public Object getTrainer() { return trainer; }
  
  // ===========================================================================
  // FACTORY METHODS FOR CREATING ENGINE INSTANCES
  // ===========================================================================
  
  /** 
   * A factory method to create a new instance of an engine with the given backend algorithm.
   * 
   * This works in two steps: first the instance of the engine is created, then that instance's
   * method for initializing the algorithm is called (initializeAlgorithm) with the given parameters.
   * However, some training algorithms cannot be instantiated until all the training data is
   * there (e.g. Mallet CRF) - for these, the initializeAlgorithm method does nothing and the
   * actual algorithm initialization happens when the train method is called. 
   * The engine also stores a reference to the Mallet corpus representation (and thus, the Pipe),
   * which enables the Engine to know about the fields and other meta-information.
   * <p>
   * @param algorithm algorithm to use
   * @param parms algorithm parameters
   * @param featureInfo feature info instance
   * @param targetType type of target
   * @param directory data/model directory URL
   * @return  Engine instance
   */
  public static Engine create(Algorithm algorithm, String parms, FeatureInfo featureInfo, TargetType targetType, URL directory) {
    Engine eng;
    try {
      System.err.println("CREATE ENGINE: trying to create for class "+algorithm.getEngineClass());
      @SuppressWarnings("unchecked")
      Constructor<?> tmpc = algorithm.getEngineClass().getDeclaredConstructor();
      eng = (Engine)tmpc.newInstance();
    } catch (IllegalAccessException | IllegalArgumentException | InstantiationException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
      throw new GateRuntimeException("Could not create the Engine "+algorithm.getEngineClass(),ex);
    }
    eng.algorithm = algorithm;
    eng.initializeAlgorithm(algorithm,parms);
    eng.initWhenCreating(directory, algorithm, parms, featureInfo, targetType);    
    eng.info = new Info();
    // we have to prevent a NPE for those algorithms where the trainer class is not stored
    // in the Algorithm instance
    if(algorithm.getTrainerClass()!=null) {
      eng.info.trainerClass = algorithm.getTrainerClass().getName();
    }
    eng.info.engineClass = algorithm.getEngineClass().getName();
    eng.info.task = eng.getAlgorithm().getAlgorithmKind().toString();
    eng.info.algorithmClass = algorithm.getClass().getName();
    eng.info.algorithmName = algorithm.toString();
    eng.algorithm = algorithm;
    return eng;
  }
  
  
  
  /**
   * A factory method to return the engine which is stored in the given directory.
   * All the filenames are fixed so only the directory name is needed.
   * This will first read the info file which contains information about the Engine class,
   * then construct the Engine instance and initialize it.
   * If there are parameters that will influence the initialization of the algorithm,
   * they will be used.
   * <p>
   * NOTE: this will also be used for creating an Engine for those situations
   * where an external model was trained. For this the user has to provide
   * an info.yaml file manually. This file must contain the Engine class,
   * all other entries can be missing.
   * <p>
   * @param directory data/model directory URL
   * @param parms algorithm parameters
   * @return Engine instance
   */
  public static Engine load(URL directory, String parms) {
    // 1) read the info file
    Info info = Info.load(directory);
    // read the feature info file: not all engines do this (YET!) so if there is 
    // no such saved file, we will simply get null here
    FeatureInfo fi = FeatureInfo.load(directory);
    // extract the Engine class from the file and create an instance of the engine
    Engine eng;
    try { 
      //String classname = "gate.plugin.learningframework.engines.EngineMalletClass";
      //System.err.println("Trying literal >"+classname+"<");
      //eng = (Engine)info.getClass().getClassLoader().loadClass(classname).newInstance();
      //System.err.println("Trying fomr var: >"+info.engineClass+"<");      
      //eng = (Engine)info.getClass().getClassLoader().loadClass(info.engineClass.trim()).newInstance();
      eng = (Engine)Class.forName(info.engineClass).getDeclaredConstructor().newInstance();
    } catch (ClassNotFoundException | IllegalAccessException | 
            IllegalArgumentException | InstantiationException | 
            NoSuchMethodException | SecurityException | 
            InvocationTargetException ex) {
      throw new GateRuntimeException("Error creating engine class when loading: "+info.engineClass,ex);
    }
    // store the info we have just obtained in the new engine instance
    eng.info = info;
    eng.initWhenLoading(directory, parms);
    return eng;
  }
  
  
  // ==================================================================
  // SAVING THE ENGINE 
  // ==================================================================
  
  /**
   * Save an engine to a directory.
   * This saves the information about the engine and the training algorithm together with
   * a trained model.
   * It does not make sense to save an engine before all that information is present, a
   * GateRuntimeException is thrown if the engine is not in a state where it can be reasonably 
   * saved.
   * <p>
   * The saving of a specific Engine subclass is influenced by its implementation of the following
   * methods:
   * <ul>
   * <li>
   * </ul>
   * @param directory  TODO
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
    // not all models use this (YET!) so need a NPE guard
    if (featureInfo!=null) {
      featureInfo.save(directory);
    }
    // Then delegate to the engine to save the model
    saveModel(directory);
    // finally save the corpus representation
    saveCorpusRepresentation(directory);
  }
  
  
  // ================================================================
  // INSTANCE-SPECIFIC METHODS THAT NEED TO GET IMPLEMENTED FOR ENGINES
  // =================================================================
  
  // DETAILS OF HOW TO CREATE 
  protected abstract void initWhenCreating(URL directory, Algorithm algorithm, String parms, FeatureInfo featureInfo, TargetType targetType);

  // DETAILS OF HOW TO LOAD
  
  /**
   * The details of how to initialise a specific instance of an Engine subclass.
   * 
   * The Engine class implements a default implementation which can be overridden and called 
   * by subclasses. The default implementation does the following: 1) call the instance specific
   * loadModel method, 2) call the instance specific loadCorpusRepresentation method, 3) set the
   * Algorithm according to what is in the info.
   * 
   * @param directory model/data directory URL
   * @param parms algorithm parameters
   */
  protected void initWhenLoading(URL directory, String parms) {
    // now use the specific engine's loadModel method to complete the loading: each engine
    // knows best how to load its own kinds of models.
    // NOTE: loadModel also loads the Mallet corpus representation and initializes any non-Mallet
    // representation if necessary.
    this.loadModel(directory, parms);
    this.loadAndSetCorpusRepresentation(directory);
    //System.err.println("Loaded mallet corpus representation: "+eng.getCorpusRepresentationMallet());

    // we could stop growh right after loading, but that would interfere with engines which
    // allow updating, incremental learning etc. 
    // Instead we stop growth at the beginning of each applyModel method and re-enabled it 
    // at the end. 
    // eng.corpusRepresentationMallet.stopGrowth();
    
    // TODO: check why we cannot use the algorithm field?
    Algorithm tmp_algorithm = null;
    if(info.algorithmClass != null && !info.algorithmClass.isEmpty()) {
      if(info.algorithmClass.equals(AlgorithmClassification.class.getName())) {
        tmp_algorithm = AlgorithmClassification.valueOf(info.algorithmName);      
      } else if(info.algorithmClass.equals(AlgorithmRegression.class.getName())) {
        tmp_algorithm = AlgorithmRegression.valueOf(info.algorithmName);      
      } else {
        throw new GateRuntimeException("Not a known algorithm enumeration class "+info.algorithmClass);
      }
      if(tmp_algorithm.getTrainerClass()==null) {
        try {
          // NOTE: in case we do not know a trainer class and we also do not have one stored in 
          // the info file, do not create it - sometimes this is simply not necessary for 
          // classification!
          if(info.trainerClass!=null) {
            tmp_algorithm.setTrainerClass(Class.forName(info.trainerClass));
          }
        } catch (ClassNotFoundException ex) {
          throw new GateRuntimeException("Could not find the trainer class "+info.trainerClass);
        }
      }    
      this.initializeAlgorithm(tmp_algorithm,parms);
      this.algorithm = tmp_algorithm;
    } // if we have an algorithm class in the info file
    
  }
  
  
  
  /**
   * Re-create the corpus representation we need for this engine.
   * 
   * This loads the corpus representation from the directory and sets it into the engine instance.
   * This method may use data stored in the directory or the info metadata which already must
   * be set in the engine to figure out what and how to re-create.
   * 
   * @param directory  model/data directory URL
   */
  protected abstract void loadAndSetCorpusRepresentation(URL directory);
  
  
  
  /**
   * Load a stored model into the engine.
   * 
   * NOTE: this also loads and sets the Mallet corpus representation needed for the engine
   * and creates any internal corpus representation from the mallet representation. Since 
   * loading of a model is done for subsequent classification, the internal representaiton
   * may be one where the class attribute is deliberately left away.
   * 
   * @param directory model/data/ directory URL
   * @param parms parameters
   */
  protected abstract void loadModel(URL directory, String parms);
  
  

  // DETAILS OF HOW TO SAVE
  
  /**
   * Save the corpus representation used by the engine instance in some way to the directory.
   * 
   * For some representations this may be a null action.
   * 
   * @param directory model/data directory URL
   */
  protected abstract void saveCorpusRepresentation(File directory);
  
  
  protected abstract void saveModel(File directory);
  
  // DETAILS OF HOW TO USE THE ENGINE 
  
  /**
   * Train a model from the instances.
   * 
   * This always takes our own representation of instances (which is a Mallet InstanceList ATM).
   * The Engine instance should know best how to use or convert that representation to its own
   * format, using one of the CorpusRepresentationXXX classes.
   * 
   * @param dataDirectory model/data directory URL
   * @param instanceType instance annotation type
   * @param parms parameters
   */
   public abstract void trainModel(File dataDirectory, String instanceType, String parms);
  
  public abstract EvaluationResult evaluate(String algorithmParameters,EvaluationMethod evaluationMethod,int numberOfFolds,double trainingFraction,int numberOfRepeats);
  
  
  /**
   * Classify all instance annotations.
   * 
   * If the algorithm is a sequence tagger, the sequence annotations must be given, otherwise
   * they must not be given. 
   * 
   * @param instanceAS instance annotation set
   * @param inputAS input annotation set
   * @param sequenceAS sequence annotation set
   * @param parms parameters
   * @return list of actions to carry out on the document
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
  
  
  
  @Override
  public String toString() {
    return "Engine{"+getClass()+"/"+
            (algorithm==null ? "(null)" : algorithm.getClass())+
            ",alg="+trainer+",info="+info+
            ",model="+this.getModel()+",CR="+getCorpusRepresentation()+"}";
  }
  
  /**
   * Re-create and return the corpus representation that was stored for this engine.
   * 
   * @param dir data/mdoel directory URL
   * @return corpus representation
   */
  protected CorpusRepresentation recreateCorpusRepresentation(File dir) {
    throw new RuntimeException("Method recreateCorpusRepresentation not yet implemented for "+this.getClass().getName());
  }
  
  
}
