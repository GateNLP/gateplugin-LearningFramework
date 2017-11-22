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

/**
 *
 * @author Johann Petrak
 */
public class PerFeatureStats {

  public double sum = Double.NaN;
  public double sumOfSquares = Double.NaN;
  public double mean = Double.NaN;
  public double var = Double.NaN;
  public double min = Double.NaN;
  public double max = Double.NaN;
  public Boolean binary = null;

  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("PerFeature{");
    sb.append("mean=");
    sb.append(mean);
    sb.append(",var=");
    sb.append(var);
    sb.append(",min=");
    sb.append(min);
    sb.append(",max=");
    sb.append(max);
    sb.append(",bin=");
    sb.append(binary);
    //sb.append(",sum=");
    //sb.append(sum);
    //sb.append(",sumsq=");
    //sb.append(sumOfSquares);
    sb.append("}");
    return sb.toString();
  }

}
