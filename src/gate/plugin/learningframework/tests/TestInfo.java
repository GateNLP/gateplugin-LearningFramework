/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework.tests;

import gate.plugin.learningframework.engines.Info;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Johann Petrak
 */
public class TestInfo {
  @Test
  public void testInfo1() {
    Info info = new Info();
    info.trainerClass = "theAlgorithmClass";
    info.engineClass = "theEngineClass";
    info.nrTrainingInstances = 2;
    File directory = new File("/tmp/testInfo");
    directory.mkdir();
    info.save(directory);
    Info info2 = Info.load(directory);
    System.err.println("Info1="+info);
    System.err.println("Info2="+info2);
    assertEquals(info, info2);
  }
}
