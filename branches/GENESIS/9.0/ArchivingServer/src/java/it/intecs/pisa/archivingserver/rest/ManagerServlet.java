package it.intecs.pisa.archivingserver.rest;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import it.intecs.pisa.archivingserver.log.Log;
import it.intecs.pisa.archivingserver.prefs.ChainTypesPrefs;
import it.intecs.pisa.archivingserver.prefs.MetadataPrefs;
import it.intecs.pisa.archivingserver.prefs.PreProcessingPrefs;
import it.intecs.pisa.archivingserver.prefs.Prefs;
import it.intecs.pisa.archivingserver.prefs.WatchPrefs;
import it.intecs.pisa.archivingserver.services.FTPService;
import it.intecs.pisa.util.json.JsonUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Andrea Marongiu
 */
public class ManagerServlet extends ArchivingServerServlet {

    protected static final String REST_RESOURCE_CONFIG = "config";
    protected static final String REST_RESOURCE_METADATA_PROCESSING = "config/metadata/processing";
    protected static final String REST_LOGIN = "config/userprofiles";
    protected static final String REST_RESOURCE_CHAIN_TYPES = "config/chaintypes";
    protected static final String REST_RESOURCE_PRE_PROCESSING = "config/preprocessing";
    protected static final String REST_RESOURCE_WATCH_LIST = "config/watchlist";
    protected static final String REST_RESOURCE_LOG_LEVEL = "config/log/level";
    protected static final String REST_RESOURCE_LOG = "config/log";
    protected static final String REST_RESOURCE_AUTHENTICATE = "authenticate";
    protected static final String LOG_PROPERTY_FILE_RELATIVE_PATH = "WEB-INF/classes/logging.properties";
    protected static final int LOG_ROWS = 500;
    protected static final String ARMS_LOG_SUFIX = ".log";
    private String appDirStr = null;
    private File appDir = null;
    private String armsLogPrefix = null;
    private String armsLogFolder = null;
    private File logFileDir;

    @Override
    public void init() throws ServletException {
        super.init();
        appDirStr = getServletContext().getRealPath("/");
        appDir = new File(appDirStr);
        Properties logProp = new Properties();
        try {
            logProp.load(new FileInputStream(new File(appDir, LOG_PROPERTY_FILE_RELATIVE_PATH)));
        } catch (IOException ex) {
            Log.logException(ex);
        }
        armsLogPrefix = logProp.getProperty("org.apache.juli.FileHandler.prefix");
        armsLogFolder = logProp.getProperty("org.apache.juli.FileHandler.directory");
        armsLogFolder = armsLogFolder.split("catalina.base}/")[1];
        logFileDir = new File(System.getProperty("catalina.base"), armsLogFolder);
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
        AuthenticationManager am=null;
        try {
            String uri;
            String responseStr = null;
            String authHeader = request.getHeader(AuthenticationManager.AUTHORIZATION_HEADER);
      
            am = new AuthenticationManager();

            if (am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE)) {

                uri = request.getRequestURI();
                if (uri.endsWith(REST_RESOURCE_CONFIG)) {
                    response.setHeader("Content-Type", "application/json");
                    responseStr = Prefs.getJSONStringConfiguration(appDir);
                } else if (uri.contains(REST_LOGIN)) {
                    response.setHeader("Content-Type", "application/json");
                    RestResponse jsonResponse = new RestResponse("getUserProfile");
                    jsonResponse.setSuccess(true);
                    responseStr = jsonResponse.getStringRestResponse();
                    //TODO Load Profile not yet implemented
                } else if (uri.endsWith(REST_RESOURCE_METADATA_PROCESSING)) {
                    try {
                        response.setHeader("Content-Type", "application/json");
                        responseStr = MetadataPrefs.load(appDir);
                    } catch (Exception ex) {
                        Logger.getLogger(ManagerServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (uri.endsWith(REST_RESOURCE_PRE_PROCESSING)) {
                    try {
                        response.setHeader("Content-Type", "application/json");
                        responseStr = PreProcessingPrefs.load(appDir);
                    } catch (Exception ex) {
                        Logger.getLogger(ManagerServlet.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else if (uri.endsWith(REST_RESOURCE_WATCH_LIST)) {
                    try {
                        response.setHeader("Content-Type", "application/json");
                        responseStr = WatchPrefs.load(appDir);

                    } catch (Exception ex) {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return;
                    }
                } else if (uri.endsWith(REST_RESOURCE_CHAIN_TYPES)) {
                    try {
                        response.setHeader("Content-Type", "application/json");
                        responseStr = ChainTypesPrefs.load(appDir);

                    } catch (Exception ex) {
                        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        return;
                    }
                } else if (uri.contains(REST_RESOURCE_LOG)) {
                    String[] splitUri = uri.split("/");
                    int logNumberRows = LOG_ROWS;
                    int logRows = 0;

                    if (uri.endsWith(REST_RESOURCE_LOG)) {
                        response.setHeader("Content-Type", "text/html");
                        logRows = this.getRowsNumber(new FileInputStream(new File(this.getLogFilePath())));
                        this.copyLastRows(new FileInputStream(new File(this.getLogFilePath())),
                                response.getOutputStream(), logRows, logNumberRows);
                    } else if (uri.contains(REST_RESOURCE_LOG_LEVEL)) {
                        if (uri.endsWith(REST_RESOURCE_LOG_LEVEL)) {
                            try {
                                response.setHeader("Content-Type", "application/json");
                                responseStr = getLogLevel();

                            } catch (Exception ex) {
                                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                                return;
                            }
                        } else {
                            response.setHeader("Content-Type", "application/json");
                            String[] uriSplit = uri.split("/");
                            responseStr = setLogLevel(Level.parse(uriSplit[(uriSplit.length - 1)]));

                        }
                    } else {
                        response.setHeader("Content-Type", "text/html");
                        logNumberRows = new Integer(splitUri[splitUri.length - 1]).intValue();
                        logRows = this.getRowsNumber(new FileInputStream(new File(this.getLogFilePath())));
                        this.copyLastRows(new FileInputStream(new File(this.getLogFilePath())),
                                response.getOutputStream(), logRows, logNumberRows);
                    }

                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                if (responseStr != null) {
                    response.getOutputStream().print(responseStr);
                }
            } else {
                response.setHeader("Content-Type", "application/json");
                response.getOutputStream().print(JsonUtil.getJsonAsString(am.notAuthorizatedResponse()));
            }
        } catch (Exception ex) {
            Logger.getLogger(ManagerServlet.class.getName()).log(Level.SEVERE, null, ex);
            ex.printStackTrace();
            response.setHeader("Content-Type", "application/json");
            response.getOutputStream().print(JsonUtil.getJsonAsString(am.notAuthorizatedResponse()));
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

            } else if (uri.endsWith(REST_RESOURCE_METADATA_PROCESSING)) {
                MetadataPrefs.save(appDir, obj);
                response.setHeader("Content-Type", "application/json");
                JsonObject outputJson = new JsonObject();
                outputJson.addProperty("success", true);
                Gson gson = new Gson();
                response.getOutputStream().print(gson.toJson(outputJson));
                return;
            } else if (uri.endsWith(REST_RESOURCE_PRE_PROCESSING)) {
                PreProcessingPrefs.save(appDir, obj);
                response.setHeader("Content-Type", "application/json");
                JsonObject outputJson = new JsonObject();
                outputJson.addProperty("success", true);
                Gson gson = new Gson();
                response.getOutputStream().print(gson.toJson(outputJson));
                return;
            } else if (uri.endsWith(REST_RESOURCE_WATCH_LIST)) {
                WatchPrefs.save(appDir, obj);
                response.setHeader("Content-Type", "application/json");
                JsonObject outputJson = new JsonObject();
                outputJson.addProperty("success", true);
                Gson gson = new Gson();
                response.getOutputStream().print(gson.toJson(outputJson));
                return;

            } else if (uri.endsWith(REST_RESOURCE_CHAIN_TYPES)) {
                ChainTypesPrefs.save(appDir, obj);
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

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String uri;
        Gson gson = null;
        JsonObject jObj = null;
        OutputStream out = resp.getOutputStream();
        uri = req.getRequestURI();
        try {
            if (uri.endsWith(REST_RESOURCE_LOG)) {
                if (authenticateUser(req)) {
                    this.cleanLog();
                    jObj = new JsonObject();
                    jObj.addProperty("success", Boolean.TRUE);
                    gson = new Gson();
                    resp.setHeader("Content-Type", "application/json");
                    out.write(gson.toJson(jObj).getBytes());
                }
            }
        } catch (Exception ex) {
            req.getSession().invalidate();
            resp.sendError(401);
        } finally {

            out.close();
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
        try {
            AuthenticationManager am = new AuthenticationManager();
            String authHeader = request.getHeader(AuthenticationManager.AUTHORIZATION_HEADER);
            return am.authenticate(authHeader, AuthenticationManager.ADMIN_RULE);
        } catch (Exception ex) {
            ex.printStackTrace();
            Logger.getLogger(ManagerServlet.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

    /**
     *
     * @param request
     * @return
     */
    private void saveConfiguration(JsonObject jsonObj) throws FileNotFoundException, IOException {
        Properties oldprops = Prefs.load(appDir);
        String deleteAfter = jsonObj.get("delete.after").getAsString()
                + jsonObj.get("delete.after.uom").getAsString();

        jsonObj.remove("delete.after");
        jsonObj.remove("delete.after.uom");

        jsonObj.addProperty("delete.after", deleteAfter);

        Prefs.save(appDir, jsonObj);


        String oldFtpIP = jsonObj.get("publish.local.ftp.ip").getAsString();
        String oldFtpPort = jsonObj.get("publish.local.ftp.port").getAsString();
        String oldFtpRootDir = jsonObj.get("publish.local.ftp.rootdir").getAsString();

        String newFtpIP = oldprops.getProperty("publish.local.ftp.ip");
        String newFtpPort = oldprops.getProperty("publish.local.ftp.port");
        String newFtpRootDir = oldprops.getProperty("publish.local.ftp.rootdir");

        if (!oldFtpIP.equals(newFtpIP) || !oldFtpPort.equals(newFtpPort)
                || !oldFtpRootDir.equals(newFtpRootDir)) {
            File ftpConfigDir;
            File webinfDir = new File(appDir, "WEB-INF");
            ftpConfigDir = new File(webinfDir, "FTPServer");
            FTPService ftpService = this.getFtpService();
            if (ftpService != null) {
                ftpService.deleteServer();
            }
            ftpService = FTPService.getInstance(ftpConfigDir.getAbsolutePath());
            this.setFtpService(ftpService);
            if (ftpService == null) {
                Log.log("Cannot start FTP server");
            } else {
                Log.log("FTP server started");
            }
        }

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

    private String getLogLevel() {
        JsonObject obj = new JsonObject();

        obj.addProperty("logLevel", Log.getLogLevel().toString());
        Gson gson = new Gson();
        String jsonStr = gson.toJson(obj);
        return jsonStr;
    }

    private String setLogLevel(Level newLevel) {
        Log.setLevel(newLevel);
        JsonObject obj = new JsonObject();
        obj.addProperty("success", Boolean.valueOf(true));
        Gson gson = new Gson();
        String jsonStr = gson.toJson(obj);
        return jsonStr;
    }

    private void cleanLog() throws Exception {

        FileOutputStream erasor = new FileOutputStream(this.getLogFilePath());
        erasor.write((new String()).getBytes());
        erasor.close();

    }

    private String getLogFilePath() throws IOException {

        String logFileName = armsLogPrefix;

        Calendar cal = Calendar.getInstance();//ergorr.2011-03-15.log
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        logFileName += sdf.format(cal.getTime()) + ARMS_LOG_SUFIX;


        return new File(this.logFileDir, logFileName).getCanonicalPath();
    }

    private void copyLastRows(InputStream is, OutputStream out, int rowsNumber, int lastRowsNumber) throws IOException {
        int firstRow = rowsNumber - lastRowsNumber;
        if (firstRow < 0) {
            firstRow = 0;
        }
        try {
            byte[] c = new byte[1024];
            int count = 0, i;
            int readChars = 0;
            out.write("<pre>".getBytes());
            while ((readChars = is.read(c)) != -1) {
                if (count >= firstRow) {
                    out.write(c);
                } else {
                    for (i = 0; i < readChars; ++i) {
                        if (c[i] == '\n') {
                            ++count;
                            if (count >= firstRow) {
                                break;
                            }
                        }
                    }
                    if (count >= firstRow) {
                        out.write(c, i + 1, readChars - (i + 1));
                    }

                }
            }

        } finally {
            out.write("</pre>".getBytes());
            is.close();
        }
    }

    private int getRowsNumber(InputStream is) throws IOException {

        try {
            byte[] c = new byte[1024];
            int count = 0;
            int readChars = 0;
            while ((readChars = is.read(c)) != -1) {
                for (int i = 0; i < readChars; ++i) {
                    if (c[i] == '\n') {
                        ++count;
                    }
                }
            }
            return count;
        } finally {
            is.close();
        }
    }
}
