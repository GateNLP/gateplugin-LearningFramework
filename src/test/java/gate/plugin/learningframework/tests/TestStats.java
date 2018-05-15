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

import gate.plugin.learningframework.stats.Stats;
import gate.plugin.learningframework.stats.StatsForFeatures;
import java.net.MalformedURLException;
import static org.junit.Assert.*;
import org.junit.Test;
import gate.test.GATEPluginTests;

/**
 *
 * @author Johann Petrak
 */
public class TestStats extends GATEPluginTests {
  @Test
  public void testStats1() throws MalformedURLException {
    StatsForFeatures stats = new StatsForFeatures();
    stats.addValue("feature1", 13);
    stats.addValue("feature2", true);
    stats.addValue("feature3", new double[]{1.0,2.0});
    stats.addValue("feature1", 0.0);
    stats.addValue("feature2", true);
    stats.addValue("feature3", new double[]{1.0,2.0,3.0,4.0});
    stats.addValue("feature1", 2);
    stats.addValue("feature2", false);
    stats.addValue("feature3", new double[]{1.0});
    Stats st_feature1 = stats.getStatistics("feature1");
    Stats st_feature2 = stats.getStatistics("feature2");
    Stats st_feature3 = stats.getStatistics("feature3");
    //System.err.println("TestStats/testStats1 Debug: feature1="+st_feature1.toString());   
    //System.err.println("TestStats/testStats1 Debug: feature2="+st_feature2.toString());   
    //System.err.println("TestStats/testStats1 Debug: feature3="+st_feature3.toString());   

    assertEquals(3,st_feature1.getN());
    assertEquals(0.0,st_feature1.getMin(),0.00001);
    assertEquals(13.0,st_feature1.getMax(),0.00001);
    assertEquals(49.0,st_feature1.getVariance(),0.00001);

    assertEquals(3,st_feature2.getN());
    assertEquals(0.0,st_feature2.getMin(),0.00001);
    assertEquals(1.0,st_feature2.getMax(),0.00001);
    assertEquals(0.33333333333333333,st_feature2.getVariance(),0.0001);

    assertEquals(3,st_feature3.getN());
    assertEquals(1.0,st_feature3.getMin(),0.00001);
    assertEquals(4.0,st_feature3.getMax(),0.00001);
    assertEquals(2.33333333333333333,st_feature3.getVariance(),0.0001);
  }
}
