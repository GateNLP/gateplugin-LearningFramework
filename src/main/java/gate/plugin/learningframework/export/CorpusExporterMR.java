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

import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.engines.Info;
import gate.plugin.learningframework.mallet.LFPipe;
import java.util.ArrayList;

/**
 * Common base class of all mallet-related exporters.
 * @author johann
 */
public abstract class CorpusExporterMR extends CorpusExporter {
  
  @Override
  public void initWhenCreating() {
    // for all mallet related exporters, we need to create a mallet corpus 
    // representation here, either seq or target, depending on the actual
    // exporter. We provide a default implementation here which creates a 
    // target CR, the seq exporters then override in turn
    // TODO: need to properly support scaling when exporting!
    corpusRepresentation = (CorpusRepresentationMalletTarget)new CorpusRepresentationMalletTarget(featureInfo, ScalingMethod.NONE, targetType);
  }
  
  // All the mallet related exporters also write the pipe and the info, each
  // of the export() implementations should call this method
  public void exportMeta() {
    // get the pre-filled info object
    Info info = getInfo();
    // In addition to the actual data file exported by the methods above,
    // always also export the pipe and a template info file!
    info.classAnnotationType = "null";
    CorpusRepresentationMallet crm = (CorpusRepresentationMallet)corpusRepresentation;
    LFPipe lfpipe = (LFPipe) crm.getPipe();
    if (lfpipe.getTargetAlphabet() == null) {
      info.classLabels = null;
    } else {
      //info.classLabels = lfpipe.getTargetAlphabet().toArray();
      Object[] objs = lfpipe.getTargetAlphabet().toArray();
      info.nrTargetValues = objs.length;
      ArrayList<String> labels = new ArrayList<String>();
      for (Object obj : objs) {
        labels.add(obj.toString());
      }
      info.classLabels = labels;
    }
    info.nrTrainingDimensions = lfpipe.getDataAlphabet().size();
    info.nrTrainingDocuments = 0;
    info.nrTrainingInstances = crm.getRepresentationMallet().size();
    info.targetFeature = "class";
    info.task = "CLASSIFIER";
    info.trainerClass = "";
    info.trainingCorpusName = "";
    info.save(dataDirFile);
    // finally save the Mallet corpus representation
    crm.savePipe(dataDirFile);
    
  }
  
  
}
