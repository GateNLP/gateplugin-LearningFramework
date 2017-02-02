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
package gate.plugin.learningframework.engines;

import cc.mallet.types.FeatureVector;
import cc.mallet.types.Instance;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import gate.Annotation;
import gate.AnnotationSet;
import gate.plugin.learningframework.EvaluationMethod;
import gate.plugin.learningframework.GateClassification;
import gate.plugin.learningframework.data.CorpusRepresentationMalletTarget;
import gate.plugin.learningframework.mallet.LFPipe;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * An engine that represents a server for ML application.
 * 
 * This engine does not support training, it may at some point support evaluation but
 * does not yet.
 * 
 * The basic idea is that the engine connects to a HTTP server, sends one or more
 * vectors and gets back the predictions. 
 * 
 * Currently all communication is done using JSON and the following format is used:
 * 
 * For sending data
 * indices: a vector of vectors where each inner vector contains the dimension indices of a sparse
 *   vector. This contains as main inner vectors as we send sparse vectors. 
 *   type: integer
 *   if this is missing entirely, then the values are assumed to be dense vectors.
 * values: a vector of vectors where each inner vector contains the values of a sparse vector, where
 *   the i-th value is for the dimension indicated in the i-th location of the corresponding index vector.
 *   type: double
 * weights: a vector of instance weights, or entirely missing to indicate that instance weights 
 *   should not be used
 *   type: double
 * 
 * For receiving data:
 * preds: a vector of vectors of double. Each inner vector is either of length 1 if it contains
 *   the prediction or of length > 1 if it contains the probabilities for the classes.
 *   type: double
 * 
 * 
 * @author Johann Petrak
 */
public class EngineServer extends Engine {

  protected String serverUrl = "http://127.0.0.1:7000";
  
  public EngineServer(File directory, String serverUrl) {
    this.serverUrl = serverUrl;
    info = Info.load(directory);
    loadMalletCorpusRepresentation(directory);
  }
  
  @Override
  protected void loadModel(File directory, String parms) {
    
  }

  @Override
  protected void saveModel(File directory) {
  }

  @Override
  public void trainModel(File dataDirectory, String instanceType, String parms) {
    throw new UnsupportedOperationException("Training not supported");
  }

  @Override
  public EvaluationResult evaluate(String algorithmParameters, EvaluationMethod evaluationMethod, int numberOfFolds, double trainingFraction, int numberOfRepeats) {
    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
  }

  @Override
  public List<GateClassification> classify(AnnotationSet instanceAS, AnnotationSet inputAS, 
          AnnotationSet sequenceAS, String parms) {
    Parms ps = new Parms(parms, "d:dense:b");
    boolean dense = (boolean)ps.getValueOrElse("dense", false);    
    
    CorpusRepresentationMalletTarget data = (CorpusRepresentationMalletTarget)corpusRepresentationMallet;
    data.stopGrowth();
    int nrCols = data.getPipe().getDataAlphabet().size();
    //System.err.println("Running EngineSklearn.classify on document "+instanceAS.getDocument().getName());
    List<GateClassification> gcs = new ArrayList<GateClassification>();
    LFPipe pipe = (LFPipe)data.getRepresentationMallet().getPipe();
    ArrayList<String> classList = null;
    // If we have a classification problem, pre-calculate the class label list
    if(pipe.getTargetAlphabet() != null) {
      classList = new ArrayList<String>();
      for(int i = 0; i<pipe.getTargetAlphabet().size(); i++) {
        String labelstr = pipe.getTargetAlphabet().lookupObject(i).toString();
        classList.add(labelstr);
      }
    }
    // For now create a single request per document
    // eventually we could allow a parameter for sending a maximum number of 
    // instances per request.

    List<Annotation> instances = instanceAS.inDocumentOrder();
    List<double[]> valuesvec = new ArrayList<double[]>();
    List<int[]> indicesvec = new ArrayList<int[]>();
    List<Double> weights = new ArrayList<Double>();
    ObjectMapper mapper = new ObjectMapper();
    boolean haveWeights = false;
    for(Annotation instAnn : instances) {
      Instance inst = data.extractIndependentFeatures(instAnn, inputAS);
      
      inst = pipe.instanceFrom(inst);      
      FeatureVector fv = (FeatureVector)inst.getData();
      //System.out.println("Mallet instance, fv: "+fv.toString(true)+", len="+fv.numLocations());
      
      // Convert to the sparse vector we use to send to the process
      // TODO: depending on a parameter send sparse or dense vectors, for now always send sparse
      
      if (dense) {
        double[] values = new double[nrCols];
        for(int i=0; i<nrCols; i++) {
          values[i] = fv.value(i);
        }
        valuesvec.add(values);
      } else {
        // To send a sparse vector, we need the indices and the values      
        int locs = fv.numLocations();
        int[] indices = new int[locs];
        double[] values = new double[locs];
        for (int i = 0; i < locs; i++) {
          indices[i] = fv.indexAtLocation(i);
          values[i] = fv.valueAtLocation(i);
        }
        valuesvec.add(values);
        indicesvec.add(indices);
      }
      double weight = Double.NaN;
      Object weightObj = inst.getProperty("instanceWeight");
      if(weightObj != null) {
        weight = (double)weightObj;
        haveWeights = true;
      }
      weights.add(weight);
    }
    // create the JSON for the request
    Map<String,Object> data4json = new HashMap<String,Object>();
    if(!dense)
      data4json.put("indices",indicesvec);
    data4json.put("values",valuesvec);
    data4json.put("n",nrCols);
    if(haveWeights) data4json.put("weights",weights);
    String json = null;
    try {
      json = mapper.writeValueAsString(data4json);
    } catch (JsonProcessingException ex) {
      throw new GateRuntimeException("Could not convert instances to json",ex);
    }
    //System.err.println("GOT JSON: "+json);
    
    HttpResponse<String> response;
    try {
      response = Unirest.post(serverUrl)
              .header("accept","application/json")
              .header("content-type","application/json")
              .body(json)
              .asString();
    } catch (UnirestException ex) {
      throw new GateRuntimeException("Exception when connecting to the server",ex);
    }

    // The response should be either OK and JSON or not OK and an error message
    int status = response.getStatus();
    if(status != 200) {
      throw new GateRuntimeException("Response von server is NOK, status="+status+" msg="+response.getBody());
    }
    //System.err.println("Got response, status is OK, data is: "+response.getBody());
    Map responseMap = null;
    try {
      // Parse the json
      responseMap = mapper.readValue(response.getBody(), HashMap.class);
    } catch (IOException ex) {
      Logger.getLogger(EngineServer.class.getName()).log(Level.SEVERE, null, ex);
    }
    
    // NOTE: the json created by the weka server currently automatically creates 1 instead
    // of 1.0 if the value is 1.0, and the parser then creates an Inteer from this. 
    // We could probably change the parsing behaviour into always creating doubles somehow but
    // for now we simply first parse the arrays into Number, then convert each vector into
    // a vector of Double
    @SuppressWarnings("unchecked")
    ArrayList<ArrayList<Number>> targets = (ArrayList<ArrayList<Number>>)responseMap.get("preds");
    
    GateClassification gc = null;
    
    // now go through all the instances again and do the target assignment from the vector(s) we got
    int instNr = 0;
    for(Annotation instAnn : instances) {
      if(pipe.getTargetAlphabet() == null) { // we have regression        
        gc = new GateClassification(instAnn, (double)targets.get(instNr).get(0));
      } else {
        ArrayList<Number> valsN = targets.get(instNr);
        ArrayList<Double> vals = new ArrayList<Double>(valsN.size());
        for(Number valN : valsN) vals.add(valN.doubleValue());
        double target = vals.get(0); // if vals contains just one value, this will be what to use
        if(vals.size()>1) {
          // find the maximum probability and use the index as target
          double maxProb = Double.NEGATIVE_INFINITY;
          double bestIndex = -1;
          int curIdx = 0;
          for(double val : vals) {
            if(val > maxProb) {
              maxProb = val;
              bestIndex = (double)curIdx;
            }
            curIdx++;
          } // for
          target = bestIndex;
        }
        int bestlabel = (int)target;
        String cl
                = pipe.getTargetAlphabet().lookupObject(bestlabel).toString();
        double bestprob = Double.NaN;
        if(vals.size()>1) {
          bestprob = Collections.max(vals);
          gc = new GateClassification(
                instAnn, cl, bestprob, classList, vals);
        } else {
          // create a fake probability distribution with 1.0/0.0 probabilities
          ArrayList<Double> probs = new ArrayList<Double>(classList.size());
          for(int i=0;i<classList.size();i++) {
            if(i==bestlabel) probs.add(1.0);
            else probs.add(0.0);
          }
          gc = new GateClassification(            
                instAnn, cl, bestprob, classList, probs);
          
        }
      }
      gcs.add(gc);
      instNr++;
    }
    data.startGrowth();
    return gcs;
  }

  @Override
  public void initializeAlgorithm(Algorithm algorithm, String parms) {
    // do not do anything
  }

  @Override
  protected void loadMalletCorpusRepresentation(File directory) {
    corpusRepresentationMallet = CorpusRepresentationMalletTarget.load(directory);
  }
  
    
}
