
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
import org.junit.Test;
import org.junit.BeforeClass;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import weka.core.Instances;

/**
 *
 * @author Johann Petrak
 */
public class TestEngineWekaRegression {

  @BeforeClass
  public static void init() throws GateException {
    gate.Gate.init();
    // load the plugin
    gate.Utils.loadPlugin(new File("."));
  }
  

  @Test
  public void testEngineWekaRegression1() throws MalformedURLException, ResourceInstantiationException {
    File configFile = new File("tests/rg-abalone/feats.xml");
    FeatureSpecification spec = new FeatureSpecification(configFile);
    FeatureInfo featureInfo = spec.getFeatureInfo();
    CorpusRepresentationMalletTarget crm = new CorpusRepresentationMalletTarget(featureInfo, ScalingMethod.NONE,TargetType.NUMERIC);
    Engine engine = Engine.createEngine(AlgorithmRegression.WEKA_RG_LINEAR_REGRESSION, "", crm);
    System.err.println("TESTS: have engine "+engine);
    
    // load a document and train the model
    Document doc = loadDocument(new File("tests/rg-abalone/abalone_gate.xml"));
    
    AnnotationSet instanceAS = doc.getAnnotations().get("Mention");
    System.err.println("Number of instances: "+instanceAS.size());
    AnnotationSet sequenceAS = null;
    AnnotationSet inputAS = doc.getAnnotations();
    AnnotationSet classAS = null;
    String targetFeature = "target";
    String nameFeature = null;
    crm.add(instanceAS, sequenceAS, inputAS, classAS, targetFeature, TargetType.NUMERIC, nameFeature);
    System.err.println("TESTS: added instances, number of instances now: "+crm.getRepresentationMallet().size());
    
    // check that the data has been extracted correctly
    InstanceList il = crm.getRepresentationMallet();
    assertEquals(4177, il.size());
    Alphabet da = il.getDataAlphabet();
    //System.err.println("Data Alphabet: "+da);
    assertTrue(da.contains("A:Mention:length"));
    LabelAlphabet la = (LabelAlphabet)il.getTargetAlphabet();
    //System.err.println("Label Alphabet: "+la);
    assertEquals(la,null);
    
    // Low-level check if the conversion of the structure works ...
    Instances wekaInstances =  emptyDatasetFromMallet(crm);
    //System.err.println("Weka attributes: "+wekaInstances.toSummaryString());
    //assertEquals(11,wekaInstances.numAttributes());
    //assertEquals(10,wekaInstances.classIndex());
    
    // check the conversion into weka format works
    // For funs, just convert the first instance:
    weka.core.Instance wekaInstance = wekaInstanceFromMalletInstance(wekaInstances, il.get(0));
    //System.err.println("MalletInstance 0, data="+il.get(0).getData()+" target="+il.get(0).getTarget());
    //System.err.println("WekaInstance 0="+wekaInstance);
    
    //CorpusRepresentationWeka crw = new CorpusRepresentationWeka(crm);
    
    
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
    // co-extensive annotations from LF, and compare the values from the "target" feature, calculate
    // MSE and MAE
    double totalAbs = 0.0;
    double totalSquared = 0.0;
    int n = 0;
    for(Annotation orig : instanceAS) {
      n++;
      Annotation lf = gate.Utils.getOnlyAnn(gate.Utils.getCoextensiveAnnotations(lfAS, orig));
      //System.err.println("ORIG="+orig+", lf="+lf);
      double origTarget = (double)Double.parseDouble(orig.getFeatures().get("target").toString());
      double predTarget = (double)lf.getFeatures().get("target");
      totalAbs = Math.abs(origTarget - predTarget);
      totalSquared = Math.abs((origTarget - predTarget)*(origTarget - predTarget));
    }
    
    double mae = totalAbs / (double)n;
    double mse = totalSquared / (double)n;
    System.err.println("Got total="+n+", mse="+mse+", mae="+mae);
    assertEquals("Mean square error",  0.000252, mse, 0.00001);
    assertEquals("Mean absolute error",0.000245, mae, 0.00001);
  }

  
}
