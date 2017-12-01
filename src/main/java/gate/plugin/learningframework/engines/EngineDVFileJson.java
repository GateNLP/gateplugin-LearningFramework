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
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.yaml.snakeyaml.Yaml;

/**
 * Common base class for all Engines which are dense, volatile and write JSON to a file.
 * 
 * 
 * @author Johann Petrak
 */
public abstract class EngineDVFileJson extends EngineDV {
  
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
  
  
  protected String getCommandPathTrain()  {
    File wrapperRoot = new File(dataDir, WRAPPER_NAME);
    String ext = getShellExtension();
    File cmd = new File(wrapperRoot, COMMAND_BASE_TRAIN+ext);
    return cmd.getAbsolutePath();
  }
  protected String getCommandPathApply() {
    File wrapperRoot = new File(dataDir, WRAPPER_NAME);
    String ext = getShellExtension();
    File cmd = new File(wrapperRoot, COMMAND_BASE_APPLY + ext);
    return cmd.getAbsolutePath();
  }
  
  protected String getShellExtension() {
    return getOsType() == OsType.LINUXLIKE ? ".sh" : ".cmd";
  }
  
  /**
   * Return LINUXLIKE, WINDOWSLIKE, or throw an exception for anything else
   * 
   * @return 
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
  
  public enum OsType {
    WINDOWSLIKE,
    LINUXLIKE
  }
  
  /**
   * Read a WRAPPERNAME.yaml file from the data dir, if it exists.
   * @return 
   */
  public Map<String,String> getWrapperConfig() {
    File wrapperInfoFile = new File(dataDir,WRAPPER_NAME+".yaml");
    if(wrapperInfoFile.exists()) {
      Yaml yaml = new Yaml();
      Object obj;
      try {
        obj = yaml.load(new InputStreamReader(new FileInputStream(wrapperInfoFile),"UTF-8"));
      } catch (Exception ex) {
        throw new GateRuntimeException("Could not load yaml file "+wrapperInfoFile,ex);
      }    
      Map map = null;
      if(obj instanceof Map) {
        map = (Map<String,String>)obj;
      } else {
        throw new GateRuntimeException("Info file has strange format: "+wrapperInfoFile.getAbsolutePath());
      }
      return map;
    } else {
      return new HashMap<String,String>();
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
  protected void initWhenCreating(URL directory, Algorithm algorithm, String parms, FeatureInfo featureInfo, TargetType targetType) {
    dataDir = Files.fileFromURL(directory);
    this.featureInfo = featureInfo;
    corpusRepresentation = new CorpusRepresentationVolatileDense2JsonStream(dataDir, featureInfo);
    corpusRepresentation.startAdding();
    // NOTE: we are copying the wrapper code only when starting training, not
    // here. This allows the user to copy their own code while the PR is running
    // but creating the corpus has not yet finished.
  }

  @Override
  protected void loadAndSetCorpusRepresentation(URL directory) {
    System.err.println("DEBUG EngineDVFileJson: running loadAndSetCorpusRepresentation "+directory);
    // this does not actually need to load anything but the featureInfo ... 
    // this is needed to convert our instance data to JSON, which is then sent
    // off to the script or server which is responsible to use any other saved
    // model info (the model itself, scaling info, vocab info, embeddings etc)
    dataDir = Files.fileFromURL(directory);
    featureInfo = FeatureInfo.load(directory);
    corpusRepresentation = new CorpusRepresentationVolatileDense2JsonStream(dataDir, featureInfo);
  }

  
  @Override
  protected void loadModel(URL directory, String parms) {
    loadAndSetCorpusRepresentation(directory);
    // the loadModel method should get called before all the applyModel
    // calls, so here we can start the external process with which we communicate
    // in applyModel
    
    // Start the process
    ArrayList<String> finalCommand = new ArrayList<String>();
    String modelBaseName = new File(dataDir, WRAPPER_NAME+".model").getAbsolutePath();
    finalCommand.add(getCommandPathApply());
    finalCommand.add(modelBaseName);
    finalCommand.add(corpusRepresentation.getMetaFile().getAbsolutePath());
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
    System.err.println("Running: ");
    for(int i=0; i<finalCommand.size();i++) {
      System.err.println(i+": >"+finalCommand.get(i)+"<");
    }
    Map<String,String> env = new HashMap<String,String>();
    env.put("WRAPPER_HOME",getWrapperHome());
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
  public void trainModel(File dataDirectoryZZZ, String instanceType, String parms) {    
    // first of all close the corpus and save the metadata
    corpusRepresentation.finishAdding();
    
    // update the info instance with stuff we should know now
    info.classLabels = corpusRepresentation.getTargetLabels();
    info.nrTargetValues = info.classLabels.size();
    info.nrTrainingDimensions = corpusRepresentation.getNrFeatures();
    
    // first of all copy the wrapper files into the data directory if needed
    Utils4Engines.copyWrapper(WRAPPER_NAME, dataDir);
    
    ArrayList<String> finalCommand = new ArrayList<String>();
    String dataFileName = dataDir.getAbsolutePath()+File.separator;
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
    System.err.println("Running: ");
    for(int i=0; i<finalCommand.size();i++) {
      System.err.println(i+": >"+finalCommand.get(i)+"<");
    }
    // Create a fake Model jsut to make LF_Apply... happy which checks if this is null
    model = new Object();
    Map<String,String> env = new HashMap<String,String>();
    env.put("WRAPPER_HOME",getWrapperHome());
    process = ProcessSimple.create(dataDir,env,finalCommand);
    process.waitFor();
    
    // we also need to save the updated info file
    info.nrTrainingInstances = corpusRepresentation.nrInstances();
    info.engineClass = this.getClass().getName();
    info.save(dataDir);    
    featureInfo.save(dataDir);
  }

  @Override
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    throw new UnsupportedOperationException("Not supported (yet?)"); 
  }

  @Override
  public List<ModelApplication> applyModel(AnnotationSet instancesAS, AnnotationSet inputAS, AnnotationSet sequenceAS, String parms) {
    ObjectMapper mapper = new ObjectMapper();
    List<ModelApplication> modelapps = new ArrayList<ModelApplication>();
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
        process.writeObject(json);
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
        if(status==null) status = "";
        if(!"ok".equals(status.toLowerCase())) {
          throw new GateRuntimeException("Something went wrong applying the model, got status: "+status);
        }
        // outputs and confidence can both be a scalar (for a single feature vector) or a list
        // for a sequence of feature vectors. Also the type depends on the kind of algorithm...
        ModelApplication ma = null;
        if(info.task.equals(AlgorithmKind.REGRESSOR.toString())) {
          Double output = (Double)retMap.get("output");
          if(output==null) throw new GateRuntimeException("Did not get a regression result from model");
          // NOTE: eventually we may get variance or confidence interval boundaries here: "ci_upper"/"ci_lower"/"ci_p"
          // Double variance = (Double)retMap.get("variance");
          ma = new ModelApplication(instanceAnnotation,output); 
        } else if(info.task.equals(AlgorithmKind.CLUSTERING.toString())) {
          throw new GateRuntimeException("Not implemented yet: task CLUSTERING");
        } else if(info.task.equals(AlgorithmKind.CLASSIFIER.toString())) {
          String output = (String)retMap.get("output");
          if(output==null) throw new GateRuntimeException("Did not get a classification result from model");
          // note: the confidence actually may be null (missing in the map) meaning we do not have it
          Double confidence = (Double)retMap.get("confidence");
          ma = new ModelApplication(instanceAnnotation,output, confidence, null, null);
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
        if(status==null) status = "";
        if(!"ok".equals(status.toLowerCase())) {
          throw new GateRuntimeException("Something went wrong applying the model, got status: "+status);
        }
        
        // we expect output to be a list of string and if confidence exists, a list of double
        List<String> output = (List<String>)retMap.get("output");
        if(output==null) throw new GateRuntimeException("Did not get a classification result from model");
        // note: the confidence actually may be null (missing in the map) meaning we do not have it
        List<Double>confidence = (List<Double>)retMap.get("confidence");
        
        
        ModelApplication ma = null;
        if(info.task.equals(AlgorithmKind.SEQUENCE_TAGGER.toString())) {
          // we need to get back as many labels as there are instances in the insts4seq list
          int i = 0;
          for(Annotation ann : instanceAnnotations) {
            // expects class, confidence, sequence span id
            Double conf = null;
            if(confidence!=null) conf=confidence.get(i);
            ma = new ModelApplication(ann, output.get(i), conf, seq_id);
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