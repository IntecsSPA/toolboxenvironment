/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.managerNativePlugins;

import com.google.gson.JsonObject;
import it.intecs.pisa.pluginscore.ManagerCommandPlugin;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.pluginscore.exceptions.GenericException;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Massimiliano
 */
public abstract class NativeCommandsManagerPlugin extends ManagerCommandPlugin {

    public static final String CDATA_S = "<![CDATA[";
    public static final String CDATA_E = "]]>";
    protected Toolbox tbxServlet;
    protected Logger logger;

    public NativeCommandsManagerPlugin() {
        tbxServlet = Toolbox.getInstance();
        logger = tbxServlet.getLogger();
    }

    protected void sendXMLAsResponse(HttpServletResponse resp, Document doc) throws Exception {
        resp.setContentType("text/xml");
        IOUtil.copy(DOMUtil.getDocumentAsInputStream(doc), resp.getOutputStream());
    }

    protected void sendXMLAsResponse(HttpServletResponse resp, Element el) throws Exception {
        resp.setContentType("text/xml");
        IOUtil.copy(DOMUtil.getElementAsInputStream(el), resp.getOutputStream());
    }

    protected InputStream getResource(HttpServletRequest request) throws Exception {
        String id;
        String resourceKey;
        InputStream resourceStream = null;
        ServiceManager serviceManager;
        File f;
        Document doc;
        try {
            id = request.getParameter("id");
            resourceKey = request.getParameter("resourceKey");

            serviceManager = ServiceManager.getInstance();

            if (resourceKey!=null && resourceKey.startsWith("file:/")) {
                resourceKey = resourceKey.replaceAll(" ", "%20");
                f = new File(new java.net.URI(resourceKey));

                resourceStream = new FileInputStream(f);
            } else {
               doc=XMLResourcesPersistence.getInstance().retrieveXML(id);
               resourceStream=DOMUtil.getDocumentAsInputStream(doc);
            }
        } catch (Exception e) {
            throw new GenericException("Cannot get resource");

        }

        return resourceStream;
    }

    

    protected Document removeSOAP(Document messageDoc) throws Exception {
        Document xsl;
        Document result;
        File xslFile;
        DOMUtil util;
        TransformerFactory transformerFactory;
        Transformer transformer;

        util = new DOMUtil();
        result = util.newDocument();

        xslFile = new File(tbxServlet.getRootDir(), "WEB-INF/XSL/SOAP_remover.xsl");
        xsl = util.fileToDocument(xslFile);

        transformerFactory = TransformerFactory.newInstance();
        transformer = transformerFactory.newTransformer(new DOMSource(xsl));

        transformer.transform(new DOMSource(messageDoc), new DOMResult(result));

        return result;
    }

    protected Document getXMLFromInput(HttpServletRequest req) {
        Document doc = null;
        DOMUtil util;

        try {
            util = new DOMUtil();

            doc = util.inputStreamToDocument(req.getInputStream());
        } catch (Exception e) {
            doc = null;
        }
        return doc;
    }

    protected Hashtable<String, FileItem> parseMultiMime(HttpServletRequest req) throws Exception {
        Hashtable<String, FileItem> mimeparts;
        Iterator iter;
        FileItem item;

        DiskFileUpload upload = new DiskFileUpload();
        List items = upload.parseRequest(req);
        iter = items.iterator();

        mimeparts = new Hashtable<String, FileItem>();

        while (iter.hasNext()) {
            item = (FileItem) iter.next();
            mimeparts.put(item.getFieldName(), item);
        }

        return mimeparts;
    }

    protected String getStringFromMimeParts(Hashtable<String, FileItem> parts, String key) {
        try {
            return parts.get(key).getString();
        } catch (Exception e) {
            return null;
        }
    }

    public JsonObject executeCommand(String method, JsonObject request)  {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Document executeCommand(String cmd, Document inputDoc) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
