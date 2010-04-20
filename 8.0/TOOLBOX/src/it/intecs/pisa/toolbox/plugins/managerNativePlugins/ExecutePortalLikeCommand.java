/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.stream.XmlDirectivesFilterStream;
import it.intecs.pisa.common.tbx.ServiceAdditionalParameters;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXOperation;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.sseportal.simulation.MessageForXSLCreator;
import it.intecs.pisa.toolbox.sseportal.simulation.PortalXSLTransformer;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.SOAPMessageBuilder;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
public class ExecutePortalLikeCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName;
        String operationName;
        OutputStream out;
        TBXOperation operation;
        File serviceRoot;
        File propsDir;
        ServiceAdditionalParameters servAddParameters;
        Document inputDoc;
        TBXService service;
        Document soapRequestDocument;
        Document responseDocument;
        boolean isOperationAsynchronous = false;
        File htmlHead;
        File htmlFoot;
        Document indoc;
        Element rootEl;
        Element serviceEl;
        Element operationEl;
        FileOutputStream outputStream;
        String messageId;
        SimpleDateFormat formatter;
        String orderId;
        String soapAction;
        ServiceManager serviceManager;
        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/xml; charset=utf-8");

            indoc = this.getXMLFromInput(req);
            orderId = getOrderId(indoc);

            rootEl = indoc.getDocumentElement();

            serviceEl = DOMUtil.getChildByTagName(rootEl, "serviceName");
            serviceName = DOMUtil.getTextFromNode(serviceEl);
            operationEl = DOMUtil.getChildByTagName(rootEl, "operationName");
            operationName = DOMUtil.getTextFromNode(operationEl);

            serviceManager=ServiceManager.getInstance();
            service = serviceManager.getService(serviceName);
            operation=(TBXOperation) service.getImplementedInterface().getOperationByName(operationName);
            soapAction=operation.getSoapAction();
            isOperationAsynchronous = operation.getType().equals("asynchronous");

            serviceRoot = tbxServlet.getServiceRoot(serviceName);
            propsDir = new File(serviceRoot, "AdditionalResources/SSEPortalXSL");

            servAddParameters = new ServiceAdditionalParameters(propsDir, "INTECS_TEST_OPERATION");

            inputDoc = createInputMessage(indoc, servAddParameters, serviceName, operationName);
            //soapRequestDocument=createSoapMessage(isOperationAsynchronous,inputDoc);

            messageId = serviceName + "_" + operationName + "_";
            formatter = new SimpleDateFormat("yyMMddHHmmssSSS");
            messageId += formatter.format(new Date());
            soapRequestDocument = SOAPMessageBuilder.buildSOAPMessage(inputDoc, messageId, tbxServlet.getPublicAddress() + "/Push");

            responseDocument = service.processRequest(soapAction, soapRequestDocument, false);

            if (isOperationAsynchronous) {
                sendAsynchronousResponse(orderId,messageId,resp.getOutputStream());
            } else {

                sendSynchronousResponse(serviceName, operationName, responseDocument, resp.getOutputStream());
            }

        } catch (Exception ex) {
            String errorMsg = "Cannot get portal like input message";
            throw new GenericException(errorMsg);
        }
    }

    protected String getOrderId(Document doc) {
        Element rootEl;
        Element orderIdEl;

        rootEl = doc.getDocumentElement();
        orderIdEl = DOMUtil.getChildByTagName(rootEl, "orderId");

        return DOMUtil.getStringFromElement(orderIdEl);
    }

    protected Document createInputMessage(Document indoc, ServiceAdditionalParameters addparams, String service, String operation) throws IOException {
        String portalNodeNamespace;
        String portalNodeName;

        Document resultDoc;
        Document portalXML = null;
        DOMUtil util;

        util = new DOMUtil();

        portalNodeNamespace = addparams.getParameter(operation + ".PORTALXMLNAMESPACE");
        portalNodeName = addparams.getParameter(operation + ".PORTALXMLNODE");

        portalXML = MessageForXSLCreator.getPortalTemplateXML(portalNodeNamespace, portalNodeName, indoc.getDocumentElement());

        resultDoc = util.newDocument();
        PortalXSLTransformer.transform(service, operation, "INPUTXMLTARGET", portalXML, new DOMResult(resultDoc));

        return resultDoc;
    }

    protected void sendAsynchronousResponse(String orderId,String messageId,OutputStream out) throws Exception {
        DOMUtil util;
        Document doc;
        Element rootEl;
        Element orderIdEl;
        Element instanceIdEl;
       
       

        util = new DOMUtil();
        doc = util.newDocument();

        rootEl = doc.createElement("response");
        rootEl.setAttribute("type", "asynchronous");
        doc.appendChild(rootEl);

        orderIdEl = doc.createElement("orderId");
        orderIdEl.setAttribute("value", orderId);
        rootEl.appendChild(orderIdEl);

        instanceIdEl = doc.createElement("messageId");
        instanceIdEl.setAttribute("value", messageId);
        rootEl.appendChild(instanceIdEl);

        IOUtil.copy(DOMUtil.getDocumentAsInputStream(doc), out);
        out.flush();
    }

    protected void sendSynchronousResponse(String serviceName, String operationName, Document responseDocument, OutputStream out) throws IOException {
        File htmlHead;
        File htmlFoot;

        htmlHead = new File(pluginDir, "resources/portalHead.txt");
        htmlFoot = new File(pluginDir, "/resources/portalFoot.txt");

        IOUtil.copy(new FileInputStream(htmlHead), out);
        PortalXSLTransformer.transform(serviceName, operationName, "OUTPUTHTMLTARGET", responseDocument, out);
        IOUtil.copy(new FileInputStream(htmlFoot), out);

        out.flush();

    }

   
}
