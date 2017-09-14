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
import cc.mallet.types.LabelAlphabet;
import gate.plugin.learningframework.Globals;
import gate.plugin.learningframework.data.Attributes;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.Info;
import gate.plugin.learningframework.engines.Parms;
import gate.plugin.learningframework.features.FeatureExtraction;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintStream;

/**
 * Exporter (experimental so far!) for exporting feature vectors in python-readable
 * format.
 *
 * This format is very basic JSON where each example is represented as a list
 * with either two or one element: if there are two, the second element is for
 * the targets, otherwise there are only inputs. The first element is a list that
 * represents the feature vector (independent variables).
 * The optional second element is a target. If the Mallet instance does not 
 * have a target (the target object is null), then the second element is missing
 * <p>
 * Example with targets: '[[0.1, 2.3], 1]'<br>
 * Example without targets: '[[0.1, 2.3]]'
 *
 * @author Johann Petrak
 */
public class CorpusExporterJsonTarget extends CorpusExporterJsonBase {

  @Override
  public Info getInfo() {
    Info info = new Info();

    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmClassification";
    info.algorithmName = "DUMMY";
    info.engineClass = "DUMMY";
    info.modelClass = "DUMMY";
    return info;
  }

  @Override
  public void export(File directory, CorpusRepresentationMallet cr, String instanceType, String parms) {
    InstanceList malletInstances = cr.getRepresentationMallet();
    Pipe pipe = malletInstances.getPipe();
    Attributes attrs = new Attributes(pipe, instanceType);
    // We create one file that contains something that should be readable as JSON:
    // A list of one or two elements, where the first element is a list of lists,
    // which is a list of feature vectors and the optional second element is 
    // a list of target values.
    // If the parameter 's:string:b' is true, then any nominal attribute is 
    // included as a string, not as its encoding.
    // NOTE: for this exporting, sparse encodings of nominal values should be avoided!

    Parms ps = new Parms(parms, "S:string:b", "f:filtermv:b");
    boolean asString = (boolean) ps.getValueOrElse("string", false);
    boolean filterMV = (boolean) ps.getValueOrElse("filtermv", false);
    System.err.println("DEBUG: writing nominal values as string: " + asString);
    System.err.println("DEBUGL filter instances with missing values: " + filterMV);

    PrintStream dataOut = null;
    File dataFile = null;
    try {
      String basename = Globals.dataBasename;
      dataFile = new File(directory, basename + ".py.json");
      dataOut = new PrintStream(new FileOutputStream(dataFile));
    } catch (Exception ex) {
      throw new RuntimeException("Could not open " + dataFile.getAbsolutePath(), ex);
    }

    // get the alphabets
    Alphabet dataAlphabet = malletInstances.getPipe().getDataAlphabet();
    LabelAlphabet targetAlphabet = (LabelAlphabet) malletInstances.getPipe().getTargetAlphabet();
    int nrFeatures = pipe.getDataAlphabet().size();
    for (Instance inst : malletInstances) {
      dataOut.print(instance2String(inst, targetAlphabet, attrs, nrFeatures, asString, filterMV));
      dataOut.println();
    }
    try {
      dataOut.close();
    } catch (Exception ex) {
      //
    }
  } // export

  
  public String instance2String(
          Instance inst,
          LabelAlphabet targetAlphabet,
          Attributes attrs,
          int nrFeatures,
          boolean asString,
          boolean filterMV) {
    StringBuilder sb = new StringBuilder();
    sb.append("[");  // outermost list 
    FeatureVector fv = (FeatureVector)inst.getData();
    Object targetObject = inst.getTarget();
    if (filterMV) {
      Object ignore = inst.getProperty(FeatureExtraction.PROP_IGNORE_HAS_MV);
      if (ignore != null && ignore.equals(true)) {
          return null;
      }
    }
    sb.append(featureVector2String(fv, nrFeatures, attrs, asString));
    // for now, we always try to output the target, even if it is null, this may change 
    // in the future
    if (targetObject!=null) {
      sb.append(", ");
      sb.append(target2String(targetObject, targetAlphabet, asString));
    }
    sb.append("]");  // close outer list
    return sb.toString();
  }// instance2String
  
  
}
