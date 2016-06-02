/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.engines;

/**
 *
 * @author johann
 */
public enum AlgorithmSequenceTagging implements Algorithm {
  MALLET_SEQ_CRF(EngineMalletSeq.class,null), // ByLabelLikelihood or ByThreadedLabelLikelihood
  MALLET_SEQ_CRF_SG(EngineMalletSeq.class,null), // ByStochasticGradient
  MALLET_SEQ_CRF_VG(EngineMalletSeq.class,null), // ByValueGradient
  // The following requires specification of an array of Optimizable.ByGradientValue
  // instances which need to be initialized with Instances 
  // We only add this after figuring out exactly how it needs to get set up!
  // MALLET_SEQ_CRF_VGS(EngineMalletSeq.class,null), // ByValueGradients  
  MALLET_SEQ_MEMM(EngineMalletSeq.class,null);
  // HMM requires a different representation: instead of a sequence of feature vectors a sequence of features.
  // We do not bother to implement this at this stage ...
  //MALLET_SEQ_HMM(EngineMalletSeq.class,null); // too complex to specify the trainer class here
  // MALLET_SEQ_SPECIFY_CLASS(EngineMallet.class,null); // it is not really possible to specify a class for this (yet?)
  private AlgorithmSequenceTagging() {
    
  }
  private AlgorithmSequenceTagging(Class engineClass, Class algorithmClass) {
    this.engineClass = engineClass;
    this.trainerClass = algorithmClass;
  }
  private Class engineClass;
  private Class trainerClass;
  public Class getEngineClass() { return engineClass; }

  @Override
  public Class getTrainerClass() {
    return trainerClass;
  }

  @Override
  public void setTrainerClass(Class trainerClass) {
    this.trainerClass = trainerClass;
  }
}
