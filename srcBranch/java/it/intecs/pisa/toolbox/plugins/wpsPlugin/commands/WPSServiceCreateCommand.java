
package it.intecs.pisa.toolbox.plugins.wpsPlugin.commands;


import it.intecs.pisa.toolbox.plugins.managerNativePlugins.NativeCommandsManagerPlugin;
import it.intecs.pisa.toolbox.plugins.wpsPlugin.manager.WPSCommands;
import it.intecs.pisa.util.DOMUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Andrea Marongiu
 */
public class WPSServiceCreateCommand extends NativeCommandsManagerPlugin {



    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Document serviceInformationDocument=null;
        DOMUtil domUtil=new DOMUtil();
        serviceInformationDocument = domUtil.inputStreamToDocument(req.getInputStream());
        WPSCommands command= new WPSCommands();
        Document responseDocument=command.createWPSService(serviceInformationDocument);
        resp.getOutputStream().print(DOMUtil.getDocumentAsString(responseDocument));
    }

}
