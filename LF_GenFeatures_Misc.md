# LF_GenFeatures_Misc Processing Resources

This processing resource helps with the creation of additional features from the original
string of a word / instance annotation.

The processing resources has no init parameters and the following runtime parameters:
* `genWordShape` (Boolean, default false) - if the wordshape feature should get generated
* `genWordShapeShort` (Boolean, default false) - if the short word shape feature should get generated
* `inputASName` (String, default is empty for the default set) - the annotation set that contains the word annotations where to put the generated features
* `instanceType` (String, default Token) - the annotation type for words and where the features will be placed
* `stringFeature` (String, default empty = use document string) - if this is non-empty, then the annotation feature of that name is used to generate the required additional features from

For the wordshape feature, every character in the original word string is mapped to one
of the following:
* any upper case letter is mapped to "A"
* any non-upper case letter is mapped to "a"
* any numeric digit is mapped to "9"
* all other characters are copied

So the wordshape for the word "C6-hydroxylation" is "A9-aaaaaaaaaaaaa"

For the short word shape feature, the same mapping is performed, but multiple
subsequent characters of the same type are all mapped to the same single output characters.

So the short wordshape for the word "C6-hydroxylation" is "A9-a"
