/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.schemas.ToolboxServiceSchemaEntityResolver;
import it.intecs.pisa.toolbox.configuration.ToolboxNetwork;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import it.intecs.pisa.util.XMLSerializer2;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Vector;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class TBXSOAPInterface extends Interface{
    
    public TBXSOAPInterface()
    {
        super();
    }

    @Override
    protected Operation[] getOperationsDefinition(Element parentEl) {
        LinkedList operationList;
        Element operationEl;
        int count = 0;
        Operation[] operationsDef;
        String opType;

        operationList = DOMUtil.getChildrenByTagName(parentEl, TAG_OPERATION);
        count = operationList.size();

        operationsDef = new TBXOperation[count];
        for (int j = 0; j < count; j++) {
            operationEl = (Element) operationList.get(j);

            opType=operationEl.getAttribute(ATTRIBUTE_TYPE);

            if(opType.equals(Operation.OPERATION_TYPE_SYNCHRONOUS))
                operationsDef[j] = new TBXSynchronousOperation();
            else
                operationsDef[j] = new TBXAsynchronousOperation();

            operationsDef[j].initFromXMLDocument(operationEl);
        }

        return operationsDef;
    }

   public Document validateDocument(Document document) throws Exception {
        File schemaFile;
        File serviceRoot;
        //Added to solve the problem of the disable the valida
        document.getDocumentElement().removeAttribute("schema-location");

        if(schemaRoot!=null && schemaRoot.equals("")==false && isValidationActive())
        {
            serviceRoot=((TBXService)this.parentService).getServiceRoot();
            schemaFile=new File(serviceRoot,schemaDir);
            
            String serviceName;
            String schemaURL;
            
            serviceName=this.getParent().getServiceName();
            schemaURL=ToolboxNetwork.getEndpointURL()+"/WSDL/"+serviceName+"/"+this.getSchemaRoot();
            DocumentBuilder validatingParser = DOMUtil.getValidatingParser(schemaURL);

            validatingParser.setEntityResolver(new ToolboxServiceSchemaEntityResolver(schemaFile));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            new XMLSerializer2(out).serialize(document);
            out.close();

                //entityResolver.resetFetchedSchemas();
             document = validatingParser.parse(new ByteArrayInputStream(out.toByteArray()));
        }

        return document;
    }

    protected DocumentBuilder getValidatingParser() throws FileNotFoundException {
        final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema";
        final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
        final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setValidating(true);
        documentBuilderFactory.setExpandEntityReferences(false);
        documentBuilderFactory.setAttribute(JAXP_SCHEMA_LANGUAGE,
                W3C_XML_SCHEMA);
        documentBuilderFactory.setAttribute(JAXP_SCHEMA_SOURCE, getSchemaArray());
        DocumentBuilder documentBuilder = null;
        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            documentBuilder.setErrorHandler(DOMUtil.getThrowerErrorHandler());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return documentBuilder;
    }

    protected InputStream[] getSchemaArray() throws FileNotFoundException
    {
        String[] schemaDirListing;
        File serviceRoot = ((TBXService) this.parentService).getServiceRoot();
        File schemaFile = new File(serviceRoot, schemaDir);

        schemaDirListing=IOUtil.listDir(schemaFile);

        Vector<String> filteredListing=new Vector<String>();

        for(String schema:schemaDirListing)
        {
            if(schema.endsWith(".xsd"))
                filteredListing.add(schema);
        }

        InputStream[] streams;
        String[] filteredArray=filteredListing.toArray(new String[0]);

        streams=new InputStream[filteredListing.size()];
        for(int i=0;i<filteredArray.length;i++)
        {
            System.out.println("Adding schema stream for file "+filteredArray[i]);
            streams[i]=new FileInputStream(new File(filteredArray[i]));
        }

        return streams;
    }

    public boolean hasWSDL()
    {
        return hasSchema();
    }

    public boolean hasSchema()
    {
        if(schemaRoot==null || schemaRoot.equals(""))
            return false;
        else return true;
    }

   
}
