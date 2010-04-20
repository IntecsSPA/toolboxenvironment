/*
 *
 * ****************************************************************************
 *  Copyright 2003*2007 Intecs
 ****************************************************************************
 *  This file is part of TOOLBOX.
 *
 *  TOOLBOX is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  TOOLBOX is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with TOOLBOX; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 ****************************************************************************
 *
 */

package it.intecs.pisa.util;
import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.toolbox.configuration.ToolboxConfiguration;
import it.intecs.pisa.toolbox.service.ServiceManager;
import it.intecs.pisa.toolbox.service.TBXService;
import java.util.*;
import java.io.*;
import org.w3c.dom.*;
import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.*;
import javax.xml.transform.*;
import javax.xml.transform.stream.*;
import java.net.*;

import it.intecs.pisa.util.*;


public class URLReader {
    private static final DOMUtil domUtil = new DOMUtil();
    private static final DOMUtil domUtilNS = new DOMUtil(true);
    private static final int PORT = 80;
    private static final String SCHEMA = "schemas/";
    
    public static Object getURLContent( String url) throws Exception {
        HttpMethod method;
        try {
            ToolboxConfiguration toolboxConfiguration;
            toolboxConfiguration=ToolboxConfiguration.getInstance();

            String host = "http://services.eoportal.org"; //toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SSE_PORTAL);
            String fullUrl;

            URLConnection conn;
            URL connUrl;

            if(url.startsWith("http://"))
                fullUrl=url;
            else fullUrl=host+url;

            connUrl=new URL(fullUrl);
            conn=connUrl.openConnection();

            Reader in = new InputStreamReader(conn.getInputStream());
            StringBuffer out = new StringBuffer();
            char[] buffer = new char[1024];
            for (int count = in.read(buffer); count >= 0; count = in.read(buffer)) {
                out.append(buffer, 0, count);
            }
            return out.toString();
        } catch( Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }
    
    public static Object getFullURLContent( String urlString) throws Exception {
        
        String returnString ="";
        
        URL url=null;
        URLConnection connection=null;
        
        // Open Connection
        try {
            url = new URL(urlString);
        } catch (MalformedURLException ex) {
            ex.printStackTrace();
        }
        
        try {
            connection = url.openConnection();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        HttpURLConnection httpConn = (HttpURLConnection) connection;
        try {
            
            
            StreamSource source = new StreamSource(httpConn.getInputStream());
            
            TransformerFactory fac = TransformerFactory.newInstance();
            Transformer trans = null;
            StreamResult res = new StreamResult(new StringWriter());
            try {
                trans = fac.newTransformer();
            } catch (TransformerConfigurationException ex) {
                ex.printStackTrace();
            }
            try {
                
                trans.transform(source,res);
            } catch (TransformerException ex) {
                ex.printStackTrace();
            }
            StringWriter w=(StringWriter) res.getWriter();
            
            returnString = w.toString();
            
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return returnString;
    }
    
    
    public static Object getURLContent( String host, String url, int port) throws Exception {
        HttpMethod method;
        try {
            method = new GetMethod(url);
            HttpConnection conn = new HttpConnection(host, port);
            String proxyHost;
            String proxyPort;
            if ((proxyHost = System.getProperty("http.proxyHost")) != null && (proxyPort = System.getProperty("http.proxyPort")) != null) {
                conn.setProxyHost(proxyHost);
                conn.setProxyPort(Integer.parseInt(proxyPort));
            }
            conn.open();
            method.execute(new HttpState(), conn);
            String inpTmp = method.getResponseBodyAsString();
            Reader in = new InputStreamReader(method.getResponseBodyAsStream());
            StringBuffer out = new StringBuffer();
            char[] buffer = new char[1024];
            for (int count = in.read(buffer); count >= 0; count = in.read(buffer)) {
                out.append(buffer, 0, count);
            }
            return out.toString();
        } catch( Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
    }
    
    public static void writeURLContentToXML( String host, String url, int port, String fileName ) throws Exception {
        try {
            String fileContent = (String)getURLContent( host, url, port );
            if( fileContent == null ) return;
            fileContent = fileContent.substring(fileContent.indexOf("<"), fileContent.lastIndexOf(">")+1);
            File newFile = new File( fileName );
            FileWriter fw = new FileWriter(newFile);
            fw.write(fileContent);
            fw.close();
        } catch( Exception e) {
            e.printStackTrace(System.out);
            return;
        }
    }
    
    public static void writeURLContentToXML( String infoXmlFile, String fileName ) throws Exception {
        try {
            String host = null;
            String url  = null;
            int port = 0;
            Element infoXml = new DOMUtil().fileToDocument(new File(infoXmlFile)).getDocumentElement();
            if ( infoXml != null ) {
                host = infoXml.getAttribute("newVersionHost");
                url  = infoXml.getAttribute("newVersionUrl");
                String portStr = infoXml.getAttribute("newVersionPort");
                Integer portInt = new Integer(portStr);
                port = portInt.intValue();
            }
           
            String fileContent = (String)getURLContent( host, url, port );
            if( fileContent == null ) return;
            fileContent = fileContent.substring(fileContent.indexOf("<"), fileContent.lastIndexOf(">")+1);
            File newFile = new File( fileName );
            FileWriter fw = new FileWriter(newFile);
            fw.write(fileContent);
            fw.close();
        } catch( Exception e) {
            e.printStackTrace(System.out);
            return;
        }
    }
    
    public static String fileToString( String fileName ) throws Exception {
        String thisLine;
        FileInputStream fis =  new FileInputStream(fileName);
        BufferedReader inputReader = new BufferedReader(new InputStreamReader(fis));
        StringBuffer out = new StringBuffer();
        while ((thisLine = inputReader.readLine()) != null) {
            out.append(thisLine);
        }
        return out.toString();
    }
    
    private static final boolean compareVersions(String newVersion, String currentVersion, boolean isMin ) {
        int i;
        StringTokenizer newer = new StringTokenizer(newVersion, "._-");
        StringTokenizer current = new StringTokenizer(currentVersion, "._-");
        while (current.hasMoreTokens()){
            if (!newer.hasMoreTokens()) return (false);
            String last = newer.nextToken();
            String cur = current.nextToken();
            int lastVal = 0;
            int curVal = 0;
            try{
                lastVal = Integer.parseInt(last);
                curVal = Integer.parseInt(cur);
            } catch (NumberFormatException nfe){
                return (false);
            }
            
            if(!isMin) {
                if ( lastVal < curVal )
                    return false;
                else
                    if( lastVal > curVal )
                        return true;
            } else {
                if (lastVal > curVal)
                    return false;
            }
        }
        
        if( newVersion.equals( currentVersion ) )
            return false;
        
        return (true);
    }
    
    
    public static boolean isVersionToUpdate( String newVersionFile, String currentVersionFile ) throws Exception {
        String thisLine = null;
        String currentVersion = null;
        String newVersion = null;
        try {
            Element infoXml = new DOMUtil().fileToDocument(new File(currentVersionFile)).getDocumentElement();
            if ( infoXml != null ) {
                currentVersion = infoXml.getAttribute("toolboxVersion");
            }
            
            Element tbUpgradeXml = new DOMUtil().fileToDocument(new File(newVersionFile)).getDocumentElement();
            if ( tbUpgradeXml != null ) {
                newVersion = tbUpgradeXml.getAttribute("version");
            }
            
            return compareVersions(newVersion, currentVersion, false );
        } catch( Exception e) {
            e.printStackTrace(System.out);
            return false;
        }
    }
    
    // Remove first String from a String array
    public static String[] removeFirstElement(String[] tokens) {
        int index = 0;
        int ix    = 0;
        String[] result = new String[tokens.length - 1];
        while(index < tokens.length ) {
            if ( index > 0 )
                result[index -1] = tokens[index];
            index++;
        }
        return result;
    }
    
    // Get references to schema Versions
    public static String[] getRefFromHTML(String content) {
        int index = 0;
        int ix    = 0;
        String[] matchList = content.split(SCHEMA);
        String[] result = null;
        result = removeFirstElement(matchList);
        index = 0;
        while(index < result.length ) {
            ix = result[index].indexOf(">");
            result[index] = result[index].substring(0, ix-1);
            index++;
        }
        
        return result;
    }
    
    // Get referenses to schema files
    public static String[] getRefFromHTML(String content, boolean isFile ) {
        int index = 0;
        int ix    = 0;
        if( !isFile )
            return getRefFromHTML(content);
        String[] matchList = getRefFromHTML(content);
        String[] result = removeFirstElement(matchList);
        return result;
    }
    
    public static String[] getVersionsToDW() {
        String[] versions = null;
        try {
            String content = (String)getURLContent("/" + SCHEMA);
            versions = getRefFromHTML(content);
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
        return versions;
    }
    
    public static String[] getFileToDW(String url) {
        String[] filesToDw = null;
        String[] filesToReturn = null;
        try {
            String content = (String)getURLContent(url);
            filesToDw = getRefFromHTML(content, true);
            int count = 0;
            String tmpValue;
            int ix = 0;
            while ( ix < filesToDw.length ) {
                tmpValue = filesToDw[ix];
                int pointIndex = tmpValue.lastIndexOf(".");
                if( pointIndex > 0 && (tmpValue.substring(pointIndex)).equals(".xsd") )
                    count++;
                ix++;
            }
            filesToReturn = new String[count];
            ix = 0;
            count = 0;
            while ( ix < filesToDw.length ) {
                tmpValue = filesToDw[ix];
                int pointIndex = tmpValue.lastIndexOf(".");
                if( pointIndex > 0 && (tmpValue.substring(pointIndex)).equals(".xsd") ) {
                    filesToReturn[count] = tmpValue;
                    count++;
                }
                ix++;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
        return filesToReturn;
    }
    
    public static String[] getDirectoryToDW(String url) {
        String[] filesToDw = null;
        String[] filesToReturn = null;
        try {
            String content = (String)getURLContent(url);
            filesToDw = getRefFromHTML(content, true);
            int count = 0;
            String tmpValue;
            int ix = 0;
            while ( ix < filesToDw.length ) {
                tmpValue = filesToDw[ix];
                if( tmpValue.endsWith("/") )
                    count++;
                ix++;
            }
            
            filesToReturn = new String[count];
            ix = 0;
            count = 0;
            while ( ix < filesToDw.length ) {
                tmpValue = filesToDw[ix];
                if( tmpValue.endsWith("/") ){
                    filesToReturn[count] = tmpValue;
                    count++;
                }
                ix++;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
        return filesToReturn;
    }
    
    
    // Import some directory's files into the Toolbox service
    public static String importFiles(String urlVersion, String versionName, String serviceName, Vector fileToImport ) throws Exception {
        int    index       = 0;
        int    ix          = 0;
        String fileName    = null;
        String warnMessage = "";
        String schemaName ="";
      /*  try {
            ToolboxConfigurator toolboxConfigurator = Toolbox.getToolboxConfigurator();
            ServiceDescriptor tempServiceDescriptor = (ServiceDescriptor) toolboxConfigurator.getTempServiceDescriptors().get(serviceName);
            LinkedHashMap schemas        = tempServiceDescriptor.getSchemas();
            LinkedHashMap schemasVersion = tempServiceDescriptor.getSchemasVersion();
            if ( schemasVersion == null ) {
                schemasVersion = new LinkedHashMap();
            }
            
            LinkedHashMap newSchemasVersion = new LinkedHashMap();
            while ( ix < fileToImport.size() ) {
                String schemaUrl = (String)fileToImport.get( ix );
                schemaName = schemaUrl.substring(schemaUrl.lastIndexOf("/") + 1);
                File schemaFile;
                Document schema = null;
                DOMUtil domUtil = new DOMUtil(true);
                if (schemaName.length() == 0) {
                    warnMessage = "Missing schema!";
                } else {
                    String contentFile = (String) getURLContent( ( urlVersion + schemaUrl ) );
                    schema = domUtil.inputStreamToDocument(new ByteArrayInputStream(contentFile.getBytes()));
                }
                newSchemasVersion.put(schemaName, schema);
                ix++;
            }
            
            
            Iterator keys;
            String key;
            // Old version removed from schemas
            if( !schemasVersion.isEmpty() ) {
                keys = schemasVersion.keySet().iterator();
                while (keys.hasNext()) {
                    key = (String) keys.next();
                    schemas.remove(key);
                }
            }
            
            // New version added to schemas
            if( !newSchemasVersion.isEmpty() ) {
                keys = newSchemasVersion.keySet().iterator();
                while (keys.hasNext()) {
                    key = (String) keys.next();
                    schemas.put(key, newSchemasVersion.get( key));
                }
            }
            
            tempServiceDescriptor.setSchemas(schemas);
            if( newSchemasVersion.size() > 0 ) {
                tempServiceDescriptor.setSchemasVersion(newSchemasVersion);
                tempServiceDescriptor.setSSEVersionName( new String(versionName) );
            }
        } catch (Exception e) {
            warnMessage = "Not well-formed file! : " + schemaName;
        }*/
        return warnMessage;
    }
    
    
    public static String downloadLastVersion(String serviceName) {
        String lastVersion;
        TBXService service;
        ServiceManager servMan;

        Interface interf;
       try {
           if(serviceName== null || serviceName.equals(""))
               return null;

            servMan=ServiceManager.getInstance();
            service=servMan.getService(serviceName);
            interf=service.getImplementedInterface();

            ToolboxConfiguration toolboxConfiguration;
            toolboxConfiguration=ToolboxConfiguration.getInstance();

            if(interf.getName().equals("SSE") && Boolean.parseBoolean(toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SCHEMA_VERSION_CHECK)))
            {
                String content = (String) URLReader.getURLContent("/" + SCHEMA);
                String[] versions = URLReader.getRefFromHTML(content);
                lastVersion = versions[ versions.length - 1 ];
                String SSEVersion = interf.getVersion();

                if(RevisionUtil.isEarlier(SSEVersion, lastVersion))
                     return lastVersion;
                else return null;

            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
            return null;
        }

        return null;
    }
    
    public static Document getDirectoryStructure( String url) throws Exception {
        Document urlTree = new DOMUtil().newDocument();
        Element rootNode = urlTree.createElement("urlContent");
        urlTree.appendChild(rootNode);
        try {
            getContent(url,urlTree,rootNode);
        } catch( Exception e) {
            e.printStackTrace(System.out);
            return null;
        }
        return urlTree;
    }
    
    public static void getContent( String url, Document urlTree, Element fatherNode) throws Exception {
        Element directoryNode = urlTree.createElement("directory");
        directoryNode.setAttribute("name",url);
        fatherNode.appendChild(directoryNode);
        try {
            String[] filesToDw = URLReader.getFileToDW("/" +SCHEMA + url);
            int index = 0;
            while(index < filesToDw.length ) {
                Element fileNode = urlTree.createElement("file");
                fileNode.setAttribute("name",filesToDw[index]);
                directoryNode.appendChild(fileNode);
                index++;
            }
            
            String[] directoryToDw = URLReader.getDirectoryToDW("/" +SCHEMA + url);
            index = 0;
            while(index < directoryToDw.length ) {
                getContent(directoryToDw[index],urlTree,directoryNode);
                index++;
            }
        } catch( Exception e) {
            e.printStackTrace(System.out);
        }
    }

    protected static String[] getSSESchemaDirectoryListingFromPortal(String baseUrl)
    {
        String[] listing;
        String[] fileListing;
        String[] directoryListing;
        String[] subDirectoryListing;
        Vector<String> subFolderFullListing;
        Enumeration<String> en;
        int i=0;
        String url;

        subFolderFullListing=new Vector<String>();

        ToolboxConfiguration toolboxConfiguration;
        toolboxConfiguration=ToolboxConfiguration.getInstance();

        url="http://"+toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SSE_PORTAL)+"/schemas/"+baseUrl;
        
        fileListing=getFileToDW(url);
        directoryListing=getDirectoryToDW(url);
        for(String subDir:directoryListing)
        {
            subDirectoryListing=getSSESchemaDirectoryListingFromPortal(subDir);
            for(String sub:subDirectoryListing)
            {
                System.out.println("ADDING "+sub);
                subFolderFullListing.add(sub);
            }
        }

        listing=new String[fileListing.length+subFolderFullListing.size()];
        en=subFolderFullListing.elements();
        for(String file:fileListing)
        {
            listing[i]=file;
            i++;
        }
        
        while(en.hasMoreElements())
        {
            listing[i]=en.nextElement();
            i++;
        }
        return listing;
    }

   public static void downloadSSESchemaVersionFromPortal(String schemaversion,File outputDir) throws Exception
   {
       String ssePortalBaseUrl;
       String[] fileListing;
       File outputFile;
       String content;

       ToolboxConfiguration toolboxConfiguration;
       toolboxConfiguration=ToolboxConfiguration.getInstance();

       ssePortalBaseUrl="http://"+toolboxConfiguration.getConfigurationValue(ToolboxConfiguration.SSE_PORTAL)+"/schemas/";
       fileListing=getSSESchemaDirectoryListingFromPortal(schemaversion);

       for(String listing:fileListing)
       {
           System.out.println("SSE FILE: "+listing);
           outputFile=new File(outputDir,listing);
           if(outputFile.isDirectory())
            outputFile.mkdirs();
           else
           {
                outputFile.getParentFile().mkdirs();
                content=(String) getURLContent(ssePortalBaseUrl+"/"+listing);
                IOUtil.dumpString(outputFile.getAbsolutePath(), content);
           }


       }

   }
}


