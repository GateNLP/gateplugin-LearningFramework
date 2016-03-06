
package gate.plugin.learningframework.tests;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Instance;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.creole.ResourceInstantiationException;
import gate.plugin.learningframework.GateClassification;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.data.CorpusRepresentationWeka;
import gate.plugin.learningframework.engines.AlgorithmClassification;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.engines.EngineWeka;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Johann Petrak
 */
public class TestFeatureScaling {

  @BeforeClass
  public static void init() throws GateException {
    gate.Gate.init();
    // load the plugin
    gate.Utils.loadPlugin(new File("."));
  }
  
  @Test
  public void testEngineWekaClass1() throws MalformedURLException, ResourceInstantiationException {
    File configFile = new File("tests/cl-ionosphere/feats.xml");
    FeatureSpecification spec = new FeatureSpecification(configFile);
    FeatureInfo featureInfo = spec.getFeatureInfo();
    CorpusRepresentationMalletTarget crm = new CorpusRepresentationMalletTarget(featureInfo, ScalingMethod.NONE, TargetType.NOMINAL);
    Engine engine = (EngineWeka)Engine.createEngine(AlgorithmClassification.WEKA_CL_NAIVE_BAYES, "", crm);
    System.err.println("TEST TestFeatureScaling: have new engine "+engine);
    
    // load a document and train the model
    Document doc = loadDocument(new File("tests/cl-ionosphere/ionosphere_gate.xml"));
    
    AnnotationSet instanceAS = doc.getAnnotations().get("Mention");
    AnnotationSet sequenceAS = null;
    AnnotationSet inputAS = doc.getAnnotations();
    AnnotationSet classAS = null;
    String targetFeature = "class";
    String nameFeature = null;
    crm.add(instanceAS, sequenceAS, inputAS, classAS, targetFeature, TargetType.NOMINAL, nameFeature);
    crm.addScaling(ScalingMethod.MEANVARIANCE_ALL_FEATURES);
    System.err.println("TESTS: added instances, number of instances now: "+crm.getRepresentationMallet().size());
    engine.trainModel("");
    System.err.println("TESTS: model trained");
    System.err.println("TESTS: engine before saving: "+engine);
    engine.saveEngine(new File("."));
    
    // also, get the first instance in Weka format, print it and save for later
    CorpusRepresentationWeka crw = new CorpusRepresentationWeka(crm);
    weka.core.Instance wekaInst1 = crw.getRepresentationWeka().get(0);
    
    // Now check if we can restore the engine and thus the corpus representation
    EngineWeka engine2 = (EngineWeka)Engine.loadEngine(new File("."), "");
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
    
    System.err.println("Weka instance for training="+wekaInst1);
    // Get the weka instance for the first instance annotation again ...
    Annotation instanceAnn = instanceAS.inDocumentOrder().get(0);
    crmc2.stopGrowth();
    weka.core.Instances wekaDS = engine2.getCorpusRepresentationWeka().getRepresentationWeka();
    Instance inst = crmc2.extractIndependentFeatures(instanceAnn, inputAS);
    // Mallet instances do not have a nice toString()
    //System.err.println("Mallet instance direct="+inst);
    weka.core.Instance wekaInst2 = CorpusRepresentationWeka.wekaInstanceFromMalletInstance(wekaDS, inst);
    System.err.println("WEKA directly converted = "+wekaInst2);
    
    inst = crmc2.getPipe().instanceFrom(inst);
    wekaInst2 = CorpusRepresentationWeka.wekaInstanceFromMalletInstance(wekaDS, inst);
    System.err.println("WEKA instance through pipe = "+wekaInst2);
    // Mallet instances do not have a nice toString()
    //System.err.println("Mallet instance after going through pipe="+inst);

    // make sure all 34 attributes are equal
    for(int i=0; i<34; i++) {
      assertEquals("Weka feature"+i+" is not equal",wekaInst1.value(i), wekaInst2.value(i),0.00001);
    }
    
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
