package gate.plugin.learningframework.data;

import gate.Annotation;
import gate.AnnotationSet;
import java.util.List;
import cc.mallet.pipe.Noop;
import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.AugmentableFeatureVector;
import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;
import cc.mallet.types.LabelAlphabet;
import gate.plugin.learningframework.ScalingMethod;
import gate.plugin.learningframework.features.FeatureSpecAttribute;
import gate.plugin.learningframework.features.FeatureExtraction;
import gate.plugin.learningframework.features.FeatureInfo;
import gate.plugin.learningframework.features.TargetType;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.plugin.learningframework.mallet.PipeScaleMeanVarAll;
import gate.plugin.learningframework.mallet.PipeScaleMinMaxAll;
import gate.plugin.learningframework.stats.FVStatsMeanVarAll;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import org.apache.log4j.Logger;

/**
 * This represents a corpus in Mallet format where we have a single feature vector and single
 * target. This corpus may be created for classification, regression or sequence tagging, but the
 * representation is always a pair (featurevector,target). In the case of sequence tagging, the
 * corpus is created for the use of normal classification algorithms that will be used to predict
 * the begin/inside/outside classifications.
 *
 * @author johann
 */
public class CorpusRepresentationMalletTarget extends CorpusRepresentationMallet {

  static final Logger logger = Logger.getLogger("CorpusRepresentationMallet");


  /**
   * Constructor for creating a new CorpusRepresentation from a FeatureInfo. 
   * @param fi
   * @param sm 
   */
  public CorpusRepresentationMalletTarget(FeatureInfo fi, ScalingMethod sm, TargetType targetType) {
    featureInfo = fi;
    scalingMethod = sm;

    LabelAlphabet targetAlphabet = (targetType == TargetType.NOMINAL) ? new LabelAlphabet() : null;
    Pipe innerPipe = new Noop(new Alphabet(), targetAlphabet);
    List<Pipe> pipes = new ArrayList<Pipe>();
    pipes.add(innerPipe);
    pipe = new LFPipe(pipes);
    pipe.setFeatureInfo(fi);
    instances = new InstanceList(pipe);
  }
  
  /**
   * Non-public constructor for use when creating from a serialized pipe.
   * @param fi 
   */
  CorpusRepresentationMalletTarget(LFPipe pipe) {
    this.pipe = pipe;
    this.featureInfo = pipe.getFeatureInfo();
    this.scalingMethod = null;
    this.instances = new InstanceList(pipe);
  }

  /**
   * Create a new CRMT instance based on the pipe stored in directory.
   * @param directory
   * @return 
   */
  public static CorpusRepresentationMalletTarget load(File directory) {
    // load the pipe
    File inFile = new File(directory,"pipe.pipe");
    ObjectInputStream ois = null;
    LFPipe lfpipe = null;
    try {
      ois = new ObjectInputStream (new FileInputStream(inFile));
      lfpipe = (LFPipe) ois.readObject();
    } catch (Exception ex) {
      throw new GateRuntimeException("Could not read pipe from "+inFile,ex);
    } finally {
      try {
        if(ois!=null) ois.close();
      } catch (IOException ex) {
        logger.error("Error closing stream after loading pipe "+inFile, ex);
      }
    }
    CorpusRepresentationMalletTarget crmc = new CorpusRepresentationMalletTarget(lfpipe);
    return crmc;
  }
  
  
  
  
  
  public void clear() {
    LFPipe pipe = (LFPipe)instances.getPipe();
    instances = new InstanceList(pipe);
  }
  
  // NOTE: at application time we do not explicitly create a CorpusRepresentatioMallet object.
  // Instead, the pipe gets saved with the model and can get retrieved from the loaded model 
  // later. The method extractIndependentFeaturesHelper is also used at application time to 
  // extract the Instances, using the Pipe that was stored with the model.
  // For non-Mallet algorithms we store the pipe separately and load it separately when the model
  // is loaded for application. The Pipe is then again used with extractIndependentFeaturesHelper 
  // to get the instances.


  public Instance extractIndependentFeatures(
          Annotation instanceAnnotation,
          AnnotationSet inputAS)
  {
    LFPipe pipe = (LFPipe)instances.getPipe();
    FeatureInfo featureInfo = pipe.getFeatureInfo();
    return extractIndependentFeaturesHelper(instanceAnnotation, inputAS,
            featureInfo, pipe);
  }
  
  /**
   * Extract the independent features for a single instance annotation.
   * Extract the independent features for a single annotation according to the information
   * in the featureInfo object. The information in the featureInfo instance gets updated 
   * by this. 
   * NOTE: this method is static so that it can be used in the CorpusRepresentationMalletSeq class too.
   * @param instanceAnnotation
   * @param inputAS
   * @param targetFeatureName
   * @param featureInfo
   * @param pipe
   * @param nameFeature
   * @return 
   */
  static Instance extractIndependentFeaturesHelper(
          Annotation instanceAnnotation,
          AnnotationSet inputAS,
          FeatureInfo featureInfo,
          Pipe pipe) {
    
    AugmentableFeatureVector afv = new AugmentableFeatureVector(pipe.getDataAlphabet());
    Instance inst = new Instance(afv, null, null, null);
    for(FeatureSpecAttribute attr : featureInfo.getAttributes()) {
      FeatureExtraction.extractFeature(inst, attr, inputAS, instanceAnnotation);
    }
    // TODO: we destructively replace the AugmentableFeatureVector by a FeatureVector here,
    // but it is not clear if this is beneficial - our assumption is that yes.
    inst.setData(((AugmentableFeatureVector)inst.getData()).toFeatureVector());
    return inst;
  }

  /**
   * Add instances. The exact way of how the target is created to the instances depends on which
   * parameters are given and which are null. The parameter sequenceAS must always be null for this
   * corpus representation since this corpus representation is not usable for sequence tagging
   * algorithms If the parameter classAS is non-null then instances for a sequence tagging task are
   * created, in that case targetFeatureName must be null. If targetFeatureName is non-null then
   * instances for a regression or classification problem are created (depending on targetType) and
   * classAS must be null. if the parameter nameFeatureName is non-null, then a Mallet instance name
   * is added from the source document and annotation.
   *
   * @param instancesAS
   * @param inputAS
   * @param nameFeatureName
   */
  @Override
  public void add(AnnotationSet instancesAS, AnnotationSet sequenceAS, AnnotationSet inputAS, AnnotationSet classAS, String targetFeatureName, TargetType targetType, String nameFeatureName) {
    if(sequenceAS != null) {
      throw new GateRuntimeException("LF invalid call to CorpusRepresentationMallet.add: sequenceAS must be null "+
              " for document "+inputAS.getDocument().getName());
    }
    List<Annotation> instanceAnnotations = instancesAS.inDocumentOrder();
    for (Annotation instanceAnnotation : instanceAnnotations) {
      Instance inst = extractIndependentFeaturesHelper(instanceAnnotation, inputAS, featureInfo, pipe);
      if (classAS != null) {
        // extract the target as required for sequence tagging
        FeatureExtraction.extractClassForSeqTagging(inst, pipe.getTargetAlphabet(), classAS, instanceAnnotation);
      } else {
        if(targetType == TargetType.NOMINAL) {
          FeatureExtraction.extractClassTarget(inst, pipe.getTargetAlphabet(), targetFeatureName, instanceAnnotation, inputAS);
        } else if(targetType == TargetType.NUMERIC) {
          FeatureExtraction.extractNumericTarget(inst, targetFeatureName, instanceAnnotation, inputAS);
        }
      }
      // if a nameFeature is specified, add the name informatin to the instance
      if(nameFeatureName != null) {
        FeatureExtraction.extractName(inst, instanceAnnotation, inputAS.getDocument());
      }
      if(!FeatureExtraction.ignoreInstanceWithMV(inst)) {
        instances.add(inst);
      }
    }
  }
  
  /**
   * Finish adding instances to the CR. 
   * This will also do the rescaling and any other additional calculations, if necessary.
   * @param scaleFeatures 
   */
  @Override
  public void finish() {    
    if(scalingMethod == ScalingMethod.NONE) return;
    Pipe normalizer = null;
    if(scalingMethod == ScalingMethod.MEANVARIANCE_ALL_FEATURES) {
      FVStatsMeanVarAll stats = new FVStatsMeanVarAll(instances);
      //System.err.println("DEBUG: got stats:\n"+stats);
      normalizer = new PipeScaleMeanVarAll(instances.getDataAlphabet(), stats);
    } else if(scalingMethod == ScalingMethod.MINMAX_ALL_FEATURES) {
      FVStatsMeanVarAll stats = new FVStatsMeanVarAll(instances);
      //System.err.println("DEBUG: got stats:\n"+stats);
      normalizer = new PipeScaleMinMaxAll(instances.getDataAlphabet(), stats);      
    } else {
      throw new GateRuntimeException("Internal error: unexpected scaling method");
    }
    //System.err.println("DEBUG: re-normalizing instances");
    for(Instance inst : instances) {
      inst = normalizer.pipe(inst);
    }
    ArrayList<Pipe> pipeList = pipe.pipes();
    pipeList.add(normalizer);
    //System.out.println("DEBUG normalize: added normalizer pipe " + normalizer);
    //System.out.println("DEBUG pipes after normalization: " + pipe);
  }

}
