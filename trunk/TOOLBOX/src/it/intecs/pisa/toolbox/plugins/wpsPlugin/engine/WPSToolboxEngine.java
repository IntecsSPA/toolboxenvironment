package it.intecs.pisa.toolbox.plugins.wpsPlugin.engine;

import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.util.DOMUtil;
import it.intecs.pisa.util.IOUtil;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class WPSToolboxEngine implements WPSEngine{

    private static String TOOLBOX_SCRIPT_PATH="Resources/ToolboxScripts";
    private static String TOOLBOX_SCRIPT_FILE_PREFIX ="execute_";
    private static String TOOLBOX_TEMPLATE_XSLT_PATH="AdditionalResources/WPS/XSL/ToolboxEngine/Create_ToolboxScript_Template.xsl";


    private DOMUtil domUtil=new DOMUtil();
    private static Transformer transformer;

    private String engineName="Toolbox";

    public Operation createWPSSyncOperation(File newServicePath, String processingName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Operation createWPSAsyncOperation(File newServicePath, String processingName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Script[] getExecuteScriptDescriptorSync(File servicePath, String operationName) throws IOException, SAXException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Script[] getExecuteScriptDescriptorAsync(File servicePath, String operationName) throws IOException, SAXException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void setScriptEngine(Object script) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public String generateEngineTemplate(File newServicePath, Document describeDocument, String processingName) throws Exception {
        Document xslDocument;
        String toolboxTemplate=null;
        File stylesheet=new File(newServicePath, TOOLBOX_TEMPLATE_XSLT_PATH);
        File shellScriptFolder=new File(newServicePath,TOOLBOX_SCRIPT_PATH);
        shellScriptFolder.mkdirs();
        if(stylesheet.exists()){
           xslDocument=domUtil.fileToDocument(stylesheet);
           transformer = TransformerFactory.newInstance().newTransformer(new DOMSource(xslDocument));
           transformer.transform(new StreamSource(DOMUtil.getDocumentAsInputStream(describeDocument)), new StreamResult(new FileOutputStream(new File(shellScriptFolder,TOOLBOX_SCRIPT_FILE_PREFIX+processingName+"_template.sh"))));
           toolboxTemplate=IOUtil.loadString(new File(shellScriptFolder,TOOLBOX_SCRIPT_FILE_PREFIX+processingName+"_template.sh"));
        }
       return toolboxTemplate;
    }

    public String getEngineName() {
        return engineName;
    }

    public void generateEngineOutputManager(File servicePath, Document describeDocument, String processingName) throws Exception {
        
    }

    public String getScriptFolder() {
        return TOOLBOX_SCRIPT_PATH;
    }

     public String getScriptPathforProcessingName(String processingName) {
        return TOOLBOX_SCRIPT_PATH+"/"+TOOLBOX_SCRIPT_FILE_PREFIX+processingName+".tscript";
    }

     public String getOriginalScriptPathforProcessingName(String processingName) {
        return TOOLBOX_SCRIPT_PATH+"/"+TOOLBOX_SCRIPT_FILE_PREFIX+processingName+"_original.tscript";
    }

    public void updateScriptEngine(File newServicePath, String processingName, Object script) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public void deleteScriptEngine(File newServicePath, String processingName) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
