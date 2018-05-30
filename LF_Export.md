# LF_Export Processing Resource

The export PR allows you to export data in common formats allowing you to work with your data outside of GATE, in other machine learning software such as Weka. The PR has no init time parameters. The runtime parameters are listed below.

**Note that the user indicates whether they are exporting a classification (or regression) or a chunking problem through the presence or absence of a classAnnotationType. If you want to export a classification or regression problem, you must leave classAnnotationType blank.**

* `algorithmParameters` - This runtime parameter is a catch-all for various currently undocumented switches that don't warrant their own parameter. You can leave it blank.
* `classAnnotationType` - For exporting chunking sets, what is the type of the annotation indicating the chunk you are learning? For example, "Person" or "Organization". Leave blank to indicate that you are exporting a classification set.
* `dataDirectory` - Where do you want to save your output dataset to?
* `exporter`
* `featureSpecURL` - The feature spec; see training PRs for more information
* `inputASName` - Input annotation set containing attributes
* `instanceType` - Annotation type to use as instance
* `scaleFeatures` - Feature scaling can be performed prior to exporting the data if you wish
* `sequenceSpan` - Not currently supported; exporting sequence tagging datasets isn't supported at all at present
* `targetFeature` - For classification, in which feature of the instance is the class? Leave blank to indicate that you are exporting a chunking problem.
* `targetType` - Having indicated that you are exporting a classification problem, by indicating a targetFeature and leaving classAnnotationType blank, you should select numeric for a regression target or nominal for a regular classification problem.
