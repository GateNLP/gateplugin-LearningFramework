
// convert the class label  "0" or "1" which we get from the model that
// was created using the costs vectors, back to the actual label "b" or "g"

def mentions = doc.getAnnotations("LearningFramework").get("Mention")

for(Annotation mention : mentions) {
  fm=mention.getFeatures()
  cl = fm.get("class")
  if(cl.equals("0")) {
    cl="b"
  } else {
    cl="g"
  }
  fm.put("class",cl)
}
