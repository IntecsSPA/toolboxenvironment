package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.toolbox.engine.ToolboxEngine;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import  it.intecs.pisa.soap.toolbox.AsynchScriptExecutor;
import it.intecs.pisa.toolbox.Toolbox;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ForkTag extends NativeTagExecutor {
    protected String tagName="fork";
    
    public ForkTag()
    {
        tagName="fork";
    }
    
    @Override
    public Object executeTag(org.w3c.dom.Element forkElement) throws Exception {
        Element child;
        Element script;
        DOMUtil domUtil = new DOMUtil();
        File resultScriptFile;
        String filePath;
        File forkedFile;
        SimpleDateFormat formatter;
        File newResultScriptFile;
        long forkedId;

        formatter = new SimpleDateFormat("yyyyMMddHHmmssSSS");
            
        child = DOMUtil.getFirstChild(forkElement);
        if (child.getNodeName().equals("file")) {
            Element childElem = DOMUtil.getFirstChild(child);
            
            filePath=(String) this.executeChildTag(childElem);
            forkedFile=new File(filePath);
            Document newScript = DOMUtil.loadXML(forkedFile,new File(Toolbox.getInstance().getToolboxScriptSchemaLocation()));

            script = newScript.getDocumentElement();        
        } else {
            script = child;
        }

        forkedId=XMLResourcesPersistence.getInstance().getNewResourceFile();
        resultScriptFile=XMLResourcesPersistence.getInstance().getXMLFile(forkedId);

        AsynchScriptExecutor asynch = new AsynchScriptExecutor((ToolboxEngine)((ToolboxEngine)engine).clone(), script, resultScriptFile);
        asynch.start();
        
        addScriptLinkToDebugTree(forkedId);
        return null;
    }
}
