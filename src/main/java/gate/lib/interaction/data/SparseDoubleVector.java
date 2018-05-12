package gate.lib.interaction.data;

import java.io.Serializable;

/**
 * Minimal container for a sparse vector of n non-zero locations.
 * This is meant to be used only for passing on sparse vectors, so 
 * no attempt is made to have any of the specific methods normally
 * needed for sparse vectors, and especially there is no code to make
 * accessing vector elements fast.
 * 
 * @author Johann Petrak
 */
public class SparseDoubleVector implements Serializable {

  private static final long serialVersionUID = 2L;
 
  protected int[] indices;
  protected double[] values;
  protected double instanceWeight = Double.NaN;
  public SparseDoubleVector(int numberOfLocations) {
    indices = new int[numberOfLocations];
    values = new double[numberOfLocations];
  }
  
  public int[] getLocations() { return indices; }
  public double[] getValues() { return values; }
  public int nLocations() { return indices.length; }
  public double getInstanceWeight () { return instanceWeight; }
  public void setInstanceWeight(double weight) { instanceWeight = weight; }
  
}
