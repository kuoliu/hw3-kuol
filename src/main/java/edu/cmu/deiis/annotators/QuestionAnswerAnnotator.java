package edu.cmu.deiis.annotators;

import java.util.regex.*;

import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.JCasAnnotator_ImplBase;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.*;

import edu.cmu.deiis.types.*;

/**
 * Description: Annotate the Questions and Answers using regular expressions.
 */
public class QuestionAnswerAnnotator extends JCasAnnotator_ImplBase {

  private Pattern questionPattern;

  private Pattern answerPattern;

  private String casProcessId;

  private double confidence;

  public void initialize(UimaContext aContext) throws ResourceInitializationException {
    String str = (String) aContext.getConfigParameterValue("QuestionPattern");
    questionPattern = Pattern.compile(str);
    str = (String) aContext.getConfigParameterValue("AnswerPattern");
    answerPattern = Pattern.compile(str);
    casProcessId = (String) aContext.getConfigParameterValue("ProcessId");
    confidence = 1.0;
  }

  public void process(JCas aJCas) throws AnalysisEngineProcessException {
    String docText = aJCas.getDocumentText();

    Matcher m = questionPattern.matcher(docText);
    while (m.find()) {
      Question q = new Question(aJCas);
      q.setBegin(m.start(1));
      q.setEnd(m.end(1));
      q.setCasProcessorId(casProcessId);
      q.setConfidence(confidence);
      q.addToIndexes();
    }
    m = answerPattern.matcher(docText);
    while (m.find()) {
      Answer a = new Answer(aJCas);
      a.setBegin(m.start(2));
      a.setEnd(m.end(2));
      a.setCasProcessorId(casProcessId);
      a.setConfidence(confidence);
      int truth = Integer.parseInt(m.group(1));
      if (truth == 0)
        a.setIsCorrect(false);
      else
        a.setIsCorrect(true);
      a.addToIndexes();
    }
  }
}
