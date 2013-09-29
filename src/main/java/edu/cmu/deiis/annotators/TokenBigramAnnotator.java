package edu.cmu.deiis.annotators;

import java.util.Iterator;
import java.util.regex.*;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.*;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.*;
import org.apache.uima.jcas.cas.FSArray;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.deiis.types.*;

/**
 * Description: Annotate the bigrams using the Question index, Answer index and regular expressions.
 */
public class TokenBigramAnnotator extends JCasAnnotator_ImplBase {

  private Pattern bigramPattern;

  private String casProcessId;

  private double confidence;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    String str = (String) aContext.getConfigParameterValue("BigramPattern");
    bigramPattern = Pattern.compile(str);
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
      annotateBigram(str, begin, aJCas);
    }

    Iterator<Answer> aIter = aIndex.iterator();
    while (aIter.hasNext()) {
      Answer a = aIter.next();
      int begin = a.getBegin();
      String str = a.getCoveredText();
      annotateBigram(str, begin, aJCas);
    }
  }

  private void annotateBigram(String str, int begin, JCas aJCas) {
    int i = 0;
    Matcher m = bigramPattern.matcher(str);
    while (m.find(i)) {
      NGram bi = new NGram(aJCas);
      bi.setBegin(begin + m.start());
      bi.setEnd(begin + m.end());
      String content = m.group();
      bi.setCasProcessorId(casProcessId);
      bi.setConfidence(confidence);
      bi.setElementType(Token.class.getName());

      FSArray arr = new FSArray(aJCas, 2);
      bi.setElements(arr);
      Token t1 = new Token(aJCas);
      String[] strs = content.split("\\W+");
      t1.setBegin(begin + m.start());
      t1.setEnd(begin + m.start() + strs[0].length());
      t1.setCasProcessorId(casProcessId);
      t1.setConfidence(confidence);
      bi.setElements(0, t1);
      Token t2 = new Token(aJCas);
      t2.setBegin(begin + m.start() + content.lastIndexOf(strs[1]));
      t2.setEnd(begin + m.start() + content.length());
      t2.setCasProcessorId(casProcessId);
      t2.setConfidence(confidence);
      bi.setElements(1, t2);

      bi.addToIndexes();
      i = m.start(1) + 1;
    }
  }
}
