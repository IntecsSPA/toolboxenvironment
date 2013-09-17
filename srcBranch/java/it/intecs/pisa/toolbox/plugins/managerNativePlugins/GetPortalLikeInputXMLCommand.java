/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.common.tbx.ServiceAdditionalParameters;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.sseportal.simulation.MessageForXSLCreator;
import it.intecs.pisa.toolbox.sseportal.simulation.PortalXSLTransformer;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.OutputStream;
import java.util.Enumeration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class GetPortalLikeInputXMLCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
         String service;
        String operation;
        OutputStream out;
        Document portalXML = null;
        File serviceRoot;
        File propsDir;
        ServiceAdditionalParameters servAddParameters;
        String portalNodeNamespace;
        String portalNodeName;
        Document indoc;

        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/xml; charset=utf-8");

            service = req.getParameter("serviceName");
            operation = req.getParameter("operationName");

            serviceRoot = tbxServlet.getServiceRoot(service);
            propsDir = new File(serviceRoot, "AdditionalResources/SSEPortalXSL");

            servAddParameters = new ServiceAdditionalParameters(propsDir, "INTECS_TEST_OPERATION");
            portalNodeNamespace = servAddParameters.getParameter(operation + ".PORTALXMLNAMESPACE");
            portalNodeName = servAddParameters.getParameter(operation + ".PORTALXMLNODE");

            indoc = createXMLFlat(req);
            portalXML = MessageForXSLCreator.getPortalTemplateXML(portalNodeNamespace, portalNodeName, indoc.getDocumentElement());
            out = resp.getOutputStream();

            PortalXSLTransformer.transform(service, operation, "INPUTXMLTARGET", portalXML, out);

            out.flush();
        } catch (Exception ex) {
           String errorMsg = "Cannot get portal like input message";
            throw new GenericException(errorMsg);
        }
    }
    
    protected Document createXMLFlat(HttpServletRequest req)
    {
        Document doc;
        DOMUtil util;
        Enumeration enumer;
        String name;
        Element rootEl;
        Element fieldEl;
        
        util=new DOMUtil();
        doc=util.newDocument();
        
        rootEl=doc.createElement("flatRoot");
        doc.appendChild(rootEl);
        enumer=req.getParameterNames();
        
       while(enumer.hasMoreElements())
        {
            name=(String)enumer.nextElement();
            fieldEl=doc.createElement(name);
            DOMUtil.setTextToElement(doc, fieldEl, req.getParameter(name));        
            rootEl.appendChild(fieldEl);
        }
        
        return doc;
    }

}
