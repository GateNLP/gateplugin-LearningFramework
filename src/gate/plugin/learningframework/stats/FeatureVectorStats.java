/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.stats;

import cc.mallet.types.FeatureVector;

/**
 * A base class for calculating statistics for features in a feature vector.
 * A specific implementation may provide means to select which features
 * are included in the stats, which statistics are calculated etc. 
 * 
 * The common way to use the concrete implementations is to construct a status
 * object, then invoke addFeatureVector(fv) for all the feature vectors and
 * complete the calculation of the stats using the method finish(). Once 
 * finish has been invoked, the stats instance becomes immutable.
 * 
 * @author Johann Petrak
 */
public interface FeatureVectorStats {
  public void addFeatureVector(FeatureVector fv);
  public void finish();
}
