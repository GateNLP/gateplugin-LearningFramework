package gate.plugin.learningframework.stats;

import gate.util.GateRuntimeException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.SummaryStatistics;

/**
 * Simple wrapper around either SummaryStatistics or our own thing for strings.
 * 
 * The kind of backing object is determined at construction time when 
 * the (first) object to be added already needs to get passed.
 * 
 * @author Johann Petrak <johann.petrak@gmail.com>
 */
public class Stats {
  SummaryStatistics numStats;
  Map<String,Long> stringStats;
  long n = 0;
  public Stats(Object firstValue) {
    if(firstValue instanceof String) {
      stringStats = new HashMap<String,Long>();
    } else {
      numStats = new SummaryStatistics();
    }
  }
  
  public void addValue(Object value) {
      if(value instanceof String) {
        if(stringStats == null) throw new GateRuntimeException("Stats object did not expect a String");
        String val = (String)value;
        if(stringStats.containsKey(val)) {
          stringStats.put(val,stringStats.get(val)+1);
        } else {
          stringStats.put(val,1l);
        }
      } else {
        if(numStats == null) throw new GateRuntimeException("Stats object did not expect a non-String");
      if(value instanceof Double) {
        numStats.addValue((Double)value);
      } else if(value instanceof Number) {
         numStats.addValue(((Number) value).doubleValue());
      } else if(value instanceof String) {
        // none for now     
      } else if(value instanceof Boolean) {
        numStats.addValue(((Boolean)value) ? 1.0 : 0.0);
      } else if(value instanceof List) {
        numStats.addValue(((List)value).size());
      } else if(value instanceof String[]) {
        numStats.addValue(((double[])value).length);
      } else if(value instanceof double[]) {
        numStats.addValue(((double[])value).length);
      } else {
        throw new GateRuntimeException("Cannot calculate statistics for objects of type "+value.getClass());
      }
      }
    n += 1;
  }
  
  public long getN() {
    return n;
  }
  
  public boolean isString() {
    return stringStats != null;
  }
  
  /**
   * Number of different values, only if isString() is true, exception otherwise.
   */
  public int nrValues() {
    if(stringStats==null) throw new GateRuntimeException("Cannot use nrValues for non-String statistics");
    return stringStats.keySet().size();
  }
  
  public List<String> stringValues() {
    if(stringStats==null) throw new GateRuntimeException("Cannot use stringValues for non-String statistics");
    return new ArrayList<String>(stringStats.keySet());
  }
  
  public Map<String,Long> stringCounts() {
    if(stringStats==null) throw new GateRuntimeException("Cannot use stringCounts for non-String statistics");
    return new HashMap<String,Long>(stringStats);
  }
  
  // wrapped SummaryStatistics methods as we need them
  public double getMin() {
    if(numStats==null) throw new GateRuntimeException("Cannot use getMin for String statistics");
    return numStats.getMin();
  }
  public double getMax() {
    if(numStats==null) throw new GateRuntimeException("Cannot use getMax for String statistics");
    return numStats.getMax();
  }
  public double getMean() {
    if(numStats==null) throw new GateRuntimeException("Cannot use getMean for String statistics");
    return numStats.getMean();
  }
  public double getVariance() {
    if(numStats==null) throw new GateRuntimeException("Cannot use getVariance for String statistics");
    return numStats.getVariance();
  }
  
  public StatsObject getStatsObject() {
    StatsObject ret = new StatsObject();
    ret.isString = isString();
    ret.n = getN();
    if(isString()) {
      ret.stringCounts = stringCounts();
    } else {
      // create an empty one because having that in JSON is easier to handle than "null"
      ret.stringCounts = new HashMap<String,Long>(); 
      ret.min = getMin();
      ret.max = getMax();
      ret.mean = getMean();
      ret.variance = getVariance();      
    }
    return ret;
  }
  
  public static class StatsObject implements Serializable {
    public boolean isString = false;
    public Map<String,Long> stringCounts;
    public double min;
    public double max;
    public double mean;
    public double variance;
    public long n;
  }

  
}
