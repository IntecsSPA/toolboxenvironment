/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.XSLT;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 *
 * @author Massimiliano
 */
public class GetXMLRemoteResourceCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
         InputStream docStream;
         String url;
         String output;
         File xsltFile;

         try {
            url = req.getParameter("location");
            output=req.getParameter("output");

            docStream= new URL(url).openStream();

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
