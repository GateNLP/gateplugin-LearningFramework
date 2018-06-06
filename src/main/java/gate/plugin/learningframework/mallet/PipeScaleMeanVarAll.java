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

package gate.plugin.learningframework.mallet;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import gate.plugin.learningframework.mbstats.FVStatsMeanVarAll;
import gate.plugin.learningframework.mbstats.PerFeatureStats;
import gate.util.GateRuntimeException;
import java.io.Serializable;
import java.util.List;

/**
 *
 *
 */
public class PipeScaleMeanVarAll extends Pipe implements Serializable {

  protected double means[];
  protected double variances[];
  protected boolean normalize[];
  
  /**
   * Constructor from alphabet and stats.
   * @param alphabet alphabet
   * @param stats feature stats
   */
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

  @Override
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
