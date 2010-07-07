/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package it.intecs.pisa.common.tbx.schemas;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;

/**
 * This class retrieves all entities linked in the service schemas.
 *
 * @author massi
 */
public class ScriptSchemaEntityResolver implements EntityResolver {

    protected File rootDir;

    public ScriptSchemaEntityResolver(File schemaRootDir) {
        rootDir = new File(schemaRootDir,"");
    }

    public InputSource resolveEntity(String publicId, String systemId) {
        try {
            String schemaFileName;
            schemaFileName = systemId.substring(systemId.lastIndexOf("/") + 1);
            return new InputSource(new FileInputStream(new File(rootDir, schemaFileName)));
        } catch (FileNotFoundException ex) {
            return new InputSource();
        }

    }

   
}
