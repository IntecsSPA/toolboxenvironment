
package it.intecs.pisa.archivingserver.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.intecs.pisa.archivingserver.prefs.ChainTypesPrefs;
import it.intecs.pisa.archivingserver.prefs.MetadataPrefs;
import it.intecs.pisa.archivingserver.prefs.PreProcessingPrefs;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.archivingserver.prefs.WatchPrefs;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Andrea Marongiu
 */
public class ManagerServlet extends HttpServlet {
   
    protected static final String REST_RESOURCE_CONFIG = "config";
    protected static final String REST_RESOURCE_METADATA_PROCESSING = "config/metadata/processing";
    
    protected static final String REST_RESOURCE_CHAIN_TYPES = "config/chaintypes";
    protected static final String REST_RESOURCE_PRE_PROCESSING = "config/preprocessing";
    protected static final String REST_RESOURCE_WATCH_LIST = "config/watchlist";
    protected static final String REST_RESOURCE_LOG_LEVEL = "config/log/level";
    protected static final String REST_RESOURCE_AUTHENTICATE = "authenticate";
    
    private String rootDirStr=null;
    private File  rootDir = null;

    @Override
    public void init() throws ServletException {
        super.init();
        rootDirStr=getServletContext().getRealPath("/");
        rootDir = new File(rootDirStr);
    }

    
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String uri;
        String jsonStr="";

        if (authenticateUser(request)) {

            uri = request.getRequestURI();
            if (uri.endsWith(REST_RESOURCE_CONFIG)) {
                jsonStr = Prefs.getJSONStringConfiguration(this.rootDir);
            } else 
                if(uri.endsWith(REST_RESOURCE_METADATA_PROCESSING)){
                    try {
                        jsonStr = MetadataPrefs.load(this.rootDir);
                    } catch (Exception ex) {
                        Logger.getLogger(ManagerServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }else 
                    if(uri.endsWith(REST_RESOURCE_PRE_PROCESSING)){
                        try {
                            jsonStr = PreProcessingPrefs.load(this.rootDir);
                        } catch (Exception ex) {
                            Logger.getLogger(ManagerServlet.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }else 
                        if(uri.endsWith(REST_RESOURCE_WATCH_LIST)){
                            try {
                                jsonStr = WatchPrefs.load(this.rootDir); 

                            } catch (Exception ex) {
                               response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                               return;
                            }
                        }else 
                        if(uri.endsWith(REST_RESOURCE_CHAIN_TYPES)){
                            try {
                                jsonStr = ChainTypesPrefs.load(this.rootDir); 

                            } catch (Exception ex) {
                               response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                               return;
                            } 
                }else{
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().print(jsonStr);
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        String uri;
        JsonObject obj = null;
        if (authenticateUser(request)) {
            obj = readConfigurationFromRequest(request.getReader());

            uri = request.getRequestURI();
            if (uri.endsWith(REST_RESOURCE_CONFIG)) {
                saveConfiguration(obj);
                response.setHeader("Content-Type", "application/json");
                JsonObject outputJson = new JsonObject();
                outputJson.addProperty("success", true);
                Gson gson = new Gson();
                response.getOutputStream().print(gson.toJson(outputJson));
  
            } else 
                if (uri.endsWith(REST_RESOURCE_METADATA_PROCESSING)) {
                    MetadataPrefs.save(rootDir, obj);
                    response.setHeader("Content-Type", "application/json");
                    JsonObject outputJson = new JsonObject();
                    outputJson.addProperty("success", true);
                    Gson gson = new Gson();
                    response.getOutputStream().print(gson.toJson(outputJson));
                    return;
            } else 
                if (uri.endsWith(REST_RESOURCE_PRE_PROCESSING)) {
                    PreProcessingPrefs.save(rootDir, obj);
                    response.setHeader("Content-Type", "application/json");
                    JsonObject outputJson = new JsonObject();
                    outputJson.addProperty("success", true);
                    Gson gson = new Gson();
                    response.getOutputStream().print(gson.toJson(outputJson));
                    return;
            } else
                if(uri.endsWith(REST_RESOURCE_WATCH_LIST)){
                    WatchPrefs.save(rootDir, obj);
                    response.setHeader("Content-Type", "application/json");
                    JsonObject outputJson = new JsonObject();
                    outputJson.addProperty("success", true);
                    Gson gson = new Gson();
                    response.getOutputStream().print(gson.toJson(outputJson));
                    return;
                    
                }
            else
                if(uri.endsWith(REST_RESOURCE_CHAIN_TYPES)){
                    ChainTypesPrefs.save(rootDir, obj);
                    response.setHeader("Content-Type", "application/json");
                    JsonObject outputJson = new JsonObject();
                    outputJson.addProperty("success", true);
                    Gson gson = new Gson();
                    response.getOutputStream().print(gson.toJson(outputJson));
                    return;
                    
                }
                        
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }
    
    
    

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    
    
     /**
     * This method checks client authorization.
     * @param request Request class containing all information to use to authenticate
     * the user
     * @return True or False depending of the user authorization.
     */
    private boolean authenticateUser(HttpServletRequest request) {
        
        return true;
    }
    
    
   
    
    /**
     *
     * @param request
     * @return
     */
    private void saveConfiguration(JsonObject jsonObj) throws FileNotFoundException, IOException {
        
        String deleteAfter=jsonObj.get("delete.after").getAsString()+
                           jsonObj.get("delete.after.uom").getAsString();
        
        jsonObj.remove("delete.after");
        jsonObj.remove("delete.after.uom");
        
        jsonObj.addProperty("delete.after", deleteAfter);
        
        Prefs.save(rootDir, jsonObj);

    }
    
    
    
    
    
    
    
    /**
     *
     * @param input
     * @return
     */
    private JsonObject readConfigurationFromRequest(Reader input) {
        JsonParser parser;
        JsonObject obj;

        parser = new JsonParser();
        obj = (JsonObject) parser.parse(input);

        return obj;
    }
    
    
    

}
