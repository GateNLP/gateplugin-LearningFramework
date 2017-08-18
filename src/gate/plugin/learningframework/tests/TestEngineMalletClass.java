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
import gate.plugin.learningframework.ModelApplication;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.data.CorpusRepresentation;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
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
import static gate.plugin.learningframework.tests.Utils.*;
import java.net.MalformedURLException;
import java.util.List;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Petrak
 */
public class TestEngineMalletClass {

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
    Engine engine = Engine.createEngine(AlgorithmClassification.MALLET_CL_C45, "", featureInfo, TargetType.NOMINAL, null);
    CorpusRepresentationMalletTarget crm = (CorpusRepresentationMalletTarget)engine.getCorpusRepresentation();
    System.err.println("TESTS: have engine "+engine);
    
    // load a document and train the model
    Document doc = loadDocument(new File("tests/cl-ionosphere/ionosphere_gate.xml"));
    System.err.println("TESTS: have document");
    
    AnnotationSet instanceAS = doc.getAnnotations().get("Mention");
    AnnotationSet sequenceAS = null;
    AnnotationSet inputAS = doc.getAnnotations();
    AnnotationSet classAS = null;
    String targetFeature = "class";
    String nameFeature = null;
    crm.add(instanceAS, sequenceAS, inputAS, classAS, targetFeature, TargetType.NOMINAL, "", nameFeature, null);
    System.err.println("TESTS: added instances, number of instances now: "+crm.getRepresentationMallet().size());
    engine.trainModel(null,"","");
    System.err.println("TESTS: model trained");
    System.err.println("TESTS: engine before saving: "+engine);
    engine.saveEngine(new File("."));
    
    // Now check if we can restore the engine and thus the corpus representation
    Engine engine2 = Engine.loadEngine(new File("."), "");
    System.err.println("RESTORED engine is "+engine2);
    
    // check if the corpusRepresentation has been restored correctly
    CorpusRepresentation cr2 = engine2.getCorpusRepresentation();
    assertNotNull(cr2);
    assertTrue(cr2 instanceof CorpusRepresentationMalletTarget);
    CorpusRepresentationMalletTarget crmc2 = (CorpusRepresentationMalletTarget)cr2;
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
    assertEquals(0.9630, acc, 0.01);
  }
  
}
