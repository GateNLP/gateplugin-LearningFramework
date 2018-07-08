/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.mallet;

import cc.mallet.types.Label;
import cc.mallet.types.LabelAlphabet;

/**
 * Attempt to make LabelAlphabet more synchronized.
 * 
 * !!NOTE: currently, this is not used as Mallet Classifier requires the 
 * target alphabet to be LabelAlphabet or a superclasse (???) instead of 
 * LabelAlphabet or a subclass. 
 * See https://github.com/mimno/Mallet/issues/132
 * 
 * @author Johann Petrak
 */
public class LFLabelAlphabet extends LabelAlphabet {

  private static final long serialVersionUID = -5084491342253339406L;
  
  @Override
  public synchronized int lookupIndex(Object entry) {
    return super.lookupIndex(entry);
  }

  @Override
  public synchronized int lookupIndex(Object entry, boolean addifmissing) {
    return super.lookupIndex(entry, addifmissing);
  }

  @Override
  public synchronized Object lookupObject(int index) {
    return super.lookupObject(index);
  }

  @Override
  public synchronized int size() {
    return super.size();
  }

  @Override
  public synchronized Object[] toArray() {
    return super.toArray();
  }

  @Override
  public synchronized Label lookupLabel(Object entry, boolean addifmissing) {
    return super.lookupLabel(entry, addifmissing);
  }
  
  @Override
  public synchronized Label lookupLabel(Object entry) {
    return super.lookupLabel(entry);
  }
  
  @Override
  public synchronized Label lookupLabel(int idx) {
    return super.lookupLabel(idx);
  }
  
}
