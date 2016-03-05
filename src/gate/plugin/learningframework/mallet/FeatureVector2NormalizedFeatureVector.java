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
import gate.util.GateRuntimeException;
import java.io.Serializable;
import java.util.Arrays;

/**
 *
 *
 */
public class FeatureVector2NormalizedFeatureVector extends Pipe implements Serializable {

  double[] means;
  double[] variances;

  public FeatureVector2NormalizedFeatureVector(double[] means, double[] variances,
          Alphabet alphabet) {
    super(alphabet, null);
    this.means = means;
    this.variances = variances;
    System.err.println("Creating FV2NF instance with means="+Arrays.toString(means)+",variances="+Arrays.toString(variances));
  }

  public Instance pipe(Instance carrier) {
    if (!(carrier.getData() instanceof FeatureVector)) {
      System.out.println(carrier.getData().getClass());
      throw new IllegalArgumentException("Data must be of type FeatureVector not " + carrier.getData().getClass() + " we got " + carrier.getData());
    }

    if (this.means.length != this.getDataAlphabet().size()
            || this.variances.length != this.getDataAlphabet().size()) {
      throw new GateRuntimeException("Alphabet has grown, this should not happen!");
    }

    FeatureVector fv = (FeatureVector) carrier.getData();
    int[] indices = fv.getIndices();
    double[] values = fv.getValues();
    for (int i = 0; i < indices.length; i++) {
      int index = indices[i];
      double value = values[i];
      if (index < means.length) {
        double mean = means[index];
        double variance = variances[index];
        double newvalue = (value - mean) / Math.sqrt(variance);
        fv.setValue(index, newvalue);
      }
    }
    // TODO: JP: this was here before but I think it is not needed, the code above should 
    // already destructively modify the feature vector inside the instance?
    /*
    boolean isLocked = carrier.isLocked();
    carrier.unLock();
    carrier.setData(fv);
    if(isLocked) carrier.lock();
    */

    return carrier;
  }

  private static final long serialVersionUID = 1;
  private static final int CURRENT_SERIAL_VERSION = 0;

}
