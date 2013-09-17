/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.XSLT;
import java.io.File;
import java.io.InputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class GetXMLResourcesCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
         InputStream docStream;
         Document doc=null;
         String id;
         String output;
         File xsltFile;

         try {
            id = req.getParameter("id");
            output=req.getParameter("output");

            doc=XMLResourcesPersistence.getInstance().retrieveXML(id);
            docStream=DOMUtil.getDocumentAsInputStream(doc);

            if(output!=null && output.equals("text"))
            {
                xsltFile=new File(tbxServlet.getRootDir(),"WEB-INF/XSL/resourceDisplay.xsl");
                resp.setContentType("text/html");
                
                XSLT.transform(new StreamSource(xsltFile), new StreamSource(docStream), new StreamResult(resp.getOutputStream()));
            }
            else
            {
                IOUtil.copy(docStream, resp.getOutputStream());
                resp.setContentType("text/xml");
            }
        } catch (Exception ex) {
            String errorMsg = "Error getting XML resource: " + CDATA_S + ex.getMessage() + CDATA_E;           
            throw new GenericException(errorMsg);
        }
    }

}
