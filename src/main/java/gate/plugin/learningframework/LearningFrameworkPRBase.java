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

import org.jpetrak.gate8.api.plugins.AbstractDocumentProcessor;
import gate.creole.metadata.CreoleParameter;
import gate.creole.metadata.Optional;
import gate.creole.metadata.RunTime;

/**
 * Base class for all LearningFramework PRs providing the shared parameters.
 */
@SuppressWarnings("serial")
public abstract class LearningFrameworkPRBase
        extends AbstractDocumentProcessor {

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


  protected String algorithmParameters = "";
  protected boolean algorithmParamtersChanged = true;

  @RunTime
  @Optional
  @CreoleParameter(comment = "Some of the learners take parameters. Parameters "
          + "can be entered here. For example, the LibSVM supports parameters.", defaultValue = "")
  public void setAlgorithmParameters(String learnerParams) {
    if(learnerParams == null) {
      learnerParams = "";
    }
    if(learnerParams.equals(this.algorithmParameters)) {
      // do nothing
    } else {
      algorithmParamtersChanged = true;
      this.algorithmParameters = learnerParams;
    }
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
