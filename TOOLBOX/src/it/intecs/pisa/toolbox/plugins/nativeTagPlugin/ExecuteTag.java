package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.exceptions.ReturnTagException;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExecuteTag extends NativeTagExecutor {
    protected String tagName="execute";
    
    @Override
    public Object executeTag(org.w3c.dom.Element tagEl) throws Exception {
        Iterator argumentIterator = null;
        Object result = null;
        String namespaceURI = null;
        Element procedureTag = null;
        Element procedureTagChildElement = null;
        Element procedureTagForDebug = null;
        DOMUtil domUtil = null;
        Document externalScriptDoc = null;
        Element externalFileDebugEl=null;
        String externalFilePath=null;
        File externalFile=null;
        String externalDebugFileName=null;
        domUtil = new DOMUtil();

        try {
          /*  namespaceURI = tagEl.getNamespaceURI();
            procedureTag = (Element) tagEl.getElementsByTagNameNS(namespaceURI, PROCEDURE).item(0);
            procedureTagChildElement = DOMUtil.getFirstChild(procedureTag);

            procedureTagForDebug = addDebugInfoForProcedureTag();

            externalFilePath=(String) executeChildTag(procedureTagChildElement, procedureTagForDebug);
            externalFile=new File(externalFilePath);
            externalScriptDoc = domUtil.fileToDocument(externalFilePath);

            this.offlineDbgTag.setAttribute("externalLink", externalFilePath);
            
            argumentIterator = DOMUtil.getChildrenByLocalName(tagEl, ARGUMENT).iterator();
            setArguments(argumentIterator);*/


            LinkedList children=DOMUtil.getChildren(tagEl);

            procedureTag = (Element) children.get(0);
            procedureTagChildElement = DOMUtil.getFirstChild(procedureTag);

            procedureTagForDebug = addDebugInfoForProcedureTag();
            Object procedureObj=executeChildTag(procedureTagChildElement, procedureTagForDebug);
            if(procedureObj instanceof String){
              externalFilePath=(String) procedureObj;
              externalFile=new File(externalFilePath);
              externalScriptDoc = domUtil.fileToDocument(externalFilePath);
            }else
                if(procedureObj instanceof Document){
                    externalScriptDoc=(Document) procedureObj;
                }

            if(children.size()>1){
                argumentIterator = children.iterator();
                argumentIterator.next();
                setArguments(argumentIterator);
            }

            externalFileDebugEl=getExternalFileDebugDoc();
            
            result = this.executeChildTag(externalScriptDoc.getDocumentElement(), externalFileDebugEl);            
        } 
        catch(ReturnTagException rtExc)
        {
            result=rtExc.getReturnedObject();
        }
        catch (Exception e) {
            String message;
            
            message=e.getLocalizedMessage();
            if(message==null)
                message=e.toString();
            
             offlineDbgTag.setAttribute("Exception", message);
            throw e;
        }
        finally
        {
            if(externalFileDebugEl!=null)
            {
                externalDebugFileName=externalFile.getName();
                externalDebugFileName=externalDebugFileName.substring(0,externalDebugFileName.indexOf('.'));
                externalDebugFileName+=".xml";
                dumpExternalFileExecutionTree(externalDebugFileName,externalFileDebugEl.getOwnerDocument());
                addResourceLinkToDebugTree(new File(externalDebugFileName));
            }
        }
        return result;
    }

   
  
}
