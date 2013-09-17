package it.intecs.pisa.util;

import it.intecs.pisa.util.wsdl.WSDL;
import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 *
 * @author Andrea Marongiu
 */
public class XMLSchemaUtil {

    private static final String SCHEMA_NAMESPACE = "http://www.w3.org/2001/XMLSchema";
    private static final String TAG_IMPORT = "import";
    private static final String TAG_INCLUDE = "include";
    private static final String ATTRIBUTE_SCHEMA_LOCATION = "schemaLocation";
    private static final String MAIN_SCHEMA_FILE_NAME = "main.xsd";
    private static final int CONNECTION_TIME_OUT = 100000;
    
    public void updateWSDLSchemas(String wsdlURL, File schemaDirecorty) throws Exception {
        int i;
        DOMUtil domUtil = new DOMUtil();
        Element elementImport;
        File mainSchemaFile = new File(schemaDirecorty, MAIN_SCHEMA_FILE_NAME);
        WSDL wsdl = new WSDL(new URL(wsdlURL));
        Document schemaDoc = domUtil.inputStreamToDocument(new FileInputStream(mainSchemaFile));
        String[] schemaImportLoc = wsdl.getImportLocations();
        String[] namespaceImport = wsdl.getImportNamespaces();

        if (schemaImportLoc != null) {
            for (i = 0; i < schemaImportLoc.length; i++) {
                if (schemaImportLoc[i].isEmpty()) {
                    continue;
                }
                if (!namespaceImport[i].equals("http://schemas.xmlsoap.org/ws/2003/03/addressing")) {
                    elementImport = schemaDoc.createElementNS(SCHEMA_NAMESPACE, "import");
                    elementImport.setAttribute("schemaLocation", schemaImportLoc[i]);
                    elementImport.setAttribute("namespace", namespaceImport[i]);
                    schemaDoc.getDocumentElement().appendChild(elementImport);
                }
            }
        }

        DOMUtil.dumpXML(schemaDoc, mainSchemaFile);
    }

    public String saveWSDLSchemas(String wsdlURL, File schemaDirecorty) throws Exception {
        String relativePath;
        int i = 0, importedSchemas = 0;
        DOMUtil domUtil = new DOMUtil();
        Element elementImport;
        File currentSchemaLocationDirecotry;
        String schemaFileName;
        File mainSchemaFile = new File(schemaDirecorty, MAIN_SCHEMA_FILE_NAME);
        WSDL wsdl = new WSDL(new URL(wsdlURL));
        Document schemaDoc = domUtil.inputStreamToDocument(new FileInputStream(mainSchemaFile));
        String[] schemaImportLoc = wsdl.getImportLocations();
        String[] namespaceImport = wsdl.getImportNamespaces();
        Document[] schema = wsdl.getSchemas();
        String[] internalNamespaces = wsdl.getInternalNamespaces();


        if (schemaImportLoc != null) {
            importedSchemas = schemaImportLoc.length;
            for (i = 0; i < schemaImportLoc.length; i++) {
                if (schemaImportLoc[i].isEmpty())
                    continue;
                relativePath = "wsdlSchemaImport_" + new Integer(i + 1).toString();
                currentSchemaLocationDirecotry = new File(schemaDirecorty, relativePath);
                currentSchemaLocationDirecotry.mkdirs();
                SchemaDownload(schemaImportLoc[i], currentSchemaLocationDirecotry.getCanonicalFile(), relativePath);
                String[] URLSplit = schemaImportLoc[i].split("/");
                schemaFileName = URLSplit[URLSplit.length - 1];
                schemaFileName = URLEncoder.encode(schemaFileName, "UTF-8");
                if (!schemaFileName.endsWith(".xsd")) {
                    schemaFileName += ".xsd";
                }
                elementImport = schemaDoc.createElementNS(SCHEMA_NAMESPACE, "import");
                elementImport.setAttribute("schemaLocation", relativePath + "/" + schemaFileName);
                elementImport.setAttribute("namespace", namespaceImport[i]);
                schemaDoc.getDocumentElement().appendChild(elementImport);
            }
        }

//        if (schema != null) {
//            for (i = 0; i < schema.length; i++) {
//                relativePath = "wsdlSchemaImport_" + new Integer(importedSchemas + i + 1).toString();
//                currentSchemaLocationDirecotry = new File(schemaDirecorty, relativePath);
//                currentSchemaLocationDirecotry.mkdirs();
//                DOMUtil.dumpXML(schema[i], new File(currentSchemaLocationDirecotry.getCanonicalFile(), relativePath));
//                schemaFileName = "internalSchema_" + new Integer(i + 1) + ".xsd";
//                elementImport = schemaDoc.createElementNS(SCHEMA_NAMESPACE, "import");
//                elementImport.setAttribute("schemaLocation", relativePath + "/" + schemaFileName);
//                elementImport.setAttribute("namespace", internalNamespaces[i]);
//                schemaDoc.getDocumentElement().appendChild(elementImport);
//            }
//        }

        DOMUtil.dumpXML(schemaDoc, mainSchemaFile);
        return wsdl.getTargetNameSpace();
    }

    public String[] getExternalSchemasAndSaveWithRelativePath(Document xmlDoc, File pathSchema, String relativeSchemaFolder) throws Exception {
        String[] URLSplit = null;
        ArrayList<String> extSchemaUrls = new ArrayList();
        String schemaFileName;
        String schemaLoc = "";
        NodeList children;
        Element xmlElement = xmlDoc.getDocumentElement();
        int i = 0;
        Element tag;
        children = xmlElement.getElementsByTagNameNS("*", TAG_IMPORT);
        for (i = 0; i < children.getLength(); i++) {
            tag = (Element) children.item(i);
            schemaLoc = tag.getAttribute(ATTRIBUTE_SCHEMA_LOCATION);
            extSchemaUrls.add(schemaLoc);
            URLSplit = schemaLoc.split("/");
            schemaFileName = URLSplit[URLSplit.length - 1];
            schemaFileName = URLEncoder.encode(schemaFileName, "UTF-8");
            if (!schemaFileName.endsWith(".xsd")) {
                schemaFileName += ".xsd";
            }
            tag.setAttribute(ATTRIBUTE_SCHEMA_LOCATION, relativeSchemaFolder + "/" + schemaFileName);
        }

        children = null;
        children = xmlElement.getElementsByTagNameNS("*", TAG_INCLUDE);
        for (i = 0; i < children.getLength(); i++) {
            tag = (Element) children.item(i);
            schemaLoc = tag.getAttribute(ATTRIBUTE_SCHEMA_LOCATION);
            extSchemaUrls.add(schemaLoc);
            URLSplit = schemaLoc.split("/");
            schemaFileName = URLSplit[URLSplit.length - 1];
            schemaFileName = URLEncoder.encode(schemaFileName, "UTF-8");
            if (!schemaFileName.endsWith(".xsd")) {
                schemaFileName += ".xsd";
            }
            tag.setAttribute(ATTRIBUTE_SCHEMA_LOCATION, relativeSchemaFolder + "/" + schemaFileName);
        }

        DOMUtil.dumpXML(xmlElement.getOwnerDocument(), pathSchema);
        return extSchemaUrls.toArray(new String[extSchemaUrls.size()]);
    }

    public void SchemaDownload(String schemaURLString, File schemaDirectory, String relativeSchemaPath) throws Exception {
        DOMUtil du = new DOMUtil();
        URL schemaURL = new URL(schemaURLString);
        String[] URLSplit = schemaURLString.split("/");
        String schemaFileName = URLSplit[URLSplit.length - 1];
        /*  URLConnection conn = schemaURL.openConnection();
        conn.setConnectTimeout(CONNECTION_TIME_OUT);
        conn.setReadTimeout(CONNECTION_TIME_OUT);*/
        Document schemaDoc = du.inputStreamToDocument(/*conn.getInputStream()*/schemaURL.openStream());

        //  schemaFileName=URLEncoder.encode(schemaFileName, "UTF-8");
        if (!schemaFileName.endsWith(".xsd")) {
            schemaFileName += ".xsd";
        }
        File schemaPath = new File(schemaDirectory, schemaFileName);
        if (!schemaPath.exists()) {
            String[] importedScheams = getExternalSchemasAndSaveWithRelativePath(schemaDoc, schemaPath, relativeSchemaPath);

            for (int i = 0; i < importedScheams.length; i++) {
                SchemaDownload(importedScheams[i], schemaDirectory, relativeSchemaPath);
            }
        }


    }

    private String getNormalizedNameSchema(String name) {

        String schemaName = name.replaceAll("\\?", "_");
        schemaName = schemaName.replaceAll("=", "_");

        return schemaName;
    }
}
