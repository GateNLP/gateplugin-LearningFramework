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

import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.plugin.learningframework.features.FeatureSpecAttribute;
import gate.plugin.learningframework.features.FeatureExtractionMalletSparse;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.FeatureSpecSimpleAttribute;
import gate.plugin.learningframework.mallet.LFAlphabet;
import static gate.plugin.learningframework.tests.Utils.*;
import gate.util.GateException;
import java.util.HashSet;
import java.util.List;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import gate.test.GATEPluginTests;
import gate.Utils;
import gate.plugin.learningframework.ModelApplication;
import gate.plugin.learningframework.features.SeqEncoder;
import gate.plugin.learningframework.features.SeqEncoder_SimpleBIO;
import java.util.Collections;


/**
 * Tests for the FeatureSpecification parsing and creation of FeatureInfo.
 * 
 * @author Johann Petrak
 */
public class TestModelApplication extends GATEPluginTests {
    
  
  @Test
  /**
   * Test some ModelApplication corner cases.
   */
  public void modelApplication1() throws ResourceInstantiationException {
    // Create a document that has two sequence annotations
    // then create the temporary set with the instance annotations and the 
    // necessary features
    Document doc = Factory.newDocument(String.join("", Collections.nCopies(20, "word ")));
    
    AnnotationSet defSet = doc.getAnnotations();
    AnnotationSet instSet = doc.getAnnotations("LF_SEQ_TMP");
    AnnotationSet outSet = doc.getAnnotations("LearningFramework");
    
    // 4 words are Sentence 1
    int seq1 = Utils.addAnn(defSet, 0, 20, "Sentence", Utils.featureMap());
    // another 4 words from the end are Sentence 2
    int seq2 = Utils.addAnn(defSet, 80, 100, "Sentence", Utils.featureMap());
    
    // create the instance annotations
    int inst1 = Utils.addAnn(instSet, 0, 5, "Token", 
            Utils.featureMap("LF_seq_span_id", seq1, "LF_target", "O", "LF_confidence", 0.9));
    int inst2 = Utils.addAnn(instSet, 5, 10, "Token", 
            Utils.featureMap("LF_seq_span_id", seq1, "LF_target", "SEQ|B", "LF_confidence", 0.9));
    int inst3 = Utils.addAnn(instSet, 10, 15, "Token", 
            Utils.featureMap("LF_seq_span_id", seq1, "LF_target", "SEQ|I", "LF_confidence", 0.9));
    int inst4 = Utils.addAnn(instSet, 15, 20, "Token", 
            Utils.featureMap("LF_seq_span_id", seq1, "LF_target", "O", "LF_confidence", 0.9));
    
    
    int inst5 = Utils.addAnn(instSet, 85, 90, "Token", 
            Utils.featureMap("LF_seq_span_id", seq2, "LF_target", "SEQ|B", "LF_confidence", 0.9));
    int inst6 = Utils.addAnn(instSet, 90, 95, "Token", 
            Utils.featureMap("LF_seq_span_id", seq2, "LF_target", "SEQ|B", "LF_confidence", 0.9));
    int inst7 = Utils.addAnn(instSet, 95, 100, "Token", 
            Utils.featureMap("LF_seq_span_id", seq2, "LF_target", "SEQ|I", "LF_confidence", 0.9));
    
    
    
    SeqEncoder seqEncoder = new SeqEncoder_SimpleBIO();
    ModelApplication.addSurroundingAnnotations(instSet, outSet, 0.5); 
    
    System.err.println("!!!!!! OUTPUT SET="+outSet);
    List<Annotation> outseqs = outSet.inDocumentOrder();
    assertEquals(3, outSet.size());
    Annotation seqAnn1 = outseqs.get(0);
    assertEquals((Long)5L, seqAnn1.getStartNode().getOffset());
    assertEquals((Long)15L, seqAnn1.getEndNode().getOffset());
    Annotation seqAnn2 = outseqs.get(1);
    assertEquals((Long)85L, seqAnn2.getStartNode().getOffset());
    assertEquals((Long)90L, seqAnn2.getEndNode().getOffset());
    Annotation seqAnn3 = outseqs.get(2);
    assertEquals((Long)90L, seqAnn3.getStartNode().getOffset());
    assertEquals((Long)100L, seqAnn3.getEndNode().getOffset());
    
  }  

  
 
}
