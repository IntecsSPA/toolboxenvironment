/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.common.tbx.lifecycle;

import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.util.Iterator;
import java.util.LinkedList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author massi
 */
public class LifeCycle {
     protected static final String TAG_BUILD = "build";
     protected static final String TAG_DELETE = "delete";
     protected static final String ATTRIBUTE_PATH="path";
     protected static final String ATTRIBUTE_FROM="from";
     protected static final String ATTRIBUTE_TO="to";
     protected static final String TASK_COPY="copy";
     protected static final String TASK_DELETE="delete";
     public static final String SCRIPT_BUILD="build";
     public static final String SCRIPT_DELETE="delete";
     protected String buildScript="";
     protected String deleteScript="";
     protected File repoRootDir;

     public LifeCycle()
     {

     }

     public void initFromXML(Element interfNode) {
        Element el;

        try {
            el = DOMUtil.getChildByTagName(interfNode,TAG_BUILD);
            if(el!=null)
                buildScript=el.getAttribute(ATTRIBUTE_PATH);

            el = DOMUtil.getChildByTagName(interfNode,TAG_DELETE);
            if(el!=null)
                deleteScript=el.getAttribute(ATTRIBUTE_PATH);
        } catch (Exception e) {
           e.printStackTrace();
        }

    }

    public String getBuildScriptPath()
    {
        return buildScript;
    }

    public String getBuildScript() {
        return buildScript;
    }

    public void setBuildScript(String buildScript) {
        this.buildScript = buildScript;
    }

    public String getDeleteScript() {
        return deleteScript;
    }

    public void setDeleteScript(String deleteScript) {
        this.deleteScript = deleteScript;
    }

    public File getRepoRootDir() {
        return repoRootDir;
    }

    public void setRepoRootDir(File repoRootDir) {
        this.repoRootDir = repoRootDir;
    }

    public String getDeleteScriptpath()
    {
        return deleteScript;
    }
    
    public void setRepoInterfaceRootDir(File dir)
    {
        this.repoRootDir=dir;
    }

    public void executeScript(String type,File serviceRootDir) throws Exception
    {
        if(type.equals(SCRIPT_BUILD))
        {
            executeScript(new File(repoRootDir,buildScript),serviceRootDir);
        }
        else if(type.equals(SCRIPT_DELETE))
        {
            executeScript(new File(repoRootDir,deleteScript),serviceRootDir);
        }
    }

    protected void executeScript(File scriptToExecute,File serviceRootDir) throws Exception
    {
        Document doc;
        DOMUtil util;
        Element rootEl;
        LinkedList children;
        Iterator iter;
        Element tagEl;
        String nodeName;

        util=new DOMUtil();
        doc=util.fileToDocument(scriptToExecute);
        rootEl=doc.getDocumentElement();

        children=DOMUtil.getChildren(rootEl);
        iter=children.iterator();

        while(iter.hasNext())
        {
            tagEl=(Element) iter.next();
            nodeName=tagEl.getNodeName();
            if(nodeName.equals(TASK_COPY))
            {
                CopyTask.execute(new File(repoRootDir, tagEl.getAttribute(ATTRIBUTE_FROM)),new File(serviceRootDir,tagEl.getAttribute(ATTRIBUTE_TO)));
            }
            else if(nodeName.equals(TASK_DELETE))
            {
                DeleteTask.execute(new File(serviceRootDir,tagEl.getAttribute(ATTRIBUTE_PATH)));
            }
        }
    }
    
    @Override
    public Object clone()
    {
        LifeCycle newCycle;

        newCycle=new LifeCycle();
        newCycle.setRepoInterfaceRootDir(repoRootDir);
        newCycle.setBuildScript(new String(this.buildScript));
        newCycle.setDeleteScript(new String(this.deleteScript));

        return newCycle;
    }

   
}
