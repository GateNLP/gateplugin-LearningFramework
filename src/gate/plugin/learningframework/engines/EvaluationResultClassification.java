
package gate.plugin.learningframework.engines;

/**
 * A class that represents the result of a classification evaluation.
 * 
 * @author Johann Petrak
 */
public abstract class EvaluationResultClassification extends EvaluationResult {
  public double accuracyEstimate;
  public int nrCorrect;    // number of correct over all folds and all repeats
  public int nrIncorrect;  // number of incorrect over all folds and all repeats
  
  // TODO: correct implementation of equals and hashCode!?!

  
}
