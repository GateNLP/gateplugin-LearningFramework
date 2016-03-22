
package gate.plugin.learningframework.engines;

/**
 * A class that represents the result of a crossvalidation or hold-out evaluation.
 * 
 * @author Johann Petrak
 */
public class EvaluationResultClXval  extends EvaluationResultClassification {
  public int nrFolds;
  public boolean stratified;
  public int nrRepeats;
  
  @Override
  public String toString() {    
    return "EvaluationResultClXval{" + "accuracy=" + accuracyEstimate + ",nrFolds="+nrFolds+"nrRepeats="+nrRepeats+
            "stratified="+stratified + "}";
  }
  
}
