package edu.cmu.deiis.annotators;

import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.*;

import edu.cmu.deiis.types.*;

/**
 * Description: Annotate the unigrams using the index of Token.
 */
public class TokenUnigramAnnotator extends JCasAnnotator_ImplBase {

  private String casProcessId;

  private double confidence;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    casProcessId = (String) aContext.getConfigParameterValue("ProcessId");
    confidence = 1.0;
  }

  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    FSIndex tIndex = aJCas.getAnnotationIndex(Token.type);

    Iterator<Token> tIter = tIndex.iterator();
    while (tIter.hasNext()) {
      Token t = tIter.next();
      NGram ng = new NGram(aJCas);
      ng.setBegin(t.getBegin());
      ng.setEnd(t.getEnd());
      ng.setCasProcessorId(casProcessId);
      ng.setConfidence(confidence);

      FSArray arr = new FSArray(aJCas, 1);
      ng.setElements(arr);
      ng.setElements(0, t);
      ng.setElementType(t.getClass().getName());

      ng.addToIndexes();
    }

  }
}
