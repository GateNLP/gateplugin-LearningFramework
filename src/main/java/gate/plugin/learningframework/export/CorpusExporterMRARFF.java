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

package gate.plugin.learningframework.export;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import gate.plugin.learningframework.Globals;
import gate.plugin.learningframework.data.Attribute;
import gate.plugin.learningframework.data.Attributes;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.Info;
import gate.plugin.learningframework.features.CodeAs;
import gate.plugin.learningframework.features.Datatype;
import gate.plugin.learningframework.features.FeatureExtractionMalletSparse;
import gate.plugin.learningframework.features.MissingValueTreatment;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 *
 * @author johann
 */
public class CorpusExporterMRARFF extends CorpusExporterMR {

  @Override
  public Info getInfo() {
    Info info = new Info();
    
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmClassification";
    info.algorithmName = "DUMMY";
    info.engineClass = "gate.plugin.learningframework.engines.EngineWekaExternal";
    info.modelClass =  "DUMMY";    
    return info;
  }

  @Override
  public void export() {    
    exportMeta();
    CorpusRepresentationMallet crm = (CorpusRepresentationMallet)corpusRepresentation;
    InstanceList malletInstances = crm.getRepresentationMallet();
    Pipe pipe = malletInstances.getPipe();
    Attributes attrs = new Attributes(pipe,instanceType);
    // We create two files: one with just the header and no instances and
    // one with everything
    File headerOnlyFile = new File(dataDirFile,Globals.headerBasename+".arff");
    File dataFile = new File(dataDirFile,Globals.dataBasename+".arff");
    PrintStream headerOut = null;
    try {
      headerOut = new PrintStream(new FileOutputStream(headerOnlyFile));
    } catch (FileNotFoundException ex) {
      throw new RuntimeException("Could not open "+headerOnlyFile.getAbsolutePath(),ex);
    }
    PrintStream dataOut = null;
    try {
      dataOut = new PrintStream(new FileOutputStream(dataFile));
    } catch (FileNotFoundException ex) {
      throw new RuntimeException("Could not open "+dataFile.getAbsolutePath(),ex);
    }
    headerOut.println("@RELATION GateLearningFramework");
    dataOut.println("@RELATION GateLearningFramework");
    for(Attribute attr : attrs) {
      headerOut.print("@ATTRIBUTE ");
      dataOut.print("@ATTRIBUTE ");
      // get the name, if necessary, escape it properly
      String name = escape4Arff(attr.name);
      headerOut.print(name);
      dataOut.print(name);      
      headerOut.print(" ");
      dataOut.print(" ");
      if(attr.datatype == Datatype.numeric) {
        headerOut.print("NUMERIC");
        dataOut.print("NUMERIC");
      } else if(attr.datatype == Datatype.nominal && attr.codeAs == CodeAs.number ||
                attr.datatype == Datatype.bool) {
        if(attr.alphabet == null) {
          throw new RuntimeException("Attribute is not numeric but no alphabet: "+attr);
        }
        String vals = alphabet2Arff(attr.alphabet,attr.mvTreatment);
        headerOut.print(vals);
        dataOut.print(vals);
      } else {
        // fall back is NUMERIC
        headerOut.print("NUMERIC");
        dataOut.print("NUMERIC");        
      }
      headerOut.println();
      dataOut.println();
    } // for attr : attrs
    // Now one more line for the target
    Attribute target = attrs.getTargetAttribute();
    headerOut.print("@ATTRIBUTE ");
    dataOut.print("@ATTRIBUTE ");
    // get the name, if necessary, escape it properly
    String name = escape4Arff(target.name);
    headerOut.print(name);
    dataOut.print(name);      
    headerOut.print(" ");
    dataOut.print(" ");
    if(target.datatype == Datatype.numeric) {
      headerOut.print("NUMERIC");
      dataOut.print("NUMERIC");
    } else {
      if(target.alphabet == null) {
        throw new RuntimeException("target is not numeric but no alphabet: "+target);
      }
      String vals = alphabet2Arff(target.alphabet,null);
      headerOut.print(vals);
      dataOut.print(vals);
    }
    headerOut.println();
    dataOut.println();
    headerOut.println("@DATA");
    dataOut.println("@DATA");
    try {
      headerOut.close();
    } catch(Exception ex) {
      //
    }
    // export the actual data in sparse format
    // TODO: make sure we respect the flag to ignore an instance with missing values
    // TODO: if the instance has a weight, also output the weight!!
    for(Instance inst : malletInstances) {
      String line = instance2WekaArffLine(inst,attrs);
      dataOut.println(line);
    }
    try {
      dataOut.close();
    } catch(Exception ex) {
      //
    }
  } // export

  /**
   * Escape characters as needed for the ARFF format.
   * If the string contains a quote character, a percent character or 
   * any whitespace those characters are escaped with a backslash.
   * Also, a backslash is escaped with a backslash.
   * If any character needed to be escaped, the whole string is quoted. 
   * The string is also quoted if it contains curly braces.
   * 
   * @param what the string to escape
   * @return  escaped string
   */
  public static String escape4Arff(String what) {
  	if(what == null) {
          what = "";
          }
  	if(what.trim().isEmpty()) {
  		return "'" + what + "'";
  	}
    int len = what.length();
    what = what.replaceAll("([\"'%\\n\\r \\t\\\\])", "\\\\$1");
    if(what.length()!=len || what.contains("{") || what.contains("}") || what.contains(",")) {
      what = "'" + what + "'";
    }
    return what;
  }
  
  /**
   * Convert alphabet to ARFF declaration string.
   * @param alph Mallet alphabet
   * @param mvt missing value treatment setting
   * @return ARFF declaration
   */
  public String alphabet2Arff(Alphabet alph, MissingValueTreatment mvt) {
    // NOTE: mvt can be null, if this is used for a target!!
    StringBuilder sb = new StringBuilder();
    sb.append("{");
    for(int i=0; i<alph.size(); i++) {
      if(i>0) sb.append(",");
      String val = alph.lookupObject(i).toString();
      sb.append(escape4Arff(val));
    }
    // TODO: we may need to add the definition for the missing value here,
    // but by default, we do not do that.
    sb.append("}");
    return sb.toString();
  }
  
  /**
   * Convert an instance to the ARFF representation.
   * 
   * This does not include a final new-line character!
   * NOTE: this returns null if the instance is flagged that it should
   * be ignored because it contains a missing value. 
   * 
   * @param inst instance
   * @param attrs attributes
   * @return  arff data line
   */
  public String instance2WekaArffLine(Instance inst, Attributes attrs) {
    return instance2WekaArffLine(inst,attrs,true);
  }
  
  private String instance2WekaArffLine(Instance inst, Attributes attrs, boolean filterMVs) {
    StringBuilder sb = new StringBuilder();
    
    if(filterMVs) {
      Object ignore = inst.getProperty(FeatureExtractionMalletSparse.PROP_IGNORE_HAS_MV);    
      // If the flag says the instance should get ignored, return null
      // to indicate to the caller that this is an ignored instance.
      if(ignore != null && ignore.equals(true)) {
        return null;
      }
    }
    Double instanceWeight = (Double)inst.getProperty("instanceWeight");
    Object data = inst.getData();
    if(data instanceof FeatureVector) {
      FeatureVector vector = (FeatureVector)data;
      sb.append("{");
      boolean first = true;
      // TODO: maybe it is easier to do 
      // for(int idx : vector.getIndices) 
      for(int i=0; i<vector.numLocations(); i++) {   
        int idx = vector.indexAtLocation(i);
        if(first) { 
          first = false;
        } else {
          sb.append(", ");
        } 
        sb.append(idx);
        sb.append(" ");
        double value = vector.valueAtLocation(i);
        if(Double.isNaN(value)) {
          sb.append("?");
        } else {
          // TODO: proper handling of missing values!!!
          // Also: codeas may be null sometimes, make sure if we have a datatype
          // where codeas is relevant, we ALWAYS have codeas set to the correct value!
          Attribute attr = attrs.getAttribute(idx);
          if(attr.datatype==Datatype.numeric || (attr.datatype==Datatype.nominal && attr.codeAs!=CodeAs.number)) {
            sb.append(value);
          } else if(attr.datatype==Datatype.bool) {
            // TODO: check for missing value, also use the special alphabet we created?
            if(value<0.5) { sb.append("false"); } else { sb.append("true"); }
          } else if(attr.datatype==Datatype.nominal) {
            // TODO: check for how to exactly handling missing values, for now we simply output
            // the Weka missing value placeholder
            if(((int)value)==-1) {
              sb.append("?");
            } else {
              sb.append(escape4Arff((String)attr.alphabet.lookupObject((int) value)));
            }
          } else {
            // guard for forgetting about here when we add datatypes later
            sb.append("GOTCHA!!!! DATATYPE NOT SUPPORTED IN THE EXPORT CODE");
          }                  
        }
      } // for 
      // Now also add location and value for the target, if we have one
      Object target = inst.getTarget();
      if(target!=null) {
        Attribute targetAttr = attrs.getTargetAttribute();
        sb.append(", ");        
        sb.append(targetAttr.index);
        sb.append(" ");
        // we expect this to be either a Label instance or something that can be cast to double
        if(target instanceof Label) {
          if(targetAttr.datatype != Datatype.nominal) {
            throw new RuntimeException("Target is a label but datatype for attribute is not nominal");
          }
          Label malletLabel = (Label)target;
          String targetString = malletLabel.toString();
          sb.append(escape4Arff(targetString));
          // TODO: could check here if the label index is the same as expected from
          // the attribute defintion!
        } else {
          if(targetAttr.datatype != Datatype.numeric) {
            throw new RuntimeException("Target is a number but datatype for attribute is not  numeric");
          }
          double targetValue = (double)target;
          sb.append(targetValue);
        }
      } else {
        // target is null: do nothing, simply create the row without a target 
        // TODO: not sure what I was thinking here, but admittedly, exporting without
        // a target or a missing target could have its uses, so we leave this as it is
      }
      sb.append("}");
      if(instanceWeight!=null) {
        sb.append(", {");
        sb.append(instanceWeight);
        sb.append("}");
      }
    } else {
      throw new RuntimeException("Cannot export, instance is not a feature vector but "+data.getClass());
    }
    return sb.toString();
  } 

  
}
