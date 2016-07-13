/*
 * Copyright (c) 1995-2015, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * Copyright 2015 South London and Maudsley NHS Trust and King's College London
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 *
 */
package gate.plugin.learningframework.mallet;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import gate.plugin.learningframework.stats.FVStatsMeanVarAll;
import gate.plugin.learningframework.stats.PerFeatureStats;
import gate.util.GateRuntimeException;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 *
 *
 */
public class PipeScaleMeanVarAll extends Pipe implements Serializable {

  double means[];
  double variances[];
  boolean normalize[];
  
  public PipeScaleMeanVarAll(Alphabet alphabet, FVStatsMeanVarAll stats) {
    super(alphabet, null);
    List<PerFeatureStats> pfss = stats.getStats();
    int n = pfss.size();
    means = new double[n];
    variances = new double[n];
    normalize = new boolean[n];
    for(int i=0; i<n; i++) {
      PerFeatureStats pfs = pfss.get(i);
      // we do not normalize binary features and we do not normalize features with no
      // values at all
      if(pfs.binary != null && pfs.binary != true) {
        means[i] = pfs.mean;
        variances[i] = pfs.var;
        normalize[i] = true;
      } else {
        means[i] = Double.NaN;
        variances[i] = Double.NaN;
        normalize[i] = false;
      }
    }
    //System.err.println("DEBUG: Creating PipeScaleMeanVarAll instance with means="+Arrays.toString(means)+
    //        ",variances="+Arrays.toString(variances)+",flags="+Arrays.toString(normalize));
  }

  public Instance pipe(Instance carrier) {
    if (!(carrier.getData() instanceof FeatureVector)) {
      System.out.println(carrier.getData().getClass());
      throw new IllegalArgumentException("Data must be of type FeatureVector not " + carrier.getData().getClass() + " we got " + carrier.getData());
    }

    if (this.means.length != this.getDataAlphabet().size()
            || this.variances.length != this.getDataAlphabet().size()) {
      throw new GateRuntimeException("Size mismatch, alphabet="+getDataAlphabet().size()+", stats="+means.length);    }

    FeatureVector fv = (FeatureVector) carrier.getData();
    int[] indices = fv.getIndices();
    double[] values = fv.getValues();
    for (int i = 0; i < indices.length; i++) {
      int index = indices[i];
      if(normalize[index]) {
        double value = values[i];
        double mean = means[index];
        double variance = variances[index];
        double newvalue = (value - mean) / Math.sqrt(variance);
        fv.setValue(index, newvalue);
      }
    }
    return carrier;
  }

  private static final long serialVersionUID = 2;
  private static final int CURRENT_SERIAL_VERSION = 0;

}
