/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.common.communication.ServerDebugConsole;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.NetUtil;
import java.net.InetAddress;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public class InitDebugConsoleCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        Document initDbgDoc;
        DOMUtil domUtil;
        Element rootEl;
        boolean consoleStarted = false;
        ServerDebugConsole console = null;
        int port = 12345;
        InetAddress bindAddress = null;

        try {
            domUtil = new DOMUtil(true);
            initDbgDoc = domUtil.newDocument();
            rootEl = initDbgDoc.createElement("initDbgConsole");
            initDbgDoc.appendChild(rootEl);

            port=NetUtil.findFreePort();
            if(port==-1)
                port=12345;
            console = tbxServlet.initDebugConsole(port);
            consoleStarted = console != null;

            rootEl.setAttribute("inited", Boolean.toString(consoleStarted));

            if (consoleStarted) {
                bindAddress = console.getListeningAddress();

                rootEl.setAttribute("listeningAtAddress", bindAddress.getHostAddress());
                rootEl.setAttribute("listeningOnPort", Integer.toString(port));
            } else {
                rootEl.setAttribute("errorMsg", "Cannot initialize Debug Console");
            }

            sendXMLAsResponse(resp, initDbgDoc);
        } catch (Exception ex) {
            String errorMsg = "Error while initializing the Debug console";
           throw new GenericException(errorMsg);
        }
    }

}
