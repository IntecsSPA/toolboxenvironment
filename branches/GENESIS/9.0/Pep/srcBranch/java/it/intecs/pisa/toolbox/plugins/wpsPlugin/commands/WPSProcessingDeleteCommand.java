

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
public class WPSProcessingDeleteCommand extends NativeCommandsManagerPlugin{


     public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String processingName="";
        String serviceName=req.getParameter("serviceName");
        processingName=req.getParameter("processingName");
        String async=req.getParameter("async");
        String engineType=req.getParameter("engineType");
        Document documentResponse=null;
        WPSCommands commands= new WPSCommands();
        documentResponse=commands.removeWPSProcess(serviceName,processingName,Boolean.parseBoolean(async),engineType);
        resp.getOutputStream().print(DOMUtil.getDocumentAsString(documentResponse));

    }

}
