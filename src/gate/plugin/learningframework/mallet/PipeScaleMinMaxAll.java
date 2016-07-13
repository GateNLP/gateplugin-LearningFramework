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
public class PipeScaleMinMaxAll extends Pipe implements Serializable {

  double min[];
  double max[];
  boolean normalize[];
  
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
