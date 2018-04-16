import sys
import json
import os
import logging
from gatelfdata import Dataset
from gatelfpytorch import ModelWrapperSimple
# from gatelfpytorch import ModelWrapper


print("PYTHON APPLICATION SCRIPT, args=",sys.argv,file=sys.stderr)

modelprefix=sys.argv[1]
metafile=sys.argv[2]
datadir=sys.argv[3]

# Set up logging
logger = logging.getLogger("gatelfdata")
logger.setLevel(logging.ERROR)
logger = logging.getLogger("gatelfpytorch")
logger.setLevel(logging.DEBUG)
streamhandler = logging.StreamHandler(stream=sys.stderr)
formatter = logging.Formatter(
                '%(asctime)s %(name)-12s %(levelname)-8s %(message)s')
streamhandler.setFormatter(formatter)
logger.addHandler(streamhandler)
filehandler = logging.FileHandler(os.path.join(datadir,"FileJsonPyTorch.train.log"))
logger.addHandler(filehandler)

# restore the wrapper
wrapper = ModelWrapperSimple.load(modelprefix)

with sys.stdin as infile:
    for line in infile:
        print("PYTHON APPLICATION, input=",line,file=sys.stderr)
        if line == "STOP":
            break
        # TODO: currently the LF sends individual instances here, we may want to change
        # However we need to always apply to a set of instances, so wrap into another array
        instancedata = json.loads(line)
        preds=wrapper.apply([instancedata])
        print("PYTHON APPLICATION, preds=", preds, file=sys.stderr)
        print(preds)
        # TODO: IMPORTANT!!! What the model returns is currently different from what the LF code expects!!!
        sys.stdout.flush()
print("PYTHON APPLICATION SCRIPT: finishing",file=sys.stderr)
