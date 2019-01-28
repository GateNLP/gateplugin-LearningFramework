/*
 *  Copyright (c) The University of Sheffield.
 *
 *  This file is free software, licensed under the 
 *  GNU Library General Public License, Version 2.1, June 1991.
 *  See the file LICENSE.txt that comes with this software.
 *
 */
package gate.plugin.learningframework.engines;

import com.fasterxml.jackson.databind.ObjectMapper;
import gate.Annotation;
import gate.AnnotationSet;
import gate.lib.interaction.process.Process4StringStream;
import gate.lib.interaction.process.ProcessBase;
import gate.lib.interaction.process.ProcessSimple;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.ModelApplication;
import gate.plugin.learningframework.data.CorpusRepresentation;
import gate.plugin.learningframework.data.CorpusRepresentationVolatileDense2JsonStream;
import gate.plugin.learningframework.data.InstanceRepresentation;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.util.Files;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Common base class for all Engines which are dense, volatile and write JSON to a file.
 * 
 * <p>
 * See <a href="https://github.com/GateNLP/gateplugin-LearningFramework/wiki/Class_EngineDVFileJson">Wiki</a>
 * 
 * @author Johann Petrak
 */
public abstract class EngineDVFileJson extends EngineDV {
  
  
  // NOTEs about how to find python:
  // On linux, python is usually on the path, but we cannot be sure if it is python 3
  // In some cases, python3 is the version 3 command.
  // On Windows (10) if installing using anaconda3, the installer recommends NOT
  // putting python on the path and to install for the user only by default.
  // !! In that case, it gets installed by default into C:\\Users\\username\\Anaconda3 which 
  // contains python.exe, but things like ipython.exe are in the Scripts subdirectory.
  // If installing for all, it gets installed by default into C:\\ProgramData\\Anaconda3
  // If installing pythong from python.org we have the options:
  // 1) Windows executable installer (python-3.6.5-amd64.exe): 
  // this one recommends to install for all users but does not add to PATH by default
  // Also recommends to disable the path length limit
  // Installing for all: This gets installed into C:\\Users\\username\\AppData\\Local\\Programs\\Python\\Python36
  // which contains python, Scripts contains pip
  // 2) Web-based installer (python-3.6.5-amd64-webinstall.exe): seems to use the same location
  // (could not test after already installed using the other installer)
  
  // So in order to find python we do the following:
  // 1) check if there is a config file in the data dir, use the pythonhome variable from there
  // 2) check if the PYTHON_BIN environment variable is set, use it as full path to the executable
  // 3) try to find it on the executable path (this is not easy in Java, instead try to run python/python3
  //    or python.exe with parameter "-V" to get version. May use Runtime.getRuntime().exec("...") in a 
  //    try catch for that or own interaction library to get back the output.
  // 3) if on Windows, check one of the two paths above
  // 4) if on Linux check in decreasing order of importance: /usr/bin/python3 or /usr/bin/python
  
  
  // Wrapper name: this is set by the actual implementing engine class and 
  // will influence the file name of scripts, config files etc specific to that
  // wrapper.
  protected String WRAPPER_NAME = "WRAPPER_NAME MUST BE OVERRIDEN BY IMPLEMENTING SUBCLASS";
  protected String MODEL_BASENAME = "MODEL_BASENAME MUST BE OVERRIDEN BY IMPLEMENTING SUBCLASS";
  
  // all implementing subclasses will follow these name conventions
  protected String COMMAND_BASE_TRAIN = "train";
  protected String COMMAND_BASE_APPLY = "apply";
  
  protected File dataDir; // the model/data directory as specified when creating the engine but as a File
  
  protected String getWrapperHome()  {
    File wrapperRoot = new File(dataDir, WRAPPER_NAME);
    return wrapperRoot.getAbsolutePath();
  }
  
  /**
   * Get the command path to run for training.
   * 
   * @return the training command path
   */
  protected String getCommandPathTrain()  {
    File wrapperRoot = new File(dataDir, WRAPPER_NAME);
    String ext = getShellExtension();
    File cmd = new File(wrapperRoot, COMMAND_BASE_TRAIN+ext);
    return cmd.getAbsolutePath();
  }

  /**
   * Get the command path to run for application.
   * 
   * @return application command path
   */
  protected String getCommandPathApply() {
    File wrapperRoot = new File(dataDir, WRAPPER_NAME);
    String ext = getShellExtension();
    File cmd = new File(wrapperRoot, COMMAND_BASE_APPLY + ext);
    return cmd.getAbsolutePath();
  }
  
  /**
   * Get the shell extensions for the operating system.
   * @return shell extension, including the dot
   */
  protected String getShellExtension() {
    return getOsType() == OsType.LINUXLIKE ? ".sh" : ".cmd";
  }
  
  /**
   * Return LINUXLIKE, WINDOWSLIKE, or throw an exception for anything else
   * 
   * @return  OS type
   */
  protected OsType getOsType() {
    boolean linuxLike = System.getProperty("file.separator").equals("/");
    boolean windowsLike = System.getProperty("file.separator").equals("\\");
    if(linuxLike) {
      return OsType.LINUXLIKE;
    } else if(windowsLike) {
      return OsType.WINDOWSLIKE;
    } else {
      throw new GateRuntimeException("It appears this OS is not supported");
    }
    
  }
  
  /**
   * Known OS types.
   */
  public enum OsType {
    WINDOWSLIKE,
    LINUXLIKE
  }
  
  /**
   * Read a WRAPPERNAME.yaml file from the data dir, if it exists.
   * @return the map of settings 
   */
  @SuppressWarnings("unchecked")
  public Map<String,String> getWrapperConfig() {
    File wrapperInfoFile = new File(dataDir,WRAPPER_NAME+".yaml");
    if(!wrapperInfoFile.exists()) {
      // Windows is just insane and hides a txt extension if a user creates 
      // a "text file" with a yaml extensions, so lets allow .yaml.txt as well
      wrapperInfoFile = new File(dataDir,WRAPPER_NAME+".yaml.txt");
    }
    // System.err.println("DEBUG: wrapper file: "+wrapperInfoFile.getAbsolutePath());
    if(wrapperInfoFile.exists()) {
      // System.err.println("DEBUG: seems to exist ...");
      Yaml yaml = new Yaml();
      Object obj;
      try {
        obj = yaml.load(new InputStreamReader(new FileInputStream(wrapperInfoFile),"UTF-8"));
      } catch (FileNotFoundException | UnsupportedEncodingException ex) {
        throw new GateRuntimeException("Could not load yaml file "+wrapperInfoFile,ex);
      }    
      Map<String,String> map = null;
      if(obj instanceof Map) {
        map = (Map<String,String>)obj;
        // System.err.println("DEBUG: got map: "+map);
      } else {
        throw new GateRuntimeException("Info file has strange format: "+wrapperInfoFile.getAbsolutePath());
      }
      return map;
    } else {
      // System.err.println("DEBUG: does not exist, returning empty map");
      return new HashMap<>();
    }
    
  }
  
  protected ProcessBase process;
  
  
  // For this engine, this will always be a CorpusRepresentationVolatileDense2JsonStream
  protected CorpusRepresentationVolatileDense2JsonStream corpusRepresentation;
  
  @Override
  public CorpusRepresentation getCorpusRepresentation() { 
    return corpusRepresentation; 
  }
  
  @Override
  protected void initWhenCreating(URL directory, Algorithm algorithm, 
          String parms, FeatureInfo featureInfo, TargetType targetType) {
    dataDir = Files.fileFromURL(directory);
    this.featureInfo = featureInfo;
    corpusRepresentation = new CorpusRepresentationVolatileDense2JsonStream(dataDir, featureInfo);
    corpusRepresentation.startAdding();
    this.featureInfo = featureInfo;
    // NOTE: we are copying the wrapper code only when starting training, not
    // here. This allows the user to copy their own code while the PR is running
    // but creating the corpus has not yet finished.
  }

  @Override
  protected void loadAndSetCorpusRepresentation(URL directory) {
    //System.err.println("DEBUG EngineDVFileJson: running loadAndSetCorpusRepresentation "+directory);
    
    // this does not actually need to load anything but the featureInfo ... 
    // this is needed to convert our instance data to JSON, which is then sent
    // off to the script or server which is responsible to use any other saved
    // model info (the model itself, scaling info, vocab info, embeddings etc)
    dataDir = Files.fileFromURL(directory);
    featureInfo = FeatureInfo.load(directory);
    corpusRepresentation = new CorpusRepresentationVolatileDense2JsonStream(dataDir, featureInfo);
  }

  protected String getDefaultPythonBin() {
    // TODO: depending on OS and the result of doing the equivalent of "which python"
    // provide some useful default here
    if(getOsType()==OsType.LINUXLIKE) {
      return "python";
    } else {
      // On windows, use C:\\User\\username\\Miniconda3\python.exe 
      String drive = System.getenv("HOMEDRIVE");
      // System.err.println("DEBUG: Windows drive is "+drive);
      String path = System.getenv("HOMEPATH");
      // System.err.println("DEBUG: Windows home path is "+path);
      return drive + path + "\\Miniconda3\\python.exe";
    }
  }
  
  @Override
  protected void loadModel(URL directory, String parms) {
    loadAndSetCorpusRepresentation(directory);
    // the loadModel method should get called before all the applyModel
    // calls, so here we can start the external process with which we communicate
    // in applyModel
    
    // first, check if the wrapper is present. Normally this should be the case,
    // but sometimes it may be required to update the wrapper on purpose, e.g.
    // for debugging or for a bugfix. This can be achieved by removing the wrapper
    // directory and running apply again which will then re-install the wrapper 
    // here.
    if(!new File(dataDir,"WRAPPER_NAME").exists()) {
      Utils4Engines.copyWrapper(WRAPPER_NAME, dataDir);
    }

    
    // Start the process
    ArrayList<String> finalCommand = new ArrayList<>();
    String modelBaseName = new File(dataDir, WRAPPER_NAME+".model").getAbsolutePath();
    finalCommand.add(getCommandPathApply());
    finalCommand.add(modelBaseName);
    finalCommand.add(corpusRepresentation.getMetaFile().getAbsolutePath());
    finalCommand.add(new File(dataDir,WRAPPER_NAME).getAbsolutePath());
    if(!parms.trim().isEmpty()) {
      String[] tmp = parms.split("\\s+",-1);
      finalCommand.addAll(Arrays.asList(tmp));
    }
    // if we have a shell command prepend that, and if we have shell parms too, include them
    Map<String,String> config = getWrapperConfig();
    String shellcmd = config.get("shellcmd");
    String shellparms = config.get("shellparms");
    if(shellcmd != null) {
      finalCommand.add(0,shellcmd);
      if(shellparms != null) {
        String[] sps = shellparms.trim().split("\\s+");
        int i=0; for(String sp : sps) { finalCommand.add(++i,sp); }
      }
    }
    //System.err.println("Running: ");
    //for(int i=0; i<finalCommand.size();i++) {
    //  System.err.println(i+": >"+finalCommand.get(i)+"<");
    //}
    Map<String,String> env = new HashMap<>();
    env.put("WRAPPER_HOME",getWrapperHome());
    env.put("GATE_LF_DATA_DIR", dataDir.getAbsolutePath());
    String pythonbin = config.get("PYTHON_BIN");
    if (pythonbin != null) {
      env.put("PYTHON_BIN", pythonbin);
    } else {
      env.put("PYTHON_BIN", getDefaultPythonBin());
    }
    process = Process4StringStream.create(dataDir,env,finalCommand);
    
  }

  @Override
  protected void saveCorpusRepresentation(File directory) {
  }

  @Override
  protected void saveModel(File directory) {
    // this is all handled by the script we are running for training, nothing 
    // needed in here.
  }

  // NOTE: if we already pass and initialise the dataDir when initialising, we
  // do not need the file as a parameter here??? Refactor to remove this parm!
  @Override
  public void trainModel(File dataDirectory, String instanceType, String parms) {    
    // first of all close the corpus and save the metadata
    corpusRepresentation.finishAdding();
    if (corpusRepresentation.nrInstances()==0) {
      throw new RuntimeException("No training instances found in the corpus, cannot train!");
    }
    // update the info instance with stuff we should know now
    info.classLabels = corpusRepresentation.getTargetLabels();
    info.nrTargetValues = info.classLabels.size();
    info.nrTrainingDimensions = corpusRepresentation.getNrFeatures();
    
    // first of all copy the wrapper files into the data directory if needed
    Utils4Engines.copyWrapper(WRAPPER_NAME, dataDir);
    
    ArrayList<String> finalCommand = new ArrayList<>();
    String modelBaseName = new File(dataDir, WRAPPER_NAME+".model").getAbsolutePath();
    finalCommand.add(getCommandPathTrain());
    finalCommand.add(corpusRepresentation.getMetaFile().getAbsolutePath());
    finalCommand.add(modelBaseName);
    if(!parms.trim().isEmpty()) {
      String[] tmp = parms.split("\\s+",-1);
      finalCommand.addAll(Arrays.asList(tmp));
    }
    // if we have a shell command prepend that, and if we have shell parms too, include them
    Map<String,String> config = getWrapperConfig();
    String shellcmd = config.get("shellcmd");
    String shellparms = config.get("shellparms");
    if(shellcmd != null) {
      finalCommand.add(0,shellcmd);
      if(shellparms != null) {
        String[] sps = shellparms.trim().split("\\s+");
        int i=0; for(String sp : sps) { finalCommand.add(++i,sp); }
      }
    }
    //System.err.println("Running: ");
    //for(int i=0; i<finalCommand.size();i++) {
    //  System.err.println(i+": >"+finalCommand.get(i)+"<");
    //}
    // Create a fake Model jsut to make LF_Apply... happy which checks if this is null
    model = new Object();
    Map<String,String> env = new HashMap<>();
    env.put("WRAPPER_HOME",getWrapperHome());
    env.put("GATE_LF_DATA_DIR", dataDir.getAbsolutePath());
    String pythonbin = config.get("PYTHON_BIN");
    // System.err.println("DEBUG: config python bin: "+pythonbin);
    if (pythonbin != null) {
      // System.err.println("DEBUG: python bin from config: "+pythonbin);
      env.put("PYTHON_BIN", pythonbin);
    } else {
      env.put("PYTHON_BIN", getDefaultPythonBin());
      // System.err.println("DEBUG: python bin from default: "+getDefaultPythonBin());
    }    
    process = ProcessSimple.create(dataDir,env,finalCommand);
    process.waitFor();
    
    // we also need to save the updated info file
    info.nrTrainingInstances = corpusRepresentation.nrInstances();
    info.engineClass = this.getClass().getName();
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    info.modelWhenTrained = sdf.format(new Date());
    
    info.save(dataDir);    
    featureInfo.save(dataDir);
  }

  @Override
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    throw new UnsupportedOperationException("Not supported (yet?)"); 
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ModelApplication> applyModel(AnnotationSet instancesAS, AnnotationSet inputAS, AnnotationSet sequenceAS, String parms) {
    //System.err.println("DEBUG: running applyModel");
    ObjectMapper mapper = new ObjectMapper();
    List<ModelApplication> modelapps = new ArrayList<>();
    
    if(sequenceAS==null) {
      // non-sequences
      List<Annotation> instanceAnnotations = instancesAS.inDocumentOrder();
      
      // We have two choices here: send each instace separately or send them all
      // together in one go. For now we send each instance separately.
      // TODO: figure out which mode is better/faster!!
      for (Annotation instanceAnnotation : instanceAnnotations) {

        InstanceRepresentation inst = 
                corpusRepresentation.unlabeledAnnotation2Instance(instanceAnnotation, inputAS, null);
        String json = corpusRepresentation.internal2Json(inst,true);        
        //System.err.println("DEBUG - sending json: "+json);
        process.writeObject(json);
        //System.err.println("DEBUG - before reading response");
        String returnJson = (String)process.readObject();
        //System.err.println("DEBUG - received return json: "+returnJson);
        Object obj = null;
        try {
          obj = mapper.readValue(returnJson,Map.class);          
        } catch (IOException ex) {
          throw new GateRuntimeException("Could not interpret response json: "+returnJson,ex);
        }
        // we always expect a map as a response!
        Map<String,Object>retMap = (Map<String,Object>)obj;
        // we always expect these keys, having these types!
        String status = (String)retMap.get("status");
        if(status==null) {
          status = "";
        }
        if(!"ok".equals(status.toLowerCase())) {
          // try to get the exception from json
          String exc = (String)retMap.get("error");
          throw new GateRuntimeException("Something went wrong applying the model, got status: "+status+
                  " error is: "+exc);
        }
        
        // Here we do NOT have a sequence tagging problem, so for now, the only
        // thing we should get is the label and the scores, which should be
        // a list of nclasses values. In addition we get the labels array so 
        // we know which score belongs to which label
        // TODO: we should retrieve the label array only once through a special command!
        ModelApplication ma = null;
        if(info.task.equals(AlgorithmKind.REGRESSOR.toString())) {
          throw new GateRuntimeException("Not implemented yet: task REGRESSION");
          // NOTE: this is not actually supported yet, we do not support REGRESSION
          /*
          Double output = (Double)retMap.get("output");
          if(output==null) {
            throw new GateRuntimeException("Did not get a regression result from model");
          }
          // NOTE: eventually we may get variance or confidence interval boundaries here: "ci_upper"/"ci_lower"/"ci_p"
          // Double variance = (Double)retMap.get("variance");
          ma = new ModelApplication(instanceAnnotation,output); 
          */
        } else if(info.task.equals(AlgorithmKind.CLUSTERING.toString())) {
          throw new GateRuntimeException("Not implemented yet: task CLUSTERING");
        } else if(info.task.equals(AlgorithmKind.CLASSIFIER.toString())) {
          String output = (String)retMap.get("output");
          if(output==null) {
            throw new GateRuntimeException("Did not get a classification result from model");
          }
          Double conf = (Double)retMap.get("conf");
          List<Double> dist = (List<Double>)retMap.get("dist");
          List<String> labels = (List<String>)retMap.get("labels");
          ma = new ModelApplication(instanceAnnotation,output, conf, labels, dist);
        } else if(info.task.equals(AlgorithmKind.SEQUENCE_TAGGER.toString())) {
          // error: if no sequence AS is specified we should not get this!
          throw new GateRuntimeException("Model application not possible: no sequenceAS but model expects it!");
        }
        modelapps.add(ma);
      }      
    } else {
      // sequences
      // Again, we could send the data for all sequences in one go but for
      // now we just send each sequence separately.
      // TODO: figure out what is better!
      // DONE: we generally send instances separately. This is mainly for the non-sequence
      // case where we may need to inject the prediction of the previous instance, which is
      // really only properly possible if we handle each instance separately.
      for(Annotation sequenceAnn : sequenceAS) {
        int seq_id = sequenceAnn.getId();
        List<Annotation> instanceAnnotations = gate.Utils.getContainedAnnotations(
              instancesAS, sequenceAnn).inDocumentOrder();        
        List<InstanceRepresentation> insts4seq
                = corpusRepresentation.unlabeledInstancesForSequence(instancesAS, sequenceAnn, inputAS);
        String json = corpusRepresentation.internal2Json(insts4seq,true);

        process.writeObject(json);
        // TODO: need to decide on the format of the response. Probably best to
        // expect a map with both data and metadata
        String returnJson = (String)process.readObject();
        
        Object obj = null;
        try {
          obj = mapper.readValue(returnJson,Map.class);          
        } catch (IOException ex) {
          throw new GateRuntimeException("Could not interpret response json: ",ex);
        }
        // we always expect a map as a response!
        Map<String,Object>retMap = (Map<String,Object>)obj;
        // we always expect these keys, having these types!
        String status = (String)retMap.get("status");
        if(status==null) {
          status = "";
        }
        if(!"ok".equals(status.toLowerCase())) {
          String exc = (String)retMap.get("error");
          throw new GateRuntimeException("Something went wrong applying the model, got status: "+status+
                  " error is: "+exc);
        }
        
        // we expect output to be a list of string and if confidence exists, a list of double
        List<String> output = (List<String>)retMap.get("output");
        if(output==null) {
          throw new GateRuntimeException("Did not get a classification result from model");
        }
        // note: the confidence actually may be null (missing in the map) meaning we do not have it
        List<Double>confidence = (List<Double>)retMap.get("conf");
        List<String>labels = (List<String>)retMap.get("labels");
        List<List<Double>>dist = (List<List<Double>>)retMap.get("dist");
        
        ModelApplication ma;
        if(info.task.equals(AlgorithmKind.SEQUENCE_TAGGER.toString())) {
          // we need to get back as many labels as there are instances in the insts4seq list
          int i = 0;
          for(Annotation ann : instanceAnnotations) {
            // expects class, confidence, sequence span id
            Double conf = null;
            if(confidence!=null) {
              conf=confidence.get(i);
            }
            if(dist!=null && labels != null) {
              if(dist.get(i) != null) {
                ma = new ModelApplication(ann, output.get(i), conf, labels, dist.get(i), seq_id);
              } else {
                ma = new ModelApplication(ann, output.get(i), conf, seq_id);
              }
            } else {
              ma = new ModelApplication(ann, output.get(i), conf, seq_id);
            }
            modelapps.add(ma);
            i++;
          }
        } else {
          // error: sequence AS is specified but this is not a Sequence tagger model
          throw new GateRuntimeException("Model application not possible: sequenceAS specified but model does not expect it!");
        }        
      }
    }
    
    // * use the predictions to create the return list
    // * TODO: how do we terminate the process again?
    return modelapps;
  }

  @Override
  public void initializeAlgorithm(Algorithm algorithm, String parms) {
    // TODO
  }

  
  protected void updateInfo() {
    // TODO:    
  }
  
}