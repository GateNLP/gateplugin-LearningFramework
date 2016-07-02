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

import gate.plugin.learningframework.export.CorpusExporter;
import gate.plugin.learningframework.export.CorpusExporterARFF;
import gate.plugin.learningframework.export.CorpusExporterLibSVM;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.Info;
import gate.plugin.learningframework.export.CorpusExporterMatrixMarket2;
import gate.plugin.learningframework.features.TargetType;
import gate.plugin.learningframework.mallet.LFPipe;
import java.io.File;
import java.util.ArrayList;

public enum Exporter {
  EXPORTER_ARFF_CLASS(CorpusExporterARFF.class,TargetType.NOMINAL),  
  EXPORTER_ARFF_REGRESSION(CorpusExporterARFF.class,TargetType.NUMERIC),  
  //EXPORTER_MALLET_CLASS(),
  //EXPORTER_MALLET_SEQ(),
  EXPORTER_LIBSVM_CLASS(CorpusExporterLibSVM.class,TargetType.NOMINAL),
  EXPORTER_LIBSVM_REGRESSION(CorpusExporterLibSVM.class,TargetType.NUMERIC),
  EXPORTER_MATRIXMARKET2_CLASS(CorpusExporterMatrixMarket2.class,TargetType.NOMINAL),
  EXPORTER_MATRIXMARKET2_REGRESSION(CorpusExporterMatrixMarket2.class,TargetType.NUMERIC);

  /**
   * Write the instances to one or more files.
   * If parms is null, the "default natural format" for that representation is used, otherwise
   * some other format that this representation supports is created, depending on the concrete
   * parameters given.
   * @param directory
   */
  //public abstract void export(File directory, String parms);
  public static void export(CorpusRepresentationMallet crm, Exporter exporter, File directory, String instanceType, String parms) {
    CorpusExporter ce = null;
    try {
      ce = (CorpusExporter) exporter.getCorpusExporterClass().newInstance();
    } catch (Exception ex) {
      throw new RuntimeException("Could not instanciate exporter class " + exporter.getCorpusExporterClass(), ex);
    }
    // get the pre-filled info object
    Info info = ce.getInfo();
    // actually export the data
    ce.export(directory, crm, instanceType, parms);
    /*
    if(action == Exporter.EXPORTER_MALLET_CLASS) {
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmClassification";
    info.algorithmName = "MALLET_CL_DUMMY";
    info.engineClass = "gate.plugin.learningframework.engines.EngineMalletClass";
    info.modelClass = "cc.mallet.classify.Dummy";
    crm.export(directory, parms);
    } else if(action == Exporter.EXPORTER_ARFF_CLASS) {
    System.err.println("Exporting for classification in ARFF format to "+directory);
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmClassification";
    info.algorithmName = "ARFF_CL_DUMMY";
    info.engineClass = "gate.plugin.learningframework.engines.EngineMicroserviceArff";
    info.modelClass =  "gate.plugin.learningframework.engines.EngineMicroserviceArff";
    // TODO
    } else if(action == Exporter.EXPORTER_ARFF_REGRESSION) {
    System.err.println("Exporting for regression in ARFF format to "+directory);
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmRegression";
    info.engineClass = "gate.plugin.learningframework.engines.EngineMicroserviceArff";
    info.modelClass =  "gate.plugin.learningframework.engines.EngineMicroserviceArff";
    info.algorithmName = "ARFF_RG_DUMMY";
    // TODO
    } else if(action == Exporter.EXPORTER_LIBSVM_CLASS) {
    System.err.println("Exporting for classification as LibSVM to "+directory);
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmClassification";
    info.engineClass = "gate.plugin.learningframework.engines.EngineLibSVM";
    info.modelClass =  "todo.do.not.know.yet";
    info.algorithmName = "LIBSVM_CL_DUMMY";
    CorpusRepresentationLibSVM crl = new CorpusRepresentationLibSVM(crm);
    crl.export(directory, parms);
    } else if(action == Exporter.EXPORTER_LIBSVM_REGRESSION) {
    System.err.println("Exporting for regression as LibSVM to "+directory);
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmRegression";
    info.engineClass = "gate.plugin.learningframework.engines.EngineLibSVM";
    info.modelClass =  "todo.do.not.know.yet";
    info.algorithmName = "LIBSVM_RG_DUMMY";
    CorpusRepresentationLibSVM crl = new CorpusRepresentationLibSVM(crm);
    crl.export(directory, parms);
    } else {
    // NOTE: if we start to get lots more representations and export formats, maybe
    // we should do this by reflection somehow ...
    throw new GateRuntimeException("Export method not yet implemented: "+action);
    }
     */
    // In addition to the actual data file exported by the methods above,
    // always also export the pipe and a template info file!
    info.classAnnotationType = "null";
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
    info.save(directory);
    // finally save the Mallet corpus representation
    crm.savePipe(directory);
  }
  private Exporter(Class corpusExporterClass, TargetType ttype) {
    this.corpusExporterClass = corpusExporterClass;
    this.ttype = ttype;
  }
  private Class corpusExporterClass = null;
  private TargetType ttype = TargetType.NOMINAL;
  public Class getCorpusExporterClass() { return corpusExporterClass; }
  public TargetType getTargetType() { return ttype; }
}
