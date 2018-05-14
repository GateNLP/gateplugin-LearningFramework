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

import gate.AnnotationSet;
import gate.Document;
import gate.creole.ResourceInstantiationException;
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
import gate.test.GATEPluginTests;

/**
 *
 * @author Johann Petrak
 */
public class TestFeatureScaling extends GATEPluginTests {

  @BeforeClass
  public static void init() throws GateException  {
    gate.Gate.init();
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
    crm.add(instanceAS, sequenceAS, inputAS, classAS, targetFeature, TargetType.NOMINAL, "", nameFeature, null);
    
    System.err.println("TESTS Scaling 1: added instances, number of instances now: "+crm.getRepresentationMallet().size());

    // TODO: make this test actually work!
    File outDir1 = new File(new File(System.getProperty("java.io.tmpdir")),"lf-unscaled");
    outDir1.mkdir();
    File outDir2 = new File(new File(System.getProperty("java.io.tmpdir")),"lf-scaled");
    outDir2.mkdir();
    //System.err.println("Exporting unscaled");
    //Exporter.export(crm, Exporter.ARFF_CL_MR, outDir1, "Mention", "");
    crm.finishAdding();
    //System.err.println("Exporting scaled");
    //Exporter.export(crm, Exporter.ARFF_CL_MR, outDir2, "Mention", "");

  }
  
}
