package gate.plugin.learningframework.engines;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import gate.Annotation;
import gate.AnnotationSet;
import gate.lib.interaction.data.SparseDoubleVector;
import gate.lib.interaction.process.Process4ObjectStream;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.GateClassification;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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

  Process4ObjectStream process;
  
  @Override
  protected void loadModel(File directory, String parms) {
    // Instead of loading a model, this establishes a connection with the 
    // external weka process. For this, we expect an additional file in the 
    // directory, weka.yaml, which describes how to run the weka wrapper
    File wekaInfoFile = new File(directory,"weka.yaml");
    if(!wekaInfoFile.exists()) {
      throw new GateRuntimeException("No weka.yaml file found in the data directory!");
    }
    Yaml yaml = new Yaml();
    Object obj;
    try {
      obj = yaml.load(new InputStreamReader(new FileInputStream(wekaInfoFile),"UTF-8"));
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not load file "+wekaInfoFile,ex);
    }    
    String wrapperPath = null;
    if(obj instanceof Map) {
      Map map = (Map)obj;
      wrapperPath = (String)map.get("path");      
    } else {
      throw new GateRuntimeException("Info file is not a map");
    }
    if(wrapperPath == null) {
      throw new GateRuntimeException("No entry 'path' in the weka.yaml file to specify the wrapper path");
    }
    File commandFile = new File(wrapperPath);
    String command = commandFile.isAbsolute() ? 
            wrapperPath :
            new File(directory,wrapperPath).getAbsolutePath();
    if(!new File(command).canExecute()) {
      throw new GateRuntimeException("Not an executable file or not found: "+command);
    }
    String modelFileName = new File(directory,FILENAME_MODEL).getAbsolutePath();
    if(!new File(modelFileName).exists()) {
      throw new GateRuntimeException("File not found: "+modelFileName);
    }
    String header = new File(directory,"header.arff").getAbsolutePath();
    if(!new File(header).exists()) {
      throw new GateRuntimeException("File not found: "+header);
    }
    String finalCommand = command+" "+modelFileName+" "+header;
    System.err.println("Running: "+finalCommand);
    // Create a fake Model jsut to make LF_Apply... happy which checks if this is null
    model = "ExternalWekaWrapperModel";
    process = new Process4ObjectStream(directory,finalCommand);
  }

  @Override
  protected void saveModel(File directory) {
    // NOTE: not needed, since we train using the external command, the model gets saved using the wrapper
  }

  @Override
  public void trainModel(String parms) {
    // TODO: invoke the weka wrapper
    // NOTE: for this the first word in parms must be the full weka class name, the rest are parms
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<GateClassification> classify(AnnotationSet instanceAS, AnnotationSet inputAS, AnnotationSet sequenceAS, String parms) {
    CorpusRepresentationMalletTarget data = (CorpusRepresentationMalletTarget)corpusRepresentationMallet;
    data.stopGrowth();
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
        locations[i] = fv.location(i);
        values[i] = fv.value(i);
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
