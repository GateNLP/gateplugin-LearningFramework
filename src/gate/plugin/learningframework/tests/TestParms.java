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
