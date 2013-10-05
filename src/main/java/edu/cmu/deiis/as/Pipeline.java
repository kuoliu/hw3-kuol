package edu.cmu.deiis.as;

import java.util.HashMap;
import java.util.Map;

import org.apache.uima.aae.client.UimaAsBaseCallbackListener;
import org.apache.uima.aae.client.UimaAsynchronousEngine;
import org.apache.uima.adapter.jms.client.BaseUIMAAsynchronousEngine_impl;
import org.apache.uima.cas.CAS;
import org.apache.uima.cas.CASException;

/**
 * Description: Deploy the pipeline for homework2. 
 * 
 */
public class Pipeline {

    private UimaAsynchronousEngine uimaAsEngine = null;
    
    public Pipeline(UimaAsBaseCallbackListener callback) throws Exception 
    {
        System.out.println("UIMA application - parallel - Kuo Liu");
        System.out.println("==============================================");

        // creating UIMA analysis engine
        uimaAsEngine = new BaseUIMAAsynchronousEngine_impl();

        // preparing map for use in deploying services
        Map<String,Object> deployCtx = new HashMap<String,Object>();
        deployCtx.put(UimaAsynchronousEngine.DD2SpringXsltFilePath, System.getenv("UIMA_HOME") + "/bin/dd2spring.xsl");
        deployCtx.put(UimaAsynchronousEngine.SaxonClasspath, "file:" + System.getenv("UIMA_HOME") + "/saxon/saxon8.jar");

        System.out.println("Deploying UIMA services");
        uimaAsEngine.deploy("./src/main/resources/hw2-kuol-aae-deploy.xml", deployCtx);
        
        // add callback listener that will be informed when processing completes
        uimaAsEngine.addStatusCallbackListener(callback);

    }
    
}