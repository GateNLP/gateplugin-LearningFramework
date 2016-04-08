
package gate.plugin.learningframework.tests;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.creole.ResourceInstantiationException;
import gate.plugin.learningframework.GateClassification;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import static gate.plugin.learningframework.data.CorpusRepresentationWeka.emptyDatasetFromMallet;
import static gate.plugin.learningframework.data.CorpusRepresentationWeka.wekaInstanceFromMalletInstance;
import gate.plugin.learningframework.engines.AlgorithmClassification;
import gate.plugin.learningframework.engines.AlgorithmRegression;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.TargetType;
import gate.plugin.learningframework.mallet.LFPipe;
import static gate.plugin.learningframework.tests.Utils.loadDocument;
import gate.util.GateException;
import java.io.File;
import java.net.MalformedURLException;
import java.util.List;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import weka.core.Instances;

/**
 *
 * @author Johann Petrak
 */
public class TestEngineWekaClassification {

  @BeforeClass
  public static void init() throws GateException {
    gate.Gate.init();
    // load the plugin
    gate.Utils.loadPlugin(new File("."));
  }
  
  @Test
  public void testEngineWekaClassification1() throws MalformedURLException, ResourceInstantiationException {
    File configFile = new File("tests/cl-ionosphere/feats.xml");
    FeatureSpecification spec = new FeatureSpecification(configFile);
    FeatureInfo featureInfo = spec.getFeatureInfo();
    CorpusRepresentationMalletTarget crm = new CorpusRepresentationMalletTarget(featureInfo, ScalingMethod.NONE,TargetType.NOMINAL);
    Engine engine = Engine.createEngine(AlgorithmClassification.WEKA_CL_NAIVE_BAYES, "", crm);
    System.err.println("TESTS: have engine "+engine);
    
    // load a document and train the model
    Document doc = loadDocument(new File("tests/cl-ionosphere/ionosphere_gate.xml"));
    
    AnnotationSet instanceAS = doc.getAnnotations().get("Mention");
    AnnotationSet sequenceAS = null;
    AnnotationSet inputAS = doc.getAnnotations();
    AnnotationSet classAS = null;
    String targetFeature = "class";
    String nameFeature = null;
    crm.add(instanceAS, sequenceAS, inputAS, classAS, targetFeature, TargetType.NOMINAL, nameFeature);
    System.err.println("TESTS: added instances, number of instances now: "+crm.getRepresentationMallet().size());

    
    // check that the data has been extracted correctly
    InstanceList il = crm.getRepresentationMallet();
    //assertEquals(4177, il.size());
    Alphabet da = il.getDataAlphabet();
    //System.err.println("Data Alphabet: "+da);
    LabelAlphabet la = (LabelAlphabet)il.getTargetAlphabet();
    //System.err.println("Label Alphabet: "+la);
    
    // Low-level check if the conversion of the structure works ...
    Instances wekaInstances =  emptyDatasetFromMallet(crm);
    //System.err.println("Weka attributes: "+wekaInstances.toSummaryString());
    
    // check the conversion into weka format works
    // For funs, just convert the first instance:
    weka.core.Instance wekaInstance = wekaInstanceFromMalletInstance(wekaInstances, il.get(0));
    //System.err.println("MalletInstance 0, data="+il.get(0).getData()+" target="+il.get(0).getTarget());
    //System.err.println("WekaInstance 0="+wekaInstance);
    
    engine.trainModel("");
    System.err.println("TESTS: model trained");
    System.err.println("TESTS: engine before saving: "+engine);
    engine.saveEngine(new File("."));
    
    // Now check if we can restore the engine and thus the corpus representation
    Engine engine2 = Engine.loadEngine(new File("."), "");
    System.err.println("RESTORED engine is "+engine2);
    
    // check if the corpusRepresentation has been restored correctly
    CorpusRepresentationMallet crm2 = engine2.getCorpusRepresentationMallet();
    assertNotNull(crm2);
    assertTrue(crm2 instanceof CorpusRepresentationMalletTarget);
    CorpusRepresentationMalletTarget crmc2 = (CorpusRepresentationMalletTarget)crm2;
    Pipe pipe = crmc2.getPipe();
    assertNotNull(pipe);
    assertTrue(pipe instanceof LFPipe);
    LFPipe lfpipe = (LFPipe)pipe;
    FeatureInfo fi = lfpipe.getFeatureInfo();
    assertNotNull(fi);
    
    AnnotationSet lfAS = doc.getAnnotations("LF");
    String parms = "";
    List<GateClassification> gcs = engine2.classify(instanceAS, inputAS, sequenceAS, parms);
    System.err.println("Number of classifications: "+gcs.size());
    GateClassification.applyClassification(doc, gcs, "target", lfAS, null);
    
    System.err.println("Original instances: "+instanceAS.size()+", classification: "+lfAS.size());
    
    // quick and dirty evaluation: go through all the original annotations, get the 
    // co-extensive annotations from LF, and compare the values from the "class" feature
    int total = 0;
    int correct = 0;
    for(Annotation orig : instanceAS) {
      total++;
      Annotation lf = gate.Utils.getOnlyAnn(gate.Utils.getCoextensiveAnnotations(lfAS, orig));
      //System.err.println("ORIG="+orig+", lf="+lf);
      if(orig.getFeatures().get("class").equals(lf.getFeatures().get("target"))) {
        correct++;
      }
    }
    
    double acc = (double)correct / (double)total;
    System.err.println("Got total="+total+", correct="+correct+", acc="+acc);
    assertEquals(0.8291, acc, 0.01);
  }

  
}
