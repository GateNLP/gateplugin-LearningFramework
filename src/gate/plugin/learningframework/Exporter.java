/*
 * Copyright (c) 1995-2015, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * Copyright 2015 South London and Maudsley NHS Trust and King's College London
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 */
package gate.plugin.learningframework;

import gate.plugin.learningframework.data.CorpusExporterARFF;
import gate.plugin.learningframework.data.CorpusExporterLibSVM;
import gate.plugin.learningframework.features.TargetType;

public enum Exporter {
  EXPORTER_ARFF_CLASS(CorpusExporterARFF.class,TargetType.NOMINAL),  
  EXPORTER_ARFF_REGRESSION(CorpusExporterARFF.class,TargetType.NUMERIC),  
  //EXPORTER_MALLET_CLASS(),
  //EXPORTER_MALLET_SEQ(),
  EXPORTER_LIBSVM_CLASS(CorpusExporterLibSVM.class,TargetType.NOMINAL),
  EXPORTER_LIBSVM_REGRESSION(CorpusExporterLibSVM.class,TargetType.NUMERIC);
  private Exporter(Class corpusExporterClass, TargetType ttype) {
    this.corpusExporterClass = corpusExporterClass;
    this.ttype = ttype;
  }
  private Class corpusExporterClass = null;
  private TargetType ttype = TargetType.NOMINAL;
  public Class getCorpusExporterClass() { return corpusExporterClass; }
  public TargetType getTargetType() { return ttype; }
}
