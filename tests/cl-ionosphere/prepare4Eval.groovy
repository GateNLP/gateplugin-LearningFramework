
def mentions = doc.getAnnotations("LearningFramework").get("Mention")

for(Annotation mention : mentions) {
  fm=mention.getFeatures()
  fm.put("class_orig",fm.get("class"))
  fm.put("class",fm.get("LF_class"))
}