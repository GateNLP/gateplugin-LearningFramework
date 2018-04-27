import sys
import json
import os
import logging
from gatelfdata import Dataset
from gatelfkerasjson import KerasWrapperImpl1
modelprefix=sys.argv[1]
metafile=sys.argv[2]
datadir=sys.argv[3]

# Set up logging
logger = logging.getLogger("gatelfdata")
logger.setLevel(logging.ERROR)
logger = logging.getLogger("gatelfkerasjson")
logger.setLevel(logging.DEBUG)
streamhandler = logging.StreamHandler(stream=sys.stderr)
formatter = logging.Formatter(
                '%(asctime)s %(name)-12s %(levelname)-8s %(message)s')
streamhandler.setFormatter(formatter)
logger.addHandler(streamhandler)
filehandler = logging.FileHandler(os.path.join(datadir,"FileJsonPyTorch.train.log"))
logger.addHandler(filehandler)

ds = Dataset(metafile)

kerasModel = KerasWrapperImpl1(ds)
kerasModel.genKerasModel()
kerasModel.trainModel()
kerasModel.saveModel(modelprefix)

