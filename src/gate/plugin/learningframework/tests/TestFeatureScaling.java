
package gate.plugin.learningframework.tests;

import gate.AnnotationSet;
import gate.Document;
import gate.creole.ResourceInstantiationException;
import gate.plugin.learningframework.Exporter;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.TargetType;
import static gate.plugin.learningframework.tests.Utils.loadDocument;
import gate.util.GateException;
import java.io.File;
import java.net.MalformedURLException;
import org.junit.Test;
import org.junit.BeforeClass;

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
  public void testEngineMalletClass1() throws MalformedURLException, ResourceInstantiationException {
    File configFile = new File("tests/cl-ionosphere/feats.xml");
    FeatureSpecification spec = new FeatureSpecification(configFile);
    FeatureInfo featureInfo = spec.getFeatureInfo();
    CorpusRepresentationMalletTarget crm = new CorpusRepresentationMalletTarget(featureInfo, ScalingMethod.MEANVARIANCE_ALL_FEATURES, TargetType.NOMINAL);
    
    Document doc = loadDocument(new File("tests/cl-ionosphere/ionosphere_gate.xml"));
    
    AnnotationSet instanceAS = doc.getAnnotations().get("Mention");
    AnnotationSet sequenceAS = null;
    AnnotationSet inputAS = doc.getAnnotations();
    AnnotationSet classAS = null;
    String targetFeature = "class";
    String nameFeature = null;
    crm.add(instanceAS, sequenceAS, inputAS, classAS, targetFeature, TargetType.NOMINAL, nameFeature);
    
    System.err.println("TESTS Scaling 1: added instances, number of instances now: "+crm.getRepresentationMallet().size());

    // TODO: make this test actually work!
    File outDir1 = new File(new File(System.getProperty("java.io.tmpdir")),"lf-unscaled");
    outDir1.mkdir();
    File outDir2 = new File(new File(System.getProperty("java.io.tmpdir")),"lf-scaled");
    outDir2.mkdir();
    //System.err.println("Exporting unscaled");
    //Exporter.export(crm, Exporter.EXPORTER_ARFF_CLASS, outDir1, "Mention", "");
    crm.finish();
    //System.err.println("Exporting scaled");
    //Exporter.export(crm, Exporter.EXPORTER_ARFF_CLASS, outDir2, "Mention", "");

  }
  
}
