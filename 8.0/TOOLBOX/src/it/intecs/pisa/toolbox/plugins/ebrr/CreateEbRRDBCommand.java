/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.toolbox.plugins.ebrr;

import be.kzen.ergorr.interfaces.soap.CswBackendClient;
import be.kzen.ergorr.interfaces.soap.csw.CswClient;
import be.kzen.ergorr.model.csw.InsertType;
import be.kzen.ergorr.model.csw.TransactionResponseType;
import be.kzen.ergorr.model.csw.TransactionType;
import be.kzen.ergorr.model.util.JAXBUtil;
import be.kzen.ergorr.persist.service.DbConnectionParams;
import it.intecs.pisa.toolbox.plugins.managerNativePlugins.*;
import it.intecs.pisa.toolbox.plugins.managerCommandPlugins.exceptions.GenericException;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Hashtable;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBElement;
import org.apache.commons.fileupload.FileItem;
import org.apache.log4j.Level;
import javax.xml.bind.Unmarshaller;

/**
 *
 * @author Massimiliano Fanciullli
 */
public class CreateEbRRDBCommand extends NativeCommandsManagerPlugin {

    public void executeCommand(HttpServletRequest req, HttpServletResponse resp) throws Exception {
        String host, port, username, password, schema, template;
        Hashtable<String, FileItem> mimeparts;
        try {
            mimeparts = parseMultiMime(req);

            host = getStringFromMimeParts(mimeparts, "host");
            port = getStringFromMimeParts(mimeparts, "port");
            username = getStringFromMimeParts(mimeparts, "username");
            password = getStringFromMimeParts(mimeparts, "password");
            schema = getStringFromMimeParts(mimeparts, "schema");
            template = getStringFromMimeParts(mimeparts, "template");

            createDB(host, port, username, password, schema, template);
            loadTables(host, port, username, password, schema);
            insertSlots(host, port, username, password, schema);

            resp.sendRedirect("createEbRRDB_showresult.jsp");
        } catch (Exception ex) {
            resp.sendRedirect("createEbRRDB_showresult.jsp?error="+resp.encodeRedirectURL(ex.getMessage()));
        }
    }

    private void createDB(String host, String port, String username, String password, String schema, String template) throws SQLException {
        String jdbcString;
        String sql;
        Connection conn;

        jdbcString = "jdbc:postgresql://" + host + ":" + port + "/postgres";

        sql = "CREATE DATABASE " + schema + " WITH OWNER = " + username + " ENCODING = 'UTF8'  TEMPLATE = " + template + ";";

        conn = DriverManager.getConnection(jdbcString, username, password);
        Statement stmt = conn.createStatement();
        stmt.execute(sql);

        stmt.close();
        conn.close();
    }

    protected void loadTables(String host, String port, String username, String password, String schema) throws Exception {
        File sqlScript = new File(tbxServlet.getRootDir(), "WEB-INF/plugins/ebRRPlugin/resources/database.sql");

        try {
            String sql = IOUtil.loadString(sqlScript);

            executeSql(sql, host, port, username, password, schema);

        } catch (Exception ex) {
            throw new Exception(ex);
        }
    }

    private void executeSql(String sql, String host, String port, String username, String password, String schema) throws Exception {
        try {
            String jdbcString = "jdbc:postgresql://" + host + ":" + port + "/" + schema;
            Connection conn = DriverManager.getConnection(jdbcString, username, password);
            Statement stmt = conn.createStatement();
            stmt.execute(sql);

            stmt.close();
            conn.close();
        } catch (SQLException ex) {
            logger.log(Level.FATAL, "Could not execute script", ex);
            throw new Exception("Could not connect to database", ex);
        }
    }

    private void insertSlots(String host, String port, String username, String password, String schema) throws Exception {
        File slots;
        File fileToTransact;

        DbConnectionParams cp = new DbConnectionParams();
        cp.setDbUrl(host + ":" + port);
        cp.setDbName(schema);
        cp.setDbUser(username);
        cp.setDbPassword(password);

        CswClient client = new CswBackendClient(cp);


        slots = new File(tbxServlet.getRootDir(), "WEB-INF/plugins/ebRRPlugin/resources/data");

        /* RIM */
        fileToTransact = new File(slots, "rim/rim-objecttype-scheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "rim/rim-datatype-scheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "rim/rim-associationtype-scheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "rim/rim-querylanguage-scheme.xml");
        transactFile(client, fileToTransact);
    
        /* ERGO */
        fileToTransact = new File(slots, "ergo/ergo.xml");
        transactFile(client, fileToTransact);
 
        /* OGC */
        fileToTransact = new File(slots, "ogc/OGC-root-package.xml");
        transactFile(client, fileToTransact);
  
        /* EOP */
        fileToTransact = new File(slots, "eo/eo-rim-model.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "eo/eo-slot-init.xml");
        transactFile(client, fileToTransact);
 
        /* BASIC */
        fileToTransact = new File(slots, "basic-package/schemes/ISO19119-Services-Scheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "basic-package/schemes/UNSD-Regions-Scheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "basic-package/Basic-Package.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "basic-package/basic-package-slot-init.xml");
        transactFile(client, fileToTransact);

        /* CIM */
       /* fileToTransact = new File(slots, "cim/schemes/CIM-CharacterSetScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-CitedResponsiblePartyScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-ClassificationCodeScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-CouplingTypeScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-DCPListScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-FormatNameAndVersionScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-KeywordScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-KeywordTypeScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-MetadataStandardNameAndVersionScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-RestrictionCodeScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-RestrictionTypeScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-SpatialRepresentationScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/schemes/CIM-TopicCategoryScheme.xml");
        transactFile(client, fileToTransact);
        fileToTransact = new File(slots, "cim/CIM-Package.xml");
        transactFile(client, fileToTransact);
        System.out.println("CIM");*/
    }

    protected void transactFile(CswClient client, File file) throws Exception {
        Unmarshaller unmarshaller = null;


        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
        unmarshaller = JAXBUtil.getInstance().createUnmarshaller();


        TransactionType request = new TransactionType();
        InsertType insert = new InsertType();


        JAXBElement jaxbEl = (JAXBElement) unmarshaller.unmarshal(file);
        insert.getAny().add(jaxbEl);
        request.getInsertOrUpdateOrDelete().add(insert);

        TransactionResponseType response;

        response = client.transact(request);


       /* String responseStr = JAXBUtil.getInstance().marshallToStr(OFactory.csw.createTransactionResponse(response));
        System.out.println(responseStr);*/

    }
}
