# Installation

With GATE version 8.5 or newer, the LearningFramework plugin gets installed just
like most other standard GATE plugins, using the plugin manager. This is only
necessary if you start with a new pipeline that requires the plugin - if you load
a pipeline that already uses the plugin, it will automatically get downloaded to
your computer under the hood.

In the GATE GUI:
* Open the plugin manager by clicking on the "jigsaw puzzle" icon in the tool bar or
  by choosing "Manage Creole Plugins" from the "File" menu.
* Opening the plugin manager may take a while since GATE may try to update the
  list of available plugins by accessing the internet
* Once the Creole Plugin Manager dialog window has opened, you will see a list
  of all available plugins. Scroll down to find the entry for "LearningFramework"
  and the version you need.
* Click the "Load Now" box for the LearningFramework plugin, and optionally
  click the box for all other plugins you need for your pipeline
* Click "Apply All" to load the plugins into GATE for use in your pipeline. Any
  plugin that has not yet been downloaded to your computer will get downloaded
  (and stored in your local Maven cache)

IMPORTANT: 
* if you want to use the Pytorch and Keras wrapper algorithms 
  [additional installation steps are required](DNN/Preparation)
* if you want to use other wrapper-based learning algorithms (Weka, Sklearn, CostCLA)
additional installation steps specific to that wrapper may be required. See the documentation
for that algorithm for additional information.

For GATE version 8.4.x or earlier, the LearningFramework plugin can get installed
using the plugin manager of that GATE version:
* Click on the "jigsaw puzzle" icon or choose "Manage Creole Plugins" from the "File" menu
* Select the "Configuration" tab in the top
* Make sure the "User Plugin Directory" is set to some directory on your computer (Click the
  folder icon on the right)
* Click the checkbox in front of "Additional Plugins from the GATE Team"
* Select the "Available to Install" tab on the top
* The list of plugins shown should now also include the LearningFramework. Click the checkbox
  and then click "Apply All" to download and install the plugin.
* Select the "Installed Plugins" tab. The plugin should now be listed as "gateplugin-LearningFramework".
  Click the "Load Now" checkbox to load it into GATE
Alternately for GATE version 8.4.x or earlier, you can choose and download one
of the releases from https://github.com/GateNLP/gateplugin-LearningFramework/releases
then unzip the pluging on your computer and in the Plugin Manger, in the "Installed Plugins"
tab, use the "+" button  in the top of the window to add that directory, which also should
make it show up in the list.

Upgrading the plugins and upgrading a pipeline from GATE 8.4.x or previous to GATE 8.5:
* Pipelines which use the LearningFramework should get upgraded to 8.5 in order
  to use the new-style plugin (managed by Maven) and also use a more recent version of
  the plugin
* However if the pipeline uses a version of the LearningFramework prior to
  version 3.9, that version may not be compatible with the changed PR parameters,
  algorithms and model formats used in 3.9 or later.
  Note that plugin version 3.9 provides a shell script (for Linux and MacOS) which  
  helps with updating the PR parameters in pipelines automatically.
