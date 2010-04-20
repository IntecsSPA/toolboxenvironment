/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package it.intecs.pisa.soap.toolbox;

/**
 *
 * @author Massimiliano
 */
public interface IEngine {   
    public enum EngineStringType {ATTRIBUTE,TEXT};
    
    public IVariableStore getVariablesStore();
    public IVariableStore getConfigurationVariablesStore();
    public String evaluateString(String strToBeEvaluated, EngineStringType type);
}
