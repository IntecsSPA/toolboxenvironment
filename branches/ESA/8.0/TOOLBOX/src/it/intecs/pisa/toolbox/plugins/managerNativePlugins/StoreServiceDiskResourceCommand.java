/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class StoreServiceDiskResourceCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName="",filePath;
        InputStream body;
        DOMUtil util;
        Document xml;
        try {
            util=new DOMUtil();
            serviceName=req.getParameter("serviceName");
            filePath=req.getParameter("filePath");

            body=req.getInputStream();

            xml= util.inputStreamToDocument(body);


        } catch (Exception io) {
             throw new GenericException("Cannot get RSS feed for service "+serviceName);
        }
    }

}
