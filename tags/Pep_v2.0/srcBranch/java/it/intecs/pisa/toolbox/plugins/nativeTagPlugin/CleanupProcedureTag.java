package it.intecs.pisa.toolbox.plugins.nativeTagPlugin;

import it.intecs.pisa.pluginscore.toolbox.engine.interfaces.IVariableStore;
import it.intecs.pisa.toolbox.db.InstanceMarkers;
import it.intecs.pisa.toolbox.engine.ToolboxEngineVariablesKeys;
import it.intecs.pisa.toolbox.resources.XMLResourcesPersistence;
import it.intecs.pisa.util.DOMUtil;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
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
        String value;

        confStore = this.engine.getConfigurationVariablesStore();
        String instanceId = (String) confStore.getVariable(ToolboxEngineVariablesKeys.CONFIGURATION_INSTANCE_ID);

        marker = cleanup.getAttribute(MARKER);
        value = InstanceMarkers.getMarkerValue(new Long(instanceId), marker);

        if (value.equals("") == false) {
            InstanceMarkers.store(new Long(instanceId), marker, storeCleanup(cleanup));
        } else {
            InstanceMarkers.update(new Long(instanceId), marker, storeCleanup(cleanup));
        }

    }

    protected String storeCleanup(Element cleanup) throws Exception {
        DOMUtil util;
        Document cleanupDoc;
        Element sequenceEl;

        util = new DOMUtil();
        cleanupDoc = util.newDocument();
        sequenceEl = cleanupDoc.createElement("sequence");
        sequenceEl.setAttribute("xmlns", "http://pisa.intecs.it/mass/toolbox/xmlScript");
        cleanupDoc.appendChild(sequenceEl);

        LinkedList children = DOMUtil.getChildren(cleanup);
        for (int i = 0; i < children.size(); i++) {
            Element scriptEl;
            scriptEl = (Element) children.get(i);

            sequenceEl.appendChild(cleanupDoc.importNode(scriptEl, true));
        }

        return XMLResourcesPersistence.getInstance().storeXML(cleanupDoc);
    }
}
