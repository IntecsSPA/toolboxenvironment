/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.util.wsil;

import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXSOAPInterface;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.net.URL;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class WSILBuilder {
    public static final String REFERENCED_NAMESPACE = "referencedNamespace";
    public static final String LOCATION = "location";
    public static final String VIEW_SERVICE_INFO = "/viewServiceInfoNotLogged.jsp?serviceName=";

    public static void createWSIL() throws Exception
    {
        TBXService[] services;
        ServiceManager servMan;
        Document wsilDoc;
        DOMUtil util;
        Element inspectionEl;
        Element abstractEl;
        Element serviceEl;
        Element descriptionEl;
        String serviceAbstract;
        Logger logger;
        Toolbox toolboxServlet;
        String wsdlurl;
        URL url;

        util=new DOMUtil();
        wsilDoc=util.newDocument();

        toolboxServlet=Toolbox.getInstance();
        logger=toolboxServlet.getLogger();

        logger.info("Updating WSIL files...");

        inspectionEl=wsilDoc.createElement("wsil:inspection");
        inspectionEl.setAttribute("xmlns:wsil", "http://schemas.xmlsoap.org/ws/2001/10/inspection");
        wsilDoc.appendChild(inspectionEl);

        servMan=ServiceManager.getInstance();
        services = servMan.getServicesAsArray();

        for(TBXService service:services)
        {
            try
            {
            serviceEl=wsilDoc.createElement("wsil:service");
            inspectionEl.appendChild(serviceEl);

            abstractEl=wsilDoc.createElement("wsil:abstract");
            serviceAbstract = IOUtil.inputToString(service.getServiceAbstract());
            abstractEl.setTextContent(serviceAbstract);

            serviceEl.appendChild(abstractEl);

            
                TBXSOAPInterface interf;
                interf=(TBXSOAPInterface) service.getImplementedInterface();
                if(interf.hasWSDL())
                {
                    wsdlurl=service.getWSDLUrl();
                    url=new URL(wsdlurl);
                    url.openStream();

                    descriptionEl=wsilDoc.createElement("wsil:document");
                    descriptionEl.setAttribute("referencedNamespace", "http://schemas.xmlsoap.org/wsdl/");
                    descriptionEl.setAttribute("location",wsdlurl );

                    serviceEl.appendChild(descriptionEl);
                }

                Toolbox tbx;

                tbx=Toolbox.getInstance();

                descriptionEl = wsilDoc.createElement("wsil:description");
                descriptionEl.setAttribute(REFERENCED_NAMESPACE, "");
                descriptionEl.setAttribute(LOCATION, tbx.getPublicAddress()+ VIEW_SERVICE_INFO + service.getServiceName());
                serviceEl.appendChild(descriptionEl);

            }
            catch(Exception e)
            {

            }

        }
        File wsilDir = new File(toolboxServlet.getRootDir(), Toolbox.WSIL);
        File deployedWSIL = new File(wsilDir, Toolbox.WSIL_FILE);
        DOMUtil.dumpXML(wsilDoc, deployedWSIL);

        logger.info("deployed service WSIL file: " + deployedWSIL.getAbsolutePath());
    }
}
