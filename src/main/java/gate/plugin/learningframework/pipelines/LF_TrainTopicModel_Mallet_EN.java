
package gate.plugin.learningframework.pipelines;

import gate.creole.PackagedController;
import gate.creole.metadata.AutoInstance;
import gate.creole.metadata.AutoInstanceParam;
import gate.creole.metadata.CreoleResource;

/**
 * Class for providing the ready made application.
 * @author Johann Petrak, johann.petrak@gmail.com
 */
@CreoleResource(
        name = "LF_TrainTopicModel-Mallet-EN", 
        comment = "Example steps to prepare and train a topic model using Mallet LDA",
        // icon = "measurements", 
        autoinstances = @AutoInstance(parameters = {
          @AutoInstanceParam(name="pipelineURL", value="resources/pipelines/LF_TrainTopicModel-Mallet-EN.xgapp"), 
          @AutoInstanceParam(name="menu", value="LearningFramework")})) 
public class LF_TrainTopicModel_Mallet_EN extends PackagedController {
  
  private static final long serialVersionUID = -1892123999534954276L;
  
}
