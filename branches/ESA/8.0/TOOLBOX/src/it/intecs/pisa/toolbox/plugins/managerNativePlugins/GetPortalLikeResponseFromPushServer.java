/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.stream.XmlDirectivesFilterStream;
import it.intecs.pisa.common.tbx.ServiceAdditionalParameters;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.sseportal.simulation.MessageForXSLCreator;
import it.intecs.pisa.toolbox.sseportal.simulation.PortalXSLTransformer;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.Vector;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class GetPortalLikeResponseFromPushServer extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String messageId;
        File responseFile;
        OutputStream out;
        Document doc;
        DOMUtil util;
        String serviceName;
        String operationName;
        Document noSOAPDocument;
        try {
            util = new DOMUtil();

            messageId = req.getParameter("messageId");
            serviceName = req.getParameter("serviceName");
            operationName = req.getParameter("operationName");

            responseFile = new File(tbxServlet.getRootDir(), "Push/" + messageId + ".xml");
            resp.setContentType("text/xml");
            out = resp.getOutputStream();

            if (responseFile.exists()) {
                doc=util.fileToDocument(responseFile);
                noSOAPDocument=removeSOAP(doc);
                PortalXSLTransformer.transform(serviceName, operationName, "OUTPUTHTMLTARGET", noSOAPDocument, out);
            } else {

                doc = util.newDocument();
                doc.appendChild(doc.createElement("fileNotReceived"));

                IOUtil.copy(DOMUtil.getDocumentAsInputStream(doc), out);
            }
            out.flush();
            out.close();
        } catch (Exception ex) {
            String errorMsg = "Cannot get portal like input message";
            throw new GenericException(errorMsg);
        }
    }
}
