package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IToolboxEngineConstants;
import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import java.util.HashSet;

public class AddCleanupMarkerTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element addCleanupMarker) throws Exception {
        IVariableStore confStore;
        HashSet markers;
        
        confStore=this.engine.getConfigurationVariablesStore();
        markers=(HashSet) confStore.getVariable(IToolboxEngineConstants.CONFIGURATION_MARKERS);
        
        markers.add(addCleanupMarker.getAttribute(VALUE));
        return null;
    }
    
 
}