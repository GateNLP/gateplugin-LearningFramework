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

package gate.plugin.learningframework.mallet;

import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.SerialPipes;
import gate.plugin.learningframework.features.FeatureInfo;
import java.io.Serializable;
import java.util.Collection;

/**
 * An extended version of the Mallet SerialPipes class which allows us to store
 * some additional important information.
 * This adds methods to store the feature configuration, to associate each entry from the 
 * feature config with one or more features, to associate each feature with its feature config,
 * and to associate features which are nominal and codedas numeric with their value alphabet. 
 * All the additional information is stored in a single container: this container is used when
 * the features get extracted from documents to look up and store the relevant information. 
 * 
 * @author Johann Petrak
 * 
 * TODO: turns out we will probably not need this after all: it is probably easiest to 
 * store the featureinfo object in whatever pipe we store as a property!
 */
public class LFPipe extends SerialPipes implements Serializable {
  private static final long serialVersionUID = 1;
  public LFPipe(Collection<Pipe> pipes) {
    super(pipes);
  }
  protected FeatureInfo featureInfo;

  /**
   * Set the feature info.
   * @param info feature info
   */
  public void setFeatureInfo(FeatureInfo info) { featureInfo = info; }

  /**
   * Get the feature info.
   * @return feature info
   */
  public FeatureInfo getFeatureInfo() { return featureInfo; }
  
  /**
   * Add another pipe at the end of this SerialPipes.
   * @param pipe pipe to add
   */
  public void addPipe(Pipe pipe) {
    super.pipes().add(pipe);
  }

}
