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
import cc.mallet.types.FeatureSequence;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.FeatureVectorSequence;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import gate.plugin.learningframework.Globals;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.data.Attributes;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.data.CorpusRepresentationMalletSeq;
import gate.plugin.learningframework.engines.Info;
import gate.plugin.learningframework.engines.Parms;
import gate.plugin.learningframework.features.FeatureExtractionMalletSparse;
import gate.plugin.learningframework.mallet.LFAlphabet;
import gate.plugin.learningframework.mallet.LFLabelAlphabet;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;


/**
 * Exporter (experimental so far!) for exporting sequences in python-readable
 * format.
 *
 * This format is very basic JSON where each example is represented as a list
 * with either two or one element: if there are two, the second element is for
 * the targets, otherwise there are only inputs. The first element is a list of
 * lists, each inner list represents the feature vector of a sequence element.
 * The optional second element is a list of targets. If targets are present then
 * the number of targets must be identical to the number of feature vectors.
 * <p>
 * Example with targets: '[[[0.1, 2.3], [1.1, 1.2]], [1, 2]]'<br>
 * Example without targets: '[[[0.1, 2.3], [1.1, 1.2]]]'
 *
 * @author Johann Petrak
 */
public class CorpusExporterMRJsonSeq extends CorpusExporterMRJsonBase {

  @Override
  public Info getInfo() {
    Info info = new Info();

    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmSequenceTagging";
    info.algorithmName = "DUMMY";
    info.engineClass = "DUMMY";
    info.modelClass = "DUMMY";
    return info;
  }

  @Override
  public void export() {
    exportMeta();
    CorpusRepresentationMallet crm = (CorpusRepresentationMallet)corpusRepresentation;
    InstanceList malletInstances = crm.getRepresentationMallet();
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
      dataFile = new File(dataDirFile, basename + ".py.json");
      dataOut = new PrintStream(new FileOutputStream(dataFile));
    } catch (FileNotFoundException ex) {
      throw new RuntimeException("Could not open " + dataFile.getAbsolutePath(), ex);
    }

    // get the alphabets
    LFAlphabet dataAlphabet = (LFAlphabet)malletInstances.getPipe().getDataAlphabet();
    LFLabelAlphabet targetAlphabet = (LFLabelAlphabet) malletInstances.getPipe().getTargetAlphabet();
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

  /**
   * Convert instance to string representation.
   * @param inst instance 
   * @param targetAlphabet target alphabet
   * @param attrs attributes
   * @param nrFeatures number of features
   * @param asString represent as quoted string
   * @param filterMV filter missing values
   * @return string representation
   */
  public String instance2String(
          Instance inst,
          LFLabelAlphabet targetAlphabet,
          Attributes attrs,
          int nrFeatures,
          boolean asString,
          boolean filterMV) {
    StringBuilder sb = new StringBuilder();
    // For sequences, each instance wraps a feature vector list and a feature vector
    // was created as new Instance(fvseq, fseq, null, null);
    // where FeatureVectorSequence fvseq = new FeatureVectorSequence(vectors);
    // and FeatureSequence fseq = new FeatureSequence(pipe.getTargetAlphabet(), labelidxs);

    // First unpack the feature vector sequence and fseq 
    FeatureVectorSequence fvseq = (FeatureVectorSequence) inst.getData();
    FeatureSequence fseq = (FeatureSequence) inst.getTarget();
    boolean haveTargets = (fseq != null && fseq.size() > 0);
    if (haveTargets && (fseq.size() != fvseq.size())) {
      throw new GateRuntimeException("There are targets but not the same number, in fvseq=" + fvseq.size() + ", targets=" + fseq.size());
    }
    sb.append("[");  // outermost list which encloses the list of feature vectors and the list of targets      
    boolean firstList = true;
    sb.append("["); // for the feature vector list
    for (int i = 0; i < fvseq.size(); i++) {
      FeatureVector fv = fvseq.get(i);
      Object targetObject = fseq.get(i);
      if (filterMV) {
        Object ignore = inst.getProperty(FeatureExtractionMalletSparse.PROP_IGNORE_HAS_MV);
        if (ignore != null && ignore.equals(true)) {
          continue;
        }
      }
      if (firstList) {
        firstList = false;
      } else {
        sb.append(", ");
      }
      // TODO: the python format does not allow the use of instance weights, instead the 
      // weight must become an additional feature!
      Double instanceWeight = (Double) inst.getProperty("instanceWeight");
      boolean first = true;

      sb.append(featureVector2String(fv, nrFeatures, attrs, asString));
    }
    sb.append("]"); // close the feature vector list
    // Only if there are targets, add another list with the targets
    if (haveTargets) {
      sb.append(", [");
      boolean first = true;
      for (int i = 0; i < fseq.size(); i++) {
        Object target = fseq.get(i);
        if (first) {
          first = false;
        } else {
          sb.append(", ");
        }
        sb.append(target2String(target, targetAlphabet, asString));
      } // for each target
      sb.append("]"); // close target list
    } // if haveTargets
    sb.append("]");  // close outer list
    return sb.toString();
  }// instance2String

  @Override
  public void initWhenCreating() {
    corpusRepresentation = new CorpusRepresentationMalletSeq(featureInfo, ScalingMethod.NONE);
  }
  
}
