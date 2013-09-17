/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.pluginscore.InterfacePluginManager;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class CreateServiceCommand extends RESTManagerCommandPlugin{
    protected static final String INTERFACETYPE = "interfaceType";

    @Override
    public Document executeCommand(String cmd, Document inputDoc) {
        Document responseDoc=null;
        Hashtable<String,String> serviceInfo;

        try
        {
            serviceInfo=extractInfo(inputDoc);
            String serviceName=createService(serviceInfo);
            responseDoc=createResponse(serviceName);
        }catch(Exception e)
        {

        }
        return responseDoc;
    }

    private Hashtable<String, String> extractInfo(Document inputDoc) {
        Hashtable<String,String> serviceInfo;
        String interfType;
        Element rootEl,el;

        try
        {
            rootEl=inputDoc.getDocumentElement();
            el=DOMUtil.getChildByLocalName(rootEl, INTERFACETYPE);
            interfType=el.getTextContent();

            serviceInfo=new Hashtable<String,String>();
            serviceInfo.put(INTERFACETYPE, interfType);

            NodeList children = rootEl.getChildNodes();
            for(int i=0;i<children.getLength();i++)
            {
                Node node=children.item(i);
                if(node instanceof Element)
                {
                    el=(Element) node;
                    String key;

                    key=el.getLocalName();
                    if(key.startsWith(interfType))
                        key=key.substring(interfType.length());

                    serviceInfo.put(key, el.getTextContent());
                }
            }

            return serviceInfo;
        }
        catch(Exception e)
        {
            return null;
        }
    }

    private String createService(Hashtable<String, String> serviceInfo) throws Exception {
        Service descr;
        TBXService service = null;
        String serviceName=null;
        ServiceManager serviceManager=null;

        try {
            descr = new Service();

            serviceName=serviceInfo.get("ServiceName");
            descr.setServiceName(serviceName);

            InterfacePluginManager interfman;
            Interface interf;
            Interface opInterface;
            File schemaDir;
            File serviceSchemaDir;

            interfman = InterfacePluginManager.getInstance();

            interf = (Interface) interfman.getInterfaceDescription(serviceInfo.get("interfaceName"),
                                                                    serviceInfo.get("interfaceVersion"),
                                                                    serviceInfo.get("interfaceType"),
                                                                    serviceInfo.get("interfaceMode"));

            opInterface=(Interface) interf.clone();
            opInterface.removeAllOperations();
            descr.setImplementedInterface(opInterface);
            descr.adjustReferences();

            schemaDir = interfman.getSchemaDirForInterface(serviceInfo.get("interfaceName"),
                                                                    serviceInfo.get("interfaceVersion"),
                                                                    serviceInfo.get("interfaceType"),
                                                                    serviceInfo.get("interfaceMode"));

            Toolbox tbxServlet=Toolbox.getInstance();
            serviceSchemaDir = new File(tbxServlet.getServiceRoot(serviceName), "Schemas");
            serviceSchemaDir.mkdirs();
            IOUtil.copyDirectory(schemaDir, serviceSchemaDir);

            descr.setImplementedInterface(opInterface);

            if (serviceInfo.get("ServiceAbstract") != null) {
                descr.setServiceAbstract(new ByteArrayInputStream(serviceInfo.get("ServiceAbstract").getBytes()));
            }
            if (serviceInfo.get("ServiceDescription") != null) {
                descr.setServiceDescription(new ByteArrayInputStream(serviceInfo.get("ServiceDescription").getBytes()));
            }
            if (serviceInfo.get("SSLCertificateLocation") != null) {
                descr.setSSLcertificate(serviceInfo.get("SSLCertificateLocation"));
            }
            descr.setSuspendMode(serviceInfo.get("SuspendMode"));
            if (serviceInfo.get("ServiceQueueIncoming") != null) {
                descr.setQueuing(Boolean.parseBoolean(serviceInfo.get("ServiceQueueIncoming")));
            } else {
                descr.setQueuing(false);
            }

            serviceManager=ServiceManager.getInstance();
            serviceManager.createService(descr);

            service = serviceManager.getService(serviceName);
        } catch (Exception e) {
            File serviceDir;

            serviceManager=ServiceManager.getInstance();
            if (service != null) {
                serviceManager.deleteService(serviceName);
            }

            Toolbox tbxServlet;
            tbxServlet=Toolbox.getInstance();
            serviceDir=tbxServlet.getServiceRoot(serviceName);
            if(serviceDir.exists())
               IOUtil.rmdir(serviceDir);

            throw e;
        }

        return serviceName;
    }

    private Document createResponse(String serviceName) {
        Document doc;
        DOMUtil util;

        util=new DOMUtil();
        doc=util.newDocument();

        Element rootEl=doc.createElement("response");
        doc.appendChild(rootEl);

        Element servNameEl=doc.createElement("serviceName");
        servNameEl.setTextContent(serviceName);

        rootEl.appendChild(servNameEl);

        return doc;
    }

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }


    
}
