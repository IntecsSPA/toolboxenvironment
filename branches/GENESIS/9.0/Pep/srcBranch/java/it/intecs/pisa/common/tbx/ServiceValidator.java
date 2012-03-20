/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.common.tbx;

import it.intecs.pisa.common.tbx.exceptions.ServiceValidationException;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;

/**
 *
 * @author Massimiliano Fanciulli
 */
public class ServiceValidator {
    /**
     * This method validates the service with the provided name.
     * @param serviceName Service name
     */
    public static void validateService(File serviceFolder,File scriptSchemaFile) throws Exception
    {
        File descriptorFile;
        DOMUtil util;
        Service serviceToValidate;

        try
        {
            util=new DOMUtil();
            descriptorFile = new File(serviceFolder, "serviceDescriptor.xml");

            Document descriptorDocument = util.fileToDocument(descriptorFile);
            serviceToValidate=new Service();
            serviceToValidate.initializeFromXMLDescriptor(descriptorDocument.getDocumentElement());
        }
        catch(Exception e)
        {
            throw new Exception("Cannot load service descriptor");
        }

        try
        {
            Operation[] operations=serviceToValidate.getImplementedInterface().getOperations();
            for(Operation op:operations)
            {
                validateOperation(serviceFolder,op,scriptSchemaFile);
            }
        }
        catch(Exception e)
        {
            throw new ServiceValidationException(e.getMessage());
        }

    }

    public static void validateOperation(File serviceDir,Operation op,File schemaFile) throws Exception {
        Script[] scripts=op.getScripts();
        
        for(Script s:scripts)
        {
            File scriptFile=new File(serviceDir,s.getPath());
            DocumentBuilder parser = DOMUtil.getValidatingParser(schemaFile);
            parser.parse(scriptFile);
        }

    }
}
