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
import java.util.Arrays;
import java.util.List;

/**
 *
 *
 */
public class PipeScaleMinMaxAll extends Pipe implements Serializable {

  protected double min[];
  protected double max[];
  protected boolean normalize[];
  
  /**
   * Constructor from alphabet and feature stats.
   * @param alphabet alphabet
   * @param stats feature stats
   */
  public PipeScaleMinMaxAll(Alphabet alphabet, FVStatsMeanVarAll stats) {
    super(alphabet, null);
    List<PerFeatureStats> pfss = stats.getStats();
    int n = pfss.size();
    min = new double[n];
    max = new double[n];
    normalize = new boolean[n];
    for(int i=0; i<n; i++) {
      PerFeatureStats pfs = pfss.get(i);
      // we do not normalize binary features and we do not normalize features with no
      // values at all
      if(pfs.binary != null && pfs.binary != true) {
        min[i] = pfs.min;
        max[i] = pfs.max;
      } else {
        normalize[i] = false;
      }
    }
    System.err.println("DEBUG: Creating PipeScaleMinMaxAll instance with mins="+Arrays.toString(min)+",maxs="+Arrays.toString(max));
  }

  @Override
  public Instance pipe(Instance carrier) {
    if (!(carrier.getData() instanceof FeatureVector)) {
      System.out.println(carrier.getData().getClass());
      throw new IllegalArgumentException("Data must be of type FeatureVector not " + carrier.getData().getClass() + " we got " + carrier.getData());
    }

    if (min.length != getDataAlphabet().size()
            || max.length != getDataAlphabet().size()) {
      throw new GateRuntimeException("Size mismatch, alphabet="+getDataAlphabet().size()+", stats="+min.length);
    }

    FeatureVector fv = (FeatureVector) carrier.getData();
    int[] indices = fv.getIndices();
    double[] values = fv.getValues();
    for (int i = 0; i < indices.length; i++) {
      int index = indices[i];
      double mi = min[index];
      double ma = max[index];
      double span = ma - mi;
      if(normalize[index] && span > 0.0) {
        double value = values[i];
        // NOTE: this could in theory cause an overflow error but we ignore this here!
        double newvalue = (value - mi) / span;
        fv.setValue(index, newvalue);
      }
    }
    return carrier;
  }

  private static final long serialVersionUID = 1;
  private static final int CURRENT_SERIAL_VERSION = 0;

}
