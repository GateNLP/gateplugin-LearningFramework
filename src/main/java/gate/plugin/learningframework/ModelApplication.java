/*
 * Copyright (c) 2015-2016 The University Of Sheffield.
 *
 * This file is part of gateplugin-LearningFramework 
 * (see https://github.com/GateNLP/gateplugin-LearningFramework).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
 */
package gate.plugin.learningframework;

import java.util.List;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.plugin.learningframework.features.SeqEncoder;
import gate.util.GateRuntimeException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ModelApplication {

  private Annotation instance;
  private String classAssigned;
  private Double targetAssigned = null;
  private boolean numericTarget = false;
  private Double confidenceScore;
  private Integer seqSpanID;
  private List<String> classList;
  private List<Double> confidenceList;

  public ModelApplication(Annotation instance, String classAssigned,
          Double confidenceScore) {
    this.instance = instance;
    this.classAssigned = classAssigned;
    this.confidenceScore = confidenceScore;
  }
  public ModelApplication(Annotation instance, double targetAssigned) {
    this.instance = instance;
    this.targetAssigned = targetAssigned;
    numericTarget = true;
  }

  public ModelApplication(Annotation instance, String classAssigned,
          Double confidenceScore, List<String> classes, List<Double> confidences) {
    this.instance = instance;
    this.classAssigned = classAssigned;
    this.confidenceScore = confidenceScore;
    this.classList = classes;
    this.confidenceList = confidences;
  }

  public ModelApplication(Annotation instance, String classAssigned,
          Double confidenceScore, Integer sequenceSpanID) {
    this.instance = instance;
    this.classAssigned = classAssigned;
    this.confidenceScore = confidenceScore;
    this.seqSpanID = sequenceSpanID;
  }

  public Annotation getInstance() {
    return instance;
  }

  public void setInstance(Annotation instance) {
    this.instance = instance;
  }

  public String getClassAssigned() {
    return classAssigned;
  }

  public void setClassAssigned(String classAssigned) {
    this.classAssigned = classAssigned;
  }

  public Double getConfidenceScore() {
    return confidenceScore;
  }

  public void setConfidenceScore(Double confidenceScore) {
    this.confidenceScore = confidenceScore;
  }

  public Integer getSeqSpanID() {
    return seqSpanID;
  }

  public List<String> getClassList() {
    return classList;
  }

  public List<Double> getConfidenceList() {
    return confidenceList;
  }

  public void setSeqSpanID(Integer sequenceSpanID) {
    this.seqSpanID = sequenceSpanID;
  }
  
  /**
   * Utility function to apply a list of ModelApplication to a document.
   * This creates classification/regression output from a list of ModelApplication objects.
 If outputAS is null, then the original instance annotations are modified and receive the
 target features and additional LearningFramework-specific features (confidence etc.).
 If outputAS is specified, new annotations which are a copy of the instance annotations
 are created in the outputAS and the target features are stored in those copies.
   * @param doc
   * @param gcs 
   */
  public static void applyClassification(Document doc, 
          List<ModelApplication> gcs, 
          String targetFeature, 
          AnnotationSet outputAS,
          Double minConfidence) {
    for(ModelApplication gc : gcs) {
      if (minConfidence != null && 
          !Double.isNaN(minConfidence) &&
          gc.getConfidenceScore() < minConfidence) {
        //Skip it
        continue;
      }      
      FeatureMap fm = null;
      if(outputAS == null) {
        fm = gc.getInstance().getFeatures();
      } else {
        fm = gate.Utils.toFeatureMap(gc.getInstance().getFeatures());
      }
      if(gc.numericTarget) {
        fm.put(targetFeature,gc.targetAssigned);
      } else {
        fm.put(targetFeature, gc.getClassAssigned());        
        fm.put(Globals.outputClassFeature, gc.getClassAssigned());
        fm.put(Globals.outputProbFeature, gc.getConfidenceScore());
        if (gc.getClassList() != null && gc.getConfidenceList() != null) {
          fm.put(Globals.outputClassFeature + "_list", gc.getClassList());
          fm.put(Globals.outputProbFeature + "_list", gc.getConfidenceList());
        }
        if (gc.getSeqSpanID() != null) {
          fm.put(Globals.outputSequenceSpanIDFeature, gc.getSeqSpanID());
        }
      }
      if(outputAS != null) {
        int id = gate.Utils.addAnn(outputAS, gc.getInstance(), gc.getInstance().getType(), fm);
        Annotation ann = outputAS.get(id);
        // System.err.println("DEBUG adding ann "+ann+", target feature "+targetFeature+" should be "+gc.getClassAssigned());
      }
    } // for
  }
  
  /**
   * From an annotation set with e.g. BIO class annotations on the instances,
   * create an output annotation set with the actual sequence annotations.
   * 
   * 
   * TODO/NOTE: for some reasons this passes on intputAS but we do not use it.
   * TODO/NOTE: we should really also get the sequence annotation and limit
   * resolving BIO to within each sequence.
   * 
   * NOTE: originally, this was just using B/I/O, we now changed to using Type|B
   * Type|I and O. However, this should really get moved to the corresponding SeqEncode subclass.
   * 
   * @param unused
   * @param instanceAS
   * @param outputAS
   * @param outputAnnType
   * @param minConfidence 
   * @param seqEncoder 
   */
  public static void addSurroundingAnnotations( 
          AnnotationSet unused, 
          AnnotationSet instanceAS, 
          AnnotationSet outputAS,
          String outputAnnType,
          Double minConfidence,
          SeqEncoder seqEncoder) {


    // TODO!! we need to delegate this to the proper method of seqEncoder, in a way
    // that abstracts away a little from annotations etc., ideally!!!
    // Probably best to process a whole sequence everytime we call the seqEncoder method,
    // if we do not have a sequence, then a whole document. 
    
    // map of open annotations, per sequence annotation type
    Map<String, AnnToAdd> annsToAdd = new HashMap<String, AnnToAdd>();

    int oldSeqId = -1;  // keep track of which sequence annotation we are in
    
    for (Annotation inst : instanceAS.inDocumentOrder()) {

      // get the sequence id of the current instance, or 0 if no sequence (whole document)
      Integer sequenceSpanID = (Integer) inst.getFeatures().get(Globals.outputSequenceSpanIDFeature);
      if (sequenceSpanID == null) {
        sequenceSpanID = 0;
      }
      if(sequenceSpanID != oldSeqId) {
        // if the oldSeqId is -1, do not worry, this is just the first instance annotation
        if(oldSeqId == -1) oldSeqId = sequenceSpanID;
        else {
          // close any annotations still open and remove            
            Iterator<Map.Entry<String, AnnToAdd>> it = annsToAdd.entrySet().iterator();
            while(it.hasNext()) {
              Map.Entry<String,AnnToAdd> entry = it.next();              
              //System.err.println("Finishing at seq end: "+entry.getValue().thisEnd);
              addSequenceAnn(entry.getValue(), outputAS, minConfidence);
              it.remove();
            }
            oldSeqId = sequenceSpanID;
        }
      }
      
      // Type|B, Type|I or Type|O??
      // We could also get a sequence of different TypeX|B or TypeY|I here
      String target = (String) inst.getFeatures().get(Globals.outputClassFeature);
      String[] typesAndCodes;
      if(target == null) target = SeqEncoder.CODE_OUTSIDE;
      // now we have two cases: either we got an outside or we got one or more 
      // type/code pairs
      
      // if we have an outside, just end all the open annotations, if any
      if(target.equals(SeqEncoder.CODE_OUTSIDE)) {
            // finish any open anns of the same type and remove the open anns
            Iterator<Map.Entry<String, AnnToAdd>> it = annsToAdd.entrySet().iterator();
            while(it.hasNext()) {
              Map.Entry<String,AnnToAdd> entry = it.next();          
              //System.err.println("Finishing because of O "+entry.getValue().thisEnd);
              addSequenceAnn(entry.getValue(), outputAS, minConfidence);
              it.remove();
            }
      } else {
        typesAndCodes = target.split(SeqEncoder.TYPESEP_PATTERN);
        // otherwise: iterate over all types and codes and process accordingly
        // after processing all types and codes, finish all the types which
        // are open but where not in the target
        Set<String> touchedTypes = new HashSet<>();
        for(String typeAndCode : typesAndCodes) {
          String[] tac = typeAndCode.split(SeqEncoder.CODESEP_PATTERN);
          //System.err.println("type/code="+tac[0]+"/"+tac[1]);
          if(tac[1].equals(SeqEncoder.CODE_BEGIN)) {
            touchedTypes.add(tac[0]);
            // finish any ann which is of the same type and remove
            Iterator<Map.Entry<String, AnnToAdd>> it = annsToAdd.entrySet().iterator();
            while(it.hasNext()) {
              Map.Entry<String,AnnToAdd> entry = it.next();              
              if(entry.getKey().equals(tac[0])) {
                //System.err.println("Finishing because B: "+entry.getValue().thisEnd);
                addSequenceAnn(entry.getValue(), outputAS, minConfidence);
                it.remove();
              }
            }
            // now add a new open annotation for that type
            AnnToAdd ata = new AnnToAdd();
            ata.thisStart = inst.getStartNode().getOffset();
            ata.annType = tac[0];
            //Update the end on the offchance that this is it
            ata.thisEnd = inst.getEndNode().getOffset();
            Object tmpfv = inst.getFeatures().get(Globals.outputProbFeature);
            ata.conf += tmpfv == null ? 0.0 : (Double)tmpfv;
            ata.conf = (Double) inst.getFeatures().get(Globals.outputProbFeature);
            ata.len++;
            annsToAdd.put(tac[0], ata);            
          } else if(tac[1].equals(SeqEncoder.CODE_INSIDE)) {
            // go through the open annotations and if we find one with that type, continue
            // it
            Iterator<Map.Entry<String, AnnToAdd>> it = annsToAdd.entrySet().iterator();
            while(it.hasNext()) {
              Map.Entry<String,AnnToAdd> entry = it.next();              
              if(entry.getKey().equals(tac[0])) {
                //System.err.println("extending existing annotation to offset "+inst.getEndNode().getOffset());
                touchedTypes.add(tac[0]);
                // continue the ann and extend the span
                Object tmpfv = inst.getFeatures().get(Globals.outputProbFeature);
                entry.getValue().conf += tmpfv == null ? 0.0 : (Double)tmpfv;
                entry.getValue().len++;
                //Update the end on the offchance that this is it
                entry.getValue().thisEnd = inst.getEndNode().getOffset();
              }
            }            
          } else {
            throw new GateRuntimeException("Unexpected SeqEncoder code: "+tac[1]);
          }
        } // for typeAndCode : typesAndCodes
        // after processing all the types/codes in the target, go through the 
        // open annotations and close those which have not been touched by this target
        //System.err.println("Set of touched types: "+touchedTypes);
        Iterator<Map.Entry<String, AnnToAdd>> it = annsToAdd.entrySet().iterator();
        while(it.hasNext()) {
          Map.Entry<String,AnnToAdd> entry = it.next();              
          // if this is an open annotation with a type which has not been included
          // in the target, close and remove it
          if(!touchedTypes.contains(entry.getKey())) {
            //System.err.println("finishing untouched ann at "+entry.getValue().thisEnd);
            addSequenceAnn(entry.getValue(), outputAS, minConfidence);
            it.remove();
          }
        }        
      } // if we do not have CODE_OUTSIDE
      
    } // for all instance annotations
  }
  
  /**
   * If confidence constraint is satisfied, add Annotation and return it, otherwise
   * add nothing and return null.
   * 
   * @param annToAdd
   * @param outputAS
   * @param outputAnnType
   * @param minConfidence
   * @return 
   */
  public static Annotation addSequenceAnn(AnnToAdd annToAdd, AnnotationSet outputAS, Double minConfidence) {
    double entityConfidence = annToAdd.conf / annToAdd.len;
    if(annToAdd.thisStart != -1 && annToAdd.thisEnd != -1 && 
            (minConfidence == null || entityConfidence >= minConfidence)) {
      FeatureMap fm = Factory.newFeatureMap();
      fm.put(Globals.outputProbFeature, entityConfidence);
      // TODO: add the sequence span id? UPDATE: since we return the annotation
      // we just created, the caller can add anything to the feature map
      int id = gate.Utils.addAnn(outputAS, annToAdd.thisStart, annToAdd.thisEnd, annToAdd.annType, fm);
      return outputAS.get(id);
    } else {
      return null;
    }
  }
  
  private static class AnnToAdd {

      long thisStart = -1;
      long thisEnd = -1;
      int len = 0;
      double conf = 0.0;
      String annType = "INVALID";
    }
  
  
  @Override
  public String toString() {
    return "GateClassification{type="+instance.getType()+", at="+gate.Utils.start(instance)+
            ", target="+(numericTarget?targetAssigned:classAssigned)+"}";
  }
}
