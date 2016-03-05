/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.tests;

import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.Instance;
import cc.mallet.types.LabelAlphabet;
import gate.Annotation;
import gate.Document;
import gate.creole.ResourceInstantiationException;
import gate.plugin.learningframework.features.Attribute;
import gate.plugin.learningframework.features.FeatureExtraction;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import gate.plugin.learningframework.features.SimpleAttribute;
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
public class TestPipeSerialization {
  // Test if we can serialize a pipe that has a FeatureInfo stored with it and
  // get everything back as needed
  @Test
  public void testPipeSerialization1() throws ResourceInstantiationException, IOException, ClassNotFoundException {
    String spec = "<ROOT>"+
            "<ATTRIBUTE><TYPE>theType</TYPE><FEATURE>feature1</FEATURE><DATATYPE>nominal</DATATYPE><CODEAS>number</CODEAS></ATTRIBUTE>"+
            "</ROOT>";    
    FeatureInfo fi = new FeatureSpecification(spec).getFeatureInfo();
    // Create a pipe with a data and target alphabet
    Pipe tmppipe = new Noop(new Alphabet(),new LabelAlphabet());
    List<Pipe> pipes = new ArrayList<Pipe>();
    pipes.add(tmppipe);
    LFPipe pipe = new LFPipe(pipes);
    pipe.setFeatureInfo(fi);
    
    // add an entry to the data alphabet
    pipe.getDataAlphabet().lookupIndex("feature1");
    // extract an instance - this should create/update the alphabet for the number representation of the feature
    Document doc = newDocument();
    Annotation instAnn = addAnn(doc,"",0,0,"theType",gate.Utils.featureMap("feature1","val1"));
    Instance inst = newInstance();
    Attribute attr = fi.getAttributes().get(0);
    // make sure the attribute is a SimpleAttribute as expected
    assertEquals(SimpleAttribute.class, attr.getClass());
    SimpleAttribute sa = (SimpleAttribute)attr;
    FeatureExtraction.extractFeature(inst, sa, doc.getAnnotations(), instAnn);
    // verify that we do have an alphabet in the attribute info
    assertNotNull(sa.alphabet);    
    System.err.println("DEBUG: the alphabet we have is "+sa.alphabet);
    assertTrue(sa.alphabet.contains("val1"));
    // remember that alphabet for later
    Alphabet valuealphabet = sa.alphabet;
    
    // No serialize the lfpipe
    File tmpFile = File.createTempFile("LF_test",".pipe");
    tmpFile.deleteOnExit();
    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(tmpFile));
    oos.writeObject(pipe);
    oos.close();    
    // Now read it back and check if everything is there
    ObjectInputStream ois = new ObjectInputStream (new FileInputStream(tmpFile));
    LFPipe pipe2 = (LFPipe) ois.readObject();
    ois.close();
    // check if the data and target alphabets match
    assertTrue(pipe2.alphabetsMatch(pipe));
    // Do we have a feature info?
    assertNotNull(pipe2.getFeatureInfo());
    // do we have attributes?
    assertNotNull(pipe2.getFeatureInfo().getAttributes());
    // is there exactly one attribute
    assertEquals(1, pipe2.getFeatureInfo().getAttributes().size());
    // does that attribute have an alphabet
    assertNotNull(((SimpleAttribute)pipe2.getFeatureInfo().getAttributes().get(0)).alphabet);
    // is the alphabet identical to what we originally had
    assertEquals(valuealphabet,((SimpleAttribute)pipe2.getFeatureInfo().getAttributes().get(0)).alphabet);
  }
}
