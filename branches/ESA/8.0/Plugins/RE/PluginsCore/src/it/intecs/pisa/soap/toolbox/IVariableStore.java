/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.soap.toolbox;

import java.util.Hashtable;

/**
 *
 * @author Massimiliano
 */
public interface IVariableStore {
      public Object getVariable(Object key);
      public void setVariable(Object key,Object value);
      public Hashtable getVariables();
      public void setEngine(IEngine engine);
}
