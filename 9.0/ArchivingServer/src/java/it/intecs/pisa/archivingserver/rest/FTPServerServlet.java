/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.archivingserver.rest;

import com.google.gson.JsonObject;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.services.FTPService;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author massi
 */
public class FTPServerServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        boolean success=true;
        try
        {
            System.out.println(this.getServletContext().getContextPath());
            String requestURI=request.getRequestURI();
            System.out.println(requestURI);
            if(requestURI.endsWith("start"))
            {
                startFTP();
            }
            else if(requestURI.endsWith("stop"))
            {
                stopFTP();
            }
        }
        catch(Exception e)
        {
            success=false;
        }

        JsonObject obj;

        obj=new JsonObject();
        obj.addProperty("success", success);
        sendJsonBackToClient(obj,response);
    }

    @Override
    public void destroy() {
        super.destroy();
        Log.log("Shutting down FTP Server Servlet");
    }

    @Override
    public void init() throws ServletException {
        super.init();
        Log.log("Initing FTP Server Servlet");
    }

    private void sendJsonBackToClient(JsonObject outputJson, HttpServletResponse response) throws IOException {
        String jsonStr;

        jsonStr=JsonUtil.getJsonAsString(outputJson);
        response.setContentType("text/json;charset=UTF-8");
        response.getWriter().print(jsonStr);
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
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

    private void startFTP() {
        File rootDir = new File(getServletContext().getRealPath("/"));
        File webinfDir = new File(rootDir, "WEB-INF");
        File ftpConfigDir;

        ftpConfigDir=new File(webinfDir, "FTPServer");
        FTPService ftpService = FTPService.getInstance(ftpConfigDir.getAbsolutePath());

        if(ftpService==null)
            Log.log("Cannot start FTP server");
        else Log.log("FTP server started");
    }

    private void stopFTP() {
        FTPService ftpService = FTPService.getInstance();
        ftpService.stopServer();
    }

}
