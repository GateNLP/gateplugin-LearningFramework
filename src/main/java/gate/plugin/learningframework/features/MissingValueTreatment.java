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

package gate.plugin.learningframework.features;

/**
 * How to treat/represent missing values when creating the instances.
 *
 * "keep" tries to preserve the missing value for the learning algorithm. This is done by
 * representing the missing value as a value that is not otherwise used: for one_of_k
 * representations, all k features are set to 0.0, for numeric representations of a nominal value,
 * -1 is used, for numeric features, NaN is used and for boolean 0.5 is used. NOTE: this will only
 * work if the algorithm supports missing values in some way!
 *
 * "special_value" replaces the missing value with a special value that should be different from
 * all other values, but still can be handled by algorithms which do not support missing values.
 * This is not really always possible, but the values used are a hopefully good compromise: for
 * nominal features, a special nominal value is used, for numeric values "-1.0" is used and for
 * boolean "0.5" is used if the boolean is represented as a number, otherwise false is used.
 *
 * "zero_value" is similar to "special_value" but uses the "zero" value for the datatype, 
 * false for boolean and 0.0 for numeric. For nominal values which are coded numerically, 
 * the MV string is used, for nominal values which are coded one-of-k this is the 
 * same as "keep" and no feature is set.
 *
 * "ignore_instance" records the fact that a missing value is present in the instance and filters
 * the instance. This means that the instance is not used for training and at application time,
 * that no classification is performed for it.
 *
 * "impute_mostfreq" initially uses "keep" but makes another pass over all instances at training
 * time and replaces the value with the most frequent value. This may not be a good idea for
 * truely continuous numeric features. The same value is then also used at application time.
 *
 * "impute_median" initially uses "keep" but makes another pass over all isntances at training
 * time and replaces the value with the median value. At application time, that median value is
 * also used.
 *
 * "use_value" uses the value giving in the element MISSINGVALUEVALUE for the attribute.
 *
 * NOTE: not all treatments are yet implemented!!!!
 *
 */
public enum MissingValueTreatment {
  ignore_instance, keep, special_value, zero_value, impute_mostfreq, impute_median, use_value
  
}
