/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.tests;

import gate.plugin.learningframework.engines.Parms;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 * Tester for the Parms class.
 * @author Johann Petrak
 */
public class TestParms {
  @Test
  public void testParms1() {
    Parms ps = new Parms("-toIgnore -maxDepth 3 -prune ", "m:maxDepth:i", "p:prune:b", "x:xoxo:d");
    assertEquals(3,ps.size());
    assertEquals(3,ps.getValue("maxDepth"));
    assertEquals(true,ps.getValue("prune"));
    assertEquals(2.0,(double)ps.getValueOrElse("xoxo",2.0),0.001);
  }
}
