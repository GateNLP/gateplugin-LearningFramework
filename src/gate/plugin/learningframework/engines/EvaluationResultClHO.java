
package gate.plugin.learningframework.engines;

/**
 * A class that represents the result of a crossvalidation or hold-out evaluation.
 * 
 * @author Johann Petrak
 */
public class EvaluationResultClHO  extends EvaluationResultClassification {
  public double trainingFraction;
  public boolean stratified;
  public int nrRepeats;
  
  @Override
  public String toString() {    
    return "EvaluationResultClHO{" + "accuracy=" + accuracyEstimate + ",trainingFraction="+trainingFraction+",nrRepeats="+nrRepeats+
            ",stratified="+stratified + "}";
  }
  
}
