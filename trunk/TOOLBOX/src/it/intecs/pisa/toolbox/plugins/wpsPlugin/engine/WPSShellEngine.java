package it.intecs.pisa.toolbox.plugins.wpsPlugin.engine;


import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.toolbox.plugins.wpsPlugin.manager.WPSOperation;
import it.intecs.pisa.toolbox.service.TBXAsynchronousOperation;
import it.intecs.pisa.toolbox.service.TBXOperation;
import it.intecs.pisa.toolbox.service.TBXSynchronousOperation;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;

import java.util.Vector;
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
public class WPSShellEngine implements WPSEngine{

    private static String SHELL_SCRIPT_PATH="Resources/ShellScripts";
    private static String SHELL_SCRIPT_FILE_PREFIX ="execute_";
    private static String EXECUTE_SHELL_ENGINE_SCRIPT_FILE_NAME ="scriptShellEngine.tscript";
    private static String EXECUTE_SHELL_ERROR_SCRIPT_FILE_NAME ="globalError.tscript";
    private static String EXECUTE_ASYNC_SHELL_ENGINE_SCRIPT_FOLDER_PATH="AdditionalResources/WPS/ExecuteRequestToolboxScript/Async/ShellEngine/";;


    private static String EXECUTE_ENVIRONMENT_TXT_FILE_TOP ="AdditionalResources/WPS/XSL/Execute_ShellEnvironment_top.txt";
    private static String EXECUTE_ENVIRONMENT_TXT_FILE_BOTTOM ="AdditionalResources/WPS/XSL/Execute_ShellEnvironment_bottom.txt";
    private static String EXECUTE_ENVIRONMENT_XSL_FILE_PATH ="AdditionalResources/WPS/XSL/Execute_Shell_Enviorement.xsl";

    private static String SHELL_TEMPLATE_XSLT_PATH="AdditionalResources/WPS/XSL/Create_ShellScript_Template.xsl";
    private static String SHELL_OUTPUT_MANAGER_XLST_PATH="AdditionalResources/WPS/XSL/OutptManager_GrassScript.xsl";

    private InputStream shellStream=null;
    private DOMUtil domUtil=new DOMUtil();
    private static Transformer transformer;

    private String engineName="Shell";

    public TBXSynchronousOperation createWPSSyncOperation(File newServicePath, String processingName) throws Exception {
        TBXSynchronousOperation operationShellDescr=WPSOperation.newWPSSyncOperation(processingName);
        FileInputStream topStream,bottomStream;
        SequenceInputStream seqStream;
        Vector<InputStream> streams;
        topStream=new FileInputStream(new File(newServicePath,EXECUTE_ENVIRONMENT_TXT_FILE_TOP));
        bottomStream=new FileInputStream(new File(newServicePath,EXECUTE_ENVIRONMENT_TXT_FILE_BOTTOM));
        streams=new Vector<InputStream>();
        streams.add(topStream);
        File shellScriptFolder=new File(newServicePath,SHELL_SCRIPT_PATH);
        shellScriptFolder.mkdirs();
        IOUtil.copy(shellStream, new FileOutputStream(new File(shellScriptFolder,SHELL_SCRIPT_FILE_PREFIX+processingName+"_original.sh")));
        streams.add(new FileInputStream(new File(shellScriptFolder,SHELL_SCRIPT_FILE_PREFIX+processingName+"_original.sh")));
        /*String shellOutpManager=IOUtil.inputToString(new FileInputStream(new File(newServicePath,SHELL_SCRIPT_PATH+"/"+SHELL_SCRIPT_FILE_PREFIX+processingName+"_outputManager.tmp")));
        shellOutpManager=shellOutpManager.replaceAll("<", "&lt;");
        shellOutpManager=shellOutpManager.replaceAll(">", "&gt;");
        shellOutpManager=shellOutpManager.replaceAll("&", "&amp;");
        streams.add(new ByteArrayInputStream(shellOutpManager.getBytes()));*/
        streams.add(bottomStream);
        
        seqStream=new SequenceInputStream(streams.elements());
        String shellScript=IOUtil.inputToString(seqStream);
        FileOutputStream shellScriptOutputStream=new FileOutputStream(new File(newServicePath,EXECUTE_ENVIRONMENT_XSL_FILE_PATH));
        shellScriptOutputStream.write(shellScript.getBytes());
        shellScriptOutputStream.close();

        String shellOutpManager=IOUtil.loadString(new File(newServicePath,SHELL_SCRIPT_PATH+"/"+SHELL_SCRIPT_FILE_PREFIX+processingName+"_outputManager.tmp"));
        String shellProcessScript=IOUtil.inputToString(
                new FileInputStream(new File(shellScriptFolder,
                SHELL_SCRIPT_FILE_PREFIX+processingName+"_original.sh")))
                +shellOutpManager;
        shellScriptOutputStream=new FileOutputStream(new File(newServicePath,getScriptPathforProcessingName(processingName)));
        shellScriptOutputStream.write(shellProcessScript.getBytes());
        shellScriptOutputStream.close();
        
        operationShellDescr.setScripts(getExecuteScriptDescriptorSync(newServicePath,EXECUTE_OPERATION_PREFIX+processingName));
        operationShellDescr.setAdmittedHosts("");
      return operationShellDescr;
    }

    public TBXAsynchronousOperation createWPSAsyncOperation(File newServicePath, String processingName) throws Exception {
       TBXAsynchronousOperation operationEngineDescr=WPSOperation.newWPSAsyncOperation(newServicePath,processingName);
       operationEngineDescr.setScripts(getExecuteScriptDescriptorAsync(newServicePath,operationEngineDescr.getName()));
       operationEngineDescr.setAdmittedHosts("");
       return operationEngineDescr;
    }

    public Script[] getExecuteScriptDescriptorSync(File servicePath, String operationName) throws IOException, SAXException{
     Script[] scripts=new Script[2];
     scripts[0] = new Script();
     scripts[0].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_TOOLBOX_SCRIPT_FOLDER_PATH+EXECUTE_SHELL_ENGINE_SCRIPT_FILE_NAME))));
     scripts[0].setPath(PATH_OPERATION+"/"+operationName + "/"+ FIRST_SCRIPT_FILE_NAME);
     scripts[0].setType(Script.SCRIPT_TYPE_FIRST_SCRIPT);
     scripts[1] = new Script();
     scripts[1].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_TOOLBOX_SCRIPT_FOLDER_PATH+EXECUTE_SHELL_ERROR_SCRIPT_FILE_NAME))));
     scripts[1].setPath(PATH_OPERATION+"/"+operationName + "/"+ GLOBAL_ERROR_SCRIPT_FILE_NAME);
     scripts[1].setType(Script.SCRIPT_TYPE_GLOBAL_ERROR);
     return scripts;
    }

    public Script[] getExecuteScriptDescriptorAsync(File servicePath, String operationName) throws IOException, SAXException {
      Script[] scripts=new Script[6];
      scripts[0] = new Script();
      scripts[0].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_SHELL_ENGINE_SCRIPT_FOLDER_PATH+FIRST_SCRIPT_FILE_NAME))));
      scripts[0].setPath(PATH_OPERATION+"/"+operationName + "/"+FIRST_SCRIPT_FILE_NAME);
      scripts[0].setType(Script.SCRIPT_TYPE_FIRST_SCRIPT);
      scripts[1] = new Script();
      scripts[1].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_SHELL_ENGINE_SCRIPT_FOLDER_PATH+SECOND_SCRIPT_FILE_NAME))));
      scripts[1].setPath(PATH_OPERATION+"/"+operationName + "/"+SECOND_SCRIPT_FILE_NAME);
      scripts[1].setType(Script.SCRIPT_TYPE_SECOND_SCRIPT);
      scripts[2] = new Script();
      scripts[2].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_SHELL_ENGINE_SCRIPT_FOLDER_PATH+THIRD_SCRIPT_FILE_NAME))));
      scripts[2].setPath(PATH_OPERATION+"/"+operationName + "/"+THIRD_SCRIPT_FILE_NAME);
      scripts[2].setType(Script.SCRIPT_TYPE_THIRD_SCRIPT);
      scripts[3] = new Script();
      scripts[3].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_SHELL_ENGINE_SCRIPT_FOLDER_PATH+RESPONSE_BUILDER_SCRIPT_FILE_NAME))));
      scripts[3].setPath(PATH_OPERATION+"/"+operationName + "/"+RESPONSE_BUILDER_SCRIPT_FILE_NAME);
      scripts[3].setType(Script.SCRIPT_TYPE_RESPONSE_BUILDER);
      scripts[4] = new Script();
      scripts[4].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_SHELL_ENGINE_SCRIPT_FOLDER_PATH+RESPONSE_BUILDER_ERROR_SCRIPT_FILE_NAME))));
      scripts[4].setPath(PATH_OPERATION+"/"+operationName + "/"+RESPONSE_BUILDER_ERROR_SCRIPT_FILE_NAME);
      scripts[4].setType(Script.SCRIPT_TYPE_ERROR_ON_RESP_BUILDER);
      scripts[5] = new Script();
      scripts[5].setScriptDoc(domUtil.inputStreamToDocument(new FileInputStream(new File(servicePath, EXECUTE_ASYNC_SHELL_ENGINE_SCRIPT_FOLDER_PATH+GLOBAL_ERROR_SCRIPT_FILE_NAME))));
      scripts[5].setPath(PATH_OPERATION+"/"+operationName + "/"+GLOBAL_ERROR_SCRIPT_FILE_NAME);
      scripts[5].setType(Script.SCRIPT_TYPE_GLOBAL_ERROR);
      return scripts;
    }

    public void setScriptEngine(Object script) throws Exception{
         shellStream=(InputStream) script;
    }

    public void updateScriptEngine(File newServicePath, String processingName, Object script)throws Exception{
        FileInputStream topStream,bottomStream;
        SequenceInputStream seqStream;
        shellStream=(InputStream) script;
        Vector<InputStream> streams;
        topStream=new FileInputStream(new File(newServicePath,EXECUTE_ENVIRONMENT_TXT_FILE_TOP));
        bottomStream=new FileInputStream(new File(newServicePath,EXECUTE_ENVIRONMENT_TXT_FILE_BOTTOM));
        streams=new Vector<InputStream>();
        streams.add(topStream);
        File shellScriptFolder=new File(newServicePath,SHELL_SCRIPT_PATH);
        shellScriptFolder.mkdirs();
        IOUtil.copy(shellStream, new FileOutputStream(new File(shellScriptFolder,SHELL_SCRIPT_FILE_PREFIX+processingName+"_original.sh")));
        streams.add(new FileInputStream(new File(shellScriptFolder,SHELL_SCRIPT_FILE_PREFIX+processingName+"_original.sh")));
        /*String shellOutpManager=IOUtil.inputToString(new FileInputStream(new File(newServicePath,SHELL_SCRIPT_PATH+"/"+SHELL_SCRIPT_FILE_PREFIX+processingName+"_outputManager.tmp")));
        shellOutpManager=shellOutpManager.replaceAll("<", "&lt;");
        shellOutpManager=shellOutpManager.replaceAll(">", "&gt;");
        shellOutpManager=shellOutpManager.replaceAll("&", "&amp;");
        streams.add(new ByteArrayInputStream(shellOutpManager.getBytes()));*/
        streams.add(bottomStream);
       
        seqStream=new SequenceInputStream(streams.elements());
        String shellScript=IOUtil.inputToString(seqStream);
        FileOutputStream shellScriptOutputStream=new FileOutputStream(new File(newServicePath,EXECUTE_ENVIRONMENT_XSL_FILE_PATH));
        shellScriptOutputStream.write(shellScript.getBytes());
        shellScriptOutputStream.close();

        String shellOutpManager=IOUtil.loadString(new File(newServicePath,SHELL_SCRIPT_PATH+"/"+SHELL_SCRIPT_FILE_PREFIX+processingName+"_outputManager.tmp"));
        String shellProcessScript=IOUtil.inputToString(
                new FileInputStream(new File(shellScriptFolder,
                SHELL_SCRIPT_FILE_PREFIX+processingName+"_original.sh")))
                +shellOutpManager;
        shellScriptOutputStream=new FileOutputStream(new File(newServicePath,getScriptPathforProcessingName(processingName)));
        shellScriptOutputStream.write(shellProcessScript.getBytes());
        shellScriptOutputStream.close();
    }

    public String generateEngineTemplate(File newServicePath, Document describeDocument, String processingName) throws Exception {
        Document xslDocument;
        String shellTemplate=null;
        File stylesheet=new File(newServicePath, SHELL_TEMPLATE_XSLT_PATH);
        File shellScriptFolder=new File(newServicePath,SHELL_SCRIPT_PATH);
        shellScriptFolder.mkdirs();
        if(stylesheet.exists()){
           xslDocument=domUtil.fileToDocument(stylesheet);
           transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
           transformer.transform(new StreamSource(DOMUtil.getDocumentAsInputStream(describeDocument)), new StreamResult(new FileOutputStream(new File(shellScriptFolder,SHELL_SCRIPT_FILE_PREFIX+processingName+"_template.sh"))));
           shellTemplate=IOUtil.loadString(new File(shellScriptFolder,SHELL_SCRIPT_FILE_PREFIX+processingName+"_template.sh"));
        }
       return shellTemplate;
    }

    public String getEngineName() {
        return engineName;
    }

    public void generateEngineOutputManager(File servicePath, Document describeDocument, String processingName) throws Exception{
        File stylesheet=new File(servicePath, SHELL_OUTPUT_MANAGER_XLST_PATH);
        Document xslDocument;
        File shellScriptFolder=new File(servicePath,SHELL_SCRIPT_PATH);
        shellScriptFolder.mkdirs();

        if(stylesheet.exists()){
             xslDocument=domUtil.fileToDocument(stylesheet);
             transformer = TransformerFactory.newInstance().newTemplates(new DOMSource(xslDocument)).newTransformer();
             transformer.setParameter("processingName", processingName);
             transformer.setParameter("complexDataFolderPath", new File(servicePath, COMPLEX_DATA_PATH).getAbsolutePath());
             transformer.transform(new StreamSource(DOMUtil.getDocumentAsInputStream(describeDocument)), new StreamResult(new FileOutputStream(new File(shellScriptFolder,SHELL_SCRIPT_FILE_PREFIX+processingName+"_outputManager.tmp"))));
        }
    }

     public String getScriptPathforProcessingName(String processingName) {
        return SHELL_SCRIPT_PATH+"/"+SHELL_SCRIPT_FILE_PREFIX+processingName+".sh";
    }

     public String getOriginalScriptPathforProcessingName(String processingName) {
        return SHELL_SCRIPT_PATH+"/"+SHELL_SCRIPT_FILE_PREFIX+processingName+"_original.sh";
    }

    public void deleteScriptEngine(File newServicePath, String processingName) throws Exception {
        File outputMan=new File(newServicePath,SHELL_SCRIPT_PATH+"/"+SHELL_SCRIPT_FILE_PREFIX+processingName+"_outputManager.tmp");
        outputMan.delete();
        File script=new File(newServicePath,getScriptPathforProcessingName(processingName));
        script.delete();
        File original=new File(newServicePath,getOriginalScriptPathforProcessingName(processingName));
        original.delete();
    }



}
