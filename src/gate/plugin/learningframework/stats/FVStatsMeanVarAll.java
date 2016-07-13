/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.stats;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
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
    for(Instance instance : instances) {
      FeatureVector fv = (FeatureVector)instance.getData();
      addFeatureVector(fv);
    }
    finish();
  }
  
  List<PerFeatureStats> pfs = new ArrayList<PerFeatureStats>();
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
      if(Double.isNaN(value)) {
        continue;
      }
      // get the PerFeatureStats values for this feature or create it.
      // also, if needed, extend the array of PerFeatureStats objects
      
      // to accommodate index i we need size i+1
      if(pfs.size()<(index+1)) {
        for(int i=pfs.size(); i<=index; i++) {
          pfs.add(null);
        }
      }
      PerFeatureStats pf = pfs.get(index);
      if(pf==null) {        
        pf = new PerFeatureStats();
        pfs.set(index, pf);
      }
      
      // now do the actual stats collection: if the value in pf is still NaN
      // set it, otherwise recalculate it. However, we do not check all the 
      // values in pf for NaN because we can infer e.g. that sumofsquares is
      // NaN if sum is NaN.
      if(Double.isNaN(pf.sum)) {
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

  }

  @Override
  public void finish() {
    // Nothing to do really since we calculate everything on the fly
    immutable = true;
  }
  
  public List<PerFeatureStats> getStats() {
    return pfs;
  }

  
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("FVStatsMeanVarAll{");
    sb.append("n="); sb.append(nrInstances);
    sb.append(",");
    int i = 0;
    for(PerFeatureStats pf : pfs) {
      if(i!=0) sb.append(",");
      sb.append(i++);
      sb.append("=");
      if(pf==null) {
        sb.append("(null)");
      } else {
        sb.append(pf.toString());
      }
    }
    sb.append("}");
    return sb.toString();
  }
  
}
