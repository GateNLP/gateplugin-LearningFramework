# Preparation / Installation

The Pytorch and Keras learning algorithms use Python and a number of python packages which
have to be installed before the algorithms can be used.

Python can be installed in different ways and depending on the installation option chosen,
there are one or more ways of how to install the required packages. The LearningFramework 
does no require any specific way of installing Python and the packages, as long as the following
requriements are met:

* Python version 3.5 or newer, 64bit
* For the Pytorch backend: Pytorch 4.1 or newer installed, optionally with CUDA support of a 
  supported GPU is available
* For the Keras backend: Keras ??? or newer installed, optionally with everything installed in 
  addition to support CUDA, if a GPU is available. 

However, the recommended approach to install Python and the required packages is by using
Anaconda (https://www.anaconda.com/download/) or Miniconda (https://conda.io/miniconda.html)

The following installation instructions show how to prepare the system by using Miniconda.

## Linux and Linux-like Operating Systems

## Windows

* Install the 64 bit distribution for Python 3.x from https://conda.io/miniconda.html
  * Download the file
  * Run it to start the installation process
  * At the prompt "Install for:" choose "Just Me"
  * Confirm the default installation location (probably "C:\Users\THEUSERNAME\Miniconda3") and
    take a note of that location.
  * In the "Advanced Options" screen, keep the default settings
* After the installation completes successfully you should find an entry "Anaconda3" in your Start menu
  which contains the entry "Anaconda Prompt"
* Click "Anaconda Prompt" to start a terminal window 
* In the terminal window enter and run the following commands  (NOTE: a working internet connection is 
  quired for this! The following steps will automatically download the required packages and all dependencies
  and install them into your Miniconda environment)
  * `conda install -y pytorch torchvision -c pytorch`
  * `pip install -y keras` 

## MacOS


