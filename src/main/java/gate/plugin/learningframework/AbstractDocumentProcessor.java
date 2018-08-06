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

import org.apache.log4j.Logger;

import gate.Controller;
import gate.Document;
import gate.Resource;
import gate.creole.ControllerAwarePR;
import gate.creole.ResourceInstantiationException;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
import gate.creole.metadata.Sharable;
import gate.util.Benchmark;
import gate.util.Benchmarkable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Abstract base class for all the PRs in this plugin.
 */
// The inheriting class should define a serverVersionUID
@SuppressWarnings("serial")
public abstract class AbstractDocumentProcessor
        extends AbstractLanguageAnalyser
        implements ControllerAwarePR, Benchmarkable {

  /**
   *
   */
  private final Logger LOGGER = 
          Logger.getLogger(AbstractDocumentProcessor.class.getCanonicalName());

  
  // This will be shared between all duplicates
  protected AtomicInteger seenDocuments = null;

  @Sharable
  public void setSeenDocuments(AtomicInteger n) {
    seenDocuments = n;
  }
  
  public AtomicInteger getSeenDocuments() {
    return seenDocuments;
  }
  
  
  public AtomicInteger seenDocumentsThisDuplicate;
  
  protected Controller controller;
  
  protected static final Object SYNC_OBJECT = new Object();
  
  // because the setter for this is marked @Sharable, all duplicates will hold 
  // the same reference after initialisation. This is updated in init() and remains 
  // forever. This is the actual number of duplicates (1-based, not 0-based)
  protected AtomicInteger nDuplicates = null;
  
  @Sharable
  public void setNDuplicates(AtomicInteger n) {
    nDuplicates = n;
  }
  
  public AtomicInteger getNDuplicates() {
    return nDuplicates;
  }
  
  
  // the following shared counter is used when processing starts to find out which invocation 
  // of the controller started method is the last one, and when processing finishes to figure out which
  // invocation of controller finished/aborted is the last one. The counter gets incremented
  // for each controller started and decremented for each finished/aborted.
  // During execution the counter should hold the actual number of running duplicates and should
  // be equal to nDuplicates
  protected AtomicInteger remainingDuplicates = null;
  
  @Sharable
  public void setRemainingDuplicates(AtomicInteger n) {
    remainingDuplicates = n;
  }
  
  public AtomicInteger getRemainingDuplicates() {
    return remainingDuplicates;
  }
  
  protected Throwable lastError = null;
  
  @Sharable
  public void setLastError(Throwable x) {
    lastError = x;
  }
  
  public Throwable getLastError() {
    return lastError;
  }
      
  protected ConcurrentHashMap<String,Object> sharedData = null;
  
  @Sharable
  public void setSharedData(ConcurrentHashMap<String,Object> v) {
    sharedData = v;
  }
  public ConcurrentHashMap<String,Object> getSharedData() {
    return sharedData;
  }
  
  protected Object syncObject = null;
         
  @Sharable
  public void setSyncObject(Object val) {
    syncObject = val;
  }
  public Object getSyncObject() {
    return syncObject;
  }
  
  
         
  
  // Each duplicate holds its own duplicate id after initialisation.
  // The duplicate id is 0-based, not 1-based, so the first duplicate has id 0 and 
  // the last nDuplicates-1
  protected int duplicateId = 0;
  public int getDuplicateId() {
    return duplicateId;
  }

  //===============================================================================
  // Implementation of the relevant API methods for DocumentProcessors. These
  // get inherited by the implementing class. This also defines abstract methods 
  // that make it easier to handle the control flow:
  // void process(Document doc) - replaces void execute(): process the Document
  // void controllerStarted(Controller) - replaces controllerExecutionStarted(Controller)
  // void controllerFinished(Controller, Throwable) - replaces 
  //     controllerExecutionFinished and controllerExecutionAborted
  // int getSeenDocuments().get() - returns the current number of documents
  //     for which processing has been started
  // int seenDocumentsThisDuplicate.get() - the documents seen by only this duplicate
  // int getDuplicateId() - returns the duplicate number for the current duplicate.
  //     this returns 0 for the instance for which init() was invoked first, 
  //     usually the template other duplicates where cloned from.
  // int getNDuplicates().get() - returns the current number of duplicates 
  //     that exist. 
  //================================================================================
  @Override
  public Resource init() throws ResourceInstantiationException {
    // we always provide the following shared fields to all PRs which are used for duplicated PRs:
    // nDuplicates is an AtomicInt which gets incremented whenever a resource
    // gets duplicated. seenDocuments is an AtomicInt that contains the number
    // of documents for which processing was started already. 
    // syncObject is an Object used for synchronizing between threads 
    // that run duplicates.
    // sharedData is a ConcurrentHashMap that contains any
    // other shared data.
    
    // NOTE: this piece of code does not need to get synchronized since we 
    // always expect duplication to happen in a single thread, one after the
    // other. Usuall, all duplicates will get created from the same first
    // created instance, but we do not rely on that.
    seenDocumentsThisDuplicate = new AtomicInteger(0);
    if(getNDuplicates() == null || getNDuplicates().get() == 0) {    
      LOGGER.debug("DEBUG: creating first instance of PR "+this.getName());
      setNDuplicates(new AtomicInteger(1));
      duplicateId = 0;
      System.err.println("DEBUG: "+this.getName()+" init() for first instance, duplicateId="+duplicateId);
      setSharedData(new ConcurrentHashMap<>());
      setSeenDocuments(new AtomicInteger(0));
      setRemainingDuplicates(new AtomicInteger(0));
      setSyncObject(new Object());
      LOGGER.debug("DEBUG: "+this.getName()+" created duplicate "+duplicateId);
    } else {
      int thisn = getNDuplicates().getAndAdd(1);
      duplicateId = thisn;
      System.err.println("DEBUG: "+this.getName()+" init() for non-first instance, duplicateId="+duplicateId);
      LOGGER.debug("DEBUG: created duplicate "+duplicateId+" of PR "+this.getName());
    }
    return this;
  }

  @Override
  public void execute() throws ExecutionException {
    // The document counting happens in this synchronized code block.
    // We could probably also use volatile Integer for the counting.
    synchronized (getSyncObject()) {
      seenDocumentsThisDuplicate.incrementAndGet();
      getSeenDocuments().incrementAndGet();
    }
    // actual processing happens in parallel if there are duplicates
    process(getDocument());
  }
  
  /**
   * Handle the controller execution aborted callback.
   * 
   * This does very much the same as the controller execution finished callback
   * but also stores the last Throwable so it can be inspected by the PR.
   * @param arg0 controller invoking the callback
   * @param arg1 throwable representing the error that was encountered
   * @throws ExecutionException 
   */
  @Override
  public void controllerExecutionAborted(Controller arg0, Throwable arg1)
          throws ExecutionException {
    // reset the flags for the next time the controller is run
    controller = arg0;
    setLastError(arg1);
    LOGGER.error("Controller ended with error "+arg1.getMessage());
    int tmp = getRemainingDuplicates().decrementAndGet();
    LOGGER.debug("DEBUG "+this.getName()+" controllerExecutionAborted invocation "+tmp+" for duplicate "+duplicateId);
    System.err.println("DEBUG: "+this.getName()+" controllerExecutionAborted, duplicateId="+duplicateId+" remaining="+tmp);
    // Assert.assertEquals(tmp, duplicateId);
    
    controllerFinished(arg0, arg1);
  }

  @Override
  public void controllerExecutionFinished(Controller arg0)
          throws ExecutionException {
    controller = arg0;
    int tmp = getRemainingDuplicates().decrementAndGet();
    LOGGER.debug(this.getName()+": controllerExecutionFinished invocation "+tmp+" for duplicate "+duplicateId);
    System.err.println("DEBUG: "+this.getName()+" controllerExecutionFinished, duplicateId="+duplicateId+" remaining="+tmp);
    // Assert.assertEquals(tmp, duplicateId);
    
    controllerFinished(arg0, null);
  }

  @Override
  public void controllerExecutionStarted(Controller arg0)
          throws ExecutionException {
    controller = arg0;
    seenDocumentsThisDuplicate.set(0);
    // we count up to the number of duplicates we have. The first invocation of this is also
    // responsible for resetting the document counter (it needs to be the first because 
    // at any later time, another duplicate could already have their execute method invoked 
    int tmp = getRemainingDuplicates().getAndIncrement();
    if(tmp==0) {
      LOGGER.debug(this.getName()+": First controllerExecutionStarted invocation, resetting error and doc count in duplicate "+duplicateId);
      setLastError(null);
      getSeenDocuments().set(0);
    } else {
      LOGGER.debug(this.getName()+": controllerExecutionStarted invocation number "+tmp+" in duplicate "+duplicateId);
    }
    // just for checking that our assumption is right that invocation happens
    // in the order the duplicate was originally created in GCP.
    System.err.println("DEBUG: "+this.getName()+" controllerExecutionStarted, duplicateId="+duplicateId+" remaining="+tmp);
    // Assert.assertEquals(tmp, duplicateId);
    
    controllerStarted(arg0);
  }
  

  //=====================================================================
  // New simplified API for the child classes 
  //=====================================================================
  
  // NOTE: not sure which of these should be abstract (and thus force 
  // the programmer to implement them even if empty) and which should be
  // pre-implemented to do nothing. 
  
  /**
   * The new method to implement by PRs which derive from this class.
   * 
   * @param document  the document to get processed
   * 
   */
  protected abstract void process(Document document);

  /**
   * Callback for when each controller gets started on a corpus.
   * This method gets called once when processing starts for a controller.
   * This replaces the controllerExecutionStarted callback which must not be
   * overridden! 
   * Note that controllerStarted gets invoked for each duplicate
   * in sequence, without any concurrency and that (in the case of GCP at least)
   * the order of invocation should agree with the order of creation, so it
   * should match the duplicateId assigned to each instance.
   * 
   * @param ctrl the controller instance
   */
  public abstract void controllerStarted(Controller ctrl);

  /**
   * Callback for when each controller finishes on a corpus.
   * This method gets called once when processing finishes for a controller.
   * This replaces the controllerExecutionFinished and controllerExecutionAborted
   * callbacks which must not be overridden! 
   * If the execution of a controller had an error, the Throwable is non-null,
   * otherwise a null Throwable indicates normal completion.
   * Note that controllerFinished gets invoked for each duplicate
   * in sequence, without any concurrency and that (in the case of GCP at least)
   * the order of invocation should agree with the order of creation, so it
   * should match the duplicateId assigned to each instance.
   * 
   * @param ctrl the controller instance
     * @param thrw the Throwable indicating the error or null for normal completion
   */
  public abstract void controllerFinished(Controller ctrl, Throwable thrw);


  
  
  protected void benchmarkCheckpoint(long startTime, String name) {
    if (Benchmark.isBenchmarkingEnabled()) {
      Benchmark.checkPointWithDuration(
              Benchmark.startPoint() - startTime,
              Benchmark.createBenchmarkId(name, this.getBenchmarkId()),
              this, null);
    }
  }

  @Override
  public String getBenchmarkId() {
    return benchmarkId;
  }

  @Override
  public void setBenchmarkId(String string) {
    benchmarkId = string;
  }
  private String benchmarkId = this.getName();

}
