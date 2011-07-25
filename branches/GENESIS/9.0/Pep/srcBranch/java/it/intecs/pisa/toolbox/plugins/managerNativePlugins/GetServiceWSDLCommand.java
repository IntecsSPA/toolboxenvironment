/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.XSLT;
import java.io.File;
import java.io.FileInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class GetServiceWSDLCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
         String serviceName;
         File wsdlFile;
         String output;
         File xsltFile;

         try {
            serviceName = req.getParameter("serviceName");
            output=req.getParameter("output");

            String panelResource=req.getParameter("panelResource");
            wsdlFile=new File(tbxServlet.getRootDir(),"WSDL/"+serviceName+"/"+serviceName+".wsdl");
            FileInputStream docStream = new FileInputStream(wsdlFile);

            if(output!=null && output.equals("text"))
            {
                xsltFile=new File(tbxServlet.getRootDir(),"WEB-INF/XSL/resourceDisplay.xsl");
                resp.setContentType("text/html");

                DOMUtil du= new DOMUtil();
                Document xsltDocument=du.fileToDocument(xsltFile);
                if(panelResource != null)
                    XSLT.addParameter(xsltDocument, "PanelResource", "'"+panelResource+"'");
                
                XSLT.transform(new DOMSource(xsltDocument), new StreamSource(docStream), new StreamResult(resp.getOutputStream()));
            }
            else
            {
                IOUtil.copy(docStream, resp.getOutputStream());
            }
        } catch (Exception ex) {
            String errorMsg = "Error getting WSDL: " + CDATA_S + ex.getMessage() + CDATA_E;
           
            throw new GenericException(errorMsg);
        }
    }

}
