
package gate.plugin.learningframework.tests;

import cc.mallet.pipe.Pipe;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.plugin.evaluation.api.AnnotationDifferTagging;
import gate.plugin.evaluation.api.AnnotationTypeSpecs;
import gate.plugin.evaluation.api.EvalStatsTagging;
import gate.plugin.evaluation.api.EvalStatsTagging4Score;
import gate.plugin.evaluation.api.FeatureComparison;
import gate.plugin.learningframework.GateClassification;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.data.CorpusRepresentationMalletSeq;
import gate.plugin.learningframework.engines.AlgorithmClassification;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.TargetType;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateException;
import java.io.File;
import org.junit.Test;
import org.junit.BeforeClass;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Petrak
 */
public class TestEngineMalletSeq {

  @BeforeClass
  public static void init() throws GateException {
    gate.Gate.init();
    // load the Format_FastInfoset plugin, we need it for the .finf files
    gate.Utils.loadPlugin("Format_FastInfoset");
    // load the plugin
    gate.Utils.loadPlugin(new File("."));
  }
  
  @Test
  public void testEngineMalletSeq1() throws MalformedURLException, ResourceInstantiationException, IOException, XMLStreamException {
    File configFile = new File("tests/seq-wikipedia1/feats.xml");
    FeatureSpecification spec = new FeatureSpecification(configFile);
    FeatureInfo featureInfo = spec.getFeatureInfo();
    CorpusRepresentationMalletSeq crm = new CorpusRepresentationMalletSeq(featureInfo, ScalingMethod.NONE);
    Engine engine = Engine.createEngine(AlgorithmClassification.MALLET_SEQ_CRF, "", crm);
    System.err.println("TESTS: have engine "+engine);
    
    // for this we need to go through a number of documents and train on all of them
    // The directory testing/trainingset_prepared contains the prepared documents in XML format
    // They have:
    // class annotations Mention
    // instance annotations Token
    // sequence annotations Sentence
    
    // To do this efficiently we create a corpus and populate it from the directory
    Corpus corpus = (Corpus)Factory.createResource("gate.corpora.CorpusImpl");
    corpus.populate(new File("tests/seq-wikipedia1/corpus/").toURI().toURL(), 
            new gate.util.ExtensionFileFilter("","finf"), "UTF-8", true);
    
    for(Document doc : corpus) {
      //System.err.println("Processing document "+doc.getName());
      AnnotationSet instanceAS = doc.getAnnotations().get("Token");
      AnnotationSet sequenceAS = doc.getAnnotations().get("Sentence");
      AnnotationSet inputAS = doc.getAnnotations();
      AnnotationSet classAS = doc.getAnnotations().get("Link");
      String targetFeature = null;
      String nameFeature = null;
      crm.add(instanceAS, sequenceAS, inputAS, classAS, targetFeature, TargetType.NOMINAL, nameFeature);
    }
    
    System.err.println("TESTS: added instances, number of instances now: "+crm.getRepresentationMallet().size());
    
    engine.trainModel("");
    //System.err.println("TESTS: model trained");
    //System.err.println("TESTS: engine before saving: "+engine);
    engine.saveEngine(new File("."));
    
    // Now check if we can restore the engine and thus the corpus representation
    Engine engine2 = Engine.loadEngine(new File("."), "");
    System.err.println("RESTORED engine is "+engine2);
    
    // check if the corpusRepresentation has been restored correctly
    CorpusRepresentationMallet crm2 = engine2.getCorpusRepresentationMallet();
    assertNotNull(crm2);
    assertTrue(crm2 instanceof CorpusRepresentationMalletSeq);
    CorpusRepresentationMalletSeq crmc2 = (CorpusRepresentationMalletSeq)crm2;
    Pipe pipe = crmc2.getPipe();
    assertNotNull(pipe);
    assertTrue(pipe instanceof LFPipe);
    LFPipe lfpipe = (LFPipe)pipe;
    FeatureInfo fi = lfpipe.getFeatureInfo();
    assertNotNull(fi);
    
    // For the application, first remove the class annotations from the default set. This is 
    // not strictly necessary but just so we are sure no cheating is possible
    for(Document doc : corpus) {
      doc.getAnnotations().removeAll(doc.getAnnotations().get("Link"));
    }
    
    //File outDir = new File("TestEngineMalletSeqOut");
    //FileUtils.deleteDirectory(outDir);
    //outDir.mkdir();
    
    // Setup the evaluation
    List<String> evalTypes = new ArrayList<String>();
    evalTypes.add("Link");
    AnnotationTypeSpecs annotationTypeSpecs = new AnnotationTypeSpecs(evalTypes);    
    EvalStatsTagging stats = new EvalStatsTagging4Score(Double.NaN);
    
    // now go through all the documents and create "Link" annotations in the LF set
    String parms = "";
    for(Document doc : corpus) {
      //System.err.println("Applying to document "+doc.getName());
      AnnotationSet instanceAS = doc.getAnnotations().get("Token");
      AnnotationSet sequenceAS = doc.getAnnotations().get("Sentence");
      AnnotationSet inputAS = doc.getAnnotations();
      List<GateClassification> gcs = engine2.classify(instanceAS, inputAS, sequenceAS, parms);
      
      // actually create annotations for the GateClassification instances
      AnnotationSet lfAS = doc.getAnnotations("LF_TMP");
      // First null: targetFeature name, we do not need this and maybe should remove that 
      // parameter alltogether
      // Second null: confidence threshold: if null, do not check the threshold at all
      //GateClassification.addClassificationAnnotations(doc, gcs, lfAS, null, null);
      GateClassification.applyClassification(doc, gcs, null, lfAS, null);
      
      AnnotationSet outputAS = doc.getAnnotations("LF");
      String outputType = "Link";
      instanceAS = lfAS;
      GateClassification.addSurroundingAnnotations(inputAS, instanceAS, outputAS, outputType, null);
      
      AnnotationDifferTagging docDiffer = new AnnotationDifferTagging(
              doc.getAnnotations("Key").get("Link"),
              doc.getAnnotations("LF").get("Link"),
              new HashSet(),
              FeatureComparison.FEATURE_EQUALITY,
              annotationTypeSpecs
      );
      stats.add(docDiffer.getEvalStatsTagging());
      
      //File outFile = new File(outDir,doc.getName());
      //gate.corpora.DocumentStaxUtils.writeDocument(doc,outFile);
    }
    
    System.err.println("GOT STATS F strict="+stats.getFMeasureStrict(1.0));
    System.err.println("GOT STATS F lenient="+stats.getFMeasureLenient(1.0));
    
    assertEquals(0.3646, stats.getFMeasureStrict(1.0), 0.01);
    assertEquals(0.4299, stats.getFMeasureLenient(1.0), 0.01);
        
  }

  
}
