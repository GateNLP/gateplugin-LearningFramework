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

import gate.plugin.learningframework.data.CorpusRepresentationVolatileDense2JsonStream;
import gate.plugin.learningframework.engines.Info;

/**
 * Common base class of all dense vector / json file representation based exporters
 * @author Johann Petrak
 */
public class CorpusExporterDRJson extends CorpusExporter {
  
  @Override
  public void initWhenCreating() {
    corpusRepresentation = (CorpusRepresentationVolatileDense2JsonStream)new CorpusRepresentationVolatileDense2JsonStream(dataDirFile, featureInfo);
    corpusRepresentation.startAdding();
  }
  

  @Override
  public void export() {
    // all the data already has been written through the corpusRepresentation.add 
    // method.
    // Here we only need to finish the writing to that file and also write 
    // the metadata.
    corpusRepresentation.finishAdding();
    CorpusRepresentationVolatileDense2JsonStream crdr = 
            (CorpusRepresentationVolatileDense2JsonStream)corpusRepresentation;
    crdr.saveMetadata();
  } // export


  @Override
  public Info getInfo() {
    throw new UnsupportedOperationException("getInfo() not implemented but should not be necessary???"); 
  }
  
  
}
