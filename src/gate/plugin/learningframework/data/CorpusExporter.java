/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.data;

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
  public abstract void export(File directory, CorpusRepresentation cr);
  
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
