package it.intecs.pisa.toolbox.plugins.wpsPlugin.engine;

import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.toolbox.plugins.wpsPlugin.manager.WPSOperation;
import it.intecs.pisa.toolbox.service.TBXAsynchronousOperation;
import it.intecs.pisa.toolbox.service.TBXScript;
import it.intecs.pisa.toolbox.service.TBXSynchronousOperation;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author Andrea Marongiu
 */
public class WPSGrassEngine implements WPSEngine{

  private static String GRASS_SCRIPT_PATH="Resources/GrassScripts";
  private static String GRASS_CHECKSTATUS_PATH="Resources/Scripts/checkStatus.sh";
  private static String GRASS_SCRIPT_FILE_PREFIX ="execute_";
  private static String EXECUTE_GRASS_ENGINE_SCRIPT_FILE_NAME ="scriptGrassEngine.tscript";
  private static String EXECUTE_ERROR_SCRIPT_FILE_NAME ="globalError.tscript";
  private static String EXECUTE_ASYNC_GRASS_ENGINE_SCRIPT_FOLDER_PATH ="AdditionalResources/WPS/ExecuteRequestToolboxScript/Async/GrassEngine/";
  private static String GRASS_TEMPLATE_XSLT_PATH="AdditionalResources/WPS/XSL/GrassEngine/Create_GrassScript_Template.xsl";
  private static String GRASS_OUTPUT_MANAGER_XLST_PATH="AdditionalResources/WPS/XSL/GrassEngine/OutptManager_GrassScript.xsl";


  private String grassScript="";
  private DOMUtil domUtil=new DOMUtil();
  private static Transformer transformer;
  private String engineName="Grass";

    public WPSGrassEngine(){


    }

    public void setScriptEngine(Object script) throws Exception{
        if(script instanceof String)
            grassScript=(String)script;
        else
          if(script instanceof InputStream)
            grassScript=IOUtil.inputToString((InputStream)script);


    }

    public String generateEngineTemplate(File newServicePath, Document describeDocument, String processingName) throws Exception {
        File stylesheet=new File(newServicePath, GRASS_TEMPLATE_XSLT_PATH);
        File grassScriptFolder=new File(newServicePath,GRASS_SCRIPT_PATH);
        grassScriptFolder.mkdirs();
        Document xslDocument;
        String grassTemplate=null;
        if(stylesheet.exists()){
            xslDocument=domUtil.fileToDocument(stylesheet);
               transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
               transformer.transform(new StreamSource(DOMUtil.getDocumentAsInputStream(describeDocument)), new StreamResult(new FileOutputStream(new File(grassScriptFolder,GRASS_SCRIPT_FILE_PREFIX+processingName+"_template.sh"))));
               grassTemplate=IOUtil.loadString(new File(grassScriptFolder,GRASS_SCRIPT_FILE_PREFIX+processingName+"_template.sh"));
            }
        return grassTemplate;
    }

    public void updateScriptEngine(File newServicePath, String processingName, Object script)throws Exception{
        if(script instanceof String)
            grassScript=(String)script;
        else
          if(script instanceof InputStream)
            grassScript=IOUtil.inputToString((InputStream)script);

        IOUtil.copy(new ByteArrayInputStream(getGrassScript().getBytes()),
                new FileOutputStream(new File(newServicePath,getOriginalScriptPathforProcessingName(processingName))));
        String grassOutpManager=IOUtil.loadString(new File(newServicePath,GRASS_SCRIPT_PATH+"/"+GRASS_SCRIPT_FILE_PREFIX+processingName+"_outputManager.tmp"));
        String grassCheckStatus=IOUtil.loadString(new File(newServicePath,GRASS_CHECKSTATUS_PATH));
        String grassProcessScript=grassCheckStatus+getGrassScript().replaceAll("#!/bin/bash", "")+grassOutpManager;
        FileOutputStream grassScriptOutputStream=new FileOutputStream(new File(newServicePath,getScriptPathforProcessingName(processingName)));
        grassScriptOutputStream.write(grassProcessScript.getBytes());
        grassScriptOutputStream.close();
    }

    public void deleteScriptEngine(File newServicePath, String processingName)throws Exception{
        File outputMan=new File(newServicePath,GRASS_SCRIPT_PATH+"/"+GRASS_SCRIPT_FILE_PREFIX+processingName+"_outputManager.tmp");
        outputMan.delete();
        File script=new File(newServicePath,getScriptPathforProcessingName(processingName));
        script.delete();
        File original=new File(newServicePath,getOriginalScriptPathforProcessingName(processingName));
        original.delete();
    }

    public TBXSynchronousOperation createWPSSyncOperation(File newServicePath, String processingName) throws Exception {
        TBXSynchronousOperation operationGrassDescr=WPSOperation.newWPSSyncOperation(processingName);
        File grassScriptFolder=new File(newServicePath,GRASS_SCRIPT_PATH);
        grassScriptFolder.mkdirs();
        IOUtil.copy(new ByteArrayInputStream(getGrassScript().getBytes()),
                new FileOutputStream(new File(newServicePath,getOriginalScriptPathforProcessingName(processingName))));
         String grassOutpManager=IOUtil.loadString(new File(newServicePath,GRASS_SCRIPT_PATH+"/"+GRASS_SCRIPT_FILE_PREFIX+processingName+"_outputManager.tmp"));
         String grassCheckStatus=IOUtil.loadString(new File(newServicePath,GRASS_CHECKSTATUS_PATH));
         String grassProcessScript=grassCheckStatus+getGrassScript().replaceAll("#!/bin/bash", "")+grassOutpManager;
         FileOutputStream grassScriptOutputStream=new FileOutputStream(new File(newServicePath,getScriptPathforProcessingName(processingName)));
         grassScriptOutputStream.write(grassProcessScript.getBytes());
         grassScriptOutputStream.close();
         operationGrassDescr.setScripts(getExecuteScriptDescriptorSync(newServicePath,EXECUTE_OPERATION_PREFIX+processingName));
         operationGrassDescr.setAdmittedHosts("");
        return operationGrassDescr;
    }

    public TBXAsynchronousOperation createWPSAsyncOperation(File newServicePath, String processingName) throws Exception {
        TBXAsynchronousOperation operationEngineDescr=WPSOperation.newWPSAsyncOperation(newServicePath,processingName);
        operationEngineDescr.setScripts(getExecuteScriptDescriptorAsync(newServicePath,operationEngineDescr.getName()));
         operationEngineDescr.setAdmittedHosts("");
        return operationEngineDescr;
    }

    public TBXScript[] getExecuteScriptDescriptorSync(File servicePath, String operationName) throws IOException, SAXException{
     TBXScript[] scripts=new TBXScript[2];
     scripts[0] = new TBXScript();
     scripts[0].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_TOOLBOX_SCRIPT_FOLDER_PATH+EXECUTE_GRASS_ENGINE_SCRIPT_FILE_NAME))));
     scripts[0].setPath(PATH_OPERATION+"/"+operationName + "/"+FIRST_SCRIPT_FILE_NAME);
     scripts[0].setType(Script.SCRIPT_TYPE_FIRST_SCRIPT);
     scripts[1] = new TBXScript();
     scripts[1].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_TOOLBOX_SCRIPT_FOLDER_PATH+EXECUTE_ERROR_SCRIPT_FILE_NAME))));
     scripts[1].setPath(PATH_OPERATION+"/"+operationName + "/"+GLOBAL_ERROR_SCRIPT_FILE_NAME);
     scripts[1].setType(Script.SCRIPT_TYPE_GLOBAL_ERROR);
     return scripts;
    }

    public TBXScript[] getExecuteScriptDescriptorAsync(File servicePath, String operationName) throws IOException, SAXException{
      TBXScript[] scripts=new TBXScript[6];
      
      scripts[0] = new TBXScript();
      scripts[0].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_GRASS_ENGINE_SCRIPT_FOLDER_PATH+FIRST_SCRIPT_FILE_NAME))));
      scripts[0].setPath(PATH_OPERATION+"/"+operationName + "/"+FIRST_SCRIPT_FILE_NAME);
      scripts[0].setType(Script.SCRIPT_TYPE_FIRST_SCRIPT);
      scripts[1] = new TBXScript();
      scripts[1].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_GRASS_ENGINE_SCRIPT_FOLDER_PATH+SECOND_SCRIPT_FILE_NAME))));
      scripts[1].setPath(PATH_OPERATION+"/"+operationName + "/"+SECOND_SCRIPT_FILE_NAME);
      scripts[1].setType(Script.SCRIPT_TYPE_SECOND_SCRIPT);
      scripts[2] = new TBXScript();
      scripts[2].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_GRASS_ENGINE_SCRIPT_FOLDER_PATH+THIRD_SCRIPT_FILE_NAME))));
      scripts[2].setPath(PATH_OPERATION+"/"+operationName + "/"+THIRD_SCRIPT_FILE_NAME);
      scripts[2].setType(Script.SCRIPT_TYPE_THIRD_SCRIPT);
      scripts[3] = new TBXScript();
      scripts[3].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_GRASS_ENGINE_SCRIPT_FOLDER_PATH+RESPONSE_BUILDER_SCRIPT_FILE_NAME))));
      scripts[3].setPath(PATH_OPERATION+"/"+operationName + "/"+RESPONSE_BUILDER_SCRIPT_FILE_NAME);
      scripts[3].setType(Script.SCRIPT_TYPE_RESPONSE_BUILDER);
      scripts[4] = new TBXScript();
      scripts[4].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_GRASS_ENGINE_SCRIPT_FOLDER_PATH+RESPONSE_BUILDER_ERROR_SCRIPT_FILE_NAME))));
      scripts[4].setPath(PATH_OPERATION+"/"+operationName + "/"+RESPONSE_BUILDER_ERROR_SCRIPT_FILE_NAME);
      scripts[4].setType(Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER);
      scripts[5] = new TBXScript();
      scripts[5].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_GRASS_ENGINE_SCRIPT_FOLDER_PATH+GLOBAL_ERROR_SCRIPT_FILE_NAME))));
      scripts[5].setPath(PATH_OPERATION+"/"+operationName + "/"+GLOBAL_ERROR_SCRIPT_FILE_NAME);
      scripts[5].setType(Script.SCRIPT_TYPE_GLOBAL_ERROR);
      return scripts;
    }


     public String getScriptPathforProcessingName(String processingName) {
        return GRASS_SCRIPT_PATH+"/"+GRASS_SCRIPT_FILE_PREFIX+processingName+".sh";
    }


     public String getOriginalScriptPathforProcessingName(String processingName) {
        return GRASS_SCRIPT_PATH+"/"+GRASS_SCRIPT_FILE_PREFIX+processingName+"_original.sh";
    }
    /**
     * @return the grassScript
     */
    public String getGrassScript() {
        return grassScript;
    }

    /**
     * @param grassScript the grassScript to set
     */
    public void setGrassScript(String grassScript) {
        this.grassScript = grassScript;
    }

    /**
     * @return the engineName
     */
    public String getEngineName() {
        return engineName;
    }

    public void generateEngineOutputManager(File servicePath, Document describeDocument, String processingName) throws Exception{
        File stylesheet=new File(servicePath, GRASS_OUTPUT_MANAGER_XLST_PATH);
        Document xslDocument;
        File grassScriptFolder=new File(servicePath,GRASS_SCRIPT_PATH);
        grassScriptFolder.mkdirs();

        if(stylesheet.exists()){
             xslDocument=domUtil.fileToDocument(stylesheet);
             transformer = TransformerFactory.newInstance().newTemplates(new DOMSource(xslDocument)).newTransformer();
             transformer.setParameter("processingName", processingName);
             transformer.setParameter("complexDataFolderPath", new File(servicePath, COMPLEX_DATA_PATH).getAbsolutePath());
             transformer.transform(new StreamSource(DOMUtil.getDocumentAsInputStream(describeDocument)), new StreamResult(new FileOutputStream(new File(grassScriptFolder,GRASS_SCRIPT_FILE_PREFIX+processingName+"_outputManager.tmp"))));
        }
    }






}
