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

import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.CustomClassLoaderConstructor;
import org.yaml.snakeyaml.nodes.Tag;
import static gate.plugin.learningframework.LFUtils.newURL;

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
  public String classAnnotationType;  // classAnnotationType for classification
  public List<String> classAnnotationTypes; // for sequence tagging
  public String seqEncoderClass;
  public String seqEncoderOptions;
  
  /**
   * TODO: NOTE: this is incomplete!! Should contain all fields that are also in the hashCode method!
   * For now we have only included the fields we need for the unit test.
   * @param other TODO
   * @return TODO
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
  
  /**
   * TODO
   * @param directory TODO
   */
  public void save(File directory) {
    CustomClassLoaderConstructor constr = 
            new CustomClassLoaderConstructor(this.getClass().getClassLoader());
    String dump = 
            new Yaml(constr)
                    .dumpAs(this,Tag.MAP,DumperOptions.FlowStyle.BLOCK);
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

  /**
   * TODO
   * @param directory TODO
   * @return TODO
   */
  public static Info load(URL directory) {
    CustomClassLoaderConstructor constr = 
            new CustomClassLoaderConstructor(Info.class.getClassLoader());
    Yaml yaml = new Yaml(constr);
    Object obj;
    URL infoFile = newURL(directory,FILENAME_INFO);
    try (InputStream is = infoFile.openStream()) {
      obj = yaml.loadAs(new InputStreamReader(is,"UTF-8"),Info.class);
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
