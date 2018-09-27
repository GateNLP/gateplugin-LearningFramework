// pre-filter tokens and put subset into set LDA
// Johann Petrak, 2018-09-27
import gate.Utils;

// remove what we added last time, if any
oldAnns = outputAS.get("TokenWord")
outputAS.removeAll(oldAnns)

for(Annotation ann : inputAS.get("Token")) {
  fm = ann.getFeatures()
  kind = fm.get("kind")
  pick = true
  if(!kind.equals("word")) {
    pick = false
  }
  pos = (String)fm.get("category")
  if(pos.startsWith("V")) {
    pick = false
  }
  if(pick) {
    str = (String)fm.get("string")
    if(str.length() > 1) {
      fm.put("lc_string",str.toLowerCase())
      gate.Utils.addAnn(outputAS, ann, "TokenWord", fm)
    }
  }
}