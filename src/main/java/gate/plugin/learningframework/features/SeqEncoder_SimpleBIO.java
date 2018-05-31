/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.features;

import gate.Annotation;
import gate.Document;
import gate.Utils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Class for implementing methods to convert sequences to classes and back
 *
 * @author Johann Petrak 
 */
public class SeqEncoder_SimpleBIO extends SeqEncoder {

  
  
  
  // NOTE: currently this is implemented in a way that made it easy to add it
  // to the feature extraction code. However, this approach always only looks at
  // exactly one instance. Not sure if this will work for all we want to implement.
  @Override
  public String seqAnns2ClassLabel(Collection<Annotation> seqAnns, Annotation instAnn, Document curDoc) {
    long instanceStartOffset = Utils.start(instAnn);
    long instanceEndOffset = Utils.end(instAnn);
    List<String> classnames = new ArrayList<>();
    // NOTE: the boundaries of seqAnns may not always align with the boundaries of
    // instAnns (e.g. Organization may start or end in the middle of a token).
    // This can cause a lot of odd situations, e.g. several seqAnns of the same
    // type all within a single instAnn. 
    // Here, we ignore all seqAnns which do NOT  align with the beginning or 
    // end. 
    for (Annotation clann : seqAnns) {
      String clType = clann.getType();
      boolean startsHere = (Utils.start(clann) == instanceStartOffset);
      boolean endsHere = (Utils.end(clann) == instanceEndOffset);
      boolean startsInside = (Utils.start(clann) > instanceStartOffset && Utils.start(clann) < instanceEndOffset);
      boolean endsInside = (Utils.end(clann) > instanceStartOffset && Utils.end(clann) < instanceEndOffset);
      if(startsInside || endsInside) {
        System.err.println("WARNING: class annotation does not align with beginning or end of instance annotation: ignored. In document="+
                curDoc.getName()+" instAnn="+instAnn+" classAnns="+seqAnns);
        continue;
      }
      String code = CODE_INSIDE;
      // possibilities for start/end and corresponding codes:
      // false/false -> I (must be an inside)
      // true/false  -> B (begins here)
      // false/true  -> I (ends here, but we do not distinguish ends here)
      // true/true   -> B (begins here but we do not distinguish begins and ends)      
      if (startsHere) {
        code = CODE_BEGIN;
      }
      String clName = clType + CODESEP + code;
      classnames.add(clName);
    }
    // we have now a list of all the clNames for this location. This now needs 
    // to get sorted in some unique way so we always get the same class label 
    // no matter the random order of annotations.
    // we simply order alphabetically for now
    classnames.sort(Comparator.naturalOrder());
    // now create the actual class label by concatenating all the classnames 
    // into a single class label
    if(classnames.isEmpty()) {
      System.err.println("Warning: no label generated in "+curDoc.getName()+" for instance "+instAnn);
    }
    return String.join(TYPESEP, classnames);
  }  

  /* current algorithm for creating the surrounding annotations:
   * go through the instance annotations in document order:
   *   get the id of the sequence we are in or 0
   *   get the current annotation already open, if any
   *     (this should probably get changed to a list of open annotations)
   *   get the output class feature of the current instance
   *     (this should change to getting the list of type/code combinations
   *   assume we got an O if we got nothing at all 
   *   if there is an open annotation and we get a beginning or outside:
   *     this is the start of a new one, need to complete the current one
   *     NEW: only if the same type is open
   *   if we have a beginning:
   *     start and open a new chunk
   *   if we have an inside: 
   *     continue the current chunk
   * add any hanging annotations for the sequence / document
  */
}
