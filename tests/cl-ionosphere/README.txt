The document has been created from the ionosphere ARFF dataset that gets distributed
with Weka:
  see e.g. https://github.com/svn2github/weka/blob/master/tags/dev-3-7-3/wekadocs/data/ionosphere.arff

QA numbers for running the training application then the application application for 
SVM, Naive Bayes and RandomForests: since this is classification on the training set, only 
F1.0 strict is really relevent:

Without feature scaling:
LibSVM: 0.9972
RF:     1.0000
NB:     0.8291
MLP:    0.9972
MallC45 0.9630

With feature scaling:
LibSVM: 0.3590
RF:     0.6410
NB:     0.8291
MLP:    0.9972
MallC45 [Exception during training, see below]

This values were obtained for
  branch version_1_0, commit 63e734dbc5adf1a7115e8e5d8f1fdaa8ea0f23bb
  branch jp-151218-merge1, commit 2dcaee3fd53066f60e0dd11d371fdda41dde2c2e
  also later merge1 branches since we did not change any of the old code.

No feature scaling, new:
LibSVM -c 1000 -g 0.02: 0.9972
Weka RF:              : 1.0000
NB:                     0.8291
MalletC45               0.9630

With feature scaling, new:
LibSVM -c 1000 -g 0.02: 0.9972
Weka RF:                1.0000
NB                      0.8291
MalletC45:              Exception/does not complete

OK,LibSVM works with feature scaling!

MalletC45 training exception:
Exception in thread "ApplicationViewer1" java.lang.StackOverflowError
	at java.util.regex.Pattern$BmpCharProperty.match(Pattern.java:3797)
	at java.util.regex.Pattern$GroupHead.match(Pattern.java:4658)
	at java.util.regex.Pattern$Branch.match(Pattern.java:4604)
	at java.util.regex.Pattern$Branch.match(Pattern.java:4602)
	at java.util.regex.Pattern$Branch.match(Pattern.java:4602)
	at java.util.regex.Pattern$BranchConn.match(Pattern.java:4568)
	at java.util.regex.Pattern$GroupTail.match(Pattern.java:4717)
	at java.util.regex.Pattern$Curly.match0(Pattern.java:4279)
	at java.util.regex.Pattern$Curly.match(Pattern.java:4234)
	at java.util.regex.Pattern$GroupHead.match(Pattern.java:4658)
	at java.util.regex.Pattern$Branch.match(Pattern.java:4604)
	at java.util.regex.Pattern$BranchConn.match(Pattern.java:4568)
	at java.util.regex.Pattern$GroupTail.match(Pattern.java:4717)
	at java.util.regex.Pattern$BmpCharProperty.match(Pattern.java:3798)
	at java.util.regex.Pattern$Curly.match0(Pattern.java:4279)
	at java.util.regex.Pattern$Curly.match(Pattern.java:4234)
	at java.util.regex.Pattern$GroupHead.match(Pattern.java:4658)
	at java.util.regex.Pattern$Branch.match(Pattern.java:4604)
	at java.util.regex.Pattern$BmpCharProperty.match(Pattern.java:3798)
	at java.util.regex.Pattern$Start.match(Pattern.java:3461)
	at java.util.regex.Matcher.search(Matcher.java:1248)
	at java.util.regex.Matcher.find(Matcher.java:664)
	at java.util.Formatter.parse(Formatter.java:2549)
	at java.util.Formatter.format(Formatter.java:2501)
	at java.util.Formatter.format(Formatter.java:2455)
	at java.lang.String.format(String.java:2928)
	at java.util.logging.SimpleFormatter.format(SimpleFormatter.java:161)
	at java.util.logging.StreamHandler.publish(StreamHandler.java:211)
	at java.util.logging.ConsoleHandler.publish(ConsoleHandler.java:116)
	at java.util.logging.Logger.log(Logger.java:738)
	at java.util.logging.Logger.doLog(Logger.java:765)
	at java.util.logging.Logger.log(Logger.java:788)
	at java.util.logging.Logger.info(Logger.java:1489)
	at cc.mallet.types.GainRatio.calcGainRatios(GainRatio.java:258)
	at cc.mallet.types.GainRatio.createGainRatio(GainRatio.java:310)
	at cc.mallet.classify.C45$Node.<init>(C45.java:129)
	at cc.mallet.classify.C45$Node.split(C45.java:210)
	at cc.mallet.classify.C45Trainer.splitTree(C45Trainer.java:138)
        [recursive exception]
