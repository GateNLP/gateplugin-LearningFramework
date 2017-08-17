/*
 * Copyright (c) 2015-2016 The University Of Sheffield.
 *
 * This file is part of gateplugin-LearningFramework 
 * (see https://github.com/GateNLP/gateplugin-LearningFramework).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package gate.plugin.learningframework.tests;

import cc.mallet.pipe.Pipe;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.creole.ResourceInstantiationException;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.ModelApplication;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.engines.AlgorithmClassification;
import gate.plugin.learningframework.engines.AlgorithmRegression;
import gate.plugin.learningframework.engines.Engine;
import gate.plugin.learningframework.engines.EvaluationResultClXval;
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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 *
 * @author Johann Petrak
 */
public class TestEngineLibSVM {

  @BeforeClass
  public static void init() throws GateException {
    gate.Gate.init();
    // load the plugin
    gate.Utils.loadPlugin(new File("."));
  }
  
  @Test
  public void testEngineLibSvmForClass() throws MalformedURLException, ResourceInstantiationException {
    File configFile = new File("tests/cl-ionosphere/feats.xml");
    FeatureSpecification spec = new FeatureSpecification(configFile);
    FeatureInfo featureInfo = spec.getFeatureInfo();
    CorpusRepresentationMalletTarget crm = new CorpusRepresentationMalletTarget(featureInfo, ScalingMethod.NONE, TargetType.NOMINAL);
    Engine engine = Engine.createEngine(AlgorithmClassification.LIBSVM_CL, "", crm);
    System.err.println("TESTS: have engine "+engine);
    
    // load a document and train the model
    Document doc = loadDocument(new File("tests/cl-ionosphere/ionosphere_gate.xml"));
    
    AnnotationSet instanceAS = doc.getAnnotations().get("Mention");
    AnnotationSet sequenceAS = null;
    AnnotationSet inputAS = doc.getAnnotations();
    AnnotationSet classAS = null;
    String targetFeature = "class";
    String nameFeature = null;
    crm.add(instanceAS, sequenceAS, inputAS, classAS, targetFeature, TargetType.NOMINAL, "", nameFeature, null);
    System.err.println("TESTS: added instances, number of instances now: "+crm.getRepresentationMallet().size());
    // Use the same parameters as we did in previous tests with previous versions so we can 
    // compare the results
    engine.trainModel(new File("."),"","-c 1000 -g 0.02");
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
    List<ModelApplication> gcs = engine2.applyModel(instanceAS, inputAS, sequenceAS, parms);
    System.err.println("Number of classifications: "+gcs.size());
    ModelApplication.applyClassification(doc, gcs, "target", lfAS, null);
    
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
    assertEquals(0.9972, acc, 0.01);
    
  }
  
  @Test
  public void testEngineLibSvmEvalClass() throws MalformedURLException, ResourceInstantiationException {
    File configFile = new File("tests/cl-ionosphere/feats.xml");
    FeatureSpecification spec = new FeatureSpecification(configFile);
    FeatureInfo featureInfo = spec.getFeatureInfo();
    CorpusRepresentationMalletTarget crm = new CorpusRepresentationMalletTarget(featureInfo, ScalingMethod.NONE, TargetType.NOMINAL);
    Engine engine = Engine.createEngine(AlgorithmClassification.LIBSVM_CL, "", crm);
    System.err.println("TestEngineLibSVM-testEngineLibSvmEvalClass: have engine "+engine);
    
    // load the document and run xcross validation evaluation
    Document doc = loadDocument(new File("tests/cl-ionosphere/ionosphere_gate.xml"));
    
    AnnotationSet instanceAS = doc.getAnnotations().get("Mention");
    AnnotationSet sequenceAS = null;
    AnnotationSet inputAS = doc.getAnnotations();
    AnnotationSet classAS = null;
    String targetFeature = "class";
    String nameFeature = null;
    crm.add(instanceAS, sequenceAS, inputAS, classAS, targetFeature, TargetType.NOMINAL, "", nameFeature, null);
    System.err.println("added instances, number of instances now: "+crm.getRepresentationMallet().size());

    // method parameters: algparameters, method, folds, fraction, repeats, stratification
    EvaluationResultClXval res = (EvaluationResultClXval)engine.evaluate("-c 1000 -g 0.02", EvaluationMethod.CROSSVALIDATION, 10, 0.66, 1);
    System.err.println("TESTS-EVALUATION1: "+res);
    assertEquals(0.9088, res.accuracyEstimate,0.0001);
    res = (EvaluationResultClXval)engine.evaluate("-c 10 -g 0.1", EvaluationMethod.CROSSVALIDATION, 10, 0.66, 1);
    System.err.println("TESTS-EVALUATION2: "+res);
    assertEquals(0.9515, res.accuracyEstimate,0.0001);
  }
  

  
  @Test
  public void testEngineLibSVMRegression1() throws MalformedURLException, ResourceInstantiationException {
    File configFile = new File("tests/rg-abalone/feats.xml");
    FeatureSpecification spec = new FeatureSpecification(configFile);
    FeatureInfo featureInfo = spec.getFeatureInfo();
    CorpusRepresentationMalletTarget crm = new CorpusRepresentationMalletTarget(featureInfo, ScalingMethod.NONE,TargetType.NUMERIC);
    Engine engine = Engine.createEngine(AlgorithmRegression.LIBSVM_RG, "", crm);
    System.err.println("TESTS: have engine "+engine);
    
    // load a document and train the model
    Document doc = loadDocument(new File("tests/rg-abalone/abalone_gate.xml"));
    
    AnnotationSet instanceAS = doc.getAnnotations().get("Mention");
    AnnotationSet sequenceAS = null;
    AnnotationSet inputAS = doc.getAnnotations();
    AnnotationSet classAS = null;
    String targetFeature = "target";
    String nameFeature = null;
    crm.add(instanceAS, sequenceAS, inputAS, classAS, targetFeature, TargetType.NUMERIC, "", nameFeature, null);
    System.err.println("TESTS: added instances, number of instances now: "+crm.getRepresentationMallet().size());
    engine.trainModel(null,"","");
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
    List<ModelApplication> gcs = engine2.applyModel(instanceAS, inputAS, sequenceAS, parms);
    System.err.println("Number of classifications: "+gcs.size());
    ModelApplication.applyClassification(doc, gcs, "target", lfAS, null);
    
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
    assertEquals("Mean square error",0.0001758, mse, 0.0000001);
    assertEquals("Mean square error",0.0002052, mae, 0.0000001);
    
  }

  
  
}
