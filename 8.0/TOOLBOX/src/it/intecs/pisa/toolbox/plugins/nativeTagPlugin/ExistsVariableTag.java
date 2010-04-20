package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;


import it.intecs.pisa.soap.toolbox.IEngine;
import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.toolbox.plugins.TagExecutor;

public class ExistsVariableTag extends NativeTagExecutor{
 
          
	@Override
	public Object executeTag(org.w3c.dom.Element existsVariable) throws Exception{
                 Boolean result = null;
                 IVariableStore varStore=null;
                 String variableName=null;
          
                 varStore=engine.getVariablesStore();
                 variableName=this.engine.evaluateString(existsVariable.getAttribute(NAME), IEngine.EngineStringType.ATTRIBUTE);
                 
                 result=new Boolean(varStore.getVariable(variableName)!=null);
   
        return result;
    }

   

}
