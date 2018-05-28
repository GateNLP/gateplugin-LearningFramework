# Vectors as feature values

The LearningFramework can make use of annotation features for numeric attributes which contain an array of double values or an Iterable of objects that can be converted to double. In that case, instead of mapping the annoation feature to one dimension (learning feature) in the feature vector, there will be as many dimensions as there are elements in the array or Iterable. The name of these dimensions is composed of the attribute name plus the element number. Only element values which are not 0.0 are actually represented in the feature vector. 

This is useful if there are already pre-calculated vectors for instances: with this mechanism these vectors can get directly "spliced" into the final feature vector. One application for this is pre-calculated dense vectors for embeddings. 

NOTE: currently there is no support for making use of pre-calculated sparse vectors. 