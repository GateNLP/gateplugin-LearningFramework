/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gate.plugin.learningframework.mallet;

import cc.mallet.types.Instance;
import cc.mallet.types.InstanceList;

/**
 * A concurrent replacement for Mallet InstanceList.
 * This tries in a naive way to synchronize some access to the list (but not all).
 * Most importantly the add(Instance) method is synchronized.
 * 
 * @author JohannPetrak
 */
public class LFInstanceList extends InstanceList {
  
  public LFInstanceList(LFPipe pipe) {
    super(pipe);
  }
  
  
  private static final long serialVersionUID = 4320038272253815542L;
  
  @Override
  public synchronized boolean add(Instance instance) {
    return super.add(instance);
  }
}
