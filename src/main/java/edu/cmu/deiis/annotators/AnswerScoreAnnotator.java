package edu.cmu.deiis.annotators;

import java.util.Iterator;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.*;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.*;
import org.apache.uima.resource.ResourceInitializationException;

import edu.cmu.deiis.types.*;

/**
 * Description: Give the score to the answer. Here, we use the token overlap scoring method.
 */
public class AnswerScoreAnnotator extends JCasAnnotator_ImplBase {

  private String splitPattern;

  private String casProcessId;

  private double confidence;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    splitPattern = (String) aContext.getConfigParameterValue("SplitPattern");
    casProcessId = (String) aContext.getConfigParameterValue("ProcessId");
    confidence = 1.0;
  }

  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    String ques = "";

    FSIndex qIndex = aJCas.getAnnotationIndex(Question.type);
    Iterator<Question> qIter = qIndex.iterator();
    if (qIter.hasNext()) {
      ques = qIter.next().getCoveredText();
    }

    String[] quesStrs = ques.split(splitPattern);

    FSIndex aIndex = aJCas.getAnnotationIndex(Answer.type);
    Iterator<Answer> aIter = aIndex.iterator();
    while (aIter.hasNext()) {
      Answer ans = aIter.next();
      String[] ansStrs = ans.getCoveredText().split(splitPattern);
      int cnt = 0;
      for (int i = 0; i < ansStrs.length; ++i) {
        if (ques.indexOf(ansStrs[i]) >= 0)
          ++cnt;
      }
      AnswerScore ansScore = new AnswerScore(aJCas);
      ansScore.setBegin(ans.getBegin());
      ansScore.setEnd(ans.getEnd());
      ansScore.setCasProcessorId(casProcessId);
      ansScore.setConfidence(confidence);
      int len = Math.max(ansStrs.length, quesStrs.length);
      ansScore.setScore((float) cnt / (float) len);
      ansScore.setAnswer(ans);
      ansScore.addToIndexes();
    }
  }

}
