/*
 * Copyright (c) 2015-2016 The University Of Sheffield.
 *
 * This file is part of gateplugin-LearningFramework 
 * (see https://github.com/GateNLP/gateplugin-LearningFramework).
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 2.1 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this software. If not, see <http://www.gnu.org/licenses/>.
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
