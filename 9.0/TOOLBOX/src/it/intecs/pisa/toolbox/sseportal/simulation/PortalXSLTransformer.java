/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.sseportal.simulation;

import it.intecs.pisa.common.tbx.ServiceAdditionalParameters;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.OutputStream;
import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class PortalXSLTransformer {

    public static void transform(String service, String operation, String target,Document xml, OutputStream out) {
            transform(service,operation,target,xml,new StreamResult(out));
    }
    
    public static void transform(String service, String operation, String target,Document xml, Result result) {
         DOMUtil util;
        Toolbox tbxServlet;
        File serviceRoot;
        File propsDir;
        ServiceAdditionalParameters servAddParameters;
        String xslTargetName;
        String xslFileStr;
        File xslFile;
        Document xslDocument;
        Transformer transformer;
        TransformerFactory transformerFactory;
        PortalXSLUriResolver resolver;

        try {
            util = new DOMUtil(true);
            resolver=new PortalXSLUriResolver(service);
            tbxServlet = Toolbox.getInstance();

            serviceRoot = tbxServlet.getServiceRoot(service);
            propsDir = new File(serviceRoot, "AdditionalResources/SSEPortalXSL");

            servAddParameters = new ServiceAdditionalParameters(propsDir, "INTECS_TEST_OPERATION");
            xslTargetName = servAddParameters.getParameter(operation + "."+target);
            xslFileStr = servAddParameters.getParameter(operation + ".XSLFILE");
            xslFile = new File(serviceRoot, xslFileStr);

            xslDocument = util.fileToDocument(xslFile);
            transformerFactory=TransformerFactory.newInstance();
            transformerFactory.setURIResolver(resolver);
            
            transformer = transformerFactory.newTransformer(new DOMSource(xslDocument));
         //   transformer.setURIResolver(resolver);
            transformer.setParameter("part", xslTargetName);
            //transformer.setParameter("displayFilter", "ALL");
            

            transformer.transform(new DOMSource(xml), result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
