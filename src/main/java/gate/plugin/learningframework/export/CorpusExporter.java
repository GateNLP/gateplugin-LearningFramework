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
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.Info;
import gate.plugin.learningframework.features.TargetType;
import java.io.File;

/**
 *
 * @author johann
 */
public abstract class CorpusExporter {
  
  /**
   * Return a new Info object suitable for the data exported.
   */
  public abstract Info getInfo();
    
  /**
   * Export the data to the given directory.
   * Depending on the format this will create one or more files in the 
   * given directory. The file name and extension is chosen automatically
   * and any existing file will get overwritten.
   * @param directory
   * @param cr 
   */
  public abstract void export(File directory, CorpusRepresentation cr, String instanceType, String parms);
  
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
  
  protected TargetType targetType = TargetType.NONE;
  
}
