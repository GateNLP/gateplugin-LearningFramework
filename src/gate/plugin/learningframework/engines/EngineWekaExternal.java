package gate.plugin.learningframework.engines;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import gate.Annotation;
import gate.AnnotationSet;
import gate.lib.interaction.data.SparseDoubleVector;
import gate.lib.interaction.process.Process4ObjectStream;
import gate.lib.interaction.process.ProcessBase;
import gate.lib.interaction.process.ProcessSimple;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.Exporter;
import gate.plugin.learningframework.GateClassification;
import gate.plugin.learningframework.Globals;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;

/**
 * An engine that represents Weka through en external process.
 * This can only be used for application of the model at the moment.
 * 
 * For now, this engine gets only used for application, so it gets only
 * created by Engine.loadEngine. The only methods called in this context
 * are loadModel() and loadMalletCorpusRepresentation() and if the user
 * also specified an algorithm class in the info file, initializeAlgorithm, 
 * which at the moment does nothing.
 * 
 * This requires that the user also places a second yaml file into the 
 * directory: weka.yaml. This file needs to contain the following fields:
 * <ul>
 * <li>path: the path to a script or program that starts the weka wrapper 
 * (see the weka-wrapper project on github). If the path starts with a slash
 * it is an absolute path, otherwise the path is resolved relative to the 
 * directory. 
 * </ul>
 * 
 * The directory also needs to contain files lf.model, pipe.pipe, header.arff
 * 
 * The weka-wrapper command will then get invoked with the first parameter
 * being the path to the model and the second parameter being the path to 
 * the head file.
 *
 * 
 * @author Johann Petrak
 */
public class EngineWekaExternal extends Engine {

  ProcessBase process;
  
  /**
   * Try to find the script running the Weka-Wrapper command.
   * If apply is true, the executable for application is searched,
   * otherwise the one for training.
   * This checks the following settings (increasing priority): 
   * environment variable WEKA_WRAPPER_HOME,
   * java property gate.plugin.learningframework.wekawrapper.home and
   * the setting "wekawrapper.home" in file "weka.yaml" in the data directory,
   * if it exists. 
   * The setting for the weka wrapper home can be relative in which case it
   * will be resolved relative to the dataDirectory
   * @param dataDirectory
   * @return 
   */
  private File findWrapperCommand(File dataDirectory, boolean apply) {
    String homeDir = System.getenv("WEKA_WRAPPER_HOME");
    String tmp = System.getProperty("gate.plugin.learningframework.wekawrapper.home");
    if(tmp!=null) homeDir = tmp;
    File wekaInfoFile = new File(dataDirectory,"weka.yaml");
    if(wekaInfoFile.exists()) {
      Yaml yaml = new Yaml();
      Object obj;
      try {
        obj = yaml.load(new InputStreamReader(new FileInputStream(wekaInfoFile),"UTF-8"));
      } catch (Exception ex) {
        throw new GateRuntimeException("Could not load yaml file "+wekaInfoFile,ex);
      }    
      tmp = null;
      if(obj instanceof Map) {
        Map map = (Map)obj;
        tmp = (String)map.get("wekawrapper.home");      
      } else {
        throw new GateRuntimeException("Info file has strange format: "+wekaInfoFile.getAbsolutePath());
      }
      if(tmp == null) {
        System.err.println("weka.yaml file present but does not contain wekawrapper.home setting");
      } else {
        homeDir = tmp;
      }      
    }
    if(homeDir == null) {
      throw new GateRuntimeException("WekaWrapper home not set, please see https://github.com/GateNLP/gateplugin-LearningFramework/wiki/UsingWeka");
    }
    File wrapperHome = new File(homeDir);
    if(!wrapperHome.isAbsolute()) {
      wrapperHome = new File(dataDirectory,homeDir);
    }
    if(!wrapperHome.isDirectory()) {
      throw new GateRuntimeException("WekaWrapper home is not a directory: "+wrapperHome.getAbsolutePath());
    }
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
        commandFile = new File(new File(wrapperHome,"bin"),"wekaWrapperApply.sh");
      else
        commandFile = new File(new File(wrapperHome,"bin"),"wekaWrapperTrain.sh");
    } else if(windowsLike) {
      if(apply) 
        commandFile = new File(new File(wrapperHome,"bin"),"wekaWrapperApply.cmd");
      else
        commandFile = new File(new File(wrapperHome,"bin"),"wekaWrapperTrain.cmd");      
    } else {
      throw new GateRuntimeException("It appears this OS is not supported");
    }
    commandFile = commandFile.isAbsolute() ? 
            commandFile :
            new File(dataDirectory,commandFile.getPath());
    if(!commandFile.canExecute()) {
      throw new GateRuntimeException("Not an executable file or not found: "+commandFile+" please see https://github.com/GateNLP/gateplugin-LearningFramework/wiki/UsingWeka");
    }
    return commandFile;
  }
  
  
  @Override
  protected void loadModel(File directory, String parms) {
    // Instead of loading a model, this establishes a connection with the 
    // external weka process. For this, we expect an additional file in the 
    // directory, weka.yaml, which describes how to run the weka wrapper
    File commandFile = findWrapperCommand(directory, true);
    String modelFileName = new File(directory,FILENAME_MODEL).getAbsolutePath();
    if(!new File(modelFileName).exists()) {
      throw new GateRuntimeException("File not found: "+modelFileName);
    }
    String header = new File(directory,"header.arff").getAbsolutePath();
    if(!new File(header).exists()) {
      throw new GateRuntimeException("File not found: "+header);
    }
    String finalCommand = commandFile.getAbsolutePath()+" "+modelFileName+" "+header;
    System.err.println("Running: "+finalCommand);
    // Create a fake Model jsut to make LF_Apply... happy which checks if this is null
    model = "ExternalWekaWrapperModel";
    process = new Process4ObjectStream(directory,finalCommand);
  }

  @Override
  protected void saveModel(File directory) {
    // NOTE: we do not need to save the model here because the external
    // WekaWrapper command does this.
    // However we still need to make sure a usable info file is saved!
    info.engineClass = EngineWekaExternal.class.getName();
    info.save(directory);
  }

  @Override
  public void trainModel(File dataDirectory, String instanceType, String parms) {
    // TODO: invoke the weka wrapper
    // NOTE: for this the first word in parms must be the full weka class name, the rest are parms
    if(parms == null || parms.isEmpty()) {
      throw new GateRuntimeException("Cannot train using WekaWrapper, algorithmParameter must contain Weka algorithm class as first word");
    }
    String wekaClass = null;
    String wekaParms = "";
    int spaceIdx = parms.indexOf(" ");
    if(spaceIdx<0) {
      wekaClass = parms;
    } else {
      wekaClass = parms.substring(0,spaceIdx);
      wekaParms = parms.substring(spaceIdx).trim();
    }
    File commandFile = findWrapperCommand(dataDirectory, false);
    // Export the data 
    // Note: any scaling was already done in the PR before calling this method!
    // find out if we train classification or regression
    // NOTE: not sure if classification/regression matters here as long as
    // the actual exporter class does the right thing based on the corpus representation!
    Exporter.export(getCorpusRepresentationMallet(), 
            Exporter.EXPORTER_ARFF_CLASS, dataDirectory, instanceType, parms);
    String dataFileName = new File(dataDirectory,Globals.dataBasename+".arff").getAbsolutePath();
    String modelFileName = new File(dataDirectory, FILENAME_MODEL).getAbsolutePath();
    String finalCommand = commandFile.getAbsolutePath()+" "+dataFileName+" "+modelFileName+" "+wekaClass+" "+wekaParms;
    System.err.println("Running: "+finalCommand);
    // Create a fake Model jsut to make LF_Apply... happy which checks if this is null
    model = "ExternalWekaWrapperModel";
    
    process = new ProcessSimple(dataDirectory,finalCommand);
    process.waitFor();
  }

  @Override
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<GateClassification> classify(AnnotationSet instanceAS, AnnotationSet inputAS, AnnotationSet sequenceAS, String parms) {
    CorpusRepresentationMalletTarget data = (CorpusRepresentationMalletTarget)corpusRepresentationMallet;
    data.stopGrowth();
    //System.err.println("Running EngineWeka.classify on document "+instanceAS.getDocument().getName());
    List<GateClassification> gcs = new ArrayList<GateClassification>();
    LFPipe pipe = (LFPipe)data.getRepresentationMallet().getPipe();
    for(Annotation instAnn : instanceAS.inDocumentOrder()) {
      Instance inst = data.extractIndependentFeatures(instAnn, inputAS);
      
      //FeatureVector fv = (FeatureVector)inst.getData();      
      //System.out.println("Mallet instance, fv: "+fv.toString(true)+", len="+fv.numLocations());
      inst = pipe.instanceFrom(inst);
      
      FeatureVector fv = (FeatureVector)inst.getData();
      //System.out.println("Mallet instance, fv: "+fv.toString(true)+", len="+fv.numLocations());
      
      // Convert to the sparse vector we use to send to the weka process
      int locs = fv.numLocations();
      SparseDoubleVector sdv = new SparseDoubleVector(locs);
      int[] locations = sdv.getLocations();
      double[] values = sdv.getValues();
      for(int i=0;i<locs;i++) {
        locations[i] = fv.indexAtLocation(i);
        values[i] = fv.value(locations[i]);
      }
      // send the vector over to the weka process
      process.writeObject(sdv);
      // get the result back
      Object obj = process.readObject();
      // check that it is an array of double
      double[] ret = null;
      if(obj instanceof double[]) {
        // if the array has one element, the model treated it as regression, otherwise classification
        ret = (double[])obj;
      } else {
        // this is an error, lets panic for now
        throw new RuntimeException("Got a response from the Weka process which is not double[] but "+obj.getClass());
      }
      //System.err.println("Sent vector: locs/values="+Arrays.toString(locations)+"/"+Arrays.toString(values)+", ret="+Arrays.toString(ret));
      GateClassification gc = null;
      // now check if the mallet representation and the weka process agree 
      // on if we have regression or classification
      if(pipe.getTargetAlphabet() == null) {
        // we expect a regression result, i.e ret should have 1 element
        if(ret.length != 1) {
          throw new RuntimeException("We think we have regression but the Weka process sent a ret of length "+ret.length);
        }
        gc = new GateClassification(instAnn, ret[0]);
      } else {
        // classification, we expect ret to have length >= 2
        if(ret.length < 2) {
          throw new RuntimeException("We think we have classification but Weka process sent a ret of length "+ret.length);
        }
        double bestprob = 0.0;
        int bestlabel = 0;
        /*
        System.err.print("DEBUG: got classes from pipe: ");
    		Object[] cls = pipe.getTargetAlphabet().toArray();
        boolean first = true;
        for(Object cl : cls) {
          if(first) { first = false; } else { System.err.print(", "); }
          System.err.print(">"+cl+"<");
        }
        System.err.println();
         */
        List<String> classList = new ArrayList<String>();
        List<Double> confidenceList = new ArrayList<Double>();
        for (int i = 0; i < ret.length; i++) {
          int thislabel = i;
          double thisprob = ret[i];
          String labelstr = (String) pipe.getTargetAlphabet().lookupObject(thislabel);
          classList.add(labelstr);
          confidenceList.add(thisprob);
          if (thisprob > bestprob) {
            bestlabel = thislabel;
            bestprob = thisprob;
          }
        } // end for i < predictionDistribution.length

        String cl
                = (String) pipe.getTargetAlphabet().lookupObject(bestlabel);

        gc = new GateClassification(
                instAnn, cl, bestprob, classList, confidenceList);
      }
      gcs.add(gc);
    }
    data.startGrowth();
    return gcs;
  }

  @Override
  public void initializeAlgorithm(Algorithm algorithm, String parms) {
    // do not do anything
  }

  @Override
  protected void loadMalletCorpusRepresentation(File directory) {
    corpusRepresentationMallet = CorpusRepresentationMalletTarget.load(directory);
  }
  
}
