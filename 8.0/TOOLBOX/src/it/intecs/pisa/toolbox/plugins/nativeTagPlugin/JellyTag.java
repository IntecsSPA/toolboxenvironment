package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.util.DOMUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Hashtable;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.XMLOutput;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class JellyTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element jelly) throws Exception {
        DOMUtil domUtil = new DOMUtil(); 
        IVariableStore varStore;
        Hashtable variables;
        File tempDir;
        
        tempDir=(File) this.engine.getConfigurationVariablesStore().getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_TEMP_DIR);
        
        Element jellyChild = DOMUtil.getFirstChild(jelly);
        File jellyFile;
        if (jellyChild.getNamespaceURI().equals(XML_SCRIPT_NAMESPACE)) {
            jellyFile = (new File((String) this.executeChildTag(jellyChild))).getAbsoluteFile();
        } else {
            Document jellyDoc = domUtil.newDocument();
            jellyDoc.appendChild(jellyDoc.importNode(jellyChild, true));
            DOMUtil.dumpXML(jellyDoc,
                    jellyFile = File.createTempFile("tbx", ".jelly", tempDir));
            jellyFile.deleteOnExit();
        }
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        JellyContext context = new JellyContext();
        
        varStore=this.engine.getVariablesStore();
        variables=varStore.getVariables();
        
        context.setVariables(variables);
        XMLOutput xmlOutput = XMLOutput.createXMLOutput(output);
        context.runScript(jellyFile, xmlOutput);
        xmlOutput.flush();
        variables.putAll(context.getVariables());
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());
        InputStreamReader reader = new InputStreamReader(inputStream);
        Object result = null;
        String returnsAttribute;
        if ((returnsAttribute = jelly.getAttribute("returns")).equals("xml")) {
            result = domUtil.readerToDocument(reader);
        }
        if (returnsAttribute.equals("text")) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            int b;
            while ((b = reader.read()) != -1) {
                outputStream.write(b);
            }
            result = outputStream.toString();
        }
        return result;
    }
    
 
}
