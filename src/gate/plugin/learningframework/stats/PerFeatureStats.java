/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.stats;

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
