
package gate.plugin.learningframework.engines;

/**
 * A class that represents the result of a classification evaluation.
 * 
 * @author Johann Petrak
 */
public abstract class EvaluationResultRegression extends EvaluationResult {
  public double rmse;
  public double nrTotal;    // number of instances
  public double sumSqrErr;  // sum of squared errors
  public double sumAbsErr;  // sum of absolute errors
  
  // TODO: correct implementation of equals and hashCode!?!

  
}
