# LF_GenFeatures_Affixes Processing Resources

This processing resource helps with the creation of features from the
prefix and suffix of words.

The processing resources has no init parameters and the following runtime parameters:
* `genPrefixes` (Boolean, default false) - if features for prefixes should get generated
* `genSuffixes` (Boolean, default false) - if features for suffixes should get generated
* `inputASName` (String, default is empty for the default set) - the annotation set that contains the word annotations where to put the generated features
* `instanceType` (String, default Token) - the annotation type for words and where the features will be placed
* `mapToUpper` (Boolean, default false) - if the prefix or suffix should first get mapped to upper cases
* `mappingLanguage` (String, default "en") - the language code used when `mapToUpper` is true. This influences how the string is mapped to the canonical all-upper-case representation
* `maxPrefixLength` (Integer, default 4) - the maximum length of the prefix to use (if prefix features are generated at all)
* `maxSuffixLength` (Integer, default 4) - the maximum length of the suffix to use (if suffix features are generated at all)
* `minNonPrefixLength` (Integer, default 2) - the minimum number characters that must remain after removing the prefix, otherwise the prefix feature is not generated
* `minNonSuffixLength` (Integer, default 2) - the minimum number characters that must remain after removing the suffix, otherwise the suffix feature is not generated
* `minPrefixLength` (Integer, default 2) - the minimum prefix length for generated prefix features
* `minSuffixLength` (Integer, default 2) - the minimum prefix length for generated suffix features
* `prefixFeatureName` (String, default "pref") - the name pattern to use for prefix features, the final feature name is the concatenation of this and the prefix length
* `suffixFeatureName` (String, default "suf") - the name pattern to use for suffix features, the final feature name is the concatenation of this and the suffix length

Example: if all parameters are left at their default values except:
* `genSuffixes` is `true`
* `minSuffixLength`  is 1
* `maxSuffixLength` is 3
* `mapToUpper` is `true`
* `mappingLanguage` is `de`

Then the following features are generated for the German word "Metermaß":
* `suf1="S"`
* `suf2="SS"`
* `suf3="ASS"`
