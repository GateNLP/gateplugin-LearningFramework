/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.data;

import cc.mallet.pipe.Pipe;
import cc.mallet.types.Alphabet;
import cc.mallet.types.FeatureVector;
import cc.mallet.types.InstanceList;
import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;
import gate.plugin.learningframework.engines.Parms;
import gate.plugin.learningframework.features.CodeAs;
import gate.plugin.learningframework.features.Datatype;
import gate.plugin.learningframework.features.FeatureExtraction;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.NotImplementedException;
import weka.core.Attribute;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffSaver;
import weka.core.converters.CSVSaver;
import weka.core.converters.JSONSaver;
import weka.core.converters.LibSVMSaver;
import weka.core.converters.MatlabSaver;
import weka.core.converters.SVMLightSaver;

/**
 *
 * @author Johann Petrak
 */
public class CorpusRepresentationWeka extends CorpusRepresentation {

  weka.core.Instances data;

  /**
   * Create a Weka representation from a Mallet representation. This includes the targets.
   * @param other 
   */
  public CorpusRepresentationWeka(CorpusRepresentationMallet other) {
    data = getFromMallet(other);
  }
  

  public void clear() {
    // NOTE: not sure if this actually keeps the attribute infos and only clears the 
    // actual instances like the contract for this method promises...
    data.clear();
  }

  @Override
  public Object getRepresentation() {
    return data;
  }

  public Instances getRepresentationWeka() {
    return data;
  }

  /**
   * Export the data. If parms is null then default ARFF format is used. In addition, parms can
   * contain the parameter -format fmt and additional parameters specific to the format. If format
   * is
   * <ul>
   * <li> "csv": -F fieldSeparator (default is tab) -M missingValueString (default is ?) -N
   * (suppress header row, default is no)
   * <li> "
   *
   * @param directory
   * @param format
   */
  public void export(File directory, String parms) {
    if (parms == null || parms.isEmpty()) {
      System.err.println("EXPORTING using ArffSaver");
      ArffSaver saver = new ArffSaver();
      saver.setInstances(data);
      File outFile = new File(directory, "data.arff");
      try {
        saver.setFile(outFile);
      } catch (IOException ex) {
        throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
      }
      try {
        saver.writeBatch();
      } catch (IOException ex) {
        throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
      }
    } else {
      // first parse the parms to see if we have a -format value
      Parms ps = new Parms(parms, "f:format:s");
      String format = (String) ps.getValueOrElse("format", "");
      if (format.equals("csv")) {
        ps = new Parms(parms, "F:F:s", "M:M:s", "N:N:b");
        String fieldSep = gate.util.Strings.unescape((String) ps.getValueOrElse("F", "\\t"));
        String mv = gate.util.Strings.unescape((String) ps.getValueOrElse("M", "?"));
        boolean noHeader = (boolean) ps.getValueOrElse("N", true);
        CSVSaver saver = new CSVSaver();
        saver.setInstances(data);
        File outFile = new File(directory, "data.csv");
        try {
          saver.setFile(outFile);
        } catch (IOException ex) {
          throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
        }
        try {
          saver.writeBatch();
        } catch (IOException ex) {
          throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
        }
      } else if (format.equals("json")) {
        File outFile = new File(directory, "data.json");
        JSONSaver saver = new JSONSaver();
        saver.setInstances(data);
        try {
          saver.setFile(outFile);
        } catch (IOException ex) {
          throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
        }
        try {
          saver.writeBatch();
        } catch (IOException ex) {
          throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
        }
      } else if (format.equals("libsvm")) {
        File outFile = new File(directory, "data.libsvm");
        LibSVMSaver saver = new LibSVMSaver();
        saver.setInstances(data);
        try {
          saver.setFile(outFile);
        } catch (IOException ex) {
          throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
        }
        try {
          saver.writeBatch();
        } catch (IOException ex) {
          throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
        }
      } else if (format.equals("svmlight")) {
        File outFile = new File(directory, "data.svmlight");
        SVMLightSaver saver = new SVMLightSaver();
        saver.setInstances(data);
        try {
          saver.setFile(outFile);
        } catch (IOException ex) {
          throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
        }
        try {
          saver.writeBatch();
        } catch (IOException ex) {
          throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
        }
      } else if (format.equals("matlab")) {
        File outFile = new File(directory, "data.m");
        MatlabSaver saver = new MatlabSaver();
        saver.setInstances(data);
        try {
          saver.setFile(outFile);
        } catch (IOException ex) {
          throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
        }
        try {
          saver.writeBatch();
        } catch (IOException ex) {
          throw new GateRuntimeException("Error exporting Weka data to " + outFile, ex);
        }
      } else {
        throw new GateRuntimeException("Unknown format for exporting Weka representation: "+format);
      }
    }
  }
  
  /**
   * Create a Weka dataset from just the meta-information of the Mallet representation.
   * This creates an empty Instances object that has all the attributes constructed from 
   * the information we have in the Mallet representation. 
   * The dataset will always have a class attribute defined: if there is a mallet target alphabet,
   * a nominal (class) attribute, otherwise a numeric (regression) attribute.
   */
  public static Instances emptyDatasetFromMallet(CorpusRepresentationMallet cr) {
    if (!(cr instanceof CorpusRepresentationMalletTarget)) {
      throw new GateRuntimeException("Conversion to weka not implemented yet: " + cr.getClass());
    }
    InstanceList malletInstances = cr.getRepresentationMallet();
    Alphabet dataAlph = malletInstances.getDataAlphabet();
    Pipe pipe = malletInstances.getPipe();
    // the pipe should always be an instance of LFPipe, but we allow this to be used for instancelists
    // which have been created in a different way and contain some other type of Pipe.
    // If we do hava a LFPipe, we create a map that can be used to figure out which of the 
    // mallet features are either boolean or nominal with a numeric coding. Otherwise, we 
    // regard all features as numeric. 

    // This maps from the mallet feature name to the alphabet for a nominal feature we have
    // stored in our attribute, or to a placeholder alphabet containing true/false if we have
    // a boolean feature.
    Alphabet booleanAlph = new Alphabet();
    booleanAlph.lookupIndex("false");
    booleanAlph.lookupIndex("true");
    Map<String, Alphabet> name2lfalph
            = new HashMap<String, Alphabet>();

    if (pipe instanceof LFPipe) {
      LFPipe lfpipe = (LFPipe) pipe;
      // go through all the antries in the instances data alphabet and try to figure out which
      // of the featuers are either boolean ore nominals coded as number
      for (int i = 0; i < dataAlph.size(); i++) {
        String malletFeatureName = (String) dataAlph.lookupObject(i);
        gate.plugin.learningframework.features.Attribute lfatt
                = FeatureExtraction.lookupAttributeForFeatureName(lfpipe.getFeatureInfo().getAttributes(), malletFeatureName);
        Alphabet alphToUse = null;
        if (lfatt instanceof gate.plugin.learningframework.features.AttributeList) {
          if (((gate.plugin.learningframework.features.AttributeList) lfatt).datatype == Datatype.bool) {
            alphToUse = booleanAlph;
          } else {
            if (((gate.plugin.learningframework.features.AttributeList) lfatt).datatype == Datatype.nominal
                    && ((gate.plugin.learningframework.features.AttributeList) lfatt).codeas == CodeAs.number) {
              alphToUse = ((gate.plugin.learningframework.features.AttributeList) lfatt).alphabet;
            }
          }
        } else if (lfatt instanceof gate.plugin.learningframework.features.SimpleAttribute) {
          if (((gate.plugin.learningframework.features.SimpleAttribute) lfatt).datatype == Datatype.bool) {
            alphToUse = booleanAlph;
          } else {
            if (((gate.plugin.learningframework.features.SimpleAttribute) lfatt).datatype == Datatype.nominal
                    && ((gate.plugin.learningframework.features.SimpleAttribute) lfatt).codeas == CodeAs.number) {
              alphToUse = ((gate.plugin.learningframework.features.SimpleAttribute) lfatt).alphabet;
            }
          }
        }
        // if alphToUse is not null, add it to the map
        if (alphToUse != null) {
          name2lfalph.put(malletFeatureName, alphToUse);
        }
      }
    }
    // This is the information weka needs about the attributes
    ArrayList<Attribute> wekaAttributes = new ArrayList<Attribute>();
    // now go through the data alphabet again and add one weka attribute to the attributes list
    // for each mallet feature. If we know an alphabet for the mallet feature, create the 
    // weka attribute as a nominal otherwise as a numeric weka attribute.
    for (int i = 0; i < pipe.getDataAlphabet().size(); i++) {
      String malletFeatureName = (String) pipe.getDataAlphabet().lookupObject(i);
      Alphabet lfalph = name2lfalph.get(malletFeatureName);
      if (lfalph == null) {
        wekaAttributes.add(new Attribute(malletFeatureName));
      } else {
        List<String> nomVals = new ArrayList<String>(lfalph.size());
        for (int j = 0; j < lfalph.size(); j++) {
          nomVals.add((String) lfalph.lookupObject(j));
        }
        wekaAttributes.add(new Attribute(malletFeatureName, nomVals));
      }
    }
    // now add the class attribute, if necessary: if there is a target alphabet, the class must be nominal,
    // so create a nominal weka attribute, otherwise, create a numeric one
    weka.core.Attribute targetAttr = null;
    if (pipe.getTargetAlphabet() != null) {
      Alphabet talph = pipe.getTargetAlphabet();
      // create the values for the target from the target alphabet
      List<String> classVals = new ArrayList<String>();
      for (int i = 0; i < talph.size(); i++) {
        classVals.add((String) talph.lookupObject(i));
      }
      targetAttr = new Attribute("class", classVals);
      wekaAttributes.add(targetAttr);
      System.err.println("LF: created an empty weka dataset for classification");
    } else {
      targetAttr = new Attribute("target");
      wekaAttributes.add(targetAttr);
      System.err.println("LF: created an empty weka dataset for regression");
    }
    // create the weka dataset 
    Instances insts = new weka.core.Instances("GATELearningFramework", wekaAttributes, malletInstances.size());
    insts.setClass(targetAttr);
    return insts;
  }
  
  public static weka.core.Instance wekaInstanceFromMalletInstance(Instances wekaDataset, 
          cc.mallet.types.Instance malletInstance) {
      FeatureVector fv = (FeatureVector) malletInstance.getData();
      int size = fv.numLocations();
      int wekaTargetIndex = wekaDataset.classIndex();
      // TODO: for now we just directly copy over the mallet values to the weka values
      // We may need to handle certain cases with missing values separately!

      // create  the arrays with one more entry which will be the target, if we have a target
      
      //int indices[] = haveTarget ? new int[size + 1] : new int[size];
      // experimental change: always allocate the space for the class attribute! 
      // We do this because Weka Random Forest threw an exception and complained about a missing
      // class. 
      int indices[] = new int[size+1];
      double values[] = new double[size+1];
      for (int i = 0; i < size; i++) {
        indices[i] = fv.indexAtLocation(i);
        values[i] = fv.valueAtLocation(i);
      }
      // now set the target, if we have one 
      Object malletValue = malletInstance.getTarget();
      if(malletValue != null) {  // we do have a target value, could be a class label or a numeric value
        indices[size] = wekaTargetIndex;
        // if we have a target alphabet, convert the label to a class index, otherwise expect
        // a double value directly
        if(malletInstance.getTargetAlphabet() == null) {
          values[size] = (double) malletInstance.getTarget();
        } else {
          LabelAlphabet la = (LabelAlphabet)malletInstance.getTargetAlphabet();
          Label malletLabel = (Label)malletInstance.getTarget();
          int targetIndex = malletLabel.getIndex();
          String targetString = malletLabel.toString();
          int wekaIndex = wekaDataset.classAttribute().indexOfValue(targetString);
          values[size] = (double)wekaIndex;
          if(targetIndex != wekaIndex) {
            System.err.println("DEBUG ASSERTION FAILED: malletIndex for target is not equal to wekaIndex");
          }
        }
      } else {  // we do not have a target value, so lets create a missing value target for weka
        indices[size] = wekaDataset.classIndex();
        values[size] = Double.NaN;
      }
      weka.core.SparseInstance wekaInstance = new weka.core.SparseInstance(1.0, values, indices, values.length);
      // TODO: is this necessary, is this useful?
      // What does this actually do? Hopefully not actually add or modify anything in the wekaDataset
      // and just give the instance a chance to know about the attributes?
      wekaInstance.setDataset(wekaDataset);
      return wekaInstance;
  }

  /**
   * Create a Weka dataset from Mallet instances.
   * This creates a Weka dataset from the mallet corpus representation.
   * NOTE: for now the attributes list will always contain either a numeric or nominal class
   * (if the pipe has a target alphabet, a nominal class is assumed, otherwise a numeric target).
   * However, if the mallet instance does not have a target, the corresponding weka instance
   * will not have the target attribute set in the sparse vector (so a 0 value is used). 
   * TODO: not sure if this has any bad consequences in those situations where we really
   * want an instance with no target attribute at all, i.e. at classification time.
   *
   * @param cr
   * @return
   */
  public static Instances getFromMallet(CorpusRepresentationMallet cr) {
    Instances wekaInstances =  emptyDatasetFromMallet(cr);

    InstanceList malletInstances = cr.getRepresentationMallet();
    for (cc.mallet.types.Instance malletInstance : malletInstances) {
      weka.core.Instance wekaInstance = wekaInstanceFromMalletInstance(wekaInstances, malletInstance);
      wekaInstances.add(wekaInstance);
    }
    return wekaInstances;
  }

}
