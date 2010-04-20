/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.common.tbx.schemas;

import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
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
        rootDir = new File(schemaRootDir,"");
        fetchedSchemas = new Vector<String>();
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        URI uri;
        String scheme;
        String uriAsString;

        //System.out.println("PublicID: " + publicId);
        //System.out.println("SystemID: " + systemId);

        try {
            uri = new URI(systemId);
            //System.out.println("Scheme: "+uri.getScheme());
            //checking if schema has beel already loaded
            uriAsString = uri.toString();
            if (fetchedSchemas.indexOf(uriAsString) != -1) {
                return new InputSource();
            }

            scheme = uri.getScheme();
            if (scheme == null) {
                return getFileWithNoSchema(uriAsString);
            } else if (scheme.equals("service") == true) {
                return getFileWithServiceSchema(uriAsString);
            } else if (scheme.equals("http") == true) {
                return getFileWithHttpSchema(uriAsString);
            } else if (scheme.equals("file") == true) {
                return getFileWithFileSchema(uriAsString);
            }
            else return null;

        } catch (Exception ex) {
            Logger.getLogger(ToolboxServiceSchemaEntityResolver.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }
    }

    private InputSource getFileWithHttpSchema(String uriAsString) throws Exception {
        URL url;
        InputSource inputSource;
        HttpURLConnection conn;

        //System.out.println("Downloading " + uriAsString + " from network");

        url = new URL(uriAsString);
        fetchedSchemas.add(uriAsString);
        inputSource = new InputSource(url.openStream());
              
        return inputSource;
    }

    protected InputSource getFileWithServiceSchema(String uriAsString) throws Exception {
        File schemaFile;
        InputSource inputSource;

        schemaFile = new File(rootDir, uriAsString.substring(8));

        fetchedSchemas.add(uriAsString);
        //System.out.println("Resolved " + uriAsString + " schema reference to " + schemaFile.getAbsolutePath());
        inputSource = new InputSource(new FileInputStream(schemaFile));
               
        return inputSource;
    }

    protected InputSource getFileWithFileSchema(String uriAsString) throws Exception {
        File schemaFile;
        InputSource inputSource;
        String workingDir;
        File workingDirFile;
        URI uri;
      
        workingDir=System.getProperty("user.dir");
        workingDirFile=new File(workingDir);

        uri=new URI(uriAsString);
       /* if(IOUtil.isParentDirectory(workingDirFile.toURI(), uri))
        {
            schemaFile = new File(rootDir,uriAsString.substring(workingDir.length()+uriAsString.indexOf(workingDir)));
        }
        else
        {
             schemaFile = new File(uriAsString);
             if(schemaFile.isAbsolute()==false)
                 schemaFile = new File(rootDir,schemaFile.getPath());
        }*/

        schemaFile=new File(uri);

        fetchedSchemas.add(uriAsString);
        //System.out.println("Resolved " + uriAsString + " schema reference to " + schemaFile.getAbsolutePath());


        inputSource = new InputSource(new FileInputStream(schemaFile));
       
        return inputSource;
    }

    protected InputSource getFileWithNoSchema(String uriAsString) throws Exception {
        File schemaFile;
        InputSource inputSource;
             
        schemaFile = new File(uriAsString);
        
        fetchedSchemas.add(uriAsString);
        //System.out.println("Resolved " + uriAsString + " schema reference to " + schemaFile.getAbsolutePath());
        inputSource = new InputSource(new FileInputStream(schemaFile));

        return inputSource;
    }

    public void resetFetchedSchemas()
    {
        fetchedSchemas=new Vector<String>();
    }
}
