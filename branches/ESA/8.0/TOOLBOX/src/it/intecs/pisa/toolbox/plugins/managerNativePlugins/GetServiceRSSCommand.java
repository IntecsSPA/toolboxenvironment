/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.managerNativePlugins;


import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.toolbox.service.log.ServiceLogHandler;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano
 */
public class GetServiceRSSCommand extends NativeCommandsManagerPlugin{

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
      TransformerFactory factory;
        Transformer transformer;
        File xslFile;
        String service="<UNKNOWN SERVICE>";
        InputStream logStream;
        DOMSource source;
        DOMUtil util;
        Document document;
        String tz;
        SimpleDateFormat formatter;
        ServiceManager serviceManager;

        try {
            util = new DOMUtil();
            resp.setContentType("text/xml");
            service = req.getParameter("serviceName");
            document = util.newDocument();
            serviceManager=ServiceManager.getInstance();

            formatter = new SimpleDateFormat("Z");
            tz = formatter.format(new Date());

            xslFile = new File(tbxServlet.getRootDir(), "WEB-INF/XSL/logToRSS.xsl");

            Document xslDocument = util.fileToDocument(xslFile);
            transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
            transformer.setParameter("timeZone", tz);
            transformer.transform(new StreamSource(ServiceLogHandler.extractLog(service, 0)), new DOMResult(document));

            sendXMLAsResponse(resp,document);
        } catch (Exception io) {
             throw new GenericException("Cannot get RSS feed for service "+service);
        }
    }

}
