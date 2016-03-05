/*
 * GateClassification.java
 *  
 * Copyright (c) 1995-2015, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * Copyright 2015 South London and Maudsley NHS Trust and King's College London
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 *
 * Genevieve Gorrell, 9 Jan 2015
 */
package gate.plugin.learningframework;

import java.util.List;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.plugin.learningframework.Globals;
import gate.plugin.learningframework.features.FeatureExtraction;
import gate.util.InvalidOffsetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class GateClassification {

  private Annotation instance;
  private String classAssigned;
  private Double targetAssigned = null;
  private boolean numericTarget = false;
  private Double confidenceScore;
  private Integer seqSpanID;
  private List<String> classList;
  private List<Double> confidenceList;

  public GateClassification(Annotation instance, String classAssigned,
          Double confidenceScore) {
    this.instance = instance;
    this.classAssigned = classAssigned;
    this.confidenceScore = confidenceScore;
  }
  public GateClassification(Annotation instance, double targetAssigned) {
    this.instance = instance;
    this.targetAssigned = targetAssigned;
    numericTarget = true;
  }

  public GateClassification(Annotation instance, String classAssigned,
          Double confidenceScore, List<String> classes, List<Double> confidences) {
    this.instance = instance;
    this.classAssigned = classAssigned;
    this.confidenceScore = confidenceScore;
    this.classList = classes;
    this.confidenceList = confidences;
  }

  public GateClassification(Annotation instance, String classAssigned,
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
   * Utility function to apply a list of GateClassification to a document.
   * This creates classification/regression output from a list of GateClassification objects.
   * If outputAS is null, then the original instance annotations are modified and receive the
   * target features and additional LearningFramework-specific features (confidence etc.).
   * If outputAS is specified, new annotations which are a copy of the instance annotations
   * are created in the outputAS and the target features are stored in those copies.
   * @param doc
   * @param gcs 
   */
  public static void applyClassification(Document doc, 
          List<GateClassification> gcs, 
          String targetFeature, 
          AnnotationSet outputAS,
          Double minConfidence) {
    for(GateClassification gc : gcs) {
      if (minConfidence != null && 
          minConfidence != Double.NaN &&
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
  
  
  public static void addSurroundingAnnotations( 
          AnnotationSet inputAS, 
          AnnotationSet instanceAS, 
          AnnotationSet outputAS,
          String outputAnnType,
          Double minConfidence) {

    class AnnToAdd {

      long thisStart = -1;
      long thisEnd = -1;
      int len = 0;
      double conf = 0.0;
    }

    Map<Integer, AnnToAdd> annsToAdd = new HashMap<Integer, AnnToAdd>();

    Iterator<Annotation> it = instanceAS.inDocumentOrder().iterator();
    while (it.hasNext()) {
      Annotation inst = it.next();

      //Do we have an annotation in progress for this sequence span ID?
      //If we didn't use sequence learning, just use the same ID repeatedly.
      Integer sequenceSpanID = (Integer) inst.getFeatures().get(Globals.outputSequenceSpanIDFeature);
      if (sequenceSpanID == null) {
        sequenceSpanID = 0;
      }
      AnnToAdd thisAnnToAdd = annsToAdd.get(sequenceSpanID);

      //B, I or O??
      String status = (String) inst.getFeatures().get(Globals.outputClassFeature);
      if (status == null) {
        status = FeatureExtraction.SEQ_OUTSIDE;
      }

      if (thisAnnToAdd != null && (status.equals(FeatureExtraction.SEQ_BEGINNING) || status.equals(FeatureExtraction.SEQ_OUTSIDE))) {
        //If we've found a beginning or an end, this indicates that a current
        //incomplete annotation is now completed. We should write it on and
        //remove it from the map.
        double entityconf = thisAnnToAdd.conf / thisAnnToAdd.len;

        if (thisAnnToAdd.thisStart != -1 && thisAnnToAdd.thisEnd != -1
                && (minConfidence == null || entityconf >= minConfidence) ) {
          FeatureMap fm = Factory.newFeatureMap();
          fm.put(Globals.outputProbFeature, entityconf);
          if (sequenceSpanID != null) {
            fm.put(Globals.outputSequenceSpanIDFeature, sequenceSpanID);
          }
          try {
            // TODO: get an invalid offset exception here: Offsets [31060:31059] not valid for this document of size 43447
            // so the end offset is before the start offset?
            outputAS.add(
                    thisAnnToAdd.thisStart, thisAnnToAdd.thisEnd,
                    outputAnnType, fm);
          } catch (InvalidOffsetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
        }

        annsToAdd.remove(sequenceSpanID);
      }

      if (status.equals(FeatureExtraction.SEQ_BEGINNING)) {
        AnnToAdd ata = new AnnToAdd();
        ata.thisStart = inst.getStartNode().getOffset();
        //Update the end on the offchance that this is it
        ata.thisEnd = inst.getEndNode().getOffset();
        ata.conf = (Double) inst.getFeatures().get(Globals.outputProbFeature);
        ata.len++;
        annsToAdd.put(sequenceSpanID, ata);
      }

      if (status.equals(FeatureExtraction.SEQ_INSIDE) && thisAnnToAdd != null) {
        thisAnnToAdd.conf += (Double) inst.getFeatures().get(Globals.outputProbFeature);
        thisAnnToAdd.len++;
        //Update the end on the offchance that this is it
        thisAnnToAdd.thisEnd = inst.getEndNode().getOffset();
      }

      //Remove each inst ann as we consume it
      //inputAS.remove(inst);
    }

    //Add any hanging entities at the end.
    Iterator<Integer> atait = annsToAdd.keySet().iterator();
    while (atait.hasNext()) {
      Integer sequenceSpanID = (Integer) atait.next();
      AnnToAdd thisAnnToAdd = annsToAdd.get(sequenceSpanID);
      double entityconf = thisAnnToAdd.conf / thisAnnToAdd.len;

      if (thisAnnToAdd.thisStart != -1 && thisAnnToAdd.thisEnd != -1
              && (minConfidence == null || entityconf >= minConfidence) ) {
        FeatureMap fm = Factory.newFeatureMap();
        fm.put(Globals.outputProbFeature, entityconf);
        if (sequenceSpanID != null) {
          fm.put(Globals.outputSequenceSpanIDFeature, sequenceSpanID);
        }
        try {
          outputAS.add(
                  thisAnnToAdd.thisStart, thisAnnToAdd.thisEnd,
                  outputAnnType, fm);
        } catch (InvalidOffsetException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    }
  }
  
  @Override
  public String toString() {
    return "GateClassification{type="+instance.getType()+", at="+gate.Utils.start(instance)+
            ", target="+(numericTarget?targetAssigned:classAssigned)+"}";
  }
}
