package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import java.util.HashSet;
import org.apache.log4j.Logger;

public class RemoveCleanupMarkerTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element removeCleanupMarker) throws Exception {
       IVariableStore confStore;
        HashSet markers;
        Logger logger;
        
        confStore = this.engine.getConfigurationVariablesStore();
        markers = (HashSet) confStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_MARKERS);
        
        markers.remove(removeCleanupMarker.getAttribute(VALUE));
        return null;
    }

}
