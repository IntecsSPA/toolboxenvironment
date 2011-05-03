/*
 * Archiving Service for TOOLBOX
 */
package it.intecs.pisa.archivingserver.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import it.intecs.pisa.archivingserver.chain.commands.ChainExecutor;
import it.intecs.pisa.archivingserver.chain.commands.CommandsConstants;
import it.intecs.pisa.archivingserver.data.ItemStatus;
import it.intecs.pisa.archivingserver.data.ItemStatusSerializer;
import it.intecs.pisa.archivingserver.data.StoreItem;
import it.intecs.pisa.archivingserver.data.StoreItemDeserializer;
import it.intecs.pisa.archivingserver.db.InternalDatabase;
import it.intecs.pisa.archivingserver.db.ItemRefDB;
import it.intecs.pisa.archivingserver.db.ItemStatusDB;
import it.intecs.pisa.archivingserver.db.ReverseCatalogueId;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.archivingserver.services.AutomaticFolderPublishingService;
import it.intecs.pisa.archivingserver.services.AutomaticItemDeleteService;
import it.intecs.pisa.archivingserver.services.FTPService;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.util.Properties;
import java.util.Timer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javawebparts.misc.chain.ChainContext;
import javawebparts.misc.chain.ChainManager;
import javawebparts.misc.chain.Result;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ArchivingServerServlet extends HttpServlet {

    protected static final String METHOD_STORE = "store";
    protected static final String METHOD_GET_STATUS = "getstatus";
    protected static final String METHOD_REVERSE_ID = "reverseid";
    protected static final String METHOD_DELETE = "delete";

    protected Timer timer;
    protected FTPService ftpService;
    protected String rootDirStr;
    protected File rootDir;
    protected File workspaceDir;
    
    
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
        String requestURI;

        response.setHeader("Content-Type", "application/json");
        
        requestURI = request.getRequestURI();
        if (requestURI.contains(METHOD_GET_STATUS)) {
            getItemStatus(request,response);
        }
        else if(requestURI.contains(METHOD_REVERSE_ID))
        {
            reverseId(request,response);
        }
        else {
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
        String requestURI;

        response.setHeader("Content-Type", "application/json");
        
        requestURI = request.getRequestURI();
        if (requestURI.contains(METHOD_STORE)) {
            storeItem(request, response);
        } else {
            response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
        }
    }

    /**
     * andles the HTTP <code>DELETE</code> method.
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String requestURI;

        requestURI = request.getRequestURI();
        if (requestURI.contains(METHOD_DELETE)) {
            deleteItem(request, response);
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
        return "Archiving service for TOOLBOX";
    }// </editor-fold>

    private void storeItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream in;
        JsonObject inputJson = null;
        String itemId=null;
        String errorReason=null;
        boolean success=false;

        try {
            in = request.getInputStream();
            inputJson = JsonUtil.getInputAsJson(in);
        } catch (Exception ex) {
            logError(ex);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            itemId = processItemStorage(inputJson);
            success=true;
        } catch (Exception ex) {
            errorReason=ex.getMessage();
        }

        JsonObject outputJson;
       
        outputJson=new JsonObject();
        outputJson.addProperty("success", success);

        if(itemId!=null)
            outputJson.addProperty("id", itemId);

        if(errorReason!=null)
            outputJson.addProperty("errorReason", errorReason);
        
        sendJsonBackToClient(outputJson, response);
    }

    private void sendJsonBackToClient(JsonObject outputJson, HttpServletResponse response) throws IOException {
        String jsonStr;

        jsonStr=JsonUtil.getJsonAsString(outputJson);
        response.getWriter().print(jsonStr);
    }

    /**
     * This method is temporary. Should be replaced with better logging policy
     * @param ex
     */
    private void logError(Exception ex) {
        Logger.getLogger(ArchivingServerServlet.class.getName()).log(Level.SEVERE, null, ex);
    }

    private void getItemStatus(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String itemId;
        JsonObject obj=null;
        ItemStatus itemStatus;
        try
        {
            String requestURI;
            requestURI=request.getRequestURI();
            itemId=requestURI.substring(requestURI.lastIndexOf("/")+1);
            if(ItemRefDB.exists(itemId))
            {
                 itemStatus=ItemStatusDB.getStatus(itemId);

                 GsonBuilder gsonBuilder = new GsonBuilder();
                 gsonBuilder.registerTypeAdapter(ItemStatus.class, new ItemStatusSerializer());
                 Gson gson=gsonBuilder.create();
                 obj=(JsonObject) gson.toJsonTree(itemStatus);
            }
            else obj=getItemNotAvailableResponse();

            sendJsonBackToClient(obj, response);
        }
        catch(Exception e)
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    private void deleteItem(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String itemId=null;
        boolean success=false;

        try {
            String requestURI;
            requestURI=request.getRequestURI();
            itemId=requestURI.substring(requestURI.lastIndexOf("/")+1);
            
        } catch (Exception ex) {
            logError(ex);
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try
        {
            if(ItemRefDB.exists(itemId))
            {
                ChainManager cm = new ChainManager();
                ChainContext ct = cm.createContext();

                ct.setAttribute(CommandsConstants.ITEM_ID, itemId);
                ct.setAttribute(CommandsConstants.APP_DIR, new File(getServletContext().getRealPath("/")));
                cm.executeChain("Catalogue/deleteChain", ct);

                success=ct.getResult().getCode()==Result.SUCCESS;

                if(success==false)
                    response.setStatus(500);
            }
            else response.setStatus(404);
        }
        catch(Exception e)
        {
            response.setStatus(500);
        }
        
       
    }

   

    private String processItemStorage(JsonObject inputJson) throws Exception {
        StoreItem item;
        String id;

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(StoreItem.class, new StoreItemDeserializer());

        Gson gson;
        gson=gsonBuilder.create();
        item=gson.fromJson(inputJson, StoreItem.class);

        if(item.downloadUrl==null || item.downloadUrl.equals(""))
            throw new Exception("Download Url not set");
        
        id=DateUtil.getCurrentDateAsUniqueId();

        ChainExecutor exec;
        String rollbackChain=null;

        Properties props = Prefs.load(rootDir);
        if(props.getProperty("fail.dorollback").equals("true"))
            rollbackChain="Catalogue/deleteChain";
        

        exec=new ChainExecutor("Catalogue/storeChain",rollbackChain,item,id,rootDir);
        exec.start();

        return id;
    }

    @Override
    public void destroy() {
        super.destroy();
         Log.log("Shutting down ArchivingServer Servlet");
        try
        {
            InternalDatabase internalDatabase = InternalDatabase.getInstance();
            internalDatabase.close();
        }
        catch(Exception e)
        {

        }

        try
        {
            AutomaticItemDeleteService automaticDeleteService = AutomaticItemDeleteService.getInstance();
            automaticDeleteService.requestShutdown();
        }
        catch(Exception e)
        {

        }

        try
        {
            AutomaticFolderPublishingService folderPublishingService= AutomaticFolderPublishingService.getInstance();
            folderPublishingService.requestShutdown();
        }
        catch(Exception e)
        {

        }

         try
         {
             if(ftpService!=null)
                ftpService.stopServer();
         }
         catch(Exception e)
         {
             Log.log("Cannot shutdown the FTP server");
         }
    }

    @Override
    public void init() throws ServletException {
        super.init();

        Log.log("Initing ArchivingServer Servlet");

        rootDirStr=getServletContext().getRealPath("/");
        rootDir = new File(rootDirStr);
        
        File webinfDir = new File(rootDir, "WEB-INF");
        File dbDir = new File(webinfDir, "db");
        File dblock = new File(dbDir, "database.lck");
            if (dblock.exists()) {
                dblock.delete();
        }

        InternalDatabase internalDatabase;
        internalDatabase = InternalDatabase.getInstance();
        internalDatabase.setDatabasePath("file:" + dbDir.getAbsolutePath() + File.separatorChar + "database");

        AutomaticItemDeleteService automaticDeleteService;

        automaticDeleteService=AutomaticItemDeleteService.getInstance();
        automaticDeleteService.setAppDir(rootDir);
        automaticDeleteService.start();

        AutomaticFolderPublishingService folderPublishingService;
        folderPublishingService=AutomaticFolderPublishingService.getInstance();
        folderPublishingService.setAppDir(rootDir);
        folderPublishingService.start();

        File ftpConfigDir;

        ftpConfigDir=new File(webinfDir, "FTPServer");
        ftpService = FTPService.getInstance(ftpConfigDir.getAbsolutePath());
        if(ftpService==null)
            Log.log("Cannot start FTP server");
    }

    private JsonObject getItemNotAvailableResponse() {
        JsonObject obj;

        obj=new JsonObject();
        obj.addProperty("success", false);
        obj.addProperty("errorReason", "Item not available");
        return obj;
    }

    private void reverseId(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String itemId;
        JsonObject obj=null;
        ItemStatus itemStatus;
        String catUrl;
        String rimId;
        try
        {
            String type = request.getParameter("type");
            if(type.equals("cat"))
            {
                catUrl=URLDecoder.decode(request.getParameter("url"), "UTF-8");
                rimId=URLDecoder.decode(request.getParameter("rimid"), "UTF-8");

                itemId=ReverseCatalogueId.getItem(catUrl, rimId);

                obj=new JsonObject();
                obj.addProperty("success", true);
                obj.addProperty("itemId", itemId);
            }
            else
            {
                obj=new JsonObject();
                obj.addProperty("success", false);
                obj.addProperty("errorReason", "Cat field not supported");
            }

            sendJsonBackToClient(obj, response);
        }
        catch(Exception e)
        {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
