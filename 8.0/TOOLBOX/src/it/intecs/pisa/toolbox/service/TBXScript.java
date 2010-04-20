/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.service;

import it.intecs.pisa.common.tbx.Interface;
import it.intecs.pisa.common.tbx.Operation;
import it.intecs.pisa.common.tbx.Script;
import it.intecs.pisa.common.tbx.Service;
import it.intecs.pisa.util.DOMUtil;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 *
 * @author massi
 */
public class TBXScript extends Script{

    public TBXScript() {

    }

    public TBXScript(Script s) {
        super(s);
    }

    protected File getServiceDir() {
        Interface interf;
        Service ser;
        File serviceDir;


        interf=parentOper.getParent();
        ser=interf.getParent();
        return ((TBXService)ser).getServiceRoot();
    }

  /*  public void setServiceDir(File serviceDir) {
        this.serviceDir = serviceDir;
    }*/

    public String getFullPath() {
        File absFile;

        absFile=new File(getServiceDir(),path);
        return absFile.getAbsolutePath();
    }

    protected void loadScript()
    {
        try {
            Document doc;
            DOMUtil util;
            util = new DOMUtil();
            doc = util.fileToDocument(new File(getServiceDir(), path));
            setScriptDoc(doc);
        } catch (IOException ex) {
            Logger.getLogger(TBXScript.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(TBXScript.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void dumpScript() {
         File fullPath;
        try
        {
            fullPath=new File(getFullPath());

            fullPath.getParentFile().mkdirs();
            DOMUtil.dumpXML(this.scriptDoc,fullPath);
        }
        catch(Exception ecc)
        {
            ecc.printStackTrace();
        }
    }

    @Override
    public Document getScriptDoc() {
        if(scriptDoc==null)
            loadScript();
        return scriptDoc;
    }

}
