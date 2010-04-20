/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.toolbox.engine;

import it.intecs.pisa.soap.toolbox.*;
import java.util.Hashtable;

/**
 *
 * @author Massimiliano
 */
public class ToolboxEngineVariableStore implements IVariableStore{
     protected Hashtable variables = new Hashtable();
     
     private IEngine engine=null;
     
     /**
     * The Constant NULL_MARKER.
     */
    private static final Object NULL_MARKER = new Object();
    
    public Object getVariable(Object variable) {
        Object value =null;
        
        value=variables.get(variable);
       
        return value == NULL_MARKER ? null : value;
    }

    public void setVariable(Object key, Object value) {
         variables.put(key, value == null ? NULL_MARKER : value);
    }

    public void setEngine(IEngine engine) {
         this.engine=engine;
    }

    /**
     * TODO provide a better implementation for this
     * @return
     */
    public Hashtable getVariables() {
        return variables;
    }
    
    

}
