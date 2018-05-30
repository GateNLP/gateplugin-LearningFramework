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

import java.util.HashMap;
import java.util.Map;


/**
 *
 * @author Johann Petrak
 */
public enum SeqEncoderEnum {
  BIO(SeqEncoder_SimpleBIO.class,null), 
  //BIEO(null,null),
  //BISO(null,null),
  ;
  private SeqEncoderEnum() {
    
  }
  private SeqEncoderEnum(Class encoderClass, Map<String,String> encoderOptions) {    
    this.encoderClass = encoderClass;
    this.encoderOptions = new HashMap<>();
    if(encoderOptions != null) this.encoderOptions.putAll(encoderOptions);
  }
  private Class encoderClass;
  private Map<String,String> encoderOptions;
  public Class getEncoderClass() { return encoderClass; }
  public Map<String,String> getOptions() { return encoderOptions; }

}
