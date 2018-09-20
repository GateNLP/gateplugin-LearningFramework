
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

import gate.Annotation;
import gate.AnnotationSet;
import java.net.URL;

import org.apache.log4j.Logger;

import gate.Controller;
import gate.Document;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.CreoleResource;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;
import gate.plugin.learningframework.export.ExporterText;
import gate.util.GateRuntimeException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Very simple PR to export text only in ad-hoc formats.
 * Currently, this cannot be used with GCP and only contains ad-hoc implementations.
 * 
 */
@CreoleResource(
        name = "LF_ExportText",
        helpURL = "https://gatenlp.github.io/gateplugin-LearningFramework/LF_ExportText",
        comment = "Simple exporter for text-only formats, no fancy features")
public class LF_ExportText extends AbstractDocumentProcessor {

  private static final long serialVersionUID = 2455930612239919114L;


  private transient final Logger logger = Logger.getLogger(LF_ExportText.class.getCanonicalName());

  protected URL dataDirectory;
  { 
    try {
      dataDirectory = new File(".").getCanonicalFile().toURI().toURL();
    } catch (IOException ex) {
      throw new GateRuntimeException("Could not create URL for current directory to use as a default for dataDirectory",ex);
    }
  }
  
  protected String inputASName;

  @RunTime
  @Optional
  @CreoleParameter(comment = "Input annotation set, empty means default set", defaultValue="")
  public void setInputASName(String iasn) {
    this.inputASName = iasn;
  }

  public String getInputASName() {
    return this.inputASName;
  }

  

  @RunTime
  @Optional
  @CreoleParameter(comment = "The directory where the exported data file or files will be stored")
  public void setDataDirectory(URL output) {
    dataDirectory = output;
  }

  public URL getDataDirectory() {
    return this.dataDirectory;
  }

  protected String containingAnnotation = "";
  @RunTime
  @Optional
  @CreoleParameter(comment = "If specified, each containing annotation identifies a document or sequence")
  public void setContainingAnnotation(String val) {
    this.containingAnnotation = val;
  }
  public String getContainingAnnotation() {
    return this.containingAnnotation;
  }

  protected String tokenAnnotation = "";
  @RunTime
  @Optional
  @CreoleParameter(comment = "The annotation where the text comes from, if not specified, document content", 
          defaultValue="Token")
  public void setTokenAnnotation(String val) {
    this.tokenAnnotation = val;
  }
  public String getTokenAnnotation() {
    return this.tokenAnnotation;
  }

  protected Boolean includeDocumentId = false;
  @RunTime
  @Optional
  @CreoleParameter(comment = "If true, the first field is a document/sequence name/id",
          defaultValue="false")
  public void setIncludeDocumentId(Boolean val) {
    this.includeDocumentId = val;
  }
  public Boolean getIncludeDocumentId() {
    return this.includeDocumentId;
  }
  
  
  protected String tokenFeature;
  @RunTime
  @Optional
  @CreoleParameter(comment = "If specified, use feature, otherwise covered document", defaultValue="string")
  public void setTokenFeature(String val) {
    this.tokenFeature = val;
  }
  public String getTokenFeature() {
    return this.tokenFeature;
  }
  
  
  protected String targetFeature;
  @RunTime
  @Optional
  @CreoleParameter(comment = "If specified, export a target field, otherwise, text only", defaultValue="")
  public void setTargetFeature(String classFeature) {
    this.targetFeature = classFeature;
  }
  public String getTargetFeature() {
    return this.targetFeature;
  }


  
  
  @RunTime
  @CreoleParameter(comment = "Export format, some formats allow finer configuration via the algorithmParameters")
  public void setExporter(ExporterText value) {
    this.exporter = value;
  }

  public ExporterText getExporter() {
    return exporter;
  }
  private int ndocs;
  private int nwritten;

  private ExporterText exporter;
  
  private transient PrintWriter pw;
  
  private final String tokenDelimiter = " ";
  private final String fieldDelimiter = "\t";
  
  // ----------------------------------------------------------------------------
  protected void processOne(long from, long to, Annotation containing, AnnotationSet inputAS, Document doc) {
    String text;
    // first print the field containing the id, if needed
    if(getIncludeDocumentId()) {
      pw.print(doc.getName());
      pw.print("|");
      pw.print(from);
      pw.print(fieldDelimiter);
    }
    
    
    if(getTokenAnnotation()!= null && !tokenAnnotation.isEmpty()) {
      // get the text from those annotations
      StringBuilder sb = new StringBuilder();
      boolean first = true;
      AnnotationSet allTokens = inputAS.get(getTokenAnnotation());
      List<Annotation> tokenAnnots = allTokens.getContained(from, to).inDocumentOrder();
      for(Annotation tokenAnnot : tokenAnnots) {
        // if we have a feature, get the text from the feature, otherwise from document 
        if(getTokenFeature()!=null && !getTokenFeature().isEmpty()) {
          text = (String)tokenAnnot.getFeatures().get(getTokenFeature());
        } else {
          text = gate.Utils.cleanStringFor(doc, tokenAnnot);
        }
        if(!first) {
          sb.append(tokenDelimiter);
        } else {
          first = false;
        }
        sb.append(text);
      }
      text = sb.toString();
    } else {
      text = gate.Utils.cleanStringFor(doc, from, to);
    }
    pw.print(text);
    // if we need to print a target
    if(getTargetFeature()!=null && !getTargetFeature().isEmpty()) {
      pw.print(fieldDelimiter);
      Object val  = containing.getFeatures().get(getTargetFeature());
      if(val!=null) {
        pw.print(val.toString());
      }
    }
    pw.println();
    nwritten += 1;
  }
  
  
  @Override
  public void process(Document doc) {
    // extract the required annotation sets,
    
    // TODO: this whole section has to move into its own class and be made
    // multithreading capable!
    AnnotationSet inputAS = doc.getAnnotations(getInputASName());
    // if a containing annotation type is specified, get all the annotations in 
    // document order. 
    if(getContainingAnnotation()!=null && !getContainingAnnotation().isEmpty()) {
      List<Annotation> containings = inputAS.get(getContainingAnnotation()).inDocumentOrder();
      for(Annotation containing : containings) {
        processOne(gate.Utils.start(containing), gate.Utils.end(containing), containing, inputAS, doc);
      }
    } else {
      processOne(0L, doc.getContent().size(), null, inputAS, doc);
    }
    ndocs += 1;
  }

  @Override
  public void controllerStarted(Controller controller) {

    if(getExporter() == null) {
      throw new GateRuntimeException("Exporter parameter is null");
    }
    if(getTargetFeature()!=null && !getTargetFeature().isEmpty()) {
      if(getContainingAnnotation()==null || getContainingAnnotation().isEmpty()) {
        throw new GateRuntimeException("Target feature can only be used if containing annotation is specified");
      }
    }
    File outFile = new File(gate.util.Files.fileFromURL(dataDirectory),"exportText.txt");
    try {
      pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(outFile), "UTF-8"));
    } catch (FileNotFoundException|UnsupportedEncodingException ex) {
      throw new GateRuntimeException("Could not open file for writing: "+outFile,ex);
    }
    nwritten = 0;
    ndocs = 0;
  }

  @Override
  public void controllerFinished(Controller arg0, Throwable t) {
    if(pw != null) {
      pw.close();
    }
    System.out.println("Number of documents processed: "+ndocs);
    System.out.println("Number of documents/sequences written: "+nwritten);
  }


}
