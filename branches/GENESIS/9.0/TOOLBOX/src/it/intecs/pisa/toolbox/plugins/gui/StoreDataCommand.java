/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.plugins.gui;

import com.google.gson.JsonObject;
import http.utils.multipartrequest.MultipartRequest;
import http.utils.multipartrequest.ServletMultipartRequest;
import it.intecs.pisa.pluginscore.RESTManagerCommandPlugin;
import it.intecs.pisa.toolbox.constants.MiscConstants;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class StoreDataCommand extends RESTManagerCommandPlugin{

    @Override
    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        MultipartRequest parser=null;
        JsonObject outputJson = new JsonObject();
        try {
            parser = new ServletMultipartRequest(req, MiscConstants.MAX_READ_BYTES, MultipartRequest.ABORT_IF_MAX_BYES_EXCEEDED, null);
        } catch (IllegalArgumentException ex) {
           outputJson.addProperty("success", false);
           resp.setContentType("text/html");
           IOUtil.copy(JsonUtil.getJsonAsStream(outputJson),resp.getOutputStream());
        } catch (IOException ex) {
           outputJson.addProperty("success", false);
           resp.setContentType("text/html");
           IOUtil.copy(JsonUtil.getJsonAsStream(outputJson),resp.getOutputStream());
        }finally{
            File outputFile;
            String id = DateUtil.getCurrentDateAsUniqueId();
            outputFile = new File(pluginDir, "resources/storedData/" + id);
            IOUtil.copy(parser.getFileContents(parser.getFileParameterNames().nextElement().toString()), new FileOutputStream(outputFile));
            outputJson.addProperty("success", true);
            outputJson.addProperty("id", id);
            resp.setContentType("text/html");
            IOUtil.copy(JsonUtil.getJsonAsStream(outputJson),resp.getOutputStream());
        }
    }
}
