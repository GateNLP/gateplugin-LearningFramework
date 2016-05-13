/*
 * Copyright (c) 1995-2015, The University of Sheffield. See the file
 * COPYRIGHT.txt in the software or at http://gate.ac.uk/gate/COPYRIGHT.txt
 * Copyright 2015 South London and Maudsley NHS Trust and King's College London
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free software,
 * licenced under the GNU Library General Public License, Version 2, June 1991
 * (in the distribution as file licence.html, and also available at
 * http://gate.ac.uk/gate/licence.html).
 */
package gate.plugin.learningframework;


import org.apache.log4j.Logger;

import gate.Controller;
import gate.Document;
import gate.Resource;
import gate.creole.ResourceInstantiationException;
import gate.creole.ExecutionException;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;

/**
 * Base class for all LearningFramework PRs providing the shared parameters.
 */
public abstract class LearningFrameworkPRBase
        extends AbstractDocumentProcessor
  {

  /**
   *
   */
  private Logger logger = Logger.getLogger(LearningFrameworkPRBase.class.getCanonicalName());

  // =================================================================
  // Creole Parameters for all the PRs that derive from this class
  // =================================================================
  protected String inputASName;

  @RunTime
  @Optional
  @CreoleParameter
  public void setInputASName(String iasn) {
    this.inputASName = iasn;
  }

  public String getInputASName() {
    return this.inputASName;
  }

  protected String instanceType;

  @RunTime
  @CreoleParameter(defaultValue = "Token", comment = "The annotation type to "
          + "be treated as instance.")
  public void setInstanceType(String inst) {
    this.instanceType = inst;
  }

  public String getInstanceType() {
    return this.instanceType;
  }

  protected Mode mode;

  protected String algorithmParameters;
  protected boolean algorithmParamtersChanged = true;

  @RunTime
  @Optional
  @CreoleParameter(comment = "Some of the learners take parameters. Parameters "
          + "can be entered here. For example, the LibSVM supports parameters.")
  public void setAlgorithmParameters(String learnerParams) {
    this.algorithmParameters = learnerParams;
    algorithmParamtersChanged = true;
  }

  public String getAlgorithmParameters() {
    return this.algorithmParameters;
  }
  
  public boolean getAlgorithmParametersIsChanged() {
    boolean tmp = algorithmParamtersChanged;
    algorithmParamtersChanged = false;
    return tmp;
  }
}
