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
        # TODO: better error handling: put the apply call into a try block and catch any error, also
        # check returned data. If there is a problem send back in the map we return!!
        # NOTE: the  LF expects to get a map with the following elements:
        # status: must be "ok", anything else is interpreted as an error
        # output: the actual prediction: gets extracted from the returned data here
        # confidence: some confidence/probability score for the output, may be null: this gets extracted
        # from our returned data here
        # confidences: a map with confidences for all labels, may be null: this is NOT SUPPORTED in the LF yet!
        preds=wrapper.apply([instancedata])
        print("PYTHON APPLICATION, preds=", preds, file=sys.stderr)
        # preds are a list of one or two lists, where the first list contains all the labels and the second
        # list contains all the confidences in the order used by the model. 
        # For now we just extract the label or for a sequence, the list of labels, knowing that for now we always process only one instance/sequence!
        ret = {"status":"ok", "output":preds[0][0]}
        print("PYTHON APPLICATION, return=", ret, file=sys.stderr)
        print(json.dumps(ret))
        # TODO: IMPORTANT!!! What the model returns is currently different from what the LF code expects!!!
        sys.stdout.flush()
print("PYTHON APPLICATION SCRIPT: finishing",file=sys.stderr)
