/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.export;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import gate.plugin.learningframework.Globals;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.Info;
import gate.plugin.learningframework.features.Datatype;
import gate.plugin.learningframework.features.FeatureExtraction;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *
 * @author johann
 */
public class CorpusExporterMalletTarget extends CorpusExporter {

  @Override
  public Info getInfo() {
    Info info = new Info();
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmClassification";
    info.algorithmName = "MALLET_CL_DUMMY";
    info.engineClass = "DUMMY";
    info.modelClass =  "DUMMY";    
    return info;
  }

  @Override
  public void export(File directory, CorpusRepresentationMallet cr, String instanceType, String parms) {    
    InstanceList malletInstances = cr.getRepresentationMallet();
    //Pipe pipe = malletInstances.getPipe();
    //Attributes attrs = new Attributes(pipe,instanceType);
    malletInstances.save(new File(directory, "data.mallettarget.ser"));
  } // export

  
}
