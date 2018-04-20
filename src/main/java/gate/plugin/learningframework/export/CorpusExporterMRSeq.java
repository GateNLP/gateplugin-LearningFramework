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

import cc.mallet.types.InstanceList;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.data.CorpusRepresentationMalletSeq;
import gate.plugin.learningframework.engines.Info;
import java.io.File;

/**
 *
 * @author johann
 */
public class CorpusExporterMRSeq extends CorpusExporterMR {

  @Override
  public Info getInfo() {
    Info info = new Info();
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmSequenceTagging";
    info.algorithmName = "DUMMY";
    info.engineClass = "DUMMY";
    info.modelClass =  "DUMMY";    
    return info;
  }

  @Override
  public void export() {    
    exportMeta();
    CorpusRepresentationMallet crm = (CorpusRepresentationMallet)corpusRepresentation;
    InstanceList malletInstances = crm.getRepresentationMallet();
    //Pipe pipe = malletInstances.getPipe();
    //Attributes attrs = new Attributes(pipe,instanceType);
    malletInstances.save(new File(dataDirFile, "data.malletseq.ser"));    
  } // export

  @Override
  public void initWhenCreating() {
    corpusRepresentation = (CorpusRepresentationMalletSeq)new CorpusRepresentationMalletSeq(featureInfo, ScalingMethod.NONE);
  }
  
  
}
