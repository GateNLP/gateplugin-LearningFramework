#!/usr/bin/perl -w

## Example perl script of converting a CSV file of TFIDF values as prodiced by TermRaider
## to a LST file that can be used with the ExtendedGazetteer or FeatureGazetteer from
## the StringAnnotation plugin. 
## 
## Usage:
##   perl TermBankTFIDF2lst < termbankfile.csv > tfidf.lst
## Then create a tfidf.def file with the following content:
## tfidf.lst:tfidf::
## This pair of files (tfidf.def, tfidf.lst) can then be used  with the 
## ExtendedGazetteer to create new annotations for matching text or annotations
## or with the FeatureGazetteer to add the features to existing annotations.
##
## NOTE: the Gazetteer will store the values as Strings, not numbers! 
##
use strict;

my $linenr = 0;
while(<STDIN>) {
  $linenr++;
  next if ($linenr < 3); # ignore header and summary lines
  chomp;
  my ($term,$lang,$type,$tfidf,$tfidfraw,$termfreq,$localDocFreq,$refDocFreq) = split(/\,/);
  print "$term\ttfidf=$tfidf\tdf=$localDocFreq\ttf=$termfreq\n";
}
  
