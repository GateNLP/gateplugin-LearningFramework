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

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import gate.plugin.learningframework.data.CorpusRepresentationMallet;
import gate.plugin.learningframework.engines.Info;
import gate.plugin.learningframework.features.FeatureExtractionMalletSparse;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.PrintStream;
import java.text.DecimalFormat;

/**
 *
 * @author johann
 */
public class CorpusExporterLibSVM extends CorpusExporterMalletRelated {

  @Override
  public Info getInfo() {
    Info info = new Info();
    // TODO: we should check for regression vs classification HER£E
    info.algorithmClass = "gate.plugin.learningframework.engines.AlgorithmClassification";
    info.algorithmName = "LibSVM_CL_DUMMY";
    info.engineClass = "DUMMY";
    info.modelClass =  "DUMMY";    
    return info;
  }

  @Override
  public void export() {
    exportMeta();
    CorpusRepresentationMallet crm = (CorpusRepresentationMallet)corpusRepresentation;
    PrintStream out = null;
    File outFile = new File(dataDirFile, "data.libsvm");
    try {
      out = new PrintStream(outFile);
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not open output file "+outFile,ex);
    }
    // For libSVM format, the all features and the target are always represented 
    // as numeric values and NaN is not allowed. 
    // If an instance has been marked to be skipped because of a MV we do that,
    // otherwise if a MV is encountered we replace it with 0 without warning
    
    // IMPORTANT! according to LibSVM README the libsvm sparse vector representation
    // in the external file must start with location 1 and have increasing locations.
    // However, in the code for read_problem() this does not seem to be the case and
    // precomputed kernels are only used if a parameter is specified.
    // However the examples all use 1 as the first index, so we do this here too
    
    
    InstanceList instances = crm.getRepresentationMallet();
    DecimalFormat DFf = new DecimalFormat("#.##########");
    for(Instance instance : instances) {
      Boolean ignoreInstance = (Boolean)instance.getProperty(FeatureExtractionMalletSparse.PROP_IGNORE_HAS_MV);
      if(ignoreInstance) continue;
      Boolean haveMV = (Boolean)instance.getProperty(FeatureExtractionMalletSparse.PROP_HAVE_MV);      
      Object targetObj = instance.getTarget();
      double target = 0.0;
      if(targetObj == null) {
        // TODO: NOTE: we export instances with missing targets in the ARFF exporter, so 
        // we do it here too. However, other than there we always have to output a target
        // because the format requires it. We therefore always output target 0.0 
        // which is the default value, so nothing to do        
      } else if (targetObj instanceof Double) {
        target = (Double)targetObj;
      } else if(targetObj instanceof Label) {
        Label label = (Label)targetObj;
        target = label.getIndex();
      }
      out.print(DFf.format(target));
      out.print(" ");
      // Now print all the feature values, but no MVs which will be ignored (as this is a sparse 
      // representation and we want to replace them with zero here).
      // NOTE: if the values were transformed, zero may not be the correct value to use,
      // this problem is ignored for now!!!
      Object fvObj = instance.getData();
      if(fvObj instanceof FeatureVector) {
        FeatureVector fv = (FeatureVector)fvObj;
        //for(int idx : fv.getIndices()) {
        for(int loc=0; loc<fv.numLocations(); loc++) {
          int idx = fv.indexAtLocation(loc);
          double val = fv.valueAtLocation(loc);
          if(!Double.isNaN(val)) {
            out.print(idx+1); out.print(":");
            out.print(DFf.format(val));
          }
        } // for
        // finally end with NL
        out.println();
      } else {
        throw new GateRuntimeException("Instance is not a FeatureVector but "+fvObj.getClass());
      }
    }
    /*
    // For now we do this by converting the mallet representation to a libsvm 
    // representation first and then exporting from there using the old code we 
    // had. 
    // TODO: do this directly from the Mallet representation!!
    CorpusRepresentationLibSVM crl = new CorpusRepresentationLibSVM(crm);    
    if (parms != null && !parms.isEmpty()) {
      throw new GateRuntimeException("No parameters supported, must be null, not: \""+parms+"\"");
    }
    svm_problem prob = crl.getRepresentationLibSVM();
    PrintStream out = null;
    File outFile = new File(directory, "data.libsvm");
    try {
      out = new PrintStream(outFile);
      for (int i = 0; i < prob.l; i++) {
        out.print(prob.y[i]);
        for (int j = 0; j < prob.x[i].length; j++) {
          out.print(" ");
          out.print(prob.x[i][j].index);
          out.print(":");
          out.print(prob.x[i][j].value);
        }
        out.println();
      }
      out.close();
    } catch (FileNotFoundException ex) {
      System.err.println("Could not write training instances to svm format file");
      ex.printStackTrace(System.out);
    }

  */  
  }
  
}
