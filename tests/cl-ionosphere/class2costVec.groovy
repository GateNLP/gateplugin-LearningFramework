
// Create a feature "costs" from the class of the instance. We simply use 0/1 costs
// for the two classes b and g in that order (index of b=0 and index of g=1)

def mentions = doc.getAnnotations().get("Mention")

for(Annotation mention : mentions) {
  fm=mention.getFeatures()
  cl = fm.get("class")
  costs = new double[2]
  if(cl.equals("b")) {
    costs[0]=0.0
    costs[1]=1.0
  } else {
    costs[0]=1.0
    costs[1]=0.0
  }
  fm.put("costs",costs)
}
