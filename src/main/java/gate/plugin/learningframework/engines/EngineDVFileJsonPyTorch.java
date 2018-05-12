/*
 *  Copyright (c) The University of Sheffield.
 *
 *  This file is free software, licensed under the 
 *  GNU Library General Public License, Version 2.1, June 1991.
 *  See the file LICENSE.txt that comes with this software.
 *
 */
package gate.plugin.learningframework.engines;

/**
 * Common base class for all Engines which are dense, volatile and write JSON to a file.
 * 
 * 
 * @author Johann Petrak
 */
public class EngineDVFileJsonPyTorch extends EngineDVFileJson {
  
  public EngineDVFileJsonPyTorch() {
    WRAPPER_NAME = "FileJsonPyTorch";
  }
}