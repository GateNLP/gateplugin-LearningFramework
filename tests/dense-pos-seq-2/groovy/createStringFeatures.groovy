// create features "string" and "string_lc" for the Token annotations in inputAS
tokens = inputAS.get("Token")
//System.err.println("Tokens: "+tokens.size())
for(Annotation token : tokens) {
  fm = token.getFeatures()
  str = gate.Utils.cleanStringFor(doc,token)
  fm.put("string",str)
  fm.put("string_orig",str)
  fm.put("string_lc",str.toLowerCase(Locale.ENGLISH))
}
