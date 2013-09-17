/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class HasSSEStylesheetCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName;
        String operationName;
        TBXService service;
        DOMUtil util;
        Document doc;
        Element rootEl;
        boolean result=false;
        String hasMap="false";
        Properties props;
        File stylesheetprops;
        props=new Properties();
        ServiceManager serviceManager;
        try {

            util = new DOMUtil();
            
            serviceName = req.getParameter("serviceName");
            operationName = req.getParameter("operationName");
            serviceManager=ServiceManager.getInstance();
            service = serviceManager.getService(serviceName);
            
            doc=util.newDocument();
            rootEl=doc.createElement("hasSSEStylesheet");
            doc.appendChild(rootEl);
            
            result=service!=null && service.hasSSEStylesheet(operationName);
            
            stylesheetprops=new File(service.getServiceRoot(),"AdditionalResources/SSEPortalXSL/INTECS_TEST_OPERATION.properties");
            if(stylesheetprops.exists())
            {
                props.load(new FileInputStream(stylesheetprops));
                hasMap=props.get(operationName+".HASMAP")!=null ?(String)props.get(operationName+".HASMAP"):"false";
            }
            rootEl.setAttribute("value", Boolean.toString(result));
            rootEl.setAttribute("hasMap",hasMap);
            
            IOUtil.copy(DOMUtil.getDocumentAsInputStream(doc), resp.getOutputStream());
        } catch (Exception e) {
            String errorMsg = "Cannot establish if service has SSE stylesheet";
            throw new GenericException(errorMsg);
        }
    }

}
