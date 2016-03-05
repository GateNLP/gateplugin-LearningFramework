/*
 * Algorithm.java
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

public enum Algorithm {
  LIBSVM,
  MALLET_CL_C45,
  MALLET_CL_DECISION_TREE,
  MALLET_CL_MAX_ENT,
  MALLET_CL_NAIVE_BAYES_EM,
  MALLET_CL_NAIVE_BAYES,
  MALLET_CL_WINNOW,
  MALLET_SEQ_CRF,
  WEKA_CL_NAIVE_BAYES,
  WEKA_CL_JRIP,
  WEKA_CL_J48,
  WEKA_CL_MULTILAYER_PERCEPTRON,
  WEKA_CL_RANDOM_TREE,
  WEKA_CL_IBK,
  WEKA_CL_LOGISTIC_REGRESSION,
  WEKA_CL_RANDOM_FOREST,
  WEKA_CL_NUM_ADDITIVE_REGRESSION
}
