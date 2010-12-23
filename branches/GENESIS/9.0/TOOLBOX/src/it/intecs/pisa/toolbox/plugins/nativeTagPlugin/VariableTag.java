package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IEngine;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import javax.naming.Context;
import javax.naming.InitialContext;
import it.intecs.pisa.soap.toolbox.exceptions.ToolboxException;

public class VariableTag extends NativeTagExecutor{
        protected static final String NAME = "name";
        protected static final String SCOPE = "scope";
        protected static final String APPLICATION = "application";
         
	@Override
	public Object executeTag(org.w3c.dom.Element tagEl) throws Exception{
             String variableName="unknown";
            String scope="unknown";
             IVariableStore store;
            Object value;
           
            try
            {
            scope = engine.evaluateString(tagEl.getAttribute(SCOPE), IEngine.EngineStringType.ATTRIBUTE);
            variableName = engine.evaluateString(tagEl.getAttribute(NAME), IEngine.EngineStringType.ATTRIBUTE);

            if (scope != null && scope.equals(APPLICATION)) {
                //Getting JNDI context
                Context initCtx = new InitialContext();

                //Retrieving java context in order to retrieve the needed variable
                Context envCtx = (Context) initCtx.lookup("java:comp/env");

                value = envCtx.lookup(variableName);
            } else {
                store=engine.getVariablesStore();
                
                 value =store.getVariable(variableName);
            }
            }
            catch(Exception ecc)
            {
                throw new ToolboxException("Cannot find variable "+variableName+" in scope "+scope);
            }

            return value;
    }

   

}
