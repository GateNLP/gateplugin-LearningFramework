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
