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
 * Description: Annotate trigrams using the Question index, Answer index and regular expressions.
 */
public class TokenTrigramAnnotator extends JCasAnnotator_ImplBase {

  private Pattern trigramPattern;

  private String casProcessId;

  private double confidence;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    String str = (String) aContext.getConfigParameterValue("TrigramPattern");
    trigramPattern = Pattern.compile(str);
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
      annotateTrigram(str, begin, aJCas);
    }

    Iterator<Answer> aIter = aIndex.iterator();
    while (aIter.hasNext()) {
      Answer a = aIter.next();
      int begin = a.getBegin();
      String str = a.getCoveredText();
      annotateTrigram(str, begin, aJCas);
    }
  }

  private void annotateTrigram(String str, int begin, JCas aJCas) {
    int i = 0;
    Matcher m = trigramPattern.matcher(str);
    while (m.find(i)) {
      NGram tri = new NGram(aJCas);
      tri.setBegin(begin + m.start());
      tri.setEnd(begin + m.end());
      String content = m.group();
      tri.setCasProcessorId(casProcessId);
      tri.setConfidence(confidence);
      tri.setElementType(Token.class.getName());

      FSArray arr = new FSArray(aJCas, 3);
      tri.setElements(arr);
      Token t1 = new Token(aJCas);
      String[] strs = content.split("\\W+");
      t1.setBegin(begin + m.start());
      t1.setEnd(begin + m.start() + strs[0].length());
      t1.setCasProcessorId(casProcessId);
      t1.setConfidence(confidence);
      tri.setElements(0, t1);
      Token t2 = new Token(aJCas);
      t2.setBegin(begin + m.start() + content.indexOf(strs[1], strs[0].length()));
      t2.setEnd(begin + m.start() + strs[0].length() + m.group(1).length() + strs[1].length());
      t2.setCasProcessorId(casProcessId);
      t2.setConfidence(confidence);
      tri.setElements(1, t2);
      Token t3 = new Token(aJCas);
      t3.setBegin(begin + m.start() + content.lastIndexOf(strs[2]));
      t3.setEnd(begin + m.start() + content.length());
      t3.setCasProcessorId(casProcessId);
      t3.setConfidence(confidence);
      tri.setElements(2, t3);

      tri.addToIndexes();
      i = m.start(1);
    }
  }
}
