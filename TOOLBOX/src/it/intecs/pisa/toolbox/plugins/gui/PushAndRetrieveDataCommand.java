/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import http.utils.multipartrequest.MultipartRequest;
import http.utils.multipartrequest.ServletMultipartRequest;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.ByteArrayInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URLEncoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class PushAndRetrieveDataCommand extends RESTManagerCommandPlugin{

    @Override
    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        MultipartRequest parser;

        parser = new ServletMultipartRequest(req, MultipartRequest.MAX_READ_BYTES, MultipartRequest.IGNORE_FILES_IF_MAX_BYES_EXCEEDED, null);

        File outputFile;
        String id = DateUtil.getCurrentDateAsUniqueId();
        outputFile = new File(pluginDir, "resources/storedData/" + id);


        String fileName=parser.getFileParameterNames().nextElement().toString();
        IOUtil.copy(parser.getFileContents(fileName), new FileOutputStream(outputFile));

        String contentType=parser.getContentType(fileName);
        if(contentType==null)
            throw new Exception("No content type in request");

        String out;
        if(contentType.equals("text/xml"))
        {
            Document doc;
            DOMUtil util;
            util=new DOMUtil();
            doc=util.fileToDocument(outputFile);
            DOMUtil.indent(doc);
            out=URLEncoder.encode(DOMUtil.getDocumentAsString(doc), "UTF-8");
            
            
        }
        else
        {
          out=URLEncoder.encode(IOUtil.inputToString(new FileInputStream(outputFile)), "UTF-8");

        }
        resp.setContentType("text/html");
        IOUtil.copy( new ByteArrayInputStream(out.getBytes()),resp.getOutputStream());
        outputFile.delete();
    }
}
