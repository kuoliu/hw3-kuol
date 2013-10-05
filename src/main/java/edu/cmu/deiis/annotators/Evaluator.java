package edu.cmu.deiis.annotators;

import java.util.*;

import org.apache.uima.analysis_component.*;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.cas.FSIndex;
import org.apache.uima.jcas.*;

import edu.cmu.deiis.types.*;

/**
 * Description: Sort the answers according to their scores and output the result
 * and precision@N.
 */
public class Evaluator extends JCasAnnotator_ImplBase {

	private LinkedList<Float> precs = new LinkedList<Float>();
	
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		FSIndex qIndex = aJCas.getAnnotationIndex(Question.type);
		Iterator<Question> qIter = qIndex.iterator();
		if (qIter.hasNext()) {
			System.out.println("Question: " + qIter.next().getCoveredText()
					+ "?");
		}

		FSIndex ansscoreIndex = aJCas.getAnnotationIndex(AnswerScore.type);
		Iterator<AnswerScore> iter = ansscoreIndex.iterator();
		LinkedList<MyEntry> list = new LinkedList<MyEntry>();
		while (iter.hasNext()) {
			AnswerScore ansscore = iter.next();
			list.add(new MyEntry(ansscore.getScore(), ansscore.getAnswer()));
		}
		Collections.sort(list);

		/*
		 * Output the answers and compute how many answers are true.
		 */
		LinkedList<Boolean> trueList = new LinkedList<Boolean>();
		int cntTrue = 0;
		for (MyEntry e : list) {
			double score = e.getScore();
			Answer ans = e.getAns();
			if (ans.getIsCorrect()) {
				System.out.print("+ ");
				trueList.add(true);
				++cntTrue;
			} else {
				System.out.print("- ");
				trueList.add(false);
			}
			System.out.printf("%.2f", score);
			System.out.println(" " + ans.getCoveredText());
		}

		/*
		 * Output the precision@N.
		 */
		int predictTrue = 0;
		int cnt = 0;
		for (boolean truth : trueList) {
			if (truth)
				++predictTrue;
			if ((++cnt) >= cntTrue)
				break;
		}
		float precision = (float) predictTrue / (float) cntTrue;
		System.out.println("Precision at " + cntTrue + ": " + String.format("%.2f", precision));
		System.out.println("");
		this.precs.add(new Float(precision));
	}
	
	public void collectionProcessComplete(){
		float sum = 0.0f;
		for(Float f : this.precs){
			sum += f;
		}
		System.out.println("Average Precision: " + String.format("%.2f", sum / this.precs.size()));
	}
}

/**
 * Description: In MyEntry, key is score of an answer, value is an Answer, and
 * it implements the Comparable interface, which makes the sorting more
 * convenient.
 */
class MyEntry implements Comparable {

	private double score;

	private Answer ans;

	public MyEntry(double score, Answer ans) {
		this.score = score;
		this.ans = ans;
	}

	public double getScore() {
		return score;
	}

	public Answer getAns() {
		return ans;
	}

	public int compareTo(Object obj) {
		MyEntry e = (MyEntry) obj;
		if (this.score < e.getScore())
			return 1;
		else
			return -1;
	}
}