import sys
import json
import os
from gatelfdata import Dataset
from gatelfpytorch import ModelWrapperSimple
# from gatelfpytorch import ModelWrapper

print("MODIFIED!!! PYTHON TRAINING SCRIPT, args=",sys.argv,file=sys.stderr)

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


# TODO: maybe allow passing additional parms here from what gets passed as additional parms to the script etc?
# TODO: this may be essential for setting cuda manuall!
# create the dataset instance
ds=Dataset(metafile)

# create the wrapper
# TODO: cuda autodetect finds cuda on zeus but then runs into an error
wrapper = ModelWrapperSimple(ds,cuda=False)

wrapper.prepare_data()
wrapper.validate_every_batches = 10
#wrapper.train(batch_size=33,
#        early_stopping=lambda x: ModelWrapper.early_stopping_checker(x, max_variance=0.0000001))
print("DEBUG: before running the training", file=sys.stderr)
wrapper.train()
print("DEBUG: after running the training", file=sys.stderr)

wrapper.save(modelprefix)

print("PYTHON TRAINING SCRIPT: finishing",file=sys.stderr)
