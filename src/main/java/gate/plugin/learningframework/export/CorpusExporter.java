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

package gate.plugin.learningframework.export;

import gate.plugin.learningframework.data.CorpusRepresentation;
import gate.plugin.learningframework.engines.Info;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.util.Files;
import gate.util.GateRuntimeException;
import java.io.File;
import java.lang.reflect.Constructor;
import java.net.URL;

/**
 *
 * @author johann
 */
public abstract class CorpusExporter {
  
  protected Exporter exporter;
  protected String parms;
  protected FeatureInfo featureInfo;
  protected TargetType targetType;
  protected URL datadir;
  protected String instanceType;
  protected File dataDirFile;
  
  /**
   * Create a corpus exporter ce for the given Exporter.
   * This method will create the proper corpus representation for the 
   * algorithm and the feature info. 
   * @return 
   */
  public static CorpusExporter create(Exporter exporter, String parms, FeatureInfo featureInfo, String instanceType, URL datadir) {
    CorpusExporter ce = null;
    try {
      @SuppressWarnings("unchecked")
      Constructor constr = exporter.getCorpusExporterClass().getDeclaredConstructor();
      ce = (CorpusExporter)constr.newInstance();
    } catch (Exception ex) {
      throw new GateRuntimeException("Error creating CorpusExporter instance for "+exporter.getCorpusExporterClass(),ex);
    }
    ce.datadir = datadir;
    ce.exporter = exporter;
    ce.targetType = exporter.getTargetType();
    ce.featureInfo = featureInfo;
    ce.parms = parms;
    ce.instanceType = instanceType;
    ce.dataDirFile = Files.fileFromURL(datadir);
    ce.initWhenCreating();    
    return ce;
  }
  
  /**
   * The specific things to do to properly initialize the CorpusExporter ce
 that got created by the static create method.
   */
  public abstract void initWhenCreating();
  
  /**
   * Return a new Info object suitable for the data exported.
   */
  public abstract Info getInfo();
    
  /**
   * Export the data to the given directory or finish writing the data
   * if the corpus representation used by this exporter does on-the-fly writing
   * to a file anyway.
   * Depending on the format this will create one or more files in the 
   * given directory. The file name and extension is chosen automatically
   * and any existing file will get overwritten.
   * @param directory
   * @param cr 
   */
  public abstract void export();
  
  /**
   * Set the target type to use with this exporter.
   * If this is not set, then the exporter will try to determine the 
   * target type from the corpus representation. If it is 
   * set and the corpus representation uses a different target type,
   * an error is thrown when the data is exported.
   * @param tt 
   */
  public void setTargetType(TargetType tt) {
    targetType = tt;
  }
  
  protected CorpusRepresentation corpusRepresentation = null;
  
  /**
   * Return a corpus representation ce to use with this exporter. 
   * @return 
   */
  public CorpusRepresentation getCorpusRepresentation() {
    return corpusRepresentation; 
  }
  
}
