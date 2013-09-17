package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.constants.EngineConstants;
import it.intecs.pisa.util.DOMUtil;
import org.w3c.dom.Document;

public class SoapRequestTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element xmlDocument) throws Exception {
        IVariableStore varStore;
        
        varStore=this.engine.getVariablesStore();
        
                
        return varStore.getVariable(EngineConstants.SOAP_REQUEST);
    }
    
 
}
