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

package gate.plugin.learningframework.features;

import static gate.plugin.learningframework.LFUtils.newURL;
import gate.plugin.learningframework.ScalingMethod;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * This contains the information from the parsed FeatureSpecification plus additional information
 * accumulated during the extraction of a corpus. The additional information is stuff like mappings
 * between the attribute names from the feature specification and the actual names used in the
 * Mallet feature vector, or the mapping between nominal values coded as numeric and the number used
 * to represent the value. Like alphabets, the information in this object can be locked using the
 * stopGrowth() method.
 *
 * @author Johann Petrak
 */
public class FeatureInfo implements Serializable {

  private static final long serialVersionUID = 1;
  protected boolean growthStopped = false;

  private static final String FILENAME_FEATUREINFO = "featureinfo.ser";
  
  /**
   * Create an instance with an empty list of featureSpecs.
   */
  public FeatureInfo() {
    featureSpecs = new ArrayList<FeatureSpecAttribute>();
  }

  /**
   * Create an FeatureInfo instance that is a deep copy of another one.
   * @param other TODO
   */
  public FeatureInfo(FeatureInfo other) {
    this.growthStopped = other.growthStopped;
    featureSpecs = new ArrayList<FeatureSpecAttribute>();
    for(FeatureSpecAttribute attr : other.getAttributes()) {
      featureSpecs.add(attr.clone());
    }
  }
  
  /**
   * Add a new FeatureSpecAttribut and assign its id.
   * @param attr TODO
   */
  public void add(FeatureSpecAttribute attr) {    
    featureSpecs.add(attr);
    attr.featureId = featureSpecs.size()-1;
  }
  
  /**
   * TODO
   */
  public void stopGrowth() {
    // make sure that all alphabets we have stored with some of the featureSpecs are
    // locked too!
    for(FeatureSpecAttribute attr : featureSpecs) {
      attr.stopGrowth();
    }
    growthStopped = true;
  }

  /**
   * TODO
   */
  public void startGrowth() {
    growthStopped = false;
  }

  /**
   * TODO
   * @return TODO
   */
  public boolean growthStopped() {
    return growthStopped;
  }

  protected List<FeatureSpecAttribute> featureSpecs;


  protected ScalingMethod globalScalingMethod = ScalingMethod.NONE;
  
  /**
   * TODO
   * @param sm TODO
   */
  public void setGlobalScalingMethod(ScalingMethod sm) {
    globalScalingMethod = sm;
  }
  
  /**
   * TODO
   * @return TODO
   */
  public ScalingMethod getGlobalScalingMethod() {
    return globalScalingMethod;
  }
  
  /**
   * TODO
   * @return TODO
   */
  public List<FeatureSpecAttribute> getAttributes() { return featureSpecs; }
  
  /**
   * TODO
   * @param dirFile TODO
   */
  public void save(File dirFile) {
    try (OutputStream os = new FileOutputStream(new File(dirFile,FILENAME_FEATUREINFO));
            ObjectOutputStream oos = new ObjectOutputStream(os)
            ) {
      oos.writeObject(this);
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not write feature info file ",ex);
    }
  }
  
  /**
   * TODO
   * @param dirURL TODO
   * @return TODO
   */
  public static FeatureInfo load(URL dirURL) {    
    URL infoFile = newURL(dirURL,FILENAME_FEATUREINFO);
    try (InputStream is = infoFile.openStream();
            ObjectInputStream ois = new ObjectInputStream(is);
            ) {
      FeatureInfo fi = (FeatureInfo)ois.readObject();
      return fi;
    } catch (Exception ex) {
      System.err.println("WARNING: could not read feature info: "+ex.getMessage());
      // we silently ignore this for now since not all engines even create this file (YET!)
      // throw new GateRuntimeException("Could not load feature info file "+infoFile,ex);
      return null;
    }    
  }
  
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("FeatureInfo{growthStopped=");
    sb.append(growthStopped);
    sb.append(",attrs=");
    sb.append(featureSpecs);
    sb.append("}");
    return sb.toString();
  }






}
