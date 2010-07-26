/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.saxon.SaxonXSLT;
import it.intecs.pisa.util.saxon.SaxonXSLTParameter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PipedInputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.sax.SAXSource;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CreationWizardInterfaceCommand extends RESTManagerCommandPlugin{
  
    @Override
    public Document executeCommand(String cmd, Document inputDoc,Hashtable<String,String> headers,Hashtable<String,String> params) {
        try {
            DOMUtil domUtil;

            SAXSource docSource = new SAXSource(new InputSource(new ByteArrayInputStream("<?xml version=\"1.0\" encoding=\"UTF-8\"?><tbx/>".getBytes())));

            File xsltFile;
            xsltFile=new File(pluginDir,"resources/xslt/creationWizardTemplate.xsl");

            SAXSource xsltDoc;
            xsltDoc= new SAXSource(new InputSource(new FileInputStream(xsltFile)));
            SaxonXSLT saxonUtil = new SaxonXSLT();

            int index=cmd.lastIndexOf("/");
            String interfaceType=cmd.substring(index+1);

            ArrayList<SaxonXSLTParameter> parameters= new ArrayList();
            parameters.add(new SaxonXSLTParameter("interfaceType",interfaceType));

            domUtil=new DOMUtil();

            PipedInputStream pipeInput = null;
            pipeInput = saxonUtil.saxonXSLPipeTransform(docSource, xsltDoc, parameters,"xml");
            Document document = domUtil.inputStreamToDocument(pipeInput);
            return document;
        } catch (Exception ex) {
           return null;
        }
    }

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
