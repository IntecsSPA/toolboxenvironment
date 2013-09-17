package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.db.InstanceMarkers;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import java.util.HashSet;

public class AddCleanupMarkerTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element addCleanupMarker) throws Exception {
        IVariableStore confStore;
        HashSet markers;
        
        confStore=this.engine.getConfigurationVariablesStore();
        String instanceId=(String) confStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_ID);
        
        InstanceMarkers.store(new Long(instanceId), addCleanupMarker.getAttribute(VALUE), "");
        return null;
    }
    
 
}
