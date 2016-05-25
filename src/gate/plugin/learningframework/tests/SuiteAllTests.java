
package gate.plugin.learningframework.tests;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
  TestFeatureSpecification.class,
  TestFeatureExtraction.class,
  TestPipeSerialization.class,
  TestInfo.class,
  TestParms.class,
  TestEngineMalletClass.class,
  TestEngineLibSVM.class,
  TestEngineMalletSeq.class,
  TestFeatureScaling.class
})
public class SuiteAllTests {
  // so we can run this test from the command line 
  public static void main(String args[]) {
    org.junit.runner.JUnitCore.main(SuiteAllTests.class.getCanonicalName());
  }  
  
}
