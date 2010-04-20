package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.soap.toolbox.IVariableStore;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.util.DOMUtil;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;

public class CleanupProcedureTag extends NativeTagExecutor {

    @Override
    public Object executeTag(org.w3c.dom.Element cleanupProcedure) throws Exception {
        Object result = null;
        Iterator cleanupElements = DOMUtil.getChildren(cleanupProcedure).
                iterator();
        while (cleanupElements.hasNext()) {
            executeCleanup((Element) cleanupElements.next());
        }
        return null;
    }

    private void executeCleanup(Element cleanup) throws Exception {
        String marker = "";
        IVariableStore confStore;
        HashSet markers;
        Logger logger;
        
        confStore = this.engine.getConfigurationVariablesStore();
        markers = (HashSet) confStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_MARKERS);
        logger=(Logger) confStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_SERVICE_LOGGER);
        if (!cleanup.hasAttributes() ||
                markers.contains(marker = cleanup.getAttribute(MARKER))) {
            this.executeChildTag(DOMUtil.getFirstChild(cleanup));
            logger.info("Finished cleanup marked: " + marker);
        }
    }
}
