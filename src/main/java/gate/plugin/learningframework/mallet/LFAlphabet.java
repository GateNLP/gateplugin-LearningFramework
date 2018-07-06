/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.mallet;

import cc.mallet.types.Alphabet;

/**
 * Attempt to make the Mallet Alphabet class somewhat synchronized.
 * This naively synchronizes some of the methods (but not all, hopefully
 * all we use in the LF).
 * 
 * @author Johann Petrak
 */
public class LFAlphabet extends Alphabet {

  private static final long serialVersionUID = 3271929926108562395L;
  
  public LFAlphabet() {
    super(); // same
  }
  public LFAlphabet(int capacity) {
    super(capacity); // same
  }
  
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

}
