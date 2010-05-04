package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.toolbox.db.InstanceMarkers;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;

public class AddCleanupMarkerTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element addCleanupMarker) throws Exception {
        IVariableStore confStore;
        
        confStore=this.engine.getConfigurationVariablesStore();
        String instanceId=(String) confStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_ID);
        
        InstanceMarkers.store(new Long(instanceId), addCleanupMarker.getAttribute(VALUE), "");
        return null;
    }
    
 
}
