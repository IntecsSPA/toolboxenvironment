/*
 * AsynchScriptExecutor.java
 *
 * Created on 27 giugno 2007, 15.22
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package it.intecs.pisa.soap.toolbox;


import it.intecs.pisa.toolbox.engine.ToolboxEngine;
import org.w3c.dom.Element;
import java.lang.Thread;
import java.io.File;
/**
 *
 * @author Massimiliano
 */
public class AsynchScriptExecutor extends Thread  {
    
    /** Creates a new instance of AsynchScriptExecutor */
    public AsynchScriptExecutor(ToolboxEngine engine, Element doc,File resultScriptFile) {
        this.doc = doc;
        this.asynchEngine = engine;
        this.resultScriptFile = resultScriptFile;        
    }
    
    public void run() {
        try {
            this.asynchEngine.executeScript(this.doc,resultScriptFile);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
         }
    
    
    protected ToolboxEngine asynchEngine;
    
    protected Element doc;
    
    protected File resultScriptFile;
}
