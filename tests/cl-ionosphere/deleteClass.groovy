
// Delete the target feature from the instance annotations in the 
// LearningFramework set *before* running application
def mentions = doc.getAnnotations("LearningFramework").get("Mention")

for(Annotation mention : mentions) {
  fm=mention.getFeatures()
  fm.put("class_orig",fm.get("class"))
  fm.remove("class")
}
