/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.engines;

import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Objects;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

/**
 * A class that represents the information stored in the info file.
 * This class also has static methods for storing and loading itself.
 * @author Johann Petrak
 */
public class Info {
  public static final String FILENAME_INFO = "info.yaml";
  public String engineClass;  // this also can tell us if classifier or sequence tagging algorihtm
  public String algorithmClass;  // the class of the enum 
  public String algorithmName;   // the actual value of enum
  public String trainerClass;
  public String modelClass;
  public String task;  // classification, regression or sequence tagging?  
  public int nrTrainingInstances;
  public int nrTrainingDocuments;
  public int nrTrainingDimensions;
  public int nrTargetValues;  // -1 for regression
  public List<String> classLabels; // empty for regression
  public String trainingCorpusName;
  public String targetFeature;
  public String classAnnotationType;  // classAnnotationType 
  
  /**
   * TODO: NOTE: this is incomplete!! Should contain all fields that are also in the hashCode method!
   * For now we have only included the fields we need for the unit test.
   * @param other
   * @return 
   */
  @Override 
  public boolean equals(Object other) {
    if(other == null) return false;
    if (other instanceof Info) {
      if(engineClass!=null && !engineClass.equals(((Info) other).engineClass)) return false;
      if(trainerClass!=null && !trainerClass.equals(((Info) other).trainerClass)) return false;
    }
    return true;
  }  
  
  @Override
  public int hashCode() {
    int hash = 7;
    hash = 89 * hash + Objects.hashCode(this.engineClass);
    hash = 89 * hash + Objects.hashCode(this.trainerClass);
    hash = 89 * hash + Objects.hashCode(this.task);
    hash = 89 * hash + this.nrTrainingInstances;
    hash = 89 * hash + this.nrTrainingDocuments;
    hash = 89 * hash + this.nrTrainingDimensions;
    hash = 89 * hash + this.nrTargetValues;
    hash = 89 * hash + Objects.hashCode(this.classLabels);
    hash = 89 * hash + Objects.hashCode(this.trainingCorpusName);
    return hash;
  }
  
  public void save(File directory) {
    String dump = new Yaml().dumpAs(this,Tag.MAP,DumperOptions.FlowStyle.BLOCK);
    File infoFile = new File(directory,FILENAME_INFO);
    //System.err.println("Saving engine to "+infoFile);
    OutputStreamWriter out = null;
    try {
      out = new OutputStreamWriter(new FileOutputStream(infoFile),"UTF-8");
      out.append(dump);
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not write info file "+infoFile,ex);
    } finally {
      try {
        out.close();
      } catch (IOException ex) {
        //
      }
    }
  }
  public static Info load(File directory) {
    Yaml yaml = new Yaml();
    Object obj;
    File infoFile = new File(directory,FILENAME_INFO);
    try {
      obj = yaml.loadAs(new InputStreamReader(new FileInputStream(infoFile),"UTF-8"),Info.class);
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not load info file "+infoFile,ex);
    }    
    Info info = (Info)obj;    
    return info;
  }

  @Override
  public String toString() {
    return "Info{" + "engineClass=" + engineClass + ", algorithmClass=" + trainerClass + ", task=" + task + ", nrTrainingInstances=" + nrTrainingInstances + ", nrTrainingDocuments=" + nrTrainingDocuments + ", nrTrainingDimensions=" + nrTrainingDimensions + ", nrTargetValues=" + nrTargetValues + ", classLabels=" + classLabels + ", trainingCorpusName=" + trainingCorpusName + '}';
  }
  
}
