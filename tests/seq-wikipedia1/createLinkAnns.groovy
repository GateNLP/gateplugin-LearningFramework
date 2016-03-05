// Create Link annotations in the default set and the Key set from
// all the a annotations in the original markup set which refer to 
// another wiki page. We use annotations where the title feature is present
// and non-empty to indicate this

def anns_a = doc.getAnnotations("Original markups").get("a")

for(Annotation ann_a : anns_a) {
  def fm = ann_a.getFeatures()
  def t = fm.get("title")
  if(t != null && !t.trim().isEmpty()) {
    gate.Utils.addAnn(doc.getAnnotations(),ann_a,"Link",gate.Factory.newFeatureMap())
    gate.Utils.addAnn(doc.getAnnotations("Key"),ann_a,"Link",gate.Factory.newFeatureMap())
  }
}

