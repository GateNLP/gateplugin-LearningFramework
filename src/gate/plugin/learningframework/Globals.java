/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package gate.plugin.learningframework;

/**
 *
 * @author Johann Petrak
 */
public class Globals {
  public static final String outputClassFeature = "LF_target";
  public static final String outputProbFeature = "LF_confidence";
  public static final String outputSequenceSpanIDFeature = "LF_seq_span_id";
  //In the case of NER, output instance annotations to temporary
  //AS, to keep them separate.
  public static final String tempOutputASName = "tmp_ouputas_for_ner12345";
  public static final String savedModelDirectory = "savedModel";
  public static final String trainFilename = "trainfile";
}
