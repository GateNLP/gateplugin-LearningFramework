Small and simple corpus for testing sequence tagging

A few random wikipedia articles, processed with the prepare.xgapp pipeline
which does basic ANNIE plus adds "Link" annotations for interwiki links.

We try to learn a model for predicting the links and run it on the training set.

Evaluation:

Before merge/refactoring:
Mallet CRF: Micro summary	47	4565	113	84	0.1926	0.0100	0.0190	0.5369	0.0279	0.0530

After merge/refactoring:
Mallet CRF: Micro summary	1235	3240	622	221	0.5943	0.2630	0.3646	0.7007	0.3101	0.4299

So for some reason, the old one does seem to do much worse here??
When training with the old version, I do get an error message from CRF training:
NFO: getValue() (loglikelihood, optimizable by label likelihood) = -19641.024757909672
Mar 04, 2016 6:34:51 PM cc.mallet.fst.CRFTrainerByValueGradients$OptimizableCRF getValue
INFO: getValue() (loglikelihood) = -19641.024757909672
Mar 04, 2016 6:34:51 PM cc.mallet.optimize.BackTrackLineSearch optimize
WARNING: EXITING BACKTRACK: Jump too small (alamin=2.230998288177378E-6). Exiting and using xold. Value=-19641.024757909672
cc.mallet.optimize.OptimizationException: Line search could not step in the current direction. (This is not necessarily cause for alarm. Sometimes this happens close to the maximum, where the function may be very flat.)
This happens after 18 iterations.

New version: much faster (maybe because it does not output the warnings about some weird value getting treated as 0.0)
then training proceeds to iteration 163 so no wonder we do get better results.


