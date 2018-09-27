// Remove all the TokenWord annotations within Unwanted annotations
import gate.Utils

Set toDelete = []

for(Annotation unwanted : inputAS.get("Unwanted")) {
  contained = gate.Utils.getContainedAnnotations(inputAS, unwanted)
  toDelete.addAll(contained)
}

outputAS.removeAll(toDelete)