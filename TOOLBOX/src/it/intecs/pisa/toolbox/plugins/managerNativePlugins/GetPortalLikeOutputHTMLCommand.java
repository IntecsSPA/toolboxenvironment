/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.common.tbx.ServiceAdditionalParameters;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.db.InstanceResources;
import it.intecs.pisa.toolbox.service.instances.InstanceInfo;
import it.intecs.pisa.toolbox.sseportal.simulation.PortalXSLTransformer;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class GetPortalLikeOutputHTMLCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
       String service;
        String operation;
        OutputStream out;
        Document outputMessageXML = null;
        File serviceRoot;
        File propsDir;
        ServiceAdditionalParameters servAddParameters;
        String portalNodeNamespace;
        String portalNodeName;
        Document indoc;
        InputStream resourceStream;
        DOMUtil util;
        Document messageDoc;
        Document noSOAPDocument;
        File htmlHead;
        File htmlFoot;
        
        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("text/html; charset=utf-8");

            out = resp.getOutputStream();
          
            util = new DOMUtil();

            String id = req.getParameter("id");
            long serviceInstanceId;

            serviceInstanceId=InstanceResources.getResourceAssociatedInstanceId(new Long(id));
            service=InstanceInfo.getServiceNameFromInstanceId(serviceInstanceId);
            operation=InstanceInfo.getOperationNameFromInstanceId(serviceInstanceId);

            resourceStream = getResource(req);

            messageDoc = util.inputStreamToDocument(resourceStream);
            noSOAPDocument=removeSOAP(messageDoc);
            
            
            htmlHead=new File(pluginDir,"resources/portalHead.txt");
            htmlFoot=new File(pluginDir,"/resources/portalFoot.txt");
   
            IOUtil.copy(new FileInputStream(htmlHead), out);
            PortalXSLTransformer.transform(service, operation, "OUTPUTHTMLTARGET", noSOAPDocument, out);
            IOUtil.copy(new FileInputStream(htmlFoot), out);
             
            out.flush();
        } catch (Exception ex) {
            String errorMsg = "Cannot get portal like output HTML";
            throw new GenericException(errorMsg);
        }
    }

}
