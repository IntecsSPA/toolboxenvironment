/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.plugins.managerNativePlugins.*;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class ConfigureCapabilitiesCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName;
        Document capabilitiesDoc;
        DOMUtil util;
        File serviceRootDir;
        File capabilitiesFile;

        try {
            util=new DOMUtil();

            serviceName=req.getParameter("serviceName");
            capabilitiesDoc=util.inputStreamToDocument(req.getInputStream());

            serviceRootDir=Toolbox.getInstance().getServiceRoot(serviceName);
            capabilitiesFile=new File(serviceRootDir,"AdditionalResources/EOCat/ServiceProviderInfo.xml");

            DOMUtil.dumpXML(capabilitiesDoc, System.out, true);
            DOMUtil.dumpXML(capabilitiesDoc, capabilitiesFile);
        } catch (Exception ex) {
             String errorMsg = "Error configuring capabilities: " + CDATA_S + ex.getMessage() + CDATA_E;
            throw new GenericException(errorMsg);
        }
    }

     
}
