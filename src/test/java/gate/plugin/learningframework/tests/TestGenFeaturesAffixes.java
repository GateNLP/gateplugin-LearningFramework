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

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.plugin.learningframework.LF_GenFeatures_Affixes;
import gate.test.GATEPluginTests;
import gate.util.GateException;
import java.util.List;
import junit.framework.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Johann Petrak <johann.petrak@gmail.com>
 */
public class TestGenFeaturesAffixes  extends GATEPluginTests {

  @BeforeClass
  public static void init() throws GateException {
    gate.Gate.init();
  }
  
  
  @Test
  public void testGenSuffixes1() throws ResourceInstantiationException, ExecutionException {
    Document doc = Factory.newDocument("This is a veryveryverylong word");
    AnnotationSet anns = doc.getAnnotations();
    FeatureMap fm;
    fm = gate.Utils.featureMap("string", "This");
    gate.Utils.addAnn(anns, 0L, 4L, "Token", fm);
    fm = gate.Utils.featureMap("string", "is");
    gate.Utils.addAnn(anns, 5L, 7L, "Token", fm);
    fm = gate.Utils.featureMap("string", "a");
    gate.Utils.addAnn(anns, 8L, 9L, "Token", fm);
    fm = gate.Utils.featureMap("string", "veryveryverylong");
    gate.Utils.addAnn(anns, 10L, 26L, "Token", fm);
    fm = gate.Utils.featureMap("string", "word");
    gate.Utils.addAnn(anns, 27L, 31L, "Token", fm);
    LF_GenFeatures_Affixes pr = new LF_GenFeatures_Affixes();
    pr.init();
    gate.Corpus c = Factory.newCorpus("tmp");
    c.add(doc);    
    pr.setGenPrefixes(Boolean.TRUE);
    pr.setGenSuffixes(Boolean.TRUE);
    pr.setInstanceType("Token");
    pr.setMapToUpper(Boolean.FALSE);
    pr.setMappingLanguage("en");
    pr.setMaxPrefixLength(4);
    pr.setMaxSuffixLength(4);
    pr.setMinNonSuffixLength(2);
    pr.setMinNonPrefixLength(2);
    pr.setMinPrefixLength(2);
    pr.setMinSuffixLength(2);
    pr.setPrefixFeatureName("pref");
    pr.setSuffixFeatureName("suf");
    pr.setStringFeature("string");
    SerialAnalyserController ctrl = 
            (SerialAnalyserController) Factory.createResource(
                    "gate.creole.SerialAnalyserController", 
                    Factory.newFeatureMap(), 
                    Factory.newFeatureMap(), "tmp"); 
    ctrl.add(pr);
    ctrl.setCorpus(c);
    ctrl.execute();
    anns = doc.getAnnotations();
    List<Annotation> sortedanns = anns.inDocumentOrder();
    fm = sortedanns.get(0).getFeatures();
    // System.err.println("DEBUG: doc="+doc);
    Assert.assertTrue(fm.containsKey("pref2"));
    Assert.assertTrue(fm.containsKey("suf2"));
    Assert.assertFalse(fm.containsKey("pref1"));
    Assert.assertFalse(fm.containsKey("pref3"));
    Assert.assertFalse(fm.containsKey("suf1"));
    Assert.assertFalse(fm.containsKey("suf3"));
    Assert.assertEquals("Th", fm.get("pref2"));
    Assert.assertEquals("is", fm.get("suf2"));
    fm = sortedanns.get(1).getFeatures();
    Assert.assertFalse(fm.containsKey("pref1"));
    Assert.assertFalse(fm.containsKey("pref2"));
    Assert.assertFalse(fm.containsKey("pref3"));
    Assert.assertFalse(fm.containsKey("suf1"));
    Assert.assertFalse(fm.containsKey("suf2"));
    Assert.assertFalse(fm.containsKey("suf3"));
    fm = sortedanns.get(3).getFeatures();
    Assert.assertFalse(fm.containsKey("pref1"));
    Assert.assertTrue(fm.containsKey("pref2"));
    Assert.assertTrue(fm.containsKey("pref3"));
    Assert.assertTrue(fm.containsKey("pref4"));
    Assert.assertFalse(fm.containsKey("pref5"));
    Assert.assertFalse(fm.containsKey("pref6"));
    Assert.assertFalse(fm.containsKey("suf1"));
    Assert.assertTrue(fm.containsKey("suf2"));
    Assert.assertTrue(fm.containsKey("suf3"));
    Assert.assertTrue(fm.containsKey("suf4"));
    Assert.assertFalse(fm.containsKey("suf5"));
    Assert.assertFalse(fm.containsKey("suf6"));
  }

}
