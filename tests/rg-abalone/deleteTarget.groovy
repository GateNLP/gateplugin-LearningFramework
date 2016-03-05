
// Delete the target feature from the instance annotations in the 
// LearningFramework set *before* running application
def mentions = doc.getAnnotations("LearningFramework").get("Mention")

for(Annotation mention : mentions) {
  fm=mention.getFeatures()
  fm.put("target_orig",fm.get("target"))
  fm.remove("target")
}
