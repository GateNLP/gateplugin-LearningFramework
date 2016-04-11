
package gate.plugin.learningframework.engines;

/**
 * A class that represents the result of a crossvalidation or hold-out evaluation.
 * 
 * @author Johann Petrak
 */
public class EvaluationResultRgXval  extends EvaluationResultRegression {
  public int nrFolds;
  
  @Override
  public String toString() {    
    return "EvaluationResultRgXval{" + "RMSE=" + rmse + ",nrFolds="+nrFolds + "}";
  }
  
}
