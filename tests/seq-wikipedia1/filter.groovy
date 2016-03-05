// Filter all Token and Sentence annotations from the default set which are not contained 
// in a p in the Original annotations set
// Also filter the Link annotations we have just created from the Key and default annotation sets

import java.util.HashSet

def types = new HashSet<String>()
types.add("Sentence")
types.add("Token")
types.add("Link")
types.add("Person")
types.add("Location")
types.add("Organization")

def anns_all = doc.getAnnotations().get(types)

def keys_all = doc.getAnnotations("Key").get("Link")

def anns_remove = new HashSet<Annotation>()
def anns_key_remove = new HashSet<Annotation>()

anns_remove.addAll(anns_all)
anns_key_remove.addAll(keys_all)

def anns_p = doc.getAnnotations("Original markups").get("p")

for(Annotation ann_p : anns_p) {
  def overlappings = gate.Utils.getOverlappingAnnotations(anns_all,ann_p)
  // remove the overlapping ones from the to-remove set
  anns_remove.removeAll(overlappings)
  overlappings = gate.Utils.getOverlappingAnnotations(keys_all,ann_p)
  anns_key_remove.removeAll(overlappings)  
}

// now we should be left with all the annotations which do not overlap with any p
// these get now removed from the default AS
doc.getAnnotations().removeAll(anns_remove)
doc.getAnnotations("Key").removeAll(anns_key_remove)