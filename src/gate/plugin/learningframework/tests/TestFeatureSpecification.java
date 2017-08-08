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

import gate.plugin.learningframework.features.FeatureSpecAttribute;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.FeatureSpecification;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;


/**
 * Tests for the FeatureSpecification parsing and creation of FeatureInfo.
 * 
 * @author Johann Petrak
 */
public class TestFeatureSpecification {
  
  @Test
  public void basicSpecParsing1() {
    String spec = "<ROOT>"+
            "<ATTRIBUTE><TYPE>theType</TYPE></ATTRIBUTE>"+
            "</ROOT>";
    FeatureSpecification fs;    
    FeatureInfo fi;
    List<FeatureSpecAttribute> as;
    fs = new FeatureSpecification(spec);
    fi = fs.getFeatureInfo();
    as = fi.getAttributes();
    assertNotNull(as);
    assertEquals(1,as.size());
    assertEquals("SimpleAttribute(name=,type=theType,feature=,datatype=bool,missingvaluetreatment=zero_value,within=null,codeas=number",as.get(0).toString());
        
    spec = "<ROOT>"+
            "<ATTRIBUTELIST><TYPE>theType</TYPE><FEATURE>string</FEATURE><DATATYPE>nominal</DATATYPE><FROM>-2</FROM><TO>1</TO></ATTRIBUTELIST>"+
            "</ROOT>";    
    fs = new FeatureSpecification(spec);
    fi = fs.getFeatureInfo();
    as = fi.getAttributes();
    assertNotNull(as);
    assertEquals(1,as.size());
    assertEquals("AttributeList(name=,type=theType,feature=string,datatype=nominal,missingvaluetreatment=keep,codeas=one_of_k,within=null,from=-2,to=1",as.get(0).toString());

    spec = "<ROOT>"+
            "<NGRAM><TYPE>theType</TYPE><FEATURE>theFeature</FEATURE><NUMBER>3</NUMBER></NGRAM>"+
            "</ROOT>";    
    fs = new FeatureSpecification(spec);
    fi = fs.getFeatureInfo();
    as = fi.getAttributes();
    assertNotNull(as);
    assertEquals(1,as.size());
    assertEquals("NgramAttribute(name=,type=theType,feature=theFeature,featureName4Value=,number=3",as.get(0).toString());
    
    // make sure that the feature info object we get from the feature specification is a clone
    FeatureInfo fi2 = fs.getFeatureInfo();
    assertFalse(fi == fi2);
    
  }  
 
}
