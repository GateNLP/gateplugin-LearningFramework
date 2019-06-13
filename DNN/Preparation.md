# Preparation / Installation for Pytorch/Keras

The Pytorch and Keras learning algorithms use Python and a number of python packages which
have to be installed before the algorithms can be used.

Python can be installed in different ways and depending on the installation option chosen,
there are one or more ways of how to install the required packages. The LearningFramework 
does no require any specific way of installing Python and the packages, as long as the following
requriements are met:

* Python version 3.5 or newer, 64bit
* For the Pytorch backend: Pytorch 4.1 or newer installed, optionally with CUDA support of a 
  supported GPU is available
* For the Keras backend: Keras 1.2 and Tensorflow 1.0 or newer installed, optionally with everything installed in 
  addition to support CUDA, if a GPU is available.
* Package configsimple must be installed 

However, the recommended approach to install Python and the required packages is by using
Anaconda (https://www.anaconda.com/download/) or Miniconda (https://conda.io/miniconda.html)

The following installation instructions show how to prepare the system by using Miniconda.

## Linux and Linux-like Operating Systems

* Install the 64 bit distribution for Python 3.x from https://conda.io/miniconda.html
  * Download the file
  * Start a terminal and ...
  * Change permissions to make it executable:
    `chmod 700 Miniconda3-latest-Linux-x86_64.sh`
  * Run the file:
    `./Miniconda3-latest-Linux-x86_64.sh`
  * Agree to the license by typing in "yes" and ENTER
  * Confirm or change the location where to install to and take a note of that path (e.g. /home/username/miniconda3)
  * Agree to initialize Miniconda3 in the .bashrc file (this will add everything to your binary path)
  * Start a new terminal and check: `which python` should show the location of the python command inside
    the miniconda installation directory (e.g. /home/username/miniconda3/bin/python)
* Start a new terminal
* In the terminal window enter and run the following commands  (NOTE: a working internet connection is 
  quired for this! The following steps will automatically download the required packages and all dependencies
  and install them into your Miniconda environment)
  * `conda install -y python=3.6`
  * `pip install configsimple` 
  * For Pytorch: `conda install -y pytorch torchvision -c pytorch`
  * For Keras, with GPU: `conda install -y tensorflow-gpu keras-gpu h5py`
  * For Keras, without GPU: `conda install -y tensorflow keras h5py`
  * NOTE: Installation of Keras/Tensorflow is much more brittle than Pytorch and may fail on your system for various
    reasons. Please consult the Keras/Tensorflow support resources on the Web if you encounter any problems!


## Windows

* Install the 64 bit distribution for Python 3.x from https://conda.io/miniconda.html
  * Download the file
  * Run it to start the installation process
  * At the prompt "Install for:" choose "Just Me"
  * Confirm the default installation location (probably "C:\Users\THEUSERNAME\Miniconda3") and
    take a note of that location! 
  * IMPORTANT: On Windows, it is not always possible to figure out where Python is installed, 
    if you copy or take a note of the location shown by the installer you can later configure it
    in the [WrapperConfig.yaml](WrapperConfig) file!
  * In the "Advanced Options" screen, keep the default settings
* After the installation completes successfully you should find an entry "Anaconda3" in your Start menu
  which contains the entry "Anaconda Prompt"
* Click "Anaconda Prompt" to start a terminal window 
* In the terminal window enter and run the following commands  (NOTE: a working internet connection is 
  quired for this! The following steps will automatically download the required packages and all dependencies
  and install them into your Miniconda environment)
  * `conda install -y python=3.6`
  * `pip install configsimple`
  * For Pytorch: `conda install -y pytorch torchvision -c pytorch`
  * For Keras, with GPU: `conda install -y tensorflow-gpu keras-gpu h5py`
  * For Keras, without GPU: `conda install -y tensorflow keras h5py`
  * NOTE: Installation of Keras/Tensorflow is much more brittle than Pytorch and may fail on your system for various
    reasons. Please consult the Keras/Tensorflow support resources on the Web if you encounter any problems!

## MacOS

* Install the 64 bit distribution for Python 3.x from https://conda.io/miniconda.html
  * Download the file
  * Start a terminal and ...
  * Change permissions to make it executable:
    `chmod 700 Miniconda3-latest-Linux-x86_64.sh`
  * Run the file:
    `./Miniconda3-latest-Linux-x86_64.sh`
  * Agree to the license by typing in "yes" and ENTER
  * Confirm or change the location where to install to and take a note of that path (e.g. /Users//username/miniconda3)
  * Agree to initialize Miniconda3 in the .bashrc file (this will add everything to your binary path)
  * Start a new terminal and check: `which python` should show the location of the python command inside
    the miniconda installation directory (e.g. /Users//username/miniconda3/bin/python)
* Start a new terminal
* In the terminal window enter and run the following commands  (NOTE: a working internet connection is
  quired for this! The following steps will automatically download the required packages and all dependencies
  and install them into your Miniconda environment)
  * `conda install -y python=3.6`
  * `pip install configsimple`
  * For Pytorch: `conda install -y pytorch torchvision -c pytorch`
  * For Keras, with GPU: `conda install -y tensorflow-gpu keras-gpu h5py`
  * For Keras, without GPU: `conda install -y tensorflow keras h5py`
  * NOTE: Installation of Keras/Tensorflow is much more brittle than Pytorch and may fail on your system for various
    reasons. Please consult the Keras/Tensorflow support resources on the Web if you encounter any problems!

