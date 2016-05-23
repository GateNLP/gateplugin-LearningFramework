/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.export;

import gate.plugin.learningframework.data.CorpusRepresentationLibSVM;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.Info;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;
import libsvm.svm_problem;

/**
 *
 * @author johann
 */
public class CorpusExporterLibSVM extends CorpusExporter {

  @Override
  public Info getInfo() {
    Info info = new Info();
    // TODO: we should check for regression vs classification HER£E
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmClassification";
    info.algorithmName = "LibSVM_CL_DUMMY";
    info.engineClass = "DUMMY";
    info.modelClass =  "DUMMY";    
    return info;
  }

  @Override
  public void export(File directory, CorpusRepresentationMallet crm, String instanceType, String parms) {
    // For now we do this by converting the mallet representation to a libsvm 
    // representation first and then exporting from there using the old code we 
    // had. 
    // TODO: do this directly from the Mallet representation!!
    CorpusRepresentationLibSVM crl = new CorpusRepresentationLibSVM(crm);    
    if (parms != null && !parms.isEmpty()) {
      throw new GateRuntimeException("No parameters supported, must be null, not: \""+parms+"\"");
    }
    svm_problem prob = crl.getRepresentationLibSVM();
    PrintStream out = null;
    File outFile = new File(directory, "data.libsvm");
    try {
      out = new PrintStream(outFile);
      for (int i = 0; i < prob.l; i++) {
        out.print(prob.y[i]);
        for (int j = 0; j < prob.x[i].length; j++) {
          out.print(" ");
          out.print(prob.x[i][j].index);
          out.print(":");
          out.print(prob.x[i][j].value);
        }
        out.println();
      }
      out.close();
    } catch (FileNotFoundException ex) {
      System.err.println("Could not write training instances to svm format file");
      ex.printStackTrace(System.out);
    }

    
  }
  
}
