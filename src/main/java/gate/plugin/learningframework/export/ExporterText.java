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

import gate.plugin.learningframework.engines.AlgorithmKind;
import gate.plugin.learningframework.features.TargetType;

public enum ExporterText {
  // For now this is done ad-hoc in the exporter PR, but it will get moved to
  // exporter classes
  TEXTLINE_TSV(null,null,null)
  ;
  

  
  private ExporterText(Class<?> corpusExporterClass, TargetType ttype, AlgorithmKind algkind) {
    this.corpusExporterClass = corpusExporterClass;
    this.ttype = ttype;
    this.algkind = algkind;
  }
  private final Class<?> corpusExporterClass;
  private final TargetType ttype;
  private final AlgorithmKind algkind;
  public Class<?> getCorpusExporterClass() { return corpusExporterClass; }
  public TargetType getTargetType() { return ttype; }
  public AlgorithmKind getAlgorithmKind() {return algkind; }
}
