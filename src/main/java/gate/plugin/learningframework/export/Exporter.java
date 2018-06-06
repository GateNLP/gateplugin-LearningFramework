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

public enum Exporter {
  JSON_CL_DR(CorpusExporterDRJson.class,TargetType.NOMINAL,AlgorithmKind.CLASSIFIER),
  JSON_RG_DR(CorpusExporterDRJson.class,TargetType.NUMERIC,AlgorithmKind.REGRESSOR),
  JSON_SEQ_DR(CorpusExporterDRJson.class,TargetType.NOMINAL,AlgorithmKind.SEQUENCE_TAGGER),
  ARFF_CL_MR(CorpusExporterMRARFF.class,TargetType.NOMINAL,AlgorithmKind.CLASSIFIER),  
  ARFF_RG_MR(CorpusExporterMRARFF.class,TargetType.NUMERIC,AlgorithmKind.REGRESSOR),  
  CSV_CL_MR(CorpusExporterMRCSV.class,TargetType.NOMINAL,AlgorithmKind.CLASSIFIER),  
  CSV_RG_MR(CorpusExporterMRCSV.class,TargetType.NUMERIC,AlgorithmKind.REGRESSOR),  
  JSON_CL_MR(CorpusExporterMRJsonTarget.class,TargetType.NOMINAL,AlgorithmKind.CLASSIFIER),
  JSON_RG_MR(CorpusExporterMRJsonTarget.class,TargetType.NUMERIC,AlgorithmKind.REGRESSOR),
  JSON_SEQ_MR(CorpusExporterMRJsonSeq.class,TargetType.NOMINAL,AlgorithmKind.SEQUENCE_TAGGER),
  LibSVM_CL_MR(CorpusExporterMRLibSVM.class,TargetType.NOMINAL,AlgorithmKind.CLASSIFIER),
  LibSVM_RG_MR(CorpusExporterMRLibSVM.class,TargetType.NUMERIC,AlgorithmKind.REGRESSOR),
  MatrixMarket2_CL_MR(CorpusExporterMRMatrixMarket2.class,TargetType.NOMINAL,AlgorithmKind.CLASSIFIER),
  MatrixMatket2_RG_MR(CorpusExporterMRMatrixMarket2.class,TargetType.NUMERIC,AlgorithmKind.REGRESSOR
  );

  
  private Exporter(Class<?> corpusExporterClass, TargetType ttype, AlgorithmKind algkind) {
    this.corpusExporterClass = corpusExporterClass;
    this.ttype = ttype;
    this.algkind = algkind;
  }
  private Class<?> corpusExporterClass = null;
  private TargetType ttype = TargetType.NOMINAL;
  private AlgorithmKind algkind = AlgorithmKind.CLASSIFIER;
  public Class<?> getCorpusExporterClass() { return corpusExporterClass; }
  public TargetType getTargetType() { return ttype; }
  public AlgorithmKind getAlgorithmKind() {return algkind; }
}
