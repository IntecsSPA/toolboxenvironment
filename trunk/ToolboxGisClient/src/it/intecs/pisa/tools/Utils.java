/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.tools;

import http.utils.multipartrequest.MultipartRequest;
import http.utils.multipartrequest.ServletMultipartRequest;
import it.intecs.pisa.gisclient.util.XmlTools;
import java.io.IOException;
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
public class Utils extends HttpServlet {
    protected static final String REQUEST_PARAMETER_COMMAND = "cmd";
    protected static final String REQUEST_PARAMETER_PUT_FILE = "putFile";
    protected static final String REQUEST_PARAMETER_PULL_FILE = "pullFile";
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, Exception {
        String command;

        command = request.getParameter(REQUEST_PARAMETER_COMMAND);
        if (command.equalsIgnoreCase(REQUEST_PARAMETER_PUT_FILE))
            putFile(request, response);
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
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
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
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(Utils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "GisClient Utils Servlet";
    }// </editor-fold>


    private void putFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        //GregorianCalendar gc = new GregorianCalendar();
        System.out.println("Utils Servlet: Put File");
        String type = request.getParameter("type");
        String fileID = request.getParameter("fileId");
        MultipartRequest parser;
        response.setContentType("text/html");
        if(type.equalsIgnoreCase("PROXY")){
            System.out.println("...........Proxy Modality");
           response.setContentType("text/html");
           response.getOutputStream().println("<HTML><HEAD></HEAD><BODY><textarea id=\"textarea\" name=\"textarea\" rows=\""+20+"\" cols=\""+20+"\">");
           parser = new ServletMultipartRequest(request, MultipartRequest.MAX_READ_BYTES, MultipartRequest.IGNORE_FILES_IF_MAX_BYES_EXCEEDED, null);
           XmlTools.copyInputStreamToOutputStream(parser.getFileContents(fileID), response.getOutputStream());
           response.getOutputStream().println("</textarea></BODY></HTML>");
        }else{

        }
              
    }

}
