/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.XSLT;
import java.io.File;
import java.io.FileInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class GetServiceSchemaCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String serviceName;
        File schemaFile,schemaDir;
        ServiceManager servMan;
        String output;
        File xsltFile;

        try {
            serviceName = req.getParameter("serviceName");
            output=req.getParameter("output");

            servMan = ServiceManager.getInstance();
            TBXService service=servMan.getService(serviceName);

            Interface interf=service.getImplementedInterface();
            schemaDir=new File(tbxServlet.getRootDir(),"WSDL/"+serviceName);
            schemaFile=new File(schemaDir,interf.getSchemaRoot());

            FileInputStream docStream = new FileInputStream(schemaFile);
            if(output!=null && output.equals("text"))
            {
                xsltFile=new File(tbxServlet.getRootDir(),"WEB-INF/XSL/resourceDisplay.xsl");
                resp.setContentType("text/html");

                XSLT.transform(new StreamSource(xsltFile), new StreamSource(docStream), new StreamResult(resp.getOutputStream()));
            }
            else
            {
                IOUtil.copy(new FileInputStream(schemaFile), resp.getOutputStream());
            }
        } catch (Exception ex) {
            String errorMsg = "Error getting WSDL: " + CDATA_S + ex.getMessage() + CDATA_E;

            throw new GenericException(errorMsg);
        }
    }
}
