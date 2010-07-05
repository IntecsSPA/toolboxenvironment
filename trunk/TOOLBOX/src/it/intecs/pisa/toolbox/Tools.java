/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox;

import http.utils.multipartrequest.*;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
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
public class Tools extends HttpServlet {
   
    protected static final String LOG_TEMP_PATH = "log/tmp/";
    
    protected static final String REQUEST_PARAMETER_COMMAND = "cmd";
    protected static final String REQUEST_PARAMETER_PUT_FILE = "putFile";
    protected static final String REQUEST_PARAMETER_PULL_FILE = "pullFile";
    
    /** 
    * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
    * @param request servlet request
    * @param response servlet response
    */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException, Exception {
        String command;

        command = request.getParameter(REQUEST_PARAMETER_COMMAND);
        if (command.equalsIgnoreCase(REQUEST_PARAMETER_PUT_FILE)) 
            putFile(request, response);
        else
          if (command.equalsIgnoreCase(REQUEST_PARAMETER_PULL_FILE)) 
            pullFile(request, response);  
    } 
        

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
    * Handles the HTTP <code>GET</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    } 

    /** 
    * Handles the HTTP <code>POST</code> method.
    * @param request servlet request
    * @param response servlet response
    */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(Tools.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /** 
    * Returns a short description of the servlet.
    */
    @Override
    public String getServletInfo() {
        return "Gis Client Tools Servlet";
    }// </editor-fold>

 /**
     * This method put a local File in the server side for the visualization or for save it.
     * @param req Servlet req class
     * @param resp Servlet response class
     */
    private void putFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        GregorianCalendar gc = new GregorianCalendar();
        String type = request.getParameter("type");
        String modality = request.getParameter("modality");
        String editAreaPath = request.getParameter("editAreaPath");
            String editAreaSection="";
            if(editAreaPath!=null){
                    editAreaSection="<script language=\"Javascript\" type=\"text/javascript\" src=\""+editAreaPath+"\"></script>"
                                           +"<script language=\"Javascript\" type=\"text/javascript\">"
                                           +"editAreaLoader.init({"
                                           +     "id: \"textarea\","
                                           +     "start_highlight: true,"
                                           +     "allow_resize: \"both\","
                                           +     "allow_toggle: false,"
                                           +     "language: \"en\","
                                           +     "syntax: \"xml\","
                                           +     "toolbar: \"new_document, |, search, go_to_line, |, undo, redo, |, select_font, |, syntax_selection, |, change_smooth_selection, highlight, reset_highlight, |, help\","
                                           +     "syntax_selection_allow: \"java,html,js,php,python,xml,c,cpp,sql\","
                                           +     "show_line_colors: true"
                                           +     "});"
                                           +"function getEditAreaValue(){"
                                           +"   return editAreaLoader.getValue(\"textarea\");"
                                           +"}"
                                           +"function setEditAreaValue(newValue){"
                                           +"   editAreaLoader.setValue(\"textarea\",newValue);"
                                           +"}"
                                           +"</script>";
            } 
        if(type.equalsIgnoreCase("MULTIPART")){
            MultipartRequest parser;
            response.setContentType("text/html");
            if(modality.equalsIgnoreCase("VIEW")){
               response.setContentType("text");
               parser = new ServletMultipartRequest(request, MultipartRequest.MAX_READ_BYTES, MultipartRequest.IGNORE_FILES_IF_MAX_BYES_EXCEEDED, null);
               IOUtil.copy(parser.getFileContents("FILE"), response.getOutputStream());
            }else  
                if(modality.equalsIgnoreCase("EDIT")){
                    String rows = request.getParameter("rows");
                    String cols = request.getParameter("cols");
                    response.getOutputStream().println("<HTML><HEAD>"+editAreaSection+"</HEAD><BODY><textarea id=\"textarea\" name=\"textarea\" rows=\""+rows+"\" cols=\""+cols+"\">");
                    parser = new ServletMultipartRequest(request, MultipartRequest.MAX_READ_BYTES, MultipartRequest.IGNORE_FILES_IF_MAX_BYES_EXCEEDED, null);
                    IOUtil.copy(parser.getFileContents("FILE"), response.getOutputStream());
                    response.getOutputStream().println("</textarea></BODY></HTML>");
            }
        }else{
            if(modality.equalsIgnoreCase("EDIT")){
               String rows = request.getParameter("rows");
               String cols = request.getParameter("cols");
               String inLine = request.getParameter("inLine");
               response.setContentType("text/html");
               response.getOutputStream().println("<HTML><HEAD>"+editAreaSection+"</HEAD><BODY><textarea id=\"textarea\" name=\"textarea\" rows=\""+rows+"\" cols=\""+cols+"\">");
               if(inLine!=null){
                   response.getOutputStream().println(inLine);
               }else
                    IOUtil.copy(request.getInputStream(),response.getOutputStream());
               //XmlTools.copyInputStreamToOutputStream(parser.getFileContents("FILE"), response.getOutputStream()); 
               response.getOutputStream().println("</textarea></BODY></HTML>");  
            }else{
                  String outputFormat = request.getParameter("outputFormat");
                  String tempFileName="temp"+gc.getTime().toString()+".tmp";
                  String pathTempFile=getServletContext().getRealPath(Tools.LOG_TEMP_PATH+tempFileName);
                  FileOutputStream fo= new FileOutputStream(pathTempFile);
                  IOUtil.copy(request.getInputStream(),fo);
                  response.getOutputStream().print("Tools?cmd=PULLFILE&outputFormat="+outputFormat+"&FILE="+Tools.LOG_TEMP_PATH+tempFileName);
            }
        }
        
    }  
    
     /**
     * This method return and remove a File in the server side.
     * @param req Servlet req class
     * @param resp Servlet response class
     */
    private void pullFile(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String file = request.getParameter("FILE");
        String outputFormat = request.getParameter("outputFormat");
        response.setContentType(outputFormat);
        File tmp=new File (getServletContext().getRealPath(file));
        IOUtil.copy(new FileInputStream(tmp), response.getOutputStream());
        tmp.delete();
    }  
}
