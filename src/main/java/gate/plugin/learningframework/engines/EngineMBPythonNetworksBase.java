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
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import gate.Annotation;
import gate.AnnotationSet;
import gate.lib.interaction.process.Process4JsonStream;
import gate.lib.interaction.process.ProcessBase;
import gate.lib.interaction.process.ProcessSimple;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.Exporter;
import gate.plugin.learningframework.ModelApplication;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.export.CorpusExporter;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.plugin.learningframework.mallet.NominalTargetWithCosts;
import gate.util.Files;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * Base class for engines that represent Python Neural Network learning algorithms.
 * 
 * 
 * @author Johann Petrak
 */
public abstract class EngineMBPythonNetworksBase extends EngineMB {

  // constants for the wrapper
  protected String WRAPPER_NAME;
  protected String ENV_WRAPPER_HOME;
  protected String PROP_WRAPPER_HOME;
  protected String YAML_FILE;
  protected String YAML_SETTING_WRAPPER_HOME;
  protected String SCRIPT_APPLY_BASENAME;
  protected String SCRIPT_TRAIN_BASENAME;
  protected String SCRIPT_EVAL_BASENAME;
  protected String MODEL_BASENAME;
  protected Object MODEL_INSTANCE;
  
  protected ProcessBase process;
  
  // These variables get set from the wrapper-specific config file, java properties or
  // environment variables.
  protected String shellcmd = null;
  protected String shellparms = null;
  protected String wrapperhome = null;
  protected CorpusExporter corpusExporter = null;
  
  @Override
  protected void initWhenCreating(URL directory, Algorithm algorithm, String parameters, FeatureInfo fi, TargetType tt) {
    //Previously, this would create the proper corpus representation in the MB base class,
    //now we instead create the corpus exporter we use later and get the CR from it
    //super.initWhenCreating(directory, algorithm, parameters, fi, tt);
    corpusExporter = CorpusExporter.create(Exporter.EXPORTER_CSV_CLASS, "-t -n "+parameters, featureInfo, parameters, directory);
    corpusRepresentation = (CorpusRepresentationMallet)corpusExporter.getCorpusRepresentation();
  } 
  
  /**
   * Try to find the script running the Wrapper command.
   * If apply is true, the executable for application is searched,
   * otherwise the one for training.
   * This checks the following settings (increasing priority): 
   * environment variable SKLEARN_WRAPPER_HOME,
   * java property gate.plugin.learningframework.[wrappername].home and
   * the setting "[wrappername].home" in file "[wrappername].yaml" in the data directory,
   * if it exists. 
   * The setting for the [wrappername] wrapper home can be relative in which case it
   * will be resolved relative to the dataDirectory
   * @param dataDirectory
   * @param apply
   * @return 
   */
  protected File findWrapperCommand(File dataDirectory, boolean apply) {
    String homeDir = System.getenv(ENV_WRAPPER_HOME);
    String tmp = System.getProperty(PROP_WRAPPER_HOME);
    if(tmp!=null) homeDir = tmp;
    File wrapperInfoFile = new File(dataDirectory,YAML_FILE);
    if(wrapperInfoFile.exists()) {
      Yaml yaml = new Yaml();
      Object obj;
      try {
        obj = yaml.load(new InputStreamReader(new FileInputStream(wrapperInfoFile),"UTF-8"));
      } catch (Exception ex) {
        throw new GateRuntimeException("Could not load yaml file "+wrapperInfoFile,ex);
      }    
      tmp = null;
      Map map = null;
      if(obj instanceof Map) {
        map = (Map)obj;
        tmp = (String)map.get(YAML_SETTING_WRAPPER_HOME);      
      } else {
        throw new GateRuntimeException("Info file has strange format: "+wrapperInfoFile.getAbsolutePath());
      }
      if(tmp != null) {
        homeDir = tmp;
      }      
      // Also get any other settings that may be present:
      // shell command
      shellcmd = (String)map.get("shellcmd");
      shellparms = (String)map.get("shellparms");
    }
    if(homeDir == null) {
      throw new GateRuntimeException(WRAPPER_NAME+" home not set, please see https://github.com/GateNLP/gateplugin-LearningFramework/wiki/UsingSklearn");
    }
    File wrapperHome = new File(homeDir);
    if(!wrapperHome.isAbsolute()) {
      wrapperHome = new File(dataDirectory,homeDir);
    }
    if(!wrapperHome.isDirectory()) {
      throw new GateRuntimeException(WRAPPER_NAME+" home is not a directory: "+wrapperHome.getAbsolutePath());
    }
    wrapperhome = wrapperHome.getAbsolutePath();
    // Now, depending on the operating system, and on train/apply,
    // find the correct script to execute
    File commandFile;
    // we use the simple heuristic that if the file separator is "/" 
    // we assume we can use the bash script, if it is "\" we use the windows
    // script and otherwise we give up
    boolean linuxLike = System.getProperty("file.separator").equals("/");
    boolean windowsLike = System.getProperty("file.separator").equals("\\");
    if(linuxLike) {
      if(apply) 
        commandFile = new File(new File(wrapperHome,"bin"),SCRIPT_APPLY_BASENAME+".sh");
      else
        commandFile = new File(new File(wrapperHome,"bin"),SCRIPT_TRAIN_BASENAME+".sh");
    } else if(windowsLike) {
      if(apply) 
        commandFile = new File(new File(wrapperHome,"bin"),SCRIPT_APPLY_BASENAME+".cmd");
      else
        commandFile = new File(new File(wrapperHome,"bin"),SCRIPT_TRAIN_BASENAME+".cmd");      
    } else {
      throw new GateRuntimeException("It appears this OS is not supported");
    }
    commandFile = commandFile.isAbsolute() ? 
            commandFile :
            new File(dataDirectory,commandFile.getPath());
    if(!commandFile.canExecute()) {
      // TODO: placeholder wiki page, this should construct the correct wiki page name
      // depending on the actual wrapper used
      throw new GateRuntimeException("Not an executable file or not found: "+commandFile+" please see https://github.com/GateNLP/gateplugin-LearningFramework/wiki/UsingPythonWrappers");
    }
    return commandFile;
  }
  
  
  @Override
  protected void loadModel(URL directoryURL, String parms) {
    File directory = null;
    if("file".equals(directoryURL.getProtocol())) directory = Files.fileFromURL(directoryURL);
    else throw new GateRuntimeException("The dataDirectory for WekaWrapper must be a file: URL not "+directoryURL);
    ArrayList<String> finalCommand = new ArrayList<String>();
    // we need the corpus representation here! Normally this is done from loadEngine and after
    // load model, but we do it here. The load crm method only loads anything if it is still
    // null, so we will do this only once anyway.
    loadAndSetCorpusRepresentation(directoryURL);
    CorpusRepresentationMalletTarget data = (CorpusRepresentationMalletTarget)corpusRepresentation;
    SimpleEntry<String,Integer> modeAndNrC = findOutMode(data);
    String mode = modeAndNrC.getKey();
    Integer nrClasses = modeAndNrC.getValue();
    // Instead of loading a model, this establishes a connection with the 
    // external wrapper process. 
    
    File commandFile = findWrapperCommand(directory, true);
    String modelFileName = new File(directory,MODEL_BASENAME).getAbsolutePath();
    finalCommand.add(commandFile.getAbsolutePath());
    finalCommand.add(modelFileName);
    finalCommand.add(mode);
    finalCommand.add(nrClasses.toString());
    // if we have a shell command prepend that, and if we have shell parms too, include them
    if(shellcmd != null) {
      finalCommand.add(0,shellcmd);
      if(shellparms != null) {
        String[] sps = shellparms.trim().split("\\s+");
        int i=0; for(String sp : sps) { finalCommand.add(++i,sp); }
      }
    }
    //System.err.println("Running: "+finalCommand);
    // Create a fake Model jsut to make LF_Apply... happy which checks if this is null
    model = MODEL_INSTANCE;
    Map<String,String> env = new HashMap<>();
    env.put(ENV_WRAPPER_HOME, wrapperhome);
    process = Process4JsonStream.create(directory,env,finalCommand);
  }

  @Override
  protected void saveModel(File directory) {
    // NOTE: we do not need to save the model here because the external
    // sklearnWrapper command does this.
    // However we still need to make sure a usable info file is saved!
    info.engineClass = this.getClass().getName();
    info.save(directory);
  }

  @Override
  public void trainModel(File dataDirectory, String instanceType, String parms) {
    ArrayList<String> finalCommand = new ArrayList<String>();
    CorpusRepresentationMalletTarget data = (CorpusRepresentationMalletTarget)corpusRepresentation;
    SimpleEntry<String,Integer> modeAndNrC = findOutMode(data);
    String mode = modeAndNrC.getKey();
    Integer nrClasses = modeAndNrC.getValue();
    
    // invoke wrapper for training
    File commandFile = findWrapperCommand(dataDirectory, false);
    // Export the data 
    // Note: any scaling was already done in the PR before calling this method!
    // find out if we train classification or regression
    // TODO: NOTE: not sure if classification/regression matters here as long as
    // the actual exporter class does the right thing based on the corpus representation!
    // TODO: we have to choose the correct target type here!!!
    // NOTE: the last argument here are the parameters for the exporter method.
    // we use the CSV exporter with parameters:
    // -t: twofiles, export indep and dep into separate files
    // -n: noheaders, do not add a header row
    
    // Exporter.export(corpusRepresentation, 
    //        Exporter.EXPORTER_CSV_CLASS, dataDirectory, instanceType, "-t -n");
    corpusExporter.export();
    String dataFileName = dataDirectory.getAbsolutePath()+File.separator;
    String modelFileName = new File(dataDirectory, MODEL_BASENAME).getAbsolutePath();
    finalCommand.add(commandFile.getAbsolutePath());
    finalCommand.add(dataFileName);
    finalCommand.add(modelFileName);
    finalCommand.add(mode);
    finalCommand.add(nrClasses.toString());
    if(!parms.trim().isEmpty()) {
      String[] tmp = parms.split("\\s+",-1);
      finalCommand.addAll(Arrays.asList(tmp));
    }
    // if we have a shell command prepend that, and if we have shell parms too, include them
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
    model = MODEL_INSTANCE;
    Map<String,String> env = new HashMap<String,String>();
    env.put(ENV_WRAPPER_HOME,wrapperhome);
    process = ProcessSimple.create(dataDirectory,env,finalCommand);
    process.waitFor();
  }

  @Override
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<ModelApplication> applyModel(AnnotationSet instanceAS, AnnotationSet inputAS, 
          AnnotationSet sequenceAS, String parms) {
    CorpusRepresentationMalletTarget data = (CorpusRepresentationMalletTarget)corpusRepresentation;
    data.stopGrowth();
    int nrCols = data.getPipe().getDataAlphabet().size();
    //System.err.println("Running EngineSklearn.applyModel on document "+instanceAS.getDocument().getName());
    List<ModelApplication> gcs = new ArrayList<ModelApplication>();
    LFPipe pipe = (LFPipe)data.getRepresentationMallet().getPipe();
    ArrayList<String> classList = null;
    // If we have a classification problem, pre-calculate the class label list
    if(pipe.getTargetAlphabet() != null) {
      classList = new ArrayList<String>();
      for(int i = 0; i<pipe.getTargetAlphabet().size(); i++) {
        String labelstr = pipe.getTargetAlphabet().lookupObject(i).toString();
        classList.add(labelstr);
      }
    }
    // create the datastructure we need for the application script: 
    // a map that contains the following fields:
    // - cmd: either STOP or "AC" for apply classification or "AR" for apply regression
    // - values: the non-zero values, for increasing rows and increasing cols within rows
    // - rowinds: for the k-th value which row number it is in
    // - colinds: for the k-th value which column number (location index) it is in
    // - shaperows: number of rows in total
    // - shapecols: maximum number of cols in a vector
    Map map = new HashMap<String,Object>();
    if(classList==null)
      map.put("cmd", "AR");
    else 
      map.put("cmd","AC");
    ArrayList<double[]> rows = new ArrayList<double[]>();
    int rowIndex = 0;
    List<Annotation> instances = instanceAS.inDocumentOrder();
    for(Annotation instAnn : instances) {
      Instance inst = data.extractIndependentFeatures(instAnn, inputAS);
      
      //FeatureVector fv = (FeatureVector)inst.getData();      
      //System.out.println("Mallet instance, fv: "+fv.toString(true)+", len="+fv.numLocations());
      inst = pipe.instanceFrom(inst);
      
      FeatureVector fv = (FeatureVector)inst.getData();
      //System.out.println("Mallet instance, fv: "+fv.toString(true)+", len="+fv.numLocations());
      
      double[] features = new double[nrCols];
      
      for(int i=0; i<nrCols; i++) {
          features[i] = fv.value(i);      
      }
      rows.add(features);
      rowIndex++;
    }
    // send the matrix data over to the weka process
    // TODO: add a key with the featureWeights to the map!
    map.put("values", rows);
    map.put("n", nrCols);
    process.writeObject(map);
    // get the result back
    Object ret = process.readObject();
    Map<String,Object> response = null;
    if(ret instanceof Map) {
      response = (Map)ret;
    }
    if(response == null) {
      throw new RuntimeException("Got a response from Wrapper process which cannot be used: "+response);
    }
    // the response has the following format:
    // - status: should be "OK" or an error message
    // - targets: a vector of target indices/values
    // - probas: if probabilities are supported, a vector of vectors of class probabilities, otherwise null
    
    String status = (String)response.get("status");
    if(status == null || !status.equals("OK")) {
      throw new RuntimeException("Status of response is not OK but "+status);
    }
    @SuppressWarnings("unchecked")
    ArrayList<Double> targets = (ArrayList<Double>)response.get("targets");
    @SuppressWarnings("unchecked")
    ArrayList<ArrayList<Double>> probas = (ArrayList<ArrayList<Double>>)response.get("probas");
    
    ModelApplication gc = null;
    
    // now check if the mallet representation and the weka process agree 
    // on if we have regression or classification
    if(pipe.getTargetAlphabet() == null) {
      // we expect a regression result, i.e probas should be null
      if(probas != null) {
        throw new RuntimeException("We think we have regression but the Sklearn process sent probabilities");
      }
    }
    // now go through all the instances again and do the target assignment from the vector(s) we got
    int instNr = 0;
    for(Annotation instAnn : instances) {
      if(pipe.getTargetAlphabet() == null) { // we have regression
        gc = new ModelApplication(instAnn, targets.get(instNr));
      } else {
        int bestlabel = targets.get(instNr).intValue();
        String cl
                = pipe.getTargetAlphabet().lookupObject(bestlabel).toString();
        double bestprob = Double.NaN;
        if(probas != null) {
          bestprob = Collections.max(probas.get(instNr));
        }
        gc = new ModelApplication(
                instAnn, cl, bestprob, classList, probas.get(instNr));
      }
      gcs.add(gc);
      instNr++;
    }
    data.startGrowth();
    return gcs;
  }

  @Override
  public void initializeAlgorithm(Algorithm algorithm, String parms) {
    // do not do anything
  }

 
  protected AbstractMap.SimpleEntry<String,Integer> findOutMode(CorpusRepresentationMalletTarget crm)  {
    InstanceList instances = crm.getRepresentationMallet();
    // we pass on a "mode" for the learning problem, which is one of the following:
    // - classind: predict the index of a class
    // - classcosts: targets are vectors of class costs
    // - regr: regression
    // we also pass on another parameter which provides details of the learning problem:
    // - the number of class indices in case of classind and classcosts
    // - 0 as a dummy value in case of "regr"
    
    int nrClasses = 0;
    String mode = "regr";
    Alphabet ta = crm.getPipe().getTargetAlphabet();
    
    if(ta != null) {
      // if this is invoked for training, we should have a first instance, but for 
      // application, we do not have any instances yet. If we do not have any instances, we 
      // just use dummy values for now since at the moment we do not need this information
      // at application time. Should we ever need it we need to store this in the pipe!
      if(instances==null || instances.isEmpty()) {
        mode="classind";
        nrClasses=-1;
      } else {
        Instance firstInstance = instances.get(0);
        Object targetObj = firstInstance.getTarget();
        if(targetObj instanceof NominalTargetWithCosts) {
          NominalTargetWithCosts target = (NominalTargetWithCosts)targetObj;
          nrClasses = target.getCosts().length;
          mode = "classcosts";
        } else {
          mode = "classind";
          nrClasses = ta.size();
        }
      }
    } 
    AbstractMap.SimpleEntry<String,Integer> ret = new AbstractMap.SimpleEntry<String, Integer>(mode,nrClasses);
    return ret;
  }
  
}
