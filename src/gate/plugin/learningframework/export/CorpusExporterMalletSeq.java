/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.export;

import cc.mallet.types.InstanceList;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.Info;
import java.io.File;

/**
 *
 * @author johann
 */
public class CorpusExporterMalletSeq extends CorpusExporter {

  @Override
  public Info getInfo() {
    Info info = new Info();
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmSequenceTagging";
    info.algorithmName = "MALLET_SQ_DUMMY";
    info.engineClass = "DUMMY";
    info.modelClass =  "DUMMY";    
    return info;
  }

  @Override
  public void export(File directory, CorpusRepresentationMallet cr, String instanceType, String parms) {    
    InstanceList malletInstances = cr.getRepresentationMallet();
    //Pipe pipe = malletInstances.getPipe();
    //Attributes attrs = new Attributes(pipe,instanceType);
    malletInstances.save(new File(directory, "data.malletseq.ser"));    
  } // export

  
}
