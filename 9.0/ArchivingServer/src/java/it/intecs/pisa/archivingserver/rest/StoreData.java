
package it.intecs.pisa.archivingserver.rest;

import com.google.gson.JsonObject;
import http.utils.multipartrequest.MultipartRequest;
import http.utils.multipartrequest.ServletMultipartRequest;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.util.DateUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
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
public class StoreData extends HttpServlet {
   
    protected static final int MAX_READ_BYTES = 10000000;
    protected static final String STORE_DATA_FOLDER = "storeData";
    protected static final String REST_RESOURCE_PRE_PROCESSING = "rest/storeddata";
    
    private File  storeDataDir = null;
    
    @Override
    public void init() throws ServletException {
        super.init();
        File rootDir = new File(getServletContext().getRealPath("/"));
        try {
            storeDataDir = new File(Prefs.getWorkspaceFolder(rootDir), STORE_DATA_FOLDER);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(StoreData.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(StoreData.class.getName()).log(Level.SEVERE, null, ex);
        }
        if(!storeDataDir.exists())
           storeDataDir.mkdirs(); 
    }
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
  
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
        MultipartRequest parser=null;
        JsonObject outputJson = new JsonObject();
        String storeFileParameter="";
        String storeFileSuffix="";
        String storeFilePrefix="";
        String storeFileName="";
        File outputFile;
        String id = null;
        String [] uriSplit;
        String fileInput = null;
        String uri = request.getRequestURI();
        int dotIndex;
        try {
            parser = new ServletMultipartRequest(request, MAX_READ_BYTES, MultipartRequest.ABORT_IF_MAX_BYES_EXCEEDED, null);
        } catch (IllegalArgumentException ex) {
           outputJson.addProperty("success", Boolean.FALSE);
           response.setContentType("text/html");
           IOUtil.copy(JsonUtil.getJsonAsStream(outputJson),response.getOutputStream());
        } catch (IOException ex) {
           outputJson.addProperty("success", Boolean.FALSE);
           response.setContentType("text/html");
           IOUtil.copy(JsonUtil.getJsonAsStream(outputJson),response.getOutputStream());
        }finally{
            
            
            Enumeration fileParms=parser.getFileParameterNames();
            if(!uri.endsWith(REST_RESOURCE_PRE_PROCESSING)){
               uriSplit= uri.split("/");
               fileInput=uriSplit[uriSplit.length-1];
            }
            while(fileParms.hasMoreElements()){
                storeFileParameter=(String) fileParms.nextElement();
                storeFileName= parser.getBaseFilename(storeFileParameter);
                if(storeFileName!=null)
                if(storeFileParameter.equals(fileInput)){
                   if(storeFileName.contains(".")){
                      dotIndex=storeFileName.lastIndexOf(".");
                      storeFileSuffix="."+storeFileName.substring(dotIndex+1);
                      storeFilePrefix=storeFileName.substring(0,dotIndex);
                   }
                id=DateUtil.getCurrentDateAsUniqueId();   
                outputFile = new File(storeDataDir, storeFilePrefix+"_"+id+storeFileSuffix);  
                IOUtil.copy(parser.getFileContents(storeFileParameter), new FileOutputStream(outputFile)); 
               } 
            }
            outputJson.addProperty("success", Boolean.TRUE);
            outputJson.addProperty("id", storeFilePrefix+"_"+id+storeFileSuffix);
            response.setContentType("text/html");
            IOUtil.copy(JsonUtil.getJsonAsStream(outputJson),response.getOutputStream());
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
        File storedFile;
        String uri,storedId;
        JsonObject outputJson = new JsonObject();
        String [] uriSplit;
        uri = req.getRequestURI();
        
        
        uriSplit=uri.split("/");
        
        if(!uriSplit[uriSplit.length-1].equals("storeddata")){
            storedId=uriSplit[uriSplit.length-1];
            storedFile = new File(storeDataDir, storedId); 
            storedFile.delete();
            outputJson.addProperty("success", Boolean.TRUE); 
        }else{
            outputJson.addProperty("success", Boolean.FALSE); 
            outputJson.addProperty("reason", "File not stored"); 
        }

        resp.setContentType("application/json");
        IOUtil.copy(JsonUtil.getJsonAsStream(outputJson),resp.getOutputStream()); 
    }
    
    

    /**
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */

    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
}
