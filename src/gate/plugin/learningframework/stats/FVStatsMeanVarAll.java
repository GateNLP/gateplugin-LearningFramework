/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.stats;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.InstanceList;
import java.util.ArrayList;
import java.util.List;

/**
 * Stats object that calculates the means and variances of all features in the 
 * vectors.
 * 
 * @author Johann Petrak
 */
public class FVStatsMeanVarAll implements FeatureVectorStats {

  public FVStatsMeanVarAll(InstanceList instances) {
    
  }
  
  List<PerFeature> pfs = new ArrayList<PerFeature>();
  int nrInstances = 0;
  boolean immutable = false;
  
  @Override
  public void addFeatureVector(FeatureVector fv) {
    if(immutable) {
      throw new RuntimeException("Stats object is immutable cannot add feature Vector");
    }
    nrInstances++;
    int[] indices = fv.getIndices();
    double[] values = fv.getValues();
    for (int j = 0; j < indices.length; j++) {
      int index = indices[j];
      double value = values[j];
      // If the value is a NaN, we disregard it: however, if this feature 
      // ends up having some non-NaN features, the mean and variance should be
      // unaffected because we still count this feature vector (and not adding
      // anything to sum and sum squares is the same as adding 0).
      // However, the calculation of min or max may be wrong in some cases.
      if(value == Double.NaN) {
        continue;
      }
      // get the PerFeature values for this feature or create it.
      // also, if needed, extend the array of PerFeature objects
      
      // to accommodate index i we need size i+1
      if(pfs.size()<(index+1)) {
        for(int i=pfs.size(); i<=index; i++) {
          pfs.add(null);
        }
      }
      PerFeature pf = pfs.get(index);
      if(pf==null) pf = new PerFeature();
      
      // now do the actual stats collection: if the value in pf is still NaN
      // set it, otherwise recalculate it. However, we do not check all the 
      // values in pf for NaN because we can infer e.g. that sumofsquares is
      // NaN if sum is NaN.
      if(pf.sum==Double.NaN) {
        pf.sum = value;
        pf.sumOfSquares = value*value;
        pf.min = value;
        pf.max = value;
        if(value == 0.0 || value == 1.0) pf.binary = true; else pf.binary = false;
      } else {
        pf.sum += value;
        pf.sumOfSquares += value*value;
        if(pf.binary == true && value != 0.0 && value != 1.0) pf.binary = false;
      }
      // we re-calculate mean and variance immediately
      pf.mean = pf.sum / nrInstances;
      // TODO: population or sample variance?
      pf.var = pf.sumOfSquares / nrInstances;
    } // for indices

    /*
    //We make a new pipe and apply it to all the instances
    FeatureVector2NormalizedFeatureVector normalizer
            = new FeatureVector2NormalizedFeatureVector(means, variances, instances.getDataAlphabet());
    
    // Run all the instances through this pipe
    for(Instance inst : instances) {
      inst = normalizer.pipe(inst);
    }

    //Add the pipe to the pipes so application time data will go through it
    ArrayList<Pipe> pipeList = pipe.pipes();
    pipeList.add(normalizer);
    System.out.println("DEBUG normalize: added normalizer pipe " + normalizer);
    System.out.println("DEBUG pipes after normalization: " + pipe);
    */
  }

  @Override
  public void finish() {
    // Nothing to do really since we calculate everything on the fly
    immutable = true;
  }
  
  private static class PerFeature {
    double sum = Double.NaN;
    double sumOfSquares = Double.NaN;
    double mean = Double.NaN;
    double var = Double.NaN;
    double min = Double.NaN;
    double max = Double.NaN;
    Boolean binary = null;
    public String toString() {
      StringBuilder sb = new StringBuilder();
      sb.append("PerFeature{");
      sb.append("mean="); sb.append(mean);
      sb.append("var="); sb.append(var);
      sb.append("min="); sb.append(min);
      sb.append("max="); sb.append(max);
      sb.append("bin="); sb.append(binary);
      sb.append("}");
      return sb.toString();
    }
  }
  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("FVStatsMeanVarAll{");
    sb.append("n="); sb.append(nrInstances);
    int i = 0;
    for(PerFeature pf : pfs) {
      if(i!=0) sb.append(",");
      sb.append(i++);
      sb.append("=");
      sb.append(pf.toString());
    }
    sb.append("}");
    return sb.toString();
  }
  
}
