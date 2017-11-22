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

package gate.plugin.learningframework.mbstats;

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
