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

import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import gate.Annotation;
import gate.Document;
import gate.test.GATEPluginTests;
import gate.creole.ResourceInstantiationException;
import gate.plugin.learningframework.features.FeatureSpecAttribute;
import gate.plugin.learningframework.features.FeatureExtractionMalletSparse;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.FeatureSpecSimpleAttribute;
import gate.plugin.learningframework.mallet.LFAlphabet;
import gate.plugin.learningframework.mallet.LFPipe;
import org.junit.Test;
import static gate.plugin.learningframework.tests.Utils.newDocument;
import static gate.plugin.learningframework.tests.Utils.newInstance;
import static gate.plugin.learningframework.tests.Utils.addAnn;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;
import static org.junit.Assert.*;
/**
 *
 * @author Johann Petrak
 */
public class TestPipeSerialization extends GATEPluginTests {
  // Test if we can serialize a pipe that has a FeatureInfo stored with it and
  // get everything back as needed
  @Test
  public void testPipeSerialization1() throws ResourceInstantiationException, IOException, ClassNotFoundException {
    String spec = "<ROOT>"+
            "<ATTRIBUTE><TYPE>theType</TYPE><FEATURE>feature1</FEATURE><DATATYPE>nominal</DATATYPE><CODEAS>number</CODEAS></ATTRIBUTE>"+
            "</ROOT>";    
    FeatureInfo fi = new FeatureSpecification(spec).getFeatureInfo();
    // Create a pipe with a data and target alphabet
    Pipe tmppipe = new Noop(new LFAlphabet(),new LabelAlphabet());
    List<Pipe> pipes = new ArrayList<>();
    pipes.add(tmppipe);
    LFPipe pipe = new LFPipe(pipes);
    pipe.setFeatureInfo(fi);
    
    // add an entry to the data alphabet
    pipe.getDataAlphabet().lookupIndex("feature1");
    // extract an instance - this should create/update the alphabet for the number representation of the feature
    Document doc = newDocument();
    Annotation instAnn = addAnn(doc,"",0,0,"theType",gate.Utils.featureMap("feature1","val1"));
    Instance inst = newInstance();
    FeatureSpecAttribute attr = fi.getAttributes().get(0);
    // make sure the attribute is a SimpleAttribute as expected
    assertEquals(FeatureSpecSimpleAttribute.class, attr.getClass());
    FeatureSpecSimpleAttribute sa = (FeatureSpecSimpleAttribute)attr;
    FeatureExtractionMalletSparse.extractFeature(inst, sa, doc.getAnnotations(), instAnn);
    // verify that we do have an alphabet in the attribute info
    assertNotNull(sa.alphabet);    
    System.err.println("DEBUG: the alphabet we have is "+sa.alphabet);
    assertTrue(sa.alphabet.contains("val1"));
    // remember that alphabet for later
    Alphabet valuealphabet = sa.alphabet;
    
    // No serialize the lfpipe
    File tmpFile = File.createTempFile("LF_test",".pipe");
    tmpFile.deleteOnExit();
    try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmpFile))) {
      oos.writeObject(pipe);
    }    
    LFPipe pipe2;
    try ( // Now read it back and check if everything is there
            ObjectInputStream ois = new ObjectInputStream (new FileInputStream(tmpFile))) {
      pipe2 = (LFPipe) ois.readObject();
    }
    // check if the data and target alphabets match
    assertTrue(pipe2.alphabetsMatch(pipe));
    // Do we have a feature info?
    assertNotNull(pipe2.getFeatureInfo());
    // do we have attributes?
    assertNotNull(pipe2.getFeatureInfo().getAttributes());
    // is there exactly one attribute
    assertEquals(1, pipe2.getFeatureInfo().getAttributes().size());
    // does that attribute have an alphabet
    assertNotNull(((FeatureSpecSimpleAttribute)pipe2.getFeatureInfo().getAttributes().get(0)).alphabet);
    // is the alphabet identical to what we originally had
    assertEquals(valuealphabet,((FeatureSpecSimpleAttribute)pipe2.getFeatureInfo().getAttributes().get(0)).alphabet);
  }
}
