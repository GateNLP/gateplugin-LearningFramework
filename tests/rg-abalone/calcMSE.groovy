
// Delete the target feature from the instance annotations in the 
// LearningFramework set *before* running application
def mentions = doc.getAnnotations("LearningFramework").get("Mention")

n=0
s=0.0
for(Annotation mention : mentions) {
  fm=mention.getFeatures()
  o=Double.parseDouble(fm.get("target_orig").toString())
  n=Double.parseDouble(fm.get("target").toString())
  n++
  s = s + (o-n)*(o-n)
}

mse = s / n
rmse = Math.sqrt(mse)

System.out.println("MSE="+mse+", RMSE="+rmse)
