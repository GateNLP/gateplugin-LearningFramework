
package gate.plugin.learningframework.engines;

/**
 * A class that represents the result of a crossvalidation or hold-out evaluation.
 * 
 * @author Johann Petrak
 */
public class EvaluationResultRgHO  extends EvaluationResultRegression {
  public double trainingFraction;
  public int nrRepeats;
  
  @Override
  public String toString() {    
    return "EvaluationResultClHO{" + "rmse=" + rmse + ",trainingFraction="+trainingFraction+",nrRepeats="+nrRepeats + "}";
  }
  
}
