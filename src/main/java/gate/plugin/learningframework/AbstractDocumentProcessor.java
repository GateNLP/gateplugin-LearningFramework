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

import java.io.Serializable;

import org.apache.log4j.Logger;

import gate.Controller;
import gate.Document;
import gate.Resource;
import gate.creole.ControllerAwarePR;
import gate.creole.ResourceInstantiationException;
import gate.creole.AbstractLanguageAnalyser;
import gate.creole.ExecutionException;
//import java.util.Optional;

/**
 * Abstract base class for all the PRs in this plugin.
 * 
 */
@SuppressWarnings("serial")
public abstract class AbstractDocumentProcessor
        extends AbstractLanguageAnalyser
        implements  ControllerAwarePR {

  private int seenDocuments = 0;

  protected Controller controller;

  protected Throwable throwable;

  //===============================================================================
  // Implementation of the relevant API methods for DocumentProcessors. These
  // get inherited by the implementing class. This also defines abstract methods 
  // that make it easier to handle the control flow:
  // void process(Document doc) - replaces void execute()
  // void beforeFirstDocument(Controller) - called before the first document is processed
  //     (not called if there were no documents in the corpus, for example)
  // void afterLastDocument(Controller, Throwable) - called after the last document was processed
  //     (not called if there were no documents in the corpus). If Throwable is
  //     not null, processing stopped because of an exception.
  // void finishedNoDocument(Controller, Throwable) - called when processing 
  //     finishes and no documents were processed. If Throwable is not null,
  //     processing finished because of an error.
  //================================================================================
  @Override
  public Resource init() throws ResourceInstantiationException {
    return this;
  }

  @Override
  public void execute() throws ExecutionException {
    if (seenDocuments == 0) {
      beforeFirstDocument(controller);
    }
    seenDocuments += 1;
    process(getDocument());
  }

  @Override
  public void controllerExecutionAborted(Controller arg0, Throwable arg1)
          throws ExecutionException {
    // reset the flags for the next time the controller is run
    controller = arg0;
    throwable = arg1;
    if (seenDocuments > 0) {
      afterLastDocument(arg0, arg1);
    } else {
      finishedNoDocument(arg0, arg1);
    }
  }

  @Override
  public void controllerExecutionFinished(Controller arg0)
          throws ExecutionException {
    controller = arg0;
    if (seenDocuments > 0) {
      afterLastDocument(arg0, null);
    } else {
      finishedNoDocument(arg0, null);
    }
  }

  @Override
  public void controllerExecutionStarted(Controller arg0)
          throws ExecutionException {
    controller = arg0;
    seenDocuments = 0;
  }

  //=====================================================================
  // New simplified API for the child classes 
  //=====================================================================
  
  // NOTE: not sure which of these should be abstract (and thus force 
  // the programmer to implement them even if empty) and which should be
  // pre-implemented to do nothing. 
  
  /**
   * The new method to implement by PRs which derive from this class.
   * This must return a document which will usually be the same object
   * as it was passed.
   * NOTE: in the future the better option here may be to return 
   * Optional of Document or even List of Document. That way downstream
   * PRs could be made to not process filtered documents and to process
   * additional generated documents. 
   * 
   * @param document  the document
   * @return document
   */
  protected abstract Document process(Document document);

  /**
   * This can be overridden in PRs and will be run once before
   * the first document seen. 
   * This method is not called if no documents are processed at all. 
   * @param ctrl  controller
   */
  protected abstract void beforeFirstDocument(Controller ctrl);

  /**
   * This can be overridden in PRs and will be run after processing has started.
   * This will run once before any document is processed and before the method
   * beforeFirstDocument is invoked, even if no document is being processed at all.
   * 
   * @param ctrl the controller
   */
  protected void processingStarted(Controller ctrl) { };
  
  /**
   * Handler for processing after last document
   * @param ctrl the controller
   * @param t any throwable 
   */
  protected abstract void afterLastDocument(Controller ctrl, Throwable t);

  /**
   * Handler for processing after finishing without any document
   * @param ctrl the controller
   * @param t any throwable
   */
  protected abstract void finishedNoDocument(Controller ctrl, Throwable t);
  
}
