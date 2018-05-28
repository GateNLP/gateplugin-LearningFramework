# Using a server for Model Application

If the `serverUrl` parameter is specified for the [[LF_ApplyClassification]] or [[LF_ApplyRegression]] PRs, then instead of loading a model from the data directory or running a wrapper, the PR will use the server to obtain the predictions. 

This works by extracting the feature vectors for all instances in a document, converting them into a JSON representation, sending that JSON to the serverUrl as a POS request and expecting back JSON that contains the predictions for these feature vectors. See below for the details of the requests and responses that get exchanged. 

The PR still needs to know about things that normally get stored in the data directory when a trained model is saved:
* The default target feature: this is stored in the `info.yaml` file and the data directory has to contain such a file for that reason (even if the target feature is specified as a runtime parameter and therefore the default target feature from the `info.yaml` file is not used). All other information from that file is ignored.
* The `pipe.pipe` file: this contains the information about attributes, the target type and, if applicable, feature scaling. This file must be present in the data directory.


## Request and Response formats

The request must have the following format and properties:
* must be a POST request
* the `content-type` header must be `application/json`
* the `accept` header must be `application/json`
* the body must be a string containing a JSON map with the following entries:
  * `values`: an array of arrays of numbers where the inner arrays represent the values in each feature vector and the outer array represents the instances
  * `indices`: an array of arrays of integers where the inner arrays represent the locations of the corresponding values within each sparse feature vector. If the `indices` entry is missing, the values are assumed to represent dense vectors
  * `weights`: an array of numbers representing the weights for each instance. If this is missing, then instance weights are not used. Even if this is present, the server may choose to ignore it.
  * `n`: an integer, giving the total number of dimensions. This is only required if sparse vectors are sent and is used in case the sparse vectors need to get converted to dense vectors.
  
The response has the following format:
* `content-type` is `application/json` for OK responses and `text/plain` for all other (error) responses.
* If there is an error, the status code is different from 200 and the body contains the error message as plain text
* if there was no error, the status code is 200 and the body contains a JSON map with the following entries:
  * `preds`: an array of arrays where the inner arrays represent the prediction for each instance
  * the inner array either contains one element in which case this is the predicted value for regression, or the class index for classification, or more than one element in which case the i-th element is the probability/confidence for the i-th class (where i is the class index). 