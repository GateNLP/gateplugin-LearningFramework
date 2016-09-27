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

import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import static gate.plugin.learningframework.engines.Engine.FILENAME_MODEL;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import org.apache.log4j.Logger;

/**
 *
 * @author Johann Petrak
 */
public abstract class EngineMallet extends Engine {
  
  private static Logger logger = Logger.getLogger(EngineMallet.class);
  
  public CorpusRepresentationMallet getCorpusRepresentationMallet() {
    return corpusRepresentationMallet;
  }


  @Override
  protected void saveModel(File directory) {
    if(model==null) {
      // TODO: this should eventually throw an exception, we leave it for testing now.
      System.err.println("WARNING: saving a null model!!!");
    }
    ObjectOutputStream oos = null;
    try {
      oos = new ObjectOutputStream(new FileOutputStream(new File(directory, FILENAME_MODEL)));
      oos.writeObject(model);
    } catch (Exception e) {
      throw new GateRuntimeException("Could not store Mallet model", e);
    } finally {
      if (oos != null) {
        try {
          oos.close();
        } catch (IOException ex) {
          logger.error("Could not close object output stream", ex);
        }
      }
    }
  }


  
}
