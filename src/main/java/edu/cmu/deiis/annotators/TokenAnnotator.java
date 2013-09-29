package edu.cmu.deiis.annotators;

import java.util.Iterator;
import java.util.regex.*;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.*;

import edu.cmu.deiis.types.*;

/**
 * Description: Annotate the Tokens using regular expressions.
 */
public class TokenAnnotator extends JCasAnnotator_ImplBase {

  private Pattern tokenPattern;

  private String casProcessId;

  private double confidence;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    String str = (String) aContext.getConfigParameterValue("TokenPattern");
    tokenPattern = Pattern.compile(str);
    casProcessId = (String) aContext.getConfigParameterValue("ProcessId");
    confidence = 1.0;
  }

  public void process(JCas aJCas) throws AnalysisEngineProcessException {

    FSIndex qIndex = aJCas.getAnnotationIndex(Question.type);
    FSIndex aIndex = aJCas.getAnnotationIndex(Answer.type);

    Iterator<Question> qIter = qIndex.iterator();
    while (qIter.hasNext()) {
      Question q = qIter.next();
      int begin = q.getBegin();
      String str = q.getCoveredText();
      annotateToken(begin, str, aJCas);
    }

    Iterator<Answer> aIter = aIndex.iterator();
    while (aIter.hasNext()) {
      Answer a = aIter.next();
      int begin = a.getBegin();
      String str = a.getCoveredText();
      annotateToken(begin, str, aJCas);
    }
  }

  private void annotateToken(int begin, String str, JCas aJCas) {
    Matcher m = tokenPattern.matcher(str);
    while (m.find()) {
      Token token = new Token(aJCas);
      token.setBegin(begin + m.start());
      token.setEnd(begin + m.end());
      token.setCasProcessorId(casProcessId);
      token.setConfidence(confidence);
      token.addToIndexes();
    }
  }
}
