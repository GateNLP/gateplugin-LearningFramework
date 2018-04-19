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
package gate.plugin.learningframework;

import gate.plugin.learningframework.export.CorpusExporterARFF;
import gate.plugin.learningframework.export.CorpusExporterLibSVM;
import gate.plugin.learningframework.engines.AlgorithmKind;
import gate.plugin.learningframework.export.CorpusExporterCSV;
import gate.plugin.learningframework.export.CorpusExporterMatrixMarket2;
import gate.plugin.learningframework.export.CorpusExporterJsonSeq;
import gate.plugin.learningframework.export.CorpusExporterJsonTarget;
import gate.plugin.learningframework.features.TargetType;

public enum Exporter {
  EXPORTER_ARFF_CLASS(CorpusExporterARFF.class,TargetType.NOMINAL,AlgorithmKind.CLASSIFIER),  
  EXPORTER_ARFF_REGRESSION(CorpusExporterARFF.class,TargetType.NUMERIC,AlgorithmKind.REGRESSOR),  
  EXPORTER_CSV_CLASS(CorpusExporterCSV.class,TargetType.NOMINAL,AlgorithmKind.CLASSIFIER),  
  EXPORTER_CSV_REGRESSION(CorpusExporterCSV.class,TargetType.NUMERIC,AlgorithmKind.REGRESSOR),  
  //EXPORTER_MALLET_CLASS(),
  EXPORTER_JSON_SEQ(CorpusExporterJsonSeq.class,TargetType.NOMINAL,AlgorithmKind.SEQUENCE_TAGGER),
  EXPORTER_JSON_REGRESSION(CorpusExporterJsonTarget.class,TargetType.NUMERIC,AlgorithmKind.REGRESSOR),
  EXPORTER_JSON_CLASS(CorpusExporterJsonTarget.class,TargetType.NOMINAL,AlgorithmKind.CLASSIFIER),
  EXPORTER_LIBSVM_CLASS(CorpusExporterLibSVM.class,TargetType.NOMINAL,AlgorithmKind.CLASSIFIER),
  EXPORTER_LIBSVM_REGRESSION(CorpusExporterLibSVM.class,TargetType.NUMERIC,AlgorithmKind.REGRESSOR),
  EXPORTER_MATRIXMARKET2_CLASS(CorpusExporterMatrixMarket2.class,TargetType.NOMINAL,AlgorithmKind.CLASSIFIER),
  EXPORTER_MATRIXMARKET2_REGRESSION(CorpusExporterMatrixMarket2.class,TargetType.NUMERIC,AlgorithmKind.REGRESSOR
  );

  
  private Exporter(Class corpusExporterClass, TargetType ttype, AlgorithmKind algkind) {
    this.corpusExporterClass = corpusExporterClass;
    this.ttype = ttype;
    this.algkind = algkind;
  }
  private Class corpusExporterClass = null;
  private TargetType ttype = TargetType.NOMINAL;
  private AlgorithmKind algkind = AlgorithmKind.CLASSIFIER;
  public Class getCorpusExporterClass() { return corpusExporterClass; }
  public TargetType getTargetType() { return ttype; }
  public AlgorithmKind getAlgorithmKind() {return algkind; }
}
