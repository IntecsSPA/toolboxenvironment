/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.common.tbx.schemas;

import it.intecs.pisa.toolbox.Toolbox;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Vector;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class retrieves all entities linked in the service schemas.
 *
 * @author massi
 */
public class ToolboxServiceSchemaEntityResolver implements EntityResolver {

    protected File rootDir;
    protected Vector<String> fetchedSchemas;

    public ToolboxServiceSchemaEntityResolver(File schemaRootDir) {
        //System.out.println("schemaeRootDir: " + schemaRootDir.getAbsolutePath());
        rootDir = new File(schemaRootDir, "");
        fetchedSchemas = new Vector<String>();
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        URI uri;
        String scheme;
        String uriAsString;

        try {
            uri = new URI(systemId);

            if (uri.getScheme().equals("http") == true) {
                return getFileWithHttpSchema(uri.toString());
            } else {
                String xercesUri="file://"+System.getProperty("user.dir")+"/";
                uriAsString=systemId.substring(xercesUri.length());

                System.out.println("Resolved uri to relative path "+uriAsString);
                if (fetchedSchemas.indexOf(uriAsString) != -1) {
                    return new InputSource();
                }

                return getFileWithNoSchema(uriAsString);
            }
            //I'm assuming that xerces will always prepend user.dir to the include path

        } catch (Exception ex) {
            log("An error occurred while trying to resolve entity " + systemId);
            return null;
        }
    }

    private InputSource getFileWithHttpSchema(String uriAsString) throws Exception {
        URL url;
        InputSource inputSource;
        HttpURLConnection conn;

        try {
            if (fetchedSchemas.indexOf(uriAsString) != -1) {
                    return new InputSource();
                }

            url = new URL(uriAsString);
            fetchedSchemas.add(uriAsString);
            inputSource = new InputSource(url.openStream());
        } catch (Exception e) {
            log("An error occurred while trying to load schema " + uriAsString);
            return null;
        }
        return inputSource;
    }

    protected InputSource getFileWithNoSchema(String uriAsString) throws Exception {
        File schemaFile;
        InputSource inputSource;

        schemaFile = new File(rootDir, uriAsString);

        fetchedSchemas.add(uriAsString);
        inputSource = new InputSource(new FileInputStream(schemaFile));

        return inputSource;
    }

    public void resetFetchedSchemas() {
        fetchedSchemas = new Vector<String>();
    }

    protected void log(String text) {
        Toolbox tbxInstance = Toolbox.getInstance();
        Logger logger = tbxInstance.getLogger();

        logger.error(text);
    }
}
