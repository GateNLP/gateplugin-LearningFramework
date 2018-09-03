# PytorchWrapper Modules - predefined networks

This page gives an overview over the pre-defined network architectures that
are available in the PytorchWrapper. Modules can be used in place of the automatically 
generated network architectures, but are limited to specific tasks for specific features and targets. 
While the default automatically generated architecture will get adapted to the features defined for
the task, each pre-defined module works only for a specific feature definition and expects a 
fixed input format. 

Remember that before training starts with the PytorchWrapper, the LearningFramework performs the following steps:
* It creates the data file that contains the JSON representation of the instances or sequences in the `dataDirectory`
* It creates the meta file that contains information and statistics about the data file in the `dataDirectory`
* If it does not already exist, it creates the subdirectory `FileJsonPytorch` in the `dataDirectory`. This directory 
  contains the scripts for running training and for running the application process, and contains the directories for
  the two necessary Python libraries, `gate-lf-python-data` and `gate-lf-pytorch-json`.

So the training PR creates a copy of the `gate-lf-pytorch-json` library as `dataDirectory/FileJsonPyTorch/gate-lf-pytorch-json`. This library has a standard Python library directory structure, the actual package is in subdirectory `gatelfpytorchjson`. All the modules are defined in the subdirectory `modules` within that package directory.
So the modules are in directory `dataDirectory/FileJsonPyTorch/gate-lf-pytorch-json/gatelfpytorchjson/modules`.

A module is a file that contains the definition of a Python class with the same name as the file (without the `.py` extension), for example the file `SeqTaggerLSTMSimple.py` contains a class `SeqTaggerLSTMSimple`. In order to use the module, the algorithm parameter `--module SeqTaggerLSTMSimple` can be used. 

The class defined for the module must inherit from `gatelfpytorchjson.CustomModule` which in turn inherits from 
`torch.nn.Module`. It is thus a Pytorch module which works essentially like a standard Pytorch module, can be saved
and re-loaded like a Pytorch module but needs to follow the following (additional) conventions:
* the `init` method must take the parameter `config` which expects a dictionary of module specific configuration paramters (but the dictionary can also contain additional entries which should get ignored)
* the `init` method must call the init method of the superclass using `super().__init__()`
* the class must implement a method `get_lossfunction(self, config=configdict)` which returns an object that
  can be used as a loss function for training.
* the class must implement a method `get_optimizer(self, config=configdict)` which returns and object that can be 
  used as an initialized optimizer for training.
* the class may override the method `set_seed(self, seed)` to handle seeding the random number generator
* the class may override the method `on_cuda(self)` which returns a flag if the module uses the GPU
* the class must implement the method `forward(self, batch)` which takes a batch of instances/sequences and returns
  a tensor of outputs to which the loss function can be applied and which can also be used to find the label(s) for
  the input at application time.
* the class must implement the method `loss(self, batch)` which takes a batch of instances/sequences and returns 
  a loss tensor that can be used for backtracking.

IMPORTANT/TODO: consolidate the interface and document here, we probably need to return more values for forward and loss.

## Example 

The following shows a simple example module (some non-essential code not included here)  which is included by default, `SeqTaggerLSTMSimple`:

```python
import sys, logging, torch.nn, torch.nn.functional as F
from gatelfpytorchjson import CustomModule, EmbeddingsModule

# set up the logger here to log to sys.stderr

class SeqTaggerLSTMSimple(CustomModule):

    def __init__(self, dataset, config={}):
        super().__init__(config=config)
        self.n_classes = dataset.get_info()["nClasses"]
        feature = dataset.get_index_features()[0]
        vocab = feature.vocab
        self.layer_emb = EmbeddingsModule(vocab)
        emb_dims = self.layer_emb.emb_dims

        self.lstm_hiddenunits = 200
        self.lstm_nlayers = 1
        self.lstm_is_bidirectional = False
        self.layer_lstm = torch.nn.LSTM( input_size=emb_dims, hidden_size=self.lstm_hiddenunits,
            num_layers=self.lstm_nlayers, dropout=0.0, bidirectional=self.lstm_is_bidirectional,
            batch_first=True)
        lin_units = self.lstm_hiddenunits*2 if self.lstm_is_bidirectional else self.lstm_hiddenunits
        self.lstm_totalunits = lin_units
        self.layer_lin = torch.nn.Linear(lin_units, self.n_classes)
        logger.info("Network created: %s" % (self, )) # display the network 

    def forward(self, batch):
        batch = torch.LongTensor(batch[0])  # only expect/use a single feature
        if self.on_cuda():
            batch.cuda()
        tmp_embs = self.layer_emb(batch)
        lstm_hidden, (lstm_c_last, lstm_h_last) = self.layer_lstm(tmp_embs)
        tmp_lin = self.layer_lin(lstm_hidden)
        out = F.log_softmax(tmp_lin, 2)
        return out

    def get_lossfunction(self, config={}):
        return torch.nn.NLLLoss(ignore_index=-1)

    def get_optimizer(self, config={}):
        parms = filter(lambda p: p.requires_grad, self.parameters())
        optimizer = torch.optim.Adam(parms, lr=0.015, betas=(0.9, 0.999), eps=1e-08, weight_decay=0)
        return optimizer
```

MORE DOCUMENTATION TO BE DONE, BUT THE IMPLEMENTATION OF THIS WILL CHANGE A BIT FIRST!
