package edu.cmu.deiis.as;

import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;
import org.apache.uima.cas.FSIterator;
import org.apache.uima.cas.text.AnnotationFS;
import org.apache.uima.collection.EntityProcessStatus;

/**
 *  
 * NOTE: Before running this, you have to start the ActiveMQ broker. This 
 *  can be done by running $UIMA_HOME/bin/startBroker.sh
 *  
 */
public class Application {

	public static final String SAMPLE_DOCUMENT_TEXT = "some input text to process";
	
	/** Stores the time that the pipeline was started. */
	private static long before = -1;
	
	
	public static void main(String[] args) throws CASException, Exception 
	{
		// prepares a listener for when the analysis engine is complete
		UimaAsBaseCallbackListener asyncListener = new UimaAsBaseCallbackListener() {
			/**
			 * This will be called once the text is processed.
			 */
			@Override
			public void entityProcessComplete(CAS output, EntityProcessStatus aStatus) {
				// record the time that this was complete
				long after = System.currentTimeMillis();

				// display the time spent processing the text
				System.out.println("Time spent in pipeline: " + (after - before));
				
				// confirm that the expected annotations were added to the CAS
				System.out.println("Confirming what was added...");
				FSIterator<AnnotationFS> annotationsIterator = output.getAnnotationIndex().iterator();
				while (annotationsIterator.hasNext()){
					AnnotationFS annotation = (AnnotationFS) annotationsIterator.next();
					System.out.println("  Found: " + annotation.getClass().getName());
				}
			}
		};
		
		// constructs a class to create and run a UIMA pipeline
		Pipeline uimaPipeline = new Pipeline(asyncListener);

	}
}