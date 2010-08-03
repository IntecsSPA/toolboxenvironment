/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import com.google.gson.JsonObject;
import http.utils.multipartrequest.MultipartRequest;
import http.utils.multipartrequest.ServletMultipartRequest;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
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

        IOUtil.copy(parser.getFileContents(parser.getFileParameterNames().nextElement().toString()), new FileOutputStream(outputFile));

        String contentType=req.getHeader("Content-Type");
        if(contentType==null)
            throw new Exception("No content type in request");

        InputStream outStream;
        if(contentType.equals("text/xml"))
        {
            Document doc;
            DOMUtil util;

            util=new DOMUtil();
            doc=util.fileToDocument(outputFile);
            DOMUtil.indent(doc);
            outStream=util.getDocumentAsInputStream(doc);
        }
        else
        {
            outStream=new FileInputStream(outputFile);
        }

        IOUtil.copy(outStream,resp.getOutputStream());
        outStream.close();
        outputFile.delete();
    }
}
